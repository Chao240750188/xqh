package com.essence.business.xqh.service.floodForecast;

import com.essence.business.xqh.api.floodForecast.dto.*;
import com.essence.business.xqh.api.floodForecast.service.SqybModelPlanInfoService;
import com.essence.business.xqh.api.floodForecast.service.SqybStStbprpBService;
import com.essence.business.xqh.api.tuoying.TuoyingInfoService;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.dao.dao.floodForecast.*;
import com.essence.business.xqh.dao.dao.tuoying.TuoyingStPptnRDao;
import com.essence.business.xqh.dao.entity.floodForecast.*;
import com.essence.business.xqh.dao.entity.tuoying.TuoyingStPptnR;
import com.essence.framework.util.DateUtil;
import com.essence.framework.util.StrUtil;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SqybStStbprpBServiceImpl implements SqybStStbprpBService {

	@Autowired
	private SqybModelPlanInfoService modelPlanInfoService;
	@Autowired
	private SqybRelStResDao relStResDao;
	@Autowired
	private SqybStPptnHrDao stPptnHrDao;
	@Autowired
	private SqybStStbprpBDao stStbprpBDao;
	@Autowired
	private SqybHriEvHrDao hriEvHrDao;  //蒸发量小时数据数据访问接口
	@Autowired
	private SqybRainPatternDao rainPatternDao; //雨型图数据访问接口
	@Autowired
	private TuoyingStPptnRDao stPptnRDao;

	@Override
	public List<RainFallSumDto> findRainFallSum(String planId) {
		// 如果缓存中有就从缓存中取否则从数据库取
		List<RainFallSumDto> rainFallSumListCache = (List<RainFallSumDto>) CacheUtil.get("rainFallSum",planId + "ptnHrMap");
		// 存放缓存雨量站小时降雨
		Map<String, Double> rainFallMapCache = null;
		if (rainFallSumListCache != null) {
			rainFallMapCache = new HashMap<>();
			for (RainFallSumDto rainFallSumDto : rainFallSumListCache) {
				String stcd = rainFallSumDto.getStation().getStcd();
				List<RainFallTime> rainList = rainFallSumDto.getRainList();
				for (RainFallTime rainFall : rainList) {
					rainFallMapCache.put(stcd + DateUtil.dateToStringNormal(rainFall.getTime()), rainFall.getDrp());
				}
			}
		}
		// 返回
		List<RainFallSumDto> rainFallSumList = new ArrayList<>();
		// 查询方案基本信息
		SqybModelPlanInfoDto planInfo = modelPlanInfoService.findByPlanId(planId);
		if (planInfo != null) {
			// 关联水库
			String resCode = planInfo.getResCode();
			// 方案起始时间
			Date startTime = planInfo.getStartTime();
			// 方案结束时间
			Date endTime = planInfo.getEndTime();

			// 这里需要考虑预测方案的情况，返回的时段列表不能包括创建方案时未来的时段，在这里处理
			// LiuGt add at 2020-03-24
			// 方案类型（0实时方案，1预测方案）
			if (planInfo.getPlanType().equals("1")){
				//预见期
				String planForesee = planInfo.getPlanForesee();
				int iPlanForesee = planForesee == null || planForesee.equals("") ?
						3 : Integer.valueOf(planForesee).intValue();
				endTime = DateUtil.getNextHour(endTime,  0 -iPlanForesee);
			}

			// 查询水库所属测站（所有类型的测站，包括：雨量站、水文站）
			List<SqybRelStRes> findByResCode = relStResDao.findByResCode(resCode);
			// 过滤出雨量站
			List<SqybRelStRes> rainStcds = findByResCode.stream()
					.filter(stcd->stcd.getSttp().equals("PP"))
					.collect(Collectors.toList());
			List<String> stcdList = new ArrayList<String>();
			for (SqybRelStRes relStRes : rainStcds) {
				stcdList.add(relStRes.getStCode());
			}
			// 查询测站实体
			List<SqybStStbprpB> stbList = stStbprpBDao.findByStcdIn(stcdList);
			Map<String, String> collect = stbList.stream().collect(Collectors.toMap(SqybStStbprpB::getRelevancestcd, SqybStStbprpB::getStcd));

			// 查询小时雨量
			Set<String> htStcdList = collect.keySet();
			List<TuoyingStPptnR> stPptnHrList = stPptnRDao.findByStcdInAndTmBetween(htStcdList, startTime, endTime);

			for (TuoyingStPptnR stPptnHr : stPptnHrList) {
				//获取到本系统数据库的stcd，然后进行替换
				stPptnHr.setStcd(collect.get(stPptnHr.getStcd()));
			}
			// 根据测站封装小时降雨
			Map<String, Double> ptnHrMap = new HashMap<String, Double>();
			for (TuoyingStPptnR stPptnHr : stPptnHrList) {
				ptnHrMap.put(stPptnHr.getStcd() + DateUtil.dateToStringNormal(stPptnHr.getTm()),
						stPptnHr.getDrp() == null ? 0.0 : stPptnHr.getDrp());
			}
			// 封装返回对象
			for (SqybStStbprpB stStbprpB : stbList) {
				// 封装小时雨量
				RainFallSumDto rainFallSumDto = new RainFallSumDto();
				SqybStStbprpBDto stStbprpBDto = new SqybStStbprpBDto();
				BeanUtils.copyProperties(stStbprpB,stStbprpBDto);
				rainFallSumDto.setStation(stStbprpBDto);
				// 时间计数器
				int timeCount = 1;
				// 总雨量
				Double sum = 0.0;
				List<RainFallTime> rainList = new ArrayList<>();
				for (Date time = DateUtil.getNextHour(startTime, 1); time.before(DateUtil.getNextHour(endTime, 1)); time = DateUtil.getNextHour(startTime, timeCount)) {
					Double drp = null;
					// 判断缓存中是否有 如果有就从缓存中取
					if (rainFallMapCache != null) {
						drp = rainFallMapCache.get(stStbprpB.getStcd() + DateUtil.dateToStringNormal(time));
					}
					if (drp == null) {
						// 从数据库取
						drp = ptnHrMap.get(stStbprpB.getStcd() + DateUtil.dateToStringNormal(time));
					}
					drp = (drp == null ? 0.0 : drp);
					RainFallTime rainFallTime = new RainFallTime();
					rainFallTime.setTime(time);
					rainFallTime.setDrp(drp);
					rainList.add(rainFallTime);
					sum += drp;
					timeCount++;
				}
				rainFallSumDto.setRainList(rainList);
				rainFallSumDto.setSumDrp(sum);
				rainFallSumList.add(rainFallSumDto);
			}
			// 将降雨数据放入缓存中
			CacheUtil.saveOrUpdate("rainFallSum", planId + "ptnHrMap", rainFallSumList);
		}
		return rainFallSumList;
	}

	/**
	 * 手工导入降雨量 文件解析存入缓存
	 * @return 
	 */
	@Override
	public List<RainFallSumDto> uploadRainData(MultipartFile mutilpartFile, String planId) {
		// 创建存储封装的行数据的对象
		InputStream inputStream = null;
		try {
			inputStream = mutilpartFile.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String[]> excelList = ExcelUtil.readFiles(mutilpartFile, 1);
		IOUtils.closeQuietly(inputStream);
		// 遍历每行数据（除了标题） 存放同一列的所有行数据
		Map<String, List<String>> stationRainMap = new LinkedHashMap<String, List<String>>();
		// 判断有无数据
		if (excelList != null && excelList.size() > 0) {
			for (int i = 0; i < excelList.size(); i++) {
				String[] strings = excelList.get(i);
				if (strings != null && strings.length > 0) {
					// 封装每列（每个指标项数据）
					for (int j = 0; j < strings.length; j++) {
						List<String> list = stationRainMap.get(j + "");
						if (list == null)
							list = new ArrayList<>();
						list.add(strings[j] + "");
						stationRainMap.put(j + "", list);
					}
				}
			}
		}
		//取出导入文件的时间值
		List<String> timeList = stationRainMap.get("0");
		// 取出所有测站记录
		List<RainFallSumDto> rainFallSumList = new ArrayList<>();
		// 查询方案基本信息
		SqybModelPlanInfoDto planInfo = modelPlanInfoService.findByPlanId(planId);
		if (planInfo != null) {
			// 关联水库
			String resCode = planInfo.getResCode();
			// 方案起始时间
			//Date startTime = DateUtil.getDateByStringNormal(timeList.get(0));
			Date startTime = planInfo.getStartTime();
			// 方案结束时间
			//Date endTime = DateUtil.getDateByStringNormal(timeList.get(timeList.size()-1));
			Date endTime = planInfo.getEndTime();
			// 查询水库所属测站
			List<SqybRelStRes> findByResCode = relStResDao.findByResCode(resCode);
			// 过滤出雨量站
			List<SqybRelStRes> rainStcds = findByResCode.stream()
					.filter(stcd->stcd.getSttp().equals("PP"))
					.collect(Collectors.toList());
			List<String> stcdList = new ArrayList<String>();
			for (SqybRelStRes relStRes : rainStcds) {
				stcdList.add(relStRes.getStCode());
			}
			// 查询测站实体
			List<SqybStStbprpB> stbList = stStbprpBDao.findByStcdIn(stcdList);
			// 封装每个测站小时雨量
			// 查询小时雨量
			List<SqybStPptnHr> stPptnHrList = new ArrayList<>();
			for (SqybStStbprpB stStbprpB : stbList) {
				String stnm = stStbprpB.getStnm();
				if(stnm.contains("小平")) {
					List<String> rainList = stationRainMap.get("1");
					for (int i = 0; i < timeList.size(); i++) {
						SqybStPptnHr stPptnHr = new SqybStPptnHr();
						stPptnHr.setStcd(stStbprpB.getStcd());
						stPptnHr.setTm(DateUtil.getDateByStringNormal(timeList.get(i)));
						stPptnHr.setDrp(Double.parseDouble(StrUtil.isEmpty(rainList.get(i))?"0.0":rainList.get(i)));
						stPptnHrList.add(stPptnHr);
					}
					
				}
				if(stnm.contains("枯强")) {
					List<String> rainList = stationRainMap.get("2");
					for (int i = 0; i < timeList.size(); i++) {
						SqybStPptnHr stPptnHr = new SqybStPptnHr();
						stPptnHr.setStcd(stStbprpB.getStcd());
						stPptnHr.setTm(DateUtil.getDateByStringNormal(timeList.get(i)));
						stPptnHr.setDrp(Double.parseDouble(StrUtil.isEmpty(rainList.get(i))?"0.0":rainList.get(i)));
						stPptnHrList.add(stPptnHr);
					}
					
				}
				if(stnm.contains("潭昔")) {
					List<String> rainList = stationRainMap.get("3");
					for (int i = 0; i < timeList.size(); i++) {
						SqybStPptnHr stPptnHr = new SqybStPptnHr();
						stPptnHr.setStcd(stStbprpB.getStcd());
						stPptnHr.setTm(DateUtil.getDateByStringNormal(timeList.get(i)));
						stPptnHr.setDrp(Double.parseDouble(StrUtil.isEmpty(rainList.get(i))?"0.0":rainList.get(i)));
						stPptnHrList.add(stPptnHr);
					}
					
				}
				if(stnm.contains("那驮")) {
					List<String> rainList = stationRainMap.get("4");
					for (int i = 0; i < timeList.size(); i++) {
						SqybStPptnHr stPptnHr = new SqybStPptnHr();
						stPptnHr.setStcd(stStbprpB.getStcd());
						stPptnHr.setTm(DateUtil.getDateByStringNormal(timeList.get(i)));
						stPptnHr.setDrp(Double.parseDouble(StrUtil.isEmpty(rainList.get(i))?"0.0":rainList.get(i)));
						stPptnHrList.add(stPptnHr);
					}
					
				}
				if(stnm.contains("九特")) {
					List<String> rainList = stationRainMap.get("5");
					for (int i = 0; i < timeList.size(); i++) {
						SqybStPptnHr stPptnHr = new SqybStPptnHr();
						stPptnHr.setStcd(stStbprpB.getStcd());
						stPptnHr.setTm(DateUtil.getDateByStringNormal(timeList.get(i)));
						stPptnHr.setDrp(Double.parseDouble(StrUtil.isEmpty(rainList.get(i))?"0.0":rainList.get(i)));
						stPptnHrList.add(stPptnHr);
					}
					
				}
				if(stnm.contains("板固")) {
					List<String> rainList = stationRainMap.get("6");
					for (int i = 0; i < timeList.size(); i++) {
						SqybStPptnHr stPptnHr = new SqybStPptnHr();
						stPptnHr.setStcd(stStbprpB.getStcd());
						stPptnHr.setTm(DateUtil.getDateByStringNormal(timeList.get(i)));
						stPptnHr.setDrp(Double.parseDouble(StrUtil.isEmpty(rainList.get(i))?"0.0":rainList.get(i)));
						stPptnHrList.add(stPptnHr);
					}
					
				}
				if(stnm.contains("叫弄")) {
					List<String> rainList = stationRainMap.get("7");
					for (int i = 0; i < timeList.size(); i++) {
						SqybStPptnHr stPptnHr = new SqybStPptnHr();
						stPptnHr.setStcd(stStbprpB.getStcd());
						stPptnHr.setTm(DateUtil.getDateByStringNormal(timeList.get(i)));
						stPptnHr.setDrp(Double.parseDouble(StrUtil.isEmpty(rainList.get(i))?"0.0":rainList.get(i)));
						stPptnHrList.add(stPptnHr);
					}
				}
				
			}
			// 根据测站封装小时降雨
			Map<String, Double> ptnHrMap = new HashMap<String, Double>();
			for (SqybStPptnHr stPptnHr : stPptnHrList) {
				ptnHrMap.put(stPptnHr.getStcd() + DateUtil.dateToStringNormal(stPptnHr.getTm()),
						stPptnHr.getDrp() == null ? 0.0 : stPptnHr.getDrp());
			}
			// 封装返回对象
			for (SqybStStbprpB stStbprpB : stbList) {
				// 封装小时雨量
				RainFallSumDto rainFallSumDto = new RainFallSumDto();
				SqybStStbprpBDto stStbprpBDto = new SqybStStbprpBDto();
				BeanUtils.copyProperties(stStbprpB,stStbprpBDto);
				rainFallSumDto.setStation(stStbprpBDto);
				// 时间计数器
				int timeCount = 1;
				// 总雨量
				Double sum = 0.0;
				List<RainFallTime> rainList = new ArrayList<>();
				for (Date time = DateUtil.getNextHour(startTime, 1); time.before(DateUtil.getNextHour(endTime, 1)); time = DateUtil.getNextHour(startTime, timeCount)) {
					// 从导入文件数据取
					Double drp = ptnHrMap.get(stStbprpB.getStcd() + DateUtil.dateToStringNormal(time));
					drp = (drp == null ? 0.0 : drp);
					RainFallTime rainFallTime = new RainFallTime();
					rainFallTime.setTime(time);
					rainFallTime.setDrp(drp);
					rainList.add(rainFallTime);
					sum += drp;
					timeCount++;
				}
				rainFallSumDto.setRainList(rainList);
				rainFallSumDto.setSumDrp(sum);
				rainFallSumList.add(rainFallSumDto);
			}
			// 将降雨数据放入缓存中
			CacheUtil.saveOrUpdate("rainFallSum", planId + "ptnHrMap", rainFallSumList);
		}
		return rainFallSumList;
	}

	@Override
	public Object updatePlanRainFall(PlanRainFallDto pRainFallDto) {
		String planId = pRainFallDto.getPlanId();
		// 将降雨数据从缓存中取
		@SuppressWarnings("unchecked")
		List<RainFallSumDto> rainFallSumList = (List<RainFallSumDto>) CacheUtil.get("rainFallSum", planId + "ptnHrMap");
		if (rainFallSumList != null) {
			RainFallSumDto rainFallSumDtos = null;
			for (RainFallSumDto rainFallSumDto : rainFallSumList) {
				if (pRainFallDto.getStcd().equals(rainFallSumDto.getStation().getStcd())) {
					rainFallSumDtos = rainFallSumDto;
					break;
				}
			}
			List<RainFallTime> rainList = rainFallSumDtos.getRainList();
			for (RainFallTime rainFall : rainList) {
				if (rainFall.getTime().getTime() == pRainFallDto.getTime().getTime()) {
					rainFall.setDrp(pRainFallDto.getDrp());
				}
			}
		}
		CacheUtil.saveOrUpdate("rainFallSum", planId + "ptnHrMap", rainFallSumList);
		return null;
	}

	/**
	 * 查询方案的蒸发量数据
	 * @param planId
	 * @return
	 */
	@Override
	public List<EvaporationSumDto> findEvaporationSum(String planId){
		// 如果缓存中有就从缓存中取否则从数据库取
		List<EvaporationSumDto> evaporationSumListCache = (List<EvaporationSumDto>) CacheUtil.get("evaporationSum",planId + "evHrMap");
		// 存放缓存水文站小时蒸发量
		Map<String, Double> evaporationMapCache = null;
		if (evaporationSumListCache != null) {
			evaporationMapCache = new HashMap<>();
			for (EvaporationSumDto evaporationSumDto : evaporationSumListCache) {
				String stcd = evaporationSumDto.getStation().getStcd();
				List<EvaporationTimeDto> evaporationList = evaporationSumDto.getEvaporationList();
				for (EvaporationTimeDto evaporationTimeDto : evaporationList) {
					evaporationMapCache.put(stcd + evaporationTimeDto.getTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), evaporationTimeDto.getEvp());
				}
			}
		}
		// 返回
		List<EvaporationSumDto> evaporationSumList = new ArrayList<>();
		// 查询方案基本信息
		SqybModelPlanInfoDto planInfo = modelPlanInfoService.findByPlanId(planId);
		if (planInfo != null) {
			// 关联水库
			String resCode = planInfo.getResCode();
			// 方案起始时间
			Date startTime = planInfo.getStartTime();
			// 方案结束时间
			Date endTime = planInfo.getEndTime();

			// 这里需要考虑预测方案的情况，返回的时段列表不能包括创建方案时未来的时段，在这里处理
			// LiuGt add at 2020-03-24
			// 方案类型（0实时方案，1预测方案）
			if (planInfo.getPlanType().equals("1")){
				//预见期
				String planForesee = planInfo.getPlanForesee();
				int iPlanForesee = planForesee == null || planForesee.equals("") ?
						3 : Integer.valueOf(planForesee).intValue();
				endTime = DateUtil.getNextHour(endTime,  0 -iPlanForesee);
			}

			// 查询水库所属测站（所有类型的测站，包括：雨量站、水文站）
			List<SqybRelStRes> findByResCode = relStResDao.findByResCode(resCode);
			// 过滤出水文站
			List<SqybRelStRes> evaporStcds = findByResCode.stream()
					.filter(stcd->stcd.getSttp().equals("DD"))
					.collect(Collectors.toList());
			//从测站对象列表中提取测站ID列
			/*List<String> stcdList = new ArrayList<String>();
			for (RelStRes relStRes : evaporStcds) {
				stcdList.add(relStRes.getStCode());
			}*/
			List<String> stcdList = evaporStcds.stream().map(SqybRelStRes::getStCode).collect(Collectors.toList());
			// 查询测站实体
			List<SqybStStbprpB> stbList = stStbprpBDao.findByStcdIn(stcdList);
			// 查询小时蒸发量
			List<SqybHriEvHr> hriEvHrList = hriEvHrDao.findByStcdInAndTmBetween(stcdList,
					startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
					endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
			// 根据测站封装小时蒸发量数据
			Map<String, Double> evHrMap = new HashMap<String, Double>();
			for (SqybHriEvHr evHr : hriEvHrList) {
				evHrMap.put(evHr.getStcd() + evHr.getTm().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
						evHr.getDre() == null ? 0.0 : evHr.getDre());
			}
			// 封装返回对象
			for (SqybStStbprpB stStbprpB : stbList) {
				// 封装小时蒸发量
				EvaporationSumDto evaporationSumDto = new EvaporationSumDto();
				SqybStStbprpBDto stStbprpBDto = new SqybStStbprpBDto();
				BeanUtils.copyProperties(stStbprpB,stStbprpBDto);
				evaporationSumDto.setStation(stStbprpBDto);
				// 时间计数器
				int timeCount = 1;
				// 总蒸发量
				Double sum = 0.0;
				List<EvaporationTimeDto> evaporationList = new ArrayList<>();
				for (Date time = DateUtil.getNextHour(startTime, 1); time.before(DateUtil.getNextHour(endTime, 1)); time = DateUtil.getNextHour(startTime, timeCount)) {
					Double dre = null;
					// 判断缓存中是否有 如果有就从缓存中取
					if (evaporationMapCache != null) {
						dre = evaporationMapCache.get(stStbprpB.getStcd() + DateUtil.dateToStringNormal(time));
					}
					if (dre == null) {
						// 从数据库取
						dre = evHrMap.get(stStbprpB.getStcd() + DateUtil.dateToStringNormal(time));
					}
					dre = (dre == null ? 0.0 : dre);
					EvaporationTimeDto evaporationTimeDto = new EvaporationTimeDto();
					evaporationTimeDto.setTime(time.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
					evaporationTimeDto.setEvp(dre);
					evaporationList.add(evaporationTimeDto);
					sum += dre;
					timeCount++;
				}
				evaporationSumDto.setEvaporationList(evaporationList);
				evaporationSumDto.setSumDrp(sum);
				evaporationSumList.add(evaporationSumDto);
			}
			// 将蒸发数据放入缓存中
			CacheUtil.saveOrUpdate("evaporationSum", planId + "evHrMap", evaporationSumList);
		}
		return evaporationSumList;
	}

	/**
	 * 手工导入蒸发量数据
	 * @param mutilpartFile
	 * @param planId
	 */
	@Override
	public List<EvaporationSumDto> uploadEvaporData(MultipartFile mutilpartFile, String planId){
		//返回数据
		List<EvaporationSumDto> evaporationSumDtoList = new ArrayList<>();

		//获取模板文件中的数据
		List<EvaporExcelDataDto> evaporExcelDataDtoList = readEvaporDataFromExcelFiles(mutilpartFile);
		if (evaporExcelDataDtoList == null || evaporExcelDataDtoList.size() <= 0){
			return null;
		}
		// 查询方案基本信息
		SqybModelPlanInfoDto planInfo = modelPlanInfoService.findByPlanId(planId);
		if (planInfo == null){
			return null;
		}
		// 查询水库所属测站
		List<SqybRelStRes> findByResCode = relStResDao.findByResCode(planInfo.getResCode());
		// 过滤出水文站
		List<SqybRelStRes> rainStcds = findByResCode.stream().filter(stcd->stcd.getSttp().equals("DD"))
				.collect(Collectors.toList());
		List<String> stcdList = rainStcds.stream().map(SqybRelStRes::getStCode).collect(Collectors.toList());
		// 查询测站实体
		List<SqybStStbprpB> stbList = stStbprpBDao.findByStcdIn(stcdList);
		for(SqybStStbprpB stStbprpB : stbList){
			EvaporationSumDto evaporationSumDto = new EvaporationSumDto();
			SqybStStbprpBDto stStbprpBDto = new SqybStStbprpBDto();
			BeanUtils.copyProperties(stStbprpB,stStbprpBDto);
			evaporationSumDto.setStation(stStbprpBDto);
			evaporationSumDto.setSumDrp(new Double(0.0));
			String stnm = stStbprpB.getStnm();
			EvaporExcelDataDto evaporExcelDataDto = evaporExcelDataDtoList.stream().filter(evapor->evapor.getStnm().equals(stnm))
					.collect(Collectors.toList()).get(0);
			if (evaporExcelDataDto != null){
				List<EvaporationTimeDto> evaporationList = new ArrayList<>();
				evaporExcelDataDto.getEvaporMap().forEach((k,v)->{
					if (k == null || k.equals("")){
						return; // 等同continue
					}
					EvaporationTimeDto evaporationTimeDto = new EvaporationTimeDto();
					evaporationTimeDto.setTime(LocalDateTime.parse(k,DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
					evaporationTimeDto.setEvp(v);
					evaporationSumDto.setSumDrp(evaporationSumDto.getSumDrp() + v);
					evaporationList.add(evaporationTimeDto);
				});
				evaporationSumDto.setEvaporationList(evaporationList);
			}
			evaporationSumDtoList.add(evaporationSumDto);
		}
		// 将蒸发数据放入缓存中
		CacheUtil.saveOrUpdate("evaporationSum", planId + "evHrMap", evaporationSumDtoList);
		return evaporationSumDtoList;
	}

	private List<EvaporExcelDataDto> readEvaporDataFromExcelFiles(MultipartFile mutilpartFile){
		//返回数据
		List<EvaporExcelDataDto> evaporExcelDataDtoList = new ArrayList<>();
		// 获得Workbook工作薄对象
		Workbook workbook = ExcelUtil.getWorkBook(mutilpartFile);
		if (workbook == null){
			return evaporExcelDataDtoList;
		}
		if (workbook.getNumberOfSheets() <= 0){
			return evaporExcelDataDtoList;
		}
		// 获得第一个sheet工作表
		Sheet sheet = workbook.getSheetAt(0);
		// 获得第一个sheet的开始行
		int firstRowNum = sheet.getFirstRowNum();
		// 获得第一个sheet的结束行
		int lastRowNum = sheet.getLastRowNum();

		// 获取第一行字段名
		Row firstRow = sheet.getRow(firstRowNum);
		int firstCellNum = firstRow.getFirstCellNum();
		int lastCellNum = firstRow.getLastCellNum();

		for (int cellNum = 1; cellNum < lastCellNum; cellNum++){
			EvaporExcelDataDto evaporExcelDataDto = new EvaporExcelDataDto();
			//获取测站名称
			evaporExcelDataDto.setStnm(ExcelUtil.getCellValue(firstRow.getCell(cellNum)));
			Map<String, Double> evaporMap = new LinkedHashMap<>();
			for (int rowNum = 1; rowNum <= lastRowNum; rowNum++){
				Row dataRow = sheet.getRow(rowNum);
				String tm = ExcelUtil.getCellValue(dataRow.getCell(0));
				Double evapor = new Double(0.0);
				try{
					String strEvapor = ExcelUtil.getCellValue(dataRow.getCell(cellNum));
					evapor = strEvapor == null || strEvapor.equals("") ? new Double(0.0) : new Double(strEvapor);
				}
				catch (Exception e){}
				evaporMap.put(tm, evapor);
			}
			evaporExcelDataDto.setEvaporMap(evaporMap);
			evaporExcelDataDtoList.add(evaporExcelDataDto);
		}

		return evaporExcelDataDtoList;
	}

	/**
	 * 修改雨量站小时蒸发量
	 * @param planEvaporationDto
	 * @return
	 */
	@Override
	public Object updatePlanEvapor(PlanEvaporationDto planEvaporationDto){
		String planId = planEvaporationDto.getPlanId();
		// 将蒸发量数据从缓存中取
		@SuppressWarnings("unchecked")
		List<EvaporationSumDto> evaporationSumList = (List<EvaporationSumDto>) CacheUtil.get("evaporationSum", planId + "evHrMap");
		if (evaporationSumList != null) {
			//根据测站ID过滤
			List<EvaporationSumDto> subEvaporationSumList = evaporationSumList.stream()
					.filter(evaporSum->evaporSum.getStation().getStcd().equals(planEvaporationDto.getStcd()))
					.collect(Collectors.toList());
			EvaporationSumDto evaporationSumDto = subEvaporationSumList == null || subEvaporationSumList.size() <= 0 ?
					null : subEvaporationSumList.get(0);
			if (evaporationSumDto != null){
				//获取测站的时段蒸发量数据，根据时段，来更新修改的数据
				String tm = planEvaporationDto.getTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
				List<EvaporationTimeDto> evaporationList = evaporationSumDto.getEvaporationList();
				for (EvaporationTimeDto evaporationTimeDto : evaporationList) {
					if (evaporationTimeDto.getTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")).equals(tm)) {
						evaporationTimeDto.setEvp(planEvaporationDto.getDre());
					}
				}
			}
		}
		//更新到缓存中去
		CacheUtil.saveOrUpdate("evaporationSum", planId + "evHrMap", evaporationSumList);
		return null;
	}

	/**
	 * 查询计算方案预见期各时段的降雨量
	 * LiuGt add at 2020-03-23
	 * @param planId 方案ID
	 * @return
	 */
	@Override
	public List<RainFallTime> queryForeseeRainFall(String planId){
		//先从缓存中取预见期各时段的降雨量
		List<RainFallTime> rainFallTimeList = (List<RainFallTime>) CacheUtil.get("foreseeRainFall", planId + "ForeseeRainList");
		if (rainFallTimeList != null && rainFallTimeList.size() >= 0){
			return rainFallTimeList;
		}
		rainFallTimeList = new ArrayList<>();
		//如果缓存中没有，则从数据库中取雨型图进行计算
		try{
			SqybModelPlanInfoDto modelPlanInfo = modelPlanInfoService.findByPlanId(planId);
			//判断是否为预测方案
			if (modelPlanInfo.getPlanType().equals("1")){
				//获取相应的值
				String rainType = modelPlanInfo.getRainType();		//雨型类型
				String planForesee = modelPlanInfo.getPlanForesee();//预见期
				BigDecimal totalRain = modelPlanInfo.getPlanForeseeTotalRain(); //预见期总降雨量
				//获取雨型图数据
				List<SqybRainPattern> rainPatternList = rainPatternDao.findByTimeAndType(Integer.valueOf(planForesee), rainType);
				if (rainPatternList == null || rainPatternList.size() <= 0){
					return rainFallTimeList;
				}
				LocalDateTime endTime = modelPlanInfo.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				endTime = endTime.plusHours(1);
				LocalDateTime startTime = endTime.plusHours(0-Long.valueOf(planForesee).longValue());
				for (SqybRainPattern rainPattern : rainPatternList) {
					RainFallTime rainFallTime = new RainFallTime();
					//时段
					rainFallTime.setTime(Date.from(startTime.atZone(ZoneId.systemDefault()).toInstant()));
					Double proportion = rainPattern.getProportion();
					Double hourRain = totalRain.doubleValue() * proportion;
					//降雨量
					rainFallTime.setDrp(new BigDecimal(hourRain).setScale(2, RoundingMode.HALF_UP).doubleValue());
					startTime = startTime.plusHours(1);
					rainFallTimeList.add(rainFallTime);
				}
			}
			else{
				return rainFallTimeList;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		//更新到缓存中去
		CacheUtil.saveOrUpdate("foreseeRainFall", planId + "ForeseeRainList", rainFallTimeList);
		return rainFallTimeList;
	}

	/**
	 * 更新计算方案预见期各时段的降雨量
	 * LiuGt add at 2020-03-23
	 * @param planRainFallDto 时段降雨量实体实例
	 * @return
	 */
	@Override
	public Object updateForeseeRainFall(PlanRainFallDto planRainFallDto){
		//先从缓存中取预见期各时段的降雨量
		List<RainFallTime> rainFallTimeList = (List<RainFallTime>) CacheUtil.get("foreseeRainFall", planRainFallDto.getPlanId() + "ForeseeRainList");
		if (rainFallTimeList == null || rainFallTimeList.size() <= 0){
			return null;
		}
		for (RainFallTime rainFallTime : rainFallTimeList) {
			if (DateUtil.dateToStringWithFormat(rainFallTime.getTime(),"yyyyMMddHHmmss").equals(DateUtil.dateToStringWithFormat(planRainFallDto.getTime(),"yyyyMMddHHmmss"))){
				rainFallTime.setDrp(planRainFallDto.getDrp());
			}
		}
		//更新到缓存中去
		CacheUtil.saveOrUpdate("foreseeRainFall", planRainFallDto.getPlanId() + "ForeseeRainList", rainFallTimeList);
		return null;
	}

	/**
	 * 查询计算方案预见期各时段的蒸发量
	 * LiuGt add at 2020-03-24
	 * @param planId 方案ID
	 * @return
	 */
	@Override
	public List<EvaporationTimeDto> queryForeseeEvaporation(String planId){
		//先从缓存中取预见期各时段的蒸发量
		List<EvaporationTimeDto> evaporTimeList = (List<EvaporationTimeDto>) CacheUtil.get("foreseeEvaporation", planId + "ForeseeEvaporList");
		if (evaporTimeList != null && evaporTimeList.size() >= 0){
			return evaporTimeList;
		}
		evaporTimeList = new ArrayList<>();
		//如果缓存中没有，则从数据库中取雨型图进行计算
		try{
			SqybModelPlanInfoDto modelPlanInfo = modelPlanInfoService.findByPlanId(planId);
			//判断是否为预测方案
			if (modelPlanInfo.getPlanType().equals("1")){
				//获取相应的值
				String planForesee = modelPlanInfo.getPlanForesee();//预见期
				//获取预先定义好的各时段预测蒸发量
				Map<String, Double> tmEvaporMap = getStaticFpreseeEvaporMap();
				//为方案的预测时段赋值蒸发量
				LocalDateTime endTime = modelPlanInfo.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
				endTime = endTime.plusHours(1);
				LocalDateTime startTime = endTime.plusHours(0-Long.valueOf(planForesee).longValue());
				while (startTime.isBefore(endTime)){
					EvaporationTimeDto evaporationTimeDto = new EvaporationTimeDto();
					//时段
					evaporationTimeDto.setTime(startTime);
					//蒸发量
					Double hourEvapor = tmEvaporMap.get(startTime.format(DateTimeFormatter.ofPattern("HH:mm")));
					evaporationTimeDto.setEvp(hourEvapor);
					startTime = startTime.plusHours(1);
					evaporTimeList.add(evaporationTimeDto);
				}
			}
			else{
				return evaporTimeList;
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		//更新到缓存中去
		CacheUtil.saveOrUpdate("foreseeEvaporation", planId + "ForeseeEvaporList", evaporTimeList);
		return evaporTimeList;
	}

	/**
	 * 预测各时段的蒸发量静态值
	 * LiuGt add at 2020-03-24
	 * @return
	 */
	private Map<String,Double> getStaticFpreseeEvaporMap(){
		Map<String, Double> tmEvaporMap = new LinkedHashMap<>();
		tmEvaporMap.put("01:00", new Double(0));
		tmEvaporMap.put("02:00", new Double(0));
		tmEvaporMap.put("03:00", new Double(0));
		tmEvaporMap.put("04:00", new Double(0));
		tmEvaporMap.put("05:00", new Double(0));
		tmEvaporMap.put("06:00", new Double(0));
		tmEvaporMap.put("07:00", new Double(0));
		tmEvaporMap.put("08:00", new Double(0));
		tmEvaporMap.put("09:00", new Double(0.2));
		tmEvaporMap.put("10:00", new Double(0.2));
		tmEvaporMap.put("11:00", new Double(0.4));
		tmEvaporMap.put("12:00", new Double(0.4));
		tmEvaporMap.put("13:00", new Double(0.5));
		tmEvaporMap.put("14:00", new Double(0.5));
		tmEvaporMap.put("15:00", new Double(0.5));
		tmEvaporMap.put("16:00", new Double(0.2));
		tmEvaporMap.put("17:00", new Double(0.2));
		tmEvaporMap.put("18:00", new Double(0.2));
		tmEvaporMap.put("19:00", new Double(0));
		tmEvaporMap.put("20:00", new Double(0));
		tmEvaporMap.put("21:00", new Double(0));
		tmEvaporMap.put("22:00", new Double(0));
		tmEvaporMap.put("23:00", new Double(0));
		tmEvaporMap.put("00:00", new Double(0));
		return tmEvaporMap;
	}

	/**
	 * 更新计算方案预见期各时段的蒸发量
	 * LiuGt add at 2020-03-24
	 * @param planEvaporationDto 时段蒸发量实体实例
	 * @return
	 */
	@Override
	public Object updateForeseeEvaporation(PlanEvaporationDto planEvaporationDto){
		//先从缓存中取预见期各时段的蒸发量
		List<EvaporationTimeDto> evaporationTimeDtoList = (List<EvaporationTimeDto>) CacheUtil.get("foreseeEvaporation", planEvaporationDto.getPlanId() + "ForeseeEvaporList");
		if (evaporationTimeDtoList == null || evaporationTimeDtoList.size() <= 0){
			return null;
		}
		for (EvaporationTimeDto evaporationTimeDto : evaporationTimeDtoList) {
			String currentTm = evaporationTimeDto.getTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			String updateTm = planEvaporationDto.getTime().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
			if (currentTm.equals(updateTm)){
				evaporationTimeDto.setEvp(planEvaporationDto.getDre());
			}
		}
		//更新到缓存中去
		CacheUtil.saveOrUpdate("foreseeEvaporation", planEvaporationDto.getPlanId() + "ForeseeEvaporList", evaporationTimeDtoList);
		return null;
	}

}
