package com.essence.business.xqh.api.tuoying;

import com.essence.business.xqh.api.floodForecast.dto.StcdInfoToMapIconViewDto;



import java.util.List;


/**
 * 远程调用tuoying数据库
 * @Author huangxiaoli
 * @Description
 * @Date 15:47 2020/12/31
 * @Param
 * @return
 **/
public interface TuoyingInfoService {

    /**
     * 查询水文站、雨量站
     * @param
     * @return
     */
    List<StcdInfoToMapIconViewDto> getStcdToMapIcon();


}
