package com.essence.business.xqh.service.skdd;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.fbc.dto.PlanInfoFbcVo;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.skdd.ModelCallSkddService;
import com.essence.business.xqh.api.skdd.dto.Qdata;
import com.essence.business.xqh.api.skdd.dto.RainData;
import com.essence.business.xqh.api.skdd.dto.RainDataDto;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.*;
import com.essence.business.xqh.dao.entity.fbc.FbcHdpHhtdzW;
import com.essence.business.xqh.dao.entity.fhybdd.*;
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
import java.util.*;
import java.util.stream.Collectors;

/**
 * 水库调度模型业务层
 */
@Service
public class ModelCallSkddServiceImpl implements ModelCallSkddService {

    @Autowired
    private YwkPlaninfoDao ywkPlaninfoDao;
    @Autowired
    private YwkModelDao ywkModelDao;
    @Autowired
    private StStbprpBDao stStbprpBDao;
    @Autowired
    private StPptnRDao stPptnRDao;
    @Autowired
    private YwkPlaninRainfallDao ywkPlaninRainfallDao;
    @Autowired
    private YwkPlanOutputQDao ywkPlanOutputQDao;


    /**
     * 根据方案名称校验方案是否存在
     *
     * @param planName
     */
    @Override
    public Integer getPlanInfoByName(String planName) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_SKDD");

