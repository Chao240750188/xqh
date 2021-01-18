package com.essence.business.xqh.web.floodForecast.controller;

import com.essence.business.xqh.api.floodForecast.service.SqybModelInfoService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 洪水预报预警--基础信息控制层
 * @Author huangxiaoli
 * @Description
 * @Date 10:11 2020/12/31
 * @Param
 * @return
 **/
@RestController
@RequestMapping("/model")
public class SqybModelController {

	@Autowired
	private SqybModelInfoService modelInfoService;
	
	/**
	 * 获取模型列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getModelList", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage getModelList() {
		try {
			return SystemSecurityMessage.getSuccessMsg("获取模型列表成功！", modelInfoService.findModelAll());
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("获取模型列表失败！");

		}
	}
	
	/**
	 * 获取水库列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getResList", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage getResList() {
		try {
			return SystemSecurityMessage.getSuccessMsg("获取水库列表成功！", modelInfoService.getAllResInfo());
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("获取水库列表失败！");

		}
	}

	/**
	 * 方案开始计算（调模型计算）
	 * 
	 * @return
	 */
	@RequestMapping(value = "/modelRaun/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage modelRaun(@PathVariable String planId) {
		try {
			return SystemSecurityMessage.getSuccessMsg("模型运算成功！", modelInfoService.modelRaun(planId));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("模型运算失败！");

		}

	}
}
