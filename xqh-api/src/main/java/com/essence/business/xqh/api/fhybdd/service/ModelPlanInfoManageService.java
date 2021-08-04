package com.essence.business.xqh.api.fhybdd.service;

import com.essence.business.xqh.api.fhybdd.dto.ModelPlanInfoManageDto;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ModelPlanInfoManageService {

    /**
     * 根据条件查询
     * @param paginatorParam
     * @return
     */
    Paginator getPlanList(PaginatorParam paginatorParam);


    /**
     * 根据方案id删除
     * @param planinfo
     */
    void deleteByPlanId(YwkPlaninfo planinfo);

    /**
     * 获取预报断面列表
     * @param planinfo
     * @return
     */
    List<Map<String, Object>> getTriggerList(YwkPlaninfo planinfo);

    /**
     *发布与撤销发布
     * @param planinfo
     */
    void publishPlan(List<String> planinfo,Integer tag);


    /**
     * 获取预警水位数据
     * @return
     */
    List<Map<String, Object>> getWarnIngWaterLevels(Map map);

    /**
     * 更新预警水位数据
     * @param datas
     */
    void upDateWarnIngWaterLevels(List<Map<String, Object>> datas);

    /**
     * 获取所有断面信息
     * @return
     */
    Object getAllRcsByRiver(String rvcd);

    /**
     * 根据断面获取其水位和流量
     * @param
     * @return
     */
    Object getWaterLevelFlow(ModelPlanInfoManageDto modelPlanInfoManageDto);

    /**
     * Excel导出
     * @throws Exception
     */
    Workbook exportTemplate(ModelPlanInfoManageDto modelPlanInfoManageDto);

    /**
     * Excel导入
     * @param mutilpartFile
     * @return
     */
    Map<String, List<Map<String, String>>> importWaterLevelFlow(MultipartFile mutilpartFile, String modelPlanInfoManageDto);

    /**
     * 经度评定模型计算
     */
    int modelCallJingDu(ModelPlanInfoManageDto modelPlanInfoManageDto);

    /**
     * 获取模型运行结果
     * @return
     */
    Object getModelResultQ(ModelPlanInfoManageDto modelPlanInfoManageDto);

    Object getHistoryJingDuRcs(String planId);

    Object getHistoryJingDuInfo(String planId, String rvcrcrsccd);

    Object getHistoryJingDuResult(String planId, String rvcrcrsccd);

    void deleteHistoryJingDu(String planId, String rvcrcrsccd);
}
