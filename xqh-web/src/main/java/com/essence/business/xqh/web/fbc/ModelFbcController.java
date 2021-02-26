package com.essence.business.xqh.web.fbc;

import com.essence.business.xqh.api.fbc.ModelCallFbcService;
import com.essence.business.xqh.api.fbc.dto.PlanInfoFbcVo;
import com.essence.business.xqh.api.fbc.dto.YwkFbcPlanInfoBoundaryDto;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.hsfxtk.dto.PlanInfoHsfxtkVo;
import com.essence.business.xqh.api.hsfxtk.dto.YwkPlanInfoBoundaryDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.dao.entity.fbc.FbcHdpHhtdzW;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.List;

/**
 * 风暴潮模型相关控制层
 */
@RestController
@RequestMapping("/modelFbc")
public class ModelFbcController {

    @Autowired
    ModelCallFbcService modelCallFbcService;

    /**
     * 根据方案名称查询方案(创建方案前查询方案是否存在)
     *
     * @return
     */
    @RequestMapping(value = "getPlanInfoByName/{planName}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanInfoByName(@PathVariable String planName) {
        try {
            Integer planInfoByName = modelCallFbcService.getPlanInfoByName(planName);

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
    public SystemSecurityMessage savePlanToDb(@RequestBody PlanInfoFbcVo vo) {
        try {
            String planId = modelCallFbcService.savePlanToDb(vo);
            if (planId == null) {
                return SystemSecurityMessage.getFailMsg("风暴潮方案保存失败！", null);
            }
            return SystemSecurityMessage.getSuccessMsg("风暴潮方案保存成功", planId);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("风暴潮方案保存失败！", null);

        }
    }

    /**
     * 查询边界条件（水位/流量）初始数据列表
     *
     * @return
     */
    @RequestMapping(value = "getBoundaryZqBasic", method = RequestMethod.POST)
    public SystemSecurityMessage getModelBoundaryBasic(@RequestBody ModelParamVo modelParamVo) {
        try {
            List<Object> modelRiverRoughnessList = modelCallFbcService.getBoundaryZqBasic(modelParamVo);

            return SystemSecurityMessage.getSuccessMsg("查询方案边界条件列表成功", modelRiverRoughnessList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询方案边界列表失败！", null);
        }
    }

    /**
     * 下载边界条件（水位，流量）数据模板
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportBoundaryZqTemplate/{planId}", method = RequestMethod.GET)
    public void exportBoundaryTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable String planId) {
        try {
            Workbook workbook = modelCallFbcService.exportBoundaryZqTemplate(planId);
            //响应尾
            response.setContentType("applicationnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "边界（水位/流量）数据模板.xlsx";
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
     * 上传界条件(水位/流量)数据解析-Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    @RequestMapping(value = "/importBoundaryZq/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage importBoundaryData(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile, @PathVariable String planId) {
        SystemSecurityMessage SystemSecurityMessage = null;
        // 上传边界（水位/流量）条件文件数据
        String checkFlog = ExcelUtil.checkFile(mutilpartFile);
        if (mutilpartFile == null) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件为空！", null);
        } else if (!"excel".equals(checkFlog)) {
            SystemSecurityMessage = new SystemSecurityMessage("error", "上传文件类型错误！", null);
        } else {
            // 解析表格数据为对象类型
            try {
                List<Object> boundaryList = modelCallFbcService.importBoundaryZq(mutilpartFile, planId);
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
     * 方案计算边界条件值(水位/流量)-保存提交入库
     *
     * @return
     */
    @RequestMapping(value = "saveimportBoundaryZq/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanBoundaryData(@RequestBody List<YwkFbcPlanInfoBoundaryDto> ywkPlanInfoBoundaryDtoList, @PathVariable String planId) {
        try {
            List<YwkFbcPlanInfoBoundaryDto> boundaryList = modelCallFbcService.saveimportBoundaryZq(ywkPlanInfoBoundaryDtoList, planId);
            return SystemSecurityMessage.getSuccessMsg("方案计算边界条件值-提交入库成功", boundaryList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("方案计算边界条件值-提交入库失败！", null);
        }
    }

    /**
     * 风暴潮模型-调用方案计算
     * @return
     */
    @RequestMapping(value = "/fbcModelCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage fbcModelCall(@PathVariable  String planId) {
        try {
            List<FbcHdpHhtdzW> fbcHdpHhtdzWS = modelCallFbcService.fbcModelCall(planId);
            return SystemSecurityMessage.getSuccessMsg("调用风暴潮模型成功！",fbcHdpHhtdzWS);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用风暴潮模型失败！",null);
        }
    }

    /**
     * 获取模型运行输出结果(预报潮位数据)
     * @return
     */
    @RequestMapping(value = "/getModelResultTdz/{planId}",method = RequestMethod.GET)
    public SystemSecurityMessage getModelResultTdz(@PathVariable String planId){
        Object results = modelCallFbcService.getModelResultTdz(planId);
        return SystemSecurityMessage.getSuccessMsg("获取模型计算结果潮位数据成功",results);

    }

    /**
     * 获取方案列表成功
     * @return
     */
    @RequestMapping(value = "/getPlanList", method = RequestMethod.POST)
    public SystemSecurityMessage getPlanList(@RequestBody PaginatorParam paginatorParam) {
        try {
            Paginator planList = modelCallFbcService.getPlanList(paginatorParam);
            return SystemSecurityMessage.getSuccessMsg("获取风暴潮方案列表成功！", planList);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取风暴潮方案列表失败！");

        }
    }

    /**
     * 根据某个id查询方案的详细信息
     * @return
     */
    @RequestMapping(value = "/getPlanInfoByPlanId/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanInfoByPlanId(@PathVariable String planId) {
        try {
            Object planInfo = modelCallFbcService.getPlanInfoByPlanId(planId);
            return SystemSecurityMessage.getSuccessMsg("根据方案id获取方案信息成功！", planInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("根据方案id获取方案信息失败！");

        }
    }


    /**
     *  根据方案id获取水位/流量数据显示
     *  @param planId
     * @return
     */
    @RequestMapping(value = "/getBoundaryZqByPlanId/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getBoundaryZqByPlanId(@PathVariable String planId) {
        try {
            Object planInfo = modelCallFbcService.getBoundaryZqByPlanId(planId);
            return SystemSecurityMessage.getSuccessMsg("根据方案id获取水位/流量成功！", planInfo);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("根据方案id获取水位/流量失败！");

        }
    }

    /**
     * 删除方案以及方案下关联点所有入参
     * @param planId
     * @return
     */
    @RequestMapping(value = "/deleteAllInputByPlanId/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage deleteAllInputByPlanId(@PathVariable String planId) {
        try {
            modelCallFbcService.deleteAllInputByPlanId(planId);
            return SystemSecurityMessage.getSuccessMsg("删除方案信息成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除方案信息失败！");

        }
    }
}

