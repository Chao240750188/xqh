package com.essence.business.xqh.web.space;

import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.rainfall.vo.RainDzmReq;
import com.essence.business.xqh.api.space.SpaceQueryRequest;
import com.essence.business.xqh.common.propertis.RemoteProperties;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.GisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xzc
 * @version 1.0
 * @date 2021/4/23 0020 17:58
 */
@RestController
@RequestMapping("/space")
public class SpaceInfoController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RemoteProperties remoteProperties;

    /**
     * http://39.97.105.38:6080/arcgis/rest/services/xqh/xqhVecIndexIntegrate/MapServer/find
     * 查询属性
     *
     * @return
     */
    @RequestMapping(value = "/base", method = RequestMethod.POST)
    public SystemSecurityMessage base(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        GisUtil gisUtil = new GisUtil();
        String spaceQuery;
        try {
            spaceQuery = gisUtil.getSpaceQuery(remoteProperties.getVecindexintegrate(),
                    spaceQueryRequest.getSearchText(),
                    spaceQueryRequest.getSearchFields(),
                    spaceQueryRequest.getLayers());
        } catch (Exception e) {
            logger.error("调用gis异常", e);
            return new SystemSecurityMessage("error", "调用gis异常", e);
        }
        JSONObject jsonObject1 = JSONObject.parseObject(spaceQuery);
        return new SystemSecurityMessage("ok", "成功", jsonObject1);
    }

    /**
     * 查询空间
     *
     * @return
     */
    @RequestMapping(value = "/geometry", method = RequestMethod.POST)
    public SystemSecurityMessage geometry(@RequestBody SpaceQueryRequest spaceQueryRequest) {
        GisUtil gisUtil = new GisUtil();
        String spaceQuery;
        try {
            spaceQuery = gisUtil.getSpaceGeometry(remoteProperties.getIdenti(),
                    spaceQueryRequest.getGeometry(),
                    spaceQueryRequest.getLayers(),
                    spaceQueryRequest.getSr());
        } catch (Exception e) {
            logger.error("调用gis异常", e);
            return new SystemSecurityMessage("error", "调用gis异常", e);
        }
        JSONObject jsonObject1 = JSONObject.parseObject(spaceQuery);
        return new SystemSecurityMessage("ok", "成功", jsonObject1);
    }
}
