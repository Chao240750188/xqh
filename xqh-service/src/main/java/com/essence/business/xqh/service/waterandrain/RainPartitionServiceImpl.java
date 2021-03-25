package com.essence.business.xqh.service.waterandrain;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.api.rainfall.vo.RainPartitionDto;
import com.essence.business.xqh.api.waterandrain.service.RainPartitionService;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpPartRelateDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpPartitionDao;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpPartRelate;
import com.essence.framework.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 雨水情查询-雨量信息查询分区业务实现层
 */
@Service
public class RainPartitionServiceImpl implements RainPartitionService {

    @Autowired
    private StStbprpPartitionDao stStbprpPartitionDao;
    @Autowired
    private StStbprpPartRelateDao stStbprpPartRelateDao;

    /**
     * 按类型和时间查询分区雨量
     *
     * @param reqDto
     * @return
     */
    @Override
    public Object getPartRain(RainPartitionDto reqDto) {
        DecimalFormat df = new DecimalFormat("0.00");
        JSONArray jsonArray = new JSONArray();
        //获取查询起始时间
        Date startTime = reqDto.getStartTime();
        String type = reqDto.getType();
        Date endTime = reqDto.getEndTime();
        //查询类型  1日，2月，3年，4时段，5上旬，6中旬，7下旬
        if ("1".equals(type)) {
            endTime = DateUtil.getNextDay(startTime, 1);
        }
        if ("2".equals(type)) {
            endTime = DateUtil.getNextMonth(startTime, 1);
        }
        if ("3".equals(type)) {
            endTime = DateUtil.getNextYear(startTime, 1);
        }
        if ("5".equals(type)) {
            endTime = DateUtil.getNextDay(startTime, 10);
        }
        if ("6".equals(type)) {
            endTime = DateUtil.getNextDay(startTime, 20);
            startTime = DateUtil.getNextDay(startTime, 10);
        }
        if ("7".equals(type)) {
            endTime = DateUtil.getNextMonth(startTime, 1);
            startTime = DateUtil.getNextDay(startTime, 20);
        }
        //查询分区关系表
        List<StStbprpPartRelate> stPartList = stStbprpPartRelateDao.findAll();
        Map<String, List<StStbprpPartRelate>> stPartMap = stPartList.stream().collect(Collectors.groupingBy(StStbprpPartRelate::getPart));
        //存放测站信息
        Map<String, String> stcdMap = new HashMap<>();
        //封装分区测站
        Map<String, List<String>> stcdPartMap = new HashMap<>();
        for (Map.Entry<String, List<StStbprpPartRelate>> entry : stPartMap.entrySet()) {
            String key = entry.getKey();
            List<StStbprpPartRelate> lists = entry.getValue();
            List<String> stcdList = new ArrayList<>();
            for (StStbprpPartRelate stStbprpPartRelate : lists) {
                stcdList.add(stStbprpPartRelate.getStcd());
                stcdMap.put(stStbprpPartRelate.getStcd(), stStbprpPartRelate.getStnm());
            }
            stcdPartMap.put(key, stcdList);
        }
        //查询封装数据
        for (Map.Entry<String, List<String>> entry : stcdPartMap.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            //分区编码
            String part_cd = entry.getKey();
            List<String> stcdList = entry.getValue();
            //查询测站降雨数据（按测站分组求和）
            List<Map<String, Object>> stcdRainDataList = stStbprpPartRelateDao.getPartRainByTime(stcdList, startTime, endTime);
            //分区平均雨量
            Double avgDrp = 0.0;
            //测站最大雨量
            Double maxDrp = 0.0;
            //最大雨量测站编码
            String maxStcd = "";
            for (Map<String, Object> map : stcdRainDataList) {
                String stcd = map.get("STCD") + "";
                Double drp = Double.parseDouble(map.get("DRP") + "");
                if (drp >= maxDrp) {
                    maxStcd = stcd;
                    maxDrp = drp;
                }
                avgDrp += drp;
            }
            //封装返回参数
            List<StStbprpPartRelate> stStbprpPartRelates = stPartMap.get(part_cd);
            jsonObject.put("partName", stStbprpPartRelates.get(0).getPartNm());
            try {
                jsonObject.put("partDrp", Double.parseDouble(df.format(avgDrp / stcdRainDataList.size()) + ""));
            } catch (Exception e) {
                jsonObject.put("partDrp", 0.0);
            }
            jsonObject.put("maxStcd", maxStcd);
            jsonObject.put("maxDrp", maxDrp);
            jsonObject.put("maxStnm", stcdMap.get(maxStcd));
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
}
