package com.essence.business.xqh.service.fhybdd;

import com.essence.business.xqh.api.fhybdd.service.ModelCallHandleDataService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.FileUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanOutputQ;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninRainfall;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class ModelCallHandleDataImpl implements ModelCallHandleDataService {

    @Autowired
    EntityManager entityManager;

    @Transactional(propagation = Propagation.REQUIRED)
    @Async
    @Override
    public CompletableFuture<Integer> saveRainToDb(List<YwkPlaninRainfall> result) {
        String sql = "INSERT ALL  ";
        String insertSql="";

        for (YwkPlaninRainfall ywkPlaninRainfall : result){
            String id = ywkPlaninRainfall.getcId();
            String stcd = ywkPlaninRainfall.getcStcd();
            Double aDouble = ywkPlaninRainfall.getnDrp();
            //Date date = ywkPlaninRainfall.getdTime();
            String planid = ywkPlaninRainfall.getnPlanid();
            insertSql = insertSql+"INTO YWK_PLANIN_RAINFALL VALUES"+"( '"+id+"','"+stcd+"',"+aDouble+",? ,'"+planid+"') ";

        }
        //insertSql = insertSql.substring(0,insertSql.length()-1);
        insertSql = insertSql +" SELECT 1 FROM DUAL ";
        Query nativeQuery = entityManager.createNativeQuery(sql + insertSql);//.executeUpdate();
        int z = 1;
        for (YwkPlaninRainfall ywkPlaninRainfall : result){
            Date date = ywkPlaninRainfall.getdTime();
            nativeQuery.setParameter(z,date);
            z++;
        }
        nativeQuery.executeUpdate();
       return CompletableFuture.completedFuture(1);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Async
    @Override
    public CompletableFuture<Integer> savePlanOut(List<YwkPlanOutputQ> result) {
        String sql = "INSERT ALL  ";
        String insertSql="";

        for (YwkPlanOutputQ ywkPlanOutputQ : result){
            String idcid = ywkPlanOutputQ.getIdcId();
            String planId = ywkPlanOutputQ.getnPlanid();
            //Date date = ywkPlanOutputQ.getdTime();
            Double q = ywkPlanOutputQ.getnQ();
            String rvcrcrsccd = ywkPlanOutputQ.getRvcrcrsccd();
            insertSql = insertSql+"INTO YWK_PLAN_OUTPUT_Q VALUES"+"( '"+idcid+"','"+planId+"',?,"+q+" ,'"+rvcrcrsccd+"') ";

        }
        //insertSql = insertSql.substring(0,insertSql.length()-1);
        insertSql = insertSql +" SELECT 1 FROM DUAL ";
        Query nativeQuery = entityManager.createNativeQuery(sql + insertSql);//.executeUpdate();
        int z = 1;
        for (YwkPlanOutputQ outputQ : result){
            Date date = outputQ.getdTime();
            nativeQuery.setParameter(z,date);
            z++;
        }
        nativeQuery.executeUpdate();
        return CompletableFuture.completedFuture(1);
    }


    @Async
    @Override
    public void handleCsvAndResult(Integer tag ,YwkPlaninfo planInfo) {




    }

    @Override
    @Async
    public CompletableFuture<Integer> callOneMode(String JDPD_MODEL_PATH, String template, String out, String run, String rvcrcrsccd, YwkPlaninfo planInfo, Map<String, List<Map<String, String>>> waterLevelFlowMap) {
        long startTime = System.currentTimeMillis(); //获取开始时间
        String JDPD_MODEL_TEMPLATE = JDPD_MODEL_PATH + File.separator + template;
        String JDPD_MODEL_TEMPLATE_INPUT = JDPD_MODEL_TEMPLATE
                + File.separator + "INPUT" + File.separator + planInfo.getnPlanid() + File.separator + rvcrcrsccd; //输入的地址
        String JDPD_MODEL_TEMPLATE_OUTPUT = JDPD_MODEL_PATH + File.separator + out
                + File.separator + planInfo.getnPlanid() + File.separator + rvcrcrsccd;//输出的地址

        //模型运行的config
        String JDPD_MODEL_RUN = JDPD_MODEL_PATH + File.separator + run;
        String JDPD_MODEL_RUN_PLAN = JDPD_MODEL_RUN + File.separator + planInfo.getnPlanid() + File.separator + rvcrcrsccd;

        File inputJdpdPath = new File(JDPD_MODEL_TEMPLATE_INPUT);
        File outJdpdPath = new File(JDPD_MODEL_TEMPLATE_OUTPUT);
        File runJdpdPath = new File(JDPD_MODEL_RUN_PLAN);

        inputJdpdPath.mkdirs();
        outJdpdPath.mkdirs();
        runJdpdPath.mkdirs();

        //1、组装input.csv文件
        int result = writeInputCsv(JDPD_MODEL_TEMPLATE_INPUT, planInfo, rvcrcrsccd, waterLevelFlowMap.get(rvcrcrsccd));
        if(result == 0) {
            System.out.println("精度评定模型:写入input.csv失败");
            throw new RuntimeException("精度评定模型:写入input.csv失败");
        }

        //2、复制config以及可执行文件
        int result1 = copyExeFile(JDPD_MODEL_RUN, JDPD_MODEL_RUN_PLAN);
        if (result1 == 0) {
            System.out.println("精度评定模型:复制执行文件与config文件写入失败。。。");
            throw new RuntimeException("精度评定模型:复制执行文件与config文件写入失败。。。");

        }

        //3、修改config文件
        int result2 = writeDataToConfig(JDPD_MODEL_RUN_PLAN, JDPD_MODEL_TEMPLATE_INPUT, JDPD_MODEL_TEMPLATE_OUTPUT);
        if (result2 == 0) {
            System.out.println("精度评定模型:修改config文件失败。。。");
            throw new RuntimeException("精度评定模型:修改config文件失败。。。");
        }
        long endTime = System.currentTimeMillis();   //获取开始时间
        System.out.println("精度评定模型:组装精度评定模型所用的参数的时间为:" + (endTime - startTime) + "毫秒");


        //4.调用模型计算
        System.out.println("精度评定模型:开始精度评定模型计算。。。");
        System.out.println("精度评定模型:模型计算路径为。。。" + JDPD_MODEL_RUN_PLAN + File.separator + "startUp.bat");
        runModelExe(JDPD_MODEL_RUN_PLAN + File.separator + "startUp.bat");
        endTime = System.currentTimeMillis();
        System.out.println("精度评定模型:模型计算结束。。。，所用时间为:" + (endTime - startTime) + "毫秒");
        startTime = System.currentTimeMillis();

        //判断是否执行成功，是否有jingdu_pingding.txt文件
        String jdStr = JDPD_MODEL_TEMPLATE_OUTPUT + File.separator + "jingdu_pingding.txt";
        File jdFile = new File(jdStr);
        if (!jdFile.exists()) {//存在表示执行失败
            System.out.println("精度评定模型:模型计算失败。。不存在jingdu_pingding.txt文件");
        } else {
            System.out.println("精度评定模型:模型计算成功。。存在jingdu_pingding.txt文件");
        }
        return CompletableFuture.completedFuture(1);
    }

    private int writeDataToConfig(String jdpd_model_run_plan, String jdpd_model_template_input, String jdpd_model_template_output) {
        String configUrl = jdpd_model_run_plan + File.separator + "config.txt";
        String inputUrl = "input&&" + jdpd_model_template_input + File.separator + "input.csv";
        String resultUrl = "result&&" + jdpd_model_template_output + File.separator + "jingdu_pingding.txt";

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 写路径
            bw.write(resultUrl);
            bw.newLine();
            bw.write(inputUrl);
            bw.close();
            System.out.println("精度评定模型:写入config成功");
            return 1;
        } catch (Exception e) {
            // File对象的创建过程中的异常捕获
            System.out.println("精度评定模型:写入config失败");
            e.printStackTrace();
            return 0;
        }
    }

    private int copyExeFile(String jdpd_model_run, String jdpd_model_run_plan) {
        String exeUrl = jdpd_model_run + File.separator + "main.exe";
        String exeInputUrl = jdpd_model_run_plan + File.separator + "main.exe";
        String batUrl = jdpd_model_run + File.separator + "startUp.bat";
        String batInputUrl = jdpd_model_run_plan + File.separator + "startUp.bat";
        try {
            FileUtil.copyFile(exeUrl, exeInputUrl, true);
            FileUtil.copyFile(batUrl, batInputUrl, true);
            System.err.println("精度评定模型：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("精度评定模型：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    private int writeInputCsv(String JDPD_MODEL_TEMPLATE_INPUT, YwkPlaninfo planInfo,
                              String rvcrcrsccd, List<Map<String, String>> waterLevelFlowList) {
        //获取水文预报模型文件
        String SWYB_SHUIWEN_MODEL_PATH = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_BASE_NEW_SHUIWEN_MODEL_PATH");
        String SHUIWEN_MODEL_TEMPLATE_OUTPUT = SWYB_SHUIWEN_MODEL_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planInfo.getnPlanid();//输出的地址

        Map<String, List<String>> finalResult = getModelResult(SHUIWEN_MODEL_TEMPLATE_OUTPUT);
        Long step = planInfo.getnOutputtm();//步长(小时)
        if (finalResult != null && finalResult.size() > 0) {
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            List<String> dataList = finalResult.get(rvcrcrsccd); //根据断面ID获取此行数据

            ArrayList<String> qList = new ArrayList<>(); //流量预报值  -- y_pred流量预报值
            ArrayList<String> zList = new ArrayList<>(); //水位预报值  -- s_pred水位预报值
//            String s = (dataList != null && dataList.size() > 0)?"a":"b";
            if (dataList != null && dataList.size() > 0) {
                int index = 0;

                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                    try {
                        qList.add(dataList.get(index));
                        index++;
                    } catch (Exception e) {
                        break;
                    }
                }

                for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                    try {
                        zList.add(dataList.get(index));
                        index++;
                    } catch (Exception e) {
                        break;
                    }
                }

                String inputOutCsv = JDPD_MODEL_TEMPLATE_INPUT + File.separator + "input.csv";

                try {
                    int lineIndex = 0;
                    BufferedWriter bw = new BufferedWriter(new FileWriter(inputOutCsv, false)); // 附加
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    // 添加新的数据行
                    bw.write("time,y_true,y_pred,s_true,s_pred"); //编写表头
                    for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                        bw.newLine();
                        bw.write(sdf.format(time) + "," + waterLevelFlowList.get(lineIndex).get("y_true") +
                                "," + qList.get(lineIndex) + "," + waterLevelFlowList.get(lineIndex).get("s_true") + "," + zList.get(lineIndex)); //填充数据
                        lineIndex++;
                    }
                    bw.close();
                    System.out.println("精度评定模型:精度评定模型input.csv输入文件写入成功");
                    return 1;
                } catch (Exception e) {
                    // File对象的创建过程中的异常捕获
                    System.out.println("精度评定模型:精度评定模型input.csv输入文件写入失败");
                    e.printStackTrace();
                    return 0;
                }
            }else{
                //没有对应预报数据时
                String inputOutCsv = JDPD_MODEL_TEMPLATE_INPUT + File.separator + "input.csv";

                try {
                    int lineIndex = 0;
                    BufferedWriter bw = new BufferedWriter(new FileWriter(inputOutCsv, false)); // 附加
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    // 添加新的数据行
                    bw.write("time,y_true,y_pred,s_true,s_pred"); //编写表头
                    for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextMinute(time, step.intValue())) {
                        bw.newLine();
                        bw.write(sdf.format(time) + "," + waterLevelFlowList.get(lineIndex).get("y_true") +
                                "," + 0 + "," + waterLevelFlowList.get(lineIndex).get("s_true") + "," + 0);
                        lineIndex++;
                    }
                    bw.close();
                    System.out.println("精度评定模型:精度评定模型input.csv输入文件写入成功");
                    return 1;
                } catch (Exception e) {
                    // File对象的创建过程中的异常捕获
                    System.out.println("精度评定模型:精度评定模型input.csv输入文件写入失败");
                    e.printStackTrace();
                    return 0;
                }
            }

        }
        return 0;
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
    }
}
