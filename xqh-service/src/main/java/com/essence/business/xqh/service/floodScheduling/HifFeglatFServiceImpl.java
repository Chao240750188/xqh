package com.essence.business.xqh.service.floodScheduling;


import com.essence.business.xqh.api.floodScheduling.dto.ScheduingRainfallDto;
import com.essence.business.xqh.api.floodScheduling.dto.SchedulingResultStatisticsDto;
import com.essence.business.xqh.api.floodScheduling.dto.SkddHifFeglatFDto;
import com.essence.business.xqh.api.floodScheduling.service.HifFeglatFService;
import com.essence.business.xqh.api.tuoying.TuoyingInfoService;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddHifFeglatFDao;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddRelStResDao;
import com.essence.business.xqh.dao.dao.floodScheduling.SkddSchedulingPlanInfoDao;
import com.essence.business.xqh.dao.dao.floodScheduling.dto.SchedulingHourAvgRainDto;
import com.essence.business.xqh.dao.dao.tuoying.TuoyingStPptnRDao;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddHifFeglatF;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddRelStRes;
import com.essence.business.xqh.dao.entity.floodScheduling.SkddSchedulingPlanInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 调度预报成果表服务实现
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/04/01
 */
@Transactional
@Service
public class HifFeglatFServiceImpl implements HifFeglatFService {

    @Autowired
    SkddHifFeglatFDao hifFeglatFDao; //调度预报成果表数据访问
    @Autowired
    SkddSchedulingPlanInfoDao schedulingPlanInfoDao; //调度方案信息数据访问

    @Autowired
    SkddRelStResDao relStResDao;
    @Autowired
    private TuoyingStPptnRDao stPptnRDao;
    @Autowired
    private TuoyingInfoService tuoyingInfoService;

    /**
     * 根据方案ID查询调度预报成果数据
     * @param planId
     * @return
     */
    @Override
    public List<SkddHifFeglatFDto> queryListByPlanId(String planId){
        List<SkddHifFeglatFDto> skddHifFeglatFDtoList = new ArrayList<>();
        List<SkddHifFeglatF> feglatFList = hifFeglatFDao.queryListByPlanId(planId);
        if (feglatFList.size()>0){
            for (int i=0;i<feglatFList.size();i++){
                SkddHifFeglatF hifFeglatF = feglatFList.get(i);
                SkddHifFeglatFDto skddHifFeglatFDto = new SkddHifFeglatFDto();
                BeanUtils.copyProperties(hifFeglatF,skddHifFeglatFDto);
                skddHifFeglatFDtoList.add(skddHifFeglatFDto);
            }
        }
        return skddHifFeglatFDtoList;
    }

    /**
     * 根据方案ID统计一次调度结果的统计数据
     * @param planId
     * @return
     */
    @Override
    public SchedulingResultStatisticsDto querySchedulingResultStatistics(String planId){
        SchedulingResultStatisticsDto dto = new SchedulingResultStatisticsDto();
        SkddSchedulingPlanInfo schedulingPlanInfo = schedulingPlanInfoDao.queryByPlanId(planId);
        if (schedulingPlanInfo == null){
            return dto;
        }
        //起调水位
        dto.setWl(schedulingPlanInfo.getWl());
        //统计其他数据
        Map<String,Object> map = hifFeglatFDao.queryResultStatisticsByPlanId(planId);
        if (map!=null && map.size() > 0){
            dto.setMaxRz(new BigDecimal(map.get("maxRz").toString()));
            dto.setMaxInq(new BigDecimal(map.get("maxInq").toString()));
            dto.setMaxOtq(new BigDecimal(map.get("maxOtq").toString()));
            dto.setMaxRzW(new BigDecimal(map.get("maxRzW").toString()));
            dto.setTotalInq(new BigDecimal(map.get("totalInq").toString()));
            dto.setTotalOtq(new BigDecimal(map.get("totalOtq").toString()));
            String maxInqTime = map.get("maxInqTime") == null ? "-" : map.get("maxInqTime").toString();
            String maxOtqTime = map.get("maxOtqTime") == null ? "-" : map.get("maxOtqTime").toString();
            //dto.setMaxInqTime(LocalDateTime.parse(map.get("maxInqTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            //dto.setMaxOtqTime(LocalDateTime.parse(map.get("maxOtqTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            dto.setMaxInqTime(maxInqTime);
            dto.setMaxOtqTime(maxOtqTime);
        }
        else{
            dto.setMaxRz(new BigDecimal(0));
            dto.setMaxInq(new BigDecimal(0));
            dto.setMaxOtq(new BigDecimal(0));
            dto.setMaxRzW(new BigDecimal(0));
            dto.setTotalInq(new BigDecimal(0));
            dto.setTotalOtq(new BigDecimal(0));
            dto.setMaxInqTime("-");
            dto.setMaxOtqTime("-");
        }
        return dto;
    }

