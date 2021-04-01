package com.essence.business.xqh.web.waterandrain.controller;

import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;
import com.essence.business.xqh.api.waterandrain.service.AnalysisOfFloodService;
import com.essence.business.xqh.api.waterandrain.service.RainPartitionService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("analysisOfFlood")
public class AnalysisOfFloodController {

    @Autowired
    AnalysisOfFloodService analysisOfFloodService;

    /**
     * 洪水形式分析---创建成公报报告
     * @param reqDto
     * @return
     */
    @PostMapping(value = "/getRainWaterCommonReport")
    public SystemSecurityMessage getRainWaterCommonReport(@RequestBody RainPartitionDto reqDto) {
        try {
            return new SystemSecurityMessage("ok", "查询洪水分析公报内容成功", analysisOfFloodService.getRainWaterCommonReport(reqDto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询洪水分析公报内容失败！");
        }
    }


    @GetMapping(value = "/getAreaList")
    public SystemSecurityMessage getAreaList() {
        try {
            return new SystemSecurityMessage("ok", "查询分区列表信息成功", analysisOfFloodService.getAreaList());
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询分区列表信息失败！");
        }
    }
}
