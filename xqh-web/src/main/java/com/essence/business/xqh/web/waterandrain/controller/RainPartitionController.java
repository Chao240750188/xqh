package com.essence.business.xqh.web.waterandrain.controller;

import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;
import com.essence.business.xqh.api.rainfall.vo.RainWaterReportDto;
import com.essence.business.xqh.api.waterandrain.service.RainPartitionService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
            return new SystemSecurityMessage("ok", "查询分区雨量成功", rainPartitionService.getPartRain(reqDto, false));
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

    /**
     * 保存简报信息
     *
     * @param reqDto
     * @return
     */
    @PostMapping(value = "/saveRainWaterSimpleReport")
    public SystemSecurityMessage saveRainWaterSimpleReport(@RequestBody RainWaterReportDto reqDto) {
        try {
            return new SystemSecurityMessage("ok", "保存简报数据成功", rainPartitionService.saveRainWaterSimpleReport(reqDto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "保存简报数据失败！");
        }
    }

    /**
     * 获取简报列表信息
     *
     * @param paginatorParam
     * @return
     */
    @RequestMapping(value = "/getReportList", method = RequestMethod.POST)
    public SystemSecurityMessage getReportList(@RequestBody PaginatorParam paginatorParam) {
        try {
            Paginator planList = rainPartitionService.getReportList(paginatorParam);
            return SystemSecurityMessage.getSuccessMsg("获取简报列表成功！", planList);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取简报列表失败！");

        }
    }

    /**
     * 根据简报id查看简报详情
     *
     * @param reportId
     * @return
     */
    @RequestMapping(value = "/getReportInfo/{reportId}", method = RequestMethod.GET)
    public SystemSecurityMessage getReportInfo(@PathVariable String reportId) {
        try {
            return SystemSecurityMessage.getSuccessMsg("获取简报信息成功！", rainPartitionService.getReportInfo(reportId));
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取简报信息失败！");
        }
    }

    /**
     * 根据简报id删除简报
     *
     * @param reportId
     * @return
     */
    @RequestMapping(value = "/deleteReportInfo/{reportId}", method = RequestMethod.GET)
    public SystemSecurityMessage deleteReportInfo(@PathVariable String reportId) {
        try {
            return SystemSecurityMessage.getSuccessMsg("删除简报信息成功！", rainPartitionService.deleteReportInfo(reportId));
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除简报信息失败！");
        }
    }

    /**
     * 创建成公报报告
     *
     * @param reqDto
     * @return
     */
    @PostMapping(value = "/getRainWaterCommonReport")
    public SystemSecurityMessage getRainWaterCommonReport(@RequestBody RainPartitionDto reqDto) {
        try {
            return new SystemSecurityMessage("ok", "查询公报内容成功", rainPartitionService.getRainWaterCommonReport(reqDto));
        } catch (Exception e) {
            return new SystemSecurityMessage("error", "查询公报内容失败！");
        }
    }

    /**
     * 保存公报报告
     *
     * @param reqDto
     * @return
     */
    @PostMapping(value = "/saveRainWaterCommonReport")
    public SystemSecurityMessage saveRainWaterCommonReport(@RequestBody RainWaterReportDto reqDto) {
        try {
            return new SystemSecurityMessage("ok", "保存公报内容成功", rainPartitionService.saveRainWaterCommonReport(reqDto));
        } catch (Exception e) {
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
            Paginator planList = rainPartitionService.getCommonReportList(paginatorParam);
            return SystemSecurityMessage.getSuccessMsg("获取公报列表成功！", planList);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取公报列表失败！");

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
            return SystemSecurityMessage.getSuccessMsg("获取公报信息成功！", rainPartitionService.getCommonReportInfo(reportId));
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取公报信息失败！");
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
            return SystemSecurityMessage.getSuccessMsg("删除公报信息成功！", rainPartitionService.deleteCommonReportInfo(reportId));
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除公报信息失败！");
        }
    }

}
