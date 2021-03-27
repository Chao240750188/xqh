package com.essence.business.xqh.web.fhybdd.controller;

import com.essence.business.xqh.api.fhybdd.dto.CalibrationMSJGAndScsVo;
import com.essence.business.xqh.api.fhybdd.dto.CalibrationXAJVo;
import com.essence.business.xqh.api.fhybdd.dto.CalibrationXGGXVo;
import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.fhybdd.service.ModelCallFhybddNewService;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlanCalibrationZoneDao;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlaninfoDao;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRcsBsin;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/modelNewController")
public class ModelNewController {


    @Autowired
    ModelCallFhybddNewService modelCallFhybddNewService;


    /**
     * 方案计划入库 TODO 未测试
     * @param vo
     * @return
     */
    @RequestMapping(value = "/savePlanToDb", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanWithCache(@RequestBody ModelCallBySWDDVo vo) {
        try {
            String planId = modelCallFhybddNewService.savePlan(vo);
            if ("isExist".equals(planId)){
                return SystemSecurityMessage.getFailMsg("方案名字已经存在，请重新输入！",null);
            }
            if (planId == null){
                return SystemSecurityMessage.getFailMsg("水文调度模型保存失败！",null);
            }
            return SystemSecurityMessage.getSuccessMsg("水文调度模型保存成功",planId);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("水文调度模型保存失败！",null);

        }
    }


    /**
     * 根据方案获取雨量信息
     * @param planId TODO 以测试
     * @return
     */
    @RequestMapping(value = "/getRainfalls/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getRainfalls(@PathVariable String planId) {
        try {
            YwkPlaninfo planinfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return SystemSecurityMessage.getFailMsg("方案id不存在");
            }
            List<Map<String, Object>> results11 = (List<Map<String, Object>>) CacheUtil.get("rainfall", planId+"new");
            if (!CollectionUtils.isEmpty(results11)){  //TODO inport的时候更新了缓存
                return SystemSecurityMessage.getSuccessMsg("根据方案获取雨量信息成功",results11);
            }
            List<Map<String,Object>> results = modelCallFhybddNewService.getRainfalls(planinfo);
            return SystemSecurityMessage.getSuccessMsg("根据方案获取雨量信息成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("根据方案获取雨量信息失败！");

        }
    }

     /**
     * 从缓存里获取获取雨量信息并存库
     * @param planId
     * @return
     */
    @RequestMapping(value = "/saveRainfallsFromCacheToDb/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage saveRainfallsFromCacheToDb(@PathVariable String planId) {
        try {
            YwkPlaninfo planinfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return SystemSecurityMessage.getFailMsg("方案id不存在");
            }
            List<Map<String,Object>> result = (List<Map<String,Object>>) CacheUtil.get("rainfall", planId+"new");

            if (CollectionUtils.isEmpty(result)){
                System.out.println("缓存里没有雨量信息");
                return SystemSecurityMessage.getFailMsg("缓存里没有雨量信息！");
            }
            modelCallFhybddNewService.saveRainfallsFromCacheToDb(planinfo,result);
            return SystemSecurityMessage.getSuccessMsg("保存雨量信息成功");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存雨量信息失败！");

        }
    }


