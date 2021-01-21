package com.essence.business.xqh.service.fhybdd;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybddService;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRsrBsin;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRvrBsin;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.euauth.common.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
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

    @Transactional
    @Override
    public List<Map<String,Object>> callMode(String planId) {

        //写入txt文件,组装雨量 pcp文件
        ///Users/xiongchao/小清河/XQH_SWYB_MODEL/SCS/MODEL/config.txt

        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);//方案基本信息
        //雨量信息表
        List<Map<String,Object>> results = (List<Map<String, Object>>) CacheUtil.get("rainfall",planId);

        //河流信息
        List<WrpRvrBsin> allParentIdIsNull = wrpRvrBsinDao.findAllParentIdIsNull();

        String modelid = planInfo.getnModelid();// 1是SCS  2是单位线

        String riverId = "RVR_011"; //小清河

        //创建入参、出参
        String SWYB_PATH="";
        if("1".equals(modelid)){
            SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_SCS_Path");
        }else if ("2".equals(modelid)){
            SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_DWX_Path");
        }
        String MODEL_TEMPLATE_INPUT = SWYB_PATH  + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE")
                + File.separator + "INPUT"+File.separator +planId; //输入的地址
        String MODEL_TEMPLATE_OUTPUT = SWYB_PATH  + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                +File.separator +planId;//输入的地址
        //入参
        String MODEL_TEMPLATE = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");
        //模型运行的config
        String MODEL_RUN = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");
        File inputPath = new File(MODEL_TEMPLATE_INPUT);
        File outPath = new File(MODEL_TEMPLATE_OUTPUT);
        inputPath.mkdir();
        outPath.mkdir();
        //写入到csv入参里 雨量
        writeDataToInputPcpCsv(MODEL_TEMPLATE_INPUT,results);
        //写入河流的数据
        writeDataToInputModelSelectCsv(MODEL_TEMPLATE_INPUT,allParentIdIsNull,modelid);
        //修改config文件里
        writeDataToConfig(MODEL_RUN,MODEL_TEMPLATE_INPUT,MODEL_TEMPLATE_OUTPUT);
        //编写模型的配置文件

        //调用模型
        runModelExe(MODEL_RUN + File.separator + "startUp.bat");

        //获取输入结果,57个断面也包含水库
        Map<String,List<String>> model_result = getModelResult(MODEL_TEMPLATE_OUTPUT);


        //List<WrpRcsBsin> wrpRcsBsins = wrpRcsBsinDao.findAll();
        //List<String> collect = wrpRcsBsins.stream().map(WrpRcsBsin::getRvcrcrsccd).collect(Collectors.toList());
        //水库编码
        List<String> collect = wrpRsrBsinDao.findAll().stream().map(WrpRsrBsin::getRscd).collect(Collectors.toList());

        Map<String,List<String>> model_shuiku = new HashMap<>();
        for(Map.Entry<String,List<String>> entry : model_result.entrySet()){
            String key = entry.getKey();
            if(collect.contains(key)){
                model_shuiku.put(key,entry.getValue());
            }
        }

        return null;
    }

    private void writeDataToInputModelSelectCsv(String model_template_input, List<WrpRvrBsin> allParentIdIsNull,String modelId) {
        String modelSelectInputUrl = model_template_input+File.separator+"model_selection.csv";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(modelSelectInputUrl, true)); // 附加
            // 添加新的数据行
            String riverId="";
            String modelSelect="";
           for(WrpRvrBsin wrpRvrBsin : allParentIdIsNull){
               riverId = riverId + wrpRvrBsin.getRvcd()+",";
               modelSelect = modelSelect + modelId + ",";
           }
            riverId = riverId.substring(0,riverId.length()-1);
            modelSelect = modelSelect.substring(0,modelSelect.length()-1);
            bw.write(riverId);
            bw.newLine();
            bw.write(modelSelect);
            bw.newLine();
            bw.close();
            System.out.println("河系写入成功");

        }catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            e.printStackTrace();
        }

    }


    private static Map<String,List<String>> getModelResult(String model_template_output) {

        Map<String,List<String>> resultMap = new HashMap<>();
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

            for(String s :datas){
                List<String> split = Arrays.asList(s.split("\t"));
                resultMap.put(split.get(0),new ArrayList<>(split.subList(1,split.size())));
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
        System.out.println("模型调用成功！");
    }


    private static  void writeDataToConfig(String model_run, String model_template_input, String model_template_output) {
            String configUrl = model_run+File.separator + "config.txt";
            String inputPcPUrl = "pcp&&"+model_template_input + File.separator+ "pcp.csv";
            String inputModelSelectUrl = "model_selection&&"+model_template_input + File.separator+ "model_selection.csv";
            String outputUrl = "result&&"+model_template_output + File.separator + "result.txt";

            List<String> result = new ArrayList<String>();
            try{
                BufferedReader br = new BufferedReader(new FileReader(configUrl));//构造一个BufferedReader类来读取文件
                String s = null;
                while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                    result.add(s);
                }
                br.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            result.set(0,inputPcPUrl);
            result.set(1,inputModelSelectUrl);
            result.set(2,outputUrl);
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
                // 添加新的数据行
                for(String s : result){
                    bw.write(s);
                    bw.newLine();
                }
                bw.close();
                System.out.println("");
            }catch (FileNotFoundException e) {
                // File对象的创建过程中的异常捕获
                e.printStackTrace();
            } catch (IOException e) {
                // BufferedWriter在关闭对象捕捉异常
                e.printStackTrace();
            }

    }



    private static  void writeDataToInputPcpCsv( String MODEL_TEMPLATE_INPUT, List<Map<String, Object>> results){

        //String railFallUrl = MODEL_TEMPLATE+File.separator+"pcp.csv";
        String railFallInputUrl = MODEL_TEMPLATE_INPUT+File.separator+"pcp.csv";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(railFallInputUrl, true)); // 附加
            // 添加新的数据行
            bw.write("\"time\""+","+"\"pcp\"");
            bw.newLine();
            for(int i=0;i<results.size();i++) {
                Map map = results.get(i);
               bw.write(map.get("i") + "," + map.get("sum"));
               bw.newLine();
            }
            bw.close();
            System.out.println("雨量输入文件写入成功");

        }catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            e.printStackTrace();
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            e.printStackTrace();
        }
    }


    @Override
    public String savePlanWithCache(ModelCallBySWDDVo vo) {
        Date startTime = vo.getStartTime(); //开始时间
        Date endTIme = vo.getEndTime();  //结束时间
        Date periodEndTime = vo.getPeriodEndTime();//预见结束时间
        if(periodEndTime != null){
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
        ywkPlaninfo.setnOutputtm(step*60L);//设置间隔分钟
        ywkPlaninfo.setnModelid(vo.getModelId());
        ywkPlaninfo.setdRainstarttime(startTime);
        ywkPlaninfo.setdRainendtime(endTIme);
        //tm,sum
        Boolean flag = CacheUtil.saveOrUpdate("planInfo",ywkPlaninfo.getnPlanid(),ywkPlaninfo);
        if(flag){
            return ywkPlaninfo.getnPlanid();
        }else {
            return  null;
        }
    }




    @Override
    public List<Map<String, Object>> getRainfalls(String planId) {

        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        String startTimeStr = format1.format(startTime);
        String endTimeStr = format1.format(endTime);
        Long step = planInfo.getnOutputtm()/60;//步长
        List<Map<String, Object>> stPptnRByStartTimeAndEndTime = stPptnRDao.findStPptnRByStartTimeAndEndTime(startTimeStr, endTimeStr);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH");
        Map<String, BigDecimal> stPptnRMap = new HashMap<>();
        Map<String,List<Map<String,Object>>> handleMap = new HashMap<>();
        for (Map<String,Object> map : stPptnRByStartTimeAndEndTime){
            String tm = map.get("tm")+"";
            List<Map<String, Object>> handles = handleMap.get(tm);
            if(CollectionUtils.isEmpty(handles)){
                handles = new ArrayList<>();
            }
            handles.add(map);
            handleMap.put(tm,handles);
        }
        Set<Map.Entry<String, List<Map<String, Object>>>> entries = handleMap.entrySet();
        for (Map.Entry<String, List<Map<String, Object>>> entry:entries){
            String key = entry.getKey();
            List<Map<String, Object>> value = entry.getValue();
            BigDecimal sum = new BigDecimal("0");
            for (Map m :value){
                sum = sum.add(new BigDecimal(m.get("sum")+""));
            }
            sum = sum.divide(sum,value.size(),BigDecimal.ROUND_HALF_UP).setScale(2,BigDecimal.ROUND_HALF_UP);
            stPptnRMap.put(key,sum);
        }
        int i = 0;
        List<Map<String,Object>> results = new ArrayList<>();
        while (startTime.before(endTime)){
            Map<String,Object> result = new HashMap<>();
            String hourStart = format.format(startTime);
            BigDecimal bigDecimal = stPptnRMap.get(hourStart);
            startTime = DateUtil.getNextHour(startTime,step.intValue());
            result.put("i",i);
            result.put("sum",bigDecimal);
            i++;
            results.add(result);
        }
        CacheUtil.saveOrUpdate("rainfall",planId,results);
        return results;
    }

}
