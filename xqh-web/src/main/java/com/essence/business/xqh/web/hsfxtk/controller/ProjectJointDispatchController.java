package com.essence.business.xqh.web.hsfxtk.controller;

import com.essence.business.xqh.api.fhybdd.dto.CalibrationMSJGAndScsVo;
import com.essence.business.xqh.api.fhybdd.dto.CalibrationXAJVo;
import com.essence.business.xqh.api.fhybdd.dto.CalibrationXGGXVo;
import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.hsfxtk.ProjectJointDispatchService;
import com.essence.business.xqh.api.hsfxtk.dto.BreakVo;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.hsfxtk.dto.YwkBreakBasicDto;
import com.essence.business.xqh.api.hsfxtk.dto.YwkPlanInfoBoundaryDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.dao.fhybdd.YwkPlanCalibrationZoneDao;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkModelRoughnessParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 工程联合调度相关控制层
 */
@RestController
@RequestMapping("/projectJointDispatch")
public class ProjectJointDispatchController {

    @Autowired
    ProjectJointDispatchService projectJointDispatchService;

    /**
     * 联合调度方案创建条件
     * 集水区模型选择跟河段模型选择
     *
     * @return
     */
    @RequestMapping(value = "/getModelList", method = RequestMethod.GET)
    public SystemSecurityMessage getModelList() {
        try {
            Map<String, Object> results = projectJointDispatchService.getModelList();
            return SystemSecurityMessage.getSuccessMsg("获取集水区模型跟河段模型列表成功", results);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取集水区模型跟河段模型列表失败！");

        }
    }

    /**
     * 根据方案名称查询方案
     * @return
     */
    @RequestMapping(value = "getPlanInfoByName/{planName}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanInfoByName(@PathVariable String planName) {
        try {
            Integer planInfoByName = projectJointDispatchService.getPlanInfoByName(planName);

            return SystemSecurityMessage.getSuccessMsg("方案校验成功",planInfoByName);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("方案校验失败！",null);

        }
    }


