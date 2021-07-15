package com.essence.business.xqh.web.csnl;

import com.essence.business.xqh.api.csnl.ModelCsnlService;
import com.essence.business.xqh.api.csnl.vo.PlanInfoCsnlVo;
import com.essence.business.xqh.api.hsfxtk.dto.BreakVo;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.hsfxtk.dto.YwkBreakBasicDto;
import com.essence.business.xqh.api.hsfxtk.dto.YwkPlanInfoBoundaryDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.CacheUtil;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.common.util.PropertiesUtil;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
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

@RestController
@RequestMapping("/modelCsnl")
public class ModelCsnlController {
    @Autowired
    ModelCsnlService modelCsnlService;

    /**
     * 查询方案名称是否已存在
     * @param planName
     * @return
     */
    @RequestMapping(value = "/searchPlanIsExits/{planName}", method = RequestMethod.GET)
    public SystemSecurityMessage searchPlanIsExits(@PathVariable String planName) {
        try {
            Boolean isExits = modelCsnlService.searchPlanIsExits(planName);

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
     * 保存创建方案基本信息入库
     * @param vo
     * @return
     */
    @RequestMapping(value = "/savePlanToDb", method = RequestMethod.POST)
    public SystemSecurityMessage savePlanToDb(@RequestBody PlanInfoCsnlVo vo) {

        try {
            String results = modelCsnlService.savePlanToDb(vo);
            if ("planNameExist".equals(results)){
                return SystemSecurityMessage.getFailMsg("方案名称已存在，请重新输入！",null);
            }
            if (results == null){
                return SystemSecurityMessage.getFailMsg("城市内涝方案保存失败！",null);
            }
            return SystemSecurityMessage.getSuccessMsg("城市内涝方案保存成功",results);

        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("城市内涝方案保存失败！",null);

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
            YwkPlaninfo planinfo = modelCsnlService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return SystemSecurityMessage.getFailMsg("方案id不存在");
            }
            List<Map<String, Object>> cacheResult = (List<Map<String, Object>>) CacheUtil.get("rainfall", planId+"new");
            if (!CollectionUtils.isEmpty(cacheResult)){  //TODO import的时候更新了缓存
                return SystemSecurityMessage.getSuccessMsg("根据方案获取雨量信息成功",cacheResult);
            }
            List<Map<String,Object>> results = modelCsnlService.getRainfallsInfo(planinfo);
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
            YwkPlaninfo planinfo = modelCsnlService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return SystemSecurityMessage.getFailMsg("方案id不存在");
            }
            List<Map<String,Object>> results = (List<Map<String,Object>>) CacheUtil.get("rainfall", planId + "new");

            if (CollectionUtils.isEmpty(results)){
                System.out.println("缓存里没有雨量信息");
                return SystemSecurityMessage.getFailMsg("缓存里没有雨量信息！");
            }
            modelCsnlService.saveRainfallsFromCacheToDb(planinfo, results);
            return SystemSecurityMessage.getSuccessMsg("保存雨量信息成功");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存雨量信息失败！");

        }
    }

    /**
     * 下载监测站雨量数据模板
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportRainfallTemplate/{planId}", method = RequestMethod.GET)
    public void exportRainfallTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable String planId) {
        try {
            YwkPlaninfo planinfo = modelCsnlService.getPlanInfoByPlanId(planId);
            if (planinfo == null){
                return ;
            }
            Workbook workbook = modelCsnlService.exportRainfallTemplate(planinfo);
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

        YwkPlaninfo planinfo = modelCsnlService.getPlanInfoByPlanId(planId);
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
                List<Map<String,Object>> list = modelCsnlService.importRainfallData(mutilpartFile,planinfo);
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
     * 城市内涝-调用方案计算
     * @return
     */
    @RequestMapping(value = "/modelCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage modelCall(@PathVariable String planId) {
        try {
            //判断是否有运行中的
            String csnl_path = PropertiesUtil.read("/filePath.properties").getProperty("CSNL_MODEL");
            String csnl_model_template_output = csnl_path +
                    File.separator + PropertiesUtil.read("/filePath.properties").getProperty("MODEL_OUTPUT")
                    + File.separator + planId; //输出的地址
            //进度文件
            File jinduFile = new File(csnl_model_template_output+ File.separator+"jindu.txt");
            System.out.println("controller城市内涝模型调用方案id:"+planId);
            //存在表示执行失败
            if (jinduFile.exists()) {
                System.out.println("controller城市内涝模型调用拦截:"+planId);
                return SystemSecurityMessage.getSuccessMsg("调用城市内涝模型运行中！");
            }
            modelCsnlService.callMode(planId);
            return SystemSecurityMessage.getSuccessMsg("调用城市内涝模型成功！");
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用城市内涝模型失败！");

        }
    }

    /**
     * 获取模型运行状态
     * @return
     */
    @RequestMapping(value = "/getModelRunStatus/{planId}",method = RequestMethod.GET)
    public SystemSecurityMessage getModelRunStatus(@PathVariable String planId){

        YwkPlaninfo planInfo = modelCsnlService.getPlanInfoByPlanId(planId);
        if (planInfo == null){
            return  SystemSecurityMessage.getFailMsg( "方案不存在！", null);
        }
        Object status = modelCsnlService.getModelRunStatus(planInfo);
        return SystemSecurityMessage.getSuccessMsg("获取城市内涝模型运行状态成功",status);

    }

    /**
     * 模型输出淹没历程-及最大水深图片列表
     * @return
     */
    @RequestMapping(value = "/getModelProcessPicList/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getModelProcessPicList(@PathVariable  String planId) {
        try {
            Object object = modelCsnlService.getModelProcessPicList(planId);
            return SystemSecurityMessage.getSuccessMsg("模型输出淹没历程-及最大水深图片列表成功！",object);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("模型输出淹没历程-及最大水深图片列表失败！");
        }
    }

    /**
     * 预览内涝过程
     * @param planId
     * @param picId
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/previewFloodPic/{planId}/{picId}", method = RequestMethod.GET)
    public @ResponseBody
    void previewFloodPic(@PathVariable(value="planId") String planId,@PathVariable(value="picId") String picId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        modelCsnlService.previewFloodPic(request,response,planId,picId);
    }
}
