package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:54
 */
public class PredictBean {

    /**
     * station : {"code":"54823","province":"山东省","city":"济南","url":"/publish/forecast/ASD/jinan.html"}
     * publish_time : 2021-01-21 20:00
     * detail : [{"date":"2021-01-21","pt":"2021-01-21 20:00","day":{"weather":{"info":"9999","img":"9999","temperature":"9999"},"wind":{"direct":"9999","power":"9999"}},"night":{"weather":{"info":"多云","img":"1","temperature":"0"},"wind":{"direct":"东北风","power":"微风"}}},{"date":"2021-01-22","pt":"2021-01-21 20:00","day":{"weather":{"info":"多云","img":"1","temperature":"6"},"wind":{"direct":"东北风","power":"3~4级"}},"night":{"weather":{"info":"多云","img":"1","temperature":"-2"},"wind":{"direct":"东北风","power":"微风"}}},{"date":"2021-01-23","pt":"2021-01-21 20:00","day":{"weather":{"info":"晴","img":"0","temperature":"7"},"wind":{"direct":"东北风","power":"微风"}},"night":{"weather":{"info":"晴","img":"0","temperature":"-1"},"wind":{"direct":"北风","power":"微风"}}},{"date":"2021-01-24","pt":"2021-01-21 20:00","day":{"weather":{"info":"多云","img":"1","temperature":"9"},"wind":{"direct":"北风","power":"微风"}},"night":{"weather":{"info":"多云","img":"1","temperature":"0"},"wind":{"direct":"北风","power":"微风"}}},{"date":"2021-01-25","pt":"2021-01-21 20:00","day":{"weather":{"info":"小雨","img":"7","temperature":"6"},"wind":{"direct":"北风","power":"微风"}},"night":{"weather":{"info":"雨夹雪","img":"6","temperature":"0"},"wind":{"direct":"北风","power":"微风"}}},{"date":"2021-01-26","pt":"2021-01-21 20:00","day":{"weather":{"info":"多云","img":"1","temperature":"7"},"wind":{"direct":"北风","power":"微风"}},"night":{"weather":{"info":"晴","img":"0","temperature":"-1"},"wind":{"direct":"南风","power":"微风"}}},{"date":"2021-01-27","pt":"2021-01-21 20:00","day":{"weather":{"info":"晴","img":"0","temperature":"9"},"wind":{"direct":"南风","power":"3~4级"}},"night":{"weather":{"info":"晴","img":"0","temperature":"-5"},"wind":{"direct":"北风","power":"3~4级"}}}]
     */

    @SerializedName("station")
    private StationBeanX station;
    @SerializedName("publish_time")
    private String publishTime;
    @SerializedName("detail")
    private List<DetailBean> detail;

    public StationBeanX getStation() {
        return station;
    }

    public void setStation(StationBeanX station) {
        this.station = station;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public List<DetailBean> getDetail() {
        return detail;
    }

    public void setDetail(List<DetailBean> detail) {
        this.detail = detail;
    }
}
