package com.essence.business.xqh.web.rainfall.controller;


import com.essence.business.xqh.api.rainfall.service.RainFallService;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/20 0020 17:58
 */
@RestController
@RequestMapping("/rainFall")
public class RainFallController {

    @Autowired
    RainFallService rainFallService;

    /**
     * 根据时间获取所有雨量站降雨数据包括1h，2h，3h，12h 24h（实时雨情）
     * @returnF  TODO 已测试
     */
    @PostMapping("/getRainFallAllByTime")
    public SystemSecurityMessage getRainFallAllByTime(@RequestBody QueryParamDto dto){
        try {
            return new SystemSecurityMessage("ok","查询成功",rainFallService.getRainFallAllByTime(dto));
        }catch (Exception e){
            e.printStackTrace();
            return  new SystemSecurityMessage("error","查询失败");
        }
    }

    /**
     * 查询单个雨量站1,2,3,12,24小时雨量数据
     * @return TODO 已测试
     */
    @PostMapping("/getStationRainFallByTime")
    public SystemSecurityMessage getStationRainFallByTime(@RequestBody QueryParamDto dto){
        try {
            return new SystemSecurityMessage("ok","查询成功",rainFallService.getStationRainFallByTime(dto));
        }catch (Exception e){
            e.printStackTrace();
            return  new SystemSecurityMessage("error","查询失败");
        }
    }


    /**
     * 根据时间获取所有水位站的实时水位数据
     * @return TODO 已测试
     */
    @PostMapping("/getWaterLevelByTime")
    public SystemSecurityMessage getWaterLevelByTime(@RequestBody QueryParamDto dto){
        try {
            return new SystemSecurityMessage("ok","查询成功",rainFallService.getWaterLevelByTime(dto));
        }catch (Exception e){
            e.printStackTrace();
            return  new SystemSecurityMessage("error","查询失败");
        }
    }


    /**
     * 根据水位站和时间获取水位数据
     * @return TODO 已测试
     */
    @PostMapping("/getWaterLevelByStationAndTime")
    public SystemSecurityMessage getWaterLevelByStationAndTime(@RequestBody QueryParamDto dto){
        try {
            return new SystemSecurityMessage("ok","查询成功",rainFallService.getWaterLevelByStationAndTime(dto));
        }catch (Exception e){
            e.printStackTrace();
            return  new SystemSecurityMessage("error","查询失败");
        }
    }

}
