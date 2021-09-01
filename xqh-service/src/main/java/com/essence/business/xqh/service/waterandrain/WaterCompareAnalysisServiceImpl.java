package com.essence.business.xqh.service.waterandrain;

import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.waterandrain.service.WaterCompareAnalysisService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StPptnRDao;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TRiverRODao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TRsvrRDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.TWasRDao;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRiverR;
import com.essence.business.xqh.dao.entity.realtimemonitor.TRsvrR;
import com.essence.business.xqh.dao.entity.realtimemonitor.TWasR;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 水情信息查询-水情对比分析
 *
 * @author fengpp
 * 2021/3/9 14:07
 */
@Service
public class WaterCompareAnalysisServiceImpl implements WaterCompareAnalysisService {
    @Autowired
    StStbprpBDao stStbprpBDao;
    @Autowired
    TRiverRODao tRiverRODao;
    @Autowired
    TRsvrRDao tRsvrRDao;
    @Autowired
    TWasRDao tWasRDao;
    @Autowired
    StPptnRDao stPptnRDao;

    /**
     * 水情信息查询-水情对比分析-左侧树
     *
     * @return
     */
    @Override
    public List<Map<String, Object>> getStnmList(QueryParamDto dto) {
        String sttp = dto.getSttp();
        List<Map<String, Object>> list = new ArrayList<>();
        if ("level".equals(sttp)) {
            list = stStbprpBDao.findUseWaterLevelStbprpb();
        } else if ("flow".equals(sttp)) {
            list = stStbprpBDao.findUseWaterFlowStbprpb();
        }
        Map<String, List<Map<String, String>>> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> tempMap = list.get(i);
            String rvnm = tempMap.get("RVNM") == null ? "其他" : tempMap.get("RVNM").toString();
            String stcd = tempMap.get("STCD").toString();
            String stnm = tempMap.get("STNM").toString();
            Map<String, String> hashMap = new HashMap<>();
            hashMap.put("stcd", stcd);
            hashMap.put("stnm", stnm);
            List<Map<String, String>> mapList = map.get(rvnm) == null ? new ArrayList<>() : map.get(rvnm);
            mapList.add(hashMap);
            map.put(rvnm, mapList);
        }

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry<String, List<Map<String, String>>> tempMap : map.entrySet()) {
            Map<String, Object> hashMap = new HashMap<>();
            hashMap.put("rvnm", tempMap.getKey());
            hashMap.put("children", tempMap.getValue());
            resultList.add(hashMap);
        }
        return resultList;
    }

    /**
     * 水情信息查询-水情对比分析-水位
     *
     * @param dto
     * @return
     */
    @Override
    public Map<String, Object> getWaterLevelTendency(QueryParamDto dto, String flag) {
        Date startTime = dto.getStartTime();
        Date endTime = dto.getEndTime();
        List<String> stcdList = dto.getStcds();
        BigDecimal min = new BigDecimal(0);
        BigDecimal max = new BigDecimal(0);
        List<Map<String, Object>> list = new ArrayList<>();

        for (String stcd : stcdList) {
            StStbprpB stbprpB = stStbprpBDao.findByStcd(stcd);
            String sttp = stbprpB.getSttp();
            String stnm = stbprpB.getStnm();
            Map<String, Object> dataMap = new HashMap<>();

            if (sttp.equals("ZQ") || sttp.equals("ZZ")) {
                dataMap = this.getRiverList(stcd, startTime, endTime, flag);
            } else if (sttp.equals("RR")) {
                dataMap = this.getRsvrList(stcd, startTime, endTime, flag);
            } else if (sttp.equals("DD")) {
                dataMap = this.getwasList(stcd, startTime, endTime, flag);
            }

            BigDecimal min1 = new BigDecimal(dataMap.get("min").toString());
            if (min.compareTo(new BigDecimal(0)) == 0 || min.compareTo(min1) == 1) {
                min = min1;
            }
            BigDecimal max1 = new BigDecimal(dataMap.get("max").toString());
            if (max.compareTo(new BigDecimal(0)) == 0 || max.compareTo(max1) == -1) {
                max = max1;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("stcd", stcd);
            map.put("stnm", stnm);
            map.put("list", dataMap.get("list"));
            list.add(map);
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("max", max);
        resultMap.put("min", min);
        resultMap.put("data", list);
        return resultMap;
    }


    //河道
    private Map<String, Object> getRiverList(String stcd, Date startTime, Date endTime, String flag) {
        List<TRiverR> list = tRiverRODao.findByStcdAndTmBetweenOrderByTmDesc(stcd, startTime, endTime);
        BigDecimal min = new BigDecimal(0);
        BigDecimal max = new BigDecimal(0);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            TRiverR tRiverR = list.get(i);
            BigDecimal value = new BigDecimal(0);
            //flow:流量  level:水位
            if ("flow".equals(flag) && tRiverR.getQ() != null && !"".equals(tRiverR.getQ())) {
                value = new BigDecimal(tRiverR.getQ());
            } else if ("level".equals(flag) && tRiverR.getZ() != null && !"".equals(tRiverR.getZ())) {
                value = new BigDecimal(tRiverR.getZ());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("timeline", tRiverR.getTm().getTime());
            map.put("value", value);
            resultList.add(map);

            if (min.compareTo(new BigDecimal(0)) == 0 || min.compareTo(value) == 1) {
                min = value;
            }
            if (max.compareTo(new BigDecimal(0)) == 0 || max.compareTo(value) == -1) {
                max = value;
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("min", min);
        resultMap.put("max", max);
        resultMap.put("list", resultList);
        return resultMap;
    }

    //水库
    private Map<String, Object> getRsvrList(String stcd, Date startTime, Date endTime, String flag) {
        List<TRsvrR> list = tRsvrRDao.findByStcdAndTmBetweenOrderByTmDesc(stcd, startTime, endTime);
        BigDecimal min = new BigDecimal(0);
        BigDecimal max = new BigDecimal(0);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            TRsvrR tRsvrR = list.get(i);
            BigDecimal value = new BigDecimal(0);
            //flow:流量  level:水位
            if ("flow".equals(flag) && tRsvrR.getInq() != null && !"".equals(tRsvrR.getInq())) {
                value = new BigDecimal(tRsvrR.getInq());
            } else if ("level".equals(flag) && tRsvrR.getRz() != null && !"".equals(tRsvrR.getRz())) {
                value = new BigDecimal(tRsvrR.getRz());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("timeline", tRsvrR.getTm().getTime());
            map.put("value", value);
            resultList.add(map);

            if (min.compareTo(new BigDecimal(0)) == 0 || min.compareTo(value) == 1) {
                min = value;
            }
            if (max.compareTo(new BigDecimal(0)) == 0 || max.compareTo(value) == -1) {
                max = value;
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("min", min);
        resultMap.put("max", max);
        resultMap.put("list", resultList);
        return resultMap;
    }

    //闸坝
    private Map<String, Object> getwasList(String stcd, Date startTime, Date endTime, String flag) {
        List<TWasR> list = tWasRDao.findByStcdAndTmBetweenOrderByTmDesc(stcd, startTime, endTime);
        BigDecimal min = new BigDecimal(0);
        BigDecimal max = new BigDecimal(0);
        List<Map<String, Object>> resultList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            TWasR tWasR = list.get(i);
            BigDecimal value = new BigDecimal(0);
            //flwo:流量  level:水位
            if ("flow".equals(flag) && tWasR.getTgtq() != null && !"".equals(tWasR.getTgtq())) {
                value = new BigDecimal(tWasR.getTgtq());
            } else if ("level".equals(flag) && tWasR.getUpz() != null && !"".equals(tWasR.getUpz())) {
                value = new BigDecimal(tWasR.getUpz());
            }
            Map<String, Object> map = new HashMap<>();
            map.put("timeline", tWasR.getTm().getTime());
            map.put("value", value);
            resultList.add(map);

            if (min.compareTo(new BigDecimal(0)) == 0 || min.compareTo(value) == 1) {
                min = value;
            }
            if (max.compareTo(new BigDecimal(0)) == 0 || max.compareTo(value) == -1) {
                max = value;
            }
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("min", min);
        resultMap.put("max", max);
        resultMap.put("list", resultList);
        return resultMap;
    }

    @Override
    public List<StStbprpB> searchAllRainfallStations() {
        return stStbprpBDao.findUsePPStation();
    }

    @Override
    public List<Map<String, Object>> searchOneStationRainfall(QueryParamDto dto) {
        SimpleDateFormat formatMil = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatToMin = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startTime = dto.getStartTime();
        Date endTime = dto.getEndTime();
        List<Map<String, Object>> rainByStcdAndTimeBetween = stPptnRDao.findRainByStcdAndTimeBetween(dto.getStcd(), formatMil.format(startTime), formatMil.format(endTime));
        List<Map<String, Object>> result = new ArrayList<>();
        if(rainByStcdAndTimeBetween.size() == 0){
            String stnm = stStbprpBDao.findByStcd(dto.getStcd()).getStnm();
            for (Date time = DateUtil.getNextMinute(startTime, 60); time.before(DateUtil.getNextMinute(endTime, 60)); time = DateUtil.getNextMinute(time, 60)) {
                String formatTime = formatToMin.format(time);
                Map<String, Object> map = new HashMap<>();
                map.put("STNM", stnm);
                map.put("TM", formatTime);
                map.put("DRP", 0);
                result.add(map);
            }
        }else{
            List<String> tmList = rainByStcdAndTimeBetween.stream().map(m -> m.get("TM").toString()).collect(Collectors.toList());
            for (Date time = DateUtil.getNextMinute(startTime, 60); time.before(DateUtil.getNextMinute(endTime, 60)); time = DateUtil.getNextMinute(time, 60)) {
                String formatTime = formatToMin.format(time);
                Map<String, Object> map = new HashMap<>();
                map.put("STNM", rainByStcdAndTimeBetween.get(0).get("STNM"));
                if(!tmList.contains(formatTime)){
                    map.put("TM", formatTime);
                    map.put("DRP", 0);
                }else{
                    for (int i = 0; i < rainByStcdAndTimeBetween.size(); i++) {
                        if(rainByStcdAndTimeBetween.get(i).get("TM").toString().equals(formatTime)){
                            map.put("TM", formatTime);
                            map.put("DRP", rainByStcdAndTimeBetween.get(i).get("DRP"));
                        }
                    }
                }
                result.add(map);
            }
        }

        return result;
    }

    @Override
    public Workbook exportTriggerFlowTemplate(QueryParamDto dto) {
        Date startTime = dto.getStartTime();
        Date endTime = dto.getEndTime();

        SimpleDateFormat formatToMin = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        //封装边界模板数据
        XSSFWorkbook workbook = new XSSFWorkbook();

        //设置样式
        XSSFFont font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);//字体高度
        font.setFontName("宋体");//字体
        XSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true);//自动换行
        XSSFSheet sheet = workbook.createSheet("雨量站雨量数据导入模板");

        //填充表头
        //第一行
        XSSFRow row = sheet.createRow(0);
        XSSFCell cell = row.createCell(0);
        cell.setCellStyle(style);
        cell.setCellValue("雨量站名称");
        //设置自动列宽
        sheet.setColumnWidth(0, 2500);
        sheet.setColumnWidth(1, 3500);

        //封装数据
        int beginLine = 0;
        for (Date time = DateUtil.getNextMinute(startTime, 60); time.before(DateUtil.getNextMinute(endTime, 60)); time = DateUtil.getNextMinute(time, 60)) {
            beginLine++;
            sheet.setColumnWidth(beginLine, 5100);
            XSSFCell c = row.createCell(beginLine);
            String formatTime = formatToMin.format(time);
            c.setCellValue(formatTime);
            c.setCellStyle(style);
        }

        List<Map<String, Object>> result = searchOneStationRainfall(dto);
        XSSFRow row1 = sheet.createRow(1);
        XSSFCell c = row1.createCell(0);
        c.setCellValue(result.get(0).get("STNM").toString());
        c.setCellStyle(style);

        beginLine = 0;
        for (Date time = DateUtil.getNextMinute(startTime, 60); time.before(DateUtil.getNextMinute(endTime, 60)); time = DateUtil.getNextMinute(time, 60)) {
            beginLine++;
            c = row1.createCell(beginLine);
            c.setCellValue(result.get(beginLine-1).get("DRP") + "");
            c.setCellStyle(style);
        }

        return workbook;

    }

    @Override
    public List<Map<String, Object>> importOneStationRainfall(MultipartFile mutilpartFile) {
        List<String[]> strings = ExcelUtil.readFiles(mutilpartFile, 0);
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = 1; i < strings.get(0).length; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("STNM", strings.get(1)[0]);
            map.put("TM", strings.get(0)[i]);
            map.put("DRP", strings.get(1)[i]);
            result.add(map);
        }
        return result;
    }
}
