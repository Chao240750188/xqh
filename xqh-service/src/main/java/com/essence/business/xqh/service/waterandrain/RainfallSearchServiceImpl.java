package com.essence.business.xqh.service.waterandrain;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.waterandrain.dto.*;
import com.essence.business.xqh.api.waterandrain.service.RainfallSearchService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StPdmmysqSDao;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.entity.fhybdd.StPdmmysqS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengpp
 * 2021/2/4 18:17
 */
@Service
public class RainfallSearchServiceImpl implements RainfallSearchService {

    @Autowired
    StStbprpBDao stStbprpBDao;
    @Autowired
    StPptnRDao stPptnRDao;
    @Autowired
    StPdmmysqSDao stPdmmysqSDao;

    /**
     * 降雨量表
     *
     * @param paramDto
     * @return
     */
    @Override
    public DayRainfallDto getDayRainfall(QueryParamDto paramDto) {
        List<Map<String, Object>> rainList = stStbprpBDao.findUseRainStbprpb();
        List<String> stcdList = new ArrayList<>();
        for (Map<String, Object> map : rainList) {
            stcdList.add(map.get("STCD").toString());
        }

        List<Map<String, Object>> stPptnRList = stPptnRDao.findByStcdInAndTmBetween(stcdList, paramDto.getStartTime(), paramDto.getEndTime());
        Map<String, BigDecimal> map = new HashMap<>();
        for (Map<String, Object> tempMap : stPptnRList) {
            if (tempMap.get("TOTAL") != null) {
                String stcd = tempMap.get("STCD").toString();
                BigDecimal total = new BigDecimal(tempMap.get("TOTAL").toString());
                map.put(stcd, total);
            }
        }

        List<RainfallDto> zero = new ArrayList<>();//无降雨
        List<RainfallDto> betweenZeroAndTen = new ArrayList<>();//0-10
        List<RainfallDto> betweenTenAndTwentyFive = new ArrayList<>();//10-25
        List<RainfallDto> betweenTwentyFiveAndFifty = new ArrayList<>();//25-50
        List<RainfallDto> betweenFiftyAndOneHundred = new ArrayList<>();//50-100
        List<RainfallDto> betweenOneHundredAndTwoHundredAndFifty = new ArrayList<>();//100-250
        List<RainfallDto> beyondTwoHundredAndFifty = new ArrayList<>();//大于250
        for (Map<String, Object> tempMap : rainList) {
            String stcd = tempMap.get("STCD").toString();
            String stnm = tempMap.get("STNM").toString();
            BigDecimal lgtd = new BigDecimal(tempMap.get("LGTD") == null ? "0" : tempMap.get("LGTD").toString());
            BigDecimal lttd = new BigDecimal(tempMap.get("LTTD") == null ? "0" : tempMap.get("LTTD").toString());
            RainfallDto dto = new RainfallDto(stcd, stnm, lgtd, lttd);

            BigDecimal total = new BigDecimal(0);
            if (map.containsKey(stcd)) {
                total = map.get(stcd);
            }
            dto.setRainfall(total);
            if (total.compareTo(new BigDecimal(0)) == 0) {
                zero.add(dto);
            } else if (total.compareTo(new BigDecimal(10)) <= 0) {
                betweenZeroAndTen.add(dto);
            } else if (total.compareTo(new BigDecimal(25)) <= 0) {
                betweenTenAndTwentyFive.add(dto);
            } else if (total.compareTo(new BigDecimal(50)) <= 0) {
                betweenTwentyFiveAndFifty.add(dto);
            } else if (total.compareTo(new BigDecimal(100)) <= 0) {
                betweenFiftyAndOneHundred.add(dto);
            } else if (total.compareTo(new BigDecimal(250)) <= 0) {
                betweenOneHundredAndTwoHundredAndFifty.add(dto);
            } else if (total.compareTo(new BigDecimal(250)) == 1) {
                beyondTwoHundredAndFifty.add(dto);
            }
        }
        DayRainfallDto rainfallDto = new DayRainfallDto();
        rainfallDto.setZero(zero);
        rainfallDto.setBetweenZeroAndTen(betweenZeroAndTen);
        rainfallDto.setBetweenTenAndTwentyFive(betweenTenAndTwentyFive);
        rainfallDto.setBetweenTwentyFiveAndFifty(betweenTwentyFiveAndFifty);
        rainfallDto.setBetweenFiftyAndOneHundred(betweenFiftyAndOneHundred);
        rainfallDto.setBetweenOneHundredAndTwoHundredAndFifty(betweenOneHundredAndTwoHundredAndFifty);
        rainfallDto.setBeyondTwoHundredAndFifty(beyondTwoHundredAndFifty);
        return rainfallDto;
    }

