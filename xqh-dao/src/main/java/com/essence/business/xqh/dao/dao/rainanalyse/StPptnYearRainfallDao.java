package com.essence.business.xqh.dao.dao.rainanalyse;

import com.essence.business.xqh.dao.entity.rainanalyse.StPptnYearRainfall;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 年雨量(StPptnYearRainfall)表数据库访问层
 *
 * @author zry
 * @since 2020-07-04 14:06:32
 */


@Repository
public interface StPptnYearRainfallDao extends MongoRepository<StPptnYearRainfall, String> {

    public List<StPptnYearRainfall> findByTmBetween(Date startTime, Date endTime);
}