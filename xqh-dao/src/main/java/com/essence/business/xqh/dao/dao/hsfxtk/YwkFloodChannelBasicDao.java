package com.essence.business.xqh.dao.dao.hsfxtk;

import com.essence.business.xqh.dao.entity.hsfxtk.YwkFloodChannelBasic;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface YwkFloodChannelBasicDao extends EssenceJpaRepository<YwkFloodChannelBasic, String> {



    List<YwkFloodChannelBasic> findByBreakIdOrderByOutflowAndInflowTypeDesc(String breadId);
}
