package com.essence.business.xqh.api.realtimemonitor.dto;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * @author Stack
 * @version 1.0
 * @date 2021/1/21 0021 19:52
 */
public class QXMessageDto implements Serializable{


    /**
     * real : {"station":{"code":"54823","province":"山东省","city":"济南","url":"/publish/forecast/ASD/jinan.html"},"publish_time":"2021-01-21 18:40","weather":{"temperature":4.1,"temperatureDiff":1,"airpressure":1000,"humidity":71,"rain":0,"rcomfort":38,"icomfort":-3,"info":"晴","img":"0","feelst":1.7},"wind":{"direct":"东北风","power":"微风","speed":""},"warn":{"alert":"9999","pic":"9999","province":"9999","city":"9999","url":"9999","issuecontent":"9999","fmeans":"9999","signaltype":"9999","signallevel":"9999","pic2":"9999"}}
     * predict : {"station":{"code":"54823","province":"山东省","city":"济南","url":"/publish/forecast/ASD/jinan.html"},"publish_time":"2021-01-21 20:00","detail":[{"date":"2021-01-21","pt":"2021-01-21 20:00","day":{"weather":{"info":"9999","img":"9999","temperature":"9999"},"wind":{"direct":"9999","power":"9999"}},"night":{"weather":{"info":"多云","img":"1","temperature":"0"},"wind":{"direct":"东北风","power":"微风"}}},{"date":"2021-01-22","pt":"2021-01-21 20:00","day":{"weather":{"info":"多云","img":"1","temperature":"6"},"wind":{"direct":"东北风","power":"3~4级"}},"night":{"weather":{"info":"多云","img":"1","temperature":"-2"},"wind":{"direct":"东北风","power":"微风"}}},{"date":"2021-01-23","pt":"2021-01-21 20:00","day":{"weather":{"info":"晴","img":"0","temperature":"7"},"wind":{"direct":"东北风","power":"微风"}},"night":{"weather":{"info":"晴","img":"0","temperature":"-1"},"wind":{"direct":"北风","power":"微风"}}},{"date":"2021-01-24","pt":"2021-01-21 20:00","day":{"weather":{"info":"多云","img":"1","temperature":"9"},"wind":{"direct":"北风","power":"微风"}},"night":{"weather":{"info":"多云","img":"1","temperature":"0"},"wind":{"direct":"北风","power":"微风"}}},{"date":"2021-01-25","pt":"2021-01-21 20:00","day":{"weather":{"info":"小雨","img":"7","temperature":"6"},"wind":{"direct":"北风","power":"微风"}},"night":{"weather":{"info":"雨夹雪","img":"6","temperature":"0"},"wind":{"direct":"北风","power":"微风"}}},{"date":"2021-01-26","pt":"2021-01-21 20:00","day":{"weather":{"info":"多云","img":"1","temperature":"7"},"wind":{"direct":"北风","power":"微风"}},"night":{"weather":{"info":"晴","img":"0","temperature":"-1"},"wind":{"direct":"南风","power":"微风"}}},{"date":"2021-01-27","pt":"2021-01-21 20:00","day":{"weather":{"info":"晴","img":"0","temperature":"9"},"wind":{"direct":"南风","power":"3~4级"}},"night":{"weather":{"info":"晴","img":"0","temperature":"-5"},"wind":{"direct":"北风","power":"3~4级"}}}]}
     * air : {"forecasttime":"2021-01-21 18:00","aqi":218,"aq":5,"text":"重度污染","aqiCode":"99202;99204;99205;99206;99207;99208;99209"}
     * tempchart : [{"time":"2021/01/14","max_temp":15.9,"min_temp":2,"day_img":"9999","day_text":"9999","night_img":"9999","night_text":"9999"},{"time":"2021/01/15","max_temp":4.6,"min_temp":-1.9,"day_img":"9999","day_text":"9999","night_img":"9999","night_text":"9999"},{"time":"2021/01/16","max_temp":2.5,"min_temp":-4.9,"day_img":"9999","day_text":"9999","night_img":"9999","night_text":"9999"},{"time":"2021/01/17","max_temp":4.3,"min_temp":-4.9,"day_img":"9999","day_text":"9999","night_img":"9999","night_text":"9999"},{"time":"2021/01/18","max_temp":9.8,"min_temp":-1.7,"day_img":"9999","day_text":"9999","night_img":"9999","night_text":"9999"},{"time":"2021/01/19","max_temp":6.4,"min_temp":1.4,"day_img":"9999","day_text":"9999","night_img":"9999","night_text":"9999"},{"time":"2021/01/20","max_temp":6.8,"min_temp":-2.8,"day_img":"9999","day_text":"9999","night_img":"9999","night_text":"9999"},{"time":"2021/01/21","max_temp":9999,"min_temp":0,"day_img":"9999","day_text":"9999","night_img":"1","night_text":"多云"},{"time":"2021/01/22","max_temp":6,"min_temp":-2,"day_img":"1","day_text":"多云","night_img":"1","night_text":"多云"},{"time":"2021/01/23","max_temp":7,"min_temp":-1,"day_img":"0","day_text":"晴","night_img":"0","night_text":"晴"},{"time":"2021/01/24","max_temp":9,"min_temp":0,"day_img":"1","day_text":"多云","night_img":"1","night_text":"多云"},{"time":"2021/01/25","max_temp":6,"min_temp":0,"day_img":"7","day_text":"小雨","night_img":"6","night_text":"雨夹雪"},{"time":"2021/01/26","max_temp":7,"min_temp":-1,"day_img":"1","day_text":"多云","night_img":"0","night_text":"晴"},{"time":"2021/01/27","max_temp":9,"min_temp":-5,"day_img":"0","day_text":"晴","night_img":"0","night_text":"晴"}]
     * passedchart : [{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":4.8,"tempDiff":"","humidity":69,"pressure":1000,"windDirection":1,"windSpeed":1.5,"time":"2021-01-21 18:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":5.8,"tempDiff":"","humidity":65,"pressure":999,"windDirection":305,"windSpeed":1.3,"time":"2021-01-21 17:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":6.6,"tempDiff":"","humidity":62,"pressure":999,"windDirection":340,"windSpeed":1.9,"time":"2021-01-21 16:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":6.7,"tempDiff":"","humidity":61,"pressure":999,"windDirection":307,"windSpeed":1.6,"time":"2021-01-21 15:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":7.2,"tempDiff":"","humidity":60,"pressure":999,"windDirection":296,"windSpeed":3.3,"time":"2021-01-21 14:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":7.3,"tempDiff":"","humidity":60,"pressure":1000,"windDirection":298,"windSpeed":4.3,"time":"2021-01-21 13:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":7,"tempDiff":"","humidity":62,"pressure":1000,"windDirection":277,"windSpeed":3.2,"time":"2021-01-21 12:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":4.4,"tempDiff":"","humidity":65,"pressure":1001,"windDirection":356,"windSpeed":0.8,"time":"2021-01-21 11:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":3.4,"tempDiff":"","humidity":69,"pressure":1002,"windDirection":316,"windSpeed":2.1,"time":"2021-01-21 10:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":3.2,"tempDiff":"","humidity":71,"pressure":1001,"windDirection":311,"windSpeed":1.3,"time":"2021-01-21 09:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":2,"tempDiff":"","humidity":77,"pressure":1000,"windDirection":299,"windSpeed":1.8,"time":"2021-01-21 08:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":1.4,"tempDiff":"","humidity":76,"pressure":1000,"windDirection":113,"windSpeed":5,"time":"2021-01-21 07:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":1.1,"tempDiff":"","humidity":78,"pressure":1001,"windDirection":9999,"windSpeed":0,"time":"2021-01-21 06:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":1.2,"tempDiff":"","humidity":77,"pressure":1000,"windDirection":215,"windSpeed":0.7,"time":"2021-01-21 05:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":0.9,"tempDiff":"","humidity":78,"pressure":1000,"windDirection":145,"windSpeed":0.9,"time":"2021-01-21 04:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":0.9,"tempDiff":"","humidity":76,"pressure":1001,"windDirection":327,"windSpeed":0.6,"time":"2021-01-21 03:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":1,"tempDiff":"","humidity":75,"pressure":1002,"windDirection":108,"windSpeed":0.4,"time":"2021-01-21 02:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":0.7,"tempDiff":"","humidity":75,"pressure":1002,"windDirection":92,"windSpeed":0.5,"time":"2021-01-21 01:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":0.7,"tempDiff":"","humidity":75,"pressure":1002,"windDirection":148,"windSpeed":0.4,"time":"2021-01-21 00:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":0.9,"tempDiff":"","humidity":74,"pressure":1002,"windDirection":153,"windSpeed":0.8,"time":"2021-01-20 23:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":1.2,"tempDiff":"","humidity":72,"pressure":1002,"windDirection":141,"windSpeed":1.2,"time":"2021-01-20 22:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":1.2,"tempDiff":"","humidity":72,"pressure":1002,"windDirection":107,"windSpeed":1.6,"time":"2021-01-20 21:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":1.8,"tempDiff":"","humidity":70,"pressure":1002,"windDirection":85,"windSpeed":1.5,"time":"2021-01-20 20:00"},{"rain1h":0,"rain24h":9999,"rain12h":9999,"rain6h":9999,"temperature":2.5,"tempDiff":"","humidity":66,"pressure":1002,"windDirection":88,"windSpeed":3.6,"time":"2021-01-20 19:00"}]
     * climate : {"time":"1981年-2010年","month":[{"month":1,"maxTemp":4.1,"minTemp":-4.1,"precipitation":5.8},{"month":2,"maxTemp":8.3,"minTemp":-0.7,"precipitation":8.7},{"month":3,"maxTemp":14.5,"minTemp":4.7,"precipitation":15.1},{"month":4,"maxTemp":21.9,"minTemp":11.1,"precipitation":28.8},{"month":5,"maxTemp":27.6,"minTemp":17,"precipitation":65.1},{"month":6,"maxTemp":31.8,"minTemp":21.5,"precipitation":85.7},{"month":7,"maxTemp":31.8,"minTemp":23.1,"precipitation":184.3},{"month":8,"maxTemp":30.5,"minTemp":21.8,"precipitation":179.4},{"month":9,"maxTemp":27,"minTemp":17.4,"precipitation":63.8},{"month":10,"maxTemp":21.1,"minTemp":11.8,"precipitation":33.2},{"month":11,"maxTemp":13.1,"minTemp":4.3,"precipitation":16.6},{"month":12,"maxTemp":5.9,"minTemp":-1.9,"precipitation":7}]}
     * radar : {"title":"济南","image":"/product/2021/01/21/RDCP/SEVP_AOC_RDCP_SLDAS_EBREF_AZ9531_L88_PI_20210121091800000.PNG?v=1611220841945","url":"/publish/radar/shan-dong/ji-nan.htm"}
     */

