package com.essence.business.xqh.service.floodForecast;

import com.alibaba.druid.util.StringUtils;
import com.essence.business.xqh.api.floodForecast.dto.*;
import com.essence.business.xqh.api.floodForecast.service.SqybModelPlanInfoService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.floodForecast.*;
import com.essence.business.xqh.dao.dao.tuoying.TuoyingStRsvrRDao;
import com.essence.business.xqh.dao.dao.tuoying.dto.HtStRsvrRViewDto;
import com.essence.business.xqh.dao.entity.floodForecast.*;
import com.essence.framework.jpa.Criterion;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import com.essence.framework.util.DateUtil;
import com.essence.framework.util.FileUtil;
import com.essence.framework.util.StrUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SqybModelPlanInfoServiceImpl implements SqybModelPlanInfoService {

	@Autowired
	private SqybModelInputRainfallDao modelInputRainfallDao;
	@Autowired
	private SqybModelPlanInfoDao modelPlanInfoDao;
	@Autowired
	private SqybModelOutPutRainfallDao modelOutPutRainfallDao;
	@Autowired
	private SqybStStbprpBDao stStbprpBDao;
	@Autowired
    private SqybModelInputEvaporationDao modelInputEvaporationDao;
	@Autowired
	private SqybModelLoopRunDao modelLoopRunDao;
	@Autowired
	SqybRelStResDao relStResDao;
	@Autowired
	SqybStPptnHrDao stPptnHrDao;
	@Autowired
	SqybResDao resDao;
	@Autowired
	TuoyingStRsvrRDao stRsvrRDao;
	@Autowired
	SqybHifZvarlBDao hifZvarlBDao;
	@Autowired
	private SqybModelInfoDao modelInfoDao;


	@Override
	public SqybModelPlanInfoDto saveModelPlanInfo(SqybModelPlanInfoDto modelPlanInfoDto) {
		SqybModelPlanInfo modelPlanInfo = new SqybModelPlanInfo();
		BeanUtils.copyProperties(modelPlanInfoDto,modelPlanInfo);
		if (StringUtils.isEmpty(modelPlanInfo.getPlanId())) {
			modelPlanInfo.setPlanId(StrUtil.getUUID());
		}
		if (modelPlanInfo.getPlanForeseeTotalRain() == null){
		    modelPlanInfo.setPlanForeseeTotalRain(new BigDecimal(0));
        }
		if (modelPlanInfo.getAutoRunSign() == null){
			modelPlanInfo.setAutoRunSign(0); //滚动计算标志（0=手动计算，1=滚动计算）
		}
		modelPlanInfo.setPlanStatusInfo("未开始计算");
		modelPlanInfo.setPlanStatus(0);
		// 动态修改手工上传文件的模板文件提供下载导入
		if ("1".equals(modelPlanInfo.getDataType())) {
			try {
				updateExcelTemplate(modelPlanInfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		SqybModelPlanInfo save = modelPlanInfoDao.save(modelPlanInfo);
		BeanUtils.copyProperties(save,modelPlanInfoDto);
		String modelName = modelInfoDao.findById(modelPlanInfo.getModelId()).get().getModelName();
		modelPlanInfoDto.setModelName(modelName);
		String resName = resDao.findById(modelPlanInfo.getResCode()).get().getResName();
		modelPlanInfoDto.setResName(resName);
		return modelPlanInfoDto;
	}

	private void updateExcelTemplate(SqybModelPlanInfo modelPlanInfo) {
		// 定义一个数据格式化对象
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(
					new FileInputStream(PropertiesUtil.read("/filePath.properties").getProperty("modelRainTemPath")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		Sheet sheet = wb.getSheetAt(0);
		// 封装时间数据
		Date startTime = modelPlanInfo.getStartTime();
		Date endTime = modelPlanInfo.getEndTime();
		int rowCount = 1;
		for (Date i = startTime; i.before(DateUtil.getNextHour(endTime, 1)); i = DateUtil.getNextHour(i, 1)) {
			Row row = sheet.createRow(rowCount);
			row.setHeightInPoints(28);
			row.createCell(0).setCellValue(DateUtil.dateToStringNormal(i));
			rowCount++;
		}
		try {
			OutputStream out = new FileOutputStream(
					PropertiesUtil.read("/filePath.properties").getProperty("modelRainTemPath"));
			wb.write(out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public SqybModelPlanInfoDto findByPlanId(String planId) {
		SqybModelPlanInfoDto modelPlanInfoDto = null;
		SqybModelPlanInfo modelPlanInfo = modelPlanInfoDao.findByPlanId(planId);
		if (null!=modelPlanInfo){
			modelPlanInfoDto = new SqybModelPlanInfoDto();
			BeanUtils.copyProperties(modelPlanInfo,modelPlanInfoDto);
		}
		String modelName = modelInfoDao.findById(modelPlanInfo.getModelId()).get().getModelName();
		modelPlanInfoDto.setModelName(modelName);
		String resName = resDao.findById(modelPlanInfo.getResCode()).get().getResName();
		modelPlanInfoDto.setResName(resName);
		return modelPlanInfoDto;
	}

	@Override
	public Paginator<SqybModelPlanInfoDto> getPlanInfoListPage(PaginatorParam param) {
		List<Criterion> orders = param.getOrders();
		if(orders==null)
			orders = new ArrayList<>();
		Criterion criterion = new Criterion();
		criterion.setFieldName("modiTime");
		criterion.setOperator(Criterion.DESC);
		orders.add(criterion);
		param.setOrders(orders);
		Paginator<SqybModelPlanInfo> all = modelPlanInfoDao.findAll(param);
		List<SqybModelPlanInfoDto> modelPlanInfoDtoList = new ArrayList<>();
		Paginator<SqybModelPlanInfoDto> paginator = new Paginator<>(param.getCurrentPage(), param.getPageSize());
		BeanUtils.copyProperties(all,paginator);
		List<SqybModelPlanInfo> items = all.getItems();
		if (items.size()>0){
			List<SqybModelInfo> modelInfoList = modelInfoDao.findAll();
			Map<String, String> modelInfoMap = modelInfoList.stream().collect(Collectors.toMap(SqybModelInfo::getModelId, SqybModelInfo::getModelName));
			List<SqybRes> resList = resDao.findAll();
			Map<String, String> resInfoMap = resList.stream().collect(Collectors.toMap(SqybRes::getResCode, SqybRes::getResName));

			for (int i=0;i<items.size();i++){
				SqybModelPlanInfo modelPlanInfo = items.get(i);
				SqybModelPlanInfoDto modelPlanInfoDto = new SqybModelPlanInfoDto();
				BeanUtils.copyProperties(modelPlanInfo,modelPlanInfoDto);
				String modelName = modelInfoMap.get(modelPlanInfoDto.getModelId());
				modelPlanInfoDto.setModelName(modelName);
				String resName = resInfoMap.get(modelPlanInfoDto.getResCode());
				modelPlanInfoDto.setResName(resName);
				modelPlanInfoDtoList.add(modelPlanInfoDto);
			}
		}
		paginator.setItems(modelPlanInfoDtoList);
		return paginator;
	}

	@Override
	@Transactional
	public Object deletePlanInfo(String planId) {
		// 删除方案计算输入条件
		modelInputRainfallDao.deleteByPlanId(planId);
		// 删除模型输出条件
		modelOutPutRainfallDao.deleteByPlanId(planId);
		// 删除方案
		modelPlanInfoDao.deleteByPlanId(planId);
		// 删除方案相关文件
		// 删除模型需要参数txt文件
		String modelParamPath = PropertiesUtil.read("/filePath.properties").getProperty("modelParamFile") + "\\"
				+ planId + ".txt";
		File paramFile = new File(modelParamPath);
		FileUtil.deleteFile(paramFile);
		// 删除输出文件
		// 输出文件夹配置
		String SCS_MODEL_OUT = PropertiesUtil.read("/filePath.properties").getProperty("modelScSOutPath") + "/"
				+ planId;
		File modelOutFile = new File(SCS_MODEL_OUT);
		FileUtil.deleteFile(modelOutFile);
		return planId;
	}

	@Override
	public List<RainFallSumDto> getPlanInPutRain(String planId) {
		List<RainFallSumDto> rainFallSumDtoList = new ArrayList<RainFallSumDto>();
		// 根据方案查询输入降雨条件数据
		List<SqybModelInPutRainfall> findByPlanId = modelInputRainfallDao.findByPlanIdOrderByTm(planId);
		Map<String, Double> stcdSumMap = new HashMap<String, Double>();
		Map<String, List<RainFallTime>> rainFallTimeListMap = new HashMap<>();
		for (SqybModelInPutRainfall modelInPutRainfall : findByPlanId) {
			// 测站
			String stcd = modelInPutRainfall.getStcd();
			// 雨量
			Double p = modelInPutRainfall.getP();
			// 时间
			Date tm = modelInPutRainfall.getTm();
			// 求和
			Double sum = stcdSumMap.get(stcd);
			if (sum == null)
				sum = 0.0;
			stcdSumMap.put(stcd, sum + p);
			// 封装数据
			List<RainFallTime> list = rainFallTimeListMap.get(stcd);
			if (list == null)
				list = new ArrayList<>();
			RainFallTime rainFallTime = new RainFallTime();
			rainFallTime.setDrp(p);
			rainFallTime.setTime(tm);
			list.add(rainFallTime);
			rainFallTimeListMap.put(stcd, list);
		}
		// 封装返回对象
		for (Map.Entry<String, List<RainFallTime>> entry : rainFallTimeListMap.entrySet()) {
			RainFallSumDto rainFallSumDto = new RainFallSumDto();
			rainFallSumDto.setSumDrp(stcdSumMap.get(entry.getKey()));
			SqybStStbprpBDto stStbprpBDto=null;
			SqybStStbprpB stStbprpB = stStbprpBDao.findById(entry.getKey()).get();
			if (null!=stStbprpB){
				stStbprpBDto=new SqybStStbprpBDto();
				BeanUtils.copyProperties(stStbprpB,stStbprpBDto);
			}
			rainFallSumDto.setStation(stStbprpBDto);
			rainFallSumDto.setRainList(entry.getValue());
			rainFallSumDtoList.add(rainFallSumDto);
		}
		return rainFallSumDtoList;
	}

    /**
     * LiuGt add at 2020-03-19
     * 查询方案蒸发条件
     * @param planId
     * @return
     */
    @Override
    public List<EvaporationSumDto> getPlanInPutEvapor(String planId){
        List<EvaporationSumDto> evaporationSumDtoList = new ArrayList<>();
        // 根据方案查询输入蒸发条件数据
        List<SqybModelInputEvaporation> findByPlanId = modelInputEvaporationDao.findByPlanIdOrderByTm(planId);
        Map<String, Double> stcdSumMap = new HashMap<>();
        Map<String, List<EvaporationTimeDto>> evaporTimeDtoListMap = new HashMap<>();
        for (SqybModelInputEvaporation modelInPutRainfall : findByPlanId) {
            // 测站
            String stcd = modelInPutRainfall.getStcd();
            // 蒸发量
            Double e = modelInPutRainfall.getE();
            // 时间
            LocalDateTime tm = modelInPutRainfall.getTm();
            // 求和
            Double sum = stcdSumMap.get(stcd);
            if (sum == null)
                sum = 0.0;
            stcdSumMap.put(stcd, sum + e);
            // 封装数据
            List<EvaporationTimeDto> list = evaporTimeDtoListMap.get(stcd);
            if (list == null)
                list = new ArrayList<>();
            EvaporationTimeDto evaporationTimeDto = new EvaporationTimeDto();
            evaporationTimeDto.setEvp(e);
            evaporationTimeDto.setTime(tm);
            list.add(evaporationTimeDto);
            evaporTimeDtoListMap.put(stcd, list);
        }
        // 封装返回对象
        for (Map.Entry<String, List<EvaporationTimeDto>> entry : evaporTimeDtoListMap.entrySet()) {
            EvaporationSumDto evaporationSumDto = new EvaporationSumDto();
            evaporationSumDto.setSumDrp(stcdSumMap.get(entry.getKey()));
			SqybStStbprpBDto stStbprpBDto=null;
			SqybStStbprpB stStbprpB = stStbprpBDao.findById(entry.getKey()).get();
			if (null!=stStbprpB){
				stStbprpBDto=new SqybStStbprpBDto();
				BeanUtils.copyProperties(stStbprpB,stStbprpBDto);
			}
            evaporationSumDto.setStation(stStbprpBDto);
            evaporationSumDto.setEvaporationList(entry.getValue());
            evaporationSumDtoList.add(evaporationSumDto);
        }
        return evaporationSumDtoList;
    }

	@Override
	public List<RainFallTime> getPlanOutPutRain(String planId) {
		DecimalFormat df = new DecimalFormat("0.00");
		List<RainFallTime> list = new ArrayList<>();
		List<SqybModelOutPutRainfall> findByPlanId = modelOutPutRainfallDao.findByPlanIdOrderByTmAsc(planId);
		for (SqybModelOutPutRainfall modelOutPutRainfall : findByPlanId) {
			RainFallTime rainFallTime = new RainFallTime();
			rainFallTime.setDrp(Double.parseDouble(df.format(modelOutPutRainfall.getQ())));
			rainFallTime.setTime(modelOutPutRainfall.getTm());
			list.add(rainFallTime);
		}

		return list;
	}

    /**
     * LiuGt add at 2020-03-19
     * 向导入降雨量数据模板文件中设置方案的起始时间和结束时间
     * @param response
     * @param planId 方案ID
     */
    @Override
    public void setRainfallTimeToTemplate(HttpServletResponse response, String planId){
        try {
            String filePath = PropertiesUtil.read("/filePath.properties").getProperty("modelRainTemPath");
            XSSFWorkbook wb = setTimeToXSSWorkbook(planId, filePath);
            String fileName = "rain_template_" + planId +".xlsx" ;
            //响应尾
            //设置要下载的文件的名称
            //response.setHeader("Content-disposition", "attachment;fileName=" + fileName);
            //通知客服文件的MIME类型
            //response.setContentType("application/vnd.ms-excel;charset=UTF-8");

            //response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            //response.setHeader("Content-disposition" , "attachment;filename="+new String(fileName.getBytes(), "iso_8859_1"));

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            //String fileName = "UseWaterPlan_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())+".xlsx" ;
            response.setHeader("Content-disposition" , "attachment;filename="+new String(fileName.getBytes(), "iso_8859_1"));
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
            wb.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	/**
	 * LiuGt add at 2020-03-17
	 * 向导入蒸发量数据模板文件中设置方案的起始时间和结束时间
	 * @param response
	 * @param planId 方案ID
	 */
	@Override
	public void setEvaporTimeToTemplate(HttpServletResponse response, String planId){
        try {
            String filePath = PropertiesUtil.read("/filePath.properties").getProperty("modelEvaporTemPath");
            XSSFWorkbook wb = setTimeToXSSWorkbook(planId, filePath);
            //响应尾
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "evaporation_template_" + planId +".xlsx" ;
            response.setHeader("Content-disposition" , "attachment;filename="+new String(fileName.getBytes(), "iso_8859_1"));
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

		/*XSSFSheet sheet = null;
		sheet = wb.getSheetAt(0);
		try{
			LocalDateTime startTm = modelPlanInfo.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			LocalDateTime endTm = modelPlanInfo.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			startTm = startTm.plusHours(1);
			endTm = endTm.plusHours(1);
            //向单元格中写入数据
            int rowCount = 1; //第2行开始写数据
			while(startTm.isBefore(endTm)){
				//添加一行
				XSSFRow row = sheet.createRow(rowCount);
				row.setHeight((short)560); //设置行高 28*20
				//渲染excel数据
				//注意，每一行的第一列首先要用createRow创建行，之后用getRow来获取该行
				//row.createCell(0).setCellValue(level1Count);	//编号
				//从第二列开始使用getRow获取行
				row.createCell(0).setCellValue(startTm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));    //时间
				startTm = startTm.plusHours(1);
				rowCount++;
			}
		}
		catch (Exception ex){
			ex.printStackTrace();
		}*/
	}

	private XSSFWorkbook setTimeToXSSWorkbook(String planId, String filePath){
        //获取方案信息
        SqybModelPlanInfoDto modelPlanInfoDto = findByPlanId(planId);

        // 定义一个数据格式化对象
        XSSFWorkbook wb = null;
        SXSSFWorkbook sWb = null;
        try {
            //excel模板路径
            File cfgFile = ResourceUtils.getFile(filePath);
            InputStream in = new FileInputStream(cfgFile);
            //读取excel模板
            wb = new XSSFWorkbook(in);
            //sWb=new SXSSFWorkbook(wb,-1);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        XSSFSheet sheet = null;
        sheet = wb.getSheetAt(0);

        LocalDateTime startTm = modelPlanInfoDto.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusHours(1);
        LocalDateTime endTm = modelPlanInfoDto.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().plusHours(1);

		// 这里需要考虑预测方案的情况，返回的时段列表不能包括创建方案时未来的时段，在这里处理
		// LiuGt add at 2020-03-24
		// 方案类型（0实时方案，1预测方案）
		if (modelPlanInfoDto.getPlanType().equals("1")){
			//预见期
			String planForesee = modelPlanInfoDto.getPlanForesee();
			int iPlanForesee = planForesee == null || planForesee.equals("") ?
					3 : Integer.valueOf(planForesee).intValue();
			endTm = endTm.plusHours(0 - iPlanForesee - 1);
		}

        //向单元格中写入数据
        int rowCount = 1; //第2行开始写数据
        while(startTm.isBefore(endTm)){
            //添加一行
            XSSFRow row = sheet.createRow(rowCount);
            row.setHeight((short)560); //设置行高 28*20
            row.createCell(0).setCellValue(startTm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));    //时间
            startTm = startTm.plusHours(1);
            rowCount++;
        }
        return wb;
    }

    /**
     * LiuGt add at 2020-03-18
     * 查询方案计算结果数据（入库流量）
     * 返回值添加了实测流量，误差率
     * @return
     */
    @Override
    public PlanResultViewDto getPlanResult(String planId){
        PlanResultViewDto planResultViewDto = new PlanResultViewDto();

        //方案基本信息
        SqybModelPlanInfo modelPlanInfo = modelPlanInfoDao.findByPlanId(planId);
        if (modelPlanInfo == null){
            return planResultViewDto;
        }
		SqybModelPlanInfoDto modelPlanInfoDto =new SqybModelPlanInfoDto();
        BeanUtils.copyProperties(modelPlanInfo,modelPlanInfoDto);

        planResultViewDto.setModelPlanInfo(modelPlanInfoDto);

        //获取模型结果（入库流量）
        List<SqybModelOutPutRainfall> modelOutPutRainfalls = modelOutPutRainfallDao.findByPlanIdOrderByTmAsc(planId);
        if (modelOutPutRainfalls == null || modelOutPutRainfalls.size() <= 0){
            planResultViewDto.setAvgErrors(new Double(0));
        }

        //设置时段的入库流量
        List<PlanResultListDto> resultListDtoList = new ArrayList<>();
        modelOutPutRainfalls.forEach(output->{
            PlanResultListDto resultListDto = new PlanResultListDto();
            resultListDto.setTm(DateUtil.dateToStringWithFormat(output.getTm(), "yyyy-MM-dd HH:mm:ss"));
            //预测流量
            Double estimateQ = new BigDecimal(output.getQ()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            resultListDto.setEstimateQ(estimateQ);
            //实测流量
            //随机产生实测流量（以后有了实测数据后，代替随机值）
            Double realQ = estimateQ - estimateQ * Math.random();
            realQ = new BigDecimal(realQ).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            resultListDto.setRealQ(realQ);
            //误差率
            Double errorRate = new Double(0.00);
            if (realQ.doubleValue() != 0){
                errorRate = new BigDecimal(Math.abs(estimateQ - realQ) / realQ * 100).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            resultListDto.setErrorRate(new BigDecimal(errorRate).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
            resultListDtoList.add(resultListDto);
        });

        if (resultListDtoList != null && resultListDtoList.size() > 0) {
			//计算平均误差率
			OptionalDouble avgErrorRate = resultListDtoList.stream()
					.mapToDouble(PlanResultListDto::getErrorRate).average();
			if (avgErrorRate.isPresent()) {
				planResultViewDto.setAvgErrors(new BigDecimal(avgErrorRate.getAsDouble()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
			}
			else{
				planResultViewDto.setAvgErrors(new Double(0));
			}
		}
        else{
			planResultViewDto.setAvgErrors(new Double(0));
		}
		planResultViewDto.setPlanResultListDtoList(resultListDtoList);
        return planResultViewDto;
    }

	/**
	 * 编辑方案模型运行的条件数据
	 * @param modelLoopRunDto
	 * @return
	 */
	@Override
	public SqybModelLoopRunDto editModelLoopRun(SqybModelLoopRunDto modelLoopRunDto){

		SqybModelLoopRun modelLoopRun = new SqybModelLoopRun();
		modelLoopRun.setId(StrUtil.getUUID());
		modelLoopRun.setModiTime(LocalDateTime.now());
		SqybModelLoopRun save = modelLoopRunDao.save(modelLoopRun);
		BeanUtils.copyProperties(save,modelLoopRunDto);
		return modelLoopRunDto;
	}

	/**
	 * 查询最新的方案模型运行条件数据
	 * @return
	 */
	@Override
	public SqybModelLoopRunDto queryNewModelLoopRun(){
		SqybModelLoopRunDto modelLoopRunDto=null;
		SqybModelLoopRun modelLoopRun = modelLoopRunDao.queryNewModelLoopRun();
		if (null!=modelLoopRun){
			modelLoopRunDto=new SqybModelLoopRunDto();
			BeanUtils.copyProperties(modelLoopRun,modelLoopRunDto);
		}
		return modelLoopRunDto;
	}

	/**
	 * 设置能启动模型滚动计算条件的降雨数据（仅测试滚动计算使用）
	 * LiuGt add at 2020-05-20
	 * @return
	 */
	@Override
	public String setAutoRunModelRain(){
		String result = "";
		List<SqybRelStRes> relStResAll = relStResDao.findAll();
		List<SqybRelStRes> ppRelStRes = relStResAll.stream()
				.filter(rel->rel.getSttp().equals("PP")).collect(Collectors.toList());
		List<String> stCodeList = ppRelStRes.stream().map(SqybRelStRes::getStCode).distinct().collect(Collectors.toList());
		for (int i = 0; i < stCodeList.size(); i++) {
			String stCode = stCodeList.get(i);
			LocalDateTime currentTime = LocalDate.now().atTime(LocalDateTime.now().getHour(),0,0).plusHours(1);
			LocalDateTime beginTime = currentTime.plusHours(-3);
			while (beginTime.isBefore(currentTime)){
				SqybStPptnHr stPptnHr = new SqybStPptnHr();
				stPptnHr.setStcd(stCode);
				/*String tm = beginTime.getYear() + "-" + String.format("%02d", beginTime.getMonthValue()) + "-" +
						String.format("%02d", beginTime.getDayOfMonth()) + " " + String.format("%02d", beginTime.getHour()) + ";" +
						String.format("%02d", beginTime.getMinute()) + ":00";*/
				Date tm = Date.from(beginTime.atZone(ZoneId.systemDefault()).toInstant());
				stPptnHr.setTm(tm);
				double rain = Math.random()*(5-1)+1;
				stPptnHr.setDrp(rain);
				beginTime = beginTime.plusMinutes(5);
				stPptnHrDao.save(stPptnHr);
			}
		}
		return "ok";
	}

	/**
	 * 查询多个方案对比的结果
	 * @param planIds
	 * @return
	 */
	@Override
	public SystemSecurityMessage queryPlanContrastResult(List<String> planIds){
		List<PlanContrastResultViewDto> resultViewDtos = new ArrayList<>();
		Integer count = modelOutPutRainfallDao.queryCountGroupRescodeAndModelIdByPlanIds(planIds);
		SystemSecurityMessage systemSecurityMessage = new SystemSecurityMessage();
		if (count > 1){
			//对比的方案含(不同方案或不同模型)
			systemSecurityMessage.setCode("error");
			systemSecurityMessage.setInfo("相同水库相同模型的多个方案才能进行对比！");
			systemSecurityMessage.setResult(resultViewDtos);
		} else{
			List<SqybModelInfo> modelInfoList = modelInfoDao.findAll();
			Map<String, String> modelInfoMap = modelInfoList.stream().collect(Collectors.toMap(SqybModelInfo::getModelId, SqybModelInfo::getModelName));
			List<SqybRes> resList = resDao.findAll();
			Map<String, String> resInfoMap = resList.stream().collect(Collectors.toMap(SqybRes::getResCode, SqybRes::getResName));


			//查询各方案数据
			List<SqybModelPlanInfo> modelPlanInfos = modelPlanInfoDao.queryByPlanIds(planIds);
			//查询各方案结果数据（入库流量）
			List<SqybModelOutPutRainfall> modelOutPutRainfalls = modelOutPutRainfallDao.queryByPlanIds(planIds);
			for (String planId : planIds) {
				//查询当前方案的输入降雨数据（各时段的平均降雨量）
				List<Map<String, Object>> hourAvgRainMapList = modelInputRainfallDao.queryAvgPByPlanId(planId);
				PlanContrastResultViewDto resultViewDto = new PlanContrastResultViewDto();
				//过滤出当前方案的基本信息
				List<SqybModelPlanInfo> subPlanInfos = modelPlanInfos.stream().filter(plan -> plan.getPlanId().equals(planId))
						.collect(Collectors.toList());
				if (subPlanInfos != null && subPlanInfos.size() > 0){
					SqybModelPlanInfo modelPlanInfo = subPlanInfos.get(0);
					SqybModelPlanInfoDto modelPlanInfoDto = new SqybModelPlanInfoDto();
					BeanUtils.copyProperties(modelPlanInfo,modelPlanInfoDto);
					String modelName = modelInfoMap.get(modelPlanInfoDto.getModelId());
					modelPlanInfoDto.setModelName(modelName);
					String resName = resInfoMap.get(modelPlanInfoDto.getResCode());
					modelPlanInfoDto.setResName(resName);
					resultViewDto.setModelPlanInfo(modelPlanInfoDto);
				}
				//存放方案结果（时段，入库流量，降雨）数据的list
				List<HourTimeInqAndPDto> hourTimeInqAndPDtoList = new ArrayList<>();
				//过滤出当前方案的模型模拟结果（入库流量）
				List<SqybModelOutPutRainfall> subOutputs = modelOutPutRainfalls.stream().filter(output -> output.getPlanId().equals(planId))
						.collect(Collectors.toList());
				if (subOutputs !=null && subOutputs.size() > 0){
					for (SqybModelOutPutRainfall subOutput : subOutputs) {
						HourTimeInqAndPDto hourTimeInqAndPDto = new HourTimeInqAndPDto();
						Date dataTm = subOutput.getTm();
						LocalDateTime tm = LocalDateTime.ofInstant(dataTm.toInstant(), ZoneId.systemDefault());
						hourTimeInqAndPDto.setTm(tm);
						hourTimeInqAndPDto.setInq(subOutput.getQ());
						//找对应时段的平均降雨
						List<Map<String, Object>> hourDrpMap = hourAvgRainMapList.stream()
								.filter(map -> map.get("tm").toString().equals(tm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
								.collect(Collectors.toList());
						if (hourDrpMap != null && hourDrpMap.size() > 0 && hourDrpMap.get(0).get("avgP")!=null){
							hourTimeInqAndPDto.setP(new BigDecimal(hourDrpMap.get(0).get("avgP").toString()).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue());
						}
						else{
							hourTimeInqAndPDto.setP(new Double(0));
						}
						hourTimeInqAndPDtoList.add(hourTimeInqAndPDto);
					}
				}
				resultViewDto.setModelOutPutRainfallList(hourTimeInqAndPDtoList);
				resultViewDtos.add(resultViewDto);
			}
			systemSecurityMessage.setCode("ok");
			systemSecurityMessage.setInfo("查询成功！");
			systemSecurityMessage.setResult(resultViewDtos);
		}
		return systemSecurityMessage;
	}

	/**
	 * 查询指定方案输入数据（各时段平均降雨和累积降雨）
	 * @param planId
	 * @return
	 */
	@Override
	public List<HourTimeDrpListDto> queryPlanInputRainfall(String planId){
		//查询当前方案的输入降雨数据（各时段的平均降雨量）
		List<Map<String, Object>> hourAvgRainMapList = modelInputRainfallDao.queryAvgPByPlanId(planId);
		//返回值
		List<HourTimeDrpListDto> hourTimeDrpListDtoList = new ArrayList<>();
		if (hourAvgRainMapList == null || hourAvgRainMapList.size() <= 0){
			return hourTimeDrpListDtoList;
		}
		double totalP = 0.0;
		for (Map<String, Object> map : hourAvgRainMapList) {
			HourTimeDrpListDto hourTimeDrpListDto = new HourTimeDrpListDto();
			hourTimeDrpListDto.setTm(LocalDateTime.parse(map.get("tm").toString(),DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
			double avgP = new BigDecimal(map.get("avgP").toString()).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
			hourTimeDrpListDto.setAvgP(new BigDecimal(avgP).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue());
			totalP += avgP;
			hourTimeDrpListDto.setTotalP(new BigDecimal(totalP).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue());
			hourTimeDrpListDtoList.add(hourTimeDrpListDto);
		}
		return hourTimeDrpListDtoList;
	}

	/**
	 * 方案结果展示 - 查询水库预测水位
	 * LiuGt add at 2020-07-09
	 * @return
	 */
	@Override
	public HourTimeWaterLevelViewDto queryResForecastWaterLevel(String planId){
		//返回值
		HourTimeWaterLevelViewDto waterLevelViewDto = new HourTimeWaterLevelViewDto();
		//存储时段水位
		List<HourTimeWaterLevelListDto> waterLevelListDtoList = new ArrayList<>();
		//查询方案基本信息
		SqybModelPlanInfo modelPlanInfo = modelPlanInfoDao.findByPlanId(planId);
		String resCode = modelPlanInfo.getResCode();
		Date startTime = modelPlanInfo.getStartTime();
		//查询水库基本信息
		SqybRes resInfo = resDao.queryByResCode(resCode);
		SqybResDto resDto=null;
		if (null!=resInfo){
			resDto=new SqybResDto();
			BeanUtils.copyProperties(resInfo,resDto);
		}
		waterLevelViewDto.setResInfo(resDto);
		if (resInfo == null || StrUtil.isEmpty(resInfo.getHtStcd())){
			//没有对应的慧图水库测站ID，直接返回空数组
			waterLevelViewDto.setWaterLevelListDtoList(waterLevelListDtoList);
			return waterLevelViewDto;
		}
		String htStcd = resInfo.getHtStcd();
		//查询水库距离方案开始时间最近的一次实测水位和库容数据（慧图的数据库）
		List<HtStRsvrRViewDto> htStRsvrRViewDtos = stRsvrRDao.queryByLatelyTmAndStcd(startTime, htStcd);
		if (htStRsvrRViewDtos == null || htStRsvrRViewDtos.size() <= 0){
			//没有实测水位和库容数据
			waterLevelViewDto.setWaterLevelListDtoList(waterLevelListDtoList);
			return waterLevelViewDto;
		}
		//查询方案结果数据（入库流量）
		List<SqybModelOutPutRainfall> planResultList = modelOutPutRainfallDao.findByPlanIdOrderByTmAsc(planId);
		if (planResultList == null || planResultList.size() <= 0){
			waterLevelViewDto.setWaterLevelListDtoList(waterLevelListDtoList);
			return waterLevelViewDto;
		}
		double v = htStRsvrRViewDtos.get(0).getW(); //起始库容值
		//根据各时段的入库流量计算对应的库容和水位
		for (SqybModelOutPutRainfall modelOutPutRainfall : planResultList) {
			HourTimeWaterLevelListDto hourTimeWaterLevelListDto = new HourTimeWaterLevelListDto();
			LocalDateTime tm = LocalDateTime.ofInstant(modelOutPutRainfall.getTm().toInstant(), ZoneId.systemDefault());
			hourTimeWaterLevelListDto.setTm(tm);
			v += (modelOutPutRainfall.getQ() * 3600)/new Double(10000).doubleValue(); //累加当前时段的入库流量
			//这里还要减出库，不知道该减多少，先不减
			//v = v - x;
			hourTimeWaterLevelListDto.setW(new BigDecimal(v).setScale(3,BigDecimal.ROUND_HALF_UP).doubleValue());
			//计算各时段的水位
			double h = 0;
			switch (resCode){
				case "RES_0010": //驮英水库
					h = 2.70150221 * Math.pow(v, 0.3190321) + 166.84502004; //驮英水库拟合曲线公式 h为水位，v为该时段的库容
					break;
				case "RES_0011": //客兰水库
					h = 1.51473623 * Math.pow(v, 0.28379909) + 108.05348282; //客兰水库拟合曲线公式 h为水位，v为该时段的库容
					break;
				case "RES_0012": //派关水库(库容曲线很细，无需用拟合曲线来计算水位)
					//h = 2.70150221 * Math.pow(v, 0.3190321) + 166.84502004; //驮英水库拟合曲线公式 h为水位，v为该时段的库容
					Double aDouble = hifZvarlBDao.queryRzByResCodeAndW(resCode, v);
					if (aDouble != null){
						h = aDouble.doubleValue();
					}
					break;
			}
			hourTimeWaterLevelListDto.setRz(new BigDecimal(h).setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue());
			waterLevelListDtoList.add(hourTimeWaterLevelListDto);
		}
		//返回值
		waterLevelViewDto.setWaterLevelListDtoList(waterLevelListDtoList);
		return waterLevelViewDto;
	}
}
