package com.essence.business.xqh.web.rainfall.controller;

import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.rainfall.dto.dzm.StationRainDto;
import com.essence.business.xqh.api.rainfall.dto.dzm.StationRainVgeDto;
import com.essence.business.xqh.api.rainfall.service.RainFallDzmBetweenService;
import com.essence.business.xqh.api.rainfall.service.RainFallDzmGtDateService;
import com.essence.business.xqh.api.rainfall.vo.RainDzmReq;
import com.essence.business.xqh.common.propertis.RemoteProperties;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.GisUtil;
import com.essence.business.xqh.common.util.TimeCompute;
import com.essence.business.xqh.dao.entity.rainfall.DzmReqGis;
import com.essence.business.xqh.dao.entity.rainfall.HdyqDzmGisResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/20 0020 17:58
 */
@RestController
@RequestMapping("/rainFallDzm")
public class RainFallDzmController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RainFallDzmBetweenService rainFallDzmBetweenService;

    @Autowired
    RainFallDzmGtDateService rainFallDzmGtDateService;

    @Autowired
    RemoteProperties remoteProperties;

    /**
     * 所有雨量站前x小时 - 等值面
     * 0 是当天
     *
     * @return
     */
    @RequestMapping(value = "/getAllStationRainDataDzm", method = RequestMethod.POST)
    public SystemSecurityMessage getAllStationRainDataXHoursDzm(@RequestBody RainDzmReq req) {
        boolean debugEnabled = logger.isDebugEnabled();
        StationRainVgeDto dto = null;
        Integer hours = req.getHours();
        //时段雨量
        if (hours.compareTo(-1) == 0) {
            req.setHours(24);
            dto = rainFallDzmBetweenService.getAllStationBetweenTotalRainfall(req);
        } else {
            // 当天 默认等级按照24小时的计算
            if (hours.compareTo(0) == 0) {
                req.setHours(24);
                req.setStartTime(getStartDate());
                dto = rainFallDzmGtDateService.getAllStationGtDateTotalRainfall(req);
            } else {
                //前几个小时的
                Date endTime = new Date();
                Date preXHourDate = TimeCompute.TimeAdd(endTime, Calendar.HOUR, -1 * hours);
                req.setStartTime(preXHourDate);
                dto = rainFallDzmGtDateService.getAllStationGtDateTotalRainfall(req);
            }
        }
        if (debugEnabled) {
            logger.debug(JSONObject.toJSONString(dto));
        }
        DzmReqGis dzmReqGis = new DzmReqGis();
        dzmReqGis.setDisplayFieldName("");
        dzmReqGis.setGeometryType("esriGeometryPoint");
        DzmReqGis.FieldAliasesBean fieldAliasesBean = new DzmReqGis.FieldAliasesBean();
        fieldAliasesBean.setDrp("drp");
        fieldAliasesBean.setStcd("stcd");
        fieldAliasesBean.setStnm("stnm");
        fieldAliasesBean.setLgtd("lgtd");
        fieldAliasesBean.setLttd("lttd");
        fieldAliasesBean.setFID("FID");
        dzmReqGis.setFieldAliases(fieldAliasesBean);
        dzmReqGis.setSpatialReference(new DzmReqGis.SpatialReferenceBean(4326, 4326));
        List<DzmReqGis.FieldsBean> fields = new ArrayList<>();
        fields.add(new DzmReqGis.FieldsBean("FID", "esriFieldTypeOID", "FID"));
        fields.add(new DzmReqGis.FieldsBean("stcd", "esriFieldTypeDouble", "stcd"));
        fields.add(new DzmReqGis.FieldsBean("stnm", "esriFieldTypeString", "stnm", 254));
        fields.add(new DzmReqGis.FieldsBean("lgtd", "esriFieldTypeDouble", "stnm"));
        fields.add(new DzmReqGis.FieldsBean("lttd", "esriFieldTypeDouble", "lttd"));
        fields.add(new DzmReqGis.FieldsBean("drp", "esriFieldTypeDouble", "drp"));
        dzmReqGis.setFields(fields);
        List<DzmReqGis.FeaturesBean> featuresBeanList = new ArrayList<>();
        dzmReqGis.setFeatures(featuresBeanList);
        List<StationRainDto> list = dto.getList();
        if (!CollectionUtils.isEmpty(list)) {
            AtomicInteger atomicInteger = new AtomicInteger(1);
            list.forEach(item -> {
                DzmReqGis.FeaturesBean featuresBean = new DzmReqGis.FeaturesBean();
                DzmReqGis.FeaturesBean.AttributesBean attributesBean = new DzmReqGis.FeaturesBean.AttributesBean();
                attributesBean.setDrp(item.getP());
                DzmReqGis.FeaturesBean.GeometryBean geometryBean = new DzmReqGis.FeaturesBean.GeometryBean();
                if (item.getLgtd()!=null){
                    attributesBean.setLgtd(item.getLgtd());
                    attributesBean.setLttd(item.getLttd());
                    geometryBean.setX(item.getLgtd());
                    geometryBean.setY(item.getLttd());
                }
                attributesBean.setStnm(item.getStnm());
                attributesBean.setFID(atomicInteger.getAndIncrement());
                featuresBean.setAttributes(attributesBean);
                featuresBean.setGeometry(geometryBean);
                if (item.getLgtd()!=null){
                    featuresBeanList.add(featuresBean);
                }
            });
            atomicInteger.set(1);
        }
        GisUtil gisUtil = new GisUtil();
        String reclassification = "0.1 9.9 1;9.9 24.9 2;24.9 49.9 3;49.9 99.9 4;99.9 199.9 5;199.9 9999 6";
        if (hours.compareTo(1) == 0) {
            reclassification = "0.1 2.49 1;2.49 7.9 2;7.9 16 3;16 20 4;20 9999 5";
        } else if (hours.compareTo(2) == 0) {
            reclassification = "0.1 3.9 1;3.9 11.9 2;11.9 24.9 3;24.9 54.9 4;54.9 89.9 5;89.9 9999 6";
        } else if (hours.compareTo(3) == 0) {
            reclassification = "0.1 3.9 1;3.9 11.9 2;11.9 24.9 3;24.9 54.9 4;54.9 89.9 5;89.9 9999 6";
        } else if (hours.compareTo(12) == 0) {
            reclassification = "0.1 4.9 1;4.9 14.9 2;14.9 29.9 3;29.9 69.9 4;69.9 139.9 5;139.9 9999 6";
        } else if (hours.compareTo(24) == 0) {
            reclassification = "0.1 9.9 1;9.9 24.9 2;24.9 49.9 3;49.9 99.9 4;99.9 199.9 5;199.9 9999 6";
        } else if (hours.compareTo(0) == 0) {
            //当天
            reclassification = "0.1 9.9 1;9.9 24.9 2;24.9 49.9 3;49.9 99.9 4;99.9 199.9 5;199.9 9999 6";
        }
        String hdyqDzm = null;
        try {
            if (debugEnabled) {
                logger.debug(reclassification);
                logger.debug(JSONObject.toJSONString(dzmReqGis));
            }
            if (!CollectionUtils.isEmpty(featuresBeanList)) {
                hdyqDzm = gisUtil.getHdyqDzm(remoteProperties.getYqdzm(), reclassification, JSONObject.toJSONString(dzmReqGis));
            } else {
                logger.error("featuresBeanList is empty,不执行等值面的调用");
            }
        } catch (Exception e) {
            logger.error("远程调用等值面异常url={}", remoteProperties.getYqdzm(), e);
        }
        HdyqDzmGisResp resp = JSONObject.parseObject(hdyqDzm, HdyqDzmGisResp.class);
        return new SystemSecurityMessage("ok", "查询所有雨量站前" + hours + "小时累计雨量的等值面成功！", resp);
    }

    private Date getStartDate() {
        Date dateNow = new Date();
        Date date = new Date();
        date.setDate(date.getDate());
        date.setHours(8);
        date.setMinutes(0);
        date.setSeconds(0);
        Date startDate = new Date();
        if (dateNow.after(date)) {
            startDate = date;
        } else {
            Date date2 = new Date();
            date2.setDate(date.getDate() - 1);
            date2.setHours(8);
            date2.setMinutes(0);
            date2.setSeconds(0);
            startDate = date2;
        }
        return startDate;
    }
}
