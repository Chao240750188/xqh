package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkFloodChannelFlow;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface YwkFloodChannelFlowDao extends EssenceJpaRepository<YwkFloodChannelFlow, String> {


    @Query(value = "select * from YWK_FLOOD_CHANNEL_FLOW where FLOOD_CHANNEL_ID in ?1 ",nativeQuery = true)
    List<YwkFloodChannelFlow> findByChannelBasicIds(List<String> breakIds);
}
