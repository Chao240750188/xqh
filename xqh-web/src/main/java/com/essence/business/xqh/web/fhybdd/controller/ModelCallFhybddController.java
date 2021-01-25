package com.essence.business.xqh.web.fhybdd.controller;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.dto.WrpRcsBsinDto;
import com.essence.business.xqh.api.fhybdd.dto.WrpRvrBsinDto;
import com.essence.business.xqh.api.fhybdd.dto.YwkModelDto;
import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybdd2Service;
import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybddService;
import com.essence.business.xqh.api.task.fhybdd.ReservoirModelCallTask;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/modelCallFhybdd")
public class ModelCallFhybddController {


    @Autowired
    ModelCallFhybddService modelCallFhybddService;
    @Autowired
    ModelCallFhybdd2Service modelCallFhybdd2Service;


    /**
     * 水文调度模型计算执行
     * @return
     */
    @RequestMapping(value = "/modelCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelCall(@PathVariable  String planId) {
        try {
            Object results = modelCallFhybddService.callMode(planId);
            return SystemSecurityMessage.getSuccessMsg("调用防洪与报警水文调度模型成功！",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用防洪与报警水文调度模型失败！");

        }
    }

    /**
     * 水文调度模型计算执行
     * @return
     */
    @RequestMapping(value = "/modelCall2/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelCall2(@PathVariable  String planId) {
        YwkPlaninfo planInfo = (YwkPlaninfo) CacheUtil.get("planInfo", planId);//方案基本信息
        try {
            Long ret = modelCallFhybdd2Service.callMode(planId);
            planInfo.setnPlanstatus(ret);
            CacheUtil.saveOrUpdate("planInfo",planId,planInfo);
            return SystemSecurityMessage.getSuccessMsg("调用防洪与报警水文调度模型成功！",ret);
        }catch (Exception e){
            planInfo.setnPlanstatus(-1L);
            CacheUtil.saveOrUpdate("planInfo",planId,planInfo);
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用防洪与报警水文调度模型失败！",-1);

        }
    }

    /**
     * 方案计划存入缓存 TODO 已测试
     * @param vo
     * @return
     */
    @RequestMapping(value = "/savePlanWithCache", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanWithCache(@RequestBody ModelCallBySWDDVo vo) {
        try {
            String planId = modelCallFhybddService.savePlanWithCache(vo);
            if (planId == null){
                return SystemSecurityMessage.getFailMsg("水文调度模型存入缓存失败！",null);
            }
            return SystemSecurityMessage.getSuccessMsg("水文调度模型存入缓存成功",planId);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("水文调度模型存入缓存失败！",null);

        }
    }



    /**
     * 根据方案获取雨量信息
     * @param planId TODO 以测试
     * @return
     */
    @RequestMapping(value = "/getRainfalls/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getRainfalls(@PathVariable String planId) {
        try {
            List<Map<String,Object>> results = modelCallFhybddService.getRainfalls(planId);
            return SystemSecurityMessage.getSuccessMsg("根据方案获取雨量信息成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("根据方案获取雨量信息失败！");

        }
    }

    @Autowired
    ReservoirModelCallTask reservoirModelCallTask;
    @RequestMapping(value = "/test",method = RequestMethod.GET)
    public SystemSecurityMessage test()throws Exception{

        reservoirModelCallTask.haha();
        CompletableFuture<String> order1 = reservoirModelCallTask.text("测试order1",1);
        CompletableFuture<String> order2 = reservoirModelCallTask.text("测试order2",2);
       CompletableFuture<String> order3 = reservoirModelCallTask.text("测试order3",0);

//        List<CompletableFuture<String>> list = new ArrayList<>();
//        for (int i =0 ;i<3;i++){
//            list.add(reservoirModelCallTask.reservoirModelCall("order"+i));
//        }
//        CompletableFuture []result = new CompletableFuture[list.size()];
//
//        for (int i=0;i<list.size();i++){
//            result[i] = list.get(i);
//        }
//        // 等待所有任务都执行完
//        // 获取每个任务的返回结果
       //CompletableFuture.allOf(order1,order2,order3);
        String result = order1.get() + order2.get();
        System.out.println("result="+result);
        System.out.println("order3="+order3.get());
     return SystemSecurityMessage.getSuccessMsg("根据方案获取雨量信息成功");

    }


    /**
     * 获取河流列表信息
     * @return
     */
    @RequestMapping(value = "/getRiverInfos",method = RequestMethod.GET)
    public SystemSecurityMessage getRiverInfos(){

        List<WrpRvrBsinDto> riverInfos = modelCallFhybddService.getRiverInfos();
        return SystemSecurityMessage.getSuccessMsg("获取河流列表信息成功",riverInfos);

    }

    /**
     * 获取河流对应的断面信息
     * @return
     */
    @RequestMapping(value = "/getRcsByRiver/{rvcd}",method = RequestMethod.GET)
    public SystemSecurityMessage getRcsByRiver(@PathVariable String rvcd){

        List<WrpRcsBsinDto> rcsList = modelCallFhybddService.getRcsByRiver(rvcd);
        return SystemSecurityMessage.getSuccessMsg("获取河流断面列表信息成功",rcsList);

    }

    /**
     * 获取模型列表信息
     * @return
     */
    @RequestMapping(value = "/getModelInfos",method = RequestMethod.GET)
    public SystemSecurityMessage getModelInfos(){

        List<YwkModelDto> modelInfos = modelCallFhybddService.getModelInfos();
        return SystemSecurityMessage.getSuccessMsg("获取模型列表信息成功",modelInfos);

    }

    /**
     * 获取模型运行状态
     * @return
     */
    @RequestMapping(value = "/getModelRunStatus/{planId}",method = RequestMethod.GET)
    public SystemSecurityMessage getModelRunStatus(@PathVariable String planId){

        String status = modelCallFhybddService.getModelRunStatus(planId);
        return SystemSecurityMessage.getSuccessMsg("获取模型列表信息成功",status);

    }

    /**
     * 获取模型运行输出结果
     * @return
     */
    @RequestMapping(value = "/getModelResultQ/{planId}",method = RequestMethod.GET)
    public SystemSecurityMessage getModelResultQ(@PathVariable String planId){

        Object results = modelCallFhybddService.getModelResultQ(planId);
        return SystemSecurityMessage.getSuccessMsg("获取模型列表信息成功",results);

    }

}
