package com.essence.business.xqh.service.tuoying;

import com.essence.business.xqh.api.floodForecast.dto.SqybTuoyingStPptnRDto;
import com.essence.business.xqh.api.floodForecast.dto.StcdInfoToMapIconListDto;
import com.essence.business.xqh.api.floodForecast.dto.StcdInfoToMapIconViewDto;
import com.essence.business.xqh.api.tuoying.TuoyingInfoService;
import com.essence.business.xqh.api.tuoying.dto.SqybHtStRsvrRViewDto;
import com.essence.business.xqh.api.tuoying.dto.TuoyingStRsvrRDto;
import com.essence.business.xqh.dao.dao.floodScheduling.dto.SchedulingHourAvgRainDto;
import com.essence.business.xqh.dao.dao.tuoying.TuoyingStStbprpBDao;
import com.essence.business.xqh.dao.entity.tuoying.TuoyingStPptnR;
import com.essence.business.xqh.dao.entity.tuoying.TuoyingStRsvrR1;
import com.essence.business.xqh.datasource.annotation.DS;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TuoyingInfoServiceImpl implements TuoyingInfoService {


    @Autowired
    private TuoyingStStbprpBDao tuoyingStStbprpBDao;


    /**
     * 查询水文站、雨量站
     * @param
     * @return
     */
    @DS(value = "tuoying")
    @Override
    public List<StcdInfoToMapIconViewDto> getStcdToMapIcon(){
        List<StcdInfoToMapIconViewDto> stcdInfoToMapIconViewDtos = new ArrayList<>();
        List<Map<String, Object>> maps = tuoyingStStbprpBDao.queryStcdToMapIcon();
        List<StcdInfoToMapIconListDto> stcdInfoToMapIconListDtos = StcdToMapIconListMapToDtos(maps);
        //水文站
        StcdInfoToMapIconViewDto shuiwen = new StcdInfoToMapIconViewDto();
        shuiwen.setSttpName("shuiwen");
        List<StcdInfoToMapIconListDto> shuiwenList = new ArrayList<>();
        shuiwenList = stcdInfoToMapIconListDtos.stream()
                .filter(dtos -> dtos.getSttp().toLowerCase().equals("zq") || dtos.getSttp().toLowerCase().equals("zz") || dtos.getSttp().toLowerCase().equals("rr"))
                .collect(Collectors.toList());
        shuiwen.setStcdList(shuiwenList);
        stcdInfoToMapIconViewDtos.add(shuiwen);
        //雨量站
        StcdInfoToMapIconViewDto yuliang = new StcdInfoToMapIconViewDto();
        yuliang.setSttpName("yuliang");
        List<StcdInfoToMapIconListDto> yuliangList = new ArrayList<>();
        yuliangList = stcdInfoToMapIconListDtos.stream().filter(dtos -> dtos.getSttp().toLowerCase().equals("pp"))
                .collect(Collectors.toList());
        yuliang.setStcdList(yuliangList);
        stcdInfoToMapIconViewDtos.add(yuliang);
        return stcdInfoToMapIconViewDtos;
    }

    private List<StcdInfoToMapIconListDto> StcdToMapIconListMapToDtos(List<Map<String, Object>> maps){
        List<StcdInfoToMapIconListDto> stcdInfoToMapIconListDtos = new ArrayList<>();
        if (maps != null && maps.size() > 0){
            for (Map<String, Object> map : maps) {
                StcdInfoToMapIconListDto stcdInfoToMapIconListDto = new StcdInfoToMapIconListDto();
                stcdInfoToMapIconListDto.setStcd(map.get("stcd").toString());
                stcdInfoToMapIconListDto.setStnm(map.get("stnm").toString());
                Object lgtd = map.get("lgtd");
                if(null!=lgtd){
                    stcdInfoToMapIconListDto.setLgtd(lgtd.toString());
                }
                Object lttd = map.get("lttd");
                if (null!=lttd){
                    stcdInfoToMapIconListDto.setLttd(lttd.toString());
                }
                stcdInfoToMapIconListDto.setSttp(map.get("sttp").toString());
                stcdInfoToMapIconListDtos.add(stcdInfoToMapIconListDto);
            }
        }
        return stcdInfoToMapIconListDtos;
    }
}