    /**
     * 保存联合调度方案计划入库
     *
     * @param vo
     * @return
     */
    @RequestMapping(value = "/savePlanToDb", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanWithCache(@RequestBody ModelCallBySWDDVo vo) {
        try {
            //工程联合调度默认计算小清河流域

            String planId = projectJointDispatchService.savePlan(vo);
            if ("isExist".equals(planId)) {
                return SystemSecurityMessage.getFailMsg("方案名字已经存在，请重新输入！", null);
            }
            if (planId == null) {
                return SystemSecurityMessage.getFailMsg("工程联合调度方案保存失败！", null);
            }
            return SystemSecurityMessage.getSuccessMsg("工程联合调度方案保存成功", planId);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("工程联合调度方案保存失败！", null);

        }
    }

    /**
     * 根据联合调度方案获取雨量信息
     *
     * @param planId
     * @return
     */
    @RequestMapping(value = "/getRainfalls/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getRainfalls(@PathVariable String planId) {
        try {
            YwkPlaninfo planinfo = projectJointDispatchService.getPlanInfoByPlanId(planId);
            if (planinfo == null) {
                return SystemSecurityMessage.getFailMsg("方案id不存在");
            }
            List<Map<String, Object>> results11 = (List<Map<String, Object>>) CacheUtil.get("rainfall", planId + "new");
            if (!CollectionUtils.isEmpty(results11)) {
                return SystemSecurityMessage.getSuccessMsg("根据方案获取雨量信息成功", results11);
            }
            List<Map<String, Object>> results = projectJointDispatchService.getRainfalls(planinfo);
            return SystemSecurityMessage.getSuccessMsg("根据方案获取雨量信息成功", results);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("根据方案获取雨量信息失败！");

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
            YwkPlaninfo planinfo = projectJointDispatchService.getPlanInfoByPlanId(planId);
            if (planinfo == null) {
                return;
            }
            Workbook workbook = projectJointDispatchService.exportRainfallTemplate(planinfo);
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
     * 上传监测站雨量数据解析-Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    @RequestMapping(value = "/importRainfallData/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage importRainfallData(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile, @PathVariable String planId) {

        YwkPlaninfo planinfo = projectJointDispatchService.getPlanInfoByPlanId(planId);
        if (planinfo == null) {
            return new SystemSecurityMessage("error", "方案id不存在");
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
                List<Map<String, Object>> list = projectJointDispatchService.importRainfallData(mutilpartFile, planinfo);
                if (CollectionUtils.isEmpty(list)) {
                    SystemSecurityMessage = new SystemSecurityMessage("error", "监测站雨量数据上传解析失败");
                } else {
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
     * 从缓存里获取获取雨量信息并存库
     *
     * @param planId
     * @return
     */
    @RequestMapping(value = "/saveRainfallsFromCacheToDb/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage saveRainfallsFromCacheToDb(@PathVariable String planId) {
        try {
            YwkPlaninfo planinfo = projectJointDispatchService.getPlanInfoByPlanId(planId);
            if (planinfo == null) {
                return SystemSecurityMessage.getFailMsg("方案id不存在");
            }
            List<Map<String, Object>> result = (List<Map<String, Object>>) CacheUtil.get("rainfall", planId + "new");

            if (CollectionUtils.isEmpty(result)) {
                System.out.println("缓存里没有雨量信息");
                return SystemSecurityMessage.getFailMsg("缓存里没有雨量信息！");
            }
            projectJointDispatchService.saveRainfallsFromCacheToDb(planinfo, result);
            return SystemSecurityMessage.getSuccessMsg("保存雨量信息成功");
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存雨量信息失败！");

        }
    }

    /**
     * 水文调度模型计算执行new版本
     *
     * @return tag 为0是第一次运算，为1是率定运算
     */
    @RequestMapping(value = "/modelCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelCall(@PathVariable String planId) {
        //TODO 这个地方优化从库里取
        YwkPlaninfo planInfo = projectJointDispatchService.getPlanInfoByPlanId(planId);
        if (planInfo == null) {
            return SystemSecurityMessage.getFailMsg("方案不存在！，模型调用失败", null);
        }
        projectJointDispatchService.modelCall(planInfo);
        System.out.println("防洪与报警水文调度模型正在运行中。。。请稍等！" + Thread.currentThread().getName());
        return SystemSecurityMessage.getSuccessMsg("防洪与报警水文调度模型正在运行中。。。请稍等！");

    }

    /**
     * 获取模型运行状态
     *
     * @return //0是第一次
     */
    @RequestMapping(value = "/getModelRunStatus/{planId}/{tag}", method = RequestMethod.GET)
    public SystemSecurityMessage getModelRunStatus(@PathVariable String planId, @PathVariable Integer tag) {

        YwkPlaninfo planInfo = projectJointDispatchService.getPlanInfoByPlanId(planId);
        if (planInfo == null) {
            return SystemSecurityMessage.getFailMsg("方案不存在！", null);
        }
        String status = projectJointDispatchService.getModelRunStatus(planInfo, tag);
        return SystemSecurityMessage.getSuccessMsg("获取模型运行状态成功", status);

    }

    /**
     * 获取水文模型计算后匹配水动力模型的边界条件数据列表
     * @return
     */
    @RequestMapping(value = "getSwModelBoundaryBasicData", method = RequestMethod.POST)
    public SystemSecurityMessage getSwModelBoundaryBasicData(@RequestBody ModelParamVo modelParamVo) {
        try {
            Object result =  projectJointDispatchService.getSwModelBoundaryBasicData(modelParamVo);
            return SystemSecurityMessage.getSuccessMsg("查询水文预报边界条件列表成功",result);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询水文预报边界条件列表失败！",null);
        }
    }

    /**
     * 保存水文模型的边界条件数据列表
     * @return
     */
    @RequestMapping(value = "saveSwModelBoundaryBasicData/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage saveSwModelBoundaryBasicData(@RequestBody List<YwkPlanInfoBoundaryDto> boundaryDtoList, @PathVariable String planId) {
        try {
            List<YwkPlanInfoBoundaryDto> swBoundaryList =  projectJointDispatchService.saveSwModelBoundaryBasicData(boundaryDtoList,planId);
            return SystemSecurityMessage.getSuccessMsg("保存水文预报边界条件列表成功",swBoundaryList);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存水文预报边界条件列表失败！",null);
        }
    }

    /**
     * 防洪保护区设置获取模型列表
     * @return
     */
    @RequestMapping(value = "getModelHsfxList", method = RequestMethod.GET)
    public SystemSecurityMessage getModelHsfxList() {
        try {
            List<Object> modelList =  projectJointDispatchService.getHsfxModelList();

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
            List<Object> modelRiverRoughnessList =  projectJointDispatchService.getModelRiverRoughness(modelId);

            return SystemSecurityMessage.getSuccessMsg("查询河道糙率列表成功",modelRiverRoughnessList);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询河道糙率列表失败！",null);
        }
    }

    /**
     * 防洪保护区设置-保存方案输入-糙率参数设置入库
     *
     * @return
     */
    @RequestMapping(value = "saveModelRiverRoughness/{nPlanid}/{idmodelId}", method = RequestMethod.POST)
    public SystemSecurityMessage saveModelRiverRoughness(@RequestBody YwkModelRoughnessParam ywkModelRoughnessParam, @PathVariable String nPlanid, @PathVariable String idmodelId) {
        try {
            ModelParamVo modelParamVos = projectJointDispatchService.saveModelRiverRoughness(ywkModelRoughnessParam,nPlanid,idmodelId);

            return SystemSecurityMessage.getSuccessMsg("防洪保护区设置-保存方案输入-糙率参数设置入库成功", modelParamVos);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("防洪保护区设置-保存方案输入-糙率参数设置入库失败！", null);
        }
    }


    /**
     * 根据模型id查询水动力边界条件列表
     * @return
     */
    @RequestMapping(value = "getModelBoundaryBasic", method = RequestMethod.POST)
    public SystemSecurityMessage getModelBoundaryBasic(@RequestBody ModelParamVo modelParamVo) {
        try {
            List<Object> modelRiverRoughnessList =  projectJointDispatchService.getModelBoundaryBasic(modelParamVo);

            return SystemSecurityMessage.getSuccessMsg("查询方案边界条件列表成功",modelRiverRoughnessList);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询方案边界列表失败！",null);
        }
    }

    /**
     * 下载水动力边界条件数据模板
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportBoundaryTemplate/{planId}/{modelId}", method = RequestMethod.GET)
    public void exportBoundaryTemplate(HttpServletRequest request, HttpServletResponse response,@PathVariable String planId,@PathVariable String modelId) {
        try {
            Workbook workbook = projectJointDispatchService.exportDutyTemplate(planId,modelId);
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
                List<Object> boundaryList = projectJointDispatchService.importBoundaryData(mutilpartFile,planId,modelId);
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
            List<YwkPlanInfoBoundaryDto> boundaryList = projectJointDispatchService.savePlanBoundaryData(ywkPlanInfoBoundaryDtoList,planId);
            return SystemSecurityMessage.getSuccessMsg("方案计算边界条件值-提交入库成功",boundaryList);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("方案计算边界条件值-提交入库失败！",null);
        }
    }


    /**
     * 获取模型运行输出结果
     *
     * @return
     */
    @RequestMapping(value = "/getModelResultQ/{planId}/{tag}", method = RequestMethod.GET)
    public SystemSecurityMessage getModelResultQ(@PathVariable String planId, @PathVariable Integer tag) {
        YwkPlaninfo planInfo = projectJointDispatchService.getPlanInfoByPlanId(planId);
        if (planInfo == null) {
            return SystemSecurityMessage.getFailMsg("方案不存在！", null);
        }
        Object results = projectJointDispatchService.getModelResultQ(planInfo, tag);
        return SystemSecurityMessage.getSuccessMsg("获取模型列表信息成功", results);
    }

    /**
     * 水文方案结果保存入库，只能保存一条，率定前跟率定后的 TODO 方案保存的是肯定已经方案修改或者撤销了
     *
     * @return
     */
    @RequestMapping(value = "/saveModelData/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage saveModelData(@PathVariable String planId) {
        YwkPlaninfo planInfo = projectJointDispatchService.getPlanInfoByPlanId(planId);
        if (planInfo == null) {
            return SystemSecurityMessage.getFailMsg("方案不存在");
        }
        if (planInfo.getnPlanstatus() != 2L) {
            return SystemSecurityMessage.getFailMsg("方案运算未成功，不能保存");
        }
        try {
            projectJointDispatchService.saveModelData(planInfo);
            return SystemSecurityMessage.getSuccessMsg("保存模型运算结果成功");
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getSuccessMsg("保存模型运算结果失败");
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
            List<YwkBreakBasicDto> result = projectJointDispatchService.getBreakList(modelId);
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
            BreakVo breakVo = projectJointDispatchService.savePlanBreak(breakDto);
            return SystemSecurityMessage.getSuccessMsg("保存溃口信息成功",breakVo);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存溃口信息失败！");

        }
    }


    /**
     * 洪水风险调度-调用方案计算
     * @return
     */
    @RequestMapping(value = "/modelCallHsfx/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelCallHsfx(@PathVariable  String planId) {
        try {
            //判断是否有运行中的
            String hsfx_path = PropertiesUtil.read("/filePath.properties").getProperty("HSFX_MODEL");
            String hsfx_model_template_output = hsfx_path +
                    File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                    + File.separator + planId; //输出的地址
            //进度文件
            File jinduFile = new File(hsfx_model_template_output+ File.separator+"jindu.txt");
            System.out.println("controller洪水动力模型调用了……方案id:"+planId);
            //存在表示执行失败
            if (jinduFile.exists()) {
                System.out.println("controller洪水动力模型调用拦截:"+planId);
                return SystemSecurityMessage.getSuccessMsg("调用洪水风险调控模型运行中！");
            }
            projectJointDispatchService.modelCallHsfx(planId);
            return SystemSecurityMessage.getSuccessMsg("调用洪水风险调控模型成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用洪水风险调控模型失败！");

        }
    }

    /**
     * 洪水风险调度-获取方案计算进度
     * @return
     */
    @RequestMapping(value = "/getHsfxModelRunStatus/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getHsfxModelRunStatus(@PathVariable  String planId) {
        try {
            Object object = projectJointDispatchService.getHsfxModelRunStatus(planId);
            return SystemSecurityMessage.getSuccessMsg("洪水风险调度-获取方案计算进度成功！",object);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("洪水风险调度-获取方案计算进度失败！");

        }
    }

    /**
     * 模型输出淹没历程-及最大水深图片列表
     * @return
     */
    @RequestMapping(value = "/getModelProcessPicList/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getModelProcessPicList(@PathVariable  String planId) {
        try {
            Object object = projectJointDispatchService.getModelProcessPicList(planId);
            return SystemSecurityMessage.getSuccessMsg("模型输出淹没历程-及最大水深图片列表成功！",object);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("模型输出淹没历程-及最大水深图片列表失败！");
        }
    }

    /**
     * 预览水深过程及最大水深文件
     * @param planId
     * @param picId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/preview/{planId}/{picId}", method = RequestMethod.GET)
    public @ResponseBody
    void preview(@PathVariable(value="planId") String planId,@PathVariable(value="picId") String picId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        projectJointDispatchService.previewPicFile(request,response,planId,picId);
    }
}

