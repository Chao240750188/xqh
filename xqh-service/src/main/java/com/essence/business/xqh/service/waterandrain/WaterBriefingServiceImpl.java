package com.essence.business.xqh.service.waterandrain;

import com.essence.business.xqh.api.waterandrain.dto.ReservoirListDto;
import com.essence.business.xqh.api.waterandrain.dto.RiverListDto;
import com.essence.business.xqh.api.waterandrain.dto.WaterBriefListDto;
import com.essence.business.xqh.api.rainfall.vo.QueryParamDto;
import com.essence.business.xqh.api.waterandrain.service.WaterBriefingService;
import com.essence.business.xqh.common.util.DateUtil;
import com.essence.business.xqh.dao.dao.fhybdd.StStbprpBDao;
import com.essence.business.xqh.dao.dao.realtimemonitor.*;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.realtimemonitor.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fengpp
 * 2021/2/2 10:54
 */
@Service
public class WaterBriefingServiceImpl implements WaterBriefingService {

    @Autowired
    StStbprpBDao stStbprpBDao;
    @Autowired
    TWasRDao wasRDao;
    @Autowired
    TRiverRODao riverRODao;
    @Autowired
    TRsvrfsrBDao rsvrfsrBDao;
    @Autowired
    TRsvrfcchBDao rsvrfcchBDao;
    @Autowired
    TRsvrRDao rsvrRDao;

    /**
     * 水情服务-水情简报表
     *
     * @param year
     * @param mth
     * @return
     */
    @Override
    public List<WaterBriefListDto> getWaterBriefList(Integer year, Integer mth) {
        List<String> sttpList = new ArrayList<>();
        sttpList.add("DD");//闸坝
        sttpList.add("ZQ");//河道水文站
        sttpList.add("ZZ");//河道水位站
        sttpList.add("RR");//水库
        List<StStbprpB> stStbprpBList = stStbprpBDao.findBySttpInAndUsfl(sttpList, "1");

        List<StStbprpB> riverList = new ArrayList<>();//河道
        List<StStbprpB> reservoirList = new ArrayList<>();//水库
        List<StStbprpB> sluiceList = new ArrayList<>();//闸坝
        for (StStbprpB stStbprpB : stStbprpBList) {
            String sttp = stStbprpB.getSttp();
            if ("DD".equals(sttp)) {
                sluiceList.add(stStbprpB);
            } else if ("RR".equals(sttp)) {
                reservoirList.add(stStbprpB);
            } else if ("ZQ".equals(sttp) || "ZZ".equals(sttp)) {
                riverList.add(stStbprpB);
            }
        }

        String time;
        if (mth < 10) {
            time = year + "/0" + mth + "/01 00:00:00";
        } else {
            time = year + "/" + mth + "/01 00:00:00";
        }
        Date startTime = DateUtil.getDateByStringNormal(time);
        Date endTime = DateUtil.getNextSecond(DateUtil.getNextMonth(startTime, 1), -1);

        List<WaterBriefListDto> list = new ArrayList<>();
        List<WaterBriefListDto> riverWaterBriefList = this.getRiverWaterBriefList(riverList, startTime, endTime);
        list.addAll(riverWaterBriefList);
        List<WaterBriefListDto> reservoirWaterBriefList = this.getReservoirWaterBriefList(reservoirList, startTime, endTime);
        list.addAll(reservoirWaterBriefList);
        List<WaterBriefListDto> sluiceWaterBriefList = this.getSluiceWaterBriefList(sluiceList, startTime, endTime);
        list.addAll(sluiceWaterBriefList);
        return list;
    }