    /**
     * 旬雨量表
     *
     * @param year
     * @param mth
     * @param prdtp
     * @return
     */
    @Override
    public MonthRainfallDto getMonthRainfall(Integer year, Integer mth, Integer prdtp) {
        List<Map<String, Object>> rainList = stStbprpBDao.findUseRainStbprpb();
        List<String> stcdList = new ArrayList<>();
        for (Map<String, Object> map : rainList) {
            stcdList.add(map.get("STCD").toString());
        }
        List<StPdmmysqS> stPdmmysqSList = stPdmmysqSDao.findByStcdInAndYrAndMthAndPrdtp(stcdList, year, mth, prdtp);
        Map<String, StPdmmysqS> map = stPdmmysqSList.stream().collect(Collectors.toMap(StPdmmysqS::getStcd, stPdmmysqS -> stPdmmysqS, (oldValue, newValue) -> oldValue));

        List<RainfallDto> lessOne = new ArrayList<>();//小于1
        List<RainfallDto> betweenOneAndTen = new ArrayList<>();//1-10
        List<RainfallDto> betweenTenAndTwentyFive = new ArrayList<>();//10-25
        List<RainfallDto> betweenTwentyFiveAndFifty = new ArrayList<>();//25-50
        List<RainfallDto> betweenFiftyAndOneHundred = new ArrayList<>();//50-100
        List<RainfallDto> betweenOneHundredAndTwoHundred = new ArrayList<>();//100-200
        List<RainfallDto> betweenTwoAndFourHundred = new ArrayList<>();//200-400
        List<RainfallDto> betweenFourAndEightHundred = new ArrayList<>();//400-800
        List<RainfallDto> beyondEightHundred = new ArrayList<>();//大于800

        for (Map<String, Object> tempMap : rainList) {
            String stcd = tempMap.get("STCD").toString();
            String stnm = tempMap.get("STNM").toString();
            BigDecimal lgtd = new BigDecimal(tempMap.get("LGTD") == null ? "0" : tempMap.get("LGTD").toString());
            BigDecimal lttd = new BigDecimal(tempMap.get("LTTD") == null ? "0" : tempMap.get("LTTD").toString());
            RainfallDto dto = new RainfallDto(stcd, stnm, lgtd, lttd);

            BigDecimal total = new BigDecimal(0);
            if (map.containsKey(stcd)) {
                StPdmmysqS values = map.get(stcd);
                if (values.getAccp() != null) {
                    total = total.add(values.getAccp());
                }
            }
            dto.setRainfall(total);
            if (total.compareTo(new BigDecimal(1)) <= 0) {
                lessOne.add(dto);
            } else if (total.compareTo(new BigDecimal(10)) <= 0) {
                betweenOneAndTen.add(dto);
            } else if (total.compareTo(new BigDecimal(25)) <= 0) {
                betweenTenAndTwentyFive.add(dto);
            } else if (total.compareTo(new BigDecimal(50)) <= 0) {
                betweenTwentyFiveAndFifty.add(dto);
            } else if (total.compareTo(new BigDecimal(100)) <= 0) {
                betweenFiftyAndOneHundred.add(dto);
            } else if (total.compareTo(new BigDecimal(200)) <= 0) {
                betweenOneHundredAndTwoHundred.add(dto);
            } else if (total.compareTo(new BigDecimal(400)) <= 0) {
                betweenTwoAndFourHundred.add(dto);
            } else if (total.compareTo(new BigDecimal(800)) <= 0) {
                betweenFourAndEightHundred.add(dto);
            } else if (total.compareTo(new BigDecimal(800)) <= 0) {
                beyondEightHundred.add(dto);
            }
        }
        MonthRainfallDto rainfallDto = new MonthRainfallDto();
        rainfallDto.setLessOne(lessOne);
        rainfallDto.setBetweenOneAndTen(betweenOneAndTen);
        rainfallDto.setBetweenTenAndTwentyFive(betweenTenAndTwentyFive);
        rainfallDto.setBetweenTwentyFiveAndFifty(betweenTwentyFiveAndFifty);
        rainfallDto.setBetweenFiftyAndOneHundred(betweenFiftyAndOneHundred);
        rainfallDto.setBetweenOneHundredAndTwoHundred(betweenOneHundredAndTwoHundred);
        rainfallDto.setBetweenTwoAndFourHundred(betweenTwoAndFourHundred);
        rainfallDto.setBetweenFourAndEightHundred(betweenFourAndEightHundred);
        rainfallDto.setBeyondEightHundred(beyondEightHundred);
        return rainfallDto;
    }

