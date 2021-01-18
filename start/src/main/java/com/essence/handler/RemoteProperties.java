/*
package com.essence.handler;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

*/
/**
 * @ClassName RemoteProperties
 * @Description 配置类
 * @Author zhichao.xing
 * @Date 2019/10/29 10:47
 * @Version 1.0
 **//*

@PropertySource({
        "classpath:prop/${spring.profiles.active}/conf.properties"
})
@Configuration
@ConfigurationProperties(prefix = "remote", ignoreUnknownFields = false)
@Component

public class RemoteProperties {
    */
/**
     * @Description 海淀雨晴等值面
     **//*

    private String hdyqdzm;
    */
/**
     * @Description 气象局url:port
     **//*

    private String weatherBureauUrl;
    */
/**
     * @Description 气象局type 与 url联合使用
     **//*

    private String weatherBureauType;

    */
/**
     * @Description 气象局  获取细网格预报图片数据type 与 url联合使用
     *//*

    private String fineGridForecastType;

    */
/**
     * @Description 气象局  是否启用  type 与 url联合使用
     **//*

    private Boolean weatherBureauActive;

    public String getFineGridForecastType() {
        return fineGridForecastType;
    }

    public void setFineGridForecastType(String fineGridForecastType) {
        this.fineGridForecastType = fineGridForecastType;
    }

    public String getHdyqdzm() {
        return hdyqdzm;
    }

    public void setHdyqdzm(String hdyqdzm) {
        this.hdyqdzm = hdyqdzm;
    }

    public String getWeatherBureauUrl() {
        return weatherBureauUrl;
    }

    public void setWeatherBureauUrl(String weatherBureauUrl) {
        this.weatherBureauUrl = weatherBureauUrl;
    }

    public String getWeatherBureauType() {
        return weatherBureauType;
    }

    public void setWeatherBureauType(String weatherBureauType) {
        this.weatherBureauType = weatherBureauType;
    }

    public Boolean getWeatherBureauActive() {
        return weatherBureauActive;
    }

    public void setWeatherBureauActive(Boolean weatherBureauActive) {
        this.weatherBureauActive = weatherBureauActive;
    }


    @Override
    public String toString() {
        return "RemoteProperties{" +
                "hdyqdzm='" + hdyqdzm + '\'' +
                ", weatherBureauUrl='" + weatherBureauUrl + '\'' +
                ", weatherBureauType='" + weatherBureauType + '\'' +
                ", fineGridForecastType='" + fineGridForecastType + '\'' +
                ", weatherBureauActive=" + weatherBureauActive +
                '}';
    }
}
*/
