package com.essence.business.xqh.web.floodScheduling;

import com.essence.business.xqh.api.floodScheduling.dto.*;
import com.essence.business.xqh.api.floodScheduling.service.*;
import com.essence.business.xqh.api.tuoying.TuoyingInfoService;
import com.essence.business.xqh.api.tuoying.dto.TuoyingStRsvrRDto;
import com.essence.business.xqh.common.returnFormat.SystemSecurityMessage;
import com.essence.framework.util.StrUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 调度管理Controller
 * @company Essence
 * @author LiuGt
 * @version 1.0 2020/03/30
 */
@RestController
@RequestMapping("/scheduling")
public class SchedulingController {

    @Autowired
    SchedulingPlanDocumentService schedulingPlanDocumentService;  //调度方案文档服务
    @Autowired
    SchedulingPlanInfoService schedulingPlanInfoService;    //调度方案服务
    @Autowired
    HifFeglatFService hifFeglatFService; //调度预报成果服务
    @Autowired
    HifZvarlBService hifZvarlBService; //出库规则服务
    @Autowired
    StZvarlBService stZvarlBService;   //库容曲线服务
    @Autowired
    TuoyingInfoService tuoyingInfoService;  //水库水情表
    @Autowired
    ObjResService objResService; //水库信息服务
    @Autowired
    private StRsvrRService stRsvrRService;

    //region 调度管理模块接口

    /**
     * 分页获取调度方案文档数据
     *
     * @param param 条件过滤
     * @return SystemMessage
     * @see SystemSecurityMessage 其中result属性值为分页格式的的数据列表
     * @see com.essence.framework.jpa.Paginator 分页格式对象
     * @throws Exception
     */
    @RequestMapping(value = "/querySchedulingDocListPage", method = RequestMethod.POST)
    public @ResponseBody
    SystemSecurityMessage querySchedulingDocListPage(@RequestBody SchedulingPlanDocPageListParamDto param) throws Exception {
        try {
            return new SystemSecurityMessage("ok", "分页查询调度方案成功！", schedulingPlanDocumentService.getSchedulingPlanDocListPage(param));
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("分页查询调度方案失败！");
        }

    }

