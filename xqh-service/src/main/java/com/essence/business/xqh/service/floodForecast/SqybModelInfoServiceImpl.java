package com.essence.business.xqh.service.floodForecast;

import com.essence.business.xqh.api.floodForecast.dto.*;
import com.essence.business.xqh.api.floodForecast.service.SqybModelInfoService;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.floodForecast.*;
import com.essence.business.xqh.dao.entity.floodForecast.*;
import com.essence.framework.util.CacheUtil;
import com.essence.framework.util.DateUtil;
import com.essence.framework.util.StrUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
/**
 * 洪水预报预警--基础信息
 * @Author huangxiaoli
 * @Description
 * @Date 10:11 2020/12/31
 * @Param
 * @return
 **/
@Service
public class SqybModelInfoServiceImpl implements SqybModelInfoService {

	@Autowired
	private SqybModelInfoDao modelInfoDao;
	@Autowired
	private SqybModelInputRainfallDao modelInputRainfallDao;
	@Autowired
	private SqybModelPlanInfoDao modelPlanInfoDao;
	@Autowired
	private SqybModelOutPutRainfallDao modelOutPutRainfallDao;
	@Autowired
	private SqybModelInputEvaporationDao modelInputEvaporationDao;
	@Autowired
	private SqybResDao resDao;

	@Override
	public List<SqybModelInfoDto> findModelAll() {
		ArrayList<SqybModelInfoDto> modelInfoDtosList = new ArrayList<>();
		List<SqybModelInfo> all = modelInfoDao.findAll();
		if (all.size()>0){
			for (SqybModelInfo modelInfo : all) {
				SqybModelInfoDto modelInfoDto = new SqybModelInfoDto();
				BeanUtils.copyProperties(modelInfo,modelInfoDto);
				modelInfoDtosList.add(modelInfoDto);
			}
		}
		return modelInfoDtosList;
	}

