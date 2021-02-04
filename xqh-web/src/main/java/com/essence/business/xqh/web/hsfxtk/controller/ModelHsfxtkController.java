package com.essence.business.xqh.web.hsfxtk.controller;

import com.essence.business.xqh.api.hsfxtk.ModelCallHsfxtkService;
import com.essence.business.xqh.api.hsfxtk.dto.*;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 洪水风险调控模型相关控制层
 */
@RestController
@RequestMapping("/modelHsfxtk")
public class ModelHsfxtkController {


    @Autowired
    ModelCallHsfxtkService modelCallHsfxtkService;

    /**
     * 根据方案名称查询方案
     * @return
     */
    @RequestMapping(value = "getPlanInfoByName/{planName}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanInfoByName(@PathVariable String planName) {
        try {
            Integer planInfoByName = modelCallHsfxtkService.getPlanInfoByName(planName);

            return SystemSecurityMessage.getSuccessMsg("方案校验成功",planInfoByName);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("方案校验失败！",null);

        }
    }

    /**
     * 保存创建方案基本信息入库
     * @param vo
     * @return
     */
    @RequestMapping(value = "/savePlanToDb", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanToDb(@RequestBody PlanInfoHsfxtkVo vo) {
        try {
            String planId = modelCallHsfxtkService.savePlanToDb(vo);
            if (planId == null){
                return SystemSecurityMessage.getFailMsg("洪水风险调控方案保存失败！",null);
            }
            return SystemSecurityMessage.getSuccessMsg("洪水风险调控方案保存成功",planId);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("洪水风险调控方案保存失败！",null);

        }
    }

    /**
     * 防洪保护区设置获取模型列表
     * @return
     */
    @RequestMapping(value = "getModelHsfxList", method = RequestMethod.GET)
    public SystemSecurityMessage getModelHsfxList() {
        try {
            List<Object> modelList =  modelCallHsfxtkService.getModelList();

            return SystemSecurityMessage.getSuccessMsg("获取模型列表成功",modelList);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取模型列表失败！",null);

        }
    }

    /**
     * 防洪保护区设置-根据保护区（模型）查询河道糙率列表
     * @return
     */
    @RequestMapping(value = "getModelRiverRoughness/{modelId}", method = RequestMethod.GET)
    public SystemSecurityMessage getModelRiverRoughness(@PathVariable String modelId) {
        try {
            List<Object> modelRiverRoughnessList =  modelCallHsfxtkService.getModelRiverRoughness(modelId);

            return SystemSecurityMessage.getSuccessMsg("查询河道糙率列表成功",modelRiverRoughnessList);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询河道糙率列表失败！",null);
        }
    }

    /**
     * 防洪保护区设置-保存方案输入-糙率参数设置入库
     * @return
     */
    @RequestMapping(value = "saveModelRiverRoughness", method = RequestMethod.POST)
    public SystemSecurityMessage saveModelRiverRoughness(@RequestBody ModelParamVo modelParamVo) {
        try {
            ModelParamVo  modelParamVos =  modelCallHsfxtkService.saveModelRiverRoughness(modelParamVo);

            return SystemSecurityMessage.getSuccessMsg("防洪保护区设置-保存方案输入-糙率参数设置入库成功",modelParamVos);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("防洪保护区设置-保存方案输入-糙率参数设置入库失败！",null);
        }
    }

    /**
     * 根据模型id查询边界条件列表
     * @return
     */
    @RequestMapping(value = "getModelBoundaryBasic", method = RequestMethod.POST)
    public SystemSecurityMessage getModelBoundaryBasic(@RequestBody ModelParamVo modelParamVo) {
        try {
            List<Object> modelRiverRoughnessList =  modelCallHsfxtkService.getModelBoundaryBasic(modelParamVo);

            return SystemSecurityMessage.getSuccessMsg("查询方案边界条件列表成功",modelRiverRoughnessList);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询方案边界列表失败！",null);
        }
    }

    /**
     * 下载边界条件数据模板
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportBoundaryTemplate/{planId}/{modelId}", method = RequestMethod.GET)
    public void exportBoundaryTemplate(HttpServletRequest request, HttpServletResponse response,@PathVariable String planId,@PathVariable String modelId) {
        try {
            Workbook workbook = modelCallHsfxtkService.exportDutyTemplate(planId,modelId);
            //响应尾
            response.setContentType("applicationnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "边界数据模板.xlsx";
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
     * 上传界条件数据解析-Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    @RequestMapping(value = "/importBoundaryData/{planId}/{modelId}", method = RequestMethod.POST)
    public SystemSecurityMessage importBoundaryData(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile,@PathVariable String planId,@PathVariable String modelId) {
        SystemSecurityMessage SystemSecurityMessage = null;
        // 值班表文件上传解析
        String checkFlog = ExcelUtil.checkFile(mutilpartFile);
        if (mutilpartFile == null) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件为空！", null);
        } else if (!"excel".equals(checkFlog)) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件类型错误！", null);
        } else {
            // 解析表格数据为对象类型
            try {
                List<Object> boundaryList = modelCallHsfxtkService.importBoundaryData(mutilpartFile,planId,modelId);
                SystemSecurityMessage = new SystemSecurityMessage("ok", "边界数据表上传解析成功!", boundaryList);
            } catch (Exception e) {
                String eMessage = "";
                if (e != null) {
                    eMessage = e.getMessage();
                }
                SystemSecurityMessage = new SystemSecurityMessage("error", "边界数据表上传解析失败，错误原因：" + eMessage, null);
            }
        }
        return SystemSecurityMessage;
    }

    /**
     * 方案计算边界条件值-保存提交入库
     * @return
     */
    @RequestMapping(value = "savePlanBoundaryData/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanBoundaryData(@RequestBody List<YwkPlanInfoBoundaryDto> ywkPlanInfoBoundaryDtoList,@PathVariable String planId) {
        try {
            List<YwkPlanInfoBoundaryDto> boundaryList = modelCallHsfxtkService.savePlanBoundaryData(ywkPlanInfoBoundaryDtoList,planId);
            return SystemSecurityMessage.getSuccessMsg("方案计算边界条件值-提交入库成功",boundaryList);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("方案计算边界条件值-提交入库失败！",null);
        }
    }

    /**  TODO 已测试
     * 根据模型id获取溃口列表
     * @param
     * @return
     */
    @RequestMapping(value = "/getBreakList/{modelId}", method = RequestMethod.GET)
    public SystemSecurityMessage getBreakList( @PathVariable String modelId) {
        try {
            List<YwkBreakBasicDto> result = modelCallHsfxtkService.getBreakList(modelId);
            return SystemSecurityMessage.getSuccessMsg("根据模型id获取溃口列表成功",result);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("根据模型id获取溃口列表失败！",new ArrayList<>());

        }
    }

    /**  TODO
     * 保存方案溃口信息
     * @param
     * @return
     */
    @RequestMapping(value = "/savePlanBreak", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanBreak( @RequestBody BreakVo breakDto) {
        try {
            BreakVo breakVo = modelCallHsfxtkService.savePlanBreak(breakDto);
            return SystemSecurityMessage.getSuccessMsg("保存溃口信息成功",breakVo);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存溃口信息失败！");

        }
    }
    @RequestMapping(value = "/test/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage savePlanToDb(@PathVariable String planId) {
        try {
            modelCallHsfxtkService.test(planId);

            return SystemSecurityMessage.getSuccessMsg("洪水风险调控方案保存成功",planId);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("洪水风险调控方案保存失败！",null);

        }
    }


    /**
     * 水文调度模型计算执行
     * @return
     */
    @RequestMapping(value = "/modelCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelCall2(@PathVariable  String planId) {
        try {
            modelCallHsfxtkService.callMode(planId);
            return SystemSecurityMessage.getSuccessMsg("调用洪水风险调控模型成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用洪水风险调控模型失败！");

        }
    }

}

