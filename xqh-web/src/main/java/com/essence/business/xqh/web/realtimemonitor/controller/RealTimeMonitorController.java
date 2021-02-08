package com.essence.business.xqh.web.realtimemonitor.controller;

import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.realtimemonitor.dto.RainDataParamDto;
import com.essence.business.xqh.api.realtimemonitor.dto.ReturnPictureDto;
import com.essence.business.xqh.api.realtimemonitor.dto.ReturnWeatherForecastDto;
import com.essence.business.xqh.api.realtimemonitor.service.RealTimeMonitorService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.ConfigurationUtil;
import com.essence.business.xqh.common.util.HttpClientUtil;
import com.essence.business.xqh.common.util.ObtainQixiangImage;
import com.essence.business.xqh.common.util.QixiangImageDto;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 16:30
 */
@RestController
@RequestMapping("/RealTimeMonitor")
public class RealTimeMonitorController {

    private static final String weatherBureauType = ConfigurationUtil.getWeatherBureauType();

    private static final String weatherBureauUrl = ConfigurationUtil.getWeatherBureauUrl();

    private static final String weatherForecast = ConfigurationUtil.getWeatherForecast();

    private static final CloseableHttpClient httpclient;

    static {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(10000).setSocketTimeout(15000).build();
        httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    }

    @Autowired
    private RealTimeMonitorService realTimeMonitorService;


    /**
     * 水雨情查询-洪水告警-水库详细信息
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getReservoirFloodWarningDetailByTime", method = RequestMethod.GET)
    public SystemSecurityMessage getReservoirFloodWarningDetailByTime(@RequestParam(value = "startTime")String startTime,
                                                                     @RequestParam(value = "endTime")String endTime) {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getReservoirFloodWarningDetailByTime(startTime,endTime));
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }


    /**
     * 水雨情查询-洪水告警-水库
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getReservoirFloodWarningByTime", method = RequestMethod.GET)
    public SystemSecurityMessage getReservoirFloodWarningByTime(@RequestParam(value = "startTime")String startTime,
                                                               @RequestParam(value = "endTime")String endTime) {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getReservoirFloodWarningByTime(startTime,endTime));
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }


    /**
     * 水雨情查询-洪水告警-河道详细信息
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getWaterWayFloodWarningDetailByTime", method = RequestMethod.GET)
    public SystemSecurityMessage getWaterWayFloodWarningDetailByTime(@RequestParam(value = "startTime")String startTime,
                                                               @RequestParam(value = "endTime")String endTime) {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getWaterWayFloodWarningDetailByTime(startTime,endTime));
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }

    /**
     * 水雨情查询-洪水告警-河道
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getWaterWayFloodWarningByTime", method = RequestMethod.GET)
    public SystemSecurityMessage getWaterWayFloodWarningByTime(@RequestParam(value = "startTime")String startTime,
                                                               @RequestParam(value = "endTime")String endTime) {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getWaterWayFloodWarningByTime(startTime,endTime));
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }


    /**
     * 获取单个水库站水位信息通过时间段
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getReservoirWaterLevelByTime", method = RequestMethod.GET)
    public SystemSecurityMessage getReservoirWaterLevelByTime(@RequestParam(value = "stcd")String stcd,
                                                             @RequestParam(value = "startTime")String startTime,
                                                             @RequestParam(value = "endTime")String endTime) {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getReservoirWaterLevelByTime(stcd,startTime,endTime));
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }


    /**
     * 获取单个水库站基本信息
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getReservoirDataSingle/{stcd}", method = RequestMethod.GET)
    public SystemSecurityMessage getReservoirDataSingle(@PathVariable(value = "stcd")String stcd) {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getReservoirDataSingle(stcd));
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
    @RequestMapping(value = "/getReservoirDataOnTime", method = RequestMethod.GET)
    public SystemSecurityMessage getReservoirDataOnTime() {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getReservoirDataOnTime());
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }


    /**
     * 获取单个河道站水位信息通过时间段
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getRiverWayWaterLevelByTime", method = RequestMethod.GET)
    public SystemSecurityMessage getRiverWayWaterLevelByTime(@RequestParam(value = "stcd")String stcd,
                                                             @RequestParam(value = "startTime")String startTime,
                                                             @RequestParam(value = "endTime")String endTime) {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getRiverWayWaterLevelByTime(stcd,startTime,endTime));
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }


    /**
     * 获取单个河道站基本信息
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getRiverWayDataSingle/{stcd}", method = RequestMethod.GET)
    public SystemSecurityMessage geRiverWayDataSingle(@PathVariable(value = "stcd")String stcd) {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.geRiverWayDataSingle(stcd));
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
    @RequestMapping(value = "/getRiverWayDataOnTime", method = RequestMethod.GET)
    public SystemSecurityMessage geRiverWayDataOnTime() {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.geRiverWayDataOnTime());
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }


    /**
     * 获取实时水情概况
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getWaterRegimenMessage", method = RequestMethod.GET)
    public SystemSecurityMessage getWaterRegimenMessage() {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getWaterRegimenMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }


    /**
     * 根據時間步長和站點編號獲取降雨信息
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getRainDataByStepTimeAndID", method = RequestMethod.POST)
    public SystemSecurityMessage getRainDataByStepTimeAndID(@RequestBody RainDataParamDto rainDataParamDto) {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getRainDataByStepTimeAndID(rainDataParamDto));
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }


    /**
     * 获取站点信息
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getStationMessage", method = RequestMethod.GET)
    public SystemSecurityMessage getStationMessage() {
        try {
            return new SystemSecurityMessage("ok", "获取站点信息成功！",realTimeMonitorService.getStationMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取站点信息失败!");
        }
    }


    /**
     * 获取气象天氣信息
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/getWeatherForecast", method = RequestMethod.GET)
    public SystemSecurityMessage getWeatherForecast() {
        try {
            //调用远程接口
            String s = HttpClientUtil.sendGet(httpclient,
                    weatherForecast);
            ReturnWeatherForecastDto dto = JSONObject.parseObject(s, ReturnWeatherForecastDto.class);
            return new SystemSecurityMessage("ok", "获取雷达回波图成功！", dto.getData());
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取雷达回波图失败!");
        }
    }


    /**
     * 获取气象雷达回波图
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:10 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/obtainRadarMapQX", method = RequestMethod.GET)
    public SystemSecurityMessage obtainRadarMapQX() {
        try {
            //调用远程接口
//            List<QixiangImageDto> obtainRadarMap = getObtainRadarMap();
            ObtainQixiangImage obtainQixiangImage = new ObtainQixiangImage();
            List<QixiangImageDto> qixiangImageDtos = obtainQixiangImage.ObtainRadarMap();
            return new SystemSecurityMessage("ok", "获取雷达回波图成功！", qixiangImageDtos);
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取雷达回波图失败!");
        }
    }


    /**
     * 获取气象卫星云图
     *
     * @return com.essence.hdfxdp.util.SystemSecurityMessage
     * @Date 14:12 2020/8/4
     * @Param []
     **/
    @RequestMapping(value = "/obtainSatellitCloudImageQX", method = RequestMethod.GET)
    public SystemSecurityMessage obtainSatellitCloudImageQX() {
        try {
//            List<QixiangImageDto> obtainSatellitCloudImage = getObtainSatellitCloudImage();
            ObtainQixiangImage obtainQixiangImage = new ObtainQixiangImage();
            List<QixiangImageDto> qixiangImageDtos = obtainQixiangImage.ObtainSatellitCloudImage();
            return new SystemSecurityMessage("ok", "获取卫星云图成功！", qixiangImageDtos);
        } catch (Exception e) {
            e.printStackTrace();
            return new SystemSecurityMessage("error", "获取卫星云图失败!");
        }
    }


