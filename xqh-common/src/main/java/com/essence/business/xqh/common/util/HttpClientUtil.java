package com.essence.business.xqh.common.util;

import net.sf.json.JSONObject;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.*;

/**
 * HttpClient GET POST请求封装工具
 *
 * @author lxf
 *
 *         2018年5月11日 上午9:18:52
 */
public class HttpClientUtil {
	private static final CloseableHttpClient httpclient;
	public static final String CHARSET = "UTF-8";

	static {
		RequestConfig config = RequestConfig.custom().setConnectTimeout(5000).setSocketTimeout(5000).build();
		httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
	}

    /**
     * Post HttpClient请求
     * @param httpClient 请求CloseableHttpClient 对象
     * @param url 请求路径地址
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String sendPost(CloseableHttpClient httpClient,String url,Map<String, Object> paramMap) throws ClientProtocolException, IOException{
        HttpPost httPost = new HttpPost(url);
        httPost.setHeader("Content-Type", "application/json;charset=UTF-8");
        //登录请求参数采用json格式
        StringEntity entity=null;
        if(paramMap!=null && paramMap.size()>0) {
            JSONObject jsonParam = JSONObject.fromObject(paramMap);
            entity= new StringEntity(jsonParam.toString(), "utf-8");//解决中文乱码问题
        }else {
            entity= new StringEntity("utf-8");//解决中文乱码问题
        }
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        httPost.setEntity(entity);
        CloseableHttpResponse httpResponse = httpClient.execute(httPost);
        HttpEntity httpEntity = httpResponse.getEntity();
        String responseJsonStr = EntityUtils.toString(httpEntity, "UTF-8");
        httPost.releaseConnection();
        return responseJsonStr;
    }


    public static String sendGet(CloseableHttpClient httpClient,String url) throws ClientProtocolException, IOException{
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        String responseContent = EntityUtils.toString(httpEntity, "UTF-8");
        return responseContent;
    }

    public static String sendGetHeard(CloseableHttpClient httpClient,String url) throws ClientProtocolException, IOException{
    	HttpGet httpGet = new HttpGet(url);
//    	httpGet.setHeader("Content-Type", "application/json;charset=UTF-8");
    	// 浏览器表示
        httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.7.6)");
        // 传输的类型
        httpGet.addHeader("Content-Type", "application/x-www-form-urlencoded");
    	CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
    	HttpEntity httpEntity = httpResponse.getEntity();
    	String responseContent = EntityUtils.toString(httpEntity, "UTF-8");
    	return responseContent;
    }



	/**
	 * HTTP Get 获取内容
	 *
	 * @param url 请求的url地址
	 *            ?之前的地址
	 * @param params 请求的参数
	 *
	 * @return 页面内容
	 */
	public static String sendGet(String url, Map<String, Object> params)
			throws ParseException, UnsupportedEncodingException, IOException {

		if (params != null && !params.isEmpty()) {

			List<NameValuePair> pairs = new ArrayList<NameValuePair>(params.size());

			for (String key : params.keySet()) {
				pairs.add(new BasicNameValuePair(key, params.get(key).toString()));
			}
			url += "?" + EntityUtils.toString(new UrlEncodedFormEntity(pairs), CHARSET);
		}

		HttpGet httpGet = new HttpGet(url);
		CloseableHttpResponse response = httpclient.execute(httpGet);
		int statusCode = response.getStatusLine().getStatusCode();
		if (statusCode != 200) {
			httpGet.abort();
			throw new RuntimeException("HttpClient,error status code :" + statusCode);
		}
		HttpEntity entity = response.getEntity();
		String result = null;
		if (entity != null) {
			result = EntityUtils.toString(entity, "utf-8");
			EntityUtils.consume(entity);
			response.close();
			System.out.println(result);
			return result;
		} else {
			return null;
		}
	}

