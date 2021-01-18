package com.essence.business.xqh.dao.dao.tuoying;

import com.essence.business.xqh.common.util.SyncDBUtil;
import com.essence.business.xqh.dao.dao.floodScheduling.dto.SchedulingHourAvgRainDto;
import com.essence.business.xqh.dao.dao.floodScheduling.dto.SchedulingHourRainDto;
import com.essence.business.xqh.dao.entity.tuoying.TuoyingStPptnR;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 实时降雨表数据访问层接口实现
 * LiuGt add at 2020-06-28
 */
@Repository
public class TuoyingStPptnRDaoImpl implements TuoyingStPptnRDao {

    private QueryRunner runner = null; // 查询运行器

    public TuoyingStPptnRDaoImpl() {
        runner = new QueryRunner();
    }

    @Override
    public List<TuoyingStPptnR> findByStcdInAndTmBetween(Set<String> stcdList, Date startTime, Date endTime) {
        StringBuffer stcds = new StringBuffer("(");
        for (String stcd : stcdList) {
            stcds.append("'"+stcd+"',");
        }
        stcds.replace(stcds.lastIndexOf(","), stcds.length(), ")");
        System.out.println(stcds.toString());
        String sql = "SELECT * FROM st_pptn_r  WHERE stcd  IN  "+stcds.toString()+" AND (tm BETWEEN ? AND ? )  ORDER BY tm DESC";
//        String sql = "SELECT * FROM st_pptn_r  WHERE stcd  IN  ? AND (tm BETWEEN ? AND ? )  ORDER BY tm DESC";

        java.sql.Timestamp sqlBeginTime = new java.sql.Timestamp(startTime.getTime());
        java.sql.Timestamp sqlEndTime = new java.sql.Timestamp(endTime.getTime());
        List<TuoyingStPptnR> records = null;
        try {
            records = runner.query(SyncDBUtil.getConnection(), sql,
                    new BeanListHandler<>(TuoyingStPptnR.class),sqlBeginTime, sqlEndTime);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }
    /**
     * 查询指定测站两个时间之间的时段降雨数据（时间分组求和）
     * LiuGt add at 2020-06-29
     *
     * @return List<StPptnR>
     * @throws SQLException
     */
    @Override
    public List<SchedulingHourRainDto> queryHourRainfallByStcdsAndTowDate(LocalDateTime beginTime, LocalDateTime endTime, List<String> stcds){
        List<SchedulingHourRainDto> records = null;
        String inWhere = "";
        if (stcds != null && stcds.size() > 0){
            for (String stcd : stcds) {
                inWhere += "," + "'" + stcd + "'";
            }
        }
        if (inWhere.length() > 1){
            inWhere = inWhere.substring(1);
        }
        String sql="select TM time,sum(DRP) rainfall from st_pptn_r where stcd in ("+inWhere+") and TM BETWEEN ? and ? group by TM order by TM";
        Timestamp sqlBeginTime1 = Timestamp.valueOf(beginTime);
        Timestamp sqlEndTime1 = Timestamp.valueOf(endTime);
        try{
            records = runner.query(SyncDBUtil.getConnection(), sql,
                    new BeanListHandler<>(SchedulingHourRainDto.class),sqlBeginTime1, sqlEndTime1);
            String ss = "";
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            /*if (conn!=null){
                conn.close();
            }*/
        }
        return records;
    }

    /**
     * 查询指定测站两个时间之间的时段总降雨和平均降雨数据
     * LiuGt add at 2020-07-14
     *
     * @return List<SchedulingHourAvgRainDto>
     * @throws SQLException
     */
    @Override
    public List<SchedulingHourAvgRainDto> queryHourAvgRainfallByStcdsAndTowDate(LocalDateTime beginTime, LocalDateTime endTime, List<String> stcds){
        List<SchedulingHourAvgRainDto> records = null;
        String inWhere = "";
        if (stcds != null && stcds.size() > 0){
            for (String stcd : stcds) {
                inWhere += "," + "'" + stcd + "'";
            }
        }
        if (inWhere.length() > 1){
            inWhere = inWhere.substring(1);
        }
        String sql="select TM time,sum(DRP) rainfall,count(0) stcdCount,ifnull(sum(DRP)/count(0),0) avgRainfall from st_pptn_r where stcd in ("+inWhere+") and TM BETWEEN ? and ? group by TM order by TM";
        Timestamp sqlBeginTime1 = Timestamp.valueOf(beginTime);
        Timestamp sqlEndTime1 = Timestamp.valueOf(endTime);
        try{
            records = runner.query(SyncDBUtil.getConnection(), sql,
                    new BeanListHandler<>(SchedulingHourAvgRainDto.class),sqlBeginTime1, sqlEndTime1);
            String ss = "";
        }
        catch (Exception e){
            e.printStackTrace();
        }
        finally {
            /*if (conn!=null){
                conn.close();
            }*/
        }
        return records;
    }
}
