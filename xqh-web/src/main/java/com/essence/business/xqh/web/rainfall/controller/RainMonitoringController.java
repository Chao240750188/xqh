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
    @PostMapping(value = "/getRainDistributionList")
    public SystemSecurityMessage getRainDistributionList(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getRainDistributionList(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 单个测站信息
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


    /**
     * 实时监测-水情监测-闸坝
     *
     * @return
     */
    @GetMapping(value = "/getSluiceList")
    public SystemSecurityMessage getSluiceList() {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getSluiceList());
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 实时监视-水情监视-站点查询-站点信息-闸坝
     *
     * @param stcd
     * @return
     */
    @GetMapping(value = "/getSluiceInfo/{stcd}")
    public SystemSecurityMessage getSluiceInfo(@PathVariable(name = "stcd") String stcd) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getSluiceInfo(stcd));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 实时监视-水情监视-站点查询-水位流量过程线-闸坝
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getSluiceTendency")
    public SystemSecurityMessage getSluiceTendency(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getSluiceTendency(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }


    /**
     * 实时监测-水情监测-潮位
     *
     * @return
     */
    @GetMapping(value = "/getTideList")
    public SystemSecurityMessage getTideList() {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getTideList());
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 实时监视-水情监视-站点查询-站点信息-潮位
     *
     * @param stcd
     * @return
     */
    @GetMapping(value = "/getTideInfo/{stcd}")
    public SystemSecurityMessage getTideInfo(@PathVariable(name = "stcd") String stcd) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getTideInfo(stcd));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 实时监视-水情监视-站点查询-水位流量过程线-潮位
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getTideTendency")
    public SystemSecurityMessage getTideTendency(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getTideTendency(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }
}
