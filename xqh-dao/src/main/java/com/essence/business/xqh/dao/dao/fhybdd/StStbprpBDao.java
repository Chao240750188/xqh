package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.dao.rainfall.dto.FxEventTypeDetailInfoDto;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface StStbprpBDao extends EssenceJpaRepository<StStbprpB, String> {
    @Query(value = "select t from StStbprpB t where t.sttp=?1 and t.usfl=1")
    public List<StStbprpB> findBySttp(String sttp);

    @Query(value = "select t from StStbprpB t where t.admauth in(?1) and t.usfl=1")
    List<StStbprpB> findByAdmauthIn(List<String> source);

    @Query(value = "select t from StStbprpB t where t.admauth in(?1) and t.sttp=?2 and t.usfl=1")
    List<StStbprpB> findByAdmauthInAndSttp(List<String> source, String sttp);

    @Query(value = "select t from StStbprpB t where t.admauth in(?1) and t.sttp=?2 and t.usfl=1")
    List<StStbprpB> findUseStationByAdmauthInAndSttp(List<String> source, String sttp);

    List<StStbprpB> findByUsflIsAndAdmauthIn(String usfl, List<String> source);

    StStbprpB findByStcd(String stcd);

    List<StStbprpB> findByUsfl(String usfl);

    List<StStbprpB> findByStnm(String name);

    /**
     * 查询数据
     *
     * @return java.util.List<com.essence.tzsyq.fxWorkDisposal.dto.FxEventTypeDetailInfoDto>
     * @Author huangxiaoli
     * @Description
     * @Date 11:05 2020/8/20
     * @Param [sttp]
     **/
    @Query(value = "select new com.essence.business.xqh.dao.dao.rainfall.dto.FxEventTypeDetailInfoDto(t.stcd,t.stnm,t.lgtd,t.lttd,t.stlc) from StStbprpB t where t.sttp=?1 and t.usfl='1'")
    public List<FxEventTypeDetailInfoDto> findEventTypeBySttp(String sttp);

    /**
     * 查询正在启用的雨量测站的信息
     *
     * @return java.util.List<com.essence.tzsyq.rainfall.entity.TStbprpB>
     * @Author huangxiaoli
     * @Description
     * @Date 16:11 2020/9/2
     * @Param [addvcd, sttp]
     **/
    @Query(value = "SELECT T.* FROM ST_STBPRP_B T LEFT JOIN ST_STSMTASK_B M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.PFL=1 AND T.ADDVCD=?1 AND T.STTP=?2 ORDER BY  T.ADDVCD ASC,T.STCD ASC", nativeQuery = true)
    public List<StStbprpB> findUseAddvcdRainStbprpB(String addvcd, String sttp);


    @Query(value = "SELECT T.* FROM ST_STBPRP_B T LEFT JOIN ST_STSMTASK_B M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.PFL=1 AND T.STTP=?1 ORDER BY  T.ADDVCD ASC,T.STCD ASC", nativeQuery = true)
    public List<StStbprpB> findUseRainStbprpB(String sttp);


    /**
     * 查询正在启用的水位测站的信息
     *
     * @return java.util.List<com.essence.tzsyq.rainfall.entity.TStbprpB>
     * @Author huangxiaoli
     * @Description
     * @Date 10:26 2020/9/3
     * @Param [addvcd, sttp]
     **/
    @Query(value = "SELECT T.* FROM ST_STBPRP_B T LEFT JOIN ST_STSMTASK_B M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.ZFL=1 AND T.RVNM=?1 AND T.STTP=?2 ORDER BY  T.RVNM ASC,T.STCD ASC", nativeQuery = true)
    public List<StStbprpB> findUseAddvcdWaterLevelStbprpB(String rvnm, String sttp);


    @Query(value = "SELECT T.* FROM ST_STBPRP_B T LEFT JOIN ST_STSMTASK_B M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.ZFL=1 AND T.STTP=?1 ORDER BY  T.RVNM ASC,T.STCD ASC", nativeQuery = true)
    public List<StStbprpB> findUseWaterLevelStbprpB(String sttp);


    @Modifying
    @Query(value = "delete from StStbprpB t where t.stcd=?1")
    public void deleteByStcd(String stcd);

    @Query(value = "SELECT T.* FROM ST_STBPRP_B_RELATE B LEFT JOIN ST_STBPRP_B_OLD T ON B.STCD=T.STCD WHERE T.STTP=?1 AND T.STNM LIKE ?2 AND T.USFL=?3 ORDER BY T.STNM ASC", nativeQuery = true)
    public List<StStbprpB> findSelfBuiltBySttpAndStnmAndUsfl(String sttp, String stnm, String usfl);


    @Query(value = "SELECT T.* FROM ST_STBPRP_B_RELATE B LEFT JOIN ST_STBPRP_B_OLD T ON B.STCD=T.STCD WHERE T.STTP=?1 AND T.STCD IN(?2) AND T.STNM LIKE ?3 AND T.USFL=?4 ORDER BY T.STNM ASC", nativeQuery = true)
    public List<StStbprpB> findSelfBuiltBySttpAndStcdInAndStnmAndUsfl(String sttp, List<String> stcdList, String stnm, String usfl);


    @Query(value = "SELECT STTP ,COUNT(1) count from ST_STBPRP_B WHERE USFL = 1 GROUP BY STTP ORDER BY STTP", nativeQuery = true)
    List<Map<String, Object>> findDataGroupBySttp();


    @Query(value = "SELECT B.STCD,B.STNM,r.DRP FROM ST_STBPRP_B b INNER JOIN ( SELECT * FROM ST_STSMTASK_B WHERE PFL = 1 ) m " +
            "ON b.STCD = m.STCD LEFT JOIN ( SELECT STCD, DRP FROM ST_PPTN_R WHERE TM >= ?1 AND TM <=?2 ) r " +
            "ON r.STCD = B.STCD ORDER BY DRP DESC", nativeQuery = true)
    List<Map<String, Object>> getRainSituation(Date startTime, Date endTime);


    @Query(value = "SELECT ST.STCD,ST.STNM,ST.RVNM,D.TOTAL,ST.LGTD,ST.LTTD FROM ST_STBPRP_B ST INNER JOIN " +
            "(SELECT a.STCD, SUM( a.DRP ) total FROM (SELECT B.STCD, R.DRP FROM ST_STBPRP_B B INNER JOIN ( " +
            "SELECT STCD FROM ST_STSMTASK_B WHERE PFL = 1 ) M ON B.STCD = M.STCD LEFT JOIN ( " +
            "SELECT STCD, DRP FROM ST_PPTN_R WHERE TM >= ?1 AND TM <= ?2 ) R ON R.STCD = B.STCD WHERE B.STCD LIKE %?3% OR B.STNM LIKE %?3% ) a " +
            "GROUP BY a.STCD ) D ON ST.STCD = D.STCD ORDER BY D.TOTAL DESC", nativeQuery = true)
    List<Map<String, Object>> getRainDistributionList(Date startTime, Date endTime,String name);


    /**
     * 实时监测-水情监测-闸坝
     *
     * @return
     */
    @Query(value = "SELECT T.* FROM (SELECT B.STCD,B.STNM,B.RVNM,B.LGTD,B.LTTD,R.WRZ,R.GRZ,R.OBHTZ,c.UPZ,c.DWZ,c.TGTQ FROM " +
            "ST_STBPRP_B B LEFT JOIN ST_RVFCCH_B R ON B.STCD = R.STCD INNER JOIN ( SELECT a.* from (" +
            "SELECT t.*,row_number() over(partition by STCD order by tm DESC) row_number from ST_WAS_R t )a " +
            "where a.ROW_NUMBER<2 ) c ON B.stcd =c.stcd WHERE B.STTP = 'DD' AND B.USFL = '1' ) T ORDER BY T.UPZ DESC", nativeQuery = true)
    List<Map<String, Object>> getSluiceList();

    /**
     * 实时监测-水情监测-潮位
     *
     * @return
     */
    @Query(value = "SELECT T.* FROM (SELECT B.STCD,B.STNM,B.LGTD,B.LTTD,R.WRZ,R.GRZ,R.OBHTZ,c.TDZ,c.AIRP FROM " +
            "ST_STBPRP_B B LEFT JOIN ST_RVFCCH_B R ON B.STCD = R.STCD INNER JOIN (SELECT a.* FROM (" +
            "SELECT r.*, row_number() over(partition by STCD ORDER BY TM DESC) row_number FROM ST_TIDE_R r )a " +
            "WHERE a.ROW_NUMBER<2) c ON B.stcd =c.stcd WHERE B.STTP = 'TT' AND B.USFL = '1' ) T ORDER BY T.TDZ DESC", nativeQuery = true)
    List<Map<String, Object>> getTideList();

    /**
     * 洪水告警-闸坝、潮汐基本信息
     * @param sttp
     * @return
     */
    @Query(value="SELECT b.STCD,b.STNM,b.LGTD,b.LTTD,b.RVNM,B.HNNM,r.WRZ,r.GRZ,r.OBHTZ FROM ST_STBPRP_B b " +
            "LEFT JOIN ST_RVFCCH_B r ON b.STCD = r.STCD WHERE b.STTP = ?1 AND b.USFL = '1'",nativeQuery=true)
    List<Map<String,Object>> getFloodWarningInfo(String sttp);

}
