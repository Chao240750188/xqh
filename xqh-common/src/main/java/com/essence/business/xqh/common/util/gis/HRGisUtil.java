package com.essence.business.xqh.common.util.gis;

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

public class HRGisUtil {



    /**
     * 获取所有河流
     */
    public static String getTownLayer(String gisUrl) throws Exception {
        // 创建默认的httpClient实例
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost(gisUrl);
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("where", "1=1"));
        params.add(new BasicNameValuePair("text", ""));
        params.add(new BasicNameValuePair("objectIds", ""));
        params.add(new BasicNameValuePair("time", ""));
        params.add(new BasicNameValuePair("geometry", ""));
        params.add(new BasicNameValuePair("geometryType", "esriGeometryEnvelope"));
        params.add(new BasicNameValuePair("inSR", ""));
        params.add(new BasicNameValuePair("spatialRel", "esriSpatialRelIntersects"));
        params.add(new BasicNameValuePair("relationParam", ""));
        params.add(new BasicNameValuePair("outFields", "*"));
        params.add(new BasicNameValuePair("returnGeometry", "true"));
        params.add(new BasicNameValuePair("maxAllowableOffset", ""));
        params.add(new BasicNameValuePair("geometryPrecision", ""));
        params.add(new BasicNameValuePair("outSR", "4326"));
        params.add(new BasicNameValuePair("returnIdsOnly", "false"));
        params.add(new BasicNameValuePair("returnCountOnly", "false"));
        params.add(new BasicNameValuePair("orderByFields", ""));
        params.add(new BasicNameValuePair("groupByFieldsForStatistics", ""));
        params.add(new BasicNameValuePair("outStatistics", ""));
        params.add(new BasicNameValuePair("returnZ", "false"));
        params.add(new BasicNameValuePair("returnM", "false"));
        params.add(new BasicNameValuePair("gdbVersion", ""));
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

}