        List<YwkPlaninfo> byCPlanname = ywkPlaninfoDao.findByCPlannameAndPlanSystem(planName, planSystem);
        return byCPlanname.size();
    }

    @Override
    public String savePlanToDb(PlanInfoFbcVo vo) {

        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_SKDD");

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

    @Override
    public List<Object> getRainDataList(ModelParamVo modelParamVo) {
        //先从缓存获取
        List<Object> dataCacheList = (List<Object>) CacheUtil.get("modelSkddRain", modelParamVo.getnPlanid());
        if (dataCacheList != null)
            return dataCacheList;
        //如果缓存没有从数据库获取
        List<Object> list = new ArrayList<>();

        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(modelParamVo.getnPlanid());
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();

        String startTimeStr = DateUtil.dateToStringNormal3(startTime);
        String endTimeStr = DateUtil.dateToStringNormal3(endTime);

        //查询雨量测站及雨量数据
        List<StStbprpB> stationList = stStbprpBDao.findAll();
        List<StStbprpB> sTList = new ArrayList<>();
        for (StStbprpB stStbprpB : stationList) {
            if (stStbprpB.getStcd().contains("ST_")) {
                sTList.add(stStbprpB);
            }
        }
        //查询雨量数据
        List<Map<String, Object>> rainDataList = stPptnRDao.findStPptnRByStartTimeAndEndTime(startTimeStr, endTimeStr);
        Map<String, Double> rainMap = new HashMap<>();
        for (Map<String, Object> map : rainDataList) {
            String tm = map.get("tm") + "";
            String stcd = map.get("STCD") + "";
            String drp = map.get("sum") + "";
            rainMap.put(stcd + "-" + tm, drp == null ? 0.0 : Double.parseDouble(drp));
        }
        //封装边界流量数据
        for (StStbprpB stStbprpB : sTList) {
            String stcd = stStbprpB.getStcd();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("station", stStbprpB);
            List<Object> dataList = new ArrayList<>();
            int count = 0;
            for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
                String timStr = DateUtil.dateToStringNormal3(time);
                JSONObject dataJsonObj = new JSONObject();
                dataJsonObj.put("time", timStr);
                String substring = timStr.substring(0, 13);
                Double drp = rainMap.get((stcd + "-" + substring));
                dataJsonObj.put("drp", drp == null ? 0.0 : drp);
                dataList.add(dataJsonObj);
                count++;
            }
            jsonObject.put("dataList", dataList);
            list.add(jsonObject);
        }
        return list;
    }

    @Override
    public Workbook exportSkddQTemplate(String planId) {
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
        cell.setCellValue("时间");
        XSSFCell cell1 = row.createCell(1);
        cell1.setCellStyle(style);
        cell1.setCellValue("入库流量");
        //设置自动列宽
        sheet.setColumnWidth(0, 5100);
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
    public List<Object> importSkddQData(MultipartFile mutilpartFile, String planId) {
        List<Object> list = new ArrayList<>();
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
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("time",strings[0]);
                    jsonObject.put("q",strings[1]);
                    list.add(jsonObject);
                }
            }
        }
        //存入缓存
        CacheUtil.saveOrUpdate("modelSkddQ", planId, list);
        return list;
    }

    @Override
    public List<Object> getSkddQDataList(ModelParamVo modelParamVo) {
        //先从缓存获取
        List<Object> dataCacheList = (List<Object>) CacheUtil.get("modelSkddQ", modelParamVo.getnPlanid());
        if (dataCacheList != null)
            return dataCacheList;
        List<Object> list = new ArrayList<>();
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(modelParamVo.getnPlanid());
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        //封装数据
        int count = 0;
        for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
            // 封装每列（每个指标项数据）
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("time",DateUtil.dateToStringNormal3(time));
            jsonObject.put("q","0");
            list.add(jsonObject);
            count++;
        }
        return list;
    }

    @Override
    @Transactional
    public List<RainDataDto> saveRainDataList(List<RainDataDto> rainDataLsit,String planId) {
        List<YwkPlaninRainfall> rainfallList = new ArrayList<>();
        for (RainDataDto rainDataDto:rainDataLsit) {
            StStbprpB station = rainDataDto.getStation();
            List<RainData> dataList = rainDataDto.getDataList();
            for (RainData rainData : dataList) {
                YwkPlaninRainfall ywkPlaninRainfall = new YwkPlaninRainfall();
                ywkPlaninRainfall.setcId(StrUtil.getUUID());
                ywkPlaninRainfall.setnPlanid(planId);
                ywkPlaninRainfall.setcStcd(station.getStcd());
                ywkPlaninRainfall.setdTime(rainData.getTime());
                ywkPlaninRainfall.setnDrp(rainData.getDrp());
                rainfallList.add(ywkPlaninRainfall);
            }
        }
        if(rainfallList.size()>0){
            //删除数据
//            ywkPlaninRainfallDao.deleteByNPlanid(planId);
//            ywkPlaninRainfallDao.flush();
            //存入数据
//            ywkPlaninRainfallDao.saveAll(rainfallList);
        }

        return rainDataLsit;
    }

    @Override
    public List<FbcHdpHhtdzW> skddModelCall(String planId) {
        //调用模型计算
        System.out.println("开始调用水库调度模型计算。。。");
        {
            String modelRunPath ="D:\\XQH_SKDD\\MODEL\\startUp.bat";
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
                if (brError.readLine() != null) {
                    System.out.println("水库调度模型调用失败！");
                } else {
                    System.out.println("水库调度模型调用成功！");
                }
            } catch (Exception e) {
                System.out.println("水库调度模型调用失败！");
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
        return null;
    }

    @Override
    @Transactional
    public List<Qdata> saveQDataList(List<Qdata> qDataLsit, String planId) {
        List<YwkPlanOutputQ> qDatallList = new ArrayList<>();

        for (Qdata qdata:qDataLsit) {
            YwkPlanOutputQ ywkPlanOutputQ = new YwkPlanOutputQ();
            ywkPlanOutputQ.setIdcId(StrUtil.getUUID());
            ywkPlanOutputQ.setnPlanid(planId);
            ywkPlanOutputQ.setdTime(qdata.getTime());
            ywkPlanOutputQ.setnQ(qdata.getQ());
            qDatallList.add(ywkPlanOutputQ);
        }
        if(qDatallList.size()>0){
            //删除数据  接口慢需要优化
            ywkPlanOutputQDao.deleteByNPlanid(planId);
            ywkPlanOutputQDao.flush();
            //存入数据
            ywkPlanOutputQDao.saveAll(qDatallList);
        }

        return qDataLsit;
    }


    /**
     * 获取水库调度模型列表
     *
     * @return
     */
    @Override
    public List<YwkModel> getModelInfoList() {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("SWYB_CATCHMENT_AREA");
        return ywkModelDao.getYwkModelByModelType(planSystem);
    }

    /**
     * 分页获取水库调度方案列表
     * @param paginatorParam
     * @return
     */
    @Override
    public Paginator getPlanList(PaginatorParam paginatorParam) {
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_SKDD");
        List<Criterion> orders = paginatorParam.getOrders();
        if(orders==null){
            orders = new ArrayList<>();
        }
        Criterion criterion = new Criterion();
        criterion.setFieldName("nCreatetime");
        criterion.setOperator(Criterion.DESC);
        orders.add(criterion);
        paginatorParam.setOrders(orders);

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

    /**
     * 获取方案雨量列表
     * @param planId
     */
    @Override
    public List<Object> getPlanRainFallList(String planId) {
        List<Object> list = new ArrayList<>();
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();

        String startTimeStr = DateUtil.dateToStringNormal3(startTime);
        String endTimeStr = DateUtil.dateToStringNormal3(endTime);

        //查询雨量测站及雨量数据
        List<StStbprpB> stationList = stStbprpBDao.findAll();
        List<StStbprpB> sTList = new ArrayList<>();
        for (StStbprpB stStbprpB : stationList) {
            if (stStbprpB.getStcd().contains("ST_")) {
                sTList.add(stStbprpB);
            }
        }
        //查询雨量数据
        List<YwkPlaninRainfall> planRainList = ywkPlaninRainfallDao.findByNPlanid("f39e730ec48149cc8e8a94ce00da3c69");
        Map<String, Double> rainMap = new HashMap<>();
        for (YwkPlaninRainfall ywkPlaninRainfall : planRainList) {
            String stcd = ywkPlaninRainfall.getcStcd();
            Date date = ywkPlaninRainfall.getdTime();
            Double drp = ywkPlaninRainfall.getnDrp();
            rainMap.put(stcd + "-" + DateUtil.dateToStringNormal3(date), drp == null ? 0.0 : drp);
        }
        //封装边界流量数据
        for (StStbprpB stStbprpB : sTList) {
            String stcd = stStbprpB.getStcd();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("station", stStbprpB);
            List<Object> dataList = new ArrayList<>();
            int count = 0;
            for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
                String timStr = DateUtil.dateToStringNormal3(time);
                JSONObject dataJsonObj = new JSONObject();
                dataJsonObj.put("time", timStr);
                Double drp = rainMap.get((stcd + "-" + timStr));
                dataJsonObj.put("drp", drp == null ? 0.0 : drp);
                dataList.add(dataJsonObj);
                count++;
            }
            jsonObject.put("dataList", dataList);
            list.add(jsonObject);
        }
        return list;
    }

    /**
     * 获取方案计算入库流量数据
     * @param planId
     * @return
     */
    @Override
    public List<Object> getPlanQList(String planId) {
        List<Object> qList = new ArrayList<>();
        List<YwkPlanOutputQ> byNPlanid = ywkPlanOutputQDao.findByNPlanid(planId);
        for (YwkPlanOutputQ ywkPlanOutputQ:byNPlanid) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("time",DateUtil.dateToStringNormal3(ywkPlanOutputQ.getdTime()));
            jsonObject.put("q",ywkPlanOutputQ.getnQ());
            qList.add(jsonObject);
        }
        return qList;
    }

    @Override
    public Object getPlanResultList(String planId) {
        JSONObject jsonObject = new JSONObject();
        DecimalFormat df = new DecimalFormat("0.000");
        YwkPlaninfo planInfo = ywkPlaninfoDao.findOneById(planId);
        //封装时间列
        Date startTime = planInfo.getdCaculatestarttm();
        Date endTime = planInfo.getdCaculateendtm();
        //出库流量数据
        String SKDD_Q_PATH = "D:\\XQH_SKDD\\MODEL_OUT\\liuliangresult.txt";
        //水位数据
        String SKDD_SW_PATH = "D:\\XQH_SKDD\\MODEL_OUT\\shuiweiresult.txt";
        List<String> qList = getModelResult(SKDD_Q_PATH);
        List<String> swList = getModelResult(SKDD_SW_PATH);
        JSONArray jsonArrayQ = new JSONArray();
        JSONArray jsonArraySW = new JSONArray();
        int count = 0;
        for (Date time = startTime; time.before(DateUtil.getNextMinute(endTime, 1)); time = DateUtil.getNextHour(startTime, count)) {
            String timStr = DateUtil.dateToStringNormal3(time);
            JSONObject dataJsonObjQ = new JSONObject();
            dataJsonObjQ.put("time",timStr);
            try{
                String q = qList.get(count + 1);
                dataJsonObjQ.put("data",q==null?0.0:df.format(Double.parseDouble(q)));
            }catch (Exception E){
                dataJsonObjQ.put("data",0.0);
            }
            jsonArrayQ.add(dataJsonObjQ);

            JSONObject dataJsonObjsw = new JSONObject();
            dataJsonObjsw.put("time",timStr);
            try {
                String sw = swList.get(count + 1);
                dataJsonObjsw.put("data", sw == null ? 0.0 : df.format(Double.parseDouble(sw)));
            }catch (Exception e){
                dataJsonObjsw.put("data", 0.0);
            }
            jsonArraySW.add(dataJsonObjsw);
            count++;
        }
        jsonObject.put("qDataList",jsonArrayQ);
        jsonObject.put("swDataList",jsonArraySW);
        return jsonObject;
    }

    @Override
    public YwkPlaninfo getPlanInfo(String planId) {
        return ywkPlaninfoDao.findOneById(planId);
    }


    /**
     * 解析模型输出结果文件成出库流量数据
     * @return
     */
    private List<String> getModelResult(String path) {
        List<String> list = new ArrayList<>();
        /* 读取数据 */
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(path)), "UTF-8"));
            String lineTxt = br.readLine();
           if(lineTxt!=null){
               List<String> split = Arrays.asList(lineTxt.split("\t"));
               return split;
           }
        } catch (Exception e) {
            System.err.println("水库模型调用结果读取失败:read errors :" + e);
            return list;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
