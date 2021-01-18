package com.essence.business.xqh.web.rainanalyse.controller;

import com.essence.business.xqh.api.rainanalyse.service.BaseStPptnRainfallService;
import com.essence.business.xqh.api.rainanalyse.vo.RainAnalyseReq;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.dao.dao.rainfall.TStbprpBOldDao;
import com.essence.framework.jpa.Paginator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @ClassName RainfallAnalyseContorller
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/7/4 16:57
 * @Version 1.0
 **/
@Slf4j
@RestController
@RequestMapping("rainfallAnalyse")
public class RainfallAnalyseController {

    @Autowired
    BaseStPptnRainfallService baseStPptnRainfallService;

    @Autowired
    TStbprpBOldDao stbprpBDao;

    /**
     * 雨晴分析
     *
     * @return TODO 已测试
     */
    @PostMapping("/getSummary")
    public SystemSecurityMessage getRainFallTrend(@RequestBody RainAnalyseReq req) {
        try {
            List<Object> objectList = baseStPptnRainfallService.getRainfallByTypeNew(req);
            Paginator<Object> p = new Paginator(req.getCurrentPage(), req.getPageSize());
            p.setTotalCount(objectList.size());
            int start = (req.getCurrentPage() - 1) * req.getPageSize();
            int end = start + req.getPageSize();
            if (end > objectList.size()) {
                end = objectList.size();
            }
            p.setItems(objectList.subList(start, end));
            return new SystemSecurityMessage("ok", "查询成功", p);
        } catch (Exception e) {
            log.error("雨晴多维分析报错" + e.getMessage(), e);
            return new SystemSecurityMessage("error", "查询失败");
        }
    }

}
