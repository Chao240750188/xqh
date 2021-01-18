package com.essence.business.xqh.api.floodForecast.service;

import com.essence.business.xqh.api.floodForecast.dto.SqybModelInfoDto;
import com.essence.business.xqh.api.floodForecast.dto.SqybResDto;

import java.util.List;

/**
 * 洪水预报预警--基础信息
 * @Author huangxiaoli
 * @Description
 * @Date 10:11 2020/12/31
 * @Param
 * @return
 **/
public interface SqybModelInfoService {

	/**
	 * 查询所有模型列表
	 * @return
	 */
	List<SqybModelInfoDto> findModelAll();

	/**
	 * 调用模型计算
	 * @param planId
	 * @return
	 */
	Object modelRaun(String planId);

	/**
	 * 获取水库列表
	 *
	 * @return
	 */
	public List<SqybResDto> getAllResInfo();
}
