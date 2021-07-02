package com.essence.business.xqh.api.hsfxtk;

import com.essence.business.xqh.api.fhybdd.dto.ModelCallBySWDDVo;
import com.essence.business.xqh.api.hsfxtk.dto.BreakVo;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.hsfxtk.dto.YwkBreakBasicDto;
import com.essence.business.xqh.api.hsfxtk.dto.YwkPlanInfoBoundaryDto;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.business.xqh.dao.entity.hsfxtk.YwkModelRoughnessParam;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 工程联合调度相关业务层
 */
public interface ProjectJointDispatchService {

    /**
     * 获取方案信息
     * @param planId
     * @return
     */
    YwkPlaninfo getPlanInfoByPlanId(String planId);
    /**
     * 保存联合调度方案基本信息
     * @param vo
     * @return
     */
    String savePlan(ModelCallBySWDDVo vo);

    /**
     * 根据联合调度方案获取雨量信息
     * @param planInfo
     * @return
     */
    List<Map<String, Object>> getRainfalls(YwkPlaninfo planInfo) throws Exception;


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
     * 下载雨量模板
     * @param planinfo
     * @return
     */
    Workbook exportRainfallTemplate(YwkPlaninfo planinfo)throws Exception;

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
     * 方案结果保存
     * @param planInfo
     * @return
     */
    void saveModelData(YwkPlaninfo planInfo);

    /**
     * 根据方案名称校验方案是否存在
     * @param planName
     */
    Integer getPlanInfoByName(String planName);

    /**
     * 查询水文预报模型结束后匹配水动力模型边界条件数据
     * @param modelParamVo
     * @return
     */
    Object getSwModelBoundaryBasicData(ModelParamVo modelParamVo);

    /**
     * 保存水文模型边界数据
     * @param boundaryDtoList
     * @param planId
     * @return
     */
    List<YwkPlanInfoBoundaryDto> saveSwModelBoundaryBasicData(List<YwkPlanInfoBoundaryDto> boundaryDtoList, String planId);

    /**
     * 防洪保护区设置获取模型列表
     * @return
     */
    List<Object> getHsfxModelList();

    /**
     * 根据模型获取河道糙率设置参数
     * @param modelId
     * @return
     */
    List<Object> getModelRiverRoughness(String modelId);

    /**
     * 保存模型参数设置
     * @param ywkModelRoughnessParam
     * @return
     */
    ModelParamVo saveModelRiverRoughness(YwkModelRoughnessParam ywkModelRoughnessParam, String nPlanid, String modelId);

    /**
     * 查询方案边界条件列表数据
     * @param modelParamVo
     * @return
     */
    List<Object> getModelBoundaryBasic(ModelParamVo modelParamVo);

    /**
     * 下载边界数据模板
     * @param planId
     * @param modelId
     * @return
     */
    Workbook exportDutyTemplate(String planId,String modelId);

    /**
     * 上传水动力界条件数据解析-Excel导入
     * @param mutilpartFile
     * @return
     */
    List<Object> importBoundaryData(MultipartFile mutilpartFile,String planId,String modelId) throws IOException;

    /**
     * 方案计算边界条件值-提交入库
     * @param ywkPlanInfoBoundaryDtoList
     * @return
     */
    List<YwkPlanInfoBoundaryDto> savePlanBoundaryData(List<YwkPlanInfoBoundaryDto> ywkPlanInfoBoundaryDtoList,String planId);


    /**
     * 根据模型id获取溃口列表
     * @param modelId
     * @return
     */
    List<YwkBreakBasicDto> getBreakList(String modelId);


    /**
     * 保存溃口
     * @param breakDto
     */
    BreakVo savePlanBreak(BreakVo breakDto);


    /**
     * 方案计算
     * @param planId
     */

    void modelCallHsfx(String planId);

    /**
     * 根据方案id获取洪水风险调控模型的计算进度
     * @param planId
     * @return
     */
    Object getHsfxModelRunStatus(String planId);

    /**
     * 模型输出淹没历程-及最大水深图片列表
     * @param planId
     * @return
     */
    Object getModelProcessPicList(String planId);

    /**
     * 预览图片
     * @param request
     * @param response
     * @param planId
     * @param picId
     */
    void previewPicFile(HttpServletRequest request, HttpServletResponse response, String planId, String picId);



    //工程联合调度方案管理接口
    /**
     * 获取方案列表信息
     * @return
     */
    Paginator getPlanList(PaginatorParam paginatorParam);


    /**
     * 根据方案id获取边界信息
     * @param planId
     * @return
     */
    List<Map> getAllBoundaryByPlanId(String planId);


    /**
     * 根据方案id获取防洪保护区信息
     * @param planId
     * @return
     */
    Map<String, Object> getAllRoughnessByPlanId(String planId);

    /**
     * 根据方案id获取溃口列表
     * @param planId
     * @return
     */
    Map<String, Object> getAllBreakByPlanId(String planId);


    /**
     * 删除方案以及方案下关联点所有入参
     * @param planId
     * @return
     */
    void deleteAllInputByPlanId(String planId);
}
