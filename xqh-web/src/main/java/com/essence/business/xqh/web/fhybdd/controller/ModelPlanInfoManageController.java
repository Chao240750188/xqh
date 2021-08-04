package com.essence.business.xqh.web.fhybdd.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.fhybdd.dto.ModelPlanInfoManageDto;
import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybddNewService;
import com.essence.business.xqh.api.fhybdd.service.ModelPlanInfoManageService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/planInfoManageSW")
public class ModelPlanInfoManageController {

    @Autowired
    ModelPlanInfoManageService modelPlanInfoManageService;

    @Autowired
    ModelCallFhybddNewService modelCallFhybddNewService;
    /**
     * 获取水文3预报模型执行成功的方案列表
     *
     * @return
     */
    @RequestMapping(value = "/getPlanList", method = RequestMethod.POST)
    public SystemSecurityMessage getPlanList(@RequestBody PaginatorParam paginatorParam) {
        try {
            Paginator planList = modelPlanInfoManageService.getPlanList(paginatorParam);
            return SystemSecurityMessage.getSuccessMsg("获取洪水风险调控模型方案列表成功！", planList);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取洪水风险调控模型方案列表失败！");

        }
    }

    /**
     * 获取方案详细信息
     *
     * @return
     */
    @RequestMapping(value = "/getPlanInfo/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanInfo(@PathVariable String planId) {
        try {
            YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
            return SystemSecurityMessage.getSuccessMsg("获取方案详细信息成功！", planInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取方案详细信息失败！");

        }
    }


    /**
     * 删除方案基本信息 todo
     *
            * @return
            */
    @RequestMapping(value = "/deleteByPlanId/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage deleteByPlanId(@PathVariable String planId) {
        YwkPlaninfo planinfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planinfo == null){
            return new SystemSecurityMessage("error", "方案id不存在" );
        }
        try {
            modelPlanInfoManageService.deleteByPlanId(planinfo);
            return SystemSecurityMessage.getSuccessMsg("删除方案详细信息成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除方案详细信息失败！");

        }
    }

