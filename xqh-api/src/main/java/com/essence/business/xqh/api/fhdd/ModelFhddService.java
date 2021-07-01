package com.essence.business.xqh.api.fhdd;

import com.essence.business.xqh.api.fhdd.vo.ModelFhddInputVo;
import com.essence.business.xqh.api.fhdd.vo.ModelFhddPlanVo;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ModelFhddService {

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
     * 方案计划入库
     * @param vo
     * @return
     */
    String savePlan(ModelFhddPlanVo vo);

    /**
     * 根据方案id查询方案信息
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
     * 从缓存里获取雨量信息并存库
     * @param planinfo
     * @return
     */
    void saveRainfallsFromCacheToDb(YwkPlaninfo planinfo, List<Map<String, Object>> results);

    /**
     * Excel导出
     *
     * @param planinfo
     * @throws Exception
     */
    Workbook exportRainfallTemplate(YwkPlaninfo planinfo) throws Exception;

    /**
     * Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    List<Map<String, Object>> importRainfallData(MultipartFile mutilpartFile, YwkPlaninfo planinfo);

    /**
     * 防洪调度初始水位和下泄流量保存
     */
    Boolean savePlanInputZ(ModelFhddInputVo vo);

    /**
     * 水库调度-防洪Pcp模型
     * @param planinfo
     * @return
     */
    void modelPcpCall(YwkPlaninfo planinfo);

    /**
     * 水库调度-防洪水文模型
     * @param planinfo
     * @return
     */
    Boolean modelHydrologyCall(YwkPlaninfo planinfo);

    /**
     * 水库调度防洪模型计算
     * @return
     */
    void modelCall(YwkPlaninfo planinfo);

    /**
     * 获取模型运行状态
     * @return
     */
    String getModelRunStatus(YwkPlaninfo planInfo);

    /**
     * 获取模型运行输出结果
     * @return
     */
    Object getModelResultQ(YwkPlaninfo planInfo);
}
