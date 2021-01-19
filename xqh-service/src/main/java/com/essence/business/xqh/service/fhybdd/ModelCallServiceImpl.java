package com.essence.business.xqh.service.fhybdd;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.service.ModelCallService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlanOutputQDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninRainfallDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.euauth.common.util.UuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelCallServiceImpl implements ModelCallService {

    @Autowired
    StPptnRDao stPptnRDao;
    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao; //方案基本信息
    @Autowired
    YwkPlaninRainfallDao ywkPlaninRainfallDao;//输入
    @Autowired
    YwkPlanOutputQDao ywkPlanOutputQDao;//输出

    @Transactional
    @Override
    public void callMode(ModelCallBySWDDVo vo) {
        Date startTime = vo.getStartTime(); //开始时间
        Date endTIme = vo.getEndTIme();  //结束时间
        Date periodEndTime = vo.getPeriodEndTime();//预见结束时间
        if(periodEndTime != null){
            endTIme = periodEndTime;
        }
        int step = vo.getStep();//以小时为单位

        //方案基本信息入库
        YwkPlaninfo ywkPlaninfo = new YwkPlaninfo();
        ywkPlaninfo.setnPlanid(UuidUtil.get32UUIDStr());
        ywkPlaninfo.setcPlanname("模型方案名");
        ywkPlaninfo.setnCreateuser("user");
        ywkPlaninfo.setnPlancurrenttime(new Date());
        ywkPlaninfo.setdCaculatestarttm(startTime);//方案计算开始时间
        ywkPlaninfo.setdCaculateendtm(endTIme);//方案计算结束时间
        ywkPlaninfo.setnPlanstatus(0l);//方案状态
        ywkPlaninfo.setnOutputtm(step*60L);//设置间隔分钟
        //ywkPlaninfo.se
        //ywkPlaninfoDao.save()

        //tm,sum
        List<Map<String, Object>> stPptnRByStartTimeAndEndTime = stPptnRDao.findStPptnRByStartTimeAndEndTime(startTime, endTIme);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh");
        Map<String,BigDecimal> stPptnRMap = new HashMap<>();
        for (Map<String,Object> map : stPptnRByStartTimeAndEndTime){
            String tm = map.get("tm")+"";
            BigDecimal sum = new BigDecimal(map.get("sum")+"");
            stPptnRMap.put(tm,sum);
        }
        while (startTime.before(endTIme)){
            String hourStart = format.format(startTime);
            BigDecimal bigDecimal = stPptnRMap.get(hourStart);
            startTime = DateUtil.getNextHour(startTime,step);
        }

    }
}
