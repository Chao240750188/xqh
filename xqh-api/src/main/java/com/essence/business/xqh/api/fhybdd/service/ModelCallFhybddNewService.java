package com.essence.business.xqh.api.fhybdd.service;

import com.essence.business.xqh.api.fhybdd.dto.CalibrationMSJGAndScsVo;
import com.essence.business.xqh.api.fhybdd.dto.CalibrationXAJVo;
import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRcsBsin;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlanTriggerRcsFlow;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ModelCallFhybddNewService {


    /**
     * 保存方案基本信息
     * @param vo
     * @return
     */
    String savePlan(ModelCallBySWDDVo vo);

    /**
     * 根据方案获取雨量信息
     * @param planId
     * @return
     */
    List<Map<String, Object>> getRainfalls(String planId);


    /**
     * 模型计算，俩个模型一起计算
     * @param planId
     * @return
     */
    Long modelCallHandleData(String planId);

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
     * @param planId
     * @param rcsId
     * @return
     */
    List<Map<String,Object>> getTriggerFlow(String planId, String rcsId);


    /**
     * 下载预报断面流量模板
     * @param planId
     * @return
     */
    Workbook exportTriggerFlowTemplate(String planId);


    /**
     * 上传预报断面流量数据excel
     * @param mutilpartFile
     * @param planId
     * @return
     */
    List<Map<String,Object>> importTriggerFlowData(MultipartFile mutilpartFile, String planId,String rcsId);


    /**
     * 下载雨量模板
     * @param planId
     * @return
     */
    Workbook exportRainfallTemplate(String planId);

    /**
     * 上传监测站雨量数据excel
     * @param mutilpartFile
     * @param planId
     * @return
     */
    List<Map<String, Object>> importRainfallData(MultipartFile mutilpartFile, String planId);

    /**
     * 从缓存里获取获取雨量信息并存库
     * @param planId
     */
    void saveRainfallsFromCacheToDb(String planId);

    /**
     * 获取模型运行状态
     * @param planId
     * @return
     */
    String getModelRunStatus(String planId,Integer tag);

    /**
     * 获取模型运行输出结果
     * @param planId
     * @return
     */
    Object getModelResultQ(String planId,Integer tag);


    /**
     * 获取率定参数交互列表
     * @param planId
     * @return
     */
    Object getCalibrationList(String planId);

    /**
     * 单位线模型参数交互
     * @param mutilpartFile
     * @param planId
     * @return
     */
    List<Map<String, Double>> importCalibrationWithDWX(MultipartFile mutilpartFile, String planId);

    /**
     * 新安江模型参数交互
     * @param mutilpartFile
     * @param planId
     * @return
     */
    List<Map<String, Object>> importCalibrationWithXAJ(MultipartFile mutilpartFile, String planId);


    /**
     * 马思京根模型参数交互
     * @param mutilpartFile
     * @param planId
     * @return
     */
    List<Map<String, Double>> importCalibrationWithMSJG(MultipartFile mutilpartFile, String planId);

    /**
     * 保存率定的单位线信息入库
     * @param planId
     */
    void saveCalibrationDwxToDB(String planId,List<Map<String,Double>> result);

    /**
     * 保存率定的新安江信息入库
     * @param planId
     * @param result
     * @param calibrationXAJVo
     */
    void saveCalibrationXAJToDB(String planId, List<Map<String, Object>> result, CalibrationXAJVo calibrationXAJVo);

    /**
     * 保存马斯京根跟SCS模型参数
     * @param planId
     * @param calibrationMSJGAndScsVo
     */
    void saveCalibrationMSJGOrScSToDB(String planId, CalibrationMSJGAndScsVo calibrationMSJGAndScsVo,Integer tag);


    /**
     * 新安江模型蒸发量
     * @param planId
     * @return
     */
    Workbook exportCalibrationXAJTemplate(String planId);

    Long ModelCallCalibration(String planId);

    /**
     * 获取率定后的结果
     * @param planId
     * @return
     */
    Object getModelResultQCalibration(String planId);
}
