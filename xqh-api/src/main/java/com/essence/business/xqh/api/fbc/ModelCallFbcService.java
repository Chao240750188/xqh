package com.essence.business.xqh.api.fbc;

import com.essence.business.xqh.api.fbc.dto.PlanInfoFbcVo;
import com.essence.business.xqh.api.fbc.dto.YwkFbcPlanInfoBoundaryDto;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.dao.entity.fbc.FbcHdpHhtdzW;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 风暴潮模型业务层接口
 */
public interface ModelCallFbcService {

    /**
     * 根据方案名称查询方案是否存在
     * @param planName
     * @return
     */
    Integer getPlanInfoByName(String planName);

    /**
     * 保存创建方案基本信息入库
     * @param vo
     * @return
     */
    String savePlanToDb(PlanInfoFbcVo vo);

    /**
     * 下载边界条件（水位，流量）数据模板
     *
     * @throws Exception
     */
    Workbook exportBoundaryZqTemplate(String planId);

    /**
     * 上传界条件(水位/流量)数据解析-Excel导入
     *
     * @return SystemSecurityMessage 返回结果json
     */
    List<Object> importBoundaryZq(MultipartFile mutilpartFile, String planId);

    /**
     * 查询边界条件（水位/流量）初始数据列表
     * @return
     */
    List<Object> getBoundaryZqBasic(ModelParamVo modelParamVo);

    /**
     * 方案计算边界条件值(水位/流量)-保存提交入库
     * @return
     */
    List<YwkFbcPlanInfoBoundaryDto> saveimportBoundaryZq(List<YwkFbcPlanInfoBoundaryDto> ywkPlanInfoBoundaryDtoList, String planId);

    /**
     * 调用风暴潮模型计算
     * @param planId
     * @return
     */
    void fbcModelCall(String planId);

    /**
     * 获取模型运行输出结果(预报潮位数据)
     * @return
     */
    Object getModelResultCsv(String planId);

    /**
     * 获取模型结果文件（预报潮位数据）
     * @param planId
     * @return
     */
    Object getModelResultTdz(String planId);

    /**
     * 获取方案列表
     * @return
     */
    Paginator getPlanList(PaginatorParam paginatorParam);

    /**
     * 根据方案id获取方案详细信息
     * @param planId
     * @return
     */
    Object getPlanInfoByPlanId(String planId);


    /**
     *  * 根据方案id获取水位/流量数据显示
     *    * @param planId
     * @return
     */
    Object getBoundaryZqByPlanId(String planId);


    /**
     * 根据方案id删除所有信息
     * @param planId
     */
    void deleteAllInputByPlanId(String planId);

    Object getModelRunStatus(String planId);
}
