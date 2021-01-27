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
     * 实时监测-雨情监测-降雨分布列表
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
     * 实时监测-雨情监测-单个测站信息
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

    /**
     * 水雨情查询-洪水告警-闸坝
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getSluiceFloodWarning")
    public SystemSecurityMessage getSluiceFloodWarning(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getSluiceFloodWarning(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水雨情查询-洪水告警-潮汐
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getTideFloodWarning")
    public SystemSecurityMessage getTideFloodWarning(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getTideFloodWarning(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水雨情查询-洪水告警-模态框-闸坝列表
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getSluiceFloodWarningList")
    public SystemSecurityMessage getSluiceFloodWarningList(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getSluiceFloodWarningList(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水雨情查询-洪水告警-模态框-潮汐列表
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getTideFloodWarningList")
    public SystemSecurityMessage getTideFloodWarningList(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getTideFloodWarningList(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水情服务-水情简报表
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getList")
    public SystemSecurityMessage getList(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getList(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水情服务-河道水情表
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getRiverList")
    public SystemSecurityMessage getRiverList(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getRiverList(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水情服务-水库水情表
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getReservoirList")
    public SystemSecurityMessage getReservoirList(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainMonitoringService.getReservoirList(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }
}