    /**
     * 根据方案ID统计一次调度过程的降雨数据
     * @param planId
     */
    @Override
    public List<ScheduingRainfallDto> getRainfallForOnecScheduling(String planId){
        //返回值
        List<ScheduingRainfallDto> rainfallDaoList = new ArrayList<>();
        //获取调度结果数据
        List<SkddHifFeglatFDto> skddHifFeglatFDtos = queryListByPlanId(planId);
        if (skddHifFeglatFDtos == null || skddHifFeglatFDtos.size() <= 0){
            return rainfallDaoList;
        }
        //获取方案基本信息
        SkddSchedulingPlanInfo schedulingPlanInfo = schedulingPlanInfoDao.queryByPlanId(planId);
        LocalDateTime startTime = schedulingPlanInfo.getStartTime();
        LocalDateTime endTime = schedulingPlanInfo.getEndTime();
        //查询水库对应的慧图测站ID
        List<SkddRelStRes> relStResList = relStResDao.queryListByResCodeAndSttp(schedulingPlanInfo.getResCode(), "PP");
        if (relStResList == null || relStResList.size() <= 0){
            return rainfallDaoList;
        }
        //查询降雨数据
        List<String> stcdList = new ArrayList<>();
        for (SkddRelStRes relStRes : relStResList) {
            String stcd =  relStRes.getStcd();
            stcdList.add(stcd);
        }
        List<SchedulingHourAvgRainDto> schedulingHourAvgRainDtos = new ArrayList<>();
        try {
            schedulingHourAvgRainDtos = stPptnRDao.queryHourAvgRainfallByStcdsAndTowDate(startTime, endTime, stcdList);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //处理数据
        for (SkddHifFeglatFDto skddHifFeglatFDto : skddHifFeglatFDtos) {

            ScheduingRainfallDto rainfallDao = new ScheduingRainfallDto();
            LocalDateTime tm = skddHifFeglatFDto.getYmdh();
            rainfallDao.setTm(tm);
            String ymdh = tm.format(DateTimeFormatter.ofPattern("yyyyMMddHH"));
            //Date currentDate = Date.from(ymdh.atZone(ZoneId.systemDefault()).toInstant());
            SimpleDateFormat sdf =   new SimpleDateFormat("yyyyMMddHH");
            double drp = 0;
            List<SchedulingHourAvgRainDto> subRains = new ArrayList<>();
            if (schedulingHourAvgRainDtos != null && schedulingHourAvgRainDtos.size() > 0) {
                subRains = schedulingHourAvgRainDtos.stream().filter(avgRain -> sdf.format(avgRain.getTime()).equals(ymdh))
                        .collect(Collectors.toList());
            }
            if (subRains != null && subRains.size() > 0){
                drp = subRains.get(0).getAvgRainfall().setScale(1,BigDecimal.ROUND_HALF_UP).doubleValue();
            }
            rainfallDao.setDrp(drp);
            rainfallDaoList.add(rainfallDao);
        }
        //返回
        return rainfallDaoList;
    }

    /**
     * 根据方案ID查询该方案最后一次调度预报数据
     * @param planId
     * @return
     */
    @Override
    public SkddHifFeglatFDto queryLastOneByPlanId(String planId){
        SkddHifFeglatFDto skddHifFeglatFDto = null;
        SkddHifFeglatF hifFeglatF = hifFeglatFDao.queryLastOneByPlanId(planId);
        if (null!=hifFeglatF){
            skddHifFeglatFDto = new SkddHifFeglatFDto();
            BeanUtils.copyProperties(hifFeglatF,skddHifFeglatFDto);
        }
        return skddHifFeglatFDto;
    }

    /**
     * 添加一个调度预报数据
     * @param hifFeglatFDto
     * @return
     */
    @Override
    public SkddHifFeglatFDto add(SkddHifFeglatFDto hifFeglatFDto){
        SkddHifFeglatF hifFeglatF = new SkddHifFeglatF();
        BeanUtils.copyProperties(hifFeglatFDto,hifFeglatF);
        SkddHifFeglatF save = hifFeglatFDao.save(hifFeglatF);
        BeanUtils.copyProperties(save,hifFeglatFDto);
        return hifFeglatFDto;
    }
}
