package com.essence.business.xqh.common.util;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class GisUtil {


    /**
     * 雨量站日降雨量生成等值面图片
     * @Author huangxiaoli
     * @Description
     * @Date 9:30 2020/6/4
     * @Param  hdqRegionSource  海淀区shp范围文件（绝对路径）
     * @Param  coordinateSystems  csv转为shp文件的坐标系文件
     * @Param  csvDataAbsolutePath  雨量站csv数据存放路径
     * @Param  dataSource 雨量站CSV数据名称(带文件后缀)
     * @Param  mxdTemplateAbsolutePath mxd存放路径
     * @Param  mxdTemplate   mxd文件名称（无后缀）
     * @Param  outputAbsolutePath 结果输出路径
     * @Param  outputPictureName  输出结果名称
     * @Param  exportFormat 导出的数据格式
     * @return com.essence.shzh.util.gis.ExportMethodResultDto
     **/
    public static ExportMethodResultDto stationSplineToPicture(String hdqRegionSource, String coordinateSystems, String csvDataAbsolutePath, String dataSource, String mxdTemplateAbsolutePath, String mxdTemplate, String outputAbsolutePath, String outputPictureName, String exportFormat){

        ExportMethodResultDto resultInfo = new ExportMethodResultDto();


        String jobId=null;
        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost("http://"+ GisPathConfigurationUtil.getArcGISServerHostIP()+":6080/arcgis/rest/services/shzh/stationSplineToPicture/GPServer/stationSplineToPicture/submitJob");

        // 创建参数队列
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("env:outSR", ""));
        params.add(new BasicNameValuePair("env:processSR",""));
        params.add(new BasicNameValuePair("f","pjson"));//format设置成json
        params.add(new BasicNameValuePair("hdqRegionSource",hdqRegionSource));
        System.out.println("hdqRegionSource:"+hdqRegionSource);
        params.add(new BasicNameValuePair("coordinateSystems",coordinateSystems));
        System.out.println("coordinateSystems:"+coordinateSystems);
        params.add(new BasicNameValuePair("csvDataAbsolutePath",csvDataAbsolutePath));
        System.out.println("csvDataAbsolutePath:"+csvDataAbsolutePath);
        params.add(new BasicNameValuePair("dataSource",dataSource));
        System.out.println("dataSource:"+dataSource);
        params.add(new BasicNameValuePair("mxdTemplateAbsolutePath",mxdTemplateAbsolutePath));
        System.out.println("mxdTemplateAbsolutePath:"+mxdTemplateAbsolutePath);
        params.add(new BasicNameValuePair("mxdTemplate",mxdTemplate));
        System.out.println("mxdTemplate:"+mxdTemplate);
        params.add(new BasicNameValuePair("outputAbsolutePath",outputAbsolutePath));
        System.out.println("outputAbsolutePath:"+outputAbsolutePath);
        params.add(new BasicNameValuePair("outputPictureName",outputPictureName));
        System.out.println("outputPictureName:"+outputPictureName);
        params.add(new BasicNameValuePair("exportFormat",exportFormat));
        System.out.println("exportFormat:"+exportFormat);
        params.add(new BasicNameValuePair("returnM","false"));
        params.add(new BasicNameValuePair("returnZ","false"));
        HttpEntity entity;
        try {
            entity = new UrlEncodedFormEntity(params, "UTF-8");
            httppost.setEntity(entity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                HttpEntity responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
                    if(responseContent.contains("esriJobSubmitted")){
                        try {
                            JSONObject json= JSONObject.fromObject(responseContent);
                            jobId = json.getString("jobId"); //异步服务返回的jobId
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (null==jobId){
            throw new RuntimeException("Gis调用失败");
        }
        resultInfo.setOutFilePath(outputAbsolutePath+"/"+outputPictureName+"."+exportFormat);
        resultInfo.setJobId(jobId);
        return resultInfo;
    }

    /**
     * 获取雨量站日降雨量生成等值面图片Gis服务状态
     * @param resultInfo
     * @return
     */
    public static String getStationSplineToPictureStatus(ExportMethodResultDto resultInfo){


        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();


        // 创建httpget
        HttpGet httpget = new HttpGet("http://"+ GisPathConfigurationUtil.getArcGISServerHostIP()+":6080/arcgis/rest/services/shzh/stationSplineToPicture/GPServer/stationSplineToPicture/jobs/"+resultInfo.getJobId());

        HttpEntity responseEntity;
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                responseEntity = response.getEntity();
                if (responseEntity != null) {
                    String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
                    if(responseContent.contains("esriJobSucceeded")) { //包含字符串表示风险图生成完毕
                        return  "true";
                    }else if (responseContent.contains("Failed")){
                        return "failed";
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            } finally {
                response.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "false";
    }


    /**
     * 获取防汛时间的buffer
     *
     * @return
     */

    public static Object geometryBuffer(Double lgtd,Double lttd, String range) {

        Object ringsPointData="";//buffer区域数据
      // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost("http://"+ GisPathConfigurationUtil.getArcGISServerHostIP()+":6080/arcgis/rest/services/Utilities/Geometry/GeometryServer/buffer");

        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("geometries", lgtd+","+lttd));
        params.add(new BasicNameValuePair("inSR", "4326"));
        params.add(new BasicNameValuePair("outSR", "4326"));
        params.add(new BasicNameValuePair("bufferSR", "3395"));
        params.add(new BasicNameValuePair("distances", range));//500米范围内
        params.add(new BasicNameValuePair("unit", "9001"));
        params.add(new BasicNameValuePair("unionResults", "true"));
        params.add(new BasicNameValuePair("geodesic", "false"));
        params.add(new BasicNameValuePair("f", "pjson"));
        HttpEntity entity = null;
        CloseableHttpResponse response =null;
        try {
            entity = new UrlEncodedFormEntity(params, "UTF-8");
            httppost.setEntity(entity);
            response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode()==200){//如果状态码为200,就是正常返回

                HttpEntity responseEntity = response.getEntity();
                if (null!=responseEntity){
                    String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
                    if (responseContent.contains("geometries")) {
                        JSONObject json= JSONObject.fromObject(responseContent);
                        Object geometries = json.get("geometries");

                        Gson gson = new Gson();
                        List<Object> geometriesList =gson.fromJson(gson.toJson(geometries), new TypeToken<List<Object>>() {}.getType());
                        Object geometriesChildObject = geometriesList.get(0);
                        JSONObject jsonObject = JSONObject.fromObject(geometriesChildObject);
                        Object ringsValue = jsonObject.get("rings");
                        ringsPointData="{\"rings\":"+gson.toJson(ringsValue)+"}";
                    }
               }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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

        return  ringsPointData;
    }


    /**
     * 获取防汛事件buffer内的摄像头编码信息
     * @Author huangxiaoli
     * @Description
     * @Date 17:22 2020/7/8
     * @Param [inputGeomery]
     * @return java.util.List<java.lang.String>
     **/
    public static List<String> cameraQueray(Object inputGeomery){

        List<String> cameraCodeList = new ArrayList<>();

        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost("http://"+ GisPathConfigurationUtil.getArcGISServerHostIP()+":6080/arcgis/rest/services/hd_gis_common/hd_dynamic_map/MapServer/0/query");

        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("geometry", inputGeomery.toString()));
        params.add(new BasicNameValuePair("geometryType", "esriGeometryPolygon"));
        params.add(new BasicNameValuePair("spatialRel", "esriSpatialRelIntersects"));
        params.add(new BasicNameValuePair("returnGeometry","true"));
        params.add(new BasicNameValuePair("returnIdsOnly","false"));
        params.add(new BasicNameValuePair("returnCountOnly","false"));
        params.add(new BasicNameValuePair("returnM","false"));
        params.add(new BasicNameValuePair("returnZ","false"));
        params.add(new BasicNameValuePair("returnDistinctValues","false"));
        params.add(new BasicNameValuePair("f", "pjson"));

        HttpEntity entity = null;
        CloseableHttpResponse response =null;
        try {
            entity = new UrlEncodedFormEntity(params, "UTF-8");
            httppost.setEntity(entity);
            response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode()==200){//如果状态码为200,就是正常返回

                HttpEntity responseEntity = response.getEntity();
                if (null!=responseEntity){
                    String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
                    if (responseContent.contains("features")) {
                        JSONObject json= JSONObject.fromObject(responseContent);
                        Object features = json.get("features");

                        Gson gson = new Gson();
                        List<Object> featuresList =gson.fromJson(gson.toJson(features), new TypeToken<List<Object>>() {}.getType());
                        if (featuresList.size()>0){
                            for (int i=0;i<featuresList.size();i++){
                                Object featureObject = featuresList.get(i);

                                JSONObject featureJson= JSONObject.fromObject(featureObject);

                                Object attributesObject = featureJson.get("attributes");

                                JSONObject cameracodeJson= JSONObject.fromObject(attributesObject);
                                Object cameracode = cameracodeJson.get("cameracode");
                                cameraCodeList.add(cameracode.toString());
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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

        return cameraCodeList;
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
     * 获取新建环境事件buffer内的乡镇街道
     * @Author huangxiaoli
     * @Description
     * @Date 17:22 2020/7/8
     * @Param [inputGeomery]
     * @return java.util.List<java.lang.String>
     **/
    public static String addvnmQueray(Object inputGeomery){

        String addvnm= "";

        // 创建默认的httpClient实例.
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建httppost
        HttpPost httppost = new HttpPost("http://"+ GisPathConfigurationUtil.getArcGISServerHostIP()+":6080/arcgis/rest/services/HDSW/BASE_MAP/MapServer/4/query");

        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("geometry", inputGeomery.toString()));
        params.add(new BasicNameValuePair("geometryType", "esriGeometryPolygon"));
        params.add(new BasicNameValuePair("spatialRel", "esriSpatialRelIntersects"));
        params.add(new BasicNameValuePair("outFields", "AD_NM"));
        params.add(new BasicNameValuePair("returnGeometry","true"));
        params.add(new BasicNameValuePair("returnIdsOnly","false"));
        params.add(new BasicNameValuePair("returnCountOnly","false"));
        params.add(new BasicNameValuePair("returnM","false"));
        params.add(new BasicNameValuePair("returnZ","false"));
        params.add(new BasicNameValuePair("returnDistinctValues","false"));
        params.add(new BasicNameValuePair("f", "pjson"));

        HttpEntity entity = null;
        CloseableHttpResponse response =null;
        try {
            entity = new UrlEncodedFormEntity(params, "UTF-8");
            httppost.setEntity(entity);
            response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode()==200){//如果状态码为200,就是正常返回

                HttpEntity responseEntity = response.getEntity();
                if (null!=responseEntity){
                    String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
                    if (responseContent.contains("features")) {
                        JSONObject json= JSONObject.fromObject(responseContent);
                        Object features = json.get("features");

                        Gson gson = new Gson();
                        List<Object> featuresList =gson.fromJson(gson.toJson(features), new TypeToken<List<Object>>() {}.getType());
                        if (featuresList.size()>0){
                            Object featureObject = featuresList.get(0);

                            JSONObject featureJson= JSONObject.fromObject(featureObject);

                            Object attributesObject = featureJson.get("attributes");

                            JSONObject addvnmJson= JSONObject.fromObject(attributesObject);
                            Object addvnmObject = addvnmJson.get("AD_NM");
                            addvnm=addvnmObject.toString();
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
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

        return addvnm;
    }
    /**
     * 两点分割河流线生成两点之间的线段
     * @Author huangxiaoli
     * @Description
     * @Date 9:31 2020/10/20
     * @Param [hashMap]
     * @return com.essence.hdszy.util.gis.ExportMethodResultDto
     **/
    public static Object getShjSplitLineAtPoint(Double lgtd1, Double lttd1, Double lgtd2, Double lttd2, String riverFilePath ) {


        // 创建默认的httpClient实例
        CloseableHttpClient httpclient = HttpClients.createDefault();

        // 创建httppost
        HttpPost httppost = new HttpPost("http://" + GisPathConfigurationUtil.getArcGISServerHostIP() + ":6080/arcgis/rest/services/shzh/shjSplitLineAtPoint/GPServer/shjSplitLineAtPoint/execute");
        // 创建参数队列
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("env:outSR", ""));
        params.add(new BasicNameValuePair("env:processSR", ""));
        params.add(new BasicNameValuePair("f", "pjson"));//format设置成json
        params.add(new BasicNameValuePair("inPintOne", lgtd1+","+lttd1));
        System.out.println("inPintOne:"+lgtd1+","+lttd1);
        params.add(new BasicNameValuePair("inPintTwo", lgtd2+","+lttd2));
        System.out.println("inPintTwo:"+lgtd2+","+lttd2);
        params.add(new BasicNameValuePair("inRiverLine", riverFilePath));
        System.out.println("inRiverLine:"+riverFilePath);
        params.add(new BasicNameValuePair("returnM", "false"));
        params.add(new BasicNameValuePair("returnZ", "false"));//不加后缀
        HttpEntity entity;
        CloseableHttpResponse response =null;
        try{
            entity = new UrlEncodedFormEntity(params, "UTF-8");
            httppost.setEntity(entity);

            response = httpclient.execute(httppost);

            if (response.getStatusLine().getStatusCode() == 200) {//如果状态码为200,就是正常返回
                HttpEntity responseEntity = response.getEntity();
                String responseContent = EntityUtils.toString(responseEntity, "UTF-8");
                if (!responseContent.contains("results")) {
                    throw new RuntimeException("调用GIS服务失败");
                }

                //字符串转json
                JSONObject jsonObject = JSONObject.fromObject(responseContent);

                String results = jsonObject.getString("results");
                // 首先把字符串转成 JSONArray  对象
                JSONArray jsonArray = JSONArray.fromObject(results);
                if (jsonArray.size() > 0) {

                    JSONObject jsonObject1 = jsonArray.getJSONObject(0);//把对象转成json对象

                    //获取区边界
                    String value = jsonObject1.getString("value");
                    JSONObject valueObject = JSONObject.fromObject(value);
                    String features = valueObject.getString("features");
                    // 首先把字符串转成 JSONArray  对象
                    JSONArray featuresJSONArray = JSONArray.fromObject(features);
                    if (featuresJSONArray.size() > 0) {

                        JSONObject featuresJsonObject = featuresJSONArray.getJSONObject(0);//把对象转成json对象

                        //获取区边界
                        String geometry = featuresJsonObject.getString("geometry");
                        JSONObject geometryObject2 = JSONObject.fromObject(geometry);
                        String paths = geometryObject2.getString("paths");
                        JSONArray ringsJsonArray1 = JSONArray.fromObject(paths);
                        if (ringsJsonArray1.size() > 0) {

                            return ringsJsonArray1;

                        }

                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                if (null!=response){
                    response.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }
}
