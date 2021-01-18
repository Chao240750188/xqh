package com.essence.business.xqh.api.floodForecast.service;


import com.essence.business.xqh.api.floodForecast.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
/**
 *  洪水预报预警
 * @Author huangxiaoli
 * @Description
 * @Date 10:47 2020/12/31
 * @Param
 * @return
 **/
public interface SqybStStbprpBService {

	/**
	 * 根据方案计算
	 * @param planId
	 * @return
	 */
	List<RainFallSumDto> findRainFallSum(String planId);

	/**
	 * 修改雨量站小时雨量
	 * @param planRainFallDto
	 * @return
	 */
	Object updatePlanRainFall(PlanRainFallDto planRainFallDto);

	/**
	 * 手工导入雨量数据
	 * @param mutilpartFile
	 * @param planId
	 */
	List<RainFallSumDto> uploadRainData(MultipartFile mutilpartFile, String planId);

	/**
	 * 查询方案的蒸发量数据
	 * @param planId
	 * @return
	 */
	List<EvaporationSumDto> findEvaporationSum(String planId);

	/**
	 * 手工导入蒸发量数据
	 * @param mutilpartFile
	 * @param planId
	 */
	List<EvaporationSumDto> uploadEvaporData(MultipartFile mutilpartFile, String planId);

	/**
	 * 修改雨量站小时蒸发量
	 * @param planEvaporationDto
	 * @return
	 */
	Object updatePlanEvapor(PlanEvaporationDto planEvaporationDto);

	/**
	 * 查询计算方案预见期各时段的降雨量
	 * LiuGt add at 2020-03-23
	 * @param planId 方案ID
	 * @return
	 */
	List<RainFallTime> queryForeseeRainFall(String planId);

	/**
	 * 更新计算方案预见期各时段的降雨量
	 * LiuGt add at 2020-03-23
	 * @param planRainFallDto 时段降雨量实体实例
	 * @return
	 */
	Object updateForeseeRainFall(PlanRainFallDto planRainFallDto);

	/**
	 * 查询计算方案预见期各时段的蒸发量
	 * LiuGt add at 2020-03-24
	 * @param planId 方案ID
	 * @return
	 */
	List<EvaporationTimeDto> queryForeseeEvaporation(String planId);

	/**
	 * 更新计算方案预见期各时段的蒸发量
	 * LiuGt add at 2020-03-24
	 * @param planEvaporationDto 时段蒸发量实体实例
	 * @return
	 */
	Object updateForeseeEvaporation(PlanEvaporationDto planEvaporationDto);
}
