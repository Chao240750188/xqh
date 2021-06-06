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

    Boolean searchPlanIsExits(String planName);

    String savePlan(ModelFhddPlanVo vo);

    YwkPlaninfo getPlanInfoByPlanId(String planId);

    List<Map<String, Object>> getRainfallsInfo(YwkPlaninfo planinfo) throws ParseException;

    void saveRainfallsFromCacheToDb(YwkPlaninfo planinfo, List<Map<String, Object>> results);

    Workbook exportRainfallTemplate(YwkPlaninfo planinfo) throws Exception;

    List<Map<String, Object>> importRainfallData(MultipartFile mutilpartFile, YwkPlaninfo planinfo);

    Boolean savePlanInputZ(ModelFhddInputVo vo);

    void modelCall(YwkPlaninfo planinfo);

    String getModelRunStatus(YwkPlaninfo planInfo);

    Object getModelResultQ(YwkPlaninfo planInfo);
}
