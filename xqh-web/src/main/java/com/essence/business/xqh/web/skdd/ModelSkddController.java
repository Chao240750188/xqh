package com.essence.business.xqh.web.skdd;

import com.essence.business.xqh.api.fbc.dto.PlanInfoFbcVo;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.skdd.ModelCallSkddService;
import com.essence.business.xqh.api.skdd.dto.Qdata;
import com.essence.business.xqh.api.skdd.dto.RainDataDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.business.xqh.common.util.ExcelUtil;
import com.essence.business.xqh.dao.entity.fbc.FbcHdpHhtdzW;
import com.essence.business.xqh.dao.entity.fhybdd.YwkModel;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
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
 * 水库调度相关控制层
 */
@RestController
@RequestMapping("/modelSkdd")
public class ModelSkddController {

    @Autowired
    ModelCallSkddService modelCallSkddService;

    /**
     * 根据方案名称查询方案(创建方案前查询方案是否存在)
     *
     * @return
     */

    @RequestMapping(value = "getPlanInfoByName/{planName}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanInfoByName(@PathVariable String planName) {
        try {
            Integer planInfoByName = modelCallSkddService.getPlanInfoByName(planName);

            return SystemSecurityMessage.getSuccessMsg("方案校验成功", planInfoByName);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("方案校验失败！", null);

        }
    }

    /**
     * 获取水库调度模型列表
     *
     * @return
     */
    @RequestMapping(value = "getModelInfoList", method = RequestMethod.GET)
    public SystemSecurityMessage getModelInfoList() {
        try {
            List<YwkModel> list = modelCallSkddService.getModelInfoList();

            return SystemSecurityMessage.getSuccessMsg("查询模型列表成功", list);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询模型列表失败！", null);

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
            String planId = modelCallSkddService.savePlanToDb(vo);
            if (planId == null) {
                return SystemSecurityMessage.getFailMsg("水库调度方案保存失败！", null);
            }
            return SystemSecurityMessage.getSuccessMsg("水库调度方案保存成功", planId);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("水库调度方案保存失败！", null);

        }
    }

