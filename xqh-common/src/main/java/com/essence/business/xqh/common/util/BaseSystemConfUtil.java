package com.essence.business.xqh.common.util;

import java.io.InputStream;
import java.util.Properties;

public class BaseSystemConfUtil {

    private static Properties props = null;
    static {
        if(null==props){
            InputStream in = null;
            props = new Properties();
            try {
                in = BaseSystemConfUtil.class.getClassLoader()
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
     * @return 附件文件存储基础目录
     */
    public static String getFileSavePath(){ return props.getProperty("FileSavePath"); }


    /**
     * @return 附件文件存储基础目录
     */
    public static String getAddress(){ return props.getProperty("address"); }

    /**
     * @return 附件文件存储基础目录
     */
    public static String getPort(){ return props.getProperty("port"); }


    /**
     * @return 访问系统token
     */
    public static String getAccessToken(){ return props.getProperty("AccessToken");
    }

    /**
     * @return 地下水路径
     */
    public static String getHdgwUrl(){ return props.getProperty("hdgwUrl"); }


    /**
     * @return 排水路径
     */
    public static String getDrainUrl(){ return props.getProperty("drainUrl"); }

    /**
     * @return 自备井路径
     */
    public static String getJlckUrl(){ return props.getProperty("jlkcUrl"); }


    /**
     * @return 水源热泵路径
     */
    public static String getHdwshpUrl(){ return props.getProperty("hdwshpUrl"); }


    /**
     * @return npmJSON数据
     */
    public static String getNpmUrl(){ return props.getProperty("npmUrl"); }


    /**
     * @return shjdata数据
     */
    public static String getShjdataUrl(){ return props.getProperty("shjdataUrl"); }


    /**
     * @return 农灌三级平台路径
     */
    public static String getHdawiUrl(){ return props.getProperty("hdawiUrl"); }


    /**
     * 海淀河长制url
     *
     * @return java.lang.String
     **/
    public static String getHdipProjectUrl() {
        return props.getProperty("hdipProjectUrl");
    }

    /**
     * 海淀河长制url
     *
     * @return java.lang.String
     **/
    public static String getHdhzzProjectUrl() {
        return props.getProperty("hdhzzProjectUrl");
    }


}
