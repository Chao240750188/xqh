package com.essence.business.xqh.web.fhybdd.controller;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybddService;
import com.essence.business.xqh.api.task.fhybdd.ReservoirModelCallTask;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ffybddModelCall")
public class ModelCallFhybddController {


    @Autowired
    ModelCallFhybddService modelCallFhybddService;
    /**
     * 水文调度模型计算执行
     * @return
     */
    @RequestMapping(value = "/modelCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelCall(@PathVariable  String planId) {
        try {
            List<Map<String,Object>> results = modelCallFhybddService.callMode(planId);
            return SystemSecurityMessage.getSuccessMsg("调用防洪与报警水文调度模型成功！",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用防洪与报警水文调度模型失败！");

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

//        CompletableFuture<String> order1 = reservoirModelCallTask.reservoirModelCall("测试order1");
//        CompletableFuture<String> order2 = reservoirModelCallTask.reservoirModelCall("测试order2");
//        CompletableFuture<String> order3 = reservoirModelCallTask.reservoirModelCall("测试order3");

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
//        CompletableFuture.allOf(result);
//        //String result = objects[0].get() + objects[1].get() + objects[2].get();
//        System.out.println("result="+result[0].get()+result[1].get()+result[2].get());
     return SystemSecurityMessage.getSuccessMsg("根据方案获取雨量信息成功");

    }


}
