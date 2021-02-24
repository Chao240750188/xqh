package com.essence.business.xqh.service.hsfxtk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.hsfxtk.ModelCallHsfxtkService;
import com.essence.business.xqh.api.hsfxtk.dto.*;
import com.essence.business.xqh.api.modelResult.PlanProcessDataService;
import com.essence.business.xqh.common.util.*;
import com.essence.business.xqh.dao.dao.fhybdd.YwkModelDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.dao.hsfxtk.*;
import com.essence.business.xqh.dao.entity.fhybdd.YwkModel;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.hsfxtk.*;
import com.essence.framework.util.StrUtil;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ModelCallHsfxtkServiceImpl implements ModelCallHsfxtkService {

    @Autowired
    private YwkPlaninfoDao ywkPlaninfoDao;
    @Autowired
    YwkPlanOutputGridProcessDao ywkPlanOutputGridProcessDao;
    @Autowired
    private YwkPlanOutputGridMaxDao ywkPlanOutputGridMaxDao;
    @Autowired
    private YwkModelDao ywkModelDao;
    @Autowired
    private YwkModelRoughnessParamDao ywkModelRoughnessParamDao;
    @Autowired
    private YwkRiverRoughnessParamDao ywkRiverRoughnessParamDao;
    @Autowired
    private YwkPlaninFloodRoughnessDao ywkPlaninFloodRoughnessDao;
    @Autowired
    private YwkPlaninRiverRoughnessDao ywkPlaninRiverRoughnessDao;
    @Autowired
    private YwkModelBoundaryBasicRlDao ywkModelBoundaryBasicRlDao;
    @Autowired
    private YwkBoundaryBasicDao ywkBoundaryBasicDao;
    @Autowired
    private YwkPlaninFloodBoundaryDao ywkPlaninFloodBoundaryDao;
    @Autowired
    YwkBreakBasicDao ywkBreakBasicDao;//溃口基本信息表

    @Autowired
    YwkPlaninFloodBreakDao ywkPlaninFloodBreakDao;//溃口方案表

    @Autowired
    YwkFloodChannelBasicDao ywkFloodChannelBasicDao;//分洪道基本信息表

    @Autowired
    YwkFloodChannelFlowDao ywkFloodChannelFlowDao;//分洪道数据表

    @Autowired
    PlanProcessDataService planProcessDataService;//模型结果解析

    /**
     * 根据方案名称校验方案是否存在
     *
     * @param planName
     */
    @Override
    public Integer getPlanInfoByName(String planName) {
        List<YwkPlaninfo> byCPlanname = ywkPlaninfoDao.findByCPlanname(planName);
        return byCPlanname.size();
    }


    /**
     * 方案计创建入库
     *
     * @param vo
     * @return
     */
    @Override
    public String savePlanToDb(PlanInfoHsfxtkVo vo) {

        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_HSFX");

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
        ywkPlaninfo.setnModelid(vo.getModelId());
        ywkPlaninfo.setdRainstarttime(startTime);
        ywkPlaninfo.setdRainendtime(endTIme);
        ywkPlaninfo.setdOpensourcestarttime(startTime);
        ywkPlaninfo.setdOpensourceendtime(endTIme);
        ywkPlaninfo.setnCreatetime(DateUtil.getCurrentTime());
        YwkPlaninfo saveDbo = ywkPlaninfoDao.save(ywkPlaninfo);
        return saveDbo.getnPlanid();
    }

    /**
     * 保存网格执行过程入库
     *
     * @param planId
     * @return
     */
    @Override
    @Transactional
    public Integer saveGridProcessToDb(String planId) {

        YwkPlaninfo planinfo = ywkPlaninfoDao.findOne(planId);
        if (planinfo == null) {
            System.out.println("计划planid没有找到记录");
            return -1;
        }
        String hsfx_path = PropertiesUtil.read("/filePath.properties").getProperty("HSFX_MODEL");

        String hsfx_model_template_output = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId; //输入的地址
        List<YwkPlanOutputGridProcess> results = analysisOfGridProcessCSV(hsfx_model_template_output, planinfo);
        if (CollectionUtils.isEmpty(results)) {
            System.out.println("解析输出文件Grid process csv 没有数据或者失败");
            return -1;
        }
        List<YwkPlanOutputGridProcess> ywkPlanOutputGridProcesses = ywkPlanOutputGridProcessDao.saveAll(results);
        if (results.size() == ywkPlanOutputGridProcesses.size()) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * 解析输出文件Grid process csv
     *
     * @param hsfx_model_template_output
     * @return
     */
    private List<YwkPlanOutputGridProcess> analysisOfGridProcessCSV(String hsfx_model_template_output, YwkPlaninfo planinfo) {
        List<YwkPlanOutputGridProcess> results = new ArrayList<>();
        Date startTime = planinfo.getdCaculatestarttm();//计算开始时间
        Long step = planinfo.getnOutputtm();
        String grid_process_csv = hsfx_model_template_output + File.separator + "erwei" + File.separator + "process.csv";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(grid_process_csv));//换成你的文件名
            reader.readLine();//第一行信息，为标题信息，不用，如果需要，注释掉
            String line = null;
            List<List<String>> datas = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                datas.add(Arrays.asList(item));
            }
            for (List<String> data : datas) {
                List<String> newList = data.subList(2, data.size());
                for (int i = 0; i < newList.size(); i++) {
                    YwkPlanOutputGridProcess ywkPlanOutputGridProcess = new YwkPlanOutputGridProcess();
                    ywkPlanOutputGridProcess.getPk().setnPlanid(planinfo.getnPlanid());
                    ywkPlanOutputGridProcess.getPk().setGridId(Long.parseLong(data.get(0)));
                    String str = newList.get(i);
                    Long stepNew = step * i;
                    Date newDate = DateUtil.getNextMinute(startTime, stepNew.intValue());
                    ywkPlanOutputGridProcess.setAbsoluteTime(new Timestamp(newDate.getTime()));
                    ywkPlanOutputGridProcess.getPk().setRelativeTime(stepNew);
                    ywkPlanOutputGridProcess.setGridDepth(Double.parseDouble(str));
                    results.add(ywkPlanOutputGridProcess);
                }
            }
        } catch (Exception e) {
            System.out.println("解析输出文件Grid process csv失败：" + e.getMessage());
            e.printStackTrace();
        }
        return results;

    }

    /**
     * 解析最大淹没（二维模型淹没统计结果）数据入库
     *
     * @param planId
     * @return
     */
    @Transactional
    public List<YwkPlanOutputGridMax> saveGridMaxToDb(String planId) throws FileNotFoundException {
        List<YwkPlanOutputGridMax> list = new ArrayList<>();
        //查询方案基本信息
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        Long step = planInfo.getnOutputtm();
        //读取最大水深文件
        //输出参数文件
        String SWYB_PATH = PropertiesUtil.read("/filePath.properties").getProperty("HSFX_MODEL");
        //解析二维输出最大水深文件
        String MODEL_OUTPUT_MAX_FILE_PATH = SWYB_PATH + File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId + File.separator + "erwei/result.csv";//模型输出最大水深文件路径
        List<String> dataList = null;
        try {
            dataList = readCSV(MODEL_OUTPUT_MAX_FILE_PATH);
        } catch (Exception e) {
            System.out.println("读取结果文件失败");
        }
        if (dataList != null && dataList.size() > 0) {
            for (int i = 1; i < dataList.size(); i++) {
                //每行每列数据
                String[] dataArray = dataList.get(i).split(",");
                YwkPlanOutputGridMax gridMaxDto = new YwkPlanOutputGridMax();
                list.add(gridMaxDto);
                YwkPlanOutputGridMaxPK pk = new YwkPlanOutputGridMaxPK(planId, Long.parseLong(dataArray[0]));
                gridMaxDto.setIdCLass(pk);
                gridMaxDto.setGridSurfaceElevation(Double.parseDouble(dataArray[1]));//网格表面高程
                gridMaxDto.setMaxWaterDepth(Double.parseDouble(dataArray[2]));//最大水深
                gridMaxDto.setAbsoluteTime(Long.parseLong(dataArray[3]));//最大水深相对时间
                gridMaxDto.setRelativeTime(DateUtil.getNextMinute(startTime, Integer.parseInt(dataArray[3])));//最大水深绝对时间
            }
        }
        if (list.size() > 0) {
            List<YwkPlanOutputGridMax> returnList = ywkPlanOutputGridMaxDao.saveAll(list);
            return returnList;
        }
        return list;
    }

    /**
     * 读取csv文件内容
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    private List<String> readCSV(String filePath) throws IOException {
        List dataList = new ArrayList();
        BufferedReader br = null;
        InputStreamReader isr = null;
        try {
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                isr = new InputStreamReader(new FileInputStream(file), "utf-8");
                br = new BufferedReader(isr);
                String lineTxt = null;
                while ((lineTxt = br.readLine()) != null) {
                    dataList.add(lineTxt);
                }
                return dataList;
            } else {
                System.out.println("文件不存在");
            }
        } catch (Exception e) {
            System.out.println("文件错误");
        } finally {
            if (br != null) {
                br.close();
            }
            if (isr != null) {
                isr.close();
            }
        }
        return dataList;
    }

    @Override
    public void test(String planId) throws Exception {
        saveGridProcessToDb(planId);
    }

    /**
     * 防洪保护区设置获取模型列表
     *
     * @return
     */
    @Override
    public List<Object> getModelList() {
        List<Object> list = new ArrayList<>();
        List<YwkModel> modelList = ywkModelDao.getYwkModelByModelType("HSFX");
        for (YwkModel ywkModel : modelList) {
            list.add(ywkModel);
        }
        return list;
    }

    /**
     * 根据模型获取河道糙率设置参数
     *
     * @param modelId
     * @return
     */
    @Override
    public List<Object> getModelRiverRoughness(String modelId) {
        List<Object> list = new ArrayList<>();
        //根据模型id获取模型糙率设置
        List<YwkModelRoughnessParam> modelRoughnessList = ywkModelRoughnessParamDao.findByIdmodelId(modelId);
        //查询糙率参数
        List<YwkRiverRoughnessParam> paramList = ywkRiverRoughnessParamDao.findAll();
        Map<String, List<YwkRiverRoughnessParam>> paramMap = paramList.stream().collect(Collectors.groupingBy(YwkRiverRoughnessParam::getRoughnessParamid));
        //封装参数
        for (YwkModelRoughnessParam roughnessParam : modelRoughnessList) {
            roughnessParam.setParamList(paramMap.get(roughnessParam.getRoughnessParamid()));
            list.add(roughnessParam);
        }
        return list;
    }

    @Override
    @Transactional
    public ModelParamVo saveModelRiverRoughness(ModelParamVo modelParamVo) {
        //修改方案计算模型
        String planId = modelParamVo.getnPlanid();
        YwkPlaninfo ywkPlaninfo = ywkPlaninfoDao.findOneById(planId);
        ywkPlaninfo.setnModelid(modelParamVo.getIdmodelId());
        ywkPlaninfoDao.save(ywkPlaninfo);
        //保存方案计算模型糙率参数
        //先删除再新增
        List<YwkPlaninFloodRoughness> planFloodRoughnessList = ywkPlaninFloodRoughnessDao.findByPlanId(planId);
        for (YwkPlaninFloodRoughness floodRoughness : planFloodRoughnessList) {
            ywkPlaninRiverRoughnessDao.deleteByPlanRoughnessId(floodRoughness.getPlanRoughnessid());
        }
        ywkPlaninFloodRoughnessDao.deleteByPlanId(planId);
        //插入最新设定数据
        //查询模板数据
        YwkModelRoughnessParam modelRoughness = ywkModelRoughnessParamDao.findOneById(modelParamVo.getRoughnessParamid());
        //方案模型糙率
        YwkPlaninFloodRoughness ywkPlaninFloodRoughness = new YwkPlaninFloodRoughness();
        String ywkPlaninFloodRoughnessId = StrUtil.getUUID();
        ywkPlaninFloodRoughness.setPlanRoughnessid(ywkPlaninFloodRoughnessId);
        ywkPlaninFloodRoughness.setPlanId(planId);
        ywkPlaninFloodRoughness.setRoughnessParamnm(modelRoughness.getRoughnessParamnm());
        ywkPlaninFloodRoughness.setRoughnessParamid(modelRoughness.getRoughnessParamid());
        ywkPlaninFloodRoughness.setGridSynthesizeRoughness(modelRoughness.getGridSynthesizeRoughness());
        //方案河道糙率
        List<YwkRiverRoughnessParam> ywkRiverRougParamsList = ywkRiverRoughnessParamDao.findByRoughnessParamid(modelRoughness.getRoughnessParamid());
        List<YwkPlaninRiverRoughness> planRiverRoughnessList = new ArrayList<>();
        for (YwkRiverRoughnessParam ywkRiverRoughnessParam : ywkRiverRougParamsList) {
            YwkPlaninRiverRoughness ywkPlaninRiverRoughness = new YwkPlaninRiverRoughness();
            ywkPlaninRiverRoughness.setId(StrUtil.getUUID());
            ywkPlaninRiverRoughness.setPlanRoughnessId(ywkPlaninFloodRoughnessId);
            ywkPlaninRiverRoughness.setRoughness(ywkRiverRoughnessParam.getRoughness());
            ywkPlaninRiverRoughness.setMileage(ywkRiverRoughnessParam.getMileage());
            ywkPlaninRiverRoughness.setIsFix(ywkRiverRoughnessParam.getIsFix());
            planRiverRoughnessList.add(ywkPlaninRiverRoughness);
        }
        //保存方案模型糙率
        ywkPlaninFloodRoughnessDao.save(ywkPlaninFloodRoughness);
        //保存方案河道糙率
        if (planRiverRoughnessList.size() > 0) {
            ywkPlaninRiverRoughnessDao.saveAll(planRiverRoughnessList);
        }
        return modelParamVo;
    }

    /**
     * 查询方案边界条件列表数据
     *
     * @param modelParamVo
     * @return
     */
    @Override
    public List<Object> getModelBoundaryBasic(ModelParamVo modelParamVo) {
        //先从缓存获取
        List<Object> dataCacheList = (List<Object>) CacheUtil.get("modelBoundaryData", modelParamVo.getnPlanid());
        if (dataCacheList != null)
            return dataCacheList;
        //如果缓存没有从数据库获取
        List<Object> list = new ArrayList<>();
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(modelParamVo.getnPlanid());
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        //查询模型边界关联表
        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(modelParamVo.getIdmodelId());
        //查询边界详细数据表
        List<String> stcdList = new ArrayList<>();
        stcdList.add(StrUtil.getUUID());
        for (YwkModelBoundaryBasicRl modelboundary : modelBoundaryList) {
            stcdList.add(modelboundary.getStcd());
        }
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByStcdInOrderByStcd(stcdList);

        //封装边界流量数据
        for (YwkBoundaryBasic ywkBoundaryBasic : boundaryBasicList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("boundary", ywkBoundaryBasic);
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

    /**
     * 下载边界数据模板
     *
     * @param planId
     * @param modelId
     * @return
     */
    @Override
    public Workbook exportDutyTemplate(String planId, String modelId) {
        //查询模型边界关联表
        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(modelId);
        //查询边界详细数据表
        List<String> stcdList = new ArrayList<>();
        stcdList.add(StrUtil.getUUID());
        for (YwkModelBoundaryBasicRl modelboundary : modelBoundaryList) {
            stcdList.add(modelboundary.getStcd());
        }
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByStcdInOrderByStcd(stcdList);
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

        for (int i = 0; i < boundaryBasicList.size(); i++) {
            sheet.setColumnWidth(i + 1, 4000);
            YwkBoundaryBasic ywkBoundaryBasic = boundaryBasicList.get(i);
            String dataType = "0".equals(ywkBoundaryBasic.getBoundaryDataType()) ? "水位" : "流量";
            XSSFCell cells = row.createCell(i + 1);
            cells.setCellStyle(style);
            cells.setCellValue(ywkBoundaryBasic.getBoundarynm() + "(" + dataType + ")");
        }
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

    @Override
    public List<Object> importBoundaryData(MultipartFile mutilpartFile, String planId, String modelId) throws IOException {
        List<Object> boundaryDataList = new ArrayList<>();
        //查询模型边界关联表
        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(modelId);
        //查询边界详细数据表
        List<String> stcdList = new ArrayList<>();
        stcdList.add(StrUtil.getUUID());
        for (YwkModelBoundaryBasicRl modelboundary : modelBoundaryList) {
            stcdList.add(modelboundary.getStcd());
        }
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByStcdInOrderByStcd(stcdList);

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
        for (int i = 0; i < boundaryBasicList.size(); i++) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("boundary", boundaryBasicList.get(i));
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
        CacheUtil.saveOrUpdate("modelBoundaryData", planId, boundaryDataList);

        return boundaryDataList;
    }

    @Override
    @Transactional
    public List<YwkPlanInfoBoundaryDto> savePlanBoundaryData(List<YwkPlanInfoBoundaryDto> ywkPlanInfoBoundaryDtoList, String planId) {
        //根据方案id删除边界条件信息
        ywkPlaninFloodBoundaryDao.deleteByPlanId(planId);
        //封装新数据
        YwkPlaninfo planinfo = ywkPlaninfoDao.findOneById(planId);
        //输出步长
        Date startTime = planinfo.getdCaculatestarttm();
        Long step = planinfo.getnOutputtm();
        //封装边界条件数据
        List<YwkPlaninFloodBoundary> planBoundaryList = new ArrayList<>();
        for (YwkPlanInfoBoundaryDto ywkPlanInfoBoundaryDto : ywkPlanInfoBoundaryDtoList) {
            YwkBoundaryBasicDto boundary = ywkPlanInfoBoundaryDto.getBoundary();
            List<YwkBoundaryDataDto> dataList = ywkPlanInfoBoundaryDto.getDataList();
            for (YwkBoundaryDataDto ywkBoundaryDataDto : dataList) {
                Date time = ywkBoundaryDataDto.getTime();
                YwkPlaninFloodBoundary ywkPlaninFloodBoundary = new YwkPlaninFloodBoundary();
                ywkPlaninFloodBoundary.setId(StrUtil.getUUID());
                ywkPlaninFloodBoundary.setPlanId(planId);
                ywkPlaninFloodBoundary.setStcd(boundary.getStcd());
                ywkPlaninFloodBoundary.setAbsoluteTime(time);
                int i = DateUtil.dValueOfTime(startTime, time);
                ywkPlaninFloodBoundary.setRelativeTime(Long.parseLong(i + ""));
                if ("0".equals(boundary.getBoundaryDataType())) {
                    ywkPlaninFloodBoundary.setZ(ywkBoundaryDataDto.getBoundaryData());
                } else {
                    ywkPlaninFloodBoundary.setQ(ywkBoundaryDataDto.getBoundaryData());
                }
                planBoundaryList.add(ywkPlaninFloodBoundary);
            }
        }
        ywkPlaninFloodBoundaryDao.saveAll(planBoundaryList);
        //更新缓存
        //存入缓存
        CacheUtil.saveOrUpdate("modelBoundaryData", planId, ywkPlanInfoBoundaryDtoList);

        return ywkPlanInfoBoundaryDtoList;
    }

    @Override
    public List<YwkBreakBasicDto> getBreakList(String modelId) {

        List<YwkBreakBasic> ywkBreakBasics = ywkBreakBasicDao.findsByModelId(modelId);
        List<YwkBreakBasicDto> results = new ArrayList<>();
        for (YwkBreakBasic source : ywkBreakBasics) {
            YwkBreakBasicDto target = new YwkBreakBasicDto();
            BeanUtils.copyProperties(source, target);
            results.add(target);
        }
        return results;
    }


    @Override
    @Transactional
    public BreakVo savePlanBreak(BreakVo breakDto) {
        //根据方案id删除旧数据
        ywkPlaninFloodBreakDao.deleteByNPlanid(breakDto.getnPlanid());
        //保存记录
        YwkPlaninFloodBreak target = new YwkPlaninFloodBreak();
        BeanUtils.copyProperties(breakDto, target);
        target.setId(StrUtil.getUUID());
        ywkPlaninFloodBreakDao.save(target);
        return breakDto;
    }


    @Override
    public void callMode(String planId) {
        //调用模型计算
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOne(planId);
        if (planInfo == null) {
            System.out.println("计划planid没有找到记录");
            return;
        }
        String modelid = planInfo.getnModelid();

        String hsfx_path = PropertiesUtil.read("/filePath.properties").getProperty("HSFX_MODEL");

        String hsfx_model_template_output = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId; //输出的地址


        String hsfx_model_template = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE");//默认文件对位置

        if ("MODEL_HSFX_01".equals(modelid)){
            hsfx_model_template = hsfx_model_template + File.separator + "FHBHQ1";
        }else if ("MODEL_HSFX_02".equals(modelid)){
            hsfx_model_template = hsfx_model_template + File.separator + "FHBHQ2";
        }else {
            System.out.println("水动力模型的模型id值不对");
            return;
        }

        String hsfx_model_template_input = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_TEMPLATE")
                + File.separator + "INPUT" + File.separator + planId; //输入的地址

        String hsfx_model_template_run = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_RUN");

        String hsfx_model_template_run_plan = hsfx_model_template_run + File.separator + planId;
        //List<YwkPlanOutputGridProcess> results = analysisOfGridProcessCSV(hsfx_model_template_output,planinfo);

        File inputyiweiPath = new File(hsfx_model_template_input + File.separator + "yiwei");
        File inputerweiPath = new File(hsfx_model_template_input + File.separator + "erwei");

        File outyiweiPath = new File(hsfx_model_template_output + File.separator + "yiwei");
        File outerweiPath = new File(hsfx_model_template_output + File.separator + "erwei");
        File runPath = new File(hsfx_model_template_run_plan);
        inputyiweiPath.mkdirs();
        inputerweiPath.mkdirs();
        outyiweiPath.mkdirs();
        outerweiPath.mkdirs();
        runPath.mkdirs();

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        String startTimeStr = format1.format(startTime);
        String endTimeStr = format1.format(endTime);


        //获取模型边界基本信息表对所有数据
        List<Map<String, Object>> BndDatas = new ArrayList<>();//表头13个的里程
        List<String> BndList = new ArrayList<>();//点位数据
        List<Map<String, Object>> breakList = new ArrayList<>();//洪道开始结束俩个点的里程
        Map<String, List<Map<String, Object>>> channels = new HashMap<>();//点位数据
        getBndCsvChanelDatas(breakList, channels, planId);//获取分洪道数据
        getBndCsvBoundaryDatas(BndDatas, BndList, planInfo, breakList, channels);//获取边界数据

        //写入边界条件成功
        int result0 = writeDataToInputBNDCsv(hsfx_model_template_input, BndDatas, BndList);
        if (result0 == 0) {
            System.out.println("水动力模型计算:边界BND.csv输入文件写入成功。。。");
            return;
        }

        List<YwkPlaninRiverRoughness> ctrCsvDatas = getCTRCsvDatas(planId);
        int result1 = writeDataToInputCTRCsv(hsfx_model_template, hsfx_model_template_input, ctrCsvDatas, planInfo, BndList.size());

        if (result1 == 0) {
            System.out.println("水动力模型计算:溃口CTR.csv输入文件写入失败。。。");
            return;
        }
        int result2 = writeDataToInputBDCsv(hsfx_model_template, hsfx_model_template_input, planInfo, BndList.size());

        if (result2 == 0) {
            System.out.println("水动力模型计算:溃口通道BD.csv输入文件写入失败。。。");
            return;
        }

        int result3 = writeDataToInputWGCsv(hsfx_model_template, hsfx_model_template_input, planInfo);

        if (result3 == 0) {
            System.out.println("水动力模型计算:糙率WG.csv输入文件写入失败。。。");
            return;
        }

        int result4 = copyOtherCsv(hsfx_model_template, hsfx_model_template_input);
        if (result4 == 0) {
            System.out.println("水动力模型计算:复制其他.csv输入文件写入失败。。。");
            return;
        }
        int result5 = copyExeFile(hsfx_model_template_run, hsfx_model_template_run_plan);
        if (result5 == 0) {
            System.out.println("水动力模型计算:复制执行文件与config文件写入失败。。。");
            return;
        }

        int result6 = writeDataToConfig(hsfx_model_template_run_plan, hsfx_model_template_input, hsfx_model_template_output);
        if (result6 == 0) {
            System.out.println("水动力模型计算:config文件写入失败。。。");
            return;
        }
        //调用模型计算
        System.out.println("水动力模型计算:开始水动力模型计算。。。");
        System.out.println("水动力模型计算路径为。。。"+hsfx_model_template_run_plan + File.separator + "startUp.bat");
        runModelExe(hsfx_model_template_run_plan + File.separator + "startUp.bat");
        System.out.println("水动力模型计算:水动力模型计算结束。。。");

        //判断是否执行成功，是否有error文件
        String errorStr = hsfx_model_template_output + File.separator + "error.txt";
        File errorFile = new File(errorStr);
        if (errorFile.exists()) {//存在表示执行失败
            planInfo.setnPlanstatus(-1L);
        } else {
            planInfo.setnPlanstatus(2L);
        }
        ywkPlaninfoDao.save(planInfo);

        //解析模型结果调用GIS服务-生成图片 -存在表示执行失败
        if (!errorFile.exists()) {
            try {
                //解析淹没过程文件数据入库
               // saveGridProcessToDb(planId);
                //解析最大水深文件数据入库
                //saveGridMaxToDb(planId);

                //如果模型运行成功-解析过程文件生成图片
                planProcessDataService.readDepthCsvFile(hsfx_model_template_output, "process", planInfo.getnModelid(), planId);
                //解析最大水深文件
                planProcessDataService.readDepthCsvFile(hsfx_model_template_output, "maxDepth", planInfo.getnModelid(), planId);
            } catch (Exception e) {
                System.out.println("模型结果解析失败！");
            }

        }


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
            // while ((line = br.readLine()) != null || (line = brError.readLine()) != null)
            // {
            while ((line = brError.readLine()) != null) {
                // 输出exe输出的信息以及错误信息
                System.out.println(line);
            }
            if (brError.readLine() != null){
                System.out.println("水动力调度模型调用失败！");
            }else {
                System.out.println("水动力调度模型调用成功！");
            }
        } catch (Exception e) {
            System.out.println("水动力调度模型调用失败！");
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

    private int copyExeFile(String hsfx_model_template_run, String hsfx_model_template_run_plan) {

        String exeUrl = hsfx_model_template_run + File.separator + "HSFXYB_MODEL.exe";
        String exeInputUrl = hsfx_model_template_run_plan + File.separator + "HSFXYB_MODEL.exe";
        String batUrl = hsfx_model_template_run + File.separator + "startUp.bat";
        String batInputUrl = hsfx_model_template_run_plan + File.separator + "startUp.bat";
        try {
            FileUtil.copyFile(exeUrl, exeInputUrl, true);
            FileUtil.copyFile(batUrl, batInputUrl, true);
            System.err.println("水动力模型计算：copy执行文件exe,bat文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水动力模型计算：copy执行文件exe,bat文件错误" + e.getMessage());
            return 0;
        }
    }

    private int writeDataToConfig(String hsfx_model_template_run_plan, String hsfx_model_template_input, String hsfx_model_template_output) {


        List<String> finals = new ArrayList<>();

        //String configUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh1_Guojia_50的副本"+File.separator+"config.txt";

        String configUrl = hsfx_model_template_run_plan + File.separator + "config.txt";

        String erweiInputBDUrl = "BD&&" + hsfx_model_template_input + File.separator + "erwei" + File.separator + "BD.csv";
        String erweiInputINUrl = "IN&&" + hsfx_model_template_input + File.separator + "erwei" + File.separator + "IN.csv";
        String erweiInputWGUrl = "WG&&" + hsfx_model_template_input + File.separator + "erwei" + File.separator + "WG.csv";
        String erweiInputTDUrl = "TD&&" + hsfx_model_template_input + File.separator + "erwei" + File.separator + "TD.csv";
        String erweiInputJDUrl = "JD&&" + hsfx_model_template_input + File.separator + "erwei" + File.separator + "JD.csv";
        String erweiInputResultUrl = "result&&" + hsfx_model_template_output + File.separator + "erwei" + File.separator + "result.csv";//输出
        String erweiInputProcessUrl = "process&&" + hsfx_model_template_output + File.separator + "erwei" + File.separator + "process.csv";//输出
        String erweiInputOverflowUrl = "overflow&&" + hsfx_model_template_output + File.separator + "erwei" + File.separator + "overflow.csv";//输出
        String erweiInputKuikouUrl = "kuikou&&" + hsfx_model_template_output + File.separator + "erwei" + File.separator + "kuikou.csv";//输出
        String yiweiInputBNDUrl = "BND&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "BND.csv";
        String yiweiInputINIUrl = "INI&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "INI.csv";
        String yiweiInputSECUrl = "SEC&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "SEC.csv";
        String yiweiInputCTRUrl = "CTR&&" + hsfx_model_template_input + File.separator + "yiwei" + File.separator + "CTR.csv";
        String yiweiInputDischargeUrl = "Discharge&&" + hsfx_model_template_output + File.separator + "yiwei" + File.separator + "Discharge.csv";//输出
        String yiweiInputWaterlevelUrl = "Waterlevel&&" + hsfx_model_template_output + File.separator + "yiwei" + File.separator + "Waterlevel.csv";//输出
        String yiweiInputWaterdepthUrl = "Waterdepth&&" + hsfx_model_template_output + File.separator + "yiwei" + File.separator + "Waterdepth.csv";//输出
        String JinduUrl = "jindu&&" + hsfx_model_template_output + File.separator + "jindu.txt";//输出
        String errorUrl = "error&&" + hsfx_model_template_output + File.separator + "error.txt";//输出

        finals.add(erweiInputBDUrl);
        finals.add(erweiInputINUrl);
        finals.add(erweiInputWGUrl);
        finals.add(erweiInputTDUrl);
        finals.add(erweiInputJDUrl);
        finals.add(erweiInputResultUrl);
        finals.add(erweiInputProcessUrl);
        finals.add(erweiInputOverflowUrl);
        finals.add(erweiInputKuikouUrl);
        finals.add(yiweiInputBNDUrl);
        finals.add(yiweiInputINIUrl);
        finals.add(yiweiInputSECUrl);
        finals.add(yiweiInputCTRUrl);
        finals.add(yiweiInputDischargeUrl);
        finals.add(yiweiInputWaterlevelUrl);
        finals.add(yiweiInputWaterdepthUrl);
        finals.add(JinduUrl);
        finals.add(errorUrl);
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
            System.out.println("写入水动力模型config失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("写入水动力模型config失败");
            e.printStackTrace();
            return 0;
        }

    }

    private int copyOtherCsv(String hsfx_model_template, String hsfx_model_template_input) {
        String INIUrl = hsfx_model_template + File.separator + "yiwei" + File.separator + "INI.csv";
        String INIInputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "INI.csv";

        String SecUrl = hsfx_model_template + File.separator + "yiwei" + File.separator + "SEC.csv";
        String SecInputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "SEC.csv";

        String shujuUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "数据.xls";
        String shujuInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "数据.xls";

        String InUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "IN.csv";
        String InInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "IN.csv";

        String JDUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "JD.csv";
        String JDInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "JD.csv";

        String TDUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "TD.csv";
        String TDInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "TD.csv";

        try {
            FileUtil.copyFile(INIUrl, INIInputUrl, true); //一维的
            FileUtil.copyFile(SecUrl, SecInputUrl, true); //一维的
            FileUtil.copyFile(shujuUrl, shujuInputUrl, true); //二维的
            FileUtil.copyFile(InUrl, InInputUrl, true); //二维的
            FileUtil.copyFile(JDUrl, JDInputUrl, true); //二维的
            FileUtil.copyFile(TDUrl, TDInputUrl, true); //二维的
            System.err.println("水动力模型计算：copy输入文件成功");
            return 1;
        } catch (Exception e) {
            System.err.println("水动力模型计算：copy输入文件错误" + e.getMessage());
            return 0;
        }

    }

    private int writeDataToInputWGCsv(String hsfx_model_template, String hsfx_model_template_input, YwkPlaninfo planInfo) {

        List<YwkPlaninFloodRoughness> byPlanId = ywkPlaninFloodRoughnessDao.findByPlanId(planInfo.getnPlanid());
        //String WGInputUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh1_Guojia_50的副本"+File.separator+"WG.csv";
        String WGInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "WG.csv";
        //String WGInputReadUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh2_Xinhecun_50/erwei" + File.separator+"WG.csv";
        String WGInputReadUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "WG.csv";

        List<List<String>> readDatas = new ArrayList();
        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(WGInputReadUrl)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                List<String> split = Arrays.asList(lineTxt.split(","));
                readDatas.add(split);
            }
        } catch (Exception e) {
            System.err.println("水动力模型计算：WG.csv输入文件读取错误:read errors :" + e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //BufferedWriter bw = new BufferedWriter(new FileWriter(WGInputUrl, false)); // 附加
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(WGInputUrl, false), "UTF-8"));
            for (int i = 1; i < readDatas.size(); i++) {
                readDatas.get(i).set(4, byPlanId.get(0).getGridSynthesizeRoughness() + "");
            }
            for (int i = 0; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                String line = "";
                for (String s : strings) {
                    line = line + s + ",";
                }
                line = line.substring(0, line.length() - 1);
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("水动力模型计算：WG.csv输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水动力模型计算：WG.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("水动力模型计算：WG.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    private int writeDataToInputBDCsv(String hsfx_model_template, String hsfx_model_template_input, YwkPlaninfo planInfo, int size) {

        //获取溃口入参数据
        YwkPlaninFloodBreak floodBreak = ywkPlaninFloodBreakDao.findByNPlanid(planInfo.getnPlanid());
        //溃口基本信息表
        YwkBreakBasic breakBasic = ywkBreakBasicDao.findById(floodBreak.getBreakId()).get();

        //String BDInputUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh1_Guojia_50的副本"+File.separator+"BD.csv";
        String BDInputUrl = hsfx_model_template_input + File.separator + "erwei" + File.separator + "BD.csv";
        //String BDInputReadUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh2_Xinhecun_50/erwei" + File.separator+"BD.csv";
        String BDInputReadUrl = hsfx_model_template + File.separator + "erwei" + File.separator + "BD.csv";

        List<List<String>> readDatas = new ArrayList();
        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(BDInputReadUrl)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                List<String> split = Arrays.asList(lineTxt.split(","));
                readDatas.add(split);
            }
        } catch (Exception e) {
            System.err.println("水动力模型计算：BD.csv输入文件读取错误:read errors :" + e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //BufferedWriter bw = new BufferedWriter(new FileWriter(BDInputUrl, false)); // 附加
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(BDInputUrl, false), "UTF-8"));
            readDatas.get(1).set(3, breakBasic.getBreakNo() + "");//设置溃口通道编号
            readDatas.get(3).set(3, size + "");//设置计算结束时间
            for (int i = 0; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                int sizeList = strings.size();
                if (sizeList < 4) {//TODO 写死4列 不能多不能少
                    List<String> newList = new ArrayList<>();
                    newList.addAll(strings);
                    for (int j = sizeList; j < 4; j++) {
                        newList.add("");
                    }
                    readDatas.set(i, newList);
                }
            }

            for (int i = 0; i < readDatas.size(); i++) {

                List<String> strings = readDatas.get(i);
                String line = "";
                for (String s : strings) {
                    line = line + s + ",";
                }
                line = line.substring(0, line.length() - 1);
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("水动力模型计算：BD.csv输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水动力模型计算：BD.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("水动力模型计算：BD.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }

    }

    private List<YwkPlaninRiverRoughness> getCTRCsvDatas(String planId) {
        List<YwkPlaninFloodRoughness> byPlanId = ywkPlaninFloodRoughnessDao.findByPlanId(planId);
        YwkPlaninFloodRoughness ywkPlaninFloodRoughness = byPlanId.get(0);
        //查出结果集
        List<YwkPlaninRiverRoughness> byPlanRoughnessId = ywkPlaninRiverRoughnessDao.findByPlanRoughnessIdOrderByMileageAsc(ywkPlaninFloodRoughness.getPlanRoughnessid());

        return byPlanRoughnessId;
    }

    private int writeDataToInputCTRCsv(String hsfx_model_template, String hsfx_model_template_input, List<YwkPlaninRiverRoughness> ctrCsvDatas, YwkPlaninfo planInfo, int size) {

        String CTRInputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "CTR.csv";
        //String CTRInputUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh1_Guojia_50的副本"+File.separator+"CTR.csv";
        String CTRInputReadUrl = hsfx_model_template + File.separator + "yiwei" + File.separator + "CTR.csv";
        //String CTRInputReadUrl = "/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh2_Xinhecun_50/yiwei" + File.separator+"CTR.csv";

        List<List<String>> readDatas = new ArrayList();
        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(CTRInputReadUrl)), "UTF-8"));
            String lineTxt = null;
            while ((lineTxt = br.readLine()) != null) {
                List<String> split = Arrays.asList(lineTxt.split(","));
                readDatas.add(split);
            }
        } catch (Exception e) {
            System.err.println("水动力模型计算：CTR.csv输入文件读取错误:read errors :" + e);
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            //获取溃口入参数据
            YwkPlaninFloodBreak floodBreak = ywkPlaninFloodBreakDao.findByNPlanid(planInfo.getnPlanid());
            //溃口基本信息表
            YwkBreakBasic breakBasic = ywkBreakBasicDao.findById(floodBreak.getBreakId()).get();

            //BufferedWriter bw = new BufferedWriter(new FileWriter(CTRInputUrl, false)); // 附加
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CTRInputUrl, false), "UTF-8"));
            //数据小于等于excel的个数
            if (ctrCsvDatas.size() <= readDatas.size() - 1) {//9个数据，7个excel 1个表头 TODO 这个if已经测试了 但是else没有测试呢

                for (int i = 0; i < ctrCsvDatas.size(); i++) {
                    List<String> strings = readDatas.get(i + 1);
                    YwkPlaninRiverRoughness ywkPlaninRiverRoughness = ctrCsvDatas.get(i);//数据是从0开始
                    strings.set(2, ywkPlaninRiverRoughness.getMileage() + "");
                    strings.set(3, ywkPlaninRiverRoughness.getRoughness() + "");
                }
                for (int i = 1; i < readDatas.size(); i++) {
                    List<String> strings = readDatas.get(i);
                    if (i == 2) {
                        strings.set(1, size - 1 + "");//模拟时间
                    } else if (i == 4) {
                        strings.set(1, planInfo.getnOutputtm() + "");//输出时间步长
                    } else if (i == 8) { //溃口里程
                        strings.set(1, breakBasic.getBreakMileage() + "");
                    } else if (i == 9) {//溃口底高程
                        strings.set(1, floodBreak.getBreakBottomElevation() + "");
                    } else if (i == 10) {//溃口宽度
                        strings.set(1, floodBreak.getBreakWidth() + "");
                    } else if (i == 12) {//起溃水位
                        strings.set(1, floodBreak.getStartZ() + "");
                    }
                }
                //还要去除原来的csv里面的里程数据
                for (int i = ctrCsvDatas.size() + 1; i < readDatas.size(); i++) {
                    List<String> strings = readDatas.get(i);
                    if (strings.size() == 4) {
                        strings.set(2, "");
                        strings.set(3, "");
                    }
                }
            } else {//数据大于excel的个数
                for (int i = 1; i < readDatas.size(); i++) {
                    YwkPlaninRiverRoughness ywkPlaninRiverRoughness = ctrCsvDatas.get(i - 1);//数据是从0开始
                    List<String> strings = readDatas.get(i);
                    strings.set(2, ywkPlaninRiverRoughness.getMileage() + "");
                    strings.set(3, ywkPlaninRiverRoughness.getRoughness() + "");
                    if (i == 2) {
                        strings.set(1, size - 1 + "");//模拟时间 比数据的个数少1
                    } else if (i == 4) {
                        strings.set(1, planInfo.getnOutputtm() + "");//输出时间步长
                    } else if (i == 8) { //溃口里程
                        strings.set(1, breakBasic.getBreakMileage() + "");
                    } else if (i == 9) {//溃口底高程
                        strings.set(1, floodBreak.getBreakBottomElevation() + "");
                    } else if (i == 10) {//溃口宽度
                        strings.set(1, floodBreak.getBreakWidth() + "");
                    } else if (i == 12) {//起溃水位
                        strings.set(1, floodBreak.getStartZ() + "");
                    }
                }
                for (int i = readDatas.size() - 1; i < ctrCsvDatas.size(); i++) {
                    List list = new ArrayList();
                    list.add("");
                    list.add("");
                    YwkPlaninRiverRoughness ywkPlaninRiverRoughness = ctrCsvDatas.get(i);//数据是从0开始
                    list.set(2, ywkPlaninRiverRoughness.getMileage() + "");
                    list.set(3, ywkPlaninRiverRoughness.getRoughness() + "");
                    readDatas.add(list);
                }
            }//组装完数据

            for (int i = 0; i < readDatas.size(); i++) {
                List<String> strings = readDatas.get(i);
                String line = "";
                for (String s : strings) {
                    line = line + s + ",";
                }
                line = line.substring(0, line.length() - 1);
                bw.write(line);
                bw.newLine();
            }
            bw.close();
            System.out.println("水动力模型计算：溃口CTR.csv输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水动力模型计算：溃口CTR.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("水动力模型计算：溃口CTR.csv输入文件写入失败");
            e.printStackTrace();
            return 0;
        }


    }

    private void getBndCsvChanelDatas(List<Map<String, Object>> breakList, Map<String, List<Map<String, Object>>> channels, String planId) {

        //一个计划id关联俩个，入点跟出点。一个入点对应数据有多条
        YwkPlaninFloodBreak floodBreak = ywkPlaninFloodBreakDao.findByNPlanid(planId);

        //通过breakId 查出起始点跟结束点数据，1是入流，-1是出流。里程
        List<YwkFloodChannelBasic> byBreakIdOrderByOutflowAndInflowType = ywkFloodChannelBasicDao.findByBreakIdOrderByOutflowAndInflowTypeDesc(floodBreak.getBreakId());
        Map<String, Object> entranceMap = new HashMap();
        entranceMap.put("stcd", "entrance");
        entranceMap.put("mileage", byBreakIdOrderByOutflowAndInflowType.get(0).getMileage());
        Map<String, Object> exportMap = new HashMap<>();
        exportMap.put("stcd", "export");
        exportMap.put("mileage", byBreakIdOrderByOutflowAndInflowType.get(1).getMileage());
        breakList.add(entranceMap);
        breakList.add(exportMap);

        List<String> breakIds = byBreakIdOrderByOutflowAndInflowType.stream().map(YwkFloodChannelBasic::getFloodChannelId).collect(Collectors.toList());
        //通过起始点跟结束点查某个区间范围点数据
        List<YwkFloodChannelFlow> byChannelBasicIds = ywkFloodChannelFlowDao.findByChannelBasicIds(breakIds);
        //按照channelBasicIds分组
        Map<String, List<YwkFloodChannelFlow>> channelCollect = byChannelBasicIds.stream().collect(Collectors.groupingBy(YwkFloodChannelFlow::getFloodChannelId));
        List<Map<String, Object>> entrance = poToListMap(channelCollect.get(byBreakIdOrderByOutflowAndInflowType.get(0).getFloodChannelId()));
        List<Map<String, Object>> export = poToListMap(channelCollect.get(byBreakIdOrderByOutflowAndInflowType.get(1).getFloodChannelId()));
        channels.put("entrance", entrance);
        channels.put("export", export);
    }

    public <T> List<Map<String, Object>> poToListMap(List<T> source) {
        List<Map<String, Object>> target = new ArrayList<>();
        if (CollectionUtils.isEmpty(source)) {
            return target;
        }
        for (Object s : source) {
            Map map = JSON.parseObject(JSON.toJSONString(s), Map.class);
            target.add(map);
        }
        return target;
    }

    private void getBndCsvBoundaryDatas(List<Map<String, Object>> bndDatas, List<String> bndList, YwkPlaninfo planInfo, List<Map<String, Object>> breakIds, Map<String, List<Map<String, Object>>> channels) {

        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(planInfo.getnModelid());//基本信息中间表
        List<String> collectStcd = modelBoundaryList.stream().map(YwkModelBoundaryBasicRl::getStcd).collect(Collectors.toList());//中间表的stcd集合
        //上游、下游，根据stcd集合获取上游下游的列表
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByStcdInOrderByBoundaryType(collectStcd);
        Map upperMap = new HashMap(); //上游
        upperMap.put("stcd", boundaryBasicList.get(0).getStcd());
        upperMap.put("mileage", 1);
        Map downMap = new HashMap();//下游
        downMap.put("stcd", boundaryBasicList.get(1).getStcd());
        downMap.put("mileage", 0);
        bndDatas.add(upperMap);
        bndDatas.add(downMap);
        //截取非上下游的其他数据
        List<YwkBoundaryBasic> newBoundaryBasicList = new ArrayList(boundaryBasicList.subList(2, boundaryBasicList.size()));
        List<Map<String, Object>> newBoundaryBasics = poToListMap(newBoundaryBasicList);

        //在这个地方把溃口堆进来
        newBoundaryBasics.addAll(breakIds);
        //按照历程排序从小到大
        Collections.sort(newBoundaryBasics, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Double mileage1 = Double.parseDouble(o1.get("mileage") + "");
                Double mileage2 = Double.parseDouble(o2.get("mileage") + "");
                return mileage1.compareTo(mileage2);//降序
            }
        });

        bndDatas.addAll(newBoundaryBasics);

        //TODO 获取里程的入参信息
        List<YwkPlaninFloodBoundary> byPlanIds = ywkPlaninFloodBoundaryDao.findByPlanId(planInfo.getnPlanid());
        //按照stcd分组。
        Map<String, List<YwkPlaninFloodBoundary>> groupCollect = byPlanIds.stream().collect(Collectors.groupingBy(YwkPlaninFloodBoundary::getStcd));
        for (Map.Entry<String, List<YwkPlaninFloodBoundary>> entry : groupCollect.entrySet()) {
            List<YwkPlaninFloodBoundary> value = entry.getValue();
            for (int i = 0; i < value.size(); i++) {
                bndList.add(""); //初始化list
            }
            break;
        }
        Map<String, List<Map<String, Object>>> planinFloodBoundaryMap = new HashMap<>();
        for (Map.Entry<String, List<YwkPlaninFloodBoundary>> entry : groupCollect.entrySet()) {
            String key = entry.getKey();
            List<YwkPlaninFloodBoundary> value = entry.getValue();
            List<Map<String, Object>> list = poToListMap(value);
            planinFloodBoundaryMap.put(key, list);
        }
        planinFloodBoundaryMap.putAll(channels);
        for (int i = 0; i < bndDatas.size(); i++) {
            Map<String, Object> map = bndDatas.get(i);
            String stcd = map.get("stcd") + "";//下游水文，其他流量
            //TODO 这个地方个数必须正确
            List<Map<String, Object>> listMap = planinFloodBoundaryMap.get(stcd);
            //按照时间排序
            Collections.sort(listMap, new Comparator<Map<String, Object>>() {
                @Override
                public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                    Long relativeTime = Long.parseLong(o1.get("relativeTime") + "");
                    Long relativeTime1 = Long.parseLong(o2.get("relativeTime") + "");
                    return relativeTime.compareTo(relativeTime1);
                }
            });//TODO 这个地方目前是根据上游点时间点个数写点
            int size = planinFloodBoundaryMap.get(bndDatas.get(1).get("stcd")).size();
            if (listMap.size() < size) {
                for (int z = 0; z < size - listMap.size(); z++) {
                    Map<String, Object> m = new HashMap<>();
                    m.put("q", listMap.get(listMap.size() - 1).get("q"));
                    listMap.add(m);
                }
            } else {
                listMap = listMap.subList(0, size);
            }
            //TODO 这个地方如果是24个点的话，不够24个的以最后一个点的值补够24个点

            for (int j = 0; j < listMap.size(); j++) {
                Map<String, Object> boundryMap = listMap.get(j);
                String s = bndList.get(j);
                Double q;
                if (i == 1) {///下游水文，其他流量
                    q = Double.parseDouble(boundryMap.get("z") + "");
                } else {
                    q = Double.parseDouble(boundryMap.get("q") + "");
                }
                if (StringUtils.isEmpty(s)) {// bw.write("\"time\"" + "," + "\"pcp\"");
                    s = j + "," + q + "";
                } else {
                    s = s + "," + j + "," + q;
                }
                bndList.set(j, s);
            }
        }
    }

    private int writeDataToInputBNDCsv(String hsfx_model_template_input, List<Map<String, Object>> datas, List<String> list) {
        String BndInputUrl = hsfx_model_template_input + File.separator + "yiwei" + File.separator + "BND.csv";

        try {
            //BufferedWriter bw = new BufferedWriter(new FileWriter(BndInputUrl, false)); // 附加
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(BndInputUrl, false), "UTF-8"));

            //BufferedWriter bw = new BufferedWriter(new FileWriter("/Users/xiongchao/小清河/洪水风险调控/yierwei0128提交版/database/Xqh1_Guojia_50的副本/yiwei/BND.csv", false)); // 附加
            // 添加新的数据行

            String head = "\"上边界条件\"" + "," + "\"\"" + ",";
            head = head + "\"下边界条件\"" + "," + "\"\"";

            Map<String, Object> map1 = datas.get(0);
            Map<String, Object> map2 = datas.get(1);
            int size = list.size();
            String flood_boundary = size + "," + map1.get("mileage") + "," + size + "," + map2.get("mileage");
            for (int i = 2; i < datas.size(); i++) {
                Map map = datas.get(i);
                String stcd = map.get("stcd") + "";
                if ("entrance".equals(stcd)) {
                    head = head + "," + "\"分洪道入流\"" + "," + "\"\"";
                } else if ("export".equals(stcd)) {
                    head = head + "," + "\"分洪道出流\"" + "," + "\"\"";
                } else {
                    head = head + "," + "\"侧向集中入流条件\"" + "," + "\"\"";
                }
                flood_boundary = flood_boundary + "," + size + "," + datas.get(i).get("mileage");
            }
            bw.write(head);
            bw.newLine();
            bw.write(flood_boundary);
            bw.newLine();
            for (int i = 0; i < list.size(); i++) {
                String str = list.get(i);
                bw.write(str);
                bw.newLine();
            }
            bw.close();
            System.out.println("水动力模型边界条件输入文件写入成功");
            return 1;
        } catch (FileNotFoundException e) {
            // File对象的创建过程中的异常捕获
            System.out.println("水动力模型边界条件输入文件写入失败");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // BufferedWriter在关闭对象捕捉异常
            System.out.println("水动力模型边界条件输入文件写入失败");
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取方案模型计算进度
     *
     * @param planId
     * @return
     */
    @Override
    public Object getHsfxModelRunStatus(String planId) {
        JSONObject jsonObject = new JSONObject();
        //运行进度
        jsonObject.put("process", 0.0);
        //运行状态 1运行结束 0运行中
        jsonObject.put("runStatus", 1);
        //运行时间
        jsonObject.put("time", 0);
        //描述
        jsonObject.put("describ", "模型运行出现异常！");

        String hsfx_path = PropertiesUtil.read("/filePath.properties").getProperty("HSFX_MODEL");
        String hsfx_model_template_output = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId; //输出的地址
        //判断是否有error文件
        String errorPath = hsfx_model_template_output + File.separator + "error.txt";
        String processPath = hsfx_model_template_output + File.separator + "jindu.txt";
        String picPath = hsfx_model_template_output + File.separator + "pic.txt";
        File picFile = new File(picPath);
        File errorFile = new File(errorPath);
        //存在表示执行失败
        if (errorFile.exists()) {
            return jsonObject;
        }
        File jinduFile = new File(processPath);
        if (!jinduFile.exists()) {
            //运行进度
            jsonObject.put("process", 0.0);
            //运行状态 1运行结束 0运行中
            jsonObject.put("runStatus", 0);
            //运行时间
            jsonObject.put("time", 0);
            jsonObject.put("describ", "模型运行准备中！");
            return jsonObject;
        } else {
            //运行状态 1运行结束 0运行中
            jsonObject.put("runStatus", 0);
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(processPath))));
                String lineTxt = br.readLine();
                if (lineTxt != null) {
                    String[] split = lineTxt.split("&&");
                    //运行时间
                    jsonObject.put("time", Double.parseDouble(split[1] + ""));
                }
                String lineTxt2 = br.readLine();
                if (lineTxt2 != null) {
                    String[] split = lineTxt2.split("&&");
                    //运行进度
                    double process = Double.parseDouble(split[1] + "");
                    jsonObject.put("process", process * 0.94);
                    if (process == 100.0) {
                        jsonObject.put("describ", "水深过程渲染效果图生成中！");
                    } else {
                        jsonObject.put("describ", "模型运行中！");
                    }
                    if (picFile.exists()) {
                        jsonObject.put("process", 100.0);
                        jsonObject.put("runStatus", 1);
                        jsonObject.put("describ", "模型运行结束！");
                    }
                }
                return jsonObject;
            } catch (Exception e) {
                System.err.println("进度读取错误！" + e.getMessage());
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return jsonObject;
    }

    /**
     * 模型输出淹没历程-及最大水深图片列表
     *
     * @param planId
     * @return
     */
    @Override
    public Object getModelProcessPicList(String planId) {
        List<String> processList = new ArrayList<>();
        //方案基本信息
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        if (planInfo != null) {
            Date startTime = planInfo.getdCaculatestarttm();
            Date endTime = planInfo.getdCaculateendtm();
            Long aLong = planInfo.getnOutputtm();
            int count = 1;
            for (Date time = startTime; time.before(endTime); time = DateUtil.getNextHour(startTime, count)) {
                count++;
            }
            for (int i = 0; i < count; i++) {
                processList.add((i + 1) + "");
            }
        }
        return processList;
    }

    @Override
    public void previewPicFile(HttpServletRequest request, HttpServletResponse response, String planId, String picId) {
        YwkPlaninfo planinfo = ywkPlaninfoDao.findOneById(planId);
        //图片路径
        String outputAbsolutePath = GisPathConfigurationUtil.getOutputPictureAbsolutePath() + "/" + planinfo.getnModelid() + "/" + planId;
        //图片路径
        String processOutputAbsolutePath = outputAbsolutePath + "/process/";
        String filePath = null;
        if("maxDepth".equals(picId)){
             filePath = outputAbsolutePath + "/maxDepth.png";
        }else{
             filePath = processOutputAbsolutePath + picId + ".png";
        }
        try {
            File file = new File(filePath);
            if (file != null && file.exists()) {
                int length = Integer.MAX_VALUE;
                if (file.length() < length) {
                    length = (int) file.length();
                }
                response.setContentLength(length);
                String fileName = file.getName();
                FileUtil.openFilebBreakpoint(request, response, file, fileName);
            }
        } catch (Exception e) {
        }
    }

}