    //河道水情简报表
    private List<WaterBriefListDto> getRiverWaterBriefList(List<StStbprpB> list, Date startTime, Date endTime) {
        List<String> stcdList = new ArrayList<>();
        list.forEach(it -> {
            stcdList.add(it.getStcd());
        });
        List<TRiverR> riverRList = riverRODao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, startTime, endTime);
        Map<String, List<TRiverR>> map = riverRList.stream().collect(Collectors.groupingBy(TRiverR::getStcd));
        List<WaterBriefListDto> resultList = new ArrayList<>();
        for (StStbprpB stStbprpB : list) {
            String stcd = stStbprpB.getStcd();
            String rvnm = stStbprpB.getRvnm();
            String stnm = stStbprpB.getStnm();
            WaterBriefListDto dto = new WaterBriefListDto();
            dto.setStnm(stnm);
            dto.setRvnm(rvnm);
            if (map.containsKey(stcd)) {
                List<TRiverR> values = map.get(stcd);

                TreeSet<BigDecimal> waterLevelSet = new TreeSet<>();
                Map<BigDecimal, TRiverR> waterLevelMap = new HashMap<>();
                BigDecimal totalWaterLevel = new BigDecimal(0);
                int waterLevelCount = 0;

                TreeSet<BigDecimal> waterFlowSet = new TreeSet<>();
                Map<BigDecimal, TRiverR> waterFlowMap = new HashMap<>();
                BigDecimal totalWaterFlow = new BigDecimal(0);
                int waterFlowCount = 0;

                for (TRiverR tRiverR : values) {
                    String waterLevel = tRiverR.getZ();//水位
                    if (!"".equals(waterLevel) && waterLevel != null) {
                        BigDecimal bigDecimal = new BigDecimal(waterLevel);
                        waterLevelSet.add(bigDecimal);
                        waterLevelMap.put(bigDecimal, tRiverR);
                        totalWaterLevel = totalWaterLevel.add(bigDecimal);
                        waterLevelCount = waterLevelCount + 1;
                    }
                    String flow = tRiverR.getQ();//流量
                    if (!"".equals(flow) && flow != null) {
                        BigDecimal bigDecimal = new BigDecimal(flow);
                        waterFlowSet.add(bigDecimal);
                        waterFlowMap.put(bigDecimal, tRiverR);
                        totalWaterFlow = totalWaterFlow.add(bigDecimal);
                        waterFlowCount = waterFlowCount + 1;
                    }
                }
                if (waterLevelSet.size() > 0) {
                    dto.setMonthLowWaterLevel(waterLevelSet.first());
                    TRiverR tRiverR = waterLevelMap.get(waterLevelSet.last());
                    dto.setMaxWaterLevel(new BigDecimal(tRiverR.getZ()));
                    dto.setMaxWaterLevelTm(tRiverR.getTm());
                    BigDecimal avg = totalWaterLevel.divide(new BigDecimal(waterLevelCount),2,BigDecimal.ROUND_HALF_UP);
                    dto.setAvgWaterLevel(avg);
                }
                if (waterFlowSet.size() > 0) {
                    TRiverR tRiverR = waterFlowMap.get(waterFlowSet.last());
                    dto.setMaxFlow(new BigDecimal(tRiverR.getQ()));
                    dto.setMaxFlowTm(tRiverR.getTm());
                    BigDecimal avg = totalWaterFlow.divide(new BigDecimal(waterFlowCount),2,BigDecimal.ROUND_HALF_UP);
                    dto.setAvgFlow(avg);
                }
            }
            resultList.add(dto);
        }
        return resultList;
    }

    //水库水情简报表
    private List<WaterBriefListDto> getReservoirWaterBriefList(List<StStbprpB> list, Date startTime, Date endTime) {
        List<String> stcdList = new ArrayList<>();
        list.forEach(it -> {
            stcdList.add(it.getStcd());
        });
        List<TRsvrR> rsvrRList = rsvrRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, startTime, endTime);
        Map<String, List<TRsvrR>> map = rsvrRList.stream().collect(Collectors.groupingBy(TRsvrR::getStcd));
        List<WaterBriefListDto> resultList = new ArrayList<>();
        for (StStbprpB stStbprpB : list) {
            String stcd = stStbprpB.getStcd();
            String rvnm = stStbprpB.getRvnm();
            String stnm = stStbprpB.getStnm();
            WaterBriefListDto dto = new WaterBriefListDto();
            dto.setStnm(stnm);
            dto.setRvnm(rvnm);
            if (map.containsKey(stcd)) {
                List<TRsvrR> values = map.get(stcd);

                TreeSet<BigDecimal> waterLevelSet = new TreeSet<>();
                Map<BigDecimal, TRsvrR> waterLevelMap = new HashMap<>();
                BigDecimal totalWaterLevel = new BigDecimal(0);
                int waterLevelCount = 0;

                TreeSet<BigDecimal> waterFlowSet = new TreeSet<>();
                Map<BigDecimal, TRsvrR> waterFlowMap = new HashMap<>();
                BigDecimal totalWaterFlow = new BigDecimal(0);
                int waterFlowCount = 0;

                for (TRsvrR tRsvrR : values) {
                    String waterLevel = tRsvrR.getRz();//水位
                    if (!"".equals(waterLevel) && waterLevel != null) {
                        BigDecimal bigDecimal = new BigDecimal(waterLevel);
                        waterLevelSet.add(bigDecimal);
                        waterLevelMap.put(bigDecimal, tRsvrR);
                        totalWaterLevel = totalWaterLevel.add(bigDecimal);
                        waterLevelCount = waterLevelCount + 1;
                    }
                    String flow = tRsvrR.getInq();//流量
                    if (!"".equals(flow) && flow != null) {
                        BigDecimal bigDecimal = new BigDecimal(flow);
                        waterFlowSet.add(bigDecimal);
                        waterFlowMap.put(bigDecimal, tRsvrR);
                        totalWaterFlow = totalWaterFlow.add(bigDecimal);
                        waterFlowCount = waterFlowCount + 1;
                    }
                }
                if (waterLevelSet.size() > 0) {
                    dto.setMonthLowWaterLevel(waterLevelSet.first());
                    TRsvrR tRsvrR = waterLevelMap.get(waterLevelSet.last());
                    dto.setMaxWaterLevel(new BigDecimal(tRsvrR.getRz()));
                    dto.setMaxWaterLevelTm(tRsvrR.getTm());
                    BigDecimal avg = new BigDecimal(0);
                    if (waterLevelCount != 0) {
                        avg = totalWaterLevel.divide(new BigDecimal(waterLevelCount),2,BigDecimal.ROUND_HALF_UP);
                    }
                    dto.setAvgWaterLevel(avg);
                }
                if (waterFlowSet.size() > 0) {
                    TRsvrR tRsvrR = waterFlowMap.get(waterFlowSet.last());
                    dto.setMaxFlow(new BigDecimal(tRsvrR.getInq()));
                    dto.setMaxFlowTm(tRsvrR.getTm());
                    BigDecimal avg = new BigDecimal(0);
                    if (waterFlowCount > 0) {
                        avg = totalWaterFlow.divide(new BigDecimal(waterFlowCount),2,BigDecimal.ROUND_HALF_UP);
                    }
                    dto.setAvgFlow(avg);
                }
            }
            resultList.add(dto);
        }
        return resultList;
    }

    //闸坝水情简报表
    private List<WaterBriefListDto> getSluiceWaterBriefList(List<StStbprpB> list, Date startTime, Date endTime) {
        List<String> stcdList = new ArrayList<>();
        list.forEach(it -> {
            stcdList.add(it.getStcd());
        });

        List<TWasR> wasRList = wasRDao.findByStcdInAndTmBetweenOrderByTmDesc(stcdList, startTime, endTime);
        Map<String, List<TWasR>> map = wasRList.stream().collect(Collectors.groupingBy(TWasR::getStcd));

        List<WaterBriefListDto> resultList = new ArrayList<>();
        for (StStbprpB stStbprpB : list) {
            String stcd = stStbprpB.getStcd();
            String rvnm = stStbprpB.getRvnm();
            String stnm = stStbprpB.getStnm();
            WaterBriefListDto dto = new WaterBriefListDto();
            dto.setStnm(stnm);
            dto.setRvnm(rvnm);
            if (map.containsKey(stcd)) {
                List<TWasR> values = map.get(stcd);

                TreeSet<BigDecimal> waterLevelSet = new TreeSet<>();
                Map<BigDecimal, TWasR> waterLevelMap = new HashMap<>();
                BigDecimal totalWaterLevel = new BigDecimal(0);
                int waterLevelCount = 0;

                TreeSet<BigDecimal> waterFlowSet = new TreeSet<>();
                Map<BigDecimal, TWasR> waterFlowMap = new HashMap<>();
                BigDecimal totalWaterFlow = new BigDecimal(0);
                int waterFlowCount = 0;

                for (TWasR wasR : values) {
                    String waterLevel = wasR.getUpz();//水位
                    if (!"".equals(waterLevel) && waterLevel != null) {
                        BigDecimal bigDecimal = new BigDecimal(waterLevel);
                        waterLevelSet.add(bigDecimal);
                        waterLevelMap.put(bigDecimal, wasR);
                        totalWaterLevel = totalWaterLevel.add(bigDecimal);
                        waterLevelCount = waterLevelCount + 1;
                    }
                    String flow = wasR.getTgtq();//流量
                    if (!"".equals(flow) && flow != null) {
                        BigDecimal bigDecimal = new BigDecimal(flow);
                        waterFlowSet.add(bigDecimal);
                        waterFlowMap.put(bigDecimal, wasR);
                        totalWaterFlow = totalWaterFlow.add(bigDecimal);
                        waterFlowCount = waterFlowCount + 1;
                    }
                }
                if (waterLevelSet.size() > 0) {
                    dto.setMonthLowWaterLevel(waterLevelSet.first());
                    TWasR wasR = waterLevelMap.get(waterLevelSet.last());
                    dto.setMaxWaterLevel(new BigDecimal(wasR.getUpz()));
                    dto.setMaxWaterLevelTm(wasR.getTm());
                    BigDecimal avg=new BigDecimal(0);
                    if (waterLevelCount>0){
                         avg = totalWaterLevel.divide(new BigDecimal(waterLevelCount),2,BigDecimal.ROUND_HALF_UP);
                    }
                    dto.setAvgWaterLevel(avg);
                }
                if (waterFlowSet.size() > 0) {
                    TWasR wasR = waterFlowMap.get(waterFlowSet.last());
                    dto.setMaxFlow(new BigDecimal(wasR.getTgtq()));
                    dto.setMaxFlowTm(wasR.getTm());
                    BigDecimal avg=new BigDecimal(0);
                    if (waterFlowCount>0){
                        avg = totalWaterFlow.divide(new BigDecimal(waterFlowCount),2,BigDecimal.ROUND_HALF_UP);
                    }
                    dto.setAvgFlow(avg);
                }
            }
            resultList.add(dto);
        }
        return resultList;
    }

    /**
     * 水情服务-导出水情简报表
     *
     * @param year
     * @param mth
     * @return
     */
    @Override
    public Workbook exportWaterBriefList(Integer year, Integer mth, InputStream in) throws IOException {

        List<WaterBriefListDto> list = this.getWaterBriefList(year, mth);
        // 定义一个数据格式化对象
        XSSFWorkbook wb = new XSSFWorkbook(in);
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 11); // 字体高度
        font.setFontName("宋体"); // 字体
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                WaterBriefListDto dto = list.get(i);
                XSSFRow row = sheet.createRow(i + 2);
                row.createCell(0).setCellValue(i + 1);

                String rvnm = dto.getRvnm() == null ? "" : dto.getRvnm();
                row.createCell(1).setCellValue(rvnm);

                String stnm = dto.getStnm() == null ? "" : dto.getStnm();
                row.createCell(2).setCellValue(stnm);

                String monthLowWaterLevel = dto.getMonthLowWaterLevel() == null ? "" : dto.getMonthLowWaterLevel().toString();
                row.createCell(3).setCellValue(monthLowWaterLevel);

                String maxWaterLevel = dto.getMaxWaterLevel() == null ? "" : dto.getMaxWaterLevel().toString();
                row.createCell(4).setCellValue(maxWaterLevel);

                Date maxWaterLevelTm = dto.getMaxWaterLevelTm();
                String date = "";
                if (maxWaterLevelTm != null) {
                    date = DateUtil.dateToStringNormal3(maxWaterLevelTm);
                }
                row.createCell(5).setCellValue(date);

                String maxWaterLevelYearMax = dto.getMaxWaterLevelYearMax() == null ? "" : dto.getMaxWaterLevelYearMax().toString();
                row.createCell(6).setCellValue(maxWaterLevelYearMax);

                String maxWaterLevelYear = dto.getMaxWaterLevelYear() == null ? "" : dto.getMaxWaterLevelYear().toString();
                row.createCell(7).setCellValue(maxWaterLevelYear);

                String maxWaterLevelAvg = dto.getMaxWaterLevelAvg() == null ? "" : dto.getMaxWaterLevelAvg().toString();
                row.createCell(8).setCellValue(maxWaterLevelAvg);

                String avgWaterLevel = dto.getAvgWaterLevel() == null ? "" : dto.getAvgWaterLevel().toString();
                row.createCell(9).setCellValue(avgWaterLevel);

                String avgWaterLevelYearAvg = dto.getAvgWaterLevelYearAvg() == null ? "" : dto.getAvgWaterLevelYearAvg().toString();
                row.createCell(10).setCellValue(avgWaterLevelYearAvg);

                String avgWaterLevelAbsolute = dto.getAvgWaterLevelAbsolute() == null ? "" : dto.getAvgWaterLevelAbsolute().toString();
                row.createCell(11).setCellValue(avgWaterLevelAbsolute);

                String maxFlow = dto.getMaxFlow() == null ? "" : dto.getMaxFlow().toString();
                row.createCell(12).setCellValue(maxFlow);

                Date maxFlowTm = dto.getMaxFlowTm();
                String time = "";
                if (maxFlowTm != null) {
                    time = DateUtil.dateToStringNormal3(maxFlowTm);
                }
                row.createCell(13).setCellValue(time);

                String maxFlowYearMax = dto.getMaxFlowYearMax() == null ? "" : dto.getMaxFlowYearMax().toString();
                row.createCell(14).setCellValue(maxFlowYearMax);

                String maxFlowYear = dto.getMaxFlowYear() == null ? "" : dto.getMaxFlowYear().toString();
                row.createCell(15).setCellValue(maxFlowYear);

                String maxFlowAvg = dto.getMaxFlowAvg() == null ? "" : dto.getMaxFlowAvg().toString();
                row.createCell(16).setCellValue(maxFlowAvg);

                String avgFlow = dto.getAvgFlow() == null ? "" : dto.getAvgFlow().toString();
                row.createCell(17).setCellValue(avgFlow);

                String avgFlowYear = dto.getAvgFlowYear() == null ? "" : dto.getAvgFlowYear().toString();
                row.createCell(18).setCellValue(avgFlowYear);

                String avgFlowAbsolute = dto.getAvgFlowAbsolute() == null ? "" : dto.getAvgFlowAbsolute().toString();
                row.createCell(19).setCellValue(avgFlowAbsolute);

                for (Cell cell : row) {
                    cell.setCellStyle(style);
                }
            }
        }
        return wb;
    }


    /**
     * 水情服务-河道水情表
     *
     * @param paramDto
     * @return
     */
    @Override
    public List<RiverListDto> getRiverList(QueryParamDto paramDto) {

        List<String> sttpList = new ArrayList<>();
        sttpList.add("ZZ");
        sttpList.add("ZQ");
        List<StStbprpB> stbprpBList = stStbprpBDao.findBySttpInAndUsfl(sttpList, "1");
        if (stbprpBList == null || stbprpBList.size() == 0) {
            return new ArrayList<>();
        }

        List<String> stcdList = new ArrayList<>();
        stbprpBList.forEach(it -> {
            stcdList.add(it.getStcd());
        });

        List<Map<String, Object>> list = riverRODao.findRiverLastData(stcdList, paramDto.getEndTime());
        Map<String, LinkedList<Map<String, Object>>> map = this.handle(list);

        List<RiverListDto> resultList = new ArrayList<>();
        for (StStbprpB stStbprpB : stbprpBList) {
            String stcd = stStbprpB.getStcd();
            String stnm = stStbprpB.getStnm();
            RiverListDto dto = new RiverListDto();
            dto.setStcd(stcd);
            dto.setStnm(stnm);
            if (map.containsKey(stcd)) {
                BigDecimal newWaterLevel = null;//水位
                BigDecimal newFlow = null;//流量
                LinkedList<Map<String, Object>> linkedList = map.get(stcd);
                Map<String, Object> newValue = linkedList.get(0);//最近的一条数据
                if (newValue.get("WATERLEVEL") != null) {
                    newWaterLevel = new BigDecimal(newValue.get("WATERLEVEL").toString());
                }
                if (newValue.get("FLOW") != null) {
                    newFlow = new BigDecimal(newValue.get("FLOW").toString());
                }
                if (newValue.get("TM") != null){
                    dto.setTime(DateUtil.dateToStringNormal3((Date)newValue.get("TM")));
                }

                dto.setWaterLevel(newWaterLevel);
                dto.setFlow(newFlow);

                if (linkedList.size() > 1) {
                    Map<String, Object> oldValue = linkedList.get(1);//最早的数据
                    BigDecimal oldWaterLevel = null;//水位
                    BigDecimal oldFlow = null;//流量
                    if (newValue.get("WATERLEVEL") != null) {
                        oldWaterLevel = new BigDecimal(oldValue.get("WATERLEVEL").toString());
                    }
                    if (newValue.get("FLOW") != null) {
                        oldFlow = new BigDecimal(oldValue.get("FLOW").toString());
                    }

                    BigDecimal waterLevelChange = null;//水位变幅
                    BigDecimal flowChange = null;//流量变幅
                    if (newWaterLevel != null && oldWaterLevel != null) {
                        waterLevelChange = newWaterLevel.subtract(oldWaterLevel);
                    }
                    if (newFlow != null && oldFlow != null) {
                        flowChange = newFlow.subtract(oldFlow);
                    }
                    dto.setWaterLevelChange(waterLevelChange);
                    dto.setFlowChange(flowChange);
                }
            }
            resultList.add(dto);
        }
        return resultList;
    }


    /**
     * 水情服务-导出河道水情表
     *
     * @param paramDto
     * @return
     */
    @Override
    public Workbook exportRiverList(QueryParamDto paramDto, InputStream in) throws IOException {
        List<RiverListDto> list = this.getRiverList(paramDto);
        // 定义一个数据格式化对象
        XSSFWorkbook wb = new XSSFWorkbook(in);
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 11); // 字体高度
        font.setFontName("宋体"); // 字体
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                RiverListDto dto = list.get(i);
                XSSFRow row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(i + 1);

                String stcd = dto.getStcd() == null ? "" : dto.getStcd();
                row.createCell(1).setCellValue(stcd);

                String stnm = dto.getStnm() == null ? "" : dto.getStnm();
                row.createCell(2).setCellValue(stnm);

                String waterLevel = dto.getWaterLevel() == null ? "" : dto.getWaterLevel().toString();
                row.createCell(3).setCellValue(waterLevel);

                String waterLevelChange = dto.getWaterLevelChange() == null ? "" : dto.getWaterLevelChange().toString();
                row.createCell(4).setCellValue(waterLevelChange);

                String flow = dto.getFlow() == null ? "" : dto.getFlow().toString();
                row.createCell(5).setCellValue(flow);

                String flowChange = dto.getFlowChange() == null ? "" : dto.getFlowChange().toString();
                row.createCell(6).setCellValue(flowChange);

                for (Cell cell : row) {
                    cell.setCellStyle(style);
                }
            }
        }
        return wb;
    }


    //将数据分组，其中list中第一个为时间最近的数据
    private Map<String, LinkedList<Map<String, Object>>> handle(List<Map<String, Object>> list) {
        Map<String, LinkedList<Map<String, Object>>> map = new HashMap<>();
        for (Map<String, Object> tempMap : list) {
            String stcd = tempMap.get("STCD").toString();
            if (map.containsKey(stcd)) {
                LinkedList<Map<String, Object>> linkedList = map.get(stcd);
                linkedList.add(tempMap);
                map.put(stcd, linkedList);
            } else {
                LinkedList<Map<String, Object>> linkedList = new LinkedList<>();
                linkedList.add(tempMap);
                map.put(stcd, linkedList);
            }
        }
        return map;
    }

    /**
     * 水情服务-水库水情表
     *
     * @param paramDto
     * @return
     */
    @Override
    public List<ReservoirListDto> getReservoirList(QueryParamDto paramDto) {

        List<Map<String, Object>> stbprpList = stStbprpBDao.getFloodWarningInfo("RR");
        List<String> stcdList = new ArrayList<>();
        for (Map<String, Object> map : stbprpList) {
            String stcd = map.get("STCD").toString();
            stcdList.add(stcd);
        }
        //水库水情表
        List<Map<String, Object>> list = rsvrRDao.findReservoirLastData(stcdList, paramDto.getEndTime());
        Map<String, LinkedList<Map<String, Object>>> map = this.handle(list);

        //汛限水位表
        List<TRsvrfsrB> rsvrfsrBList = rsvrfsrBDao.findByStcdInaAndFstp(stcdList, "1");
        Map<String, String> rsvrfsrMap = rsvrfsrBList.stream().collect(Collectors.toMap(TRsvrfsrB::getStcd, TRsvrfsrB::getFsltdz));
        //防洪指标表
        List<TRsvrfcchB> rsvrfcchBList = rsvrfcchBDao.findByStcdIn(stcdList);
        Map<String, TRsvrfcchB> rsvrfcchBMap = rsvrfcchBList.stream().collect(Collectors.toMap(TRsvrfcchB::getStcd, tRsvrfcchB -> tRsvrfcchB));

        List<ReservoirListDto> resultList = new ArrayList<>();
        for (Map<String, Object> tempMap : stbprpList) {
            ReservoirListDto dto = new ReservoirListDto();
            String stcd = tempMap.get("STCD").toString();
            dto.setStcd(stcd);
            dto.setHnnm(tempMap.get("HNNM")==null?null:tempMap.get("HNNM").toString());
            dto.setStnm(tempMap.get("STNM")==null?null:tempMap.get("STNM").toString());
            if (rsvrfsrMap.get(stcd) != null) {
                dto.setFsltdz(new BigDecimal(rsvrfsrMap.get(stcd)));
            }
            TRsvrfcchB tRsvrfcchB = rsvrfcchBMap.get(stcd);
            if (tRsvrfcchB != null) {
                if (tRsvrfcchB.getDdz() != null) {
                    dto.setDdz(new BigDecimal(tRsvrfcchB.getDdz()));
                }
                if (tRsvrfcchB.getNormz() != null) {
                    dto.setNormz(new BigDecimal(tRsvrfcchB.getNormz()));
                }
            }
            if (map.containsKey(stcd)) {
                LinkedList<Map<String, Object>> linkedList = map.get(stcd);
                Map<String, Object> newValue = linkedList.get(0);
                BigDecimal rz = null;//库水位
                BigDecimal inq = null;//入库流量
                BigDecimal otq = null;//出库流量
                if (newValue.get("WATERLEVEL") != null) {
                    rz = new BigDecimal(newValue.get("WATERLEVEL").toString());
                }
                if (newValue.get("FLOW") != null) {
                    inq = new BigDecimal(newValue.get("FLOW").toString());
                }
                if (newValue.get("WATERFLOW") != null) {
                    otq = new BigDecimal(newValue.get("WATERFLOW").toString());
                }
                dto.setRz(rz);
                dto.setInq(inq);
                dto.setOtq(otq);
                int rwptn = 6;// 落 4 涨 5 平 6
                if (linkedList.size() > 1) {
                    Map<String, Object> oldValue = linkedList.get(1);
                    BigDecimal oldRz = null;
                    if (oldValue.get("WATERLEVEL") != null) {
                        oldRz = new BigDecimal(oldValue.get("WATERLEVEL").toString());
                    }
                    if (rz != null && oldRz != null) {
                        if (rz.compareTo(oldRz) == 1) {
                            rwptn = 5;
                        } else if (rz.compareTo(oldRz) == -1) {
                            rwptn = 4;
                        }
                    }
                }
                dto.setRwptn(rwptn);
            }
            resultList.add(dto);
        }
        return resultList;
    }


    /**
     * 水情服务-导出水库水情表
     *
     * @param paramDto
     * @return
     */
    @Override
    public Workbook exportReservoirList(QueryParamDto paramDto, InputStream in) throws IOException {
        List<ReservoirListDto> list = this.getReservoirList(paramDto);
        // 定义一个数据格式化对象
        XSSFWorkbook wb = new XSSFWorkbook(in);
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFFont font = wb.createFont();
        font.setFontHeightInPoints((short) 11); // 字体高度
        font.setFontName("宋体"); // 字体
        XSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setFont(font);
        style.setWrapText(true);
        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                ReservoirListDto dto = list.get(i);
                XSSFRow row = sheet.createRow(i + 2);
                row.createCell(0).setCellValue(i + 1);

                String stnm = dto.getStnm() == null ? "" : dto.getStnm();
                row.createCell(1).setCellValue(stnm);
                String rz = dto.getRz() == null ? "" : dto.getRz().toString();
                row.createCell(2).setCellValue(rz);
                String inq = dto.getInq() == null ? "" : dto.getInq().toString();
                row.createCell(3).setCellValue(inq);
                String otq = dto.getOtq() == null ? "" : dto.getOtq().toString();
                row.createCell(4).setCellValue(otq);
                String w = dto.getW() == null ? "" : dto.getW().toString();
                row.createCell(5).setCellValue(w);
                String floodWaterLevel = dto.getFloodWaterLevel() == null ? "" : dto.getFloodWaterLevel().toString();
                row.createCell(6).setCellValue(floodWaterLevel);
                String floodW = dto.getFloodW() == null ? "" : dto.getFloodW().toString();
                row.createCell(7).setCellValue(floodW);
                String normalWaterLevel = dto.getNormalWaterLevel() == null ? "" : dto.getNormalWaterLevel().toString();
                row.createCell(8).setCellValue(normalWaterLevel);
                String normalW = dto.getNormalW() == null ? "" : dto.getNormalW().toString();
                row.createCell(9).setCellValue(normalW);
                String ddz = dto.getDdz() == null ? "" : dto.getDdz().toString();
                row.createCell(10).setCellValue(ddz);
                String fsltdz = dto.getFsltdz() == null ? "" : dto.getFsltdz().toString();
                row.createCell(11).setCellValue(fsltdz);
                String normz = dto.getNormz() == null ? "" : dto.getNormz().toString();
                row.createCell(12).setCellValue(normz);
                String rwptn = dto.getRwptn() == null ? "" : dto.getRwptn().toString();
                row.createCell(13).setCellValue(rwptn);
                String hnnm = dto.getHnnm() == null ? "" : dto.getHnnm();
                row.createCell(14).setCellValue(hnnm);
                String stcd = dto.getStcd() == null ? "" : dto.getStcd();
                row.createCell(15).setCellValue(stcd);

                for (Cell cell : row) {
                    cell.setCellStyle(style);
                }
            }
        }
        return wb;
    }
}
