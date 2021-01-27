package com.essence.business.xqh.web.hsfxtk.controller;

import com.essence.business.xqh.api.hsfxtk.ModelCallHsfxtkService;
import com.essence.business.xqh.api.hsfxtk.dto.PlanInfoHsfxtkVo;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 洪水风险调控模型相关控制层
 */
@RestController
@RequestMapping("/modelHsfxtk")
public class ModelHsfxtkController {


    @Autowired
    ModelCallHsfxtkService modelCallHsfxtkService;

    /**
     * 保存方案基本信息入库
     * @param vo
     * @return
     */
    @RequestMapping(value = "/savePlanToDb", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanToDb(@RequestBody PlanInfoHsfxtkVo vo) {
        try {
            String planId = modelCallHsfxtkService.savePlanToDb(vo);
            if (planId == null){
                return SystemSecurityMessage.getFailMsg("洪水风险调控方案保存失败！",null);
            }
            return SystemSecurityMessage.getSuccessMsg("洪水风险调控方案保存成功",planId);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("洪水风险调控方案保存失败！",null);

        }
    }

    @RequestMapping(value = "/test/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage savePlanToDb(@PathVariable String planId) {
        try {
            modelCallHsfxtkService.test(planId);

            return SystemSecurityMessage.getSuccessMsg("洪水风险调控方案保存成功",planId);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("洪水风险调控方案保存失败！",null);

        }
    }

}
