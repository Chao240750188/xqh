package com.essence.business.xqh.common.util.gis;

import com.essence.business.xqh.common.util.word.AttachmentPathUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class GisUtil {
    /**
     * 获取乡镇街道信息
     */
    public  String getWaterSlice(String gisUrl0) throws Exception {

        // 创建默认的httpClient实例
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(gisUrl0);
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("where", "1=1"));
        params.add(new BasicNameValuePair("geometryType", "esriGeometryEnvelope"));
        params.add(new BasicNameValuePair("spatialRel", "esriSpatialRelIntersects"));
        params.add(new BasicNameValuePair("outFields", "*"));
        params.add(new BasicNameValuePair("returnGeometry", "true"));
        params.add(new BasicNameValuePair("returnIdsOnly", "false"));
        params.add(new BasicNameValuePair("returnCountOnly", "false"));
        params.add(new BasicNameValuePair("returnZ", "false"));
        params.add(new BasicNameValuePair("returnM", "false"));
        params.add(new BasicNameValuePair("returnDistinctValues", "false"));
        params.add(new BasicNameValuePair("f", "pjson"));
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
        httppost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httppost);
        HttpEntity responseEntity = response.getEntity();
        String responseContent = EntityUtils.toString(responseEntity, "UTF-8");

        if (!responseContent.contains("features")) {
            throw new RuntimeException("调用GIS服务-获取水闸，错误信息：" + responseContent);
        }
        response.close();
        // 关闭连接,释放资源
        httpclient.close();
        return responseContent;
    }


    /**
     * 获取雨晴等值面
     * Reclassification         0.01 2.5 1;2.5 8 2;8 16 3;16 20 4;20 9999 5
     */
    public String getHdyqDzm(String gisUrl0, String Reclassification, String inputRainfall_shp)throws Exception {
        // 创建默认的httpClient实例
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(gisUrl0);
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("Reclassification", Reclassification));
        params.add(new BasicNameValuePair("inputRainfall_shp", inputRainfall_shp));
        params.add(new BasicNameValuePair("returnZ", "false"));
        params.add(new BasicNameValuePair("returnM", "false"));
        params.add(new BasicNameValuePair("env:processSR", ""));
        params.add(new BasicNameValuePair("env:outSR", ""));
        params.add(new BasicNameValuePair("f", "pjson"));
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
        httppost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httppost);
        HttpEntity responseEntity = response.getEntity();
        String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
        if (!responseContent.contains("features")) {
            throw new RuntimeException("调用GIS服务-获取水闸，错误信息：" + responseContent);
        }
        response.close();
        // 关闭连接,释放资源
        httpclient.close();
        return responseContent;
    }



 /**
  * 获取雨情趋势数据
  * @Author huangxiaoli
  * @Description
  * @Date 16:50 2020/9/7
  * @Param [inputRainfall_shp]
  * @return java.lang.String
  **/
    public static String getGis3ddzm(String inputRainfall_shp)throws Exception {
        // 创建默认的httpClient实例
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(AttachmentPathUtil.getGis3ddzmUrl());
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("input_rainfall", inputRainfall_shp));
        params.add(new BasicNameValuePair("returnZ", "false"));
        params.add(new BasicNameValuePair("returnM", "false"));
        params.add(new BasicNameValuePair("env:processSR", ""));
        params.add(new BasicNameValuePair("env:outSR", ""));
        params.add(new BasicNameValuePair("f", "pjson"));
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
        httppost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httppost);
        HttpEntity responseEntity = response.getEntity();
        String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
        if (!responseContent.contains("features")) {
            throw new RuntimeException("调用GIS服务-获取水闸，错误信息：" + responseContent);
        }
        response.close();
        // 关闭连接,释放资源
        httpclient.close();
        return responseContent;
    }


    /**
     * 获取雨情趋势数据
     * @Author huangxiaoli
     * @Description
     * @Date 10:46 2020/10/23
     * @Param [inputRainfall_shp]
     * @return java.lang.String
     **/
    public static String getGisTrend3ddzm(String inputRainfall_shp)throws Exception {
        // 创建默认的httpClient实例
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(AttachmentPathUtil.getGis3ddzmUrl());
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("input_rainfall", inputRainfall_shp));
        params.add(new BasicNameValuePair("returnZ", "false"));
        params.add(new BasicNameValuePair("returnM", "false"));
        params.add(new BasicNameValuePair("env:processSR", "4326"));
        params.add(new BasicNameValuePair("env:outSR", "4326"));
        params.add(new BasicNameValuePair("f", "pjson"));
        HttpEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
        httppost.setEntity(entity);
        CloseableHttpResponse response = httpclient.execute(httppost);
        HttpEntity responseEntity = response.getEntity();
        String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
        if (!responseContent.contains("features")) {
            throw new RuntimeException("调用GIS服务-获取水闸，错误信息：" + responseContent);
        }
        response.close();
        // 关闭连接,释放资源
        httpclient.close();
        return responseContent;
    }
}
