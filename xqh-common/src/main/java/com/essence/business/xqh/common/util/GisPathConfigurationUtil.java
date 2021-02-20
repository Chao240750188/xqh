package com.essence.business.xqh.common.util;

import java.io.InputStream;
import java.util.Properties;

public class GisPathConfigurationUtil {


    private static Properties props = null;

    static {
        if (null == props) {
            InputStream in = null;
            props = new Properties();
            try {
                in = GisPathConfigurationUtil.class.getClassLoader()
                        .getResourceAsStream("gisConfiguration.properties");
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
     * @return ArcGISserver主机名称（IP地址）
     */
    public static String getArcGISServerHostIP() {
        return props.getProperty("ArcGISServerHostIP");
    }

    /**
     * 远程调用验证token
     *
     * @return java.lang.String
     **/
    public static String getRemoteToken() {
        return props.getProperty("remoteToken");
    }

    /**
     * 生成dbf后的存储路径
     * @return
     */
    public  static String getModelGridDbfFilesPath(){
        return props.getProperty("modelGridDbfFilesPath");
    }


    /**
     * 获取shp模板的路径--单位线模型
     * @return
     */
    public  static String getDwxModelShpTempletePath(){
        return props.getProperty("dwxModelShpTempletePath");
    }
    /**
     * 获取shp模板的路径--SCS模型
     * @return
     */
    public  static String getScsModelShpTempletePath(){
        return props.getProperty("scsModelShpTempletePath");
    }
    /**
     * 获取shp模板的路径--防洪保护区1
     * @return
     */
    public  static String getHsfx01ModelShpTempletePath(){
        return props.getProperty("hsfx01ModelShpTempletePath");
    }
    /**
     * 获取shp模板的路径--防洪保护区2
     * @return
     */
    public  static String getHsfx02ModelShpTempletePath(){
        return props.getProperty("hsfx02ModelShpTempletePath");
    }

    /**
     * 获取mxd模板的路径
     * @return
     */
    public  static String getMxdTemplateAbsolutePath(){
        return props.getProperty("mxdTemplateAbsolutePath");
    }


    /**
     * 模型MODEL_HSFX_01的mxd模板名称
     * @return
     */
    public  static String getMxdTemplateHSFX01(){
        return props.getProperty("mxdTemplateHSFX01");
    }

    /**
     * 模型MODEL_HSFX_02的mxd模板名称
     * @return
     */
    public  static String getMxdTemplateHSFX02(){
        return props.getProperty("mxdTemplateHSFX02");
    }


    /**
     * 获取导出图片的格式
     * @return
     */
    public  static String getExportPictureFormate(){
        return props.getProperty("exportPictureFormate");
    }


    /**
     * 保存生成图片的文件夹
     * @return
     */
    public static String getOutputPictureAbsolutePath(){
        return props.getProperty("outputPictureAbsolutePath");
    }

    //线程池设置

    /**
     * 设置主线程数
     * @return
     */
    public static Integer getCorePoolSize(){
        return  Integer.parseInt(props.getProperty("corePoolSize"));
    }


    /**
     * 设置最大线程数
     * @return
     */
    public static Integer getMaxPoolSize(){
        return  Integer.parseInt(props.getProperty("maxPoolSize"));
    }

    /**
     * 设置队列中的数
     * @return
     */
    public static Integer getQueueCapacity(){
        return  Integer.parseInt(props.getProperty("queueCapacity"));
    }


    /**
     * 线程闲置后存活时间,单位：秒
     * @return
     */
    public static Integer getKeepAliveTime(){
        return  Integer.parseInt(props.getProperty("keepAliveTime"));
    }
}