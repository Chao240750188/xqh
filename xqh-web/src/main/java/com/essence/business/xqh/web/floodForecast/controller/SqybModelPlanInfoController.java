package com.essence.business.xqh.web.floodForecast.controller;

import com.essence.business.xqh.api.floodForecast.dto.*;
import com.essence.business.xqh.api.floodForecast.service.SqybModelPlanInfoService;
import com.essence.business.xqh.api.floodForecast.service.SqybStStbprpBService;
import com.essence.business.xqh.api.tuoying.TuoyingInfoService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 洪水预报预警--水情预报模型控制层
 * 
 * @author NoBugNoCode
 *
 *         2019年10月24日 下午5:46:12
 */
@RestController
@RequestMapping("/modelPlanInfo")
public class SqybModelPlanInfoController {

	@Autowired
	private SqybModelPlanInfoService modelPlanInfoService;
	@Autowired
	private SqybStStbprpBService stStbprpBService;
	@Autowired
	private TuoyingInfoService sqybTuoyingInfoService;
	/**
	 * 查询水文站、雨量站
	 * @param
	 * @return
	 */
	@RequestMapping(value = "/getStcdToMapIcon", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage getStcdToMapIcon() {
		try {
			List<StcdInfoToMapIconViewDto> stcdToMapIcon = sqybTuoyingInfoService.getStcdToMapIcon();
			return SystemSecurityMessage.getSuccessMsg("查询成功！",stcdToMapIcon);
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询失败!");
		}

	}

	/**
	 * 保存水情预报模型运行方案信息
	 * 
	 * @return
	 */
	@RequestMapping(value = "/saveModelPlanInfo", method = RequestMethod.POST)
	public @ResponseBody
	SystemSecurityMessage getModelList(@RequestBody SqybModelPlanInfoDto modelPlanInfo) {
		try {
			return SystemSecurityMessage.getSuccessMsg("方案信息保存成功！", modelPlanInfoService.saveModelPlanInfo(modelPlanInfo));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("案信息保存失败!");
		}
	}

	/**
	 * 下载手工导入数据模板(降雨量)
	 * 
	 * @return
	 */
	@RequestMapping(value = "/downRainTemplate/{planId}", method = RequestMethod.GET)
	public @ResponseBody void downRainTemplate(HttpServletResponse response, HttpServletRequest request, @PathVariable String planId) throws Exception {
		/*// 定义一个数据格式化对象
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(
					new FileInputStream(PropertiesUtil.read("/filePath.properties").getProperty("modelRainTemPath")));
            modelPlanInfoService.setEvaporTimeToTemplate(wb, planId);
			// 响应尾
			response.setContentType("applicationnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String fileName = "rain_template_"+planId+".xlsx";
			response.setHeader("Content-disposition",
					"attachment;filename=" + new String(fileName.getBytes(), "iso_8859_1"));
			OutputStream ouputStream = response.getOutputStream();
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		modelPlanInfoService.setRainfallTimeToTemplate(response, planId);
	}

	//region 降雨量

	/**
	 * 查询计算方案雨量站雨量(包括总雨量和小时雨量)
	 * 
	 * @return
	 */
	@RequestMapping(value = "/findRainFallSum/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage findRainFallSum(@PathVariable String planId) {
		try {
			return SystemSecurityMessage.getSuccessMsg("查询计算方案雨量站雨量成功！", stStbprpBService.findRainFallSum(planId));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询计算方案雨量站雨量失败!");
		}

	}
	
	/**
	 * 手工上传雨量数据
	 * 
	 * @return SystemSecurityMessage 返回结果json
	 */
	@ResponseBody
	@RequestMapping(value = "/uploadRainData/{planId}", method = RequestMethod.POST)
	public SystemSecurityMessage uploadRainData(@PathVariable String planId,@RequestParam(value = "files", required = true) MultipartFile mutilpartFile) {
		SystemSecurityMessage SystemSecurityMessage = null;
			try {
				stStbprpBService.uploadRainData(mutilpartFile,planId);
				SystemSecurityMessage = new SystemSecurityMessage("ok", "降雨量数据上传解析成功!", null);
			} catch (Exception e) {
				String eMessage = "";
				if (e != null) {
					eMessage = e.getMessage();
				}
				SystemSecurityMessage = new SystemSecurityMessage("error", "降雨量数据上传解析失败，错误原因：" + eMessage, null);
			}
		return SystemSecurityMessage;
	}
	

	/**
	 * 修改方案计算测站小时雨量值
	 * 
	 * @return
	 */
	@RequestMapping(value = "/updatePlanRainFall", method = RequestMethod.POST)
	public @ResponseBody SystemSecurityMessage updatePlanRainFall(@RequestBody PlanRainFallDto planRainFallDto) {
		try {
			return SystemSecurityMessage.getSuccessMsg("修改测站小时雨量成功！", stStbprpBService.updatePlanRainFall(planRainFallDto));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询计算方案雨量站雨量失败!");
		}

	}

    /**
     * 查询计算方案预见期的时段降雨量
     * LiuGt add at 2020-03-23
     *
     * @return
     */
    @RequestMapping(value = "/queryForeseeRainFall/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage queryForeseeRainFall(@PathVariable String planId){
		try {
			return SystemSecurityMessage.getSuccessMsg("查询计算方案预见期时段降雨量成功！", stStbprpBService.queryForeseeRainFall(planId));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询计算方案雨量站雨量失败!");
		}


    }

    /**
     * 修改方案计算预见期的时段降雨量
     *
     * @return
     */
    @RequestMapping(value = "/updateForeseeRainFall", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage updateForeseeRainFall(@RequestBody PlanRainFallDto planRainFallDto) {
		try {
			return SystemSecurityMessage.getSuccessMsg("修改预见期降雨量成功！", stStbprpBService.updateForeseeRainFall(planRainFallDto));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("修改预见期降雨量失败!");
		}

    }

	//endregion

	//region 蒸发量

	/**
	 * 查询计算方案水文站蒸发量(包括总蒸发量和小时蒸发量)
	 *
	 * @return
	 */
	@RequestMapping(value = "/findEvaporationSum/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage findEvaporationSum(@PathVariable String planId) {
		try {
			return SystemSecurityMessage.getSuccessMsg("查询计算方案水文站蒸发量成功！", stStbprpBService.findEvaporationSum(planId));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询计算方案水文站蒸发量失败!");
		}


	}


	/**
	 * 下载手工导入数据模板(蒸发量)
	 *
	 * @return
	 */
	@RequestMapping(value = "/downEvaporTemplate/{planId}", method = RequestMethod.GET)
	public @ResponseBody void downEvaporTemplate(@PathVariable String planId, HttpServletRequest request, HttpServletResponse response)	throws Exception {
		/*// 定义一个数据格式化对象
		XSSFWorkbook wb = null;
		try {
			wb = new XSSFWorkbook(
					new FileInputStream(PropertiesUtil.read("/filePath.properties").getProperty("modelEvaporTemPath")));
			modelPlanInfoService.setEvaporTimeToTemplate(wb, planId);
			// 响应尾
			response.setContentType("applicationnd.openxmlformats-officedocument.spreadsheetml.sheet");
			String fileName = "evaporation_template.xlsx";
			response.setHeader("Content-disposition",
					"attachment;filename=" + new String(fileName.getBytes(), "iso_8859_1"));
			OutputStream ouputStream = response.getOutputStream();
			wb.write(ouputStream);
			ouputStream.flush();
			ouputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		modelPlanInfoService.setEvaporTimeToTemplate(response, planId);
	}

	/**
	 * 手工上传蒸发量数据
	 *
	 * @return SystemSecurityMessage 返回结果json
	 */
	@ResponseBody
	@RequestMapping(value = "/uploadEvaporData/{planId}", method = RequestMethod.POST)
	public SystemSecurityMessage uploadEvaporData(@PathVariable String planId,@RequestParam(value = "files", required = true) MultipartFile mutilpartFile) {
		SystemSecurityMessage SystemSecurityMessage = null;
		try {
			stStbprpBService.uploadEvaporData(mutilpartFile,planId);
			SystemSecurityMessage = new SystemSecurityMessage("ok", "蒸发量数据上传解析成功!", null);
		} catch (Exception e) {
			String eMessage = "";
			if (e != null) {
				eMessage = e.getMessage();
			}
			SystemSecurityMessage = new SystemSecurityMessage("error", "蒸发量数据上传解析失败，错误原因：" + eMessage, null);
		}
		return SystemSecurityMessage;
	}

	/**
	 * 手工修改方案计算测站小时蒸发量值
	 *
	 * @return
	 */
	@RequestMapping(value = "/updatePlanEvapor", method = RequestMethod.POST)
	public @ResponseBody SystemSecurityMessage updatePlanEvapor(@RequestBody PlanEvaporationDto planEvaporationDto) {
		try {
			return SystemSecurityMessage.getSuccessMsg("修改测站小时蒸发量成功！",
					stStbprpBService.updatePlanEvapor(planEvaporationDto));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("修改测站小时蒸发量失败!");
		}


	}

    /**
     * 查询计算方案预见期的时段蒸发量
     * LiuGt add at 2020-03-24
     *
     * @return
     */
    @RequestMapping(value = "/queryForeseeEvaporation/{planId}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage queryForeseeEvaporation(@PathVariable String planId){
		try {
			return SystemSecurityMessage.getSuccessMsg("查询计算方案预见期时段蒸发量成功！", stStbprpBService.queryForeseeEvaporation(planId));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询计算方案预见期时段蒸发量失败!");
		}



    }

    /**
     * 修改方案计算预见期的时段蒸发量
     *
     * @return
     */
    @RequestMapping(value = "/updateForeseeEvaporation", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage updateForeseeEvaporation(@RequestBody PlanEvaporationDto planEvaporationDto) {
		try {
			return SystemSecurityMessage.getSuccessMsg("修改预见期蒸发量成功！",
					stStbprpBService.updateForeseeEvaporation(planEvaporationDto));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("修改预见期蒸发量失败!");
		}


    }

	//endregion

	/**
	 * 分页获取方案记录
	 * 
	 * @param param
	 *            条件过滤
	 * @return SystemMessage
	 * @see SystemSecurityMessage 其中result属性值为分页格式的的数据列表
	 * @see com.essence.framework.jpa.Paginator 分页格式对象
	 * @throws Exception
	 */
	@RequestMapping(value = "/getPlanInfoListPage", method = RequestMethod.POST)
	public @ResponseBody SystemSecurityMessage queryWaterMaintainListPage(@RequestBody PaginatorParam param) {
		try {
			Paginator<SqybModelPlanInfoDto> planInfoListPage = modelPlanInfoService.getPlanInfoListPage(param);
			return new SystemSecurityMessage("ok", "分页查询方案列表成功！", planInfoListPage);
		}catch (Exception e){
			e.printStackTrace();
			return new SystemSecurityMessage("error", "分页查询方案列表失败！", null);
		}

	}

	/**
	 * 删除计算方案及设置条件
	 * 
	 * @return
	 */
	@RequestMapping(value = "/deletePlanInfo/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage deletePlanInfo(@PathVariable String planId) {
		try {
			return SystemSecurityMessage.getSuccessMsg("方案删除成功！", modelPlanInfoService.deletePlanInfo(planId));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("方案删除失败!");
		}


	}

	/**
	 * 查询方案计算降雨设置条件
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getPlanInPutRain/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage getPlanInPutRain(@PathVariable String planId) {
		try {
			return SystemSecurityMessage.getSuccessMsg("查询方案降雨条件数据成功！", modelPlanInfoService.getPlanInPutRain(planId));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询方案降雨条件数据失败!");
		}


	}

	/**
	 * LiuGt add at 2020-03-19
	 * 查询方案计算蒸发量设置条件
	 *
	 * @return
	 */
	@RequestMapping(value = "/getPlanInPutEvapor/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage getPlanInPutEvapor(@PathVariable String planId) {
		try {
			return SystemSecurityMessage.getSuccessMsg("查询方案蒸发条件数据成功！", modelPlanInfoService.getPlanInPutEvapor(planId));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询方案蒸发条件数据失败!");
		}


	}

	/**
	 * 查询方案计算结果数据（入库流量）
	 * 
	 * @return
	 */
	@RequestMapping(value = "/getPlanOutPutRain/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage getPlanOutPutRain(@PathVariable String planId) {
		try {
			return SystemSecurityMessage.getSuccessMsg("查询方案计算结果数据成功！", modelPlanInfoService.getPlanOutPutRain(planId));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询方案计算结果数据失败!");
		}


	}

	/**
	 * LiuGt add at 2020-03-18
	 * 查询方案计算结果数据（入库流量）
	 * 返回值添加了实测流量，误差率
	 * @return
	 */
	@RequestMapping(value = "/getPlanResult/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage getPlanResult(@PathVariable String planId) {
		try {
			return SystemSecurityMessage.getSuccessMsg("查询方案计算结果数据成功！", modelPlanInfoService.getPlanResult(planId));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询方案计算结果数据失败!");
		}

	}

	/**
	 * 修改方案模型滚动计算条件
	 * LiuGt add at 2020-04-27
	 *
	 * @return
	 */
	@RequestMapping(value = "/updateModelLoopRun", method = RequestMethod.POST)
	public @ResponseBody SystemSecurityMessage updateModelLoopRun(@RequestBody SqybModelLoopRunDto modelLoopRun) {
		try {
			return SystemSecurityMessage.getSuccessMsg("修改成功！",
					modelPlanInfoService.editModelLoopRun(modelLoopRun));
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("修改失败!");
		}


	}

	/**
	 * 查询最新方案模型滚动计算条件
	 * LiuGt add at 2020-04-27
	 *
	 * @return
	 */
	@RequestMapping(value = "/getModelLoopRun", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage getModelLoopRun() {

		try {
			return SystemSecurityMessage.getSuccessMsg("查询成功！",
					modelPlanInfoService.queryNewModelLoopRun());
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询失败!");
		}

	}

	/**
	 * 设置能启动模型滚动计算条件的降雨数据（仅测试滚动计算使用）
	 * LiuGt add at 2020-05-20
	 * @return
	 */
	@RequestMapping(value = "/setAutoRunModelRain", method = RequestMethod.POST)
	public @ResponseBody SystemSecurityMessage setAutoRunModelRain(){
		try {
			return SystemSecurityMessage.getSuccessMsg("补充降雨数据成功！", modelPlanInfoService.setAutoRunModelRain());
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("补充降雨数据失败!");
		}

	}

	/**
	 * 查询多个方案对比的结果
	 * LiuGt add at 2020-07-08
	 * @return
	 */
	@RequestMapping(value = "/queryPlanContrastResult", method = RequestMethod.POST)
	public @ResponseBody SystemSecurityMessage queryPlanContrastResult(@RequestBody List<String> planIds) {

		SystemSecurityMessage systemSecurityMessage = modelPlanInfoService.queryPlanContrastResult(planIds);
		return systemSecurityMessage;
	}

	/**
	 * 方案结果展示 - 查询方案输入条件（各时段平均降雨的累积降雨）
	 * LiuGt add at 2020-07-09
	 * @return
	 */
	@RequestMapping(value = "/queryPlanInputRainfall/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage queryPlanInputRainfall(@PathVariable String planId) {
		try {
			List<HourTimeDrpListDto> hourTimeDrpListDtoList = modelPlanInfoService.queryPlanInputRainfall(planId);
			return SystemSecurityMessage.getSuccessMsg("查询成功！", hourTimeDrpListDtoList);
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询失败!");
		}

	}

	/**
	 * 方案结果展示 - 查询水库预测水位
	 * LiuGt add at 2020-07-09
	 * @return
	 */
	@RequestMapping(value = "/queryResForecastWaterLevel/{planId}", method = RequestMethod.GET)
	public @ResponseBody SystemSecurityMessage queryResForecastWaterLevel(@PathVariable String planId) {
		try {
			HourTimeWaterLevelViewDto waterLevelViewDto = modelPlanInfoService.queryResForecastWaterLevel(planId);
			return SystemSecurityMessage.getSuccessMsg("查询成功！", waterLevelViewDto);
		}catch (Exception e){
			e.printStackTrace();
			return SystemSecurityMessage.getFailMsg("查询失败!");
		}


	}
}
