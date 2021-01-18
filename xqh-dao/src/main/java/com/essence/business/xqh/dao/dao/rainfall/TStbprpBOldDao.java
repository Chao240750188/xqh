package com.essence.business.xqh.dao.dao.rainfall;

import com.essence.business.xqh.dao.dao.rainfall.dto.FxEventTypeDetailInfoDto;
import com.essence.business.xqh.dao.entity.rainfall.TStbprpBOld;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

//import com.essence.tzsyq.fxWorkDisposal.dto.FxEventTypeDetailInfoDto;

/**
 * @author Stack
 * @version 1.0
 * @date 2020/5/25 0025 14:31
 */
@Repository
public interface TStbprpBOldDao extends EssenceJpaRepository<TStbprpBOld, String> {

    @Query(value = "select t from TStbprpBOld t where t.sttp=?1 and t.usfl=1")
    public List<TStbprpBOld> findBySttp(String sttp);

    @Query(value = "select t from TStbprpBOld t where t.admauth in(?1) and t.usfl=1")
    List<TStbprpBOld> findByAdmauthIn(List<String> source);

    @Query(value = "select t from TStbprpBOld t where t.admauth in(?1) and t.sttp=?2 and t.usfl=1")
    List<TStbprpBOld> findByAdmauthInAndSttp(List<String> source, String sttp);

    @Query(value = "select t from TStbprpBOld t where t.admauth in(?1) and t.sttp=?2 and t.usfl=1")
    List<TStbprpBOld> findUseStationByAdmauthInAndSttp(List<String> source, String sttp);

    List<TStbprpBOld> findByUsflIsAndAdmauthIn(String usfl, List<String> source);

    TStbprpBOld findByStcd(String stcd);

    List<TStbprpBOld> findByUsfl(String usfl);

    List<TStbprpBOld> findByName(String name);

    /**
     * 查询数据
     * @Author huangxiaoli
     * @Description
     * @Date 11:05 2020/8/20
     * @Param [sttp]
     * @return java.util.List<com.essence.tzsyq.fxWorkDisposal.dto.FxEventTypeDetailInfoDto>
     **/
    @Query(value = "select new com.essence.business.xqh.dao.dao.rainfall.dto.FxEventTypeDetailInfoDto(t.stcd,t.stnm,t.lgtd,t.lttd,t.stlc) from TStbprpBOld t where t.sttp=?1 and t.usfl='1'")
    public List<FxEventTypeDetailInfoDto> findEventTypeBySttp(String sttp);






    /**
     * 查询正在启用的雨量测站的信息
     * @Author huangxiaoli
     * @Description
     * @Date 16:11 2020/9/2
     * @Param [addvcd, sttp]
     * @return java.util.List<com.essence.tzsyq.rainfall.entity.TStbprpB>
     **/
    @Query(value = "SELECT T.* FROM ST_STBPRP_B_OLD T LEFT JOIN ST_STSMTASK_B_OLD M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.PFL=1 AND T.ADDVCD=?1 AND T.STTP=?2 ORDER BY  T.ADDVCD ASC,T.STCD ASC",nativeQuery = true)
    public List<TStbprpBOld> findUseAddvcdRainStbprpB(String addvcd, String sttp);


    @Query(value = "SELECT T.* FROM ST_STBPRP_B_OLD T LEFT JOIN ST_STSMTASK_B_OLD M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.PFL=1 AND T.STTP=?1 ORDER BY  T.ADDVCD ASC,T.STCD ASC",nativeQuery = true)
    public List<TStbprpBOld> findUseRainStbprpB(String sttp);


    /**
     * 查询正在启用的水位测站的信息
     * @Author huangxiaoli
     * @Description
     * @Date 10:26 2020/9/3
     * @Param [addvcd, sttp]
     * @return java.util.List<com.essence.tzsyq.rainfall.entity.TStbprpB>
     **/
    @Query(value = "SELECT T.* FROM ST_STBPRP_B_OLD T LEFT JOIN ST_STSMTASK_B_OLD M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.ZFL=1 AND T.RVNM=?1 AND T.STTP=?2 ORDER BY  T.RVNM ASC,T.STCD ASC",nativeQuery = true)
    public List<TStbprpBOld> findUseAddvcdWaterLevelStbprpB(String rvnm, String sttp);


    @Query(value = "SELECT T.* FROM ST_STBPRP_B_OLD T LEFT JOIN ST_STSMTASK_B_OLD M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.ZFL=1 AND T.STTP=?1 ORDER BY  T.RVNM ASC,T.STCD ASC",nativeQuery = true)
    public List<TStbprpBOld> findUseWaterLevelStbprpB(String sttp);


    @Modifying
    @Query(value = "delete from TStbprpBOld t where t.stcd=?1")
    public void deleteByStcd(String stcd);

    @Query(value = "SELECT T.* FROM ST_STBPRP_B_RELATE B LEFT JOIN ST_STBPRP_B_OLD T ON B.STCD=T.STCD WHERE T.STTP=?1 AND T.STNM LIKE ?2 AND T.USFL=?3 ORDER BY T.STNM ASC",nativeQuery = true)
    public List<TStbprpBOld> findSelfBuiltBySttpAndStnmAndUsfl(String sttp, String stnm, String usfl);


    @Query(value = "SELECT T.* FROM ST_STBPRP_B_RELATE B LEFT JOIN ST_STBPRP_B_OLD T ON B.STCD=T.STCD WHERE T.STTP=?1 AND T.STCD IN(?2) AND T.STNM LIKE ?3 AND T.USFL=?4 ORDER BY T.STNM ASC",nativeQuery = true)
    public List<TStbprpBOld> findSelfBuiltBySttpAndStcdInAndStnmAndUsfl(String sttp, List<String> stcdList, String stnm, String usfl);
}