    /**
     * 添加一个调度方案信息
     * @param schedulingPlanDocumentDto
     * @return
     */
    @RequestMapping(value = "/addSchedulingPlan", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage addSchedulingPlan(@RequestBody SkddSchedulingPlanDocumentDto schedulingPlanDocumentDto){
        try {
            return new SystemSecurityMessage("ok", "添加调度方案成功！", schedulingPlanDocumentService.addSchedulingPlan(schedulingPlanDocumentDto));
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("添加调度方案失败！");
        }

    }

    /**
     * 根据ID查询一个调度方案信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/getSchedulingDocById/{id}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage getSchedulingDocById(@PathVariable String id){
        try {
            return new SystemSecurityMessage("ok","查询成功！", schedulingPlanDocumentService.querySchedulingPlanById(id));
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询失败！");
        }

    }

    /**
     * 编辑一个调度方案信息
     * @param schedulingPlanDocumentDto
     * @return
     */
    @RequestMapping(value = "/editSchedulingPlan", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage editSchedulingPlan(@RequestBody SkddSchedulingPlanDocumentDto schedulingPlanDocumentDto){
        try {
            return new SystemSecurityMessage("ok", "编辑调度方案成功！", schedulingPlanDocumentService.editSchedulingPlan(schedulingPlanDocumentDto));
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("编辑调度方案失败！");
        }

    }

    /**
     * 根据ID删除一个调度方案信息
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteSchedulingPlan/{id}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage deleteSchedulingPlan(@PathVariable String id) {
        try {
            boolean b = schedulingPlanDocumentService.deleteSchedulingPlanById(id);
            if (b){
                return SystemSecurityMessage.getSuccessMsg("删除调度方案成功！", null);
            }
            else{
                return SystemSecurityMessage.getFailMsg("删除调度方案失败！", null);
            }
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除调度方案失败！", null);
        }

    }

    /**
     * 上传调度方案文档文件（支持多文件上传）
     * @param groupId
     * @param files
     * @return
     * @throws Exception
     */
    @ResponseBody
    @PostMapping(value = "/uploadSchedulingPlanDoc")
    public SystemSecurityMessage uploadSchedulingPlanDoc(
            @RequestParam("files") MultipartFile[] files, @RequestParam String groupId) throws Exception {
        List<String> list = null;
        SystemSecurityMessage sm = new SystemSecurityMessage();
        if(null == files || files.length <= 0){
            sm.setCode("error");
            sm.setInfo("没有发现上传的文件！");
            sm.setResult(null);
            return sm;
        }
        //上传
        list=schedulingPlanDocumentService.uploadSchedulingPlanFile(groupId, files[0]);
        if(list.size() > 0){
            sm.setCode("ok");
            sm.setInfo("上传完成！");
            sm.setResult(list);
        }else{
            sm.setCode("error");
            sm.setInfo("上传失败！");
        }
        files = null;
        //response.setContentType("text/html;charset=UTF-8");
        return sm;
    }

    /**
     * 根据ID删除一个调度方案的附件文件
     * @param id
     * @return
     */
    @RequestMapping(value = "/deleteSchedulingPlanDoc/{id}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage deleteSchedulingPlanDoc(@PathVariable String id) {
        try {
            boolean b = schedulingPlanDocumentService.deleteSchedulingPlanDocById(id);
            if (b){
                return SystemSecurityMessage.getSuccessMsg("删除成功！", null);
            }
            else{
                return SystemSecurityMessage.getFailMsg("删除失败！", null);
            }
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("删除失败！", null);
        }

    }

    /**
     * 预览预案管理处理附件pdf
     * @param id 调度方案ID
     */
    @RequestMapping(value = "/previewSchedulingPlanDoc/{id}", method = RequestMethod.GET)
    public void previewSchedulingPlanDoc(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        try {
            schedulingPlanDocumentService.previewSchedulingPlanDoc(request,response,id);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //endregion

    //region 历史调度模块接口

    /**
     * 分页获取历史调度数据
     *
     * @param param 条件过滤
     * @return SystemMessage
     * @see SystemSecurityMessage 其中result属性值为分页格式的的数据列表
     * @see com.essence.framework.jpa.Paginator 分页格式对象
     * @throws Exception
     */
    @RequestMapping(value = "/querySchedulingHistoryListPage", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage querySchedulingHistoryListPage(@RequestBody SchedulingPlanPageListParamDto param) throws Exception {
        try {
            return new SystemSecurityMessage("ok", "分页查询调度方案成功！", schedulingPlanInfoService.getSchedulingPlanListPage(param));
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("分页查询调度方案失败！");
        }


    }

    /**
     * 根据方案ID查询调度预报成果
     */
    @RequestMapping(value = "/querySchedulingResultByPlanId/{planId}", method = RequestMethod.GET)
    public SystemSecurityMessage querySchedulingResultByPlanId(@PathVariable String planId) {
        try {
            return new SystemSecurityMessage("ok", "查询成功！", hifFeglatFService.queryListByPlanId(planId));
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询失败！");
        }
    }

    //endregion

    //region 结果展示模块接口

    /**
     * 分页获取未保存调度数据
     *
     * @param param 条件过滤
     * @return SystemMessage
     * @see SystemSecurityMessage 其中result属性值为分页格式的的数据列表
     * @see com.essence.framework.jpa.Paginator 分页格式对象
     * @throws Exception
     */
    @RequestMapping(value = "/queryNotSaveSchedulingListPage", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage queryNotSaveSchedulingListPage(@RequestBody SchedulingPlanPageListParamDto param) throws Exception {
        try {
            return new SystemSecurityMessage("ok", "分页未保存调度数据成功！", schedulingPlanInfoService.getSchedulingPlanListPage(param));
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("分页未保存调度数据失败！");
        }


    }

    /**
     * 保存调度方案的结果信息
     */
    @RequestMapping(value = "/saveSchedulingResult/{planId}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage saveSchedulingResult(@PathVariable String planId){
        try {
            int i = schedulingPlanInfoService.saveSchedulingResult(planId);
            if (i > 0){
                return new SystemSecurityMessage("ok", "保存成功！");
            }
            else{
                return SystemSecurityMessage.getFailMsg("保存失败！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存失败！");
        }

    }

    /**
     * 获取一次调度结果的统计数据
     */
    @RequestMapping(value = "/getSchedulingResultStatistics/{planId}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage getSchedulingResultStatistics(@PathVariable String planId){
        try {
            return new SystemSecurityMessage("ok", "查询成功！", hifFeglatFService.querySchedulingResultStatistics(planId));
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询失败！");
        }
    }

    /**
     * 获取一次调度过程的降雨数据
     */
    @RequestMapping(value = "/getRainfallForOnecScheduling/{planId}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage getRainfallForOnecScheduling(@PathVariable String planId){
        try {
            return new SystemSecurityMessage("ok", "查询成功！", hifFeglatFService.getRainfallForOnecScheduling(planId));
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询失败！");
        }


    }

    //endregion

    //region 水库调度模块接口

    /**
     * 根据水库ID查询出库规则
     */
    @RequestMapping(value = "/queryZvarlByResCode/{resCode}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage queryZvarlByResCode(@PathVariable String resCode) {
        try {
            return new SystemSecurityMessage("ok", "查询成功!", hifZvarlBService.queryListByResCode(resCode));
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询失败！");
        }
    }


    /**
     * 根据水库ID查询库容曲线
     */
    @RequestMapping(value = "/queryStZvarlByResCode/{resCode}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage queryStZvarlByResCode(@PathVariable String resCode){
        try {
            return new SystemSecurityMessage("ok","查询成功!",stZvarlBService.queryListByResCode(resCode));
        } catch (Exception e) {
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("查询失败！");
        }


    }

    /**
     * 根据水库ID预览预案管理附件pdf
     * @param resCode 水库ID
     */
    @RequestMapping(value = "/previewSchedulingPlanDocByResCode/{resCode}", method = RequestMethod.GET)
    public void previewSchedulingPlanDocByResCode(@PathVariable String resCode, HttpServletRequest request, HttpServletResponse response) {
        try {
            schedulingPlanDocumentService.previewSchedulingPlanDocByResCode(request,response,resCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据水库ID查询起调水位
     */
    @RequestMapping(value = "/getWlByResCode/{resCode}", method = RequestMethod.GET)
    public @ResponseBody SystemSecurityMessage getWlByResCode(@PathVariable String resCode){
       try {
           //测试使用，实际需要从慧图提供的水库实测水位数据中读取最新的一次实测水位数据
           Map<String,Object> map = new HashMap<>();
           //实测数据
           SkddObjResDto skddObjResDto = objResService.queryByResCode(resCode);
           String stcd = "";
           if (skddObjResDto != null && !StrUtil.isEmpty(skddObjResDto.getHtGuid())) {
               stcd = skddObjResDto.getHtGuid();
           }
           TuoyingStRsvrRDto tuoyingStRsvrRDto = stRsvrRService.queryLastOneByStcd(stcd);//TuoyingStRsvrRDto tuoyingStRsvrRDto = tuoyingInfoService.getStcdLastInfo(stcd);
           if (tuoyingStRsvrRDto != null){
               map.put("wl", tuoyingStRsvrRDto.getRz().doubleValue());
               SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:00");
               String tm = dateFormat.format(tuoyingStRsvrRDto.getTm());
               map.put("time", tm);
           }
           else{
               switch (resCode){
                   case "RES_0011":
                       map.put("wl",126.8); //126.8
                       break;
                   case "RES_0013":
                       map.put("wl",205.0);
                       break;
                   case "RES_0014":
                       map.put("wl",193.0);
                       break;
               }
               String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00"));
               map.put("time", time);
           }
           return new SystemSecurityMessage("ok","查询成功!",map);
       }catch (Exception e){
           e.printStackTrace();
           return new SystemSecurityMessage("error","查询失败!");
       }

    }

    /**
     * 保存水库调度信息
     * @param saveSchedulingPlanParamDto 水库调度信息实体实例
     * @return
     */
    @RequestMapping(value = "/saveSchedulingPlan", method = RequestMethod.POST)
    public @ResponseBody SystemSecurityMessage saveSchedulingPlan(@RequestBody SaveSchedulingPlanParamDto saveSchedulingPlanParamDto){
        try {
            int count = schedulingPlanInfoService.queryInProgresCountByResCode(saveSchedulingPlanParamDto.getResCode());
            if (count > 0){
                return SystemSecurityMessage.getFailMsg("此水库已有一个调度方案正在进行中，保存失败！", null);
            }
            SkddSchedulingPlanInfoDto skddSchedulingPlanInfoDto = schedulingPlanInfoService.saveSchedulingPlan(saveSchedulingPlanParamDto);
            if (skddSchedulingPlanInfoDto == null){
                return SystemSecurityMessage.getFailMsg("保存水库调度数据失败！", null);
            }
            else{
                return new SystemSecurityMessage("ok","保存水库调度数据成功！", skddSchedulingPlanInfoDto);
            }
        }catch (Exception e){
            e.printStackTrace();
            return SystemSecurityMessage.getFailMsg("保存水库调度数据失败！", null);
        }

    }

    //endregion
}
