package com.essence.business.xqh.web.fhybdd.controller;

import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybddNewService;
import com.essence.business.xqh.api.fhybdd.service.ModelPlanInfoManageService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

}
