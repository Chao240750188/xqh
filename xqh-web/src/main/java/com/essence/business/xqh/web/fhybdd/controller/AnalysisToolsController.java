package com.essence.business.xqh.web.fhybdd.controller;

import com.essence.business.xqh.api.fhybdd.service.AnalysisToolsService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.crypto.Data;
import java.io.OutputStream;
import java.util.*;

@RequestMapping("/analysisTool")
@RestController
public class AnalysisToolsController {

    @Autowired
    AnalysisToolsService analysisToolsService;

    /**
     * 获取水文站的涨差分析
     * @param map
     * @return
     */
    @RequestMapping(value = "/getAnalysisOfPriceDifference",method = RequestMethod.POST)
    public SystemSecurityMessage getAnalysisOfPriceDifference(@RequestBody Map map){

        try {
            List<Map<String,Object>> results = analysisToolsService.getAnalysisOfPriceDifference(map);
            return SystemSecurityMessage.getSuccessMsg("获取水文站的涨差分析数据成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取水文站的涨差分析数据失败",new ArrayList<>());

        }
    }

    /**
     * 获取分流比洪区列表
     * @param
     * @return
     */
        @RequestMapping(value = "/getFloodList",method = RequestMethod.GET)
        public SystemSecurityMessage getFloodList(){
            try {
                List<Map<String,Object>> results = analysisToolsService.getFloodList();
                return SystemSecurityMessage.getSuccessMsg("获取分流比洪区列表成功",results);
            }catch (Exception e){
                e.printStackTrace();
                return SystemSecurityMessage.getSuccessMsg("获取分流比洪区列表失败",new ArrayList<>());

            }
        }


    /**
     * 获取分流比计算
     * @param map
     * @return
     */
    @RequestMapping(value = "/getSplitRatioCalculation",method = RequestMethod.POST)
    public SystemSecurityMessage getSplitRatioCalculation(@RequestBody Map map){
        try {
            Map<String,Object> resultMap = analysisToolsService.getSplitRatioCalculation(map);
            return SystemSecurityMessage.getSuccessMsg("获取分流比计算数据成功",resultMap);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取分流比计算数据失败",new ArrayList<>());

        }
    }


    /**
     * 获取静库容反推入库数据
     * @param map
     * @return
     */
    @RequestMapping(value = "/getJkrftrkInformation",method = RequestMethod.POST)
    public SystemSecurityMessage getJkrftrkInformation(@RequestBody Map map){
        try {
            Map<String,Object> resultMap = analysisToolsService.getJkrftrkInformation(map);
            return SystemSecurityMessage.getSuccessMsg("获取静库容反推入库数据成功",resultMap);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取静库容反推入库数据失败",new ArrayList<>());

        }
    }

    /**
     * 获取水库列表成功
     * @return
     */
    @RequestMapping(value = "/getReservoirList",method = RequestMethod.GET)
    public SystemSecurityMessage getReservoirList(){
        try {
            Object results = analysisToolsService.getReservoirList();
            return SystemSecurityMessage.getSuccessMsg("获取水库数据列表成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取水库数据列表失败",new ArrayList<>());

        }
    }


    /**
     * before分段库容反推入库
     * @return
     */
    @RequestMapping(value = "/beforeGetJkrftrkInformationWithSection",method = RequestMethod.POST)
    public SystemSecurityMessage beforeGetJkrftrkInformationWithSection(@RequestBody Map map){
        try {
            Object results = analysisToolsService.beforeGetJkrftrkInformationWithSection(map);
            return SystemSecurityMessage.getSuccessMsg("获取分段库容反推时间序列成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取分段库容反推时间序列失败",new ArrayList<>());

        }
    }

    /**
     * 分段库容反推入库
     * @return
     */
    @RequestMapping(value = "/getJkrftrkInformationWithSection",method = RequestMethod.POST)
    public SystemSecurityMessage getJkrftrkInformationWithSection(@RequestBody List<Map> list){
        try {
            Object results = analysisToolsService.getJkrftrkInformationWithSection(list);
            return SystemSecurityMessage.getSuccessMsg("获取分段静库容反推入库数据成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("获取分段静库容反推入库数据失败",new ArrayList<>());

        }
    }
    /**
     * 下载出库流量模板
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportBeforeGetJkrftrkInformationTemplate/{rsrId}/{startTime}/{endTime}/{step}", method = RequestMethod.GET)
    public void exportCwBoundaryTemplate(HttpServletRequest request, HttpServletResponse response,
                                         @PathVariable String rsrId, @PathVariable String startTime, @PathVariable String endTime, @PathVariable String step) {
        try {
            HashMap<String, Object> map = new HashMap<>();
            map.put("rsrId",rsrId);
            map.put("step",step);
            map.put("startTime",startTime);
            map.put("endTime",endTime);
            Workbook workbook = analysisToolsService.exportBeforeGetJkrftrkInformationTemplate(map);
            //响应尾
            response.setContentType("applicationnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "出库流量模板.xlsx";
            response.setHeader("Content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "iso_8859_1"));
            OutputStream ouputStream = response.getOutputStream();
            workbook.write(ouputStream);
            ouputStream.flush();
            ouputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传出库流量数据解析-Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    @RequestMapping(value = "/importBeforeGetJkrftrkInformation", method = RequestMethod.POST)
    public SystemSecurityMessage importBeforeGetJkrftrkInformation(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile) {
        SystemSecurityMessage SystemSecurityMessage = null;
        // 出库流量文件上传解析
        String checkFlog = ExcelUtil.checkFile(mutilpartFile);
        if (mutilpartFile == null) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件为空！", null);
        } else if (!"excel".equals(checkFlog)) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件类型错误！", null);
        } else {
            // 解析表格数据为对象类型
            try {
                List<Object> boundaryList = analysisToolsService.importBeforeGetJkrftrkInformation(mutilpartFile);
                SystemSecurityMessage = new SystemSecurityMessage("ok", "出库流量数据表上传解析成功!", boundaryList);
            } catch (Exception e) {
                String eMessage = "";
                if (e != null) {
                    eMessage = e.getMessage();
                }
                SystemSecurityMessage = new SystemSecurityMessage("error", "出库流量数据表上传解析失败，错误原因：" + eMessage, null);
            }
        }
        return SystemSecurityMessage;
    }

}
