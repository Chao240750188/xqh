package com.essence.business.xqh.service.fhybdd;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.dto.ModelProperties;
import com.essence.business.xqh.api.fhybdd.dto.SkProperties;
import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybddService;
import com.essence.business.xqh.api.task.fhybdd.ReservoirModelCallTask;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.FileUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.entity.fhybdd.*;
import com.essence.euauth.common.util.UuidUtil;
import com.essence.framework.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ModelCallFhybddServiceImpl implements ModelCallFhybddService {

    @Autowired
    StPptnRDao stPptnRDao;
    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao; //方案基本信息
    @Autowired
    YwkPlaninRainfallDao ywkPlaninRainfallDao;//输入
    @Autowired
    YwkPlanOutputQDao ywkPlanOutputQDao;//输出
    @Autowired
    WrpRcsBsinDao wrpRcsBsinDao; //断面的

    @Autowired
    WrpRvrBsinDao wrpRvrBsinDao;// 河流

    @Autowired
    WrpRsrBsinDao wrpRsrBsinDao; //水库的

    @Autowired
    SkProperties skProperties;

    @Autowired
    ModelProperties modelProperties;

    @Autowired
    YwkModelDao ywkModelDao;//水文模型

    @Autowired
    ReservoirModelCallTask reservoirModelCallTask;

    @Transactional
    @Override
    public Map<String,List<String>>callMode(String planId) {

        //写入txt文件,组装雨量 pcp文件
        ///Users/xiongchao/小清河/XQH_SWYB_MODEL/SCS/MODEL/config.txt

        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);//方案基本信息

        if(planInfo == null){
            System.out.println("计划信息为空，无法计算");
            return new HashMap<>();
        }
        //雨量信息表
        List<Map<String, Object>> results = (List<Map<String, Object>>) CacheUtil.get("rainfall", planId);
        if (CollectionUtils.isEmpty(results)){
            System.out.println("雨量信息为空，无法计算");
            return new HashMap<>();
        }

        //河流信息
        List<WrpRvrBsin> allParentIdIsNull = wrpRvrBsinDao.findAllParentIdIsNull();//模型入参使用
        String modelid = planInfo.getnModelid();// 1是SCS  2是单位线
        String modelPyId = modelProperties.getModelMap().get(modelid);

        String riverId = planInfo.getRiverId(); //小清河
        //创建入参、出参
        String SWYB_PATH = "";
        if ("1".equals(modelPyId)) {
            SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_SCS_Path");
        } else if ("2".equals(modelPyId)) {
            SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_DWX_Path");
        }
        String SWYB_MODEL_TEMPLATE = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");

        String SWYB_MODEL_TEMPLATE_INPUT = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE")
                + File.separator + "INPUT" + File.separator + planId; //输入的地址
        String SWYB_MODEL_TEMPLATE_OUTPUT = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId;//输出的地址
        //模型运行的config
        String MODEL_RUN = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");
        File inputPath = new File(SWYB_MODEL_TEMPLATE_INPUT);
        File outPath = new File(SWYB_MODEL_TEMPLATE_OUTPUT);
        inputPath.mkdir();
        outPath.mkdir();
        //写入到csv入参里 雨量
       int result0 = writeDataToInputPcpCsv(SWYB_MODEL_TEMPLATE_INPUT, results);
       if (result0 == 0 ){
           System.out.println("水文模型写入雨量pcp.Csv失败");
           return new HashMap<>();
       }
        //写入河流的数据
       int result1 = writeDataToInputModelSelectCsv(SWYB_MODEL_TEMPLATE_INPUT, allParentIdIsNull, modelPyId);
       if (result1 == 0){
           System.out.println("水文模型写入河流的数据model_select失败");
           return new HashMap<>();
       }
        //修改config文件里
        int result2 = writeDataToConfig(MODEL_RUN, SWYB_MODEL_TEMPLATE_INPUT, SWYB_MODEL_TEMPLATE_OUTPUT);
        if (result2 == 0){
            System.out.println("水文模型修改config文件失败");
            return new HashMap<>();
        }
        //编写模型的配置文件
        //调用模型
        runModelExe(MODEL_RUN + File.separator + "startUp.bat");
        //获取输入结果,57个断面也包含水库
        Map<String, List<String>> model_result = getModelResult(SWYB_MODEL_TEMPLATE_OUTPUT);
        if (model_result.size()<=0){
            System.out.println("水文模型调用结果result文件失败");
            return new HashMap<>();
        }
        //水库编码
        List<String> collect = wrpRsrBsinDao.findAll().stream().map(WrpRsrBsin::getRvcrcrsccd).collect(Collectors.toList());

        Map<String, List<String>> model_shuiku = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : model_result.entrySet()) {
            String key = entry.getKey();
            if (collect.contains(key)) {
                model_shuiku.put(key, entry.getValue());//9个水库。
            }
        }

        Long step = planInfo.getnOutputtm() / 60;//步长
        //水库调度的外层文件夹
        String SKDD_PATH=PropertiesUtil.read("/filePath.properties").getProperty("SKDD_BASE_PATH");

        Map<String, String> sk_id_name = skProperties.getID_NAME();

        List<CompletableFuture<Integer>> skddFuture = new ArrayList<>();

        //水文调度模型的出参为水库调度的入参数
        for (Map.Entry<String,String> entry: sk_id_name.entrySet()){
            String key = entry.getKey();
            List<String> rkll = model_shuiku.get(key);//入库流量
            System.out.println("key:"+key+",rkll:"+rkll);
            String name = entry.getValue();
            String SKDD_MODEL_TEMPLATE_INPUT = SKDD_PATH+File.separator+name+
                    File.separator+PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE")
            +File.separator+"INPUT"+File.separator+planId;
            String SKDD_MODEL_TEMPLATE = SKDD_PATH+File.separator+name+
                    File.separator+PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");

            String SKDD_MODEL_TEMPLATE_OUTPUT = SKDD_PATH+File.separator+name+File.separator+
                    PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")+File.separator+planId;

            String SKDD_RUN = SKDD_PATH + File.separator + name + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");

            String SWYB_SK_TEMPLATE = SWYB_MODEL_TEMPLATE+File.separator+name.toLowerCase()+"shuiku.txt";//水文预报的入参数 不是input 小写
            File file1= new File(SKDD_MODEL_TEMPLATE_INPUT);
            file1.mkdir();
            File file2= new File(SKDD_MODEL_TEMPLATE_OUTPUT);
            file2.mkdir();
            skddFuture.add(reservoirModelCallTask.reservoirModelCall(SKDD_RUN,SKDD_MODEL_TEMPLATE,SKDD_MODEL_TEMPLATE_INPUT,SKDD_MODEL_TEMPLATE_OUTPUT,step,rkll,SWYB_SK_TEMPLATE));
        }

        CompletableFuture []result = new CompletableFuture[skddFuture.size()];
        for (int i=0;i<skddFuture.size();i++){
            result[i] = skddFuture.get(i);
        }

        CompletableFuture.allOf(result).join();//全部执行完后 然后整水文模型
        System.out.println("开始二次调用水文模型");
        runModelExe(MODEL_RUN + File.separator + "startUp.bat");
        System.out.println("二次调用水文模型成功");

        Map<String, List<String>> finalResult = getModelResult(SWYB_MODEL_TEMPLATE_OUTPUT);
        //找到河系关联的断面
        List<WrpRcsBsin> listByRiverId = wrpRcsBsinDao.findListByRiverId(riverId);
        List<String> sections = listByRiverId.stream().map(WrpRcsBsin::getRvcrcrsccd).collect(Collectors.toList());

        Map<String,List<String>> resultMap = new HashMap<>();
        for(String sectionId : sections){
            resultMap.put(sectionId,finalResult.get(sectionId));
        }

        return resultMap;
    }





    private int writeDataToInputModelSelectCsv(String model_template_input, List<WrpRvrBsin> allParentIdIsNull, String modelId) {
        String modelSelectInputUrl = model_template_input + File.separator + "model_selection.csv";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(modelSelectInputUrl, false)); // 附加
            // 添加新的数据行
            String riverId = "";
            String modelSelect = "";
            for (WrpRvrBsin wrpRvrBsin : allParentIdIsNull) {
                riverId = riverId + wrpRvrBsin.getRvcd() + ",";
                modelSelect = modelSelect + modelId + ",";
            }
            riverId = riverId.substring(0, riverId.length() - 1);
            modelSelect = modelSelect.substring(0, modelSelect.length() - 1);
            bw.write(riverId);
            bw.newLine();
            bw.write(modelSelect);
            bw.newLine();
            bw.close();
            System.out.println("水文模型河系写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            e.printStackTrace();
            System.out.println("水文模型河系写入失败");
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            e.printStackTrace();
            System.out.println("水文模型河系写入失败");
            return 0;
        }

    }

    /**
     * 解析模型输出结果文件成出库流量数据
     * @param model_template_output
     * @return
     */
    private  Map<String, List<String>> getModelResult(String model_template_output) {
        Map<String, List<String>> resultMap = new HashMap<>();
        List<String> datas = new ArrayList<>();

        /* 读取数据 */
        BufferedReader br = null;
        try {
            String resultFilePath = model_template_output + "/result.txt";
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(resultFilePath)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                datas.add(lineTxt);
            }

            for (String s : datas) {
                List<String> split = Arrays.asList(s.split("\t"));
                resultMap.put(split.get(0), new ArrayList<>(split.subList(1, split.size())));
            }
        } catch (Exception e) {
            System.err.println("水文模型调用结果读取失败:read errors :" + e);
            return new HashMap<>();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultMap;
    }




    /**
     * 调用模型运行模型文件
     */
    private void runModelExe(String modelRunPath) {
        BufferedReader br = null;
        BufferedReader brError = null;
        try {
            // 执行exe cmd可以为字符串(exe存放路径)也可为数组，调用exe时需要传入参数时，可以传数组调用(参数有顺序要求)
            Process p = Runtime.getRuntime().exec(modelRunPath);
            String line = null;
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            brError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            // while ((line = br.readLine()) != null || (line = brError.readLine()) != null)
            // {
            while ((line = brError.readLine()) != null) {
                // 输出exe输出的信息以及错误信息
                System.out.println(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("水文模型调用成功！");
    }


    private  int writeDataToConfig(String model_run, String model_template_input, String model_template_output) {
        String configUrl = model_run + File.separator + "config.txt";
        String inputPcPUrl = "pcp&&" + model_template_input + File.separator + "pcp.csv";
        String inputModelSelectUrl = "model_selection&&" + model_template_input + File.separator + "model_selection.csv";
        String outputUrl = "result&&" + model_template_output + File.separator + "result.txt";

        List<String> result = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(configUrl));//构造一个BufferedReader类来读取文件
            String s = null;
            while ((s = br.readLine()) != null) {//使用readLine方法，一次读一行
                result.add(s);
            }
            br.close();
        } catch (Exception e) {
            System.out.println("读取水文模型config失败");
            e.printStackTrace();
            return 0;
        }
        result.set(0, inputPcPUrl);
        result.set(1, inputModelSelectUrl);
        result.set(2, outputUrl);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 添加新的数据行
            for (int i=0;i<result.size();i++){
                String s = result.get(i);
                if (i==result.size()-1){
                    bw.write(s);
                }else {
                    bw.write(s);
                    bw.newLine();
                }
            }
            bw.close();
            System.out.println("写入水文模型config成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("写入水文模型config失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("写入水文模型config失败");
            e.printStackTrace();
            return 0;
        }

    }


    private  int writeDataToInputPcpCsv(String MODEL_TEMPLATE_INPUT, List<Map<String, Object>> results) {

        //String railFallUrl = MODEL_TEMPLATE+File.separator+"pcp.csv";
        String railFallInputUrl = MODEL_TEMPLATE_INPUT + File.separator + "pcp.csv";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(railFallInputUrl, false)); // 附加
            // 添加新的数据行
            bw.write("\"time\"" + "," + "\"pcp\"");
            bw.newLine();
            for (int i = 0; i < results.size(); i++) {
                Map map = results.get(i);
                bw.write(map.get("i") + "," + map.get("sum"));
                bw.newLine();
            }
            bw.close();
            System.out.println("水文模型雨量输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水文模型雨量输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("水文模型雨量输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }


    @Override
    public String savePlanWithCache(ModelCallBySWDDVo vo) {
        Map<String, String> id_name = skProperties.getID_NAME();
        Date startTime = vo.getStartTime(); //开始时间
        Date endTIme = vo.getEndTime();  //结束时间
        Date periodEndTime = vo.getPeriodEndTime();//预见结束时间
        if (periodEndTime != null) {
            endTIme = periodEndTime;
        }
        int step = vo.getStep();//以小时为单位

        //方案基本信息入库
        YwkPlaninfo ywkPlaninfo = new YwkPlaninfo();
        ywkPlaninfo.setnPlanid(UuidUtil.get32UUIDStr());
        ywkPlaninfo.setcPlanname("模型方案名");
        ywkPlaninfo.setnCreateuser("user");
        ywkPlaninfo.setnPlancurrenttime(new Date());
        ywkPlaninfo.setdCaculatestarttm(startTime);//方案计算开始时间
        ywkPlaninfo.setdCaculateendtm(endTIme);//方案计算结束时间
        ywkPlaninfo.setnPlanstatus(0l);//方案状态
        ywkPlaninfo.setnOutputtm(step * 60L);//设置间隔分钟
        ywkPlaninfo.setnModelid(vo.getModelId());
        ywkPlaninfo.setdRainstarttime(startTime);
        ywkPlaninfo.setdRainendtime(endTIme);
        ywkPlaninfo.setRiverId(vo.getRiverId());
        //tm,sum
        Boolean flag = CacheUtil.saveOrUpdate("planInfo", ywkPlaninfo.getnPlanid(), ywkPlaninfo);
        if (flag) {
            return ywkPlaninfo.getnPlanid();
        } else {
            return null;
        }
    }


    /**
     * 模型计算结果解析入库
     *
     * @param planId
     * @return
     */
    @Override
    public List<Map<String, Object>> getRainfalls(String planId) {
        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        String startTimeStr = format1.format(startTime);
        String endTimeStr = format1.format(endTime);
        Long step = planInfo.getnOutputtm() / 60;//步长
        List<Map<String, Object>> stPptnRByStartTimeAndEndTime = stPptnRDao.findStPptnRByStartTimeAndEndTime(startTimeStr, endTimeStr);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        Map<String, BigDecimal> stPptnRMap = new HashMap<>();
        Map<String, List<Map<String, Object>>> handleMap = new HashMap<>();
        for (Map<String, Object> map : stPptnRByStartTimeAndEndTime) {
            String tm = map.get("tm") + "";
            List<Map<String, Object>> handles = handleMap.get(tm);
            if (CollectionUtils.isEmpty(handles)) {
                handles = new ArrayList<>();
            }
            handles.add(map);
            handleMap.put(tm, handles);
        }
        Set<Map.Entry<String, List<Map<String, Object>>>> entries = handleMap.entrySet();
        for (Map.Entry<String, List<Map<String, Object>>> entry : entries) {
            String key = entry.getKey();
            List<Map<String, Object>> value = entry.getValue();
            BigDecimal sum = new BigDecimal("0");
            for (Map m : value) {
                sum = sum.add(new BigDecimal(m.get("sum") + ""));
            }
            sum = sum.divide(new BigDecimal(value.size()), 2, BigDecimal.ROUND_HALF_UP);
            stPptnRMap.put(key, sum);
        }
        int i = 0;
        List<Map<String, Object>> results = new ArrayList<>();
        while (startTime.before(DateUtil.getNextMillis(endTime,1))) {
            Map<String, Object> result = new HashMap<>();
            String hourStart = format.format(startTime);
            BigDecimal bigDecimal = stPptnRMap.get(hourStart);
            startTime = DateUtil.getNextHour(startTime, step.intValue());
            result.put("i", i);
            result.put("sum", bigDecimal);
            i++;
            results.add(result);
        }
        CacheUtil.saveOrUpdate("rainfall", planId, results);
        return results;
    }

    /**
     * 解析模型输出结果存入数据库
     *
     * @param planId
     * @return
     */
    @Override
    public int saveModwlResultToDataBase(String planId) {
        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);
        Long step = planInfo.getnOutputtm() / 60;//步长(小时)
        String modelId = planInfo.getnModelid();
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        //创建入参、出参
        String SWYB_PATH = "";
        if ("1".equals(modelId)) {
            SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_SCS_Path");
        } else if ("2".equals(modelId)) {
            SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_DWX_Path");
        }
        String MODEL_TEMPLATE_OUTPUT = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId;//输入的地址
        //获取输入结果,57个断面也包含水库
        Map<String, List<String>> model_result = getModelResult(MODEL_TEMPLATE_OUTPUT);
        //水库编码
        List<String> collect = wrpRsrBsinDao.findAll().stream().map(WrpRsrBsin::getRscd).collect(Collectors.toList());
        //封装数据入库
        List<YwkPlanOutputQ> ywkPlanOutputQList = new ArrayList<>();

        for (Map.Entry<String, List<String>> entry : model_result.entrySet()) {
            //断面编码
            String RCS_ID = entry.getKey();
            if (!collect.contains(RCS_ID))
                continue;
            List<String> outPutQList = entry.getValue();
            //封装数据
            int count = 0;
            int index = 0;
            for (Date time = startTime; time.before(endTime); time = DateUtil.getNextHour(startTime, count)) {
                double q = 0.0;
                try {
                    q = Double.parseDouble(outPutQList.get(index));
                } catch (Exception e) {
                }
                ywkPlanOutputQList.add(new YwkPlanOutputQ(StrUtil.getUUID(), planId, time, q, RCS_ID));
                count += step;
                index++;
            }
            if (ywkPlanOutputQList.size() > 0) {
                List<YwkPlanOutputQ> list = ywkPlanOutputQDao.saveAll(ywkPlanOutputQList);
                return list.size();
            }
        }
        return 0;
    }

    /**
     * 根据水文模型输出文件配置水库调度模型参数条件
     *
     * @param planId
     */
    @Override
    public void makeSwModelToSkdd(String planId) {
//        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);
//        Long step = planInfo.getnOutputtm() / 60;//步长(小时)
//        String modelId = planInfo.getnModelid();
//        Date startTime = planInfo.getdCaculatestarttm();
//        Date endTime = planInfo.getdCaculateendtm();
//
//        //创建入参、出参
//        String SWYB_PATH = "";
//        if ("1".equals(modelId)) {
//            SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_SCS_Path");
//        } else if ("2".equals(modelId)) {
//            SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_DWX_Path");
//        }
//        String MODEL_TEMPLATE_OUTPUT = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
//                + File.separator + planId;//输出地址
//        String MODEL_TEMPLATE = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("TEMPLATE")
//                + File.separator +"INPUT"+File.separator+ planId;//输入模板文件
//        //获取水文模型输出结果57个断面流量数据
//        Map<String, String> rcsqMap = getSwModelResultToSkdd(MODEL_TEMPLATE_OUTPUT);
//        //水库编码
//        List<String> rsrList = wrpRsrBsinDao.findAll().stream().map(WrpRsrBsin::getRscd).collect(Collectors.toList());
//        //处理结果文件
//        for (String rsrId: rsrList) {
//            //大站水库
//             if(rsrId.equals("RCS_018")){
//                 //写水文模型template
//                 String RCS_INPUT_MODEL = rcsqMap.get(rsrId);
//             }
//        }

    }

    /**
     * 解析模型输出结果文件给水库调度模型 （不解析成出库流量数据）
     * @param model_template_output
     * @return
     */
    private static Map<String, String> getSwModelResultToSkdd(String model_template_output) {
        Map<String, String> resultMap = new HashMap<>();
        List<String> datas = new ArrayList<>();

        /* 读取数据 */
        BufferedReader br = null;
        try {
            String resultFilePath = model_template_output + "/result.txt";
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(resultFilePath)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                datas.add(lineTxt);
            }
            for (String s : datas) {
                List<String> split = Arrays.asList(s.split("\t"));
                resultMap.put(split.get(0), s);
            }
        } catch (Exception e) {
            System.err.println("read errors :" + e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultMap;
    }


    /**
     * 获取河流列表信息
     * @return
     */
    @Override
    public List<WrpRvrBsin> getRiverInfos() {
       return wrpRvrBsinDao.findAll();
    }

    /**
     * 获取水文模型的模型列表
     * @return
     */
    @Override
    public List<YwkModel> getModelInfos() {

        List<YwkModel> swyb = ywkModelDao.getYwkModelByModelType("SWYB");
        return swyb;
    }
}
