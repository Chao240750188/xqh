package com.essence.business.xqh.dao.dao.tuoying;

import com.essence.business.xqh.dao.entity.tuoying.TuoyingStStbprpB;
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
public interface TuoyingStStbprpBDao extends EssenceJpaRepository<TuoyingStStbprpB,String> {


	@Query(value = "SELECT STCD,STNM,LGTD,LTTD,STTP FROM ST_STBPRP_B WHERE STTP IN ('PP','ZQ','RR','ZZ')",nativeQuery = true)
	List<Map<String,Object>> queryStcdToMapIcon();

}
