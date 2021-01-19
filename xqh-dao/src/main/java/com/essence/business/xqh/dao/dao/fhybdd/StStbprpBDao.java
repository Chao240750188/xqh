package com.essence.business.xqh.dao.dao.fhybdd;

import com.essence.business.xqh.dao.dao.rainfall.dto.FxEventTypeDetailInfoDto;
import com.essence.business.xqh.dao.entity.baseInfoManage.HbmAddvcdD;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.framework.jpa.EssenceJpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StStbprpBDao extends EssenceJpaRepository<StStbprpB,String > {
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
         * @Author huangxiaoli
         * @Description
         * @Date 11:05 2020/8/20
         * @Param [sttp]
         * @return java.util.List<com.essence.tzsyq.fxWorkDisposal.dto.FxEventTypeDetailInfoDto>
         **/
        @Query(value = "select new com.essence.business.xqh.dao.dao.rainfall.dto.FxEventTypeDetailInfoDto(t.stcd,t.stnm,t.lgtd,t.lttd,t.stlc) from StStbprpB t where t.sttp=?1 and t.usfl='1'")
        public List<FxEventTypeDetailInfoDto> findEventTypeBySttp(String sttp);






        /**
         * 查询正在启用的雨量测站的信息
         * @Author huangxiaoli
         * @Description
         * @Date 16:11 2020/9/2
         * @Param [addvcd, sttp]
         * @return java.util.List<com.essence.tzsyq.rainfall.entity.TStbprpB>
         **/
        @Query(value = "SELECT T.* FROM ST_STBPRP_B T LEFT JOIN ST_STSMTASK_B M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.PFL=1 AND T.ADDVCD=?1 AND T.STTP=?2 ORDER BY  T.ADDVCD ASC,T.STCD ASC",nativeQuery = true)
        public List<StStbprpB> findUseAddvcdRainStbprpB(String addvcd, String sttp);


        @Query(value = "SELECT T.* FROM ST_STBPRP_B T LEFT JOIN ST_STSMTASK_B M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.PFL=1 AND T.STTP=?1 ORDER BY  T.ADDVCD ASC,T.STCD ASC",nativeQuery = true)
        public List<StStbprpB> findUseRainStbprpB(String sttp);


        /**
         * 查询正在启用的水位测站的信息
         * @Author huangxiaoli
         * @Description
         * @Date 10:26 2020/9/3
         * @Param [addvcd, sttp]
         * @return java.util.List<com.essence.tzsyq.rainfall.entity.TStbprpB>
         **/
        @Query(value = "SELECT T.* FROM ST_STBPRP_B T LEFT JOIN ST_STSMTASK_B M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.ZFL=1 AND T.RVNM=?1 AND T.STTP=?2 ORDER BY  T.RVNM ASC,T.STCD ASC",nativeQuery = true)
        public List<StStbprpB> findUseAddvcdWaterLevelStbprpB(String rvnm, String sttp);


        @Query(value = "SELECT T.* FROM ST_STBPRP_B T LEFT JOIN ST_STSMTASK_B M ON T.STCD=M.STCD WHERE T.USFL='1' AND M.ZFL=1 AND T.STTP=?1 ORDER BY  T.RVNM ASC,T.STCD ASC",nativeQuery = true)
        public List<StStbprpB> findUseWaterLevelStbprpB(String sttp);


        @Modifying
        @Query(value = "delete from StStbprpB t where t.stcd=?1")
        public void deleteByStcd(String stcd);

        @Query(value = "SELECT T.* FROM ST_STBPRP_B_RELATE B LEFT JOIN ST_STBPRP_B_OLD T ON B.STCD=T.STCD WHERE T.STTP=?1 AND T.STNM LIKE ?2 AND T.USFL=?3 ORDER BY T.STNM ASC",nativeQuery = true)
        public List<StStbprpB> findSelfBuiltBySttpAndStnmAndUsfl(String sttp, String stnm, String usfl);


        @Query(value = "SELECT T.* FROM ST_STBPRP_B_RELATE B LEFT JOIN ST_STBPRP_B_OLD T ON B.STCD=T.STCD WHERE T.STTP=?1 AND T.STCD IN(?2) AND T.STNM LIKE ?3 AND T.USFL=?4 ORDER BY T.STNM ASC",nativeQuery = true)
        public List<StStbprpB> findSelfBuiltBySttpAndStcdInAndStnmAndUsfl(String sttp, List<String> stcdList, String stnm, String usfl);

}
