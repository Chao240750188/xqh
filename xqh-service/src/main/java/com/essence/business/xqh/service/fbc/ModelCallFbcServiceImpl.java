package com.essence.business.xqh.service.fbc;

import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.fbc.ModelCallFbcService;
import com.essence.business.xqh.api.fbc.dto.PlanInfoFbcVo;
import com.essence.business.xqh.api.fbc.dto.YwkFbcBoundaryDataDto;
import com.essence.business.xqh.api.fbc.dto.YwkFbcPlanInfoBoundaryDto;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.hsfxtk.dto.YwkBoundaryBasicDto;
import com.essence.business.xqh.common.util.*;
import com.essence.business.xqh.dao.dao.fbc.*;
import com.essence.business.xqh.dao.dao.fhybdd.YwkModelDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.entity.fbc.*;
import com.essence.business.xqh.dao.entity.fhybdd.YwkModel;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import com.essence.framework.util.StrUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 风暴潮模型业务层
 */
@Service
public class ModelCallFbcServiceImpl implements ModelCallFbcService {

    @Autowired
    private YwkPlaninfoDao ywkPlaninfoDao;

    @Autowired
    private YwkModelDao ywkModelDao;

    @Autowired
    private FbcBoundaryQDao fbcBoundaryQDao;
    @Autowired
    private FbcBoundaryZDao fbcBoundaryZDao;
    @Autowired
    private FbcHdpHhtdzWDao fbcHdpHhtdzWDao;
    @Autowired
    private FbcWindDirectionDao fbcWindDirectionDao;
    @Autowired
    private FbcWindMaximumSpeedDao fbcWindMaximumSpeedDao;
    @Autowired
    private FbcWindMinimumPressureDao fbcWindMinimumPressureDao;
    @Autowired
    private FbcWindPressureDao fbcWindPressureDao;
    @Autowired
    private FbcWindSpeedDao fbcWindSpeedDao;

    /**
     * 根据方案名称校验方案是否存在
     *
     * @param planName
     */
    @Override
    public Integer getPlanInfoByName(String planName) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_FBC");

