package com.essence.business.xqh.common.util.word;

import java.io.InputStream;
import java.util.Properties;

public class AttachmentPathUtil {
    private static Properties props = null;

    static {
        if(null==props){
            InputStream in = null;
            props = new Properties();
            try {
                in = AttachmentPathUtil.class.getClassLoader()
                        .getResourceAsStream("attachmentPath.properties");
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
     * 获取上传附件的保存路径
     * @Author huangxiaoli
     * @Description
     * @Date 15:25 2020/8/13
     * @Param []
     * @return java.lang.String
     **/
    public static String getUploadAttachmentPath(){
        return props.getProperty("uploadAttachmentPath");
    }



    /**
     * 获取防汛一张图--水雨情信息--雨情趋势GIS处理url
     * @Author huangxiaoli
     * @Description
     * @Date 16:42 2020/9/7
     * @Param []
     * @return java.lang.String
     **/
    public static String getGis3ddzmUrl(){
        return props.getProperty("gis3ddzmUrl");
    }


    /**
     * 雨量时段数据拆分时处理的极大值
     * @Author huangxiaoli
     * @Description
     * @Date 11:46 2020/9/10
     * @Param []
     * @return java.lang.String
     **/
    public static String getRaintimedataMaxRain(){
        return props.getProperty("raintimedataMaxRain");
    }


    /**
     * 雨量时段数据拆分时通知限度
     * @Author huangxiaoli
     * @Description
     * @Date 11:46 2020/9/10
     * @Param []
     * @return java.lang.String
     **/
    public static String getRaintimedataInformRain(){
        return props.getProperty("raintimedataInformRain");
    }

}
