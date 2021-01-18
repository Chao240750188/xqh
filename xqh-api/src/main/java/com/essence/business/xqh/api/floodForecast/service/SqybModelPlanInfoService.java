package com.essence.business.xqh.api.floodForecast.service;

import com.essence.business.xqh.api.floodForecast.dto.*;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
/**
 * 洪水预报预警--方案信息
 * @Author huangxiaoli
 * @Description
 * @Date 10:47 2020/12/31
 * @Param
 * @return
 **/
public interface SqybModelPlanInfoService {

	/**
	 * 保存模型记录
	 * @param modelPlanInfo
	 */
	SqybModelPlanInfoDto saveModelPlanInfo (SqybModelPlanInfoDto modelPlanInfo);

	/**
	 * 根据方案id查询方案
	 * @param planId
	 */
	SqybModelPlanInfoDto findByPlanId(String planId);

	/**
	 * 分页查询方案列表
	 * @param param
	 * @return
	 */
	Paginator<SqybModelPlanInfoDto> getPlanInfoListPage(PaginatorParam param);

	/**
	 * 删除计算方案
	 * @param planId
	 * @return
	 */
	Object deletePlanInfo(String planId);

	/**
	 * 查询方案降雨条件
	 * @param planId
	 * @return
	 */
	List<RainFallSumDto> getPlanInPutRain(String planId);

	/**
	 * 查询方案蒸发条件
	 * @param planId
	 * @return
	 */
	List<EvaporationSumDto> getPlanInPutEvapor(String planId);

	/**
	 * 查询模型输出结果
	 * @param planId
	 * @return
	 */
	List<RainFallTime> getPlanOutPutRain(String planId);

	/**
	 * LiuGt add at 2020-03-19
	 * 向导入降雨量数据模板文件中设置方案的起始时间和结束时间
	 * @param response
	 * @param planId 方案ID
	 */
	void setRainfallTimeToTemplate(HttpServletResponse response, String planId);

	/**
	 * LiuGt add at 2020-03-17
	 * 向导入蒸发量数据模板文件中设置方案的起始时间和结束时间
	 * @param response
	 * @param planId 方案ID
	 */
	void setEvaporTimeToTemplate(HttpServletResponse response, String planId);

	/**
	 * LiuGt add at 2020-03-18
	 * 查询方案计算结果数据（入库流量）
	 * 返回值添加了实测流量，误差率
	 * @return
	 */
	PlanResultViewDto getPlanResult(String planId);

	/**
	 * 编辑方案模型运行的条件数据
	 * @param modelLoopRun
	 * @return
	 */
	SqybModelLoopRunDto editModelLoopRun(SqybModelLoopRunDto modelLoopRun);

	/**
	 * 查询最新的方案模型运行条件数据
	 * @return
	 */
	SqybModelLoopRunDto queryNewModelLoopRun();

	/**
	 * 设置能启动模型滚动计算条件的降雨数据（仅测试滚动计算使用）
	 * LiuGt add at 2020-05-20
	 * @return
	 */
	String setAutoRunModelRain();

	/**
	 * 查询多个方案对比的结果
	 * @param planIds
	 * @return
	 */
	SystemSecurityMessage queryPlanContrastResult(List<String> planIds);

	/**
	 * 方案结果展示 - 查询指定方案输入数据（各时段平均降雨和累积降雨）
	 * @param planId
	 * @return
	 */
	List<HourTimeDrpListDto> queryPlanInputRainfall(String planId);

	/**
	 * 方案结果展示 - 查询水库预测水位
	 * LiuGt add at 2020-07-09
	 * @return
	 */
	HourTimeWaterLevelViewDto queryResForecastWaterLevel(String planId);
}
