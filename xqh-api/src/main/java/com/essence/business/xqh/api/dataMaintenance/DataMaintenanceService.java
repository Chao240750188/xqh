package com.essence.business.xqh.api.dataMaintenance;

import com.essence.business.xqh.api.dataMaintenance.dto.WrpRsrBsinTzDto;
import com.essence.business.xqh.dao.entity.fhybdd.StStbprpB;
import com.essence.business.xqh.dao.entity.fhybdd.WrpRsrBsin;
import com.essence.framework.jpa.Paginator;
import com.essence.framework.jpa.PaginatorParam;

/**
 * 数据维护系统-接口层
 */
public interface DataMaintenanceService {

    /**
     * 分页获取雨量站或水位站列表
     * @param paginatorParam
     * @return
     */
    Paginator getStbprpbList(PaginatorParam paginatorParam);

    /**
     * 新增或修改测站信息
     * @param stStbprpB
     * @return
     */
    StStbprpB saveOrUpdataStbprpb(StStbprpB stStbprpB);

    /**
     * 删除测站对象信息
     * @param stcd
     */
    void deleteStbprpb(String stcd);

    /**
     * 分页获取水库列表
     * @param paginatorParam
     * @return
     */
    Paginator getWrpRsrBsinList(PaginatorParam paginatorParam);

    /**
     * 新增或编辑水库信息
     * @param wrpRsrBsin
     * @return
     */
    WrpRsrBsin saveOrWrpRsrBsin(WrpRsrBsin wrpRsrBsin);

    /**
     * 删除水库对象信息
     * @param stcd
     */
    void deleteWrpRsrBsin(String stcd);

    /**
     * 根据水库编码查询水库特征值
     * @param stcd
     */
    WrpRsrBsinTzDto getWrpRsrBsinTz(String stcd);

    /**
     * 编辑水库特征值
     * @param wrpRsrBsinTzDto
     * @return
     */
    WrpRsrBsinTzDto saveWrpRsrBsinTz(WrpRsrBsinTzDto wrpRsrBsinTzDto);
}