    @SerializedName("real")
    private RealBean real;
    @SerializedName("predict")
    private PredictBean predict;
    @SerializedName("air")
    private AirBean air;
    @SerializedName("climate")
    private ClimateBean climate;
    @SerializedName("radar")
    private RadarBean radar;
    @SerializedName("tempchart")
    private List<TempchartBean> tempchart;
    @SerializedName("passedchart")
    private List<PassedchartBean> passedchart;

    public RealBean getReal() {
        return real;
    }

    public void setReal(RealBean real) {
        this.real = real;
    }

    public PredictBean getPredict() {
        return predict;
    }

    public void setPredict(PredictBean predict) {
        this.predict = predict;
    }

    public AirBean getAir() {
        return air;
    }

    public void setAir(AirBean air) {
        this.air = air;
    }

    public ClimateBean getClimate() {
        return climate;
    }

    public void setClimate(ClimateBean climate) {
        this.climate = climate;
    }

    public RadarBean getRadar() {
        return radar;
    }

    public void setRadar(RadarBean radar) {
        this.radar = radar;
    }

    public List<TempchartBean> getTempchart() {
        return tempchart;
    }

    public void setTempchart(List<TempchartBean> tempchart) {
        this.tempchart = tempchart;
    }

    public List<PassedchartBean> getPassedchart() {
        return passedchart;
    }

    public void setPassedchart(List<PassedchartBean> passedchart) {
        this.passedchart = passedchart;
    }
}
