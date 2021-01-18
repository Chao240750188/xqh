package com.essence.business.xqh.web.rainfall.controller;


import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.ObtainQixiangImage;
import com.essence.business.xqh.common.util.QixiangImageDto;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/7/7 0007 13:22
 */
@RestController
@RequestMapping("/ledDirver")
public class LedDriverController {




    /**
     * 获取雷达回波图--刷新频率6分钟
     * @Author huangxiaoli
     * @Description
     * @Date 10:05 2020/11/5
     * @Param [flag]
     * @return com.essence.tzsyq.util.SystemSecurityMessage
     **/
    @RequestMapping(value = "/obtainRadarMap", method = RequestMethod.GET)
    public SystemSecurityMessage obtainRadarMap() {
        try {
            ObtainQixiangImage obtainQixiangImage = new ObtainQixiangImage();
            List<QixiangImageDto> qixiangImageDtos = obtainQixiangImage.ObtainRadarMap();
            return new SystemSecurityMessage("ok", "查询成功!", qixiangImageDtos);
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取雷达回波图失败!");
        }
    }


    /**
     * 获取卫星云图--刷新频率30分钟和1小时
     * @Author huangxiaoli
     * @Description
     * @Date 10:07 2020/11/5
     * @Param [flag]
     * @return com.essence.tzsyq.util.SystemSecurityMessage
     **/
    @RequestMapping(value = "/obtainSatellitCloudImage", method = RequestMethod.GET)
    public SystemSecurityMessage obtainSatellitCloudImage() {
        try {

            ObtainQixiangImage obtainQixiangImage = new ObtainQixiangImage();
            List<QixiangImageDto> qixiangImageDtos = obtainQixiangImage.ObtainSatellitCloudImage();
            return new SystemSecurityMessage("ok", "获取卫星云图成功！", qixiangImageDtos);
        } catch (Exception e) {
           e.printStackTrace();
            return new SystemSecurityMessage("error", "获取卫星云图失败!");
        }
    }









}
