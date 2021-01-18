/**
 * Copyright (c) 2020 essence All rights reserved.
 *
 * http://www.iessence.com.cn
 *
 * *版权所有，侵权必究！
 */

package com.essence.business.xqh.datasource.aspect;


import com.essence.business.xqh.datasource.annotation.DS;
import com.essence.business.xqh.datasource.config.DynamicContextHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 **多数据源，切面处理类
 *
 * @author Fengjd 421626365@qq.com
 * @since 2.3.5
 */
@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DataSourceAspect {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("@annotation(com.essence.business.xqh.datasource.annotation.DS) " +
            "|| @within(com.essence.business.xqh.datasource.annotation.DS)")
    public void dataSourcePointCut() {

    }

    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Class targetClass = point.getTarget().getClass();
        Method method = signature.getMethod();

        DS targetDataSource = (DS)targetClass.getAnnotation(DS.class);
        DS methodDataSource = method.getAnnotation(DS.class);
        if(targetDataSource != null || methodDataSource != null){
            String value;
            if(methodDataSource != null){
                value = methodDataSource.value();
            }else {
                value = targetDataSource.value();
            }

            DynamicContextHolder.push(value);
            logger.debug("set datasource is {}", value);
        }

        try {
            return point.proceed();
        } finally {
            DynamicContextHolder.poll();
            logger.debug("clean datasource");
        }
    }
}