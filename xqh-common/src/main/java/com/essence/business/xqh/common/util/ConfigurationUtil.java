package com.essence.business.xqh.common.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigurationUtil {

    private static Properties props = null;
    static {
        if(null==props){
            InputStream in = null;
            props = new Properties();
            try {
                in = ConfigurationUtil.class.getClassLoader()
                        .getResourceAsStream("baseSystemConf.properties");
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
     * @return
     */
    public static String getWeatherBureauUrl(){ return props.getProperty("weatherBureauUrl"); }


    /**
     * @return
     */
    public static String getWeatherBureauType(){ return props.getProperty("weatherBureauType"); }


    /**
     * @return 天氣預備
     */
    public static String getWeatherForecast(){ return props.getProperty("weatherForecast"); }


}
