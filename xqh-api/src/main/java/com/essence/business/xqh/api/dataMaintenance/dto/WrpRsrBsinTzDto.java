package com.essence.business.xqh.api.dataMaintenance.dto;

import com.essence.business.xqh.dao.entity.fhybdd.StRsvrfcchB;
import com.essence.business.xqh.dao.entity.fhybdd.StRsvrfsrB;

import java.io.Serializable;

public class WrpRsrBsinTzDto implements Serializable {
    private StRsvrfcchB stRsvrfcchB;

    private StRsvrfsrB stRsvrfsrB;

    public StRsvrfcchB getStRsvrfcchB() {
        return stRsvrfcchB;
    }

    public void setStRsvrfcchB(StRsvrfcchB stRsvrfcchB) {
        this.stRsvrfcchB = stRsvrfcchB;
    }

    public StRsvrfsrB getStRsvrfsrB() {
        return stRsvrfsrB;
    }

    public void setStRsvrfsrB(StRsvrfsrB stRsvrfsrB) {
        this.stRsvrfsrB = stRsvrfsrB;
    }
}