    /**
     * 日雨量-单个站点雨量过程线
     *
     * @param paramDto
     * @return
     */
    @Override
    public Map<String, Object> getDayRainfallTendency(QueryParamDto paramDto) {
        Date startTime = paramDto.getStartTime();
        Date endTime = paramDto.getEndTime();
        List<Map<String, Object>> stPptnRList = stPptnRDao.findDataByStcdAndTmBetween(paramDto.getStcd(), startTime, endTime);

        Map<Date, BigDecimal> map = new HashMap<>();
        for (Map<String, Object> tempMap : stPptnRList) {
            if (tempMap.get("DRP") != null) {
                Date time;
                Date tm = (Date) tempMap.get("TM");
                Date day = DateUtil.getNextHour(DateUtil.getThisDay(tm), 8);
                if (tm.before(day)) {
                    time = DateUtil.getNextDay(day, -1);
                } else {
                    time = day;
                }
                BigDecimal bigDecimal = map.get(time) == null ? new BigDecimal(0) : map.get(time);
                bigDecimal = bigDecimal.add(new BigDecimal(tempMap.get("DRP").toString()));
                map.put(time, bigDecimal);
            }
        }

        TreeSet<BigDecimal> set = new TreeSet<>();
        List<RainfallTendencyDto> list = new ArrayList<>();
        while (startTime.getTime() <= endTime.getTime()) {
            RainfallTendencyDto dto = new RainfallTendencyDto();
            dto.setTm(startTime);
            dto.setShowTm(DateUtil.dateToStringDay2(startTime));
            BigDecimal bigDecimal = map.get(startTime) == null ? new BigDecimal(0) : map.get(startTime);
            dto.setRainfall(bigDecimal);
            set.add(bigDecimal);
            list.add(dto);
            startTime = DateUtil.getNextDay(startTime, 1);
        }

        Map<String, Object> resultMap = new HashMap<>();
        BigDecimal minValue = new BigDecimal(0);
        BigDecimal maxValue = new BigDecimal(0);
        if (set.size() > 0) {
            minValue = set.first();
            maxValue = set.last();
        }
        resultMap.put("minValue", Math.floor(minValue.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()));//最小值
        resultMap.put("maxValue", Math.ceil(maxValue.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()));//最大值
        resultMap.put("list", list);
        return resultMap;
    }

    /**
     * 时段雨量-单个站点雨量过程线
     *
     * @param paramDto
     * @return
     */
    @Override
    public Map<String, Object> getTimeRainfallTendency(QueryParamDto paramDto) {
        Date startTime = paramDto.getStartTime();
        Date endTime = paramDto.getEndTime();
        List<Map<String,Object>> stPptnRList = stPptnRDao.findDataByStcdAndTmBetween(paramDto.getStcd(), startTime, endTime);
        Integer step = paramDto.getStep();

        Map<Date, BigDecimal> map = new HashMap<>();
        for (Map<String,Object> tempMap : stPptnRList) {
            if (tempMap.get("DRP") != null) {
                BigDecimal bigDecimal = new BigDecimal(tempMap.get("DRP").toString());
                Date tm = (Date) tempMap.get("TM");
                Date nextHour = DateUtil.getNextHour(DateUtil.getThisHour(tm), 1);
                BigDecimal total = map.get(nextHour) == null ? new BigDecimal(0) : map.get(nextHour);
                total = total.add(bigDecimal);
                map.put(nextHour, total);
            }
        }

        List<RainfallTimeTendencyDto> resultList = new ArrayList<>();
        TreeSet<BigDecimal> set = new TreeSet<>();
        while (startTime.getTime() <= endTime.getTime()) {
            RainfallTimeTendencyDto dto = new RainfallTimeTendencyDto();
            dto.setTm(startTime);
            dto.setShowTm(DateUtil.dateToStringNormal3(startTime));
            BigDecimal total = map.get(startTime) == null ? new BigDecimal(0) : map.get(startTime);
            int i = 1;
            Date time = startTime;
            while (i < step) {
                time = DateUtil.getNextHour(time, 1);
                if (map.containsKey(time)) {
                    BigDecimal bigDecimal = map.get(time);
                    total = total.add(bigDecimal);
                }
                i++;
            }
            set.add(total);
            dto.setRainfall(total);
            dto.setStep(step);
            resultList.add(dto);
            startTime = DateUtil.getNextHour(startTime, step);
        }

        Map<String, Object> resultMap = new HashMap<>();
        BigDecimal minValue = new BigDecimal(0);
        BigDecimal maxValue = new BigDecimal(0);
        if (set.size() > 0) {
            minValue = set.first();
            maxValue = set.last();
        }
        resultMap.put("minValue", Math.floor(minValue.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()));//最小值
        resultMap.put("maxValue", Math.ceil(maxValue.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()));//最大值
        resultMap.put("list", resultList);
        return resultMap;
    }

