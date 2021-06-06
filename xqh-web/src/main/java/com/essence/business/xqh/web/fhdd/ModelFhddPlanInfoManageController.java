package com.essence.business.xqh.web.fhdd;

import com.essence.business.xqh.api.fhdd.ModelFhddPlanInfoManageService;
import com.essence.business.xqh.api.skdd.vo.ModelSkddXxInputVo;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modelFhddPlanInfoManage")
public class ModelFhddPlanInfoManageController {

    @Autowired
    ModelFhddPlanInfoManageService modelFhddPlanInfoManageService;

    /**
     * 获取模型执行成功的方案列表
     * @param paginatorParam
     * @return
     */
    @RequestMapping(value = "/getPlanList", method = RequestMethod.POST)
    public SystemSecurityMessage getPlanList(@RequestBody PaginatorParam paginatorParam) {
        try {
            Paginator planList = modelFhddPlanInfoManageService.getPlanList(paginatorParam);
            return SystemSecurityMessage.getSuccessMsg("获取防洪调度模型方案列表成功！", planList);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取防洪调度列表失败！");

        }
    }

    /**
     * 获取方案的详细信息
     * @param planId
     * @return
     */
    @GetMapping(value = "/getPlanInfo/{planId}")
    public SystemSecurityMessage getPlanInfo(@PathVariable String planId) {
        try {
            YwkPlaninfo planInfo = modelFhddPlanInfoManageService.getPlanInfoByPlanId(planId);
            return SystemSecurityMessage.getSuccessMsg("获取方案详细信息成功！", planInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取方案详细信息失败！");
        }
    }

    /**
     * 获取水库调度信息
     * @param planId
     * @return
     */
    @GetMapping(value = "/getPlanInputInfo/{planId}")
    public SystemSecurityMessage getPlanInputInfo(@PathVariable String planId) {
        try {
            ModelSkddXxInputVo vo = modelFhddPlanInfoManageService.getPlanInputInfo(planId);
            return SystemSecurityMessage.getSuccessMsg("获取水库调度信息成功！", vo);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取水库调度信息失败！");
        }
    }

    /**
     * 删除方案基本信息
     * @param planId
     * @return
     */
    @GetMapping(value = "/deleteByPlanId/{planId}")
    public SystemSecurityMessage deleteByPlanId(@PathVariable String planId) {
        YwkPlaninfo planinfo = modelFhddPlanInfoManageService.getPlanInfoByPlanId(planId);
        if (planinfo == null){
            return new SystemSecurityMessage("error", "方案id不存在" );
        }
        try {
            modelFhddPlanInfoManageService.deleteByPlanId(planinfo);
            return SystemSecurityMessage.getSuccessMsg("删除方案详细信息成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除方案详细信息失败！");

        }
    }

}
