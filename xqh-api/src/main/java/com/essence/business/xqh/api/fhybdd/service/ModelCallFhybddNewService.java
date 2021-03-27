package com.essence.business.xqh.api.fhybdd.service;

import com.essence.business.xqh.api.fhybdd.dto.CalibrationMSJGAndScsVo;
import com.essence.business.xqh.api.fhybdd.dto.CalibrationXAJVo;
import com.essence.business.xqh.api.fhybdd.dto.CalibrationXGGXVo;
import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRcsBsin;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanTriggerRcsFlow;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ModelCallFhybddNewService {

    /**
     * 获取方案信息
     * @param planId
     * @return
     */
     YwkPlaninfo getPlanInfoByPlanId(String planId);
    /**
     * 保存方案基本信息
     * @param vo
     * @return
     */
    String savePlan(ModelCallBySWDDVo vo);

    /**
     * 根据方案获取雨量信息
     * @param planInfo
     * @return
     */
    List<Map<String, Object>> getRainfalls(YwkPlaninfo planInfo);


    /**
     * 模型计算，俩个模型一起计算
     * @param ywkPlaninfo
     * @return
     */
    void modelCall(YwkPlaninfo ywkPlaninfo);

    /**
     * 集水区模型选择跟河段模型选择
     * @return
     */
    Map<String, Object> getModelList();


    /**
     * 获取断面列表
     * @return
     */
    List<WrpRcsBsin> getRcsList();

    /**
     * 根据方案id获取预报断面流量
     * @param planinfo
     * @param rcsId
     * @return
     */
    List<Map<String,Object>> getTriggerFlow(YwkPlaninfo planinfo, String rcsId);


    /**
     * 下载预报断面流量模板
     * @param planinfo
     * @return
     */
    Workbook exportTriggerFlowTemplate(YwkPlaninfo planinfo);


    /**
     * 上传预报断面流量数据excel
     * @param mutilpartFile
     * @param planinfo
     * @return
     */
    List<Map<String,Object>> importTriggerFlowData(MultipartFile mutilpartFile, YwkPlaninfo planinfo,String rcsId);


    /**
     * 下载雨量模板
     * @param planinfo
     * @return
     */
    Workbook exportRainfallTemplate(YwkPlaninfo planinfo);

    /**
     * 上传监测站雨量数据excel
     * @param mutilpartFile
     * @param planinfo
     * @return
     */
    List<Map<String, Object>> importRainfallData(MultipartFile mutilpartFile, YwkPlaninfo planinfo);

    /**
     * 从缓存里获取获取雨量信息并存库
     * @param planinfo
     */
    void saveRainfallsFromCacheToDb(YwkPlaninfo planinfo,List<Map<String,Object>> results);

    /**
     * 获取模型运行状态
     * @param ywkPlaninfo
     * @return
     */
    String getModelRunStatus(YwkPlaninfo ywkPlaninfo,Integer tag);

    /**
     * 获取模型运行输出结果
     * @param ywkPlaninfo
     * @return
     */
    Object getModelResultQ(YwkPlaninfo ywkPlaninfo,Integer tag);


    /**
     * 获取率定参数交互列表
     * @param ywkPlaninfo
     * @return
     */
    Object getCalibrationList(YwkPlaninfo ywkPlaninfo);

    /**
     * 单位线模型参数交互
     * @param mutilpartFile
     * @param planinfo
     * @return
     */
    List<Map<String, Double>> importCalibrationWithDWX(MultipartFile mutilpartFile, YwkPlaninfo planinfo);




    /**
     * 保存率定的单位线信息入库
     * @param planinfo
     */
    void saveCalibrationDwxToDB(YwkPlaninfo planinfo,List<Map<String,Double>> result);

    /**
     * 保存率定的新安江信息入库
     * @param planinfo
     * @param calibrationXAJVo
     */
    void saveCalibrationXAJToDB(YwkPlaninfo planinfo,  List<CalibrationXAJVo> calibrationXAJVo);

    /**
     * 保存相关关系
     * @param planInfo
     * @param calibrationXGGXVo
     */
    void saveCalibrationXGGXToDB(YwkPlaninfo planInfo, List<CalibrationXGGXVo> calibrationXGGXVo);

    /**
     * 保存马斯京根跟SCS模型参数
     * @param planinfo
     * @param calibrationMSJGAndScsVo
     */
    void saveCalibrationMSJGOrScSToDB(YwkPlaninfo planinfo, CalibrationMSJGAndScsVo calibrationMSJGAndScsVo,Integer tag);




    void ModelCallCalibration(YwkPlaninfo planInfo);

    /**
     * 获取率定后的结果
     * @param planinfo
     * @return
     */
    Object getModelResultQCalibration(YwkPlaninfo planinfo);

    /**
     * 方案结果保存
     * @param planInfo
     * @return
     */
    void saveModelData(YwkPlaninfo planInfo);

    /**
     * 修改撤销，修改保存
     * @param planInfo
     * @param tag
     */
    int saveOrDeleteResultCsv(YwkPlaninfo planInfo, Integer tag);



}
