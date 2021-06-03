package com.essence.business.xqh.api.hsfxtk;

import com.essence.business.xqh.api.hsfxtk.dto.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 洪水风险调控模型相关业务层
 */
public interface ModelCallHsfxtkService {
    /**
     * 方案计创建入库
     *
     * @param vo
     * @return
     */
    String savePlanToDb(PlanInfoHsfxtkVo vo);

    /**
     * 保存网格执行过程入库
     * @param planId
     * @return
     */
    Integer saveGridProcessToDb(String planId);

    void test(String planId) throws Exception;

    /**
     * 防洪保护区设置获取模型列表
     * @return
     */
    List<Object> getModelList();

    /**
     * 根据模型获取河道糙率设置参数
     * @param modelId
     * @return
     */
    List<Object> getModelRiverRoughness(String modelId);

    /**
     * 保存模型参数设置
     * @param modelParamVo
     * @return
     */
    ModelParamVo saveModelRiverRoughness(ModelParamVo modelParamVo);

    /**
     * 查询方案边界条件列表数据
     * @param modelParamVo
     * @return
     */
    List<Object> getModelBoundaryBasic(ModelParamVo modelParamVo);

    /**
     * 根据方案名称校验方案是否存在
     * @param planName
     */
    Integer getPlanInfoByName(String planName);

    /**
     * 下载边界数据模板
     * @param planId
     * @param modelId
     * @return
     */
    Workbook exportDutyTemplate(String planId,String modelId);

    /**
     * 上传界条件数据解析-Excel导入
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

    void callMode(String planId);

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

    void testMakePic(String planId, String modelId);
}
