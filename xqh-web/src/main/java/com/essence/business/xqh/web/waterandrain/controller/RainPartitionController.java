package com.essence.business.xqh.web.waterandrain.controller;

import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;
import com.essence.business.xqh.api.waterandrain.service.RainPartitionService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 雨水情查询-雨量信息查询分区
 * 公报/简报 控制层
 */
@RestController
@RequestMapping(value = "/rainPartition")
public class RainPartitionController {
    @Autowired
    RainPartitionService rainPartitionService;


    /**
     * 查询分区雨量
     *
     * @param reqDto
     * @return
     */
    @PostMapping(value = "/getPartRain")
    public SystemSecurityMessage getPartRain(@RequestBody RainPartitionDto reqDto) {
        try {
            return new SystemSecurityMessage("ok", "查询分区雨量成功", rainPartitionService.getPartRain(reqDto,false));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询分区雨量失败！");
        }
    }

    /**
     * 查询数据生成简报报告
     *
     * @param reqDto
     * @return
     */
    @PostMapping(value = "/getRainWaterSimpleReport")
    public SystemSecurityMessage getRainWaterSimpleReport(@RequestBody RainPartitionDto reqDto) {
        try {
            return new SystemSecurityMessage("ok", "查询简报数据成功", rainPartitionService.getRainWaterSimpleReport(reqDto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询简报数据失败！");
        }
    }
}
