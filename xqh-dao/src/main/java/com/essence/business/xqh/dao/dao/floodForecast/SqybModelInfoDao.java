package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybModelInfo;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SqybModelInfoDao extends EssenceJpaRepository<SqybModelInfo,String> {

}
