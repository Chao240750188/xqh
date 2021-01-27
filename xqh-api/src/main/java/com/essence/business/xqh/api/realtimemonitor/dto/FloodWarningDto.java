package com.essence.business.xqh.api.realtimemonitor.dto;

import lombok.Data;
import org.apache.poi.ss.formula.functions.T;

import java.io.Serializable;
import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/26 0026 10:36
 */
@Data
public class FloodWarningDto<T> implements Serializable {

    private List<T> surpassHistory;

    private List<T> surpassDesign;

    private List<T> surpassFloodLine;

    private List<T> surpassSafe;

}
