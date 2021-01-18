package com.essence.business.xqh.dao.dao.floodForecast;

import com.essence.business.xqh.dao.entity.floodForecast.SqybStStbprpB;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 水库名称
 * @author NoBugNoCode
 *
 * 2019年10月25日 上午10:42:23
 */
@Repository
public interface SqybStStbprpBDao extends EssenceJpaRepository<SqybStStbprpB,String> {

	List<SqybStStbprpB> findByStcdIn(List<String> stcdList);

	@Query(value = "select STCD,STNM,LGTD,LTTD,STTP from ST_STBPRP_B_OLD where STTP in ('PP','ZQ','RR','ZZ')",nativeQuery = true)
	List<Map<String,Object>> queryStcdToMapIcon();

}
