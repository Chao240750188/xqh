package com.essence.handler;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @ClassName RequestAspect
 * @Description TODO
 * @Author zhichao.xing
 * @Date 2020/7/23 9:33
 * @Version 1.0
 **/
@Component
@Aspect
@Slf4j
@Order(-98)
public class RequestAspect {

    @Pointcut("execution(public * com.essence..*Controller.*(..))")
    public void pointcut() {

    }

    @Around("pointcut()")
    public Object handle(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean debugEnabled = log.isDebugEnabled();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //IP地址
        String ipAddr = getRemoteHost(request);
        String url = request.getRequestURL().toString();
        String reqParam = preHandle(joinPoint, request);
        long begin = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        String respParam = postHandle(result);
        log.info("请求IP:[{}], 耗时：{}ms,请求URL:[{}]", ipAddr, (System.currentTimeMillis() - begin), url);
        if (debugEnabled) {
            log.debug("请求IP:[{}],请求URL:[{}],请求参数:[{}]", ipAddr, url, reqParam);
            log.debug("请求IP:[{}], 耗时：{}ms,请求URL:[{}],返回参数:[{}]", ipAddr, (System.currentTimeMillis() - begin), url, respParam);
        }
        return result;
    }

    /**
     * 入参数据
     *
     * @param joinPoint
     * @param request
     * @return
     */
    private String preHandle(ProceedingJoinPoint joinPoint, HttpServletRequest request) {

        String reqParam = "";
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        Annotation[] annotations = targetMethod.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(RequestMapping.class) ||
                    annotation.annotationType().equals(GetMapping.class) ||
                    annotation.annotationType().equals(PostMapping.class) ||
                    annotation.annotationType().equals(PutMapping.class) ||
                    annotation.annotationType().equals(DeleteMapping.class)) {
                reqParam = JSON.toJSONString(request.getParameterMap());
                break;
            }
        }
        return reqParam;
    }

    /**
     * 返回数据
     *
     * @param retVal
     * @return
     */
    private String postHandle(Object retVal) {
        if (null == retVal) {
            return "";
        }
        return JSON.toJSONString(retVal);
    }


    /**
     * 获取目标主机的ip
     *
     * @param request
     * @return
     */
    private String getRemoteHost(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
