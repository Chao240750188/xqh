package com.essence.handler.interceptor;

import com.essence.euauth.common.DefaultResponse;
import com.essence.euauth.common.SysConstant;
import com.essence.euauth.common.response.ResponseEnum;
import com.essence.euauth.entity.ValidRequestEntity;
import com.essence.euauth.entity.ValidResponse;
import com.essence.euauth.feign.AuthValidRemoteFeign;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Set;

/**
 * @ClassName AuthInterceptor
 * @Description 认证拦截器
 * @Author zhichao.xing
 * @Date 2019/7/31 14:44
 * @Version 1.0
 **/
@Slf4j
@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private AuthValidRemoteFeign authValidRemoteFeign;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        DefaultResponse resp = new DefaultResponse();
        String servletPath = request.getServletPath();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String s = headerNames.nextElement();
            log.debug("一对key={}", s);
        }
        String token = request.getHeader(SysConstant.REQUEST_HEADER_TOKEN);
        if (StringUtils.isEmpty(token)) {
            resp.unauthorizedReturn(response, "token为空");
            return false;
        }
        String projectId = request.getHeader(SysConstant.REQUEST_HEADER_PROJECT_ID);
        if (StringUtils.isEmpty(projectId)) {
            resp.unauthorizedReturn(response, "projectId为空");
            return false;
        }

        ValidRequestEntity validRequestEntity = new ValidRequestEntity();
        validRequestEntity.setProjectId(projectId);
        validRequestEntity.setToken(token);
        validRequestEntity.setRequestUri(servletPath);
        ValidResponse valid = authValidRemoteFeign.valid(validRequestEntity);
        if (!ResponseEnum.server_ok.getCode().equals(valid.getCode())) {
            resp.unauthorizedReturn(response, valid.getInfo());
            return false;
        }
        LinkedHashMap result = (LinkedHashMap) valid.getResult();
//        printLog(result);
        HttpSession session = request.getSession();
        session.setAttribute(SysConstant.CURRENT_USER_ID, result.get("userId"));
        session.setAttribute(SysConstant.CURRENT_LOGIN_NAME, result.get("loginName"));
        session.setAttribute(SysConstant.CURRENT_USERNAME, result.get("userName"));
        session.setAttribute(SysConstant.CURRENT_UNIT_NAME, result.get("corpName"));
        session.setAttribute(SysConstant.CURRENT_UNIT_ID, result.get("corpId"));
        session.setAttribute(SysConstant.CURRENT_UNIT_LEVEL, result.get("corpLevel"));
        session.setAttribute(SysConstant.CURRENT_USER_MOBILE, result.get("mobilephone"));
        return true;
    }

    /**
     * 获取数据输出
     *
     * @param
     * @return void
     * @Author zhichao.xing
     * @Description 获取数据输出
     * @Date 10:47 2019/9/28
     **/
    private void printLog(LinkedHashMap result) {
        Set set = result.keySet();
        set.forEach(it -> {
            log.info("循环输出={},{}", it, result.get(it));
        });
    }

}
