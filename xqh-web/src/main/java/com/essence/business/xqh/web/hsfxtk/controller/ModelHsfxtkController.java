package com.essence.business.xqh.web.hsfxtk.controller;

import com.essence.business.xqh.api.hsfxtk.ModelCallHsfxtkService;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.hsfxtk.dto.PlanInfoHsfxtkVo;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 洪水风险调控模型相关控制层
 */
@RestController
@RequestMapping("/modelHsfxtk")
public class ModelHsfxtkController {


    @Autowired
    ModelCallHsfxtkService modelCallHsfxtkService;

    /**
     * 根据方案名称查询方案
     * @return
     */
    @RequestMapping(value = "getPlanInfoByName/{planName}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanInfoByName(@PathVariable String planName) {
        try {
            Integer planInfoByName = modelCallHsfxtkService.getPlanInfoByName(planName);

            return SystemSecurityMessage.getSuccessMsg("方案校验成功",planInfoByName);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("方案校验失败！",null);

        }
    }

    /**
     * 保存创建方案基本信息入库
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

    /**
     * 防洪保护区设置获取模型列表
     * @return
     */
    @RequestMapping(value = "getModelHsfxList", method = RequestMethod.GET)
    public SystemSecurityMessage getModelHsfxList() {
        try {
            List<Object> modelList =  modelCallHsfxtkService.getModelList();

            return SystemSecurityMessage.getSuccessMsg("获取模型列表成功",modelList);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取模型列表失败！",null);

        }
    }

    /**
     * 防洪保护区设置-根据保护区（模型）查询河道糙率列表
     * @return
     */
    @RequestMapping(value = "getModelRiverRoughness/{modelId}", method = RequestMethod.GET)
    public SystemSecurityMessage getModelRiverRoughness(@PathVariable String modelId) {
        try {
            List<Object> modelRiverRoughnessList =  modelCallHsfxtkService.getModelRiverRoughness(modelId);

            return SystemSecurityMessage.getSuccessMsg("查询河道糙率列表成功",modelRiverRoughnessList);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询河道糙率列表失败！",null);
        }
    }

    /**
     * 防洪保护区设置-保存方案输入-糙率参数设置入库
     * @return
     */
    @RequestMapping(value = "saveModelRiverRoughness", method = RequestMethod.POST)
    public SystemSecurityMessage saveModelRiverRoughness(@RequestBody ModelParamVo modelParamVo) {
        try {
            ModelParamVo  modelParamVos =  modelCallHsfxtkService.saveModelRiverRoughness(modelParamVo);

            return SystemSecurityMessage.getSuccessMsg("防洪保护区设置-保存方案输入-糙率参数设置入库成功",modelParamVos);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("防洪保护区设置-保存方案输入-糙率参数设置入库失败！",null);
        }
    }

    /**
     * 根据模型id查询边界条件列表
     * @return
     */
    @RequestMapping(value = "getModelBoundaryBasic", method = RequestMethod.POST)
    public SystemSecurityMessage getModelBoundaryBasic(@RequestBody ModelParamVo modelParamVo) {
        try {
            List<Object> modelRiverRoughnessList =  modelCallHsfxtkService.getModelBoundaryBasic(modelParamVo);

            return SystemSecurityMessage.getSuccessMsg("查询方案边界条件列表成功",modelRiverRoughnessList);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询方案边界列表失败！",null);
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