    /**
     * 旬雨量-单个站点雨量过程线
     *
     * @param paramDto
     * @return
     */
    @Override
    public Map<String, Object> getTenDaysRainfallTendency(QueryParamDto paramDto) {
        Date startTime = paramDto.getStartTime();
        Date endTime = paramDto.getEndTime();
        List<Integer> prdtpList = new ArrayList<>();
        prdtpList.add(1);
        prdtpList.add(2);
        prdtpList.add(3);
        List<Map<String, Object>> list = stPdmmysqSDao.findDataByMonth(paramDto.getStcd(), startTime, endTime, prdtpList);

        TreeSet<BigDecimal> set = new TreeSet<>();
        TreeMap<Date, Map<Integer, BigDecimal>> map = new TreeMap<>();
        for (Map<String, Object> tempMap : list) {
            if (tempMap.get("ACCP") != null && tempMap.get("PRDTP") != null) {
                Date time = (Date) tempMap.get("TIME");
                Map<Integer, BigDecimal> integerMap = map.get(time) == null ? new HashMap<>() : map.get(time);
                Integer prdtp = Integer.valueOf(tempMap.get("PRDTP").toString());
                BigDecimal accp = new BigDecimal(tempMap.get("ACCP").toString());
                integerMap.put(prdtp, accp);
                set.add(accp);
                map.put(time, integerMap);
            }
        }

        List<RainfallTendencyDto> resultList = new ArrayList<>();
        while (startTime.getTime() <= endTime.getTime()) {
            String date = DateUtil.dateToStringMonth2(startTime);
            for (Integer integer : prdtpList) {
                RainfallTendencyDto dto = new RainfallTendencyDto();
                Map<Integer, BigDecimal> integerMap = map.get(startTime) == null ? new HashMap<>() : map.get(startTime);
                BigDecimal accp = integerMap.get(integer) == null ? new BigDecimal(0) : integerMap.get(integer);
                dto.setRainfall(accp);
                if (integer == 1) {
                    dto.setTm(startTime);
                    dto.setShowTm(date + "上旬");
                } else if (integer == 2) {
                    dto.setTm(DateUtil.getNextDay(startTime, 10));
                    dto.setShowTm(date + "中旬");
                } else {
                    dto.setTm(DateUtil.getNextDay(startTime, 20));
                    dto.setShowTm(date + "下旬");
                }
                resultList.add(dto);
            }
            startTime = DateUtil.getNextMonth(startTime, 1);
        }
        Map<String, Object> resultMap = new HashMap<>();
        BigDecimal minValue = new BigDecimal(0);
        BigDecimal maxValue = new BigDecimal(0);
        if (set.size() > 0) {
            minValue = set.first();
            maxValue = set.last();
        }
        resultMap.put("minValue", Math.floor(minValue.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()));//最小值
        resultMap.put("maxValue", Math.ceil(maxValue.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()));//最大值
        resultMap.put("list", list);
        return resultMap;
    }

    /**
     * 月雨量-单个站点雨量过程线
     *
     * @param paramDto
     * @return
     */
    @Override
    public Map<String, Object> getMonthRainfallTendency(QueryParamDto paramDto) {
        Date startTime = paramDto.getStartTime();
        Date endTime = paramDto.getEndTime();
        List<Integer> prdtpList = new ArrayList<>();
        prdtpList.add(4);

        List<Map<String, Object>> list = stPdmmysqSDao.findDataByMonth(paramDto.getStcd(), startTime, endTime, prdtpList);

        Map<Date, BigDecimal> map = new HashMap<>();
        TreeSet<BigDecimal> set = new TreeSet<>();
        for (Map<String, Object> tempMap : list) {
            if (tempMap.get("ACCP") != null) {
                Date time = (Date) tempMap.get("TIME");
                BigDecimal accp = new BigDecimal(tempMap.get("ACCP").toString());
                map.put(time, accp);
                set.add(accp);
            }
        }
        List<RainfallTendencyDto> resultList = new ArrayList<>();
        while (startTime.getTime() <= endTime.getTime()) {
            RainfallTendencyDto dto = new RainfallTendencyDto();
            dto.setTm(startTime);
            dto.setShowTm(DateUtil.dateToStringDay2(startTime));
            BigDecimal bigDecimal = map.get(startTime) == null ? new BigDecimal(0) : map.get(startTime);
            dto.setRainfall(bigDecimal);
            resultList.add(dto);
            startTime = DateUtil.getNextMonth(startTime, 1);
        }

        Map<String, Object> resultMap = new HashMap<>();
        BigDecimal minValue = new BigDecimal(0);
        BigDecimal maxValue = new BigDecimal(0);
        if (set.size() > 0) {
            minValue = set.first();
            maxValue = set.last();
        }
        resultMap.put("minValue", Math.floor(minValue.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()));//最小值
        resultMap.put("maxValue", Math.ceil(maxValue.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue()));//最大值
        resultMap.put("list", list);
        return resultMap;
    }
}