    /**
     * 获取预报断面列表
     * @param planId
     * @return
     */
    @RequestMapping(value = "/getTriggerList/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getTriggerList(@PathVariable String planId) {
        YwkPlaninfo planinfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planinfo == null){
            return new SystemSecurityMessage("error", "方案id不存在" );
        }
        try {
            List<Map<String,Object>> results = modelPlanInfoManageService.getTriggerList(planinfo);
            return SystemSecurityMessage.getSuccessMsg("获取预报断面列表集合成功！",results);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取预报断面列表集合失败！");

        }
    }

    /***
     * 方案发布
     * @param planIds
     * @return
     */
    @RequestMapping(value = "/publishPlan/{tag}", method = RequestMethod.POST)
    public SystemSecurityMessage publishPlan(@RequestBody List<String> planIds,@PathVariable Integer tag) {
       /* YwkPlaninfo planinfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planinfo == null){
            return new SystemSecurityMessage("error", "方案id不存在" );
        }
        if (planinfo.getnPublish()==1l){
            return new SystemSecurityMessage("error", "方案已经发布" );
        }*/
        try { //1是 发布 0 是撤销发布
             modelPlanInfoManageService.publishPlan(planIds,tag);
             String message = tag ==0?"发布方案成功!":"撤销发布方案成功";
            return SystemSecurityMessage.getSuccessMsg(message);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("发布方案失败！");

        }
    }

    /**
     * 获取警戒水位信息
     * @return
     */
    @RequestMapping(value = "/getWarnIngWaterLevels", method = RequestMethod.POST)
    public SystemSecurityMessage getWarnIngWaterLevels(@RequestBody Map map) {

        try {
           List<Map<String,Object>> results = modelPlanInfoManageService.getWarnIngWaterLevels(map);
            return SystemSecurityMessage.getSuccessMsg("获取预警水位列表信息成功！",results);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取预警水位列表信息失败！",null);

        }
    }

    @RequestMapping(value = "/upDateWarnIngWaterLevels", method = RequestMethod.POST)
    public SystemSecurityMessage upDateWarnIngWaterLevels(@RequestBody List<Map<String,Object>> datas) {

        try {
             modelPlanInfoManageService.upDateWarnIngWaterLevels(datas);
            return SystemSecurityMessage.getSuccessMsg("更新预警水位列表信息成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("更新预警水位列表信息失败！");

        }
    }

    /**
     * 根据河流获取所有断面信息
     * @return
     */
    @RequestMapping(value = "/getAllRcsByRiver/{rvcd}", method = RequestMethod.GET)
    public SystemSecurityMessage getAllRcsByRiver(@PathVariable String rvcd) {
        try{
            Object allRcs = modelPlanInfoManageService.getAllRcsByRiver(rvcd);
            return SystemSecurityMessage.getSuccessMsg("根据河流获取所有断面信息成功！", allRcs);
        }catch (Exception e){
            return SystemSecurityMessage.getSuccessMsg("根据河流获取所有断面信息失败！");
        }
    }

    /**
     * 根据断面获取其水位和流量
     * @param modelPlanInfoManageDto
     * @return
     */
    @RequestMapping(value = "/getWaterLevelFlow", method = RequestMethod.POST)
    public SystemSecurityMessage getWaterLevelFlow(@RequestBody ModelPlanInfoManageDto modelPlanInfoManageDto) {
        try{
            Object waterLevelFlow = modelPlanInfoManageService.getWaterLevelFlow(modelPlanInfoManageDto);
            return SystemSecurityMessage.getSuccessMsg("根据断面获取其水位和流量成功！", waterLevelFlow);
        }catch (Exception e){
            return SystemSecurityMessage.getSuccessMsg("根据断面获取其水位和流量失败！");
        }

    }

    /**
     * Excel导入
     * @return SystemSecurityMessage 返回结果json
     */
    @RequestMapping(value = "/importWaterLevelFlow", method = RequestMethod.POST)
    public SystemSecurityMessage importWaterLevelFlow(@RequestPart("files") MultipartFile mutilpartFile,
                                                      @RequestPart("dto") String modelPlanInfoManageDto) {
        SystemSecurityMessage SystemSecurityMessage = null;
        // 检查文件类型是否符合要求
        String checkFlog = ExcelUtil.checkFile(mutilpartFile);
        if (mutilpartFile == null) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件为空！", null);
        } else if (!"excel".equals(checkFlog)) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件类型错误！", null);
        } else {
            // 解析表格数据为对象类型
            try {
                Map<String, List<Map<String, String>>> map = modelPlanInfoManageService.importWaterLevelFlow(mutilpartFile, modelPlanInfoManageDto);
                if (CollectionUtils.isEmpty(map)){
                    SystemSecurityMessage = new SystemSecurityMessage("error", "断面水位流量信息数据上传解析失败" );
                }else {
                    SystemSecurityMessage = new SystemSecurityMessage("ok", "断面水位流量信息数据上传解析成功!", map);
                }
            } catch (Exception e) {
                String eMessage = "";
                if (e != null) {
                    eMessage = e.getMessage();
                }
                SystemSecurityMessage = new SystemSecurityMessage("error", "断面水位流量信息数据上传解析失败，错误原因：" + eMessage, null);
            }
        }
        return SystemSecurityMessage;
    }

    /**
     * Excel导出
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportTemplate", method = RequestMethod.POST)
    public void exportTemplate(HttpServletRequest request, HttpServletResponse response, @RequestBody ModelPlanInfoManageDto modelPlanInfoManageDto) {
        try {
            Workbook workbook = modelPlanInfoManageService.exportTemplate(modelPlanInfoManageDto);
            //响应尾
            response.setContentType("applicationnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "断面水位流量信息模板.xlsx";
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "iso_8859_1"));
            OutputStream ouputStream = response.getOutputStream();
            workbook.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 精度评定模型计算
     */
    @RequestMapping(value = "/modelCallJingDu", method = RequestMethod.POST)
    public SystemSecurityMessage modelCallJingDu(@RequestBody ModelPlanInfoManageDto modelPlanInfoManageDto) {
        try{
            modelPlanInfoManageService.modelCallJingDu(modelPlanInfoManageDto);
            return SystemSecurityMessage.getSuccessMsg("精度评定模型运行成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("精度评定模型运行失败！");
        }
//        int i = modelPlanInfoManageService.modelCallJingDu(modelPlanInfoManageDto);
//        if(i == 0){
//            return SystemSecurityMessage.getFailMsg("精度评定模型运行失败！");
//        }
//        return SystemSecurityMessage.getSuccessMsg("精度评定模型运行成功！");
    }

    /**
     * 获取本次精度评定模型运行的结果
     * @return
     */
    @RequestMapping(value = "/getModelResultQ",method = RequestMethod.POST)
    public SystemSecurityMessage getModelResultQ(@RequestBody ModelPlanInfoManageDto modelPlanInfoManageDto){
        try{
            Object results = modelPlanInfoManageService.getModelResultQ(modelPlanInfoManageDto);
            return SystemSecurityMessage.getSuccessMsg("解析精度评定模型运行结果成功",results);
        }catch (Exception e){
            return SystemSecurityMessage.getSuccessMsg("解析精度评定模型运行结果失败");
        }
    }

    /**
     * 获取历史精度评定计算断面列表
     * @param planId
     * @return
     */
    @RequestMapping(value = "/getHistoryJingDu/{planId}",method = RequestMethod.GET)
    public SystemSecurityMessage getHistoryJingDuRcs(@PathVariable String planId){
        try{
            Object results = modelPlanInfoManageService.getHistoryJingDuRcs(planId);
            return SystemSecurityMessage.getSuccessMsg("获取历史精度评定计算断面列表成功",results);
        }catch (Exception e){
            return SystemSecurityMessage.getSuccessMsg("获取历史精度评定计算断面列表失败");
        }
    }

    /**
     * 获取历史精度评定基本信息
     * @param planId
     * @param rvcrcrsccd
     * @return
     */
    @RequestMapping(value = "/getHistoryJingDuInfo/{planId}/{rvcrcrsccd}",method = RequestMethod.GET)
    public SystemSecurityMessage getHistoryJingDuInfo(@PathVariable String planId, @PathVariable String rvcrcrsccd){
        try{
            Object results = modelPlanInfoManageService.getHistoryJingDuInfo(planId, rvcrcrsccd);
            return SystemSecurityMessage.getSuccessMsg("获取历史精度评定基本信息成功",results);
        }catch (Exception e){
            return SystemSecurityMessage.getSuccessMsg("获取历史精度评定计算基本信息失败");
        }
    }


    /**
     * 获取历史精度评定结果成功
     * @param planId
     * @param rvcrcrsccd
     * @return
     */
    @RequestMapping(value = "/getHistoryJingDuResult/{planId}/{rvcrcrsccd}",method = RequestMethod.GET)
    public SystemSecurityMessage getHistoryJingDuResult(@PathVariable String planId, @PathVariable String rvcrcrsccd){
        try{
            Object results = modelPlanInfoManageService.getHistoryJingDuResult(planId, rvcrcrsccd);
            return SystemSecurityMessage.getSuccessMsg("获取历史精度评定结果成功",results);
        }catch (Exception e){
            return SystemSecurityMessage.getSuccessMsg("获取历史精度评定结果失败");
        }
    }

    @RequestMapping(value = "/deleteHistoryJingDu/{planId}/{rvcrcrsccd}",method = RequestMethod.GET)
    public SystemSecurityMessage deleteHistoryJingDu(@PathVariable String planId, @PathVariable String rvcrcrsccd){
        try{
            modelPlanInfoManageService.deleteHistoryJingDu(planId, rvcrcrsccd);
            return SystemSecurityMessage.getSuccessMsg("删除历史精度评定成功");
        }catch (Exception e){
            return SystemSecurityMessage.getSuccessMsg("删除历史精度评定失败");
        }
    }

}
