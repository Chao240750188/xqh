package com.essence.business.xqh.dao.dao.rainfall;

import com.essence.business.xqh.dao.entity.rainfall.TRiverR;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/25 0025 16:15
 */
@Repository
public interface TRiverRDao extends MongoRepository<TRiverR, String> {

    List<TRiverR> findByStcdOrderByTmDesc(String stcd);

    List<TRiverR> findByStcdAndZOrderByTmDesc(String stcd, String z);
    
    List<TRiverR> findByStcdAndTmBetween(String stcd, Date startTime, Date endTime);

    List<TRiverR> findByStcdAndTmIsBetweenOrderByTm(String stcd, Date startTime, Date endTime);

    List<TRiverR> findByStcdInAndTmIsBetweenOrderByTm(List<String> stcds, Date startTime, Date endTime);

    TRiverR findTopByStcdOrderByZDesc(String stcd);
    
    TRiverR findTopByStcdOrderByZDescTmDesc(String stcd);
}
