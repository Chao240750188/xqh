package com.essence.business.xqh.web.rainanalyse.controller;

import com.essence.business.xqh.api.rainanalyse.service.StPptnCompareService;
import com.essence.business.xqh.api.rainanalyse.vo.RainCompareAnalyseReq;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.LocalDateUtils;
import com.essence.framework.util.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName RainfallAnalyseContorller
 * @Description 雨量对比分析
 * @Author zhichao.xing
 * @Date 2020/7/4 16:57
 * @Version 1.0
 **/
@RestController
@RequestMapping("rainCompareAnalyse")
public class RainCompareAnalyseController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    StPptnCompareService stPptnCompareService;

    /**
     * 雨量对比分析
     * @return TODO 已测试
     */
    @PostMapping("/getSummary")
    public SystemSecurityMessage getWaterStation(@RequestBody RainCompareAnalyseReq req) {
        logger.info("开始执行雨量对比分析={}", req.toString());
        String format = "";
        if (req.getType().compareTo(3) == 0) {
            format = "yyyy-MM";
        } else if (req.getType().compareTo(2) == 0) {
            format = "yyyy-MM-dd";
        } else if (req.getType().compareTo(1) == 0) {
            format = "yyyy-MM-dd HH";
        }
        Map<String, Double> summaryAnalyse = stPptnCompareService.findSummaryAnalyse(req, format);
        Map<String, Double> result = new LinkedHashMap<>();
        summaryAnalyse.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> result.put(e.getKey(), e.getValue()));
        dealWithNull(result, req);
        return new SystemSecurityMessage("ok", "查询成功", result);
    }

    private void dealWithNull(Map<String, Double> result, RainCompareAnalyseReq req) {
        if (req.getType().compareTo(3) == 0) {
            //月
            List<Integer> yearList = req.getYearList();
            yearList.forEach(year -> {
                Date startDate = DateUtil.getDateByStringDay(year + "-01-01");
                LocalDateTime dateTime = LocalDateUtils.date2LocalDateTime(startDate);
                String format = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM"));
                for (int i = 0; i < 11; i++) {
                    boolean b = result.containsKey(format);
                    if (!b) {
                        result.put(format, 0.0D);
                    }
                    dateTime.plusMonths(1);
                }
            });
        } else if (req.getType().compareTo(2) == 0) {
            //天
            List<Integer> yearList = req.getYearList();
            yearList.forEach(year -> {
                Date startDate = DateUtil.getDateByStringDay(year + "-01-01");
                LocalDateTime dateTime = LocalDateUtils.date2LocalDateTime(startDate);
                String format = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                for (int i = 0; i < 400; i++) {
                    boolean b = result.containsKey(format);
                    if (!b) {
                        result.put(format, 0.0D);
                    }
                    dateTime.plusDays(1);
                    String format1 = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    if (format.contains("02-29")) {
                        //02-29 去掉
                        result.remove(format);
                    }
                    if (format1.contains("01-01")) {
                        continue;
                    }
                }
            });

        } else if (req.getType().compareTo(1) == 0) {
            //小时
            List<Integer> yearList = req.getYearList();
            yearList.forEach(year -> {
                Date startDate = DateUtil.getDateByStringDay(year + "-01-01");
                LocalDateTime dateTime = LocalDateUtils.date2LocalDateTime(startDate);
                String format = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
                for (int i = 0; i < 370 * 24; i++) {
                    boolean b = result.containsKey(format);
                    if (!b) {
                        result.put(format, 0.0D);
                    }
                    dateTime.plusHours(1);
                    String format1 = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH"));
                    if (format.contains("02-29")) {
                        //02-29 去掉
                        result.remove(format);
                    }
                    if (i > 24 && format1.contains("-01-01")) {
                        continue;
                    }
                }
            });
        }
    }
}
