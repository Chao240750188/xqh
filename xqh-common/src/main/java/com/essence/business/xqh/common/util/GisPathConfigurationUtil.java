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
     * 海淀区shp范围文件（绝对路径）
     *
     * @return java.lang.String
     **/
    public static String getHdqRegionSource() {
        return props.getProperty("hdqRegionSource");
    }


    /**
     * csv转为shp文件的WGS1984坐标系文件
     *
     * @return java.lang.String
     **/
    public static String getCoordinateSystems() {
        return props.getProperty("coordinateSystems");
    }


    /**
     * 文件数据存放路径
     *
     * @return java.lang.String
     **/
    public static String getAbsoluteFilePath() {
        return props.getProperty("absoluteFilePath");
    }


    /**
     * @return 雨量站日降雨量生成等值面图片mxd存放路径
     */
    public static String getMxdTemplateAbsolutePath() {
        return props.getProperty("mxdTemplateAbsolutePath");
    }

    /**
     * @return 雨量站日降雨量生成等值面图片mxd文件名称
     */
    public static String getMxdTemplateName() {
        return props.getProperty("mxdTemplateName");
    }

    /**
     * 导出的数据格式
     *
     * @return java.lang.String
     **/
    public static String getExportFormat() {
        return props.getProperty("exportFormat");
    }


    /**
     * 南旱河区域洪水风险涉及统计的雨量站
     *
     * @return java.lang.String
     **/
    public static String getNhhRiskStatisticsStation() {
        return props.getProperty("nhhRiskStatisticsStation");
    }


    /**
     * 水库/蓄洪区
     * 本公司数据库中水库测站编码
     *
     * @return java.lang.String
     **/
    public static String getOwnReservoirStcd() {
        return props.getProperty("ownReservoirStcd");
    }

    /**
     * 水库/蓄洪区
     * 本公司数据库中水库测站名称
     *
     * @return java.lang.String
     **/
    public static String getOwnReservoirStnm() {
        // TODO: 2020/8/25 待解决
        return "五七水库";
//		return props.getProperty("ownReservoirStnm");
    }

    /**
     * 水库/蓄洪区
     * 方正-南沙河数据库中水库测站编码
     *
     * @return java.lang.String
     **/
    public static String getFzReservoirStcd() {
        return props.getProperty("fzReservoirStcd");
    }

    /**
     * 水库/蓄洪区
     * 方正-南沙河数据库中水库测站名称
     *
     * @return java.lang.String
     **/
    public static String getFzReservoirStnm() {
        return props.getProperty("fzReservoirStnm");
    }

    /**
     * 五七水库高程
     *
     * @return java.lang.String
     **/
    public static String getReservoirElevation() {
        return props.getProperty("reservoirElevation");
    }

    /**
     * 上庄水库编码
     *
     * @return java.lang.String
     **/
    public static String getFzShangZhuangStcd() {
        return props.getProperty("fzShangZhuangStcd");
    }

    /**
     * 上庄水库名称
     *
     * @return java.lang.String
     **/
    public static String getFzShangZhuangName() {
        // TODO: 2020/8/25 待解决
        return "上庄水库";
//		return props.getProperty("fzShangZhuangName");
    }

    /**
     * 上庄水库名称
     *
     * @return java.lang.String
     **/
    public static String getFxsjOrigin() {
        return props.getProperty("fxsjOrigin");
    }

    /**
     * 道路易积水点中判断两条降雨量为两场降雨的相差小时数
     *
     * @return java.lang.String
     **/
    public static String getDlyjsdRainfallHour() {
        return props.getProperty("dlyjsdRainfallHour");
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
     * 指定顺序的断面测站编码
     *
     * @return java.lang.String
     **/
    public static String getSearchSectoionStcd() {
        return props.getProperty("searchSectoionStcd");
    }
}