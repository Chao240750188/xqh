package com.essence.business.xqh.web.waterandrain.controller;

import com.essence.business.xqh.api.waterandrain.service.*;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.DateUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Date;

/**
 * @author fengpp
 * 2021/1/21 18:20
 */
@RestController
@RequestMapping(value = "/rainMonitoring")
public class RainMonitoringController {
    @Autowired
    RainMonitoringService rainMonitoringService;
    @Autowired
    WaterBriefingService waterBriefingService;
    @Autowired
    WaterLevelRangeService waterLevelRangeService;
    @Autowired
    FloodWarningService floodWarningService;
    @Autowired
    RainfallSearchService rainfallSearchService;
    @Autowired
    WaterCompareAnalysisService waterCompareAnalysisService;

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
            e.printStackTrace();
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
            return new SystemSecurityMessage("ok", "查询成功", floodWarningService.getSluiceFloodWarning(dto));
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
            return new SystemSecurityMessage("ok", "查询成功", floodWarningService.getTideFloodWarning(dto));
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
            return new SystemSecurityMessage("ok", "查询成功", floodWarningService.getSluiceFloodWarningList(dto));
        } catch (Exception e) {
            e.printStackTrace();
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
            return new SystemSecurityMessage("ok", "查询成功", floodWarningService.getTideFloodWarningList(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水情服务-水情简报表
     *
     * @param year
     * @param mth
     * @return
     */
    @GetMapping(value = "/getWaterBriefList/{year}/{mth}")
    public SystemSecurityMessage getWaterBriefList(@PathVariable(name = "year") Integer year,
                                                   @PathVariable(name = "mth") Integer mth) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", waterBriefingService.getWaterBriefList(year, mth));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水情服务-导出水情简报表
     *
     * @param year
     * @param mth
     * @return
     */
    @GetMapping(value = "/exportWaterBriefList/{year}/{mth}")
    public void exportWaterBriefList(@PathVariable(name = "year") Integer year,
                                     @PathVariable(name = "mth") Integer mth, HttpServletResponse response) {
        try {
            InputStream in = getClass().getResourceAsStream("/static/exceltemplate/briefTemplate.xlsx");
            //生成数据
            Workbook wb = waterBriefingService.exportWaterBriefList(year, mth,in);
            //响应尾
            String fileName = "水情简报表_" + year + mth + ".xlsx";
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("content-type:octet-stream");
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
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
            return new SystemSecurityMessage("ok", "查询成功", waterBriefingService.getRiverList(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水情服务-导出河道水情表
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/exportRiverList")
    public void exportRiverList(@RequestBody QueryParamDto dto, HttpServletResponse response) {
        try {
            InputStream in = getClass().getResourceAsStream("/static/exceltemplate/riverTemplate.xlsx");
            //生成数据
            Workbook wb = waterBriefingService.exportRiverList(dto,in);
            String time = DateUtil.dateToStringNormal3(dto.getEndTime());
            //响应尾
            String fileName = "河道水情表_" + time + ".xlsx";
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("content-type:octet-stream");
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
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
            return new SystemSecurityMessage("ok", "查询成功", waterBriefingService.getReservoirList(dto));
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水情服务-导出水库水情表
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/exportReservoirList")
    public void exportReservoirList(@RequestBody QueryParamDto dto,HttpServletResponse response) {
        try {
            InputStream in = getClass().getResourceAsStream("/static/exceltemplate/reservoirTemplate.xlsx");
            //生成数据
            Workbook wb = waterBriefingService.exportReservoirList(dto,in);
            String time = DateUtil.dateToStringNormal3(dto.getEndTime());
            //响应尾
            String fileName = "水库水情表_" + time + ".xlsx";
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("content-type:octet-stream");
            OutputStream ouputStream = response.getOutputStream();
            wb.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 水位变幅-水位变幅
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getWaterLevelRange")
    public SystemSecurityMessage getWaterLevelRange(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", waterLevelRangeService.getWaterLevelRange(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水位变幅-最大变幅
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getWaterLevelMaxRange")
    public SystemSecurityMessage getWaterLevelMaxRange(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", waterLevelRangeService.getWaterLevelMaxRange(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 最大变幅-水库、河道、闸坝、潮汐-模态框-最新
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getWaterLevelMaxChange")
    public SystemSecurityMessage getWaterLevelMaxChange(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", waterLevelRangeService.getWaterLevelMaxChange(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水位变幅-水库-模态框-最新
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getReservoirWaterLevelChange")
    public SystemSecurityMessage getReservoirWaterLevelChange(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", waterLevelRangeService.getReservoirWaterLevelChange(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水位变幅-闸坝，潮汐，河道-模态框-最新
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getWaterLevelChange")
    public SystemSecurityMessage getWaterLevelChange(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", waterLevelRangeService.getWaterLevelChange(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }


    /**
     * 日雨量
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getDayRainfall")
    public SystemSecurityMessage getDayRainfall(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.getDayRainfall(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 时段雨量
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getTimeRainfall")
    public SystemSecurityMessage getTimeRainfall(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.getDayRainfall(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 旬雨量
     *
     * @param year
     * @param mth
     * @param prdtp
     * @return
     */
    @GetMapping(value = "/getTenDaysRainfall/{year}/{mth}/{prdtp}")
    public SystemSecurityMessage getTenDaysRainfall(@PathVariable(name = "year") Integer year,
                                                    @PathVariable(name = "mth") Integer mth,
                                                    @PathVariable(name = "prdtp") Integer prdtp) {
        try {
            //return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.getMonthRainfall(year, mth, prdtp));
            QueryParamDto dto = new QueryParamDto();
            Date startTime = DateUtil.getDateByStringNormal(year+"/"+mth+"/01 00:00:00");

            Date endTime = null;

            switch (prdtp){
                case 1:
                    endTime = DateUtil.getNextDay(startTime,10);
                    break;
                case 2:
                    startTime = DateUtil.getNextDay(startTime,10);
                    endTime = DateUtil.getNextDay(startTime,10);
                    break;
                case 3:
                    startTime = DateUtil.getNextDay(startTime,20);
                    endTime = DateUtil.getMonthEndDay(startTime);
                    break;
            }
            dto.setStartTime(startTime);
            dto.setEndTime(endTime);

            return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.getDayRainfall(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 月雨量
     *
     * @param year
     * @param mth
     * @return
     */
    @GetMapping(value = "/getMonthRainfall/{year}/{mth}")
    public SystemSecurityMessage getMonthRainfall(@PathVariable(name = "year") Integer year,
                                                  @PathVariable(name = "mth") Integer mth) {
        try {
            //return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.getMonthRainfall(year, mth, 4));
            QueryParamDto dto = new QueryParamDto();
            Date startTime = DateUtil.getDateByStringNormal(year+"/"+mth+"/01 00:00:00");
            Date endTime = DateUtil.getMonthEndDay(startTime);

            dto.setStartTime(startTime);
            dto.setEndTime(endTime);
            return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.getDayRainfall(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }


    /**
     * 日雨量-单个站点雨量过程线
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getDayRainfallTendency")
    public SystemSecurityMessage getDayRainfallTendency(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.getDayRainfallTendency(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 时段雨量-单个站点雨量过程线
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getTimeRainfallTendency")
    public SystemSecurityMessage getTimeRainfallTendency(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.getTimeRainfallTendency(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 旬雨量-单个站点雨量过程线
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getTenDaysRainfallTendency")
    public SystemSecurityMessage getTenDaysRainfallTendency(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.getTenDaysRainfallTendency(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 月雨量-单个站点雨量过程线
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getMonthRainfallTendency")
    public SystemSecurityMessage getMonthRainfallTendency(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.getMonthRainfallTendency(dto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }


    /**
     * 雨量信息查询-雨量对比分析
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/rainfallCompareAnalysis")
    public SystemSecurityMessage rainfallCompareAnalysis(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", rainfallSearchService.rainfallCompareAnalysis(dto));
        } catch (Exception exception) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水情信息查询-水情对比分析-左侧树
     *
     * @return
     */
    @PostMapping(value = "/getStnmList")
    public SystemSecurityMessage getStnmList(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", waterCompareAnalysisService.getStnmList(dto));
        } catch (Exception exception) {
            return new SystemSecurityMessage("error", "查询失败");
        }

    }

    /**
     * 水情信息查询-水情对比分析-水位
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getWaterLevelTendency")
    public SystemSecurityMessage getWaterLevelTendency(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", waterCompareAnalysisService.getWaterLevelTendency(dto, "level"));
        } catch (Exception exception) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

    /**
     * 水情信息查询-水情对比分析-流量
     *
     * @param dto
     * @return
     */
    @PostMapping(value = "/getWaterFlowTendency")
    public SystemSecurityMessage getWaterFlowTendency(@RequestBody QueryParamDto dto) {
        try {
            return new SystemSecurityMessage("ok", "查询成功", waterCompareAnalysisService.getWaterLevelTendency(dto, "flow"));
        } catch (Exception exception) {
            return new SystemSecurityMessage("error", "查询失败");
        }
    }
}
