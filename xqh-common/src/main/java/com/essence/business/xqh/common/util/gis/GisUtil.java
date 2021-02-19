package com.essence.business.xqh.common.util.gis;

import com.essence.business.xqh.common.util.ExportMethodResultDto;
import com.essence.business.xqh.common.util.GisPathConfigurationUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GisUtil {

    /**
     * 将 shp文件 调用GIS服务 替换数据源  导出png图片
     * @param inAbsolutePath    shp文件夹路径
     * @param inFileName    shp文件名
     * @param mxdTemplateAbsolutePath 渲染数据文件的文件夹路径
     * @param mxdTemplate     渲染数据文件的文件名
     * @param exportFormat   导出格式
     * @param outputAbsolutePath  导出图片文件的文件夹路径
     * @param outputPictureName  到处图片的文件名
     */
    public static ExportMethodResultDto exportToPicture(String inAbsolutePath, String inFileName, String mxdTemplateAbsolutePath, String mxdTemplate, String exportFormat, String outputAbsolutePath, String outputPictureName){

        ExportMethodResultDto resultInfo = new ExportMethodResultDto();

        String jobId=null;
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();


        // 创建httppost
        HttpPost httppost = new HttpPost("http://"+ GisPathConfigurationUtil.getArcGISServerHostIP()+":6080/arcgis/rest/services/xqh/exportToPicture/GPServer/exportToPicture/submitJob");

        // 创建参数队列
        System.out.println("封装调用图片参数……");
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("env:outSR", ""));
        params.add(new BasicNameValuePair("env:processSR",""));
        params.add(new BasicNameValuePair("f","pjson"));//format设置成json
        params.add(new BasicNameValuePair("inAbsolutePath",inAbsolutePath));
        System.out.println("inAbsolutePath:"+inAbsolutePath);
        params.add(new BasicNameValuePair("inFileName",inFileName));//不加后缀
        System.out.println("inFileName:"+inFileName);
        params.add(new BasicNameValuePair("mxdTemplateAbsolutePath",mxdTemplateAbsolutePath));
        System.out.println("mxdTemplateAbsolutePath:"+mxdTemplateAbsolutePath);
        params.add(new BasicNameValuePair("mxdTemplate",mxdTemplate));//不加后缀
        System.out.println("mxdTemplate:"+mxdTemplate);
        params.add(new BasicNameValuePair("exportFormat",exportFormat));//格式为.png
        System.out.println("exportFormat:"+exportFormat);
        params.add(new BasicNameValuePair("outputAbsolutePath",outputAbsolutePath));
        System.out.println("outputAbsolutePath:"+outputAbsolutePath);
        params.add(new BasicNameValuePair("outputPictureName",outputPictureName));
        System.out.println("outputPictureName:"+outputPictureName);

        params.add(new BasicNameValuePair("returnM","false"));
        params.add(new BasicNameValuePair("returnZ","false"));
        HttpEntity entity;
        CloseableHttpResponse response =null;
        try {
            entity = new UrlEncodedFormEntity(params, "UTF-8");
            httppost.setEntity(entity);
            response = httpclient.execute(httppost);

            HttpEntity responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
                System.out.println("调用GIS生成图片返回"+responseContent);
                if(responseContent.contains("esriJobSubmitted")){

                    JSONObject json= JSONObject.fromObject(responseContent);
                    jobId = json.getString("jobId"); //异步服务返回的jobId

                }
            }
            if (null==jobId){
                throw new RuntimeException("图片转换失败");
            }
            resultInfo.setJobId(jobId);
        } catch (Exception e) {
            System.out.println("生成图片工具类报错");
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return resultInfo;
    }

    /**
     * 获取生成图片执行状态
     * @param resultInfo
     * @return
     */
    public static String getExportToPictureStatus(ExportMethodResultDto resultInfo){

        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 创建httpget
        HttpGet httpget = new HttpGet("http://"+ GisPathConfigurationUtil.getArcGISServerHostIP()+":6080/arcgis/rest/services/xqh/exportToPicture/GPServer/exportToPicture/jobs/"+resultInfo.getJobId());

        HttpEntity responseEntity;
        CloseableHttpResponse response =null;
        try {

            response = httpclient.execute(httpget);

            responseEntity = response.getEntity();
            if (responseEntity != null) {
                String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
                if(responseContent.contains("esriJobSucceeded")) { //包含字符串表示风险图生成完毕
                    return  "true";
                }else if (responseContent.contains("Failed")){
                    return "failed";
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            // 关闭连接,释放资源
            try {
                response.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "false";
    }
}
