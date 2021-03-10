package com.essence.business.xqh.web.fbc;

import com.essence.business.xqh.api.fbc.ModelFbcGcdService;
import com.essence.business.xqh.api.hsfxtk.dto.*;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkModelRoughnessParam;
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
 * 风暴潮感潮河段模型相关控制层
 */
@RestController
@RequestMapping("/modelFbcGcd")
public class ModelFbcGcdController {

    @Autowired
    ModelFbcGcdService modelFbcGcdService;

    /**
     * 根据方案名称查询方案
     *
     * @return
     */
    @RequestMapping(value = "getPlanInfoByName/{planName}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanInfoByName(@PathVariable String planName) {
        try {
            Integer planInfoByName = modelFbcGcdService.getPlanInfoByName(planName);

            return SystemSecurityMessage.getSuccessMsg("方案校验成功", planInfoByName);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("方案校验失败！", null);

        }
    }

    /**
     * 保存创建方案基本信息入库
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/savePlanToDb", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanToDb(@RequestBody PlanInfoHsfxtkVo vo) {
        try {
            String planId = modelFbcGcdService.savePlanToDb(vo);
            if (planId == null) {
                return SystemSecurityMessage.getFailMsg("风暴潮感潮河段方案保存失败！", null);
            }
            return SystemSecurityMessage.getSuccessMsg("风暴潮感潮河段方案保存成功", planId);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("风暴潮感潮河段方案保存失败！", null);

        }
    }

    /**
     * 防洪保护区设置获取模型列表
     *
     * @return
     */
    @RequestMapping(value = "getModelFbcGcdList", method = RequestMethod.GET)
    public SystemSecurityMessage getModelFbcGcdList() {
        try {
            List<Object> modelList = modelFbcGcdService.getModelList();

            return SystemSecurityMessage.getSuccessMsg("获取模型列表成功", modelList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取模型列表失败！", null);

        }
    }

    /**
     * 防洪保护区设置-根据保护区（模型）查询河道糙率列表
     *
     * @return
     */
    @RequestMapping(value = "getModelRiverRoughness/{modelId}", method = RequestMethod.GET)
    public SystemSecurityMessage getModelRiverRoughness(@PathVariable String modelId) {
        try {
            List<Object> modelRiverRoughnessList = modelFbcGcdService.getModelRiverRoughness(modelId);

            return SystemSecurityMessage.getSuccessMsg("查询河道糙率列表成功", modelRiverRoughnessList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询河道糙率列表失败！", null);
        }
    }

    /**
     * 防洪保护区设置-保存方案输入-糙率参数设置入库
     *
     * @return
     */
    @RequestMapping(value = "saveModelRiverRoughness/{nPlanid}/{idmodelId}", method = RequestMethod.POST)
    public SystemSecurityMessage saveModelRiverRoughness(@RequestBody YwkModelRoughnessParam ywkModelRoughnessParam,@PathVariable String nPlanid,@PathVariable String idmodelId) {
        try {
            ModelParamVo modelParamVos = modelFbcGcdService.saveModelRiverRoughness(ywkModelRoughnessParam,nPlanid,idmodelId);

            return SystemSecurityMessage.getSuccessMsg("防洪保护区设置-保存方案输入-糙率参数设置入库成功", modelParamVos);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("防洪保护区设置-保存方案输入-糙率参数设置入库失败！", null);
        }
    }

    /**
     * 根据模型id查询上游及交汇点流量边界条件列表
     *
     * @return
     */
    @RequestMapping(value = "getModelBoundaryBasic", method = RequestMethod.POST)
    public SystemSecurityMessage getModelBoundaryBasic(@RequestBody ModelParamVo modelParamVo) {
        try {
            List<Object> modelRiverRoughnessList = modelFbcGcdService.getModelBoundaryBasic(modelParamVo);

            return SystemSecurityMessage.getSuccessMsg("查询方案边界条件列表成功", modelRiverRoughnessList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询方案边界列表失败！", null);
        }
    }

    /**
     * 下载边界条件数据模板
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportBoundaryTemplate/{planId}/{modelId}", method = RequestMethod.GET)
    public void exportBoundaryTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable String planId, @PathVariable String modelId) {
        try {
            Workbook workbook = modelFbcGcdService.exportBoundaryTemplate(planId, modelId);
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
    public SystemSecurityMessage importBoundaryData(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile, @PathVariable String planId, @PathVariable String modelId) {
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
                List<Object> boundaryList = modelFbcGcdService.importBoundaryData(mutilpartFile, planId, modelId);
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
     *
     * @return
     */
    @RequestMapping(value = "savePlanBoundaryData/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanBoundaryData(@RequestBody List<YwkPlanInfoBoundaryDto> ywkPlanInfoBoundaryDtoList, @PathVariable String planId) {
        try {
            List<YwkPlanInfoBoundaryDto> boundaryList = modelFbcGcdService.savePlanBoundaryData(ywkPlanInfoBoundaryDtoList, planId);
            return SystemSecurityMessage.getSuccessMsg("方案计算边界条件值-提交入库成功", boundaryList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("方案计算边界条件值-提交入库失败！", null);
        }
    }

    /**
     * 根据模型id查询下游潮位边界条件列表
     *
     * @return
     */
    @RequestMapping(value = "getModelCwBoundaryBasic", method = RequestMethod.POST)
    public SystemSecurityMessage getModelCwBoundaryBasic(@RequestBody ModelParamVo modelParamVo) {
        try {
            List<Object> modelRiverRoughnessList = modelFbcGcdService.getModelCwBoundaryBasic(modelParamVo);

            return SystemSecurityMessage.getSuccessMsg("查询方案边界条件列表成功", modelRiverRoughnessList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询方案边界列表失败！", null);
        }
    }

    /**
     * 下载潮位边界条件数据模板
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportCwBoundaryTemplate/{planId}/{modelId}", method = RequestMethod.GET)
    public void exportCwBoundaryTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable String planId, @PathVariable String modelId) {
        try {
            Workbook workbook = modelFbcGcdService.exportCwBoundaryTemplate(planId, modelId);
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
     * 上传潮位界条件数据解析-Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    @RequestMapping(value = "/importCwBoundaryData/{planId}/{modelId}", method = RequestMethod.POST)
    public SystemSecurityMessage importCwBoundaryData(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile, @PathVariable String planId, @PathVariable String modelId) {
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
                List<Object> boundaryList = modelFbcGcdService.importCwBoundaryData(mutilpartFile, planId, modelId);
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
     * TODO 已测试
     * 根据模型id获取溃口列表
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/getBreakList/{modelId}", method = RequestMethod.GET)
    public SystemSecurityMessage getBreakList(@PathVariable String modelId) {
        try {
            List<YwkBreakBasicDto> result = modelFbcGcdService.getBreakList(modelId);
            return SystemSecurityMessage.getSuccessMsg("根据模型id获取溃口列表成功", result);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("根据模型id获取溃口列表失败！", new ArrayList<>());

        }
    }

    /**
     * 风暴潮感潮河段-获取风暴潮计算的方案（使用其潮位数据做感潮段计算使用）
     *
     * @return
     */
    @RequestMapping(value = "/getFbcPlaninfoList", method = RequestMethod.GET)
    public SystemSecurityMessage getFbcPlaninfoList() {
        List<YwkPlaninfo> list = new ArrayList<>();
        try {
            list = modelFbcGcdService.getFbcPlaninfoList();
            return SystemSecurityMessage.getSuccessMsg("获取暴潮模型计算过的成功！",list);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取暴潮模型计算过的失败！",list);
        }
    }

    /**
     * 根据选择的风暴潮方案id获取风暴潮数据
     * nPlanId 风暴潮方案的id
     * @return
     */
    @RequestMapping(value = "getModelCwBoundaryByFbcPlan/{nPlanId}", method = RequestMethod.GET)
    public SystemSecurityMessage getModelCwBoundaryByFbcPlan(@RequestBody ModelParamVo modelParamVo,@PathVariable String nPlanId) {
        try {
            List<Object> modelRiverRoughnessList = modelFbcGcdService.getModelCwBoundaryByFbcPlan(modelParamVo,nPlanId);

            return SystemSecurityMessage.getSuccessMsg("查询方案边界条件列表成功", modelRiverRoughnessList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询方案边界列表失败！", null);
        }
    }

    /**
     * 方案计算潮位边界条件值-保存提交入库
     *
     * @return
     */
    @RequestMapping(value = "savePlanCwBoundaryData/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanCwBoundaryData(@RequestBody List<YwkPlanInfoBoundaryDto> ywkPlanInfoBoundaryDtoList, @PathVariable String planId) {
        try {
            List<YwkPlanInfoBoundaryDto> boundaryList = modelFbcGcdService.savePlanCwBoundaryData(ywkPlanInfoBoundaryDtoList, planId);
            return SystemSecurityMessage.getSuccessMsg("方案计算边界条件值-提交入库成功", boundaryList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("方案计算边界条件值-提交入库失败！", null);
        }
    }

    /**
     * TODO
     * 保存方案溃口信息
     *
     * @param
     * @return
     */
    @RequestMapping(value = "/savePlanBreak", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanBreak(@RequestBody BreakVo breakDto) {
        try {
            BreakVo breakVo = modelFbcGcdService.savePlanBreak(breakDto);
            return SystemSecurityMessage.getSuccessMsg("保存溃口信息成功", breakVo);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存溃口信息失败！");

        }
    }

    /**
     * 风暴潮感潮河段-调用方案计算
     *
     * @return
     */
    @RequestMapping(value = "/modelCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelCall2(@PathVariable String planId) {
        try {
            modelFbcGcdService.callMode(planId);
            return SystemSecurityMessage.getSuccessMsg("调用风暴潮感潮河段模型成功！");
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用风暴潮感潮河段模型失败！");

        }
    }

    /**
     * 风暴潮感潮河段-获取方案计算进度
     *
     * @return
     */
    @RequestMapping(value = "/getHsfxModelRunStatus/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getHsfxModelRunStatus(@PathVariable String planId) {
        try {
            Object object = modelFbcGcdService.getHsfxModelRunStatus(planId);
            return SystemSecurityMessage.getSuccessMsg("风暴潮感潮河段-获取方案计算进度成功！", object);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("风暴潮感潮河段-获取方案计算进度失败！");

        }
    }

    /**
     * 模型输出淹没历程-及最大水深图片列表
     *
     * @return
     */
    @RequestMapping(value = "/getModelProcessPicList/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getModelProcessPicList(@PathVariable String planId) {
        try {
            Object object = modelFbcGcdService.getModelProcessPicList(planId);
            return SystemSecurityMessage.getSuccessMsg("模型输出淹没历程-及最大水深图片列表成功！", object);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("模型输出淹没历程-及最大水深图片列表失败！");
        }
    }

    /**
     * 预览水深过程及最大水深文件
     *
     * @param planId
     * @param picId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/preview/{planId}/{picId}", method = RequestMethod.GET)
    public @ResponseBody
    void preview(@PathVariable(value = "planId") String planId, @PathVariable(value = "picId") String picId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        modelFbcGcdService.previewPicFile(request, response, planId, picId);
    }

}