        List<YwkPlaninfo> byCPlanname = ywkPlaninfoDao.findByCPlannameAndPlanSystem(planName, planSystem);
        return byCPlanname.size();
    }

    @Override
    public String savePlanToDb(PlanInfoFbcVo vo) {

        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_FBC");
        List<YwkModel> ywkModelByModelType = ywkModelDao.getYwkModelByModelType(planSystem);
        YwkModel ywkModel = ywkModelByModelType.get(0);


        if (StrUtil.isEmpty(vo.getnPlanid())) {
            vo.setnPlanid(StrUtil.getUUID());
        }
        Date startTime = vo.getStartTime(); //开始时间
        Date endTIme = vo.getEndTime();  //结束时间
        Date periodEndTime = vo.getPeriodEndTime();//预见结束时间
        if (periodEndTime != null) {
            endTIme = periodEndTime;
        }
        Long step = vo.getStep();//以分钟为单位

        //方案基本信息入库
        YwkPlaninfo ywkPlaninfo = new YwkPlaninfo();
        ywkPlaninfo.setnPlanid(vo.getnPlanid());
        ywkPlaninfo.setPlanSystem(planSystem);
        ywkPlaninfo.setcPlanname(vo.getcPlanname());
        ywkPlaninfo.setnCreateuser("user");
        ywkPlaninfo.setnPlancurrenttime(new Date());
        ywkPlaninfo.setdCaculatestarttm(startTime);//方案计算开始时间
        ywkPlaninfo.setdCaculateendtm(endTIme);//方案计算结束时间
        ywkPlaninfo.setnPlanstatus(0l);//方案状态
        ywkPlaninfo.setnOutputtm(step);//设置间隔分钟
        ywkPlaninfo.setnModelid(ywkModel.getIdmodelId());
        ywkPlaninfo.setdRainstarttime(startTime);
        ywkPlaninfo.setdRainendtime(endTIme);
        ywkPlaninfo.setdOpensourcestarttime(startTime);
        ywkPlaninfo.setdOpensourceendtime(endTIme);
        ywkPlaninfo.setnCreatetime(DateUtil.getCurrentTime());
        YwkPlaninfo saveDbo = ywkPlaninfoDao.save(ywkPlaninfo);
        return saveDbo.getnPlanid();
    }

    @Override
    public List<Object> getBoundaryZqBasic(ModelParamVo modelParamVo) {
        //先从缓存获取
        List<Object> dataCacheList = (List<Object>) CacheUtil.get("modelFbcBoundaryZq", modelParamVo.getnPlanid());
        if (dataCacheList != null)
            return dataCacheList;
        //如果缓存没有从数据库获取
        List<Object> list = new ArrayList<>();
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(modelParamVo.getnPlanid());
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();

        //封装边界流量数据
        for (int i = 1; i < 8; i++) {
            JSONObject jsonObject = new JSONObject();
            YwkBoundaryBasicDto YwkBoundaryBasicDto = new YwkBoundaryBasicDto();
            jsonObject.put("boundary", "boundary" + i);
            List<Object> dataList = new ArrayList<>();
            int count = 0;
            for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
                JSONObject dataJsonObj = new JSONObject();
                dataJsonObj.put("time", DateUtil.dateToStringNormal3(time));
                dataJsonObj.put("boundaryData", 0.0);
                dataList.add(dataJsonObj);
                count++;
            }
            jsonObject.put("dataList", dataList);
            list.add(jsonObject);
        }
        return list;
    }

    @Override
    public Workbook exportBoundaryZqTemplate(String planId) {
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        //封装边界模板数据
        XSSFWorkbook workbook = new XSSFWorkbook();

        //设置样式
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);//字体高度
        font.setFontName("宋体");//字体
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true);//自动换行
        XSSFSheet sheet = workbook.createSheet("边界数据导入模板");
        //填充表头
        //第一行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue("时间/边界值");
        //设置自动列宽
        sheet.setColumnWidth(0, 5100);

        //封装第一行标题头7个条件
        sheet.setColumnWidth(1, 4000);
        XSSFCell cell1 = row.createCell(1);
        cell1.setCellStyle(style);
        cell1.setCellValue("羊角沟站水位（m）");

        sheet.setColumnWidth(2, 4000);
        XSSFCell cell2 = row.createCell(2);
        cell2.setCellStyle(style);
        cell2.setCellValue("羊角沟站流量（m³/s）");

        sheet.setColumnWidth(3, 4000);
        XSSFCell cell3 = row.createCell(3);
        cell3.setCellStyle(style);
        cell3.setCellValue("羊角沟站实时风向");

        sheet.setColumnWidth(4, 4000);
        XSSFCell cell4 = row.createCell(4);
        cell4.setCellStyle(style);
        cell4.setCellValue("羊角沟站风速（m/s）");

        sheet.setColumnWidth(5, 4000);
        XSSFCell cell5 = row.createCell(5);
        cell5.setCellStyle(style);
        cell5.setCellValue("羊角沟站气压（Pa）");

        sheet.setColumnWidth(6, 4000);
        XSSFCell cell6 = row.createCell(6);
        cell6.setCellStyle(style);
        cell6.setCellValue("台风中心最低气压（Pa）");

        sheet.setColumnWidth(7, 4000);
        XSSFCell cell7 = row.createCell(7);
        cell7.setCellStyle(style);
        cell7.setCellValue("台风中心最大风速（m/s）");

        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        int beginLine = 1;
        //封装数据
        int count = 0;
        for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
            XSSFRow row1 = sheet.createRow(beginLine);
            row1.createCell(0).setCellValue(DateUtil.dateToStringNormal3(time));
            count++;
            beginLine++;
        }
        return workbook;
    }

    /**
     * 上传界条件(水位/流量)数据解析-Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    @Override
    public List<Object> importBoundaryZq(MultipartFile mutilpartFile, String planId) {
        List<Object> boundaryDataList = new ArrayList<>();

        //解析ecxel数据 不包含第一行
        List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile, 1);
        // 判断有无数据 时间-每个边界的值集合
        Map<String, List<String>> dataMap = new HashMap<>();
        if (excelList != null && excelList.size() > 0) {
            // 遍历每行数据（除了标题）
            for (int i = 0; i < excelList.size(); i++) {
                String[] strings = excelList.get(i);
                if (strings != null && strings.length > 0) {
                    // 封装每列（每个指标项数据）
                    List<String> dataList = new ArrayList<>(Arrays.asList(strings));
                    dataMap.put((strings[0] + "").trim(), dataList.subList(0, dataList.size()));
                }
            }
        }
        //封装边界流量数据
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        for (int i = 0; i < 7; i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("boundary", "boundary" + (i + 1));
            //封装时间数据
            List<Object> dataList = new ArrayList<>();
            int count = 0;
            for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
                String timeStr = DateUtil.dateToStringNormal3(time);
                List<String> strings = dataMap.get(timeStr);
                JSONObject dataTimeObj = new JSONObject();
                dataTimeObj.put("time", timeStr);
                try {
                    dataTimeObj.put("boundaryData", Double.parseDouble(strings.get(i + 1)));
                } catch (Exception e) {
                    dataTimeObj.put("boundaryData", 0.0);
                }
                dataList.add(dataTimeObj);
                count++;
            }
            jsonObject.put("dataList", dataList);
            boundaryDataList.add(jsonObject);
        }
        //存入缓存
        CacheUtil.saveOrUpdate("modelFbcBoundaryZq", planId, boundaryDataList);
        return boundaryDataList;
    }

    /**
     * 方案计算边界条件值(水位/流量)-保存提交入库
     *
     * @return
     */
    @Override
    @Transactional
    public List<YwkFbcPlanInfoBoundaryDto> saveimportBoundaryZq(List<YwkFbcPlanInfoBoundaryDto> ywkPlanInfoBoundaryDtoList, String planId) {
        //封装边界条件数据
        for (YwkFbcPlanInfoBoundaryDto boundaryDto : ywkPlanInfoBoundaryDtoList) {
            String boundary = boundaryDto.getBoundary();
            List<YwkFbcBoundaryDataDto> dataList = boundaryDto.getDataList();
            //如果是水位数据
            if ("boundary1".equals(boundary)) {
                List<FbcBoundaryZ> fbcBoundaryZList = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    YwkFbcBoundaryDataDto data = dataList.get(i);
                    FbcBoundaryZ fbcBoundaryZ = new FbcBoundaryZ();
                    fbcBoundaryZ.setId(StrUtil.getUUID());
                    fbcBoundaryZ.setnPlanid(planId);
                    fbcBoundaryZ.setAbsoluteTime(data.getTime());
                    fbcBoundaryZ.setRelativeTime(i + 1l);
                    fbcBoundaryZ.setZ(data.getBoundaryData());
                    fbcBoundaryZList.add(fbcBoundaryZ);
                }
                if (fbcBoundaryZList.size() > 0)
                    fbcBoundaryZDao.saveAll(fbcBoundaryZList);
            }
            //如果是流量数据
            if ("boundary2".equals(boundary)) {
                List<FbcBoundaryQ> fbcBoundaryQList = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    YwkFbcBoundaryDataDto data = dataList.get(i);
                    FbcBoundaryQ fbcBoundaryQ = new FbcBoundaryQ();
                    fbcBoundaryQ.setId(StrUtil.getUUID());
                    fbcBoundaryQ.setnPlanid(planId);
                    fbcBoundaryQ.setAbsoluteTime(data.getTime());
                    fbcBoundaryQ.setRelativeTime(i + 1l);
                    fbcBoundaryQ.setQ(data.getBoundaryData());
                    fbcBoundaryQList.add(fbcBoundaryQ);
                }
                if (fbcBoundaryQList.size() > 0)
                    fbcBoundaryQDao.saveAll(fbcBoundaryQList);
            }
            //如果是实时风向
            if ("boundary3".equals(boundary)) {
                List<FbcWindDirection> fbcWindDirectionList = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    YwkFbcBoundaryDataDto data = dataList.get(i);
                    FbcWindDirection fbcWindDirection = new FbcWindDirection();
                    fbcWindDirection.setId(StrUtil.getUUID());
                    fbcWindDirection.setnPlanid(planId);
                    fbcWindDirection.setAbsoluteTime(data.getTime());
                    fbcWindDirection.setRelativeTime(i + 1l);
                    fbcWindDirection.setDirection(data.getBoundaryData() + "");
                    fbcWindDirectionList.add(fbcWindDirection);
                }
                if (fbcWindDirectionList.size() > 0)
                    fbcWindDirectionDao.saveAll(fbcWindDirectionList);
            }
            //如果是风速
            if ("boundary4".equals(boundary)) {
                List<FbcWindSpeed> fbcWindSpeedList = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    YwkFbcBoundaryDataDto data = dataList.get(i);
                    FbcWindSpeed fbcWindSpeed = new FbcWindSpeed();
                    fbcWindSpeed.setId(StrUtil.getUUID());
                    fbcWindSpeed.setnPlanid(planId);
                    fbcWindSpeed.setAbsoluteTime(data.getTime());
                    fbcWindSpeed.setRelativeTime(i + 1l);
                    fbcWindSpeed.setSpeed(data.getBoundaryData());
                    fbcWindSpeedList.add(fbcWindSpeed);
                }
                if (fbcWindSpeedList.size() > 0)
                    fbcWindSpeedDao.saveAll(fbcWindSpeedList);
            }
            //如果是气压
            if ("boundary5".equals(boundary)) {
                List<FbcWindPressure> fbcWindPressureList = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    YwkFbcBoundaryDataDto data = dataList.get(i);
                    FbcWindPressure fbcWindPressure = new FbcWindPressure();
                    fbcWindPressure.setId(StrUtil.getUUID());
                    fbcWindPressure.setnPlanid(planId);
                    fbcWindPressure.setAbsoluteTime(data.getTime());
                    fbcWindPressure.setRelativeTime(i + 1l);
                    fbcWindPressure.setPressure(data.getBoundaryData());
                    fbcWindPressureList.add(fbcWindPressure);
                }
                if (fbcWindPressureList.size() > 0)
                    fbcWindPressureDao.saveAll(fbcWindPressureList);
            }
            //如果是最低气压
            if ("boundary6".equals(boundary)) {
                List<FbcWindMinimumPressure> fbcWindMinimumPressureList = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    YwkFbcBoundaryDataDto data = dataList.get(i);
                    FbcWindMinimumPressure fbcWindMinimumPressure = new FbcWindMinimumPressure();
                    fbcWindMinimumPressure.setId(StrUtil.getUUID());
                    fbcWindMinimumPressure.setnPlanid(planId);
                    fbcWindMinimumPressure.setAbsoluteTime(data.getTime());
                    fbcWindMinimumPressure.setRelativeTime(i + 1l);
                    fbcWindMinimumPressure.setPressure(data.getBoundaryData());
                    fbcWindMinimumPressureList.add(fbcWindMinimumPressure);
                }
                if (fbcWindMinimumPressureList.size() > 0)
                    fbcWindMinimumPressureDao.saveAll(fbcWindMinimumPressureList);
            }
            //如果是最大风速
            if ("boundary7".equals(boundary)) {
                List<FbcWindMaximumSpeed> fbcWindMaximumSpeedList = new ArrayList<>();
                for (int i = 0; i < dataList.size(); i++) {
                    YwkFbcBoundaryDataDto data = dataList.get(i);
                    FbcWindMaximumSpeed fbcWindMaximumSpeed = new FbcWindMaximumSpeed();
                    fbcWindMaximumSpeed.setId(StrUtil.getUUID());
                    fbcWindMaximumSpeed.setnPlanid(planId);
                    fbcWindMaximumSpeed.setAbsoluteTime(data.getTime());
                    fbcWindMaximumSpeed.setRelativeTime(i + 1l);
                    fbcWindMaximumSpeed.setSpeed(data.getBoundaryData());
                    fbcWindMaximumSpeedList.add(fbcWindMaximumSpeed);
                }
                if (fbcWindMaximumSpeedList.size() > 0)
                    fbcWindMaximumSpeedDao.saveAll(fbcWindMaximumSpeedList);
            }
        }
        //存入缓存
        CacheUtil.saveOrUpdate("modelFbcBoundaryZq", planId, ywkPlanInfoBoundaryDtoList);

        return ywkPlanInfoBoundaryDtoList;
    }

    @Override
    public List<FbcHdpHhtdzW> fbcModelCall(String planId) {
        List<FbcHdpHhtdzW> dzwList = new ArrayList<>();
        //调用模型计算
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if (planInfo == null) {
            System.out.println("计划planid没有找到记录");
            return dzwList;
        }
        //写入模型输入文件路径
        String fbc_path = PropertiesUtil.read("/filePath.properties").getProperty("FBC_MODEL");

        String fbc_model_template_output = fbc_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId; //输出的地址

        String fbc_model_template = fbc_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");//默认文件对位置

        String fbc_model_template_input = fbc_model_template
                + File.separator + "INPUT" + File.separator + planId; //输入的地址

        String fbc_model_template_run = fbc_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");

        String fbc_model_template_run_plan = fbc_model_template_run + File.separator + planId;

        File inputPlanPath = new File(fbc_model_template_input);
        File outputPlanPath = new File(fbc_model_template_output);
        File runPath = new File(fbc_model_template_run_plan);
        inputPlanPath.mkdirs();
        outputPlanPath.mkdirs();
        runPath.mkdirs();
        //复制模型运行文件到方案文件夹下
        int copyResult = copyModelRunInputTemplateFile(fbc_model_template_run, fbc_model_template_run_plan,fbc_model_template_input);
        if (copyResult == 0) {
            System.out.println("风暴潮模型模板文件复制失败。。。");
            return dzwList;
        }

        //根据方案获取条件数据
        //水位
        List<FbcBoundaryZ> zList = fbcBoundaryZDao.findByNPlanidOrderByAbsoluteTime(planId);
        //流量
        List<FbcBoundaryQ> qList = fbcBoundaryQDao.findByNPlanidOrderByAbsoluteTime(planId);
        //实时风向
        List<FbcWindDirection> directionList = fbcWindDirectionDao.findByNPlanidOrderByAbsoluteTime(planId);
        //风速
        List<FbcWindSpeed> speedList = fbcWindSpeedDao.findByNPlanidOrderByAbsoluteTime(planId);
        //气压
        List<FbcWindPressure> pressureList = fbcWindPressureDao.findByNPlanidOrderByAbsoluteTime(planId);
        //最低气压
        List<FbcWindMinimumPressure> minimumPressureList = fbcWindMinimumPressureDao.findByNPlanidOrderByAbsoluteTime(planId);
        //最大风速
        List<FbcWindMaximumSpeed> maximumSpeedList = fbcWindMaximumSpeedDao.findByNPlanidOrderByAbsoluteTime(planId);
        //写入边界条件
        int resultBoundary = writeDataToInputFbcCsv(planInfo,fbc_model_template_input,zList,qList,directionList,speedList,pressureList,minimumPressureList,maximumSpeedList);
        if (resultBoundary == 0) {
            System.out.println("风暴潮模型计算:边界fbc.csv输入文件写入失败。。。");
            return dzwList;
        }
        //修改config文件
        int resultConfig = writeConfig(fbc_model_template_run_plan, fbc_model_template_input, fbc_model_template_output);
        if (resultConfig == 0) {
            System.out.println("水动力模型计算:config文件写入失败。。。");
            return dzwList;
        }
        //调用模型计算
        System.out.println("风暴潮模型计算:开始风暴潮模型计算。。。");
        runModelExe(fbc_model_template_run_plan + File.separator + "startUp.bat");
        System.out.println("水动力模型计算:水动力模型计算结束。。。");

        //模型计算完成调用接口保存输出结果文件
        dzwList = (List<FbcHdpHhtdzW>) getModelResultCsv(planId);
        if(dzwList.size()>0){
            fbcHdpHhtdzWDao.saveAll(dzwList);
        }
        return dzwList;
    }

    private void runModelExe(String modelRunPath) {
        BufferedReader br = null;
        BufferedReader brError = null;
        try {
            // 执行exe cmd可以为字符串(exe存放路径)也可为数组，调用exe时需要传入参数时，可以传数组调用(参数有顺序要求)
            Process p = Runtime.getRuntime().exec(modelRunPath);
            String line = null;
            br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            brError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            // {
            while ((line = brError.readLine()) != null) {
                // 输出exe输出的信息以及错误信息
                System.out.println(line);
            }
            if (brError.readLine() != null){
                System.out.println("风暴潮模型调用失败！");
            }else {
                System.out.println("风暴潮模型调用成功！");
            }
        } catch (Exception e) {
            System.out.println("风暴潮模型调用失败！");
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

    private int copyModelRunInputTemplateFile(String fbc_model_template_run, String fbc_model_template_run_plan,String fbc_model_template_input) {
        try{
            String modelExeUrl = fbc_model_template_run + File.separator + "FBC_MODEL.exe";
            String modelPlanExeUrl = fbc_model_template_run_plan + File.separator + "FBC_MODEL.exe";
            String modelBatUrl = fbc_model_template_run + File.separator + "startUp.bat";
            String modelPlanBatUrl = fbc_model_template_run_plan + File.separator + "startUp.bat";
            String modelM1h5 = fbc_model_template_run + File.separator + "m1.h5";
            String modelPlanM1h5 = fbc_model_template_input + File.separator + "m1.h5";
            String modelScaler = fbc_model_template_run + File.separator + "scaler01";
            String modelPlanScaler = fbc_model_template_input + File.separator + "scaler01";
            FileUtil.copyFile(modelExeUrl, modelPlanExeUrl, true);
            FileUtil.copyFile(modelM1h5, modelPlanM1h5, true);
            FileUtil.copyFile(modelScaler, modelPlanScaler, true);
            FileUtil.copyFile(modelBatUrl, modelPlanBatUrl, true);
        }catch (Exception e){
            System.out.println("模板文件复制出错。");
            return 0;
        }
        return 1;

    }

    private int writeConfig(String fbc_model_template_run_plan, String fbc_model_template_input, String fbc_model_template_output) {
        List<String> finals = new ArrayList<>();
        String configUrl = fbc_model_template_run_plan + File.separator + "config.txt";
        String chushiUrl = "chushi&&" + fbc_model_template_input + File.separator + "fbc.csv";
        String muxingNUrl = "muxing&&" + fbc_model_template_input + File.separator + "m1.h5";
        String jieguoUrl = "jieguo&&" + fbc_model_template_output + File.separator + "result.csv";
        String scalerUrl = "scaler&&" + fbc_model_template_input + File.separator + "scaler01";

        finals.add(chushiUrl);
        finals.add(muxingNUrl);
        finals.add(jieguoUrl);
        finals.add(scalerUrl);
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(configUrl, false)); // 附加
            // 添加新的数据行
            for (int i = 0; i < finals.size(); i++) {
                String s = finals.get(i);
                if (i == finals.size() - 1) {
                    bw.write(s);
                } else {
                    bw.write(s);
                    bw.newLine();
                }
            }
            bw.close();
            System.out.println("写入水动力模型config成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("写入风暴潮模型config失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("写入风暴潮模型config失败");
            e.printStackTrace();
            return 0;
        }
    }

    private int writeDataToInputFbcCsv(YwkPlaninfo planInfo,String fbc_model_template_input, List<FbcBoundaryZ> zList, List<FbcBoundaryQ> qList, List<FbcWindDirection> directionList, List<FbcWindSpeed> speedList, List<FbcWindPressure> pressureList, List<FbcWindMinimumPressure> minimumPressureList, List<FbcWindMaximumSpeed> maximumSpeedList) {
        String boundaryInputUrl = fbc_model_template_input + File.separator + "fbc.csv";
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(boundaryInputUrl, false), "UTF-8"));
            // 添加新的数据行
            String head = "date,chaowei,dew,temp,press,wnd_dir,wnd_spd,snow,rain";
            bw.write(head);
            bw.newLine();
            int beginLine = 1;
            //封装数据
            List<String> dataList = new ArrayList<>();
            int count = 0;
            for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
                String data = DateUtil.dateToStringNormal(time);
                Double z = zList.get(count)==null?0.0:zList.get(count).getZ();
                Double q = qList.get(count)==null?0.0:qList.get(count).getQ();
                String direction = directionList.get(count)==null?"0":directionList.get(count).getDirection();
                Double speed = speedList.get(count)==null?0.0:speedList.get(count).getSpeed();
                Double pressure = pressureList.get(count)==null?0.0:pressureList.get(count).getPressure();
                Double minimumPressure = minimumPressureList.get(count)==null?0.0:minimumPressureList.get(count).getPressure();
                Double maximumSpeed = maximumSpeedList.get(count)==null?0.0:maximumSpeedList.get(count).getSpeed();
                data +=","+"3.125"+","+z+","+q+","+pressure+","+speed+","+direction+","+minimumPressure+","+maximumSpeed;
                dataList.add(data);
                count++;
            }
            for (int i = 0; i < dataList.size(); i++) {
                String str = dataList.get(i);
                bw.write(str);
                bw.newLine();
            }
            bw.close();
            System.out.println("风暴潮模型边界条件输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("风暴潮模型边界条件输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("风暴潮模型边界条件输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取模型运行输出结果(预报潮位数据)
     * @return
     */
    @Override
    public Object getModelResultCsv(String planId) {
        DecimalFormat df = new DecimalFormat("0.000");

        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        Date endTime = planInfo.getdCaculateendtm();
        //模型路径
        String FBC_MODEL = PropertiesUtil.read("/filePath.properties").getProperty("FBC_MODEL");

        String FBC_MODEL_OUTPUT_RESULT = FBC_MODEL + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator+ planId +"/result.csv";//输出的地址

        //封装结果数据
        List<FbcHdpHhtdzW> dzwList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(FBC_MODEL_OUTPUT_RESULT);
            if (fileReader != null) {
                BufferedReader reader = new BufferedReader(new FileReader(FBC_MODEL_OUTPUT_RESULT));//换成你的文件名
                //跳过第一行数据
                reader.readLine();
                List<String> dataList = new ArrayList<>();
                //读取第二行
                String line = reader.readLine();
                if(line !=null){
                    String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                    dataList.addAll(Arrays.asList(item));
                }
                //封装返回数据
                for (int i = 1; i < dataList.size(); i++) {
                    FbcHdpHhtdzW fbcHdpHhtdzW = new FbcHdpHhtdzW();
                    fbcHdpHhtdzW.setId(StrUtil.getUUID());
                    fbcHdpHhtdzW.setAbsoluteTime(DateUtil.getNextHour(endTime,i));
                    fbcHdpHhtdzW.setRelativeTime((long)i);
                    fbcHdpHhtdzW.setnPlanid(planId);
                    String data = dataList.get(i)==null?"0.0":dataList.get(i);
                    fbcHdpHhtdzW.setTdz(Double.parseDouble(df.format(Double.parseDouble(data))));
                    dzwList.add(fbcHdpHhtdzW);
                }
            }
        } catch (Exception e) {
            System.out.println("解析输出文件result csv失败：" + e.getMessage());
            e.printStackTrace();
        }
        return dzwList;
    }

    @Override
    public Object getModelResultTdz(String planId) {
        return fbcHdpHhtdzWDao.findByNPlanidOrderByAbsoluteTime(planId);
    }


    @Override
    public Paginator getPlanList(PaginatorParam paginatorParam) {

        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_FBC");

        List<Criterion> orders = paginatorParam.getOrders();
        if(orders==null){
            orders = new ArrayList<>();
        }
        Criterion criterion = new Criterion();
        criterion.setFieldName("nCreatetime");
        criterion.setOperator(Criterion.DESC);
        orders.add(criterion);

        List<Criterion> conditions = paginatorParam.getConditions();
        if(conditions==null) {
            conditions = new ArrayList<>();
            paginatorParam.setConditions(conditions);
        }
        Criterion criterion1 = new Criterion();
        criterion1.setFieldName("planSystem");
        criterion1.setOperator(Criterion.EQ);
        criterion1.setValue(planSystem);
        conditions.add(criterion1);
        Paginator<YwkPlaninfo> all = ywkPlaninfoDao.findAll(paginatorParam);
        return all;
    }


    @Override
    public Object getPlanInfoByPlanId(String planId) {
        YwkPlaninfo oneById = ywkPlaninfoDao.findOneById(planId);
        return oneById;
    }

    @Override
    public Object getBoundaryZqByPlanId(String planId) {
        List<FbcBoundaryZ> fbcBoundaryZ = fbcBoundaryZDao.findByNPlanidOrderByAbsoluteTime(planId);
        List<FbcBoundaryQ> fbcBoundaryQ = fbcBoundaryQDao.findByNPlanidOrderByAbsoluteTime(planId);
        List<FbcWindDirection> fbcWindDirection = fbcWindDirectionDao.findByNPlanidOrderByAbsoluteTime(planId);
        List<FbcWindSpeed> fbcWindSpeed = fbcWindSpeedDao.findByNPlanidOrderByAbsoluteTime(planId);
        List<FbcWindPressure> fbcWindPressure = fbcWindPressureDao.findByNPlanidOrderByAbsoluteTime(planId);
        List<FbcWindMinimumPressure> fbcWindMinimumPressure = fbcWindMinimumPressureDao.findByNPlanidOrderByAbsoluteTime(planId);
        List<FbcWindMaximumSpeed> fbcWindMaximumSpeed = fbcWindMaximumSpeedDao.findByNPlanidOrderByAbsoluteTime(planId);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        List<JSONObject> results = new ArrayList<>();
        JSONObject obj1 = new JSONObject();
        obj1.put("boundaryData","boundaryData1");
        List<Map<String,Object>> datas1 = new ArrayList();
        for (FbcBoundaryZ boundaryZ : fbcBoundaryZ){
            Map map1 = new HashMap();
            Double z = boundaryZ.getZ();
            Date absoluteTime = boundaryZ.getAbsoluteTime();
            map1.put("boundaryData",z);
            map1.put("time",format.format(absoluteTime));
            datas1.add(map1);
        }
        obj1.put("dataList",datas1);
        //----------------------------------
        JSONObject obj2 = new JSONObject();
        obj2.put("boundaryData","boundaryData2");
        List<Map<String,Object>> datas2 = new ArrayList();
        for (FbcBoundaryQ boundaryQ : fbcBoundaryQ){
            Map map2 = new HashMap();
            Double q = boundaryQ.getQ();
            Date absoluteTime = boundaryQ.getAbsoluteTime();
            map2.put("boundaryData",q);
            map2.put("time",format.format(absoluteTime));
            datas2.add(map2);
        }
        obj2.put("dataList",datas2);
        //-----------------------------------------
        JSONObject obj3 = new JSONObject();
        obj3.put("boundaryData","boundaryData3");
        List<Map<String,Object>> datas3 = new ArrayList();
        for (FbcWindDirection f : fbcWindDirection){
            Map map3 = new HashMap();
            String direction = f.getDirection();
            Date absoluteTime = f.getAbsoluteTime();
            map3.put("boundaryData",direction);
            map3.put("time",format.format(absoluteTime));
            datas3.add(map3);
        }
        obj3.put("dataList",datas3);
        //------------------------------------
        JSONObject obj4 = new JSONObject();
        obj4.put("boundaryData","boundaryData4");
        List<Map<String,Object>> datas4 = new ArrayList();
        for (FbcWindSpeed f : fbcWindSpeed){
            Map map4 = new HashMap();
            Double speed = f.getSpeed();
            Date absoluteTime = f.getAbsoluteTime();
            map4.put("boundaryData",speed);
            map4.put("time",format.format(absoluteTime));
            datas4.add(map4);
        }
        obj4.put("dataList",datas4);
        //--------------
        JSONObject obj5 = new JSONObject();
        obj5.put("boundaryData","boundaryData5");
        List<Map<String,Object>> datas5 = new ArrayList();
        for (FbcWindPressure f : fbcWindPressure){
            Map map5 = new HashMap();
            Double pressure = f.getPressure();
            Date absoluteTime = f.getAbsoluteTime();
            map5.put("boundaryData",pressure);
            map5.put("time",format.format(absoluteTime));
            datas5.add(map5);
        }
        obj5.put("dataList",datas5);
        //-------------------------------
        JSONObject obj6 = new JSONObject();
        obj6.put("boundaryData","boundaryData6");
        List<Map<String,Object>> datas6 = new ArrayList();
        for (FbcWindMinimumPressure f : fbcWindMinimumPressure){
            Map map6 = new HashMap();
            Double pressure = f.getPressure();
            Date absoluteTime = f.getAbsoluteTime();
            map6.put("boundaryData",pressure);
            map6.put("time",format.format(absoluteTime));
            datas6.add(map6);
        }
        obj6.put("dataList",datas6);

        JSONObject obj7 = new JSONObject();
        obj7.put("boundaryData","boundaryData7");
        List<Map<String,Object>> datas7 = new ArrayList();
        for (FbcWindMaximumSpeed f : fbcWindMaximumSpeed){
            Map map7 = new HashMap();
            Double speed = f.getSpeed();
            Date absoluteTime = f.getAbsoluteTime();
            map7.put("boundaryData",speed);
            map7.put("time",format.format(absoluteTime));
            datas7.add(map7);
        }
        obj7.put("dataList",datas7);
        results.add(obj1);
        results.add(obj2);
        results.add(obj3);
        results.add(obj4);
        results.add(obj5);
        results.add(obj6);
        results.add(obj7);
        return results;
    }

    @Transactional
    @Override
    public void deleteAllInputByPlanId(String planId) {
        fbcBoundaryZDao.deleteByNPlanid(planId);
        fbcBoundaryQDao.deleteByNPlanid(planId);
        fbcWindDirectionDao.deleteByNPlanid(planId);
        fbcWindSpeedDao.deleteByNPlanid(planId);
        fbcWindPressureDao.deleteByNPlanid(planId);
        fbcWindMinimumPressureDao.deleteByNPlanid(planId);
        fbcWindMaximumSpeedDao.deleteByNPlanid(planId);
    }
}
