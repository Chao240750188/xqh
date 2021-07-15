package com.essence.business.xqh.api.csnl;

import com.essence.business.xqh.api.csnl.vo.PlanInfoCsnlVo;
import com.essence.business.xqh.api.hsfxtk.dto.BreakVo;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.hsfxtk.dto.YwkBreakBasicDto;
import com.essence.business.xqh.api.hsfxtk.dto.YwkPlanInfoBoundaryDto;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface ModelCsnlService {

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
    String savePlanToDb(PlanInfoCsnlVo vo);

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
     * 下载监测站雨量数据模板
     * @param planinfo
     */
    Workbook exportRainfallTemplate(YwkPlaninfo planinfo)throws ParseException;

    /**
     * 雨量数据导入
     * @param mutilpartFile
     * @param planinfo
     * @return
     */
    List<Map<String, Object>> importRainfallData(MultipartFile mutilpartFile, YwkPlaninfo planinfo);

    /**
     * 模型计算
     * @return
     */
    void callMode(String planId);

    /**
     * 获取模型运行状态
     * @param planInfo
     * @return
     */
    Object getModelRunStatus(YwkPlaninfo planInfo);

    Object getModelProcessPicList(String planId);

    void previewFloodPic(HttpServletRequest request, HttpServletResponse response, String planId, String picId);
}
