package com.essence.business.xqh.dao.dao.tuoying;

import com.essence.business.xqh.dao.dao.tuoying.dto.HtStRsvrRViewDto;
import com.essence.business.xqh.dao.entity.tuoying.TuoyingStRsvrR;

import java.util.Date;
import java.util.List;

/**
 * 水库水情表数据访问层接口
 * LiuGt add at 2020-07-22
 */
public interface TuoyingStRsvrRDao {
    /**
     * 查询指定水库测站距离指定时间最近的一次实测数据（水位和库容）
     * @param tm
     * @param stcd
     * @return
     */
    List<HtStRsvrRViewDto> queryByLatelyTmAndStcd(Date tm, String stcd);

    /**
     * 根据测站ID查询最近一次实测数据
     * @param stcd 测站ID
     * @return
     */
    //@Query(value = "select * from st_rsvr_r where STCD = :stcd order by TM desc limit 1", nativeQuery = true)
    List<TuoyingStRsvrR> queryLastOneByStcd(String stcd);
}