	@Override
	@Transactional
	public Object modelRaun(String planId) {

		try {
			//获取方案基本信息
			SqybModelPlanInfo planInfo = modelPlanInfoDao.findByPlanId(planId);

			// region 方案条件 - 降雨量入库
			List<SqybModelInPutRainfall> modelInputRainfallList = new ArrayList<SqybModelInPutRainfall>();
			// 从缓存中获取降雨量
			List<RainFallSumDto> rainFallSumList = (List<RainFallSumDto>) CacheUtil.get("rainFallSum", planId + "ptnHrMap");
			if (rainFallSumList != null && rainFallSumList.size() > 0) {
				for (RainFallSumDto rainFallSumDto : rainFallSumList) {
					SqybStStbprpBDto station = rainFallSumDto.getStation();
					List<RainFallTime> rainList = rainFallSumDto.getRainList();
					for (RainFallTime rainFall : rainList) {
						SqybModelInPutRainfall modelInputRainfall = new SqybModelInPutRainfall();
						modelInputRainfall.setId(StrUtil.getUUID());
						modelInputRainfall.setModiTime(new Date());
						modelInputRainfall.setP(rainFall.getDrp());
						modelInputRainfall.setTm(rainFall.getTime());
						modelInputRainfall.setPlanId(planId);
						modelInputRainfall.setStcd(station.getStcd());
						modelInputRainfall.setStnm(station.getStnm());
						modelInputRainfallList.add(modelInputRainfall);
					}
				}
			}
			// 判断是否入过库了
			List<SqybModelInPutRainfall> findByPlanId = modelInputRainfallDao.findByPlanIdOrderByTm(planId);
			if (findByPlanId == null || findByPlanId.size() == 0) {
				if (modelInputRainfallList.size() > 0) {
					for (SqybModelInPutRainfall modelInputRainfall : modelInputRainfallList) {
						modelInputRainfallDao.save(modelInputRainfall);
					}
				}
			}
			//endregion

			//region 方案条件 - 蒸发量入库（新安江模型才有蒸发量）
			List<SqybModelInputEvaporation> modelInputEvaporationList = new ArrayList<>();
			if (planInfo.getModelId().equals("XAJ")) {
				// 从缓存中获取蒸发量
				List<EvaporationSumDto> evaporationSumDtoList = (List<EvaporationSumDto>) CacheUtil.get("evaporationSum", planId + "evHrMap");
				if (evaporationSumDtoList != null && evaporationSumDtoList.size() > 0) {
					for (EvaporationSumDto evaporationSumDto : evaporationSumDtoList) {
						SqybStStbprpBDto station = evaporationSumDto.getStation();
						List<EvaporationTimeDto> evaporationTimeDtoList = evaporationSumDto.getEvaporationList();
						for (EvaporationTimeDto evaporationTimeDto : evaporationTimeDtoList) {
							SqybModelInputEvaporation modelInputEvaporation = new SqybModelInputEvaporation();
							modelInputEvaporation.setId(StrUtil.getUUID());
							modelInputEvaporation.setModiTime(LocalDateTime.now());
							modelInputEvaporation.setE(evaporationTimeDto.getEvp());
							modelInputEvaporation.setTm(evaporationTimeDto.getTime());
							modelInputEvaporation.setPlanId(planId);
							modelInputEvaporation.setStcd(station.getStcd());
							modelInputEvaporationList.add(modelInputEvaporation);
						}
					}
				}
				// 判断是否入过库了
				List<SqybModelInputEvaporation> inputEvaporFindByPlanId = modelInputEvaporationDao.findByPlanIdOrderByTm(planId);
				if (inputEvaporFindByPlanId == null || inputEvaporFindByPlanId.size() == 0) {
					if (modelInputEvaporationList.size() > 0) {
						for (SqybModelInputEvaporation modelInputEvaporation : modelInputEvaporationList) {
							modelInputEvaporationDao.save(modelInputEvaporation);
						}
					}
				}
			}
			//endregion

			//修改模型参数及调用模型计算
			updateRunModel(planId, planInfo, modelInputRainfallList, modelInputEvaporationList);

			// 将模型输出流量数据写入数据库
			List<String> modelResultList = readModelOutFile(planId, planInfo.getModelId());
			if (modelResultList.size() > 0) {
				if ("XAJ".equals(planInfo.getModelId())) {
					//新安江模型输入结果与其他两个模型不同，第一行加了标题"value"，去掉此行
					if (modelResultList.get(0).equals("value")) {
						modelResultList.remove(0);
					}
				}
				Date startTime = planInfo.getStartTime();
				startTime = DateUtil.getNextHour(startTime, 1);
				int count = 0;
				List<SqybModelOutPutRainfall> modelOutPutRainfallList = new ArrayList<>();
				for (String q : modelResultList) {
					SqybModelOutPutRainfall modelOutPutRainfall = new SqybModelOutPutRainfall();
					modelOutPutRainfall.setId(StrUtil.getUUID());
					modelOutPutRainfall.setPlanId(planId);
					modelOutPutRainfall.setTm(DateUtil.getNextHour(startTime, count));
					modelOutPutRainfall.setQ(Double.parseDouble(q));
					modelOutPutRainfallList.add(modelOutPutRainfall);
					count++;
				}
				modelOutPutRainfallDao.saveAll(modelOutPutRainfallList);
			}
			// 修改方案状态
			planInfo.setPlanStatus(2);
			planInfo.setPlanStatusInfo("方案计算完成");
			modelPlanInfoDao.save(planInfo);
			return "方案计算运行完成！";
		}
		catch (Exception e){
			System.out.println("调用模型计算异常");
			e.printStackTrace();
			return "方案计算发生异常！";
		}
	}

	/**
	 * 获取水库列表
	 *
	 * @return
	 */
	@Override
	public List<SqybResDto> getAllResInfo() {
		List<SqybResDto> resDtoList = new ArrayList<>();
		List<SqybRes> all = resDao.findAll();
		if (all.size()>0){
			for (SqybRes res : all) {
				SqybResDto resDto = new SqybResDto();
				BeanUtils.copyProperties(res,resDto);
				resDtoList.add(resDto);
			}
		}
		return resDtoList;
	}