    /**
     * 查询雨量数据初始数据列表
     *
     * @return
     */
    @RequestMapping(value = "getRainDataList", method = RequestMethod.POST)
    public SystemSecurityMessage getRainDataList(@RequestBody ModelParamVo modelParamVo) {
        try {
            List<Object> modelRiverRoughnessList = modelCallSkddService.getRainDataList(modelParamVo);

            return SystemSecurityMessage.getSuccessMsg("查询雨量数据初始数据成功", modelRiverRoughnessList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询雨量数据初始数据失败！", null);
        }
    }

    /**
     * 保存雨量数据
     */
    @RequestMapping(value = "saveRainDataList/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage saveRainDataList(@RequestBody List<RainDataDto> rainDataLsit,@PathVariable String planId) {
        try {
            List<RainDataDto> modelRiverRoughnessList = modelCallSkddService.saveRainDataList(rainDataLsit,planId);

            return SystemSecurityMessage.getSuccessMsg("保存雨量数据初始数据成功", modelRiverRoughnessList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存雨量数据初始数据失败！", null);
        }
    }

    /**
     * 查询流量数据初始数据列表
     *
     * @return
     */
    @RequestMapping(value = "/getSkddQDataList", method = RequestMethod.POST)
    public SystemSecurityMessage getSkddQDataList(@RequestBody ModelParamVo modelParamVo) {
        try {
            List<Object> list = modelCallSkddService.getSkddQDataList(modelParamVo);

            return SystemSecurityMessage.getSuccessMsg("查询流量数据初始数据成功", list);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询流量数据初始数据失败！", null);
        }
    }

    /**
     * 下载入库流量数据导入模板文件
     *
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportSkddQTemplate/{planId}", method = RequestMethod.GET)
    public void exportBoundaryTemplate(HttpServletRequest request, HttpServletResponse response, @PathVariable String planId) {
        try {
            Workbook workbook = modelCallSkddService.exportSkddQTemplate(planId);
            //响应尾
            response.setContentType("applicationnd.openxmlformats-officedocument.spreadsheetml.sheet");
            String fileName = "入库流量数据模板.xlsx";
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
     * 上传入库流量数据解析-Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    @RequestMapping(value = "/importSkddQData/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage importSkddQData(@RequestParam(value = "files", required = true) MultipartFile mutilpartFile, @PathVariable String planId) {
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
                List<Object> boundaryList = modelCallSkddService.importSkddQData(mutilpartFile, planId);
                SystemSecurityMessage = new SystemSecurityMessage("ok", "入库流量数据表上传解析成功!", boundaryList);
            } catch (Exception e) {
                String eMessage = "";
                if (e != null) {
                    eMessage = e.getMessage();
                }
                SystemSecurityMessage = new SystemSecurityMessage("error", "入库流量数据表上传解析失败，错误原因：" + eMessage, null);
            }
        }
        return SystemSecurityMessage;
    }

    /**
     * 保存入库流量数据
     */
    @RequestMapping(value = "saveQDataList/{planId}", method = RequestMethod.POST)
    public SystemSecurityMessage saveQDataList(@RequestBody List<Qdata> qDataLsit, @PathVariable String planId) {
        try {
            List<Qdata> modelRiverRoughnessList = modelCallSkddService.saveQDataList(qDataLsit,planId);

            return SystemSecurityMessage.getSuccessMsg("保存流量数据初始数据成功", modelRiverRoughnessList);

        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存流量数据初始数据失败！", null);
        }
    }


    /**
     * 调用水库调度模型计算
     * @return
     */
    @RequestMapping(value = "/skddModelCall/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage skddModelCall(@PathVariable  String planId) {
        try {
            List<FbcHdpHhtdzW> fbcHdpHhtdzWS = modelCallSkddService.skddModelCall(planId);
            return SystemSecurityMessage.getSuccessMsg("调用水库调度模型成功！",fbcHdpHhtdzWS);
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("调用水库调度模型失败！",null);
        }
    }

    /**
     * 获取水库调度方案列表
     *
     * @return
     */
    @RequestMapping(value = "/getSkddPlanList", method = RequestMethod.POST)
    public SystemSecurityMessage getSkddPlanList(@RequestBody PaginatorParam paginatorParam) {
        try {
            Paginator planList = modelCallSkddService.getPlanList(paginatorParam);
            return SystemSecurityMessage.getSuccessMsg("获取水库调度方案列表成功！", planList);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取水库调度方案列表失败！");

        }
    }

    /**
     * 根据方案id获取雨量信息
     *
     * @return
     */
    @RequestMapping(value = "/getPlanRainFallList/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanRainFallList(@PathVariable String planId) {
        try {
            List<Object> list = modelCallSkddService.getPlanRainFallList(planId);
            return SystemSecurityMessage.getSuccessMsg("获取方案计算雨量列表成功！", list);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取方案计算雨量失败！");

        }
    }
    /**
     * 根据方案id获取方案基本信息
     *
     * @return
     */
    @RequestMapping(value = "/getPlanInfo/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanInfo(@PathVariable String planId) {
        try {
            YwkPlaninfo ywkPlaninfo = modelCallSkddService.getPlanInfo(planId);
            return SystemSecurityMessage.getSuccessMsg("获取方案成功！", ywkPlaninfo);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取方案失败！");

        }
    }

    /**
     * 根据方案id获取入库流量
     *
     * @return
     */
    @RequestMapping(value = "/getPlanQList/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanQList(@PathVariable String planId) {
        try {
            List<Object> list = modelCallSkddService.getPlanQList(planId);
            return SystemSecurityMessage.getSuccessMsg("获取方案计算流量列表成功！", list);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取方案计算流量失败！");

        }
    }

    /**
     * 根据方案id获取出库流量及水位数据
     *
     * @return
     */
    @RequestMapping(value = "/getPlanResultList/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage getPlanResultList(@PathVariable String planId) {
        try {
            Object list = modelCallSkddService.getPlanResultList(planId);
            return SystemSecurityMessage.getSuccessMsg("获取方案计算结果列表成功！", list);
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("获取方案计算结果失败！");

        }
    }

}