    /**
     * @return void
     * @Description 获取卫星云图数据接口
     * @Author xzc
     * @Date 19:54 2020/8/24
     **/
    protected List<QixiangImageDto> getObtainSatellitCloudImage() {
        List<QixiangImageDto> result = new ArrayList<>();
        String s = "";
        try {
            s = HttpClientUtil.sendGet(httpclient,
                    weatherBureauUrl + "/weatherBureau/couldPic?type=" + weatherBureauType);
            ReturnPictureDto dto = JSONObject.parseObject(s, ReturnPictureDto.class);
            result = dto.getResult();
            Collections.reverse(result);
            //替换请求路径信息
            for (QixiangImageDto qixiangImageDto : result) {
                String imageUrl = qixiangImageDto.getImageUrl();
                qixiangImageDto.setImageUrl(imageUrl.replace("/srv/program", weatherBureauUrl));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * @return void
     * @Description 获取雷达回波图数据接口
     * @Author xzc
     * @Date 19:54 2020/8/24
     **/
    protected List<QixiangImageDto> getObtainRadarMap() {
        List<QixiangImageDto> result = new ArrayList<>();
        String s = "";
        try {
            s = HttpClientUtil.sendGet(httpclient,
                    weatherBureauUrl + "/weatherBureau/radarPic?type=" + weatherBureauType);
            ReturnPictureDto dto = JSONObject.parseObject(s, ReturnPictureDto.class);
            result = dto.getResult();
            Collections.reverse(result);
            //替换请求路径信息
            for (QixiangImageDto qixiangImageDto : result) {
                String imageUrl = qixiangImageDto.getImageUrl();
                qixiangImageDto.setImageUrl(imageUrl.replace("/srv/program", weatherBureauUrl));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



}
