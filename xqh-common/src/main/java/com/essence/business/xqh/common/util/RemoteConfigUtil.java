package com.essence.business.xqh.common.util;

import java.io.InputStream;
import java.util.Properties;

public class RemoteConfigUtil {


    private static Properties props = null;

    static {
        if (null == props) {
            InputStream in = null;
            props = new Properties();
            try {
                in = RemoteConfigUtil.class.getClassLoader()
                        .getResourceAsStream("remoteConfig.properties");
                props.load(in);
                if (null != in) {
                    in.close();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }


    /**
     * @return 海淀雨晴等值面
     */
    public static String getHdyqdzm() {
        return props.getProperty("hdyqdzm");
    }


    /**
     * @Description 气象局url:port
     **/
    public static String getWeatherBureauUrl() {
        return props.getProperty("weatherBureauUrl");
    }


    /**
     * @Description 气象局type 与 url联合使用
     **/
    public static String getWeatherBureauType() {
        return props.getProperty("weatherBureauType");
    }



    /**
     * @Description 气象局  获取细网格预报图片数据type 与 url联合使用
     */
    public static String getFineGridForecastType() {
        return props.getProperty("fineGridForecastType");
    }



    /**
     * @Description 气象局  是否启用  type 与 url联合使用
     **/
    public static String getWeatherBureauActive() {
        return props.getProperty("weatherBureauActive");
    }

}