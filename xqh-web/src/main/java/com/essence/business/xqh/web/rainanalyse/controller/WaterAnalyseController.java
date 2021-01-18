package com.essence.business.xqh.web.rainanalyse.controller;

import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelAnalysisInfoDto;
import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelCompareInfoDto;
import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelCompareParamDto;
import com.essence.business.xqh.api.rainanalyse.dto.WaterLevelStationInfoDto;
import com.essence.business.xqh.api.rainanalyse.service.WaterLevelCompareAnalysisService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName RainfallAnalyseContorller
 * @Description 水位
 * @Author zhichao.xing
 * @Date 2020/7/4 16:57
 * @Version 1.0
 **/
@RestController
@RequestMapping("waterAnalyse")
public class WaterAnalyseController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaterLevelCompareAnalysisService waterLevelCompareAnalysisService;


    /**
     * 水情分析--查询水位站信息
     * @Author huangxiaoli
     * @Description
     * @Date 11:03 2020/9/12
     * @Param [] TODO 已测试
     * @return com.essence.tzsyq.util.SystemSecurityMessage
     **/
    @GetMapping("/getWaterStation")
    public SystemSecurityMessage getWaterStation() {
        try {
            List<WaterLevelStationInfoDto> waterLevelStationInfoDtoList = waterLevelCompareAnalysisService.getWaterLevelStationInfoDto();
            return new SystemSecurityMessage("ok", "查询成功", waterLevelStationInfoDtoList);
        }catch (Exception e){
            e.printStackTrace();
            return new SystemSecurityMessage("error", "查询失败");
        }
    }


    /**
     * 水情分析--根据条件分页查询水位站水位信息
     * @Author huangxiaoli
     * @Description
     * @Date 13:47 2020/9/12 TODO 已测试
     * @Param [param]
     * @return com.essence.framework.jpa.Paginator<com.essence.tzsyq.rainanalyse.dto.WaterLevelAnalysisInfoDto>
     **/
    @RequestMapping(value = "getWaterLevelAnalysisDataPageInfo",method = RequestMethod.POST)
    public SystemSecurityMessage getWaterLevelAnalysisDataPageInfo(@RequestBody PaginatorParam param) {
        try {
            Paginator<WaterLevelAnalysisInfoDto> paginator = waterLevelCompareAnalysisService.getWaterLevelAnalysisDataPageInfo(param);
            return new SystemSecurityMessage("ok", "分页查询成功", paginator);
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "分页查询失败");
        }
    }



    /**
     * 水情形式分析--水情对比分析--根据条件分页查询水情信息
     * @Author huangxiaoli
     * @Description
     * @Date 16:56 2020/9/3 TODO已测试
     * @Param [param]
     * @return com.essence.tzsyq.util.SystemSecurityMessage
     **/
    @RequestMapping(value = "getWaterLevelAnalysisPageInfo",method = RequestMethod.POST)
    public SystemSecurityMessage getWaterLevelAnalysisPageInfo(@RequestBody PaginatorParam param) {
        try {
            Paginator<WaterLevelAnalysisInfoDto> paginator = waterLevelCompareAnalysisService.getWaterLevelAnalysisPageInfo(param);
            return new SystemSecurityMessage("ok", "分页查询成功", paginator);
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "分页查询失败");
        }
    }


    /**
     * 水情形式分析--水情对比分析--查询单个测站去年同期水位对比数据
     * @Author huangxiaoli
     * @Description
     * @Date 17:31 2020/9/3
     * @Param [stcd, startTime, endTime]
     * @return com.essence.tzsyq.rainanalyse.dto.WaterLevelCompareInfoDto
     **/
    @RequestMapping(value = "getWaterLevelCompareInfo",method = RequestMethod.POST)
    public SystemSecurityMessage getWaterLevelCompareInfo(@RequestBody WaterLevelCompareParamDto paramDto) {
        try {
            WaterLevelCompareInfoDto waterLevelCompareInfo = waterLevelCompareAnalysisService.getWaterLevelCompareInfo(paramDto.getType(),paramDto.getStcd(), paramDto.getStartTime(), paramDto.getEndTime());
            return new SystemSecurityMessage("ok", "查询成功", waterLevelCompareInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

}