	/**
     * 上传文件 (单个文件) 及参数
     *
     * @param serverUrl 访问服务器地址
     *            服务器地址
     *
     * @param serverFieldName 文件提交参数name标识
     * @param params 附件其它参数
     * @return
     * @throws Exception
     */
    public static String sendPost(String serverUrl, File file,
            String serverFieldName, Map<String, Object> params)
            throws Exception {
        String respStr = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(serverUrl);

            FileBody binFileBody = new FileBody(file);

            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setCharset(Charset.forName("UTF-8"));
            //以浏览器兼容模式运行，防止文件名乱码。
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            // add the file params
            multipartEntityBuilder.addPart(serverFieldName, binFileBody);
            // 设置上传的其他参数
            setUploadParams(multipartEntityBuilder, params);
            HttpEntity reqEntity = multipartEntityBuilder.build();
            httppost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                respStr = getRespString(resEntity);
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        System.out.println("resp=" + respStr);
        return respStr;
    }

    /**
     * 上传文件 (多文件组  多个文件组 每个文件组多个文件) 及参数
     *
     * @param serverUrl 访问服务器地址
     *            服务器地址
     *
     *
     * @param params 附件其它参数
     * @return
     * @throws Exception
     */
    public static String sendPostFileArray(String serverUrl, Map<String, List<String>> fileMap, Map<String, Object> params)
            throws Exception {
        String respStr = null;
        Map<String, List<FileBody>> fileBodyMap=null;
        List<FileBody> fileBodyList=null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httppost = new HttpPost(serverUrl);
            Set<String> keys = fileMap.keySet();
            if(keys!=null&&keys.size()>0){
            	fileBodyMap=new HashMap<>();
            	for (String key : keys) {
            		fileBodyList=new ArrayList<>();
            		List<String> list = fileMap.get(key);
            		for (String filePath : list) {
            			FileBody fileBody = new FileBody(new File(filePath),ContentType.create("text/plain","UTF-8"));
            			fileBodyList.add(fileBody);
					}
            		fileBodyMap.put(key, fileBodyList);
            	}
            }
            MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create().setCharset(Charset.forName("UTF-8"));
            //以浏览器兼容模式运行，防止文件名乱码。
            multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            // add the file params
            Set<String> keySet = fileBodyMap.keySet();
            for (String key : keySet) {
            	List<FileBody> list = fileBodyMap.get(key);
            	for (FileBody fileBody : list) {
            		multipartEntityBuilder.addPart(key, fileBody);
				}
			}
            // 设置上传的其他参数
            setUploadParams(multipartEntityBuilder, params);
            HttpEntity reqEntity = multipartEntityBuilder.build();
            httppost.setEntity(reqEntity);
            CloseableHttpResponse response = httpclient.execute(httppost);
            try {
                System.out.println(response.getStatusLine());
                HttpEntity resEntity = response.getEntity();
                respStr = getRespString(resEntity);
                EntityUtils.consume(resEntity);
            } finally {
                response.close();
            }
        } finally {
            httpclient.close();
        }
        System.out.println("resp=" + respStr);
        return respStr;
    }

    /**
     * 将返回结果转化为String
     *
     * @param entity
     * @return
     * @throws Exception
     */
    private static String getRespString(HttpEntity entity) throws Exception {
        if (entity == null) {
            return null;
        }
        InputStream is = entity.getContent();
        StringBuffer strBuf = new StringBuffer();
        byte[] buffer = new byte[4096];
        int r = 0;
        while ((r = is.read(buffer)) > 0) {
            strBuf.append(new String(buffer, 0, r, "UTF-8"));
        }
        return strBuf.toString();
    }

	/**
     * 设置上传文件时所附带的其他参数
     * 此处
     * @param multipartEntityBuilder
     * @param params
     */
	private static void setUploadParams(MultipartEntityBuilder multipartEntityBuilder,Map<String, Object> params) {
        if (params != null && params.size() > 0) {
            Set<String> keys = params.keySet();
            for (String key : keys) {
                multipartEntityBuilder
                        .addPart(key, new StringBody(params.get(key).toString(),
                                ContentType.create("text/plain", Consts.UTF_8)));
            }
        }
    }
}
