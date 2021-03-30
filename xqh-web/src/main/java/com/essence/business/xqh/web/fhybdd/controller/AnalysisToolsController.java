package com.essence.business.xqh.web.fhybdd.controller;

import com.essence.business.xqh.api.fhybdd.service.AnalysisToolsService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RequestMapping("/analysisTool")
@RestController
public class AnalysisToolsController {

    @Autowired
    AnalysisToolsService analysisToolsService;

    /**
     * 获取水文站的涨差分析
     * @param map
     * @return
     */
    @RequestMapping(value = "/getAnalysisOfPriceDifference",method = RequestMethod.POST)
    public SystemSecurityMessage getAnalysisOfPriceDifference(@RequestBody Map map){

        try {
            List<Map<String,Object>> results = analysisToolsService.getAnalysisOfPriceDifference(map);
            return SystemSecurityMessage.getSuccessMsg("获取水文站的涨差分析数据成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取水文站的涨差分析数据失败",new ArrayList<>());

        }
    }

    /**
     * 获取分流比洪区列表
     * @param
     * @return
     */
        @RequestMapping(value = "/getFloodList",method = RequestMethod.GET)
        public SystemSecurityMessage getFloodList(){
            try {
                List<Map<String,Object>> results = analysisToolsService.getFloodList();
                return SystemSecurityMessage.getSuccessMsg("获取分流比洪区列表成功",results);
            }catch (Exception e){
                e.printStackTrace();
                return SystemSecurityMessage.getSuccessMsg("获取分流比洪区列表失败",new ArrayList<>());

            }
        }


    /**
     * 获取分流比计算
     * @param map
     * @return
     */
    @RequestMapping(value = "/getSplitRatioCalculation",method = RequestMethod.POST)
    public SystemSecurityMessage getSplitRatioCalculation(@RequestBody Map map){
        try {
            Map<String,Object> resultMap = analysisToolsService.getSplitRatioCalculation(map);
            return SystemSecurityMessage.getSuccessMsg("获取分流比计算数据成功",resultMap);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取分流比计算数据失败",new ArrayList<>());

        }
    }


    /**
     * 获取静库容反推入库数据
     * @param map
     * @return
     */
    @RequestMapping(value = "/getJkrftrkInformation",method = RequestMethod.POST)
    public SystemSecurityMessage getJkrftrkInformation(@RequestBody Map map){
        try {
            Map<String,Object> resultMap = analysisToolsService.getJkrftrkInformation(map);
            return SystemSecurityMessage.getSuccessMsg("获取静库容反推入库数据成功",resultMap);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取静库容反推入库数据失败",new ArrayList<>());

        }
    }

    /**
     * 获取水库列表成功
     * @return
     */
    @RequestMapping(value = "/getReservoirList",method = RequestMethod.GET)
    public SystemSecurityMessage getReservoirList(){
        try {
            Object results = analysisToolsService.getReservoirList();
            return SystemSecurityMessage.getSuccessMsg("获取水库数据列表成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取水库数据列表失败",new ArrayList<>());

        }
    }


    /**
     * before分段库容反推入库
     * @return
     */
    @RequestMapping(value = "/beforeGetJkrftrkInformationWithSection",method = RequestMethod.POST)
    public SystemSecurityMessage beforeGetJkrftrkInformationWithSection(@RequestBody Map map){
        try {
            Object results = analysisToolsService.beforeGetJkrftrkInformationWithSection(map);
            return SystemSecurityMessage.getSuccessMsg("获取分段库容反推时间序列成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取分段库容反推时间序列失败",new ArrayList<>());

        }
    }

    /**
     * 分段库容反推入库
     * @return
     */
    @RequestMapping(value = "/getJkrftrkInformationWithSection",method = RequestMethod.POST)
    public SystemSecurityMessage getJkrftrkInformationWithSection(@RequestBody List<Map> list){
        try {
            Object results = analysisToolsService.getJkrftrkInformationWithSection(list);
            return SystemSecurityMessage.getSuccessMsg("获取分段静库容反推入库数据成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取分段静库容反推入库数据失败",new ArrayList<>());

        }
    }


}
