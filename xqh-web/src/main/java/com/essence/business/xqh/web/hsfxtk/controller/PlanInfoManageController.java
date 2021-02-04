package com.essence.business.xqh.web.hsfxtk.controller;

import com.essence.business.xqh.api.hsfxtk.PlanInfoManageService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 方案结果列表及条件信息控制层
 */
@RestController
@RequestMapping("/planInfoManage")
public class PlanInfoManageController {
    @Autowired
    PlanInfoManageService planInfoManageService;

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
}