	/**
	 * 修改模型相关并调研模型计算
	 * @param planId	方案ID
	 * @param planInfo	方案信息
	 * @param modelInputRainfallList	//降雨条件
	 * @param modelInputEvaporationList //蒸发条件
	 */
	private void updateRunModel(String planId, SqybModelPlanInfo planInfo,
								List<SqybModelInPutRainfall> modelInputRainfallList, List<SqybModelInputEvaporation> modelInputEvaporationList) {
		//获取模型的基本目录
		String modelProgramBasePath = PropertiesUtil.read("/filePath.properties").getProperty("modelBasePath")
				+ "\\" + planInfo.getResCode() + "\\";
		// 创建模型需要参数txt文件
		/*String modelParamPath = PropertiesUtil.read("/filePath.properties").getProperty("modelScSParamFile") + "\\"
				+ planId + ".txt";*/
		String modelParamPath = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelScSParamFile") + "\\"
				+ planId + ".txt";
		if("DWX".equals(planInfo.getModelId())) {
			/*modelParamPath = PropertiesUtil.read("/filePath.properties").getProperty("modelDwxParamFile") + "\\"
					+ planId + ".txt";*/
			modelParamPath = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelDwxParamFile") + "\\"
					+ planId + ".txt";
		}
		//LiuGt add at 2020-03-11
		//新安江模型相关设置
		String modelParamPath_D = "";
		String modelParamPath_F = "";
		if ("XAJ".equals(planInfo.getModelId())){
			/*modelParamPath_D = PropertiesUtil.read("/filePath.properties").getProperty("modelXajParamFile") + "\\"
					+ planId + "_D.txt";
			modelParamPath_F = PropertiesUtil.read("/filePath.properties").getProperty("modelXajParamFile") + "\\"
					+ planId + "_F.txt";*/
			modelParamPath_D = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelXajParamFile") + "\\"
					+ planId + "_D.txt";
			modelParamPath_F = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelXajParamFile") + "\\"
					+ planId + "_F.txt";
		}
		if ("XAJ".equals(planInfo.getModelId())){
			//这里暂时要准备蒸发量的测试数据，前端改好会，会把蒸发量接进来
			//List<ModelInputEvaporation> modelInputEvaporationList = new ArrayList<>();
			//modelInputEvaporationList = initModelInputEvaporation(planId);

			//新安江模型要创建两个入参文件
			createFile(modelParamPath_D);
			createFile(modelParamPath_F);
			//写参数(日数据)
			try {
				List<SqybModelInPutRainfall> dModelInputRainfallList = deepCopy(modelInputRainfallList);
				List<SqybModelInputEvaporation> dModelInputEvaporationList = deepCopy(modelInputEvaporationList);
				xajWriteFile_D(planId, modelParamPath_D, dModelInputRainfallList, dModelInputEvaporationList);
				//写参数(逐小时数据)
				List<SqybModelInPutRainfall> fModelInputRainfallList = deepCopy(modelInputRainfallList);
				List<SqybModelInputEvaporation> fModelInputEvaporationList = deepCopy(modelInputEvaporationList);
				xajWriteFile_F(planId, modelParamPath_F, fModelInputRainfallList, fModelInputEvaporationList);
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}
		else {
			//其他模型创建一个入参文件
			createFile(modelParamPath);
			// 写参数
			writeFile(planId, modelParamPath, modelInputRainfallList);
		}

		// 修改初损文件内容的初损值
		if("DWX".equals(planInfo.getModelId())) {
			updateChuSun(modelProgramBasePath, planInfo.getInitialDamage());
		}
		// 修改配置文件参数
		updateModelParam(modelProgramBasePath,planId,planInfo.getModelId());
		// 调用模型计算
		if("SCS".equals(planInfo.getModelId())) {
			runModelExe(modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelScSExePath"));
		}else if("DWX".equals(planInfo.getModelId())) {
			runModelExe(modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelDwxExePath"));
		}
        else if("XAJ".equals(planInfo.getModelId())) {
            runModelExe(modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelXajExePath"));
        }
	}

	//List 深copy
	private static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);

		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		@SuppressWarnings("unchecked")
		List<T> dest = (List<T>) in.readObject();
		return dest;
	}

	/**
	 * 创建模型所需参数文件
	 * 
	 * @param filePath
	 */
	private void createFile(String filePath) {
		// 方案条件写入模型调用txt
		// 获取视频指定的config配置文件中配置路径
		File reportFile = new File(filePath);
		try {
			reportFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 写入参数文件数据
	 * 
	 * @param modelParamPath
	 * @param modelInputRainfallList
	 */
	private void writeFile(String planId, String modelParamPath, List<SqybModelInPutRainfall> modelInputRainfallList) {
		// 时间序列值（所有雨量站同一时间的雨量和）
		Map<String, Double> rainFallMap = new HashMap<String, Double>();

		//按照权重比例计算每小时模型需要的雨量数据
		/*for (ModelInPutRainfall mf : modelInputRainfallList) {
			String rainTimekey = DateUtil.dateToStringNormal(mf.getTm());
			Double rainFall = rainFallMap.get(rainTimekey);
			if (rainFall == null) {
				rainFall = mf.getP();
			} else {
				rainFall += mf.getP();
			}
			rainFallMap.put(rainTimekey, rainFall);
		}*/

        //按照权重比例计算每小时模型需要的雨量数据
        //LiuGt add at 2020-03-23
        // step: 先取出所有时段
        List<Date> timeList = modelInputRainfallList.stream()
                .sorted(Comparator.comparing(SqybModelInPutRainfall::getTm))
                .map(SqybModelInPutRainfall::getTm)
                .collect(Collectors.toList());
        // step2: 计算每个时间的加权平均降雨量
        for (Date date : timeList) {
            String rainTimekey = DateUtil.dateToStringNormal(date);
            //获取当前时段各雨量站的时段降雨量
            List<SqybModelInPutRainfall> hourRainList = modelInputRainfallList.stream()
                    .filter(modelInfo-> DateUtil.dateToStringNormal(modelInfo.getTm()).equals(rainTimekey))
                    .collect(Collectors.toList());
            // 加权平均算法处理每个时段的降雨量
            Double hourRain = tuoyingWeightedMean(hourRainList);
            // step3: 写入Map
            rainFallMap.put(rainTimekey, hourRain);
        }

		//region 如果方案是预测方案，则还要从缓存中提取预测时段的降雨量
		//先从缓存中取预见期各时段的降雨量
		List<RainFallTime> rainFallTimeList = (List<RainFallTime>) CacheUtil.get("foreseeRainFall", planId + "ForeseeRainList");
		if (rainFallTimeList != null && rainFallTimeList.size() > 0){
			//写入Map
			for (RainFallTime rainFallTime : rainFallTimeList) {
				String foreseeTm = DateUtil.dateToStringNormal(rainFallTime.getTime());
				rainFallMap.put(foreseeTm, rainFallTime.getDrp());
			}
		}
		//endregion

        //将加权平均降雨量写入文本（供模型调用）
		try {
			FileWriter fileWriter = new FileWriter(modelParamPath);// 创建文本文件
			// 取键时间进行排序 进而取值
			Map<String, Double> treeRainFallMap = new TreeMap<>(new MapKeyComparator());
			treeRainFallMap.putAll(rainFallMap);
			for (Map.Entry<String, Double> entry : treeRainFallMap.entrySet()) {
				// 拼接时间格式 HH:MM 时 分 如（1:00,12:00）
				fileWriter.write(
						Integer.parseInt(entry.getKey().substring(11, 13)) + ":00\t" + entry.getValue() + "\r\n");// 写入
																													// \r\n换行
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * LiuGt add at 2020-03-11
	 * 新安江模型写入参文件（日数据文件）
	 * @param planId 方案ID
	 * @param modelParamPath
	 * @param modelInputRainfallList
	 */
	private void xajWriteFile_D(String planId,
								String modelParamPath,
								List<SqybModelInPutRainfall> modelInputRainfallList,
								List<SqybModelInputEvaporation> modelInputEvaporationList){

		//region 如果方案是预测方案，则还要从缓存中提取预测时段的降雨量
		//从缓存中取预见期各时段的降雨量
		List<RainFallTime> rainFallTimeList = (List<RainFallTime>) CacheUtil.get("foreseeRainFall", planId + "ForeseeRainList");
		if (rainFallTimeList != null && rainFallTimeList.size() > 0){
			//将预测时段的降雨量写入modelInputRainfallList列表
			for (RainFallTime rainFallTime : rainFallTimeList) {
				SqybModelInPutRainfall modelInPutRainfall = new SqybModelInPutRainfall();
				modelInPutRainfall.setId(StrUtil.getUUID());
				modelInPutRainfall.setPlanId(planId);
				modelInPutRainfall.setStcd("");
				modelInPutRainfall.setStnm("");
				modelInPutRainfall.setModiTime(new Date());
				modelInPutRainfall.setTm(rainFallTime.getTime());
				modelInPutRainfall.setP(rainFallTime.getDrp());
				modelInputRainfallList.add(modelInPutRainfall);
			}
		}
		//从缓存中取预见期各时段的蒸发量
		List<EvaporationTimeDto> evaporationTimeDtoList = (List<EvaporationTimeDto>) CacheUtil.get("foreseeEvaporation", planId + "ForeseeEvaporList");
		if (evaporationTimeDtoList != null && evaporationTimeDtoList.size() > 0){
			//将预测时段的蒸发量写入modelInputEvaporationList列表
			for (EvaporationTimeDto evaporationTimeDto : evaporationTimeDtoList) {
				SqybModelInputEvaporation modelInputEvaporation = new SqybModelInputEvaporation();
				modelInputEvaporation.setId(StrUtil.getUUID());
				modelInputEvaporation.setPlanId(planId);
				modelInputEvaporation.setStcd("");
				modelInputEvaporation.setModiTime(LocalDateTime.now());
				modelInputEvaporation.setTm(evaporationTimeDto.getTime());
				modelInputEvaporation.setE(evaporationTimeDto.getEvp());
				modelInputEvaporationList.add(modelInputEvaporation);
			}
		}
		//endregion

		List<XajModelTxtInputParamDayDto> xajModelTxtInputParamDayDtos = new ArrayList<>();
		//按日期对降雨量数据进行分组求和，得到日降雨量
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		if (modelInputRainfallList == null || modelInputRainfallList.size() <= 0){
			return;
		}
		//按日期时间分组
		Map<Date, Long> dateGroupSort = new TreeMap<>(
				new Comparator<Date>() {
					public int compare(Date obj1, Date obj2) {
						// 升序排序
						return obj1.compareTo(obj2);
					}
				});
		Map<Date, Long> dateGroup = modelInputRainfallList.stream().collect(
				Collectors.groupingBy(SqybModelInPutRainfall::getTm, Collectors.counting()));
		dateGroup.forEach((k,v)->{
			dateGroupSort.put(k,v);
		});
		//封装日数据入参实体
		dateGroupSort.forEach((k,v)->{
			LocalDateTime tm = k.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			String currentTime = tm.format(DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss"));
			String currentDate = tm.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
			XajModelTxtInputParamDayDto xajModelTxtInputParamDayDto = new XajModelTxtInputParamDayDto();
			xajModelTxtInputParamDayDto.setFloodId(planId);
			xajModelTxtInputParamDayDto.setDayDate(currentTime);
			//日降雨量
			Double pSum = modelInputRainfallList.stream()
					.filter(rainfall-> formatter.format(rainfall.getTm()).equals(currentDate))
					.mapToDouble(SqybModelInPutRainfall::getP).sum();
			xajModelTxtInputParamDayDto.setDayPrecip(pSum);
			//日蒸发量
			Double evaporSum = modelInputEvaporationList.stream()
					.filter(evapor-> evapor.getTm().format(DateTimeFormatter.ofPattern("yyyyMMdd")).equals(currentDate))
					.mapToDouble(SqybModelInputEvaporation::getE).sum();
			xajModelTxtInputParamDayDto.setDayEvapor(evaporSum);
			xajModelTxtInputParamDayDtos.add(xajModelTxtInputParamDayDto);
		});

		try{
			FileWriter fileWriter = new FileWriter(modelParamPath);// 创建文本文件
			//表头（表头含意分别为：洪水标号(方案ID)、日期及时间、日降雨量、日蒸发量）
			fileWriter.write("flood_id\tday_date\tday_precip\tday_evapor" + "\r\n");
			for(XajModelTxtInputParamDayDto dto : xajModelTxtInputParamDayDtos){
				fileWriter.write(dto.getFloodId() + "\t" + dto.getDayDate() + "\t" + dto.getDayPrecip().toString() + "\t" + dto.getDayEvapor() + "\r\n");
			}
			fileWriter.flush();
			fileWriter.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * LiuGt add at 2020-03-11
	 * 新安江模型写入参文件（逐小时数据文件）
	 * @param planId 方案ID
	 * @param modelParamPath
	 * @param modelInputRainfallList
	 */
	private void xajWriteFile_F(String planId,
								String modelParamPath,
								List<SqybModelInPutRainfall> modelInputRainfallList,
								List<SqybModelInputEvaporation> modelInputEvaporationList){
	    List<XajModelTxtInputParamHourDto> xajModelTxtInputParamHourDtos = new ArrayList<>();
        if (modelInputRainfallList == null || modelInputRainfallList.size() <= 0){
            return;
        }
        //按日期时间分组
        Map<Date, Long> dateGroupSort = new TreeMap<>(
                new Comparator<Date>() {
                    public int compare(Date obj1, Date obj2) {
                        // 升序排序
                        return obj1.compareTo(obj2);
                    }
                });
        Map<Date, Long> dateGroup = modelInputRainfallList.stream().collect(
                Collectors.groupingBy(SqybModelInPutRainfall::getTm, Collectors.counting()));
        dateGroup.forEach((k,v)->{
            dateGroupSort.put(k,v);
        });
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd H:mm:ss");
        //封装日数据入参实体
        dateGroupSort.forEach((k,v)->{
            LocalDateTime tm = k.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            String currentTime = tm.format(DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss"));
            XajModelTxtInputParamHourDto xajModelTxtInputParamHourDto = new XajModelTxtInputParamHourDto();
            xajModelTxtInputParamHourDto.setFloodId(planId);
            xajModelTxtInputParamHourDto.setFloodDate(currentTime);
            // 降雨量/h
            List<SqybModelInPutRainfall> hourRainList = modelInputRainfallList.stream()
                    .filter(rainfall-> formatter.format(rainfall.getTm()).equals(currentTime))
                    .collect(Collectors.toList());
            // 加权平均算法处理每个时段的降雨量
            Double hourRain = tuoyingWeightedMean(hourRainList);
            xajModelTxtInputParamHourDto.setFloodPrecip(hourRain);
            // 蒸发量/h
			Double hourEvaporSum = modelInputEvaporationList.stream()
					.filter(evapor-> evapor.getTm().format(DateTimeFormatter.ofPattern("yyyy/MM/dd H:mm:ss")).equals(currentTime))
					.mapToDouble(SqybModelInputEvaporation::getE).sum();
            xajModelTxtInputParamHourDto.setFloodEvapor(hourEvaporSum);
            xajModelTxtInputParamHourDtos.add(xajModelTxtInputParamHourDto);
        });

		//region 如果方案是预测方案，则还要从缓存中提取预测时段的降雨量
		//先从缓存中取预见期各时段的降雨量
		List<RainFallTime> rainFallTimeList = (List<RainFallTime>) CacheUtil.get("foreseeRainFall", planId + "ForeseeRainList");
		//先从缓存中取预见期各时段的蒸发量
		List<EvaporationTimeDto> evaporationTimeDtoList = (List<EvaporationTimeDto>) CacheUtil.get("foreseeEvaporation", planId + "ForeseeEvaporList");
		if (rainFallTimeList != null && rainFallTimeList.size() > 0){
			//写入XajModelTxtInputParamHourDto实体实例
			for (RainFallTime rainFallTime : rainFallTimeList) {
				XajModelTxtInputParamHourDto xajModelTxtInputParamHourDto = new XajModelTxtInputParamHourDto();
				String foreseeTm = DateUtil.dateToStringNormal(rainFallTime.getTime());
				xajModelTxtInputParamHourDto.setFloodId(planId);
				xajModelTxtInputParamHourDto.setFloodDate(foreseeTm);
				//降雨量
				xajModelTxtInputParamHourDto.setFloodPrecip(rainFallTime.getDrp());
				//蒸发量
				Double evp = new Double(0);
				List<EvaporationTimeDto> tmEvaporationTimeDtoList = evaporationTimeDtoList.stream()
						.filter(evapor->evapor.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).equals(DateUtil.dateToStringNormal(rainFallTime.getTime())))
						.collect(Collectors.toList());
				if (tmEvaporationTimeDtoList != null && tmEvaporationTimeDtoList.size() > 0){
					evp = tmEvaporationTimeDtoList.get(0).getEvp();
				}
				xajModelTxtInputParamHourDto.setFloodEvapor(evp);
				xajModelTxtInputParamHourDtos.add(xajModelTxtInputParamHourDto);
			}
		}
		//endregion

        //将数据写入参数文本文件
        try {
            FileWriter fileWriter = new FileWriter(modelParamPath);// 创建文本文件
            //表头（表头含意分别为：洪水标号(方案ID)、时间(小时)、降雨量/h、蒸发量/h）
            fileWriter.write("flood_id\tflood_date\tflood_precip\tflood_evapor" + "\r\n");
            for(XajModelTxtInputParamHourDto dto : xajModelTxtInputParamHourDtos){
                fileWriter.write(dto.getFloodId() + "\t" + dto.getFloodDate() + "\t" + dto.getFloodPrecip().toString() + "\t" + dto.getFloodEvapor() + "\r\n");
            }
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

    /**
     * LiuGt add at 2020-03-12
     * 计算某时段驮英水库各雨量测站的雨量加权平均值
     */
	private Double tuoyingWeightedMean(List<SqybModelInPutRainfall> hourStcdRainList){
	    Map<String, Double> stcdWeightedMeanMap = new HashMap<>();
	    stcdWeightedMeanMap.put("ST001001",new Double("0.2537")); //小平站
        stcdWeightedMeanMap.put("ST001002",new Double("0.0709")); //枯强站
        stcdWeightedMeanMap.put("ST001003",new Double("0.1524")); //潭昔站
        stcdWeightedMeanMap.put("ST001004",new Double("0.2556")); //那驮站
        stcdWeightedMeanMap.put("ST001005",new Double("0.0791")); //九特站
        stcdWeightedMeanMap.put("ST001006",new Double("0.0805")); //板固站
        stcdWeightedMeanMap.put("ST001007",new Double("0.0994")); //叫弄站
        //雨量加权平均值
        Double result = new Double("0.0");
        for(SqybModelInPutRainfall rainfall : hourStcdRainList){
            result += rainfall.getP() * stcdWeightedMeanMap.get(rainfall.getStcd());
        }
        //保留两位小数
        BigDecimal b = new BigDecimal(result);
        result = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return result;
    }


	/**
	 * LiuGt add at 2020-03-12
	 * 准备测试用的蒸发量数据，前端修改并接入蒸发量数据后，此方式废弃
	 * @return
	 */
	private List<SqybModelInputEvaporation> initModelInputEvaporation(String planId){
		List<SqybModelInputEvaporation> list = new ArrayList<>();
		SqybModelPlanInfo planInfo = modelPlanInfoDao.findByPlanId(planId);
		List<String> stcdList = new ArrayList<>();
		stcdList.add("ST002001");
		stcdList.add("ST002002");
		stcdList.add("ST002003");
		for(String stcd : stcdList){
			LocalDateTime startTm = planInfo.getStartTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			LocalDateTime endTm = planInfo.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			startTm = startTm.plusHours(1);
			endTm = endTm.plusHours(1);
			while(startTm.isBefore(endTm)){
				SqybModelInputEvaporation evaporation = new SqybModelInputEvaporation();
				evaporation.setId(StrUtil.getUUID());
				evaporation.setPlanId(planId);
				evaporation.setStcd(stcd);
				evaporation.setTm(startTm);
				evaporation.setModiTime(LocalDateTime.now());
				//随机产生蒸发量
				BigDecimal b = new BigDecimal(Math.random());
				Double e = b.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				evaporation.setE(e);
				list.add(evaporation);
				startTm = startTm.plusHours(1);
			}
		}
		return list;
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

	/**
	 * 修改模型配置文件参数
	 * 
	 * @param planId  方案ID
	 * @param modelId 模型ID
	 */
	private void updateModelParam(String modelProgramBasePath,String planId, String modelId) {
	    if ("XAJ".equals(modelId)){
            setXajModelParam(modelProgramBasePath, planId);
            return;
        }
		try {
			String paramFilePath = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelScSParamPath");
			if ("DWX".equals(modelId)) {
				paramFilePath = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelDwxParamPath");
			}
			File file = new File(paramFilePath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fileWriter = new FileWriter(file);// 创建文本文件
			// 写入四行配置文件参数
			// 输入参数文件路径配置
			if("SCS".equals(modelId)) {
				fileWriter.write(modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelScSParamFile") + "\r\n");
			}else if("DWX".equals(modelId)) {
				fileWriter.write(modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelDwxParamFile") + "\r\n");
			}
			// 输出文件夹配置
			String SCS_MODEL_OUT = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelScSOutPath") + "/"
					+ planId;
			if("DWX".equals(modelId)) {
				 SCS_MODEL_OUT = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelDwxOutPath") + "/"
						+ planId;
			}
			File modelOutFile = new File(SCS_MODEL_OUT);
			modelOutFile.mkdirs();
			fileWriter.write(SCS_MODEL_OUT + "\r\n");
			if("SCS".equals(modelId)) {
				// 单位线参数配置
				fileWriter.write("unit3.45.txt" + "\r\n");
				// 输入雨量配置
				fileWriter.write(planId + ".txt" + "\r\n");
				// 输出文件配置
				fileWriter.write("result.txt" + "\r\n");
				fileWriter.write("50" + "\r\n");
			}else if("DWX".equals(modelId)) {
				// 前五天累计降雨量
				fileWriter.write("chusun.txt" + "\r\n");
				// 单位线参数配置
				fileWriter.write("unit3.45.txt" + "\r\n");
				// 输入雨量配置
				fileWriter.write(planId + ".txt" + "\r\n");
				// 输出文件配置
				fileWriter.write("result.txt" + "\r\n");
			}
			fileWriter.flush();
			fileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * LiuGt add at 2020-03-18
     * 设置新安江模型运行需要的参数
     * @param planId
     */
	private void setXajModelParam(String modelProgramBasePath, String planId){
	    //模型的配置文件路径
        String paramFilePath = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelXajParamPath");
        //模型参数文件的路径
        String modelXajParamFile = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelXajParamFile");
        try{
            File file = new File(paramFilePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fileWriter = new FileWriter(file);// 创建文本文件
            //新安江参数文件名
            fileWriter.write( modelXajParamFile + "/bestpar3.txt" + "\r\n");
            //流域面积（km2）
            fileWriter.write("606" + "\r\n");
            //洪水标号(方案ID)
            fileWriter.write(planId + "\r\n");
            //时间步长（h）
            fileWriter.write("1" + "\r\n");
            //降雨序列文件名
            fileWriter.write(modelXajParamFile + "/" + planId + "_D.txt" + "\r\n");
            //洪水序列文件名
            fileWriter.write(modelXajParamFile + "/" + planId + "_F.txt" + "\r\n");
            //单位线文件名
            fileWriter.write( modelXajParamFile + "/unit3.45.txt" + "\r\n");
            //新安江模型-结果保存路径
            fileWriter.write(modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelXajOutPath"));
            fileWriter.flush();
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

	public List<String> readModelOutFile(String planId, String modelId) {
		SqybModelPlanInfo modelPlanInfo = modelPlanInfoDao.findByPlanId(planId);
		//获取模型的基本目录
		String modelProgramBasePath = PropertiesUtil.read("/filePath.properties").getProperty("modelBasePath")
				+ "\\" + modelPlanInfo.getResCode() + "\\";
		List<String> resultStrList = new ArrayList<>();
		/* 读取数据 */
		BufferedReader br = null;
		try {
			String resultFilePath = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelScSOutPath") + "/"
					+ planId + "/result.txt";
			if("DWX".equals(modelId)) {
				resultFilePath = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelDwxOutPath") + "/"
						+ planId + "/result.txt";
			}
            if("XAJ".equals(modelId)) {
                resultFilePath = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelXajOutPath") + "/"
                        + planId + "/sim1.txt";
            }
			br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(resultFilePath)), "UTF-8"));
			String lineTxt = null;
			while ((lineTxt = br.readLine()) != null) {
				resultStrList.add(lineTxt);
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
		return resultStrList;
	}

	/**
	 * 自定义比较器 时间排序
	 */
	class MapKeyComparator implements Comparator<String> {
		@Override
		public int compare(String t1, String t2) {
			Long diff = DateUtil.getDateByStringNormal(t1).getTime() - DateUtil.getDateByStringNormal(t2).getTime();
			if (diff > 0) {
				return 1;
			} else if (diff < 0) {
				return -1;
			}
			return 0;
		}
	}

	/**
	 * 修改初损值文件
	 *
	 * @param chuSun
	 */
	private void updateChuSun(String modelProgramBasePath, Double chuSun) {
		if(chuSun!=null) {
			DecimalFormat def = new DecimalFormat("0.##");
			// 创建模型需要参数txt文件
			String filepath = modelProgramBasePath + PropertiesUtil.read("/filePath.properties").getProperty("modelDwxParamFile") + "/chusun.txt";
	        try {
	            //写入的txt文档的路径
	            PrintWriter pw=new PrintWriter(filepath);
	            //写入的内容 封装单位线模型所需的参数文件（初损值）
	            Double chuSunTime = chuSun/6;
	            for (int i = 1; i < 7; i++) {
	            	pw.write(i+"\t"+def.format(chuSunTime)+"\r\n");
				}
	            pw.flush();
	            pw.close();
	        }catch (Exception e) {
	            e.printStackTrace();
	        }
		}
	}
}
