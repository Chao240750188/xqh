package com.essence.business.xqh.web.hsfxtk.controller;


import com.essence.business.xqh.api.fhybdd.dto.YwkModelDto;
import com.essence.business.xqh.api.hsfxtk.ModelParamHsfxtkService;
import com.essence.business.xqh.api.hsfxtk.dto.*;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* 洪水风险调控模型相关控制层
        */
@RestController
@RequestMapping("/modelParamHsfxtk")
public class ModelParamHsfxtkController {


      @Autowired
      ModelParamHsfxtkService modelParamHsfxtkService;

       /**
        * 获取洪水风险调控列表 TODO 测试成功
        * @return
        */
       @RequestMapping(value = "/getModelList", method = RequestMethod.GET)
       public SystemSecurityMessage getModelList() {
           try {
             List<YwkModelDto> list =  modelParamHsfxtkService.getModelList();
               return SystemSecurityMessage.getSuccessMsg("获取洪水风险调控模型列表成功",list);

           }catch (Exception e){
               e.printStackTrace();
               return SystemSecurityMessage.getFailMsg("获取洪水风险调控模型列表失败！",new ArrayList<>());

           }
       }

       /**
        * 根据模型id获取参数列表 TODO 测试ok
        * @return
        */
       @RequestMapping(value = "/getModelParamList/{modelId}", method = RequestMethod.GET)
       public SystemSecurityMessage getModelParamList(@PathVariable String modelId) {
           try {
               List<YwkModelRoughnessParamDto> list =  modelParamHsfxtkService.getModelParamList(modelId);
               return SystemSecurityMessage.getSuccessMsg("获取洪水风险调控模型参数列表成功",list);

           }catch (Exception e){
               e.printStackTrace();
               return SystemSecurityMessage.getFailMsg("获取洪水风险调控模型参数列表失败！",new ArrayList<>());

           }
       }


       /**
        * 根据模型操率id获取河道操率列表 TODO 测试成功
        * @return
        */
       @RequestMapping(value = "/getModelRoughParamList/{roughness}", method = RequestMethod.GET)
       public SystemSecurityMessage getModelRoughParamList(@PathVariable String roughness) {
           try {
               List<YwkRiverRoughnessParamDto> list =  modelParamHsfxtkService.getModelRoughParamList(roughness);
               return SystemSecurityMessage.getSuccessMsg("获取洪水风险调控河道糙率参数列表成功",list);

           }catch (Exception e){
               e.printStackTrace();
               return SystemSecurityMessage.getFailMsg("获取洪水风险调控河道糙率参数列表失败！",new ArrayList<>());

           }
       }




           /**
            * before保存参数入库 TODO 已测试
            * @param
            * @return
            */
           @RequestMapping(value = "/beforeSaveRoughness/{modelId}", method = RequestMethod.GET)
           public SystemSecurityMessage beforeSaveRoughness(@PathVariable String modelId) {
               try {
                   List<Map<String,Object>> list =  modelParamHsfxtkService.beforeSaveRoughness(modelId);
                   return SystemSecurityMessage.getSuccessMsg("获取洪水风险调控模型基本操率参数列表成功",list);

               }catch (Exception e){
                   e.printStackTrace();
                   return SystemSecurityMessage.getFailMsg("获取洪水风险调控模型基本操率参数列表失败！",new ArrayList<>());

               }
           }

       /**
        * 保存参数入库 TODO 已测试
        * @param
        * @return
        */
       @RequestMapping(value = "/saveRoughness", method = RequestMethod.POST)
       public SystemSecurityMessage saveRoughness(@RequestBody YwkParamVo ywkParamVo) {
           try {
                modelParamHsfxtkService.saveRoughness(ywkParamVo);
               return SystemSecurityMessage.getSuccessMsg("保存洪水风险调控模型操率参数列表成功");

           }catch (Exception e){
               e.printStackTrace();
               return SystemSecurityMessage.getFailMsg("保存洪水风险调控模型操率参数列表失败！");

           }
       }



    /**  TODO 以测试
     * 删除模型参数跟河道参数列表
     * @param
     * @return
     */
    @RequestMapping(value = "/deleteRoughness/{roughness}", method = RequestMethod.GET)
    public SystemSecurityMessage deleteRoughness( @PathVariable String roughness) {
        try {
            modelParamHsfxtkService.deleteRoughness(roughness);
            return SystemSecurityMessage.getSuccessMsg("删除洪水风险调控模型操率参数列表成功");

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除洪水风险调控模型操率参数列表失败！");

        }
    }

}
