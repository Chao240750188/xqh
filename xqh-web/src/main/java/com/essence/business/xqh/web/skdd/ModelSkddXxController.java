package com.essence.business.xqh.web.skdd;

import com.essence.business.xqh.api.skdd.ModelSkddXxService;
import com.essence.business.xqh.api.skdd.vo.ModelSkddXxInputVo;
import com.essence.business.xqh.api.skdd.vo.ModelSkddXxPlanVo;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

@RestController
@RequestMapping("/modelSkddXx")
@SuppressWarnings("all")
public class ModelSkddXxController {

    @Autowired
    ModelSkddXxService modelSkddXxService;

    /**
     * 获取集水区模型和河段模型列表
     * @return
     */
    @RequestMapping(value = "/getModelList", method = RequestMethod.GET)
    public SystemSecurityMessage getModelList() {
        try {
            Map<String,Object> results = modelSkddXxService.getModelList();
            return SystemSecurityMessage.getSuccessMsg("获取集水区模型跟河段模型列表成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取集水区模型跟河段模型列表失败！");

        }
    }

    /**
     * 查询方案名称是否已存在
     * @param planName
     * @return
     */
    @RequestMapping(value = "/searchPlanIsExits/{planName}", method = RequestMethod.GET)
    public SystemSecurityMessage searchPlanIsExits(@PathVariable String planName) {
        try {
            Boolean isExits = modelSkddXxService.searchPlanIsExits(planName);

            if (isExits){
                return SystemSecurityMessage.getFailMsg("方案名称重复！",0); //存在返回0
            }

            return SystemSecurityMessage.getSuccessMsg("方案名称可使用", 1); //不存在返回1
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询方案名称失败！",null);

        }
    }

    /**
     * 方案计划入库
     * @param vo
     * @return
     */
    @RequestMapping(value = "/savePlanToDb", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanWithCache(@RequestBody ModelSkddXxPlanVo vo) {
        try {
            String results = modelSkddXxService.savePlan(vo);
            if ("planNameExist".equals(results)){
                return SystemSecurityMessage.getFailMsg("方案名称已存在，请重新输入！",null);
            }
            if (results == null){
                return SystemSecurityMessage.getFailMsg("水库调度汛限方案保存失败！",null);
            }
            return SystemSecurityMessage.getSuccessMsg("水库调度汛限方案保存成功",results);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("水库调度汛限方案保存失败！",null);

        }
    }

    /**
     * 根据方案获取雨量信息
     * @param planId
     * @return
     */
    @RequestMapping(value = "/getRainfallsInfo/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getRainfallsInfo(@PathVariable String planId) {
        try {
            YwkPlaninfo planinfo = modelSkddXxService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return SystemSecurityMessage.getFailMsg("方案id不存在");
            }
            List<Map<String, Object>> cacheResult = (List<Map<String, Object>>) CacheUtil.get("rainfall", planId+"new");
            if (!CollectionUtils.isEmpty(cacheResult)){  //TODO import的时候更新了缓存
                return SystemSecurityMessage.getSuccessMsg("根据方案获取雨量信息成功",cacheResult);
            }
            List<Map<String,Object>> results = modelSkddXxService.getRainfallsInfo(planinfo);
            return SystemSecurityMessage.getSuccessMsg("根据方案获取雨量信息成功",results);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("根据方案获取雨量信息失败！");

        }
    }

