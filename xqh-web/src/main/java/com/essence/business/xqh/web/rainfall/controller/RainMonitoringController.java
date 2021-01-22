package com.essence.business.xqh.web.rainfall.controller;

import com.essence.business.xqh.api.rainfall.service.RainMonitoringService;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author fengpp
 * 2021/1/21 18:20
 */
@RestController
@RequestMapping(value = "/rainMonitoring")
public class RainMonitoringController {
    @Autowired
    RainMonitoringService rainMonitoringService;

    /**
     * 实时监测-雨情监测-雨情概况
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/rainSummary")
    public SystemSecurityMessage rainSummary(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.rainSummary(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 降雨分布列表
     *
     * @param dto
     * @return
     */
    @GetMapping(value = "/getRainDistributionList")
    public SystemSecurityMessage getRainDistributionList(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getRainDistributionList(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 测站信息
     *
     * @param stcd
     * @return
     */
    @GetMapping(value = "/getInfo/{stcd}")
    public SystemSecurityMessage getInfo(@PathVariable(name = "stcd") String stcd) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getInfo(stcd));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }


    public SystemSecurityMessage rainfallProcess() {

        try {
            return new SystemSecurityMessage("ok", "查询成功");
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

}
