package com.essence.business.xqh.web.waterandrain.controller;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;
import com.essence.business.xqh.api.rainfall.vo.RainWaterReportDto;
import com.essence.business.xqh.api.waterandrain.service.AnalysisOfFloodService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
            e.printStackTrace();
            return new SystemSecurityMessage("error", "查询洪水分析公报内容失败！");
        }
    }

    /**
     * 获取分区列表
     * @return
     */
    @GetMapping(value = "/getAreaList")
    public SystemSecurityMessage getAreaList() {
        try {
            return new SystemSecurityMessage("ok", "查询分区列表信息成功", analysisOfFloodService.getAreaList());
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询分区列表信息失败！");
        }
    }


    /**
     * 保存报告
     *
     * @param map
     * @return
     */
    @PostMapping(value = "/saveRainWaterCommonReport")
    public SystemSecurityMessage saveRainWaterCommonReport(@RequestBody Map map) {
        try {
            return new SystemSecurityMessage("ok", "保存公报内容成功", analysisOfFloodService.saveRainWaterCommonReport(map));
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "保存公报内容失败！");
        }
    }

    /**
     * 获取公报列表信息
     *
     * @param paginatorParam
     * @return
     */
    @RequestMapping(value = "/getCommonReportList", method = RequestMethod.POST)
    public SystemSecurityMessage getCommonReportList(@RequestBody PaginatorParam paginatorParam) {
        try {
            Paginator planList = analysisOfFloodService.getCommonReportList(paginatorParam);
            return SystemSecurityMessage.getSuccessMsg("获取公报列表成功！", planList);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取公报列表失败！");
        }
    }


    /**
     * 根据公报id删除公报
     *
     * @param reportId
     * @return
     */
    @RequestMapping(value = "/deleteCommonReportInfo/{reportId}", method = RequestMethod.GET)
    public SystemSecurityMessage deleteCommonReportInfo(@PathVariable String reportId) {
        try {
            return SystemSecurityMessage.getSuccessMsg("删除公报信息成功！", analysisOfFloodService.deleteCommonReportInfo(reportId));
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除公报信息失败！");
        }
    }


    /**
     * 根据公报id查看公报详情
     * @param reportId
     * @return
     */
    @RequestMapping(value = "/getCommonReportInfo/{reportId}", method = RequestMethod.GET)
    public SystemSecurityMessage getCommonReportInfo(@PathVariable String reportId) {
        try {
            return SystemSecurityMessage.getSuccessMsg("获取公报信息成功！", analysisOfFloodService.getCommonReportInfo(reportId));
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取公报信息失败！");
        }
    }


        /**
         * 获取时段水情概况
         *
         * @return com.essence.hdfxdp.util.SystemSecurityMessage
         * @Date 14:10 2020/8/4
         * @Param []
         **/
        @RequestMapping(value = "/getWaterRegimenMessage", method = RequestMethod.POST)
        public SystemSecurityMessage getWaterRegimenMessage(@RequestBody Map map) {
            try {
                return new SystemSecurityMessage("ok", "获取站点信息成功！",analysisOfFloodService.getWaterRegimenMessage(map));
            } catch (Exception e) {
                e.printStackTrace();
                return new SystemSecurityMessage("error", "获取站点信息失败!");
            }
        }


    /**
     * 获取实时河道情况
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getRiverWayDataOnTime", method = RequestMethod.POST)
    public SystemSecurityMessage geRiverWayDataOnTime(@RequestBody Map map) {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",analysisOfFloodService.geRiverWayDataOnTime(map));
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }

        /**
         * 获取实时水库情况
         *
         * @return com.essence.hdfxdp.util.SystemSecurityMessage
         * @Date 14:10 2020/8/4
         * @Param []
         **/
        @RequestMapping(value = "/getReservoirDataOnTime", method = RequestMethod.POST)
        public SystemSecurityMessage getReservoirDataOnTime(@RequestBody Map map) {
            try {
                return new SystemSecurityMessage("ok", "获取站点信息成功！",analysisOfFloodService.getReservoirDataOnTime(map));
            } catch (Exception e) {
                e.printStackTrace();
                return new SystemSecurityMessage("error", "获取站点信息失败!");
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
            return new SystemSecurityMessage("ok", "查询成功", analysisOfFloodService.getRainDistributionList(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }



}
