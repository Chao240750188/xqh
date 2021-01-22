package com.essence.business.xqh.api.rainfall;

/**
 * @author fengpp
 * 2021/1/21 18:46
 */
public enum STTPEnum {
    MM("MM", "气象站"),
    PP("PP", "雨量站"),
    BB("BB", "蒸发站"),
    ZQ("ZQ", "河道水文站"),
    DD("DD", "堰闸水文站"),
    ZZ("ZZ", "河道水位站"),
    TT("TT", "潮位站"),
    RR("RR", "水库水文站"),
    DP("DP", "泵站"),
    ZG("ZG", "地下水站"),
    SS("SS", "墒情站"),
    ZB("ZB", "分洪水位站");
    private String sttp;
    private String desc;

    STTPEnum(String sttp, String desc) {
        this.sttp = sttp;
        this.desc = desc;
    }

    public String getSttp() {
        return sttp;
    }

    public void setSttp(String sttp) {
        this.sttp = sttp;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static String getDesc(String sttp) {
        STTPEnum[] values = values();
        for (STTPEnum sttpEnum : values) {
            if (sttp.equals(sttpEnum.sttp)) {
                return sttpEnum.desc;
            }
        }
        return null;
    }
}
