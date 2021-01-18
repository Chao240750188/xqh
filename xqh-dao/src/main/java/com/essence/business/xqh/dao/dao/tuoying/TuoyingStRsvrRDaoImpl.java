package com.essence.business.xqh.dao.dao.tuoying;


import com.essence.business.xqh.common.util.SyncDBUtil;
import com.essence.business.xqh.dao.dao.tuoying.dto.HtStRsvrRViewDto;
import com.essence.business.xqh.dao.entity.tuoying.TuoyingStRsvrR;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 水库水情表数据访问层接口实现
 * LiuGt add at 2020-07-22
 */
@Repository
public class TuoyingStRsvrRDaoImpl implements TuoyingStRsvrRDao {

    private QueryRunner runner = null; // 查询运行器

    public TuoyingStRsvrRDaoImpl() {
        runner = new QueryRunner();
    }


    /**
     * 查询指定水库测站距离指定时间最近的一次实测数据（水位和库容）
     * @param tm
     * @param stcd
     * @return
     */
    @Override
    public List<HtStRsvrRViewDto> queryByLatelyTmAndStcd(Date tm, String stcd){
        String strTm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tm);
        //String sql = "select STCD,TM,abs((str_to_date('"+strTm+"','%Y-%m-%d %T')-TM)) as time,RZ,W from st_rsvr_r where stcd = ? order by time asc limit 1";
        String sql  = "select STCD stcd,DATE_FORMAT(TM,'%Y-%m-%d %T') tm,RZ rz,W w from st_rsvr_r where stcd = ? order by abs((str_to_date('"+strTm+"','%Y-%m-%d %T')-TM)) asc limit 1";
        //java.sql.Timestamp sqlBeginTime = new java.sql.Timestamp(startTime.getTime());
        //java.sql.Timestamp sqlEndTime = new java.sql.Timestamp(endTime.getTime());
        List<HtStRsvrRViewDto> records = null;
        try {
            records = runner.query(SyncDBUtil.getConnection(), sql,
                    new BeanListHandler<>(HtStRsvrRViewDto.class), stcd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * 根据测站ID查询最近一次实测数据
     * LiuGt add at 2020-07-22
     *
     * @return List<StRsvrR>
     * @throws SQLException
     */
    @Override
    public List<TuoyingStRsvrR> queryLastOneByStcd(String stcd) {
        List<TuoyingStRsvrR> records = null;
        String sql="select * from st_rsvr_r where STCD = ? order by TM desc limit 1";
        try{
            records = runner.query(SyncDBUtil.getConnection(), sql,
                    new BeanListHandler<>(TuoyingStRsvrR.class), stcd);
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
