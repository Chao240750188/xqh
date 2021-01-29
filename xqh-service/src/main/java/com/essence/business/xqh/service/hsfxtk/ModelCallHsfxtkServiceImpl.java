package com.essence.business.xqh.service.hsfxtk;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.hsfxtk.ModelCallHsfxtkService;
import com.essence.business.xqh.api.hsfxtk.dto.*;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.YwkModelDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.dao.hsfxtk.*;
import com.essence.business.xqh.dao.entity.fhybdd.YwkModel;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.hsfxtk.*;
import com.essence.framework.util.StrUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.*;
import java.net.URL;
import java.sql.Timestamp;
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
    /**
     * 根据方案名称校验方案是否存在
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
        if(StrUtil.isEmpty(vo.getnPlanid())){
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
     * @param planId
     * @return
     */
    @Override
    public Integer saveGridProcessToDb(String planId) {

        YwkPlaninfo planinfo = ywkPlaninfoDao.findOne(planId);
        if (planinfo == null){
            System.out.println("计划planid没有找到记录");
            return -1;
        }
        String hsfx_path = PropertiesUtil.read("/filePath.properties").getProperty("HSFX_MODEL");

        String hsfx_model_template_output = hsfx_path +
                File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                + File.separator + planId; //输入的地址
        List<YwkPlanOutputGridProcess> results = analysisOfGridProcessCSV(hsfx_model_template_output,planinfo);
        if (CollectionUtils.isEmpty(results)){
            System.out.println("解析输出文件Grid process csv 没有数据或者失败");
            return -1;
        }
        List<YwkPlanOutputGridProcess> ywkPlanOutputGridProcesses = ywkPlanOutputGridProcessDao.saveAll(results);
        if (results.size() == ywkPlanOutputGridProcesses.size()){
            return 1;
        }else {
            return -1;
        }
    }

    /**
     * 解析输出文件Grid process csv
     * @param hsfx_model_template_output
     * @return
     */
    private List<YwkPlanOutputGridProcess> analysisOfGridProcessCSV(String hsfx_model_template_output,YwkPlaninfo planinfo) {
        List<YwkPlanOutputGridProcess> results = new ArrayList<>();
        Date startTime = planinfo.getdCaculatestarttm();//计算开始时间
        Long step = planinfo.getnOutputtm();
        String grid_process_csv = hsfx_model_template_output+File.separator+"erwei"+File.separator+"process.csv";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(grid_process_csv));//换成你的文件名
            reader.readLine();//第一行信息，为标题信息，不用，如果需要，注释掉
            String line = null;
            List<List<String>> datas = new ArrayList<>();
            while((line=reader.readLine())!=null){
                String item[] = line.split(",");//CSV格式文件为逗号分隔符文件，这里根据逗号切分
                datas.add(Arrays.asList(item));
            }
            for (List<String> data:datas){
                List<String> newList = data.subList(2,data.size());
                for(int i=0;i<newList.size();i++){
                    YwkPlanOutputGridProcess ywkPlanOutputGridProcess = new YwkPlanOutputGridProcess();
                    ywkPlanOutputGridProcess.getPk().setnPlanid(planinfo.getnPlanid());
                    ywkPlanOutputGridProcess.getPk().setGridId(Long.parseLong(data.get(0)));
                    String str = newList.get(i);
                    Long stepNew = step*i;
                    Date newDate = DateUtil.getNextMinute(startTime,stepNew.intValue());
                    ywkPlanOutputGridProcess.setAbsoluteTime(new Timestamp(newDate.getTime()));
                    ywkPlanOutputGridProcess.getPk().setRelativeTime(stepNew);
                    ywkPlanOutputGridProcess.setGridDepth(Double.parseDouble(str));
                    results.add(ywkPlanOutputGridProcess);
                }
            }
        } catch (Exception e) {
            System.out.println("解析输出文件Grid process csv失败："+e.getMessage());
            e.printStackTrace();
        }
        return results;

    }

    /**
     * 解析最大淹没（二维模型淹没统计结果）数据入库
     * @param planId
     * @return
     */
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
                + File.separator + planId+File.separator+"erwei/result.csv";//模型输出最大水深文件路径
        List<String> dataList = null;
        try{
            dataList= readCSV(MODEL_OUTPUT_MAX_FILE_PATH);
        }catch (Exception e){
            System.out.println("读取结果文件失败");
        }
        if(dataList!=null && dataList.size()>0){
            for (int i = 1; i < dataList.size(); i++) {
                //每行每列数据
                String[] dataArray = dataList.get(i).split(",");
                YwkPlanOutputGridMax gridMaxDto = new YwkPlanOutputGridMax();
                list.add(gridMaxDto);
                YwkPlanOutputGridMaxPK pk = new YwkPlanOutputGridMaxPK(planId,Long.parseLong(dataArray[0]));
                gridMaxDto.setIdCLass(pk);
                gridMaxDto.setGridSurfaceElevation(Double.parseDouble(dataArray[1]));//网格表面高程
                gridMaxDto.setMaxWaterDepth(Double.parseDouble(dataArray[2]));//最大水深
                gridMaxDto.setAbsoluteTime(Long.parseLong(dataArray[3]));//最大水深相对时间
                gridMaxDto.setRelativeTime(DateUtil.getNextMinute(startTime,Integer.parseInt(dataArray[3])));//最大水深绝对时间
            }
        }
        if(list.size()>0){
            List<YwkPlanOutputGridMax> returnList = ywkPlanOutputGridMaxDao.saveAll(list);
            return returnList;
        }
        return list;
    }

    /**
     * 读取csv文件内容
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
     * @return
     */
    @Override
    public List<Object> getModelList() {
        List<Object> list = new ArrayList<>();
        List<YwkModel> modelList = ywkModelDao.getYwkModelByModelType("HSFX");
        for(YwkModel ywkModel:modelList){
            list.add(ywkModel);
        }
        return list;
    }

    /**
     * 根据模型获取河道糙率设置参数
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
        for (YwkModelRoughnessParam roughnessParam:modelRoughnessList) {
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
        List<YwkPlaninFloodRoughness> planFloodRoughnessList  = ywkPlaninFloodRoughnessDao.findByPlanId(planId);
        for(YwkPlaninFloodRoughness floodRoughness:planFloodRoughnessList){
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
        List<YwkPlaninRiverRoughness>  planRiverRoughnessList = new ArrayList<>();
        for(YwkRiverRoughnessParam ywkRiverRoughnessParam:ywkRiverRougParamsList){
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
        if(planRiverRoughnessList.size()>0){
            ywkPlaninRiverRoughnessDao.saveAll(planRiverRoughnessList);
        }
        return modelParamVo;
    }

    /**
     * 查询方案边界条件列表数据
     * @param modelParamVo
     * @return
     */
    @Override
    public List<Object> getModelBoundaryBasic(ModelParamVo modelParamVo) {
        List<Object> list = new ArrayList<>();
        //查询模型边界关联表
        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(modelParamVo.getIdmodelId());
        //查询边界详细数据表
        List<String> stcdList = new ArrayList<>();
        stcdList.add(StrUtil.getUUID());
        for (YwkModelBoundaryBasicRl modelboundary:modelBoundaryList) {
            stcdList.add(modelboundary.getStcd());
        }
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByStcdInOrderByStcd(stcdList);
        //封装边界流量数据
        for (YwkBoundaryBasic ywkBoundaryBasic:boundaryBasicList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("boundary",ywkBoundaryBasic);
            jsonObject.put("dataList",new ArrayList<>());
            list.add(jsonObject);
        }
        return list;
    }

    /**
     * 下载边界数据模板
     * @param planId
     * @param modelId
     * @return
     */
    @Override
    public Workbook exportDutyTemplate(String planId,String modelId) {
        //查询模型边界关联表
        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(modelId);
        //查询边界详细数据表
        List<String> stcdList = new ArrayList<>();
        stcdList.add(StrUtil.getUUID());
        for (YwkModelBoundaryBasicRl modelboundary:modelBoundaryList) {
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
            sheet.setColumnWidth(i+1, 4000);
            YwkBoundaryBasic ywkBoundaryBasic = boundaryBasicList.get(i);
            String dataType = "0".equals(ywkBoundaryBasic.getBoundaryDataType())?"水位":"流量";
            XSSFCell cells = row.createCell(i+1);
            cells.setCellStyle(style);
            cells.setCellValue(ywkBoundaryBasic.getBoundarynm()+"("+dataType+")");
        }
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        int beginLine = 1;
        //封装数据
        int count = 1;
        for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime,1)); time = DateUtil.getNextHour(startTime, count)) {
            XSSFRow row1 = sheet.createRow(beginLine);
            row1.createCell(0).setCellValue(DateUtil.dateToStringNormal3(time));
            count++;
            beginLine++;
        }
        return workbook;
    }

    @Override
    public List<Object> importBoundaryData(MultipartFile mutilpartFile,String planId,String modelId) throws IOException {
        List<Object> boundaryDataList = new ArrayList<>();
        //查询模型边界关联表
        List<YwkModelBoundaryBasicRl> modelBoundaryList = ywkModelBoundaryBasicRlDao.findByIdmodelId(modelId);
        //查询边界详细数据表
        List<String> stcdList = new ArrayList<>();
        stcdList.add(StrUtil.getUUID());
        for (YwkModelBoundaryBasicRl modelboundary:modelBoundaryList) {
            stcdList.add(modelboundary.getStcd());
        }
        List<YwkBoundaryBasic> boundaryBasicList = ywkBoundaryBasicDao.findByStcdInOrderByStcd(stcdList);

        //解析ecxel数据 不包含第一行
        List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile,1);
        // 判断有无数据 时间-每个边界的值集合
        Map<String,List<String>> dataMap = new HashMap<>();
        if (excelList != null && excelList.size() > 0) {
            // 遍历每行数据（除了标题）
            for (int i = 0; i < excelList.size(); i++) {
                String[] strings = excelList.get(i);
                if (strings != null && strings.length > 0) {
                    // 封装每列（每个指标项数据）
                    List<String> dataList= new ArrayList<>(Arrays.asList(strings));
                    dataMap.put((strings[0]+"").trim(),dataList.subList(0,dataList.size()));
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
            jsonObject.put("boundary",boundaryBasicList.get(i));
            //封装时间数据
            List<Object> dataList = new ArrayList<>();
            int count = 1;
            for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime,1)); time = DateUtil.getNextHour(startTime, count)) {
                String timeStr = DateUtil.dateToStringNormal3(time);
                List<String> strings = dataMap.get(timeStr);
                JSONObject dataTimeObj = new JSONObject();
                dataTimeObj.put("time",timeStr);
                try{
                    dataTimeObj.put("boundaryData",Double.parseDouble(strings.get(i+1)));
                }catch (Exception e){
                    dataTimeObj.put("boundaryData",0.0);
                }
                dataList.add(dataTimeObj);
                count++;
            }
            jsonObject.put("dataList",dataList);
            boundaryDataList.add(jsonObject);
        }
        return boundaryDataList;
    }

    @Override
    @Transactional
    public List<YwkPlanInfoBoundaryDto> savePlanBoundaryData(List<YwkPlanInfoBoundaryDto> ywkPlanInfoBoundaryDtoList,String planId) {
        //根据方案id删除边界条件信息
        ywkPlaninFloodBoundaryDao.deleteByPlanId(planId);
        //封装新数据
        YwkPlaninfo planinfo = ywkPlaninfoDao.findOneById(planId);
        //输出步长
        Date startTime = planinfo.getdCaculatestarttm();
        Long step = planinfo.getnOutputtm();
        //封装边界条件数据
        List<YwkPlaninFloodBoundary> planBoundaryList = new ArrayList<>();
        for (YwkPlanInfoBoundaryDto ywkPlanInfoBoundaryDto:ywkPlanInfoBoundaryDtoList) {
            YwkBoundaryBasicDto boundary = ywkPlanInfoBoundaryDto.getBoundary();
            List<YwkBoundaryDataDto> dataList = ywkPlanInfoBoundaryDto.getDataList();
            for (YwkBoundaryDataDto ywkBoundaryDataDto:dataList){
                Date time = ywkBoundaryDataDto.getTime();
                YwkPlaninFloodBoundary ywkPlaninFloodBoundary = new YwkPlaninFloodBoundary();
                ywkPlaninFloodBoundary.setId(StrUtil.getUUID());
                ywkPlaninFloodBoundary.setPlanId(planId);
                ywkPlaninFloodBoundary.setStcd(boundary.getStcd());
                ywkPlaninFloodBoundary.setAbsoluteTime(time);
                int i = DateUtil.dValueOfTime(startTime, time);
                ywkPlaninFloodBoundary.setRelativeTime(Long.parseLong(i+""));
                if("0".equals(boundary.getBoundaryDataType())){
                    ywkPlaninFloodBoundary.setZ(ywkBoundaryDataDto.getBoundaryData());
                }else{
                    ywkPlaninFloodBoundary.setQ(ywkBoundaryDataDto.getBoundaryData());
                }
                planBoundaryList.add(ywkPlaninFloodBoundary);
            }
        }
        ywkPlaninFloodBoundaryDao.saveAll(planBoundaryList);
        return ywkPlanInfoBoundaryDtoList;
    }

    @Override
    public List<YwkBreakBasicDto> getBreakList(String modelId) {

        List<YwkBreakBasic> ywkBreakBasics = ywkBreakBasicDao.findsByModelId(modelId);
        List<YwkBreakBasicDto> results = new ArrayList<>();
        for (YwkBreakBasic source : ywkBreakBasics){
            YwkBreakBasicDto target = new YwkBreakBasicDto();
            BeanUtils.copyProperties(source,target);
            results.add(target);
        }
        return results;
    }


    @Override
    public BreakVo savePlanBreak(BreakVo breakDto) {
        YwkPlaninFloodBreak target = new YwkPlaninFloodBreak();
        BeanUtils.copyProperties(breakDto,target);
        target.setId(StrUtil.getUUID());
        ywkPlaninFloodBreakDao.save(target);
        return breakDto;
    }

}
