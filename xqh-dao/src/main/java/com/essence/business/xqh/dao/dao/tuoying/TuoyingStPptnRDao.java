package com.essence.business.xqh.dao.dao.tuoying;


import com.essence.business.xqh.dao.dao.floodScheduling.dto.SchedulingHourAvgRainDto;
import com.essence.business.xqh.dao.dao.floodScheduling.dto.SchedulingHourRainDto;
import com.essence.business.xqh.dao.entity.tuoying.TuoyingStPptnR;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 实时降雨表数据访问层接口
 * LiuGt add at 2020-06-28
 */
public interface TuoyingStPptnRDao {

    List<TuoyingStPptnR> findByStcdInAndTmBetween(Set<String> stcdList, Date startTime, Date endTime);


    /**
     * 查询指定测站两个时间之间的时段降雨数据（时间分组求和）
     * LiuGt add at 2020-06-29
     *
     * @return List<SchedulingHourRainDto>
     * @throws SQLException
     */
    List<SchedulingHourRainDto> queryHourRainfallByStcdsAndTowDate(LocalDateTime beginTime, LocalDateTime endTime, List<String> stcds);

    /**
     * 查询指定测站两个时间之间的时段总降雨和平均降雨数据
     * LiuGt add at 2020-07-14
     *
     * @return List<SchedulingHourAvgRainDto>
     * @throws SQLException
     */
    List<SchedulingHourAvgRainDto> queryHourAvgRainfallByStcdsAndTowDate(LocalDateTime beginTime, LocalDateTime endTime, List<String> stcds);
}
