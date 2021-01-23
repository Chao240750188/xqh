package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class ClimateBean {

    /**
     * time : 1981年-2010年
     * month : [{"month":1,"maxTemp":4.1,"minTemp":-4.1,"precipitation":5.8},{"month":2,"maxTemp":8.3,"minTemp":-0.7,"precipitation":8.7},{"month":3,"maxTemp":14.5,"minTemp":4.7,"precipitation":15.1},{"month":4,"maxTemp":21.9,"minTemp":11.1,"precipitation":28.8},{"month":5,"maxTemp":27.6,"minTemp":17,"precipitation":65.1},{"month":6,"maxTemp":31.8,"minTemp":21.5,"precipitation":85.7},{"month":7,"maxTemp":31.8,"minTemp":23.1,"precipitation":184.3},{"month":8,"maxTemp":30.5,"minTemp":21.8,"precipitation":179.4},{"month":9,"maxTemp":27,"minTemp":17.4,"precipitation":63.8},{"month":10,"maxTemp":21.1,"minTemp":11.8,"precipitation":33.2},{"month":11,"maxTemp":13.1,"minTemp":4.3,"precipitation":16.6},{"month":12,"maxTemp":5.9,"minTemp":-1.9,"precipitation":7}]
     */

    @SerializedName("time")
    private String time;
    @SerializedName("month")
    private List<MonthBean> month;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<MonthBean> getMonth() {
        return month;
    }

    public void setMonth(List<MonthBean> month) {
        this.month = month;
    }
}
