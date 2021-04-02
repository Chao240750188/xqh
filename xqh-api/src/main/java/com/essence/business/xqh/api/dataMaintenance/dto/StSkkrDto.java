package com.essence.business.xqh.api.dataMaintenance.dto;

import com.essence.business.xqh.dao.entity.fhybdd.StZvarlB;
import lombok.Data;

import java.util.List;

/**
 * @author NoBugNoCode
 * @date 2021/4/2 9:41
 * 水库库容曲线
 */
@Data
public class StSkkrDto {

    /**
     * 水库编码
     */
    private String rscd;
    /**
     * 水库名称
     */
    private String rsnm;
    /**
     * 库容曲线值
     */
    List<StZvarlB> dataList;
}
