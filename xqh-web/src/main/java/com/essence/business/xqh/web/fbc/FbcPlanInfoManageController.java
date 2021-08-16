package com.essence.business.xqh.web.fbc;
import com.essence.business.xqh.api.fbc.FbcPlanInfoManageService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkBreakBasic;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 方案结果列表及条件信息控制层
 */
@RestController
@RequestMapping("/fbcPlanInfoManage")
public class FbcPlanInfoManageController {
    @Autowired
    FbcPlanInfoManageService planInfoManageService;

    /**
     * 获取水动力模型执行成功的方案列表
     *
     * @return
     */
    @RequestMapping(value = "/getPlanList", method = RequestMethod.POST)
    public SystemSecurityMessage getPlanList(@RequestBody PaginatorParam paginatorParam) {
        try {
            Paginator planList = planInfoManageService.getPlanList(paginatorParam);
            return SystemSecurityMessage.getSuccessMsg("获取洪水风险调控模型方案列表成功！", planList);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取洪水风险调控模型方案列表失败！");

        }
    }


    /**
     * 根据方案id获取方案边界数据（包括流量和水位数据）
     * @param planId
     * @return
     */
    @RequestMapping(value = "/getAllBoundaryByPlanId/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getAllBoundaryByPlanId(@PathVariable String planId) {
        try {
            List<Map> results = planInfoManageService.getAllBoundaryQByPlanId(planId);
            return SystemSecurityMessage.getSuccessMsg("获取边界列表成功！",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取边界列表失败！");

        }
    }


    /**
     * 根据方案id获取防洪保护区设置
     * @param planId
     * @return
     */
    @RequestMapping(value = "/getAllRoughnessByPlanId/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getAllRoughnessByPlanId(@PathVariable String planId) {
        try {
            Map<String,Object> results = planInfoManageService.getAllRoughnessByPlanId(planId);
            return SystemSecurityMessage.getSuccessMsg("获取防洪保护区列表成功！",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取防洪保护区列表失败！");

        }
    }

    /**
     * 根据方案id获取溃点位置
     * @param planId
     * @return
     */
    @RequestMapping(value = "/getAllBreakByPlanId/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getAllBreakByPlanId(@PathVariable String planId) {
        try {
            Map<String,Object> results = planInfoManageService.getAllBreakByPlanId(planId);
            return SystemSecurityMessage.getSuccessMsg("获取溃点信息成功！",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取溃点信息失败！");

        }
    }

    /**
     * 删除方案以及方案下关联点所有入参
     * @param planId
     * @return
     */
    @RequestMapping(value = "/deleteAllInputByPlanId/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage deleteAllInputByPlanId(@PathVariable String planId) {
        try {
            planInfoManageService.deleteAllInputByPlanId(planId);
            return SystemSecurityMessage.getSuccessMsg("删除方案信息成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除方案信息失败！");

        }
    }

    /**
     * 获取所有溃口列表，风暴潮跟水动力公用
     * @return
     */
    @RequestMapping(value = "/getBreakAllList",method = RequestMethod.GET)
    public SystemSecurityMessage getBreakAllList(){
        List<YwkBreakBasic> breakAllList = planInfoManageService.getBreakAllList();
        return SystemSecurityMessage.getSuccessMsg("获取溃口列表信息成功",breakAllList);
    }
}
