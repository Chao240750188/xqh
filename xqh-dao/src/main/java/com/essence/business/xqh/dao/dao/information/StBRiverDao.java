package com.essence.business.xqh.dao.dao.information;

import com.essence.business.xqh.dao.dao.information.dto.StBRiverDto;
import com.essence.business.xqh.dao.entity.information.StBRiver;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/7/24 0024 10:56
 */
@Repository
public interface StBRiverDao extends EssenceJpaRepository<StBRiver, String> {


    @Query(value = "select  new com.essence.business.xqh.dao.dao.information.dto.StBRiverDto(t.id,t.river) from StBRiver t order by  t.level asc,t.id asc")
    public List<StBRiverDto> findAllRiverData();

    @Query(value = "select t from StBRiver t where t.id=?1")
    public StBRiver findDataById(String id);
}
