package com.essence.business.xqh.api.fhybdd.dto;

public class CalibrationMSJGAndScsVo {


    private MSJGAndScSVo msjg1;
    private MSJGAndScSVo msjg2;
    private MSJGAndScSVo msjg3;

    public MSJGAndScSVo getMsjg1() {
        return msjg1;
    }

    public void setMsjg1(MSJGAndScSVo msjg1) {
        this.msjg1 = msjg1;
    }

    public MSJGAndScSVo getMsjg2() {
        return msjg2;
    }

    public void setMsjg2(MSJGAndScSVo msjg2) {
        this.msjg2 = msjg2;
    }

    public MSJGAndScSVo getMsjg3() {
        return msjg3;
    }

    public void setMsjg3(MSJGAndScSVo msjg3) {
        this.msjg3 = msjg3;
    }

    public static class MSJGAndScSVo {

        private String cId;
        private Double msjgK;
        private Double msjgX;
        private Long scsCn;

        public Long getScsCn() {
            return scsCn;
        }

        public void setScsCn(Long scsCn) {
            this.scsCn = scsCn;
        }

        public String getcId() {
            return cId;
        }

        public void setcId(String cId) {
            this.cId = cId;
        }

        public Double getMsjgK() {
            return msjgK;
        }

        public void setMsjgK(Double msjgK) {
            this.msjgK = msjgK;
        }

        public Double getMsjgX() {
            return msjgX;
        }

        public void setMsjgX(Double msjgX) {
            this.msjgX = msjgX;
        }
    }




}