    /**
     * 从缓存里获取雨量信息并存库
     * @param planId
     * @return
     */
    @RequestMapping(value = "/saveRainfallsFromCacheToDb/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage saveRainfallsFromCacheToDb(@PathVariable String planId) {
        try {
            YwkPlaninfo planinfo = modelSkddXxService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return SystemSecurityMessage.getFailMsg("方案id不存在");
            }
            List<Map<String,Object>> results = (List<Map<String,Object>>) CacheUtil.get("rainfall", planId + "new");

            if (CollectionUtils.isEmpty(results)){
                System.out.println("缓存里没有雨量信息");
                return SystemSecurityMessage.getFailMsg("缓存里没有雨量信息！");
            }
            modelSkddXxService.saveRainfallsFromCacheToDb(planinfo, results);
            return SystemSecurityMessage.getSuccessMsg("保存雨量信息成功");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存雨量信息失败！");

        }
    }

    /**
     * 下载监测站雨量数据模板
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportRainfallTemplate/{planId}", method = RequestMethod.GET)
    public void exportRainfallTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable String planId) {
        try {
            YwkPlaninfo planinfo = modelSkddXxService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return ;
            }
            Workbook workbook = modelSkddXxService.exportRainfallTemplate(planinfo);
            //响应尾
            response.setContentType("applicationnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "雨量信息模板.xlsx";
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
     * 上传监测站雨量数据解析-Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    @RequestMapping(value = "/importRainfallData/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage importRainfallData(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile, @PathVariable String planId) {

        YwkPlaninfo planinfo = modelSkddXxService.getPlanInfoByPlanId(planId);
        if (planinfo == null){
            return new SystemSecurityMessage("error", "方案id不存在" );
        }
        SystemSecurityMessage SystemSecurityMessage = null;
        // 检查文件类型是否符合要求
        String checkFlog = ExcelUtil.checkFile(mutilpartFile);
        if (mutilpartFile == null) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件为空！", null);
        } else if (!"excel".equals(checkFlog)) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件类型错误！", null);
        } else {
            // 解析表格数据为对象类型
            try {
                List<Map<String,Object>> list = modelSkddXxService.importRainfallData(mutilpartFile,planinfo);
                if (CollectionUtils.isEmpty(list)){
                    SystemSecurityMessage = new SystemSecurityMessage("error", "雨量信息数据上传解析失败" );
                }else {
                    SystemSecurityMessage = new SystemSecurityMessage("ok", "雨量信息数据上传解析成功!", list);
                }
            } catch (Exception e) {
                String eMessage = "";
                if (e != null) {
                    eMessage = e.getMessage();
                }
                SystemSecurityMessage = new SystemSecurityMessage("error", "雨量信息数据上传解析失败，错误原因：" + eMessage, null);
            }
        }
        return SystemSecurityMessage;
    }

    /**
     * 水库调度初始水位和下泄流量保存
     */
    @RequestMapping(value = "/savePlanInputZ", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanInputZ(@RequestBody ModelSkddXxInputVo vo) {
        try {
            Boolean flag = modelSkddXxService.savePlanInputZ(vo);
            if (!flag){
                return SystemSecurityMessage.getFailMsg("水库调度初始水位和下泄流量保存失败！",null);
            }
            return SystemSecurityMessage.getSuccessMsg("水库调度初始水位和下泄流量保存成功",null);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("水库调度初始水位和下泄流量保存失败！",null);
        }
    }

    /**
     * 水库调度-汛限Pcp模型
     * @param planId
     * @return
     */
    @RequestMapping(value = "/modelPcpCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelPcpCall(@PathVariable String planId) {
        //TODO 这个地方优化从库里取
        YwkPlaninfo planinfo = modelSkddXxService.getPlanInfoByPlanId(planId);
        if (planinfo == null){
            return  SystemSecurityMessage.getFailMsg( "方案不存在！，模型调用失败", null);
        }
        modelSkddXxService.modelPcpCall(planinfo);
        System.out.println("水库调度-汛限Pcp模型正在运行中...请稍等！"+Thread.currentThread().getName());
        return SystemSecurityMessage.getSuccessMsg("水库调度-汛限Pcp模型正在运行中...请稍等！");

    }

    /**
     * 水库调度-汛限水文模型
     * @param planId
     * @return
     */
    @RequestMapping(value = "/modelHydrologyCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelHydrologyCall(@PathVariable String planId) {
        YwkPlaninfo planinfo = modelSkddXxService.getPlanInfoByPlanId(planId);
        if (planinfo == null){
            return  SystemSecurityMessage.getFailMsg( "方案不存在，模型调用失败", null);
        }
        modelSkddXxService.modelHydrologyCall(planinfo);
        System.out.println("水库调度-汛限水文模型正在运行中...请稍等！" + Thread.currentThread().getName());
        return SystemSecurityMessage.getSuccessMsg("水库调度-汛限水文模型正在运行中...请稍等！");

    }


    /**
     * 水库调度汛限模型计算
     * @return
     */
    @RequestMapping(value = "/modelCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelCall(@PathVariable String planId) {
        YwkPlaninfo planinfo = modelSkddXxService.getPlanInfoByPlanId(planId);
        if (planinfo == null){
            return SystemSecurityMessage.getFailMsg( "方案不存在！，模型调用失败", null);
        }
        modelSkddXxService.modelCall(planinfo);
        System.out.println("防洪与报警水文调度模型正在运行中。。。请稍等！"+Thread.currentThread().getName());
        return SystemSecurityMessage.getSuccessMsg("防洪与报警水文调度模型正在运行中。。。请稍等！");

    }

    /**
     * 获取模型运行状态
     * @return
     */
    @RequestMapping(value = "/getModelRunStatus/{planId}",method = RequestMethod.GET)
    public SystemSecurityMessage getModelRunStatus(@PathVariable String planId){

        YwkPlaninfo planInfo = modelSkddXxService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return  SystemSecurityMessage.getFailMsg( "方案不存在！", null);
        }
        String status = modelSkddXxService.getModelRunStatus(planInfo);
        return SystemSecurityMessage.getSuccessMsg("获取模型运行状态成功",status);

    }

    /**
     * 获取模型运行输出结果
     * @return
     */
    @RequestMapping(value = "/getModelResultQ/{planId}",method = RequestMethod.GET)
    public SystemSecurityMessage getModelResultQ(@PathVariable String planId){
        YwkPlaninfo planInfo = modelSkddXxService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return  SystemSecurityMessage.getFailMsg( "方案不存在！", null);
        }
        Object results = modelSkddXxService.getModelResultQ(planInfo);
        return SystemSecurityMessage.getSuccessMsg("获取模型列表信息成功",results);

    }

}