    /**
     * 下载监测站雨量数据模板 TODO 已经对接
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportRainfallTemplate/{planId}", method = RequestMethod.GET)
    public void exportRainfallTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable String planId) {
        try {
            YwkPlaninfo planinfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return ;
            }
            Workbook workbook = modelCallFhybddNewService.exportRainfallTemplate(planinfo);
            //响应尾
            response.setContentType("applicationnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "监测站雨量数据模板.xlsx";
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
     * 上传监测站雨量数据解析-Excel导入 //TODO 已经对接，未保存
     *
     * @return SystemSecurityMessage 返回结果json
     */
    @RequestMapping(value = "/importRainfallData/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage importRainfallData(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile, @PathVariable String planId) {

        YwkPlaninfo planinfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planinfo == null){
            return new SystemSecurityMessage("error", "方案id不存在" );
        }
        SystemSecurityMessage SystemSecurityMessage = null;
        // 预报断面数据文件上传解析
        String checkFlog = ExcelUtil.checkFile(mutilpartFile);
        if (mutilpartFile == null) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件为空！", null);
        } else if (!"excel".equals(checkFlog)) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件类型错误！", null);
        } else {
            // 解析表格数据为对象类型
            try {
                List<Map<String,Object>> list = modelCallFhybddNewService.importRainfallData(mutilpartFile,planinfo);
                if (CollectionUtils.isEmpty(list)){
                    SystemSecurityMessage = new SystemSecurityMessage("error", "监测站雨量数据上传解析失败" );
                }else {
                    SystemSecurityMessage = new SystemSecurityMessage("ok", "监测站雨量数据上传解析成功!", list);
                }
            } catch (Exception e) {
                String eMessage = "";
                if (e != null) {
                    eMessage = e.getMessage();
                }
                SystemSecurityMessage = new SystemSecurityMessage("error", "监测站雨量数据上传解析失败，错误原因：" + eMessage, null);
            }
        }
        return SystemSecurityMessage;
    }


    /**
     * 集水区模型选择跟河段模型选择 //TODO 已测试
     * @return
     */
    @RequestMapping(value = "/getModelList", method = RequestMethod.GET)
    public SystemSecurityMessage getModelList() {
        try {
            Map<String,Object> results = modelCallFhybddNewService.getModelList();
            return SystemSecurityMessage.getSuccessMsg("获取集水区模型跟河段模型列表成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取集水区模型跟河段模型列表失败！");

        }
    }

    /**
     * 获取断面集合
     * @return
     */
    @RequestMapping(value = "/getRcsList", method = RequestMethod.GET)
    public SystemSecurityMessage getRcsList() {
        try {
            List<WrpRcsBsin> results = modelCallFhybddNewService.getRcsList();
            return SystemSecurityMessage.getSuccessMsg("获取断面列表成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取断面列表失败！");

        }
    }

    /**
     * 根据方案id获取预报断面流量
     * @return
     */
    @RequestMapping(value = "/getTriggerFlow/{planId}/{rcsId}", method = RequestMethod.GET)
    public SystemSecurityMessage getTriggerFlow(@PathVariable String planId,@PathVariable String rcsId) {
        try {
            YwkPlaninfo planinfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return SystemSecurityMessage.getFailMsg("方案id不存在！");
            }
            List<Map<String,Object>> results = modelCallFhybddNewService.getTriggerFlow(planinfo,rcsId);
            return SystemSecurityMessage.getSuccessMsg("根据方案id获取预报断面流量成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("根据方案id获取预报断面流量失败！");

        }
    }

    /**
     * 下载预报断面流量条件数据模板TODO 顺序很重要
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportTriggerFlowTemplate/{planId}/{rcsName}", method = RequestMethod.GET)
    public void exportTriggerFlowTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable String planId, @PathVariable String rcsName) {
        try {
            YwkPlaninfo planinfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return ;
            }
            Workbook workbook = modelCallFhybddNewService.exportTriggerFlowTemplate(planinfo);
            //响应尾
            response.setContentType("applicationnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "预报断面("+rcsName+")数据模板.xlsx";
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
     * 上传预报断面数据解析-Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    @RequestMapping(value = "/importTriggerFlowData/{planId}/{rcsId}", method = RequestMethod.POST)
    public SystemSecurityMessage importTriggerFlowData(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile, @PathVariable String planId,@PathVariable String rcsId) {

        YwkPlaninfo planinfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planinfo == null){
            return new SystemSecurityMessage("error", "方案不存在！", null);
        }
        SystemSecurityMessage SystemSecurityMessage = null;
        // 预报断面数据文件上传解析
        String checkFlog = ExcelUtil.checkFile(mutilpartFile);
        if (mutilpartFile == null) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件为空！", null);
        } else if (!"excel".equals(checkFlog)) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件类型错误！", null);
        } else {
            // 解析表格数据为对象类型
            try {
                List<Map<String,Object>> list = modelCallFhybddNewService.importTriggerFlowData(mutilpartFile,planinfo,rcsId);
                if (CollectionUtils.isEmpty(list)){
                    SystemSecurityMessage = new SystemSecurityMessage("error", "预报断面数据表上传解析失败" );
                }else {
                    SystemSecurityMessage = new SystemSecurityMessage("ok", "预报断面数据表上传解析成功!", list);
                }
            } catch (Exception e) {
                String eMessage = "";
                if (e != null) {
                    eMessage = e.getMessage();
                }
                SystemSecurityMessage = new SystemSecurityMessage("error", "预报断面数据表上传解析失败，错误原因：" + eMessage, null);
            }
        }
        return SystemSecurityMessage;
    }


    @Autowired
    YwkPlaninfoDao ywkPlaninfoDao;

    /**
     * 水文调度模型计算执行new版本
     * @return  tag 为0是第一次运算，为1是率定运算
     */
    @RequestMapping(value = "/modelCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelCall(@PathVariable String planId) {
        //TODO 这个地方优化从库里取
        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return  SystemSecurityMessage.getFailMsg( "方案不存在！，模型调用失败", null);
        }
        modelCallFhybddNewService.modelCall(planInfo);
        System.out.println("防洪与报警水文调度模型正在运行中。。。请稍等！"+Thread.currentThread().getName());
        return SystemSecurityMessage.getSuccessMsg("防洪与报警水文调度模型正在运行中。。。请稍等！");

    }

    /**
     * 获取模型运行状态
     * @return //0是第一次
     */
    @RequestMapping(value = "/getModelRunStatus/{planId}/{tag}",method = RequestMethod.GET)
    public SystemSecurityMessage getModelRunStatus(@PathVariable String planId,@PathVariable Integer tag){

        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return  SystemSecurityMessage.getFailMsg( "方案不存在！", null);
        }
        String status = modelCallFhybddNewService.getModelRunStatus(planInfo,tag);
        return SystemSecurityMessage.getSuccessMsg("获取模型列表信息成功",status);

    }

    /**
     * 获取模型运行输出结果
     * @return
     */
    @RequestMapping(value = "/getModelResultQ/{planId}/{tag}",method = RequestMethod.GET)
    public SystemSecurityMessage getModelResultQ(@PathVariable String planId,@PathVariable Integer tag){
        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return  SystemSecurityMessage.getFailMsg( "方案不存在！", null);
        }
        Object results = modelCallFhybddNewService.getModelResultQ(planInfo,tag);
        return SystemSecurityMessage.getSuccessMsg("获取模型列表信息成功",results);

    }

    /**
     * 获取模型运行率定输出结果
     * @param planId
     * @return
     */
    @RequestMapping(value = "/getModelResultQCalibration/{planId}",method = RequestMethod.GET)
    public SystemSecurityMessage getModelResultQCalibration(@PathVariable String planId){
        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return  SystemSecurityMessage.getFailMsg( "方案不存在！", null);
        }
        Object results = modelCallFhybddNewService.getModelResultQCalibration(planInfo);
        return SystemSecurityMessage.getSuccessMsg("获取模型列表信息成功",results);

    }



    //TODO 模型二次运算

    //单位线模型参数交互  这个地方前端给下载模板文档 TODO 已测试
    @RequestMapping(value = "/importCalibrationWithDWX/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage importCalibrationWithSCS(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile, @PathVariable String planId) {

        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return  SystemSecurityMessage.getFailMsg( "方案不存在！", null);
        }
        SystemSecurityMessage SystemSecurityMessage = null;
        // 预报断面数据文件上传解析
        String checkFlog = ExcelUtil.checkFile(mutilpartFile);
        if (mutilpartFile == null) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件为空！", null);
        } else if (!"excel".equals(checkFlog)) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件类型错误！", null);
        } else {
            // 解析表格数据为对象类型
            try {
                List<Map<String,Double>> list = modelCallFhybddNewService.importCalibrationWithDWX(mutilpartFile,planInfo);
                if (CollectionUtils.isEmpty(list)){
                    SystemSecurityMessage = new SystemSecurityMessage("error", "率定参数交互单位线解析失败" );
                }else {
                    SystemSecurityMessage = new SystemSecurityMessage("ok", "率定参数交互单位线解析成功", list);
                }
            } catch (Exception e) {
                String eMessage = "";
                if (e != null) {
                    eMessage = e.getMessage();
                }
                SystemSecurityMessage = new SystemSecurityMessage("error", "率定参数交互单位线解析失败，错误原因：" + eMessage, null);
            }
        }
        return SystemSecurityMessage;
    }




    /**
     * 保存单位线模型参数 TODO 已测试
     * @param planId
     * @return
     */
    @RequestMapping(value = "/saveCalibrationDwxToDB/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage saveCalibrationDwxToDB(@PathVariable String planId) {
        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return SystemSecurityMessage.getFailMsg("方案不存在");

        }
        List<Map<String,Double>> result = (List<Map<String,Double>>) CacheUtil.get("calibrationDWX", planId);

        if (CollectionUtils.isEmpty(result)){
            System.out.println("缓存里没有率定的单位线信息");
            return SystemSecurityMessage.getSuccessMsg("率定单位线信息未导入，使用默认数据计算");
        }
        try {
            modelCallFhybddNewService.saveCalibrationDwxToDB(planInfo,result);
            return SystemSecurityMessage.getSuccessMsg("保存率定单位线信息成功");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存率定单位线信息失败！");

        }
    }

    /**
     * 保存新安江模型参数
     * @param planId TODO 未接入
     * @return
     */
    @RequestMapping(value = "/saveCalibrationXAJToDB/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage saveCalibrationXAJToDB(@RequestBody List<CalibrationXAJVo> calibrationXAJVo, @PathVariable String planId) {
        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return SystemSecurityMessage.getFailMsg("方案不存在");
        }
        try {
            modelCallFhybddNewService.saveCalibrationXAJToDB(planInfo,calibrationXAJVo);
            return SystemSecurityMessage.getSuccessMsg("保存率定新安江数据成功");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存率定新安江数据失败！");

        }
    }

    /**
     * 保存相关关系模型参数
     * @param planId TODO 未接入
     * @return
     */
    @RequestMapping(value = "/saveCalibrationXGGXToDB/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage saveCalibrationXGGXToDB(@RequestBody List<CalibrationXGGXVo> calibrationXGGXVo, @PathVariable String planId) {
        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return SystemSecurityMessage.getFailMsg("方案不存在");
        }
        try {
            modelCallFhybddNewService.saveCalibrationXGGXToDB(planInfo,calibrationXGGXVo);
            return SystemSecurityMessage.getSuccessMsg("保存率定相关关系数据成功");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存率定相关关系数据失败！");

        }
    }

    /**
     * 保存马斯京根模型参数 TODO 已测试
     * @param planId,tag 0是SMSJG ，1 是SCS
     * @return
     */
    @RequestMapping(value = "/saveCalibrationMSJGOrScSToDB/{planId}/{tag}", method = RequestMethod.POST)
    public SystemSecurityMessage saveCalibrationMSJGOrScSToDB(@RequestBody CalibrationMSJGAndScsVo calibrationMSJGAndScsVo, @PathVariable String planId,@PathVariable Integer tag) {
        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return SystemSecurityMessage.getFailMsg("方案不存在");
        }
        try {
            modelCallFhybddNewService.saveCalibrationMSJGOrScSToDB(planInfo,calibrationMSJGAndScsVo,tag);
            if (tag == 0){
                return SystemSecurityMessage.getSuccessMsg("保存率定马斯京根数据成功");
            }
            return SystemSecurityMessage.getSuccessMsg("保存率定SCS-CN值成功");

        }catch (Exception e){
            e.printStackTrace();
            if (tag == 0){
                return SystemSecurityMessage.getFailMsg("保存率定马斯京根数据失败！");
            }
            return SystemSecurityMessage.getFailMsg("保存率定SCS-CN值失败！");

        }
    }

    @Autowired
    YwkPlanCalibrationZoneDao ywkPlanCalibrationZoneDao;


    //获取率定参数交互列表
    @RequestMapping(value = "/getCalibrationList/{planId}",method = RequestMethod.GET)
    public SystemSecurityMessage getCalibrationList(@PathVariable String planId){

        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return SystemSecurityMessage.getFailMsg("方案不存在");
        }
        Object results = modelCallFhybddNewService.getCalibrationList(planInfo);
        return SystemSecurityMessage.getSuccessMsg("获取率定参数交互列表",results);

    }

    /**
     * 水文调度模型计算执行new版本
     * @return  tag 为0是第一次运算，为1是率定运算
     */
    @RequestMapping(value = "/modelCallCalibrayion/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage ModelCallCalibration(@PathVariable String planId) {
        //TODO 这个地方优化从库里取
        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return SystemSecurityMessage.getFailMsg("方案不存在,模型不调用");
        }
        if (planInfo.getnPlanstatus() != 2L){
            System.out.println("模型首次计算未成功，不能进行率定运算");
            return SystemSecurityMessage.getFailMsg("模型首次计算未成功，不能进行率定运算");
        }
        if (planInfo.getnCalibrationStatus() != 0L){//todo 查看结果的时候能不能率定计算？
            return SystemSecurityMessage.getFailMsg("率定交互未进行，不进行二次计算！",-1);
        }
        modelCallFhybddNewService.ModelCallCalibration(planInfo);
        System.out.println("率定运算水文调度模型运行中....."+Thread.currentThread().getName());

        return SystemSecurityMessage.getSuccessMsg("率定运算水文调度模型运行中.....！");

    }

    /**
     * 方案结果保存入库，只能保存一条，率定前跟率定后的 TODO 方案保存的是肯定已经方案修改或者撤销了
     * @return
     */
    @RequestMapping(value = "/saveModelData/{planId}",method = RequestMethod.GET)
    public SystemSecurityMessage saveModelData(@PathVariable String planId){
        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return SystemSecurityMessage.getFailMsg("方案不存在");
        }

        if (planInfo.getnPlanstatus() != 2L){
            return SystemSecurityMessage.getFailMsg("方案运算未成功，不能保存");
        }

        try {
            modelCallFhybddNewService.saveModelData(planInfo);
            return SystemSecurityMessage.getSuccessMsg("保存模型运算结果成功");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("保存模型运算结果失败");

        }

    }

    /**
     * 修改撤销 ，修改保存
     * @param planId
     * @param tag
     * @return
     */
    @RequestMapping(value = "/saveOrDeleteResultCsv/{planId}/{tag}",method = RequestMethod.GET)
    public SystemSecurityMessage saveOrDeleteResultCsv(@PathVariable String planId,@PathVariable Integer tag){
        YwkPlaninfo planInfo = modelCallFhybddNewService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return SystemSecurityMessage.getFailMsg("方案不存在");
        }

        if (planInfo.getnCalibrationStatus() != 2L){
            return SystemSecurityMessage.getFailMsg("方案率定运算未成功，不能保存或者撤销");
        }
        String message = "";
        if (tag == 0){  //0是撤销
            message = "修改撤销";
        }else {
            message = "修改保存";
        }
        try {
            int ret = modelCallFhybddNewService.saveOrDeleteResultCsv(planInfo,tag);

            if (ret == 1){
                return SystemSecurityMessage.getSuccessMsg(message+"成功");
            }else {
                return SystemSecurityMessage.getSuccessMsg(message+"失败");
            }
        }catch (Exception e){
            e.printStackTrace();
                return SystemSecurityMessage.getFailMsg(message+"失败");

        }

    }


}
