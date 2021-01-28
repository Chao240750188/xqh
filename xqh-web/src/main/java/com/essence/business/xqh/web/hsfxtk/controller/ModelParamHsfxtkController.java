package com.essence.business.xqh.web.hsfxtk.controller;


import com.essence.business.xqh.api.fhybdd.dto.YwkModelDto;
import com.essence.business.xqh.api.hsfxtk.ModelParamHsfxtkService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
       /* 洪水风险调控模型相关控制层
        */
@RestController
@RequestMapping("/modelParamHsfxtk")
public class ModelParamHsfxtkController {


      ModelParamHsfxtkService modelParamHsfxtkService;

       /**
        * 获取洪水风险调控列表
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
        * 根据模型id获取参数列表
        * @return
        */
       /**@RequestMapping(value = "/getModelParamList/{modelId}", method = RequestMethod.GET)
       public SystemSecurityMessage getModelParamList(@PathVariable String modelId) {
           try {
               //List<YwkModelDto> list =  modelParamHsfxtkService.getModelParamList();
               return SystemSecurityMessage.getSuccessMsg("获取洪水风险调控模型参数列表成功",list);

           }catch (Exception e){
               e.printStackTrace();
               return SystemSecurityMessage.getFailMsg("获取洪水风险调控模型参数列表失败！",new ArrayList<>());

           }
       }**/


}
