package com.essence.handler.Schedule;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybddNewService;
import com.essence.business.xqh.api.fhybdd.service.ModelPlanInfoManageService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninRainfallDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
@Configurable
@EnableScheduling
@EnableAsync
public class SwybSchedule {

    public static final String rvcd = "RVR_011";
    public static final String catchmentAreaModelId = "MODEL_SWYB_CATCHMENT_SCS";
    public static final String reachId = "MODEL_SWYB_REACH_MSJG";
    public static final int step = 60;
    public static final int timeType = 0;

    @Autowired
    ModelCallFhybddNewService modelCallFhybddNewService;

    @Autowired
    ModelPlanInfoManageService modelPlanInfoManageService;

    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao;

    @Autowired
    YwkPlaninRainfallDao ywkPlaninRainfallDao;

    @Scheduled(cron = " 0 0 4 * * ?") //每天凌晨四点开始自动进行预报计算
//    @Scheduled(cron = "*/20 * * * * ?")
    @Async
    public void generateSwyb() throws Exception {
        ModelCallBySWDDVo vo = new ModelCallBySWDDVo();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String cPlanname = format.format(date) + "自动预报方案";
        String planSystem = PropertiesUtil.read("/filePath.properties").getProperty("XT_SWYB");
        List<YwkPlaninfo> isAll = ywkPlaninfoDao.findByCPlannameAndPlanSystem(cPlanname, planSystem);
        if (!CollectionUtils.isEmpty(isAll)){
            YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(isAll.get(0).getnPlanid());
            modelPlanInfoManageService.deleteByPlanId(planInfo);
        }
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd HH");
        Date startTime = format1.parse(format1.format(DateUtil.getNextHour(date, -20))); //开始时间为昨日八点
        Date endTime = format1.parse(format1.format(DateUtil.getNextHour(date, 4))); //结束时间为今日八点
        vo.setcPlanname(cPlanname);
        vo.setStartTime(startTime);
        vo.setEndTime(endTime);
        vo.setStep(step);
        vo.setTimeType(timeType);
        vo.setRvcd(rvcd);
        vo.setCatchmentAreaModelId(catchmentAreaModelId);
        vo.setReachId(reachId);
        try{
            String nPlanid = modelCallFhybddNewService.savePlan(vo);
            YwkPlaninfo planInfoByPlanId = modelCallFhybddNewService.getPlanInfoByPlanId(nPlanid);
            List<Map<String, Object>> rainfalls = modelCallFhybddNewService.getRainfalls(planInfoByPlanId);
            modelCallFhybddNewService.saveRainfallsFromCacheToDb(planInfoByPlanId, rainfalls);
            Thread.sleep(6000);
            int count = ywkPlaninRainfallDao.findCountTMBetween(startTime, endTime);
            if(count==0){
                return;
            }
            modelCallFhybddNewService.modelCallPCP(planInfoByPlanId);
            Thread.sleep(15000);
            modelCallFhybddNewService.modelCall(planInfoByPlanId);
        }catch (Exception e){
            e.printStackTrace();
        }

    }



}
