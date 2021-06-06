package com.essence.business.xqh.web.thirdLogin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.essence.business.xqh.common.util.HttpUtil;
import org.apache.http.client.methods.HttpPost;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
@Controller
@RequestMapping("/thirdDo")
public class ThirdController {


    private final static String appId = "2ba002fe3bc03825c2fe3e03930e26b1";

    private final static String secret = "4427B60EC0FF6055ABA7933759A993AE687B5A2F9B68758973F14CE7FCE677A5";

//    @Value("#{'${third_account}'.split(',')}")
//    List<String> thirdAccount;

    @Value("${thirdLoginUrl}")//http://192.168.1.111:8888/login.jsp
    private String thirdLoginUrl;
    @Value("${thirdTokenUrl}")
    private String thirdTokenUrl;
    @Value("${thirdGetUserInfoUrl}")//"http://门户认证地址/gateway/v1/findUserByToken.do"
    private String thirdGetUserInfoUrl;
    @Value("${thirdSM2decodeUrl}")
    private String thirdSM2decodeUrl;
    @Value("${ourRedirectUrl}")
    private String ourRedirectUrl;



    @RequestMapping(value = "/redirectAirport", method = RequestMethod.GET)
    public void redirectAirport(HttpServletRequest request, HttpServletResponse response)throws Exception{
        //或者String ticket
        Object ticket = request.getParameter("ticket");//票据
        if (ticket == null){
            System.out.println("ticket票据不存在，请重新登陆");
            try {
                response.sendRedirect(thirdLoginUrl);
            }catch (Exception e){
                System.out.println("单点登录跳转失败");
                e.printStackTrace();
            }
        }


        //获取token值

        HttpUtil httpUtil = new HttpUtil();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("appId",appId);
        jsonObject.put("ticket",ticket);

        HttpPost post = new HttpPost(thirdTokenUrl);
        String returnStr = httpUtil.doPosts(post, jsonObject.toJSONString());
        JSONObject returnJson = null;
        try {
            returnJson = JSON.parseObject(returnStr);

        }catch (Exception e){
            System.out.println("获取token，解析json信息失败");
            e.printStackTrace();
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }

        if (returnJson == null ){
            System.out.println("获取token，返回json为null");
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }

        String code = returnJson.get("code") == null? null:returnJson.get("code")+"";
        String token = null;
        if (!"10000".equals(code)){//可能ticket失效，被重复使用
            System.out.println("获取token的json 返回code编码不是10000（成功）"+returnJson.get("message"));
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }

        if (returnJson.getJSONObject("data")!=null){
            token = returnJson.getJSONObject("data").getString("token");
        }
        if (token == null){
            System.out.println("获取token，token为null，但是code编码是正常编码）");
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }
        //todo 根据token获取登陆账号信息
        HttpPost post1 = new HttpPost(thirdGetUserInfoUrl);
        jsonObject = new JSONObject();
        jsonObject.put("appId",appId);
        jsonObject.put("token",token);

        returnStr = httpUtil.doPosts(post1,jsonObject.toJSONString());
        returnJson = null;
        try {
            returnJson = JSON.parseObject(returnStr);
        }catch (Exception e){
            System.out.println("获取用户信息，解析json信息失败");
            e.printStackTrace();
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }
        if (returnJson == null ){
            System.out.println("获取用户信息，返回json为null");
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }
        //加密后的用户信息
        String data = null;
        if (!"10000".equals(returnJson.getString("code"))){
            System.out.println("获取用户信息 返回code编码不是10000（成功）"+returnJson.get("message"));
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }

        data = returnJson.getString("data");//加密的data

        if (data == null){
            System.out.println("获取用户信息，data为null，但是code编码是正常编码）");
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }

        //todo  解密
        HttpPost post2 = new HttpPost(thirdSM2decodeUrl);
        jsonObject = new JSONObject();
        jsonObject.put("appId",appId);
        jsonObject.put("decodeText",data);
        jsonObject.put("decodeKey",secret);

        returnStr = httpUtil.doPosts(post2,jsonObject.toJSONString());
        returnJson = null;
        try {
            returnJson = JSON.parseObject(returnStr);
        }catch (Exception e){
            System.out.println("解密用户信息，解析json信息失败");
            e.printStackTrace();
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }
        if (returnJson == null ){
            System.out.println("解密用户信息，返回json为null");
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }
        //解密后的用户信息
        String account = null;
        if (!"10000".equals(returnJson.getString("code"))){
            System.out.println("解密用户信息 返回code编码不是10000（成功）"+returnJson.get("message"));
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }
        if (returnJson.getJSONObject("data") != null){
            account = returnJson.getJSONObject("data").getString("account");
        }
        if (account == null){
            System.out.println("解密用户信息，account为null，但是code编码是正常编码）");
            response.sendRedirect(thirdLoginUrl);//跳转
            return;
        }

        //拿到
        response.sendRedirect(ourRedirectUrl+"?account="+account);//跳转

    }
}
