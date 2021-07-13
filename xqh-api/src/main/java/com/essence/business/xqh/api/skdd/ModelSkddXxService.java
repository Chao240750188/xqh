package com.essence.business.xqh.api.skdd;


import com.essence.business.xqh.api.skdd.vo.ModelSkddXxInputVo;
import com.essence.business.xqh.api.skdd.vo.ModelSkddXxPlanVo;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ModelSkddXxService {

    /**
     * 获取集水区模型和河段模型列表
     * @return
     */
    Map<String, Object> getModelList();

    /**
     * 查询方案名称是否已存在
     * @param planName
     * @return
     */
    Boolean searchPlanIsExits(String planName);

    /**
     * 保存方案基本信息
     * @param vo
     * @return
     */
    String savePlan(ModelSkddXxPlanVo vo);

    /**
     * 获取方案信息
     * @param planId
     * @return
     */
    YwkPlaninfo getPlanInfoByPlanId(String planId);

    /**
     * 根据方案获取雨量信息
     * @param planinfo
     * @return
     */
    List<Map<String, Object>> getRainfallsInfo(YwkPlaninfo planinfo) throws ParseException;

    /**
     * 从缓存里获取获取雨量信息并存库
     * @param planinfo
     */
    void saveRainfallsFromCacheToDb(YwkPlaninfo planinfo, List<Map<String, Object>> results);

    /**
     * 下载雨量信息模板
     * @param planinfo
     * @return
     */
    Workbook exportRainfallTemplate(YwkPlaninfo planinfo) throws ParseException;

    /**
     * 上传雨量信息数据excel
     * @param mutilpartFile
     * @param planinfo
     * @return
     */
    List<Map<String, Object>> importRainfallData(MultipartFile mutilpartFile, YwkPlaninfo planinfo);

    /**
     * 水库调度初始水位和下泄流量保存
     */
    Boolean savePlanInputZ(ModelSkddXxInputVo vo);

    /**
     * 水库调度-汛限Pcp模型
     * @param planinfo
     * @return
     */
    void modelPcpCall(YwkPlaninfo planinfo);

    /**
     * 水库调度-汛限水文模型
     * @param planinfo
     */
    void modelHydrologyCall(YwkPlaninfo planinfo);

    /**
     * 水库调度汛限模型计算
     * @return
     */
    void modelCall(YwkPlaninfo planinfo);

    /**
     * 获取模型运行状态
     * @return
     */
    Object getModelRunStatus(YwkPlaninfo planInfo);

    /**
     * 获取模型运行输出结果
     * @return
     */
    Object getModelResultQ(YwkPlaninfo planInfo);



}
