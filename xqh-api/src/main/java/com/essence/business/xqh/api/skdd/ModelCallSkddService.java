package com.essence.business.xqh.api.skdd;

import com.essence.business.xqh.api.fbc.dto.PlanInfoFbcVo;
import com.essence.business.xqh.api.fbc.dto.YwkFbcPlanInfoBoundaryDto;
import com.essence.business.xqh.api.hsfxtk.dto.ModelParamVo;
import com.essence.business.xqh.api.skdd.dto.Qdata;
import com.essence.business.xqh.api.skdd.dto.RainDataDto;
import com.essence.business.xqh.dao.entity.fbc.FbcHdpHhtdzW;
import com.essence.business.xqh.dao.entity.fhybdd.YwkModel;
import com.essence.business.xqh.dao.entity.fhybdd.YwkPlaninfo;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 水库调度模型业务层接口
 */
public interface ModelCallSkddService {

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
     * 获取水库调度模型列表
     * @return
     */
    List<YwkModel> getModelInfoList();

    /**
     * 获取雨量数据
     * @param modelParamVo
     * @return
     */
    List<Object> getRainDataList(ModelParamVo modelParamVo);

    /**
     * 入库流量模板下载
     * @param planId
     * @return
     */
    Workbook exportSkddQTemplate(String planId);

    /**
     *上传入库流量数据解析-Excel导入
     * @param mutilpartFile
     * @param planId
     * @return
     */
    List<Object> importSkddQData(MultipartFile mutilpartFile, String planId);

    /**
     * 查询流量默认数据
     * @param modelParamVo
     * @return
     */
    List<Object> getSkddQDataList(ModelParamVo modelParamVo);

    /**
     * 保存雨量数据
     * @param rainDataLsit
     * @return
     */
    List<RainDataDto> saveRainDataList(List<RainDataDto> rainDataLsit,String planId);

    /**
     * 调用水库调度模型计算
     * @param planId
     * @return
     */
    List<FbcHdpHhtdzW> skddModelCall(String planId);

    /**
     *
     * @param qDataLsit
     * @param planId
     * @return
     */
    List<Qdata> saveQDataList(List<Qdata> qDataLsit, String planId);

    /**
     * 分页获取水库调度列表
     * @param paginatorParam
     * @return
     */
    Paginator getPlanList(PaginatorParam paginatorParam);

    /**
     * 获取方案计算雨量
     * @param planId
     */
    List<Object> getPlanRainFallList(String planId);

    /**
     * 获取方案计算入库流量数据
     * @param planId
     * @return
     */
    List<Object> getPlanQList(String planId);

    /**
     * 根据方案id获取出库流量及水位数据
     * @param planId
     * @return
     */
    Object getPlanResultList(String planId);

    /**
     * 获取方案基本信息
     * @param planId
     * @return
     */
    YwkPlaninfo getPlanInfo(String planId);
}
