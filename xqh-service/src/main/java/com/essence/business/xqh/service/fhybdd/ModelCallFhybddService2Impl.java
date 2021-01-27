package com.essence.business.xqh.service.fhybdd;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.fhybdd.dto.ModelProperties;
import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybdd2Service;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRcsBsin;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRvrBsin;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninRainfall;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.util.FileUtil;
import com.essence.framework.util.StrUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModelCallFhybddService2Impl implements ModelCallFhybdd2Service {



    @Autowired
    WrpRvrBsinDao wrpRvrBsinDao;// 河流


    @Autowired
    ModelProperties modelProperties;

    @Autowired
    YwkModelDao ywkModelDao;//水文模型

    @Autowired
    WrpRcsBsinDao wrpRcsBsinDao;

    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao;
    @Autowired
    StPptnRDao stPptnRDao;

    @Autowired
    YwkPlaninRainfallDao ywkPlaninRainfallDao;

    @Value("#{'${SWYB_SANRUHAI}'.split(',')}")
    List<String> SANRUHAI;
    @Value("#{'${SWYB_XIAOQINGHE}'.split(',')}")
    List<String> XIAOQINGHE;
    @Value("#{'${SWYB_ZHIMAIHE}'.split(',')}")
    List<String> ZHIMAIHE;

    @Override
    public Long callMode(String planId) {

        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);//方案基本信息

        if(planInfo == null){
            System.out.println("计划信息为空，无法计算");
            return -1L;
        }

        //雨量信息表
        List<Map<String, Object>> results = (List<Map<String, Object>>) CacheUtil.get("rainfall", planId);
        if (CollectionUtils.isEmpty(results)){
            System.out.println("雨量信息为空，无法计算");
            return -1L;
        }
        //河流信息
        List<WrpRvrBsin> allParentIdIsNull = wrpRvrBsinDao.findAllParentIdIsNull();//模型入参使用
        String modelid = planInfo.getnModelid();// 1是SCS  2是单位线
        String modelPyId = modelProperties.getModel().get(modelid);

        String riverId = planInfo.getRiverId(); //小清河

        String riverModelPath = "";

        if (SANRUHAI.contains(riverId)){
            riverModelPath = "SANRUHAI";
        }else if(XIAOQINGHE.contains(riverId)){
            riverModelPath = "XIAOQINGHE";
        }else if (ZHIMAIHE.contains(riverId)){
            riverModelPath = "ZHIMAIHE";
        }else {
            System.out.println("河流信息错误 无法计算，无法计算");
            return -1L;
        }
        //创建入参、出参
        String SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_PATH");

        String river_model_base_path = SWYB_PATH + File.separator+riverModelPath;

        String SWYB_MODEL_TEMPLATE_INPUT = river_model_base_path + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE")
                + File.separator + "INPUT" + File.separator + planId; //输入的地址
        String SWYB_MODEL_TEMPLATE_OUTPUT = river_model_base_path + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId;//输出的地址
        //模型运行的config
        String MODEL_RUN = river_model_base_path + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");
        File inputPath = new File(SWYB_MODEL_TEMPLATE_INPUT);
        File outPath = new File(SWYB_MODEL_TEMPLATE_OUTPUT);
        inputPath.mkdir();
        outPath.mkdir();
        //写入到csv入参里 雨量
       int result0 = writeDataToInputPcpCsv(SWYB_MODEL_TEMPLATE_INPUT, results);
       if (result0 == 0 ){
           System.out.println("水文模型写入雨量pcp.Csv失败");
           return -1L;
       }
        //写入河流的数据
       int result1 = writeDataToInputModelSelectCsv(SWYB_MODEL_TEMPLATE_INPUT, allParentIdIsNull, modelPyId,riverId);
       if (result1 == 0){
           System.out.println("水文模型写入河流的数据model_select失败");
           return -1L;
       }
        //修改config文件里
        int result2 = writeDataToConfig(MODEL_RUN, SWYB_MODEL_TEMPLATE_INPUT, SWYB_MODEL_TEMPLATE_OUTPUT);
        if (result2 == 0){
            System.out.println("水文模型修改config文件失败");
            return -1L;
        }
        //编写模型的配置文件
        //调用模型
        System.out.println("开始调用水文模型。。。。。");
        runModelExe(MODEL_RUN + File.separator + "startUp.bat");
        System.out.println("调用水文模型结束");
        //获取输入结果,57个断面也包含水库
        Map<String, List<String>> model_result = getModelResult(SWYB_MODEL_TEMPLATE_OUTPUT+"/result.txt");
        if (model_result.size()<=0){
            System.out.println("水文模型调用结果result文件失败");
            return -1L;
        }
        //planInfo.setnPlanstatus(2L); //计划执行成功。

        return 2L;
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
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(model_template_output)), "UTF-8"));
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


    //修改水文模型配置文件
    //TODO 同时计算并发问题，共同读一个config文件
    private  int writeDataToConfig(String model_run, String model_template_input, String model_template_output) {
        String configUrl = model_run + File.separator + "config.txt";
        String inputPcPUrl = "pcp&&" + model_template_input + File.separator + "pcp.csv";
        String inputModelSelectUrl = "model_selection&&" + model_template_input + File.separator + "model_selection.csv";
        String outputUrl = "result&&" + model_template_output + File.separator + "result.txt";
        String errorUrl = "error&&"+model_template_output+File.separator+"error_log.txt";
        String shuikuUrl = "shuiku_result&&"+model_template_output+File.separator+"shuiku_result.txt";
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

        try {
            List<String> finals = new ArrayList<>();
            for(String url : result){
                String[] split = url.split("&&");
                if ("pcp".equals(split[0])){
                    finals.add(inputPcPUrl);
                }else if ("model_selection".equals(split[0])){
                    finals.add(inputModelSelectUrl);
                }else if("result".equals(split[0])){
                    finals.add(outputUrl);
                }else if ("shuiku_result".equals(split[0])){
                    finals.add(shuikuUrl);
                }else if("error".equals(split[0])){
                    finals.add(errorUrl);
                }else{
                    finals.add(url);
                }
            }
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 添加新的数据行
            for (int i=0;i<finals.size();i++){
                String s = finals.get(i);
                if (i==finals.size()-1){
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


    //写入河流数据到model_select里面
    private int writeDataToInputModelSelectCsv(String model_template_input, List<WrpRvrBsin> allParentIdIsNull, String modelId,String riverIds) {
        String modelPyId = modelProperties.getModel().get("MODEL_SCS");
        String modelSelectInputUrl = model_template_input + File.separator + "model_selection.csv";
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(modelSelectInputUrl, false)); // 附加
            // 添加新的数据行
            String riverId = "";
            String modelSelect = "";
            for (WrpRvrBsin wrpRvrBsin : allParentIdIsNull) {
                riverId = riverId + wrpRvrBsin.getRvcd() + ",";
                if(wrpRvrBsin.getRvcd().equals(riverIds)){
                    modelSelect = modelSelect + modelId + ",";
                }else{//默认SCS模型
                    modelSelect = modelSelect + modelPyId + ",";
                }

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

    //写入雨量到输入文件
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



    @Override
    public String getModelRunStatus(String planId) {
        //创建方案时设置水文模型运行次数为2
        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);
        Long aLong = planInfo.getnPlanstatus();

        if(aLong.intValue()!=2)
            return "0";
        //创建入参、出参
        String riverId = planInfo.getRiverId();
        String riverModelPath = "";

        if (SANRUHAI.contains(riverId)){
            riverModelPath = "SANRUHAI";
        }else if(XIAOQINGHE.contains(riverId)){
            riverModelPath = "XIAOQINGHE";
        }else if (ZHIMAIHE.contains(riverId)){
            riverModelPath = "ZHIMAIHE";
        }else {
            System.out.println("河流信息错误 无法计算，无法计算");
        }
        //创建入参、出参
        String SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_PATH");

        String river_model_base_path = SWYB_PATH + File.separator+riverModelPath;

        String SWYB_MODEL_TEMPLATE_OUTPUT = river_model_base_path + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator+ planId +File.separator+"result.txt";//输出的地址
        File file = new File(SWYB_MODEL_TEMPLATE_OUTPUT);
        if(file.exists()){
            return "1";
        }
        return "0";
    }

    @Override
    public Object getModelResultQ(String planId) {
        JSONArray list = new JSONArray();

        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);
        Long step = planInfo.getnOutputtm() / 60;//步长(小时)

        String riverId = planInfo.getRiverId();
        String riverModelPath = "";

        if (SANRUHAI.contains(riverId)){
            riverModelPath = "SANRUHAI";
        }else if(XIAOQINGHE.contains(riverId)){
            riverModelPath = "XIAOQINGHE";
        }else if (ZHIMAIHE.contains(riverId)){
            riverModelPath = "ZHIMAIHE";
        }else {
            System.out.println("河流信息错误 无法计算，无法计算");
        }
        //创建入参、出参
        String SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_PATH");

        String river_model_base_path = SWYB_PATH + File.separator+riverModelPath;

        String SWYB_MODEL_OUTPUT_RESULT = river_model_base_path + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator+ planId +"/result.txt";//输出的地址

        //解析河道断面
        Map<String, List<String>> finalResult = getModelResult(SWYB_MODEL_OUTPUT_RESULT);
        //如果时小清河模型则解析水库断面
        Map<String, List<String>> shuikuResult = new HashMap<>();
        if(riverModelPath.equals("XIAOQINGHE")){
            String SWYB_MODEL_OUTPUT_SHUIKU = river_model_base_path + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                    + File.separator+ planId +"/shuiku_result.txt";//输出的地址
            shuikuResult = getModelResult(SWYB_MODEL_OUTPUT_SHUIKU);
            finalResult.putAll(shuikuResult);
        }

        //找到河系关联的断面
        List<WrpRcsBsin> listByRiverId = wrpRcsBsinDao.findListByRiverId(riverId);
        List<String> sections = listByRiverId.stream().map(WrpRcsBsin::getRvcrcrsccd).collect(Collectors.toList());

        if(finalResult!=null && finalResult.size()>0){
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            for(String sectionId : sections){
                JSONObject valObj = new JSONObject();
                list.add(valObj);
                valObj.put("RCS_ID",sectionId);
                JSONArray valList = new JSONArray();
                valObj.put("values",valList);
                List<String> dataList = finalResult.get(sectionId);
                if(dataList!=null && dataList.size()>0){
                    int index = 0;
                    int count = 0;
                    for (Date time = startTime; time.before(endTime); time = DateUtil.getNextHour(startTime, count)) {
                        try{
                            JSONObject dataObj = new JSONObject();
                            dataObj.put("time",DateUtil.dateToStringNormal3(time));
                            dataObj.put("q",dataList.get(index));
                            valList.add(dataObj);
                            count+=step;
                            index++;
                        }catch (Exception e){
                            break;
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 方案计算相关数据入库
     * @param planId
     * @return
     */
    @Override
    public Object saveModelData(String planId) {
        //保存方案基本信息
        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);
        ywkPlaninfoDao.save(planInfo);
        //保存方案计算-降雨量条件
        List<YwkPlaninRainfall> rainfallList = new ArrayList<>();
        List<Map<String, Object>> stPptnRByStartTimeAndEndTime = stPptnRDao.findStPptnRByStartTimeAndEndTime(null,null);
        for (Map<String, Object> map : stPptnRByStartTimeAndEndTime) {
            String tm = map.get("tm") + "";
            String stcd = map.get("STCD") + "";
            Double drp = Double.parseDouble(map.get("sum") + "");
            Date dataTime = DateUtil.getDateWithFormat(tm, "yyyy-MM-dd HH");
            rainfallList.add(new YwkPlaninRainfall(StrUtil.getUUID(),stcd,drp,dataTime,planId));
        }
        //保存方案输出流量结果数据
        Object modelResultQ = getModelResultQ(planId);
        return null;
    }

}
