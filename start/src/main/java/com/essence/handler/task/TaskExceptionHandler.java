package com.essence.handler.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 *
 * @author xc
 * @date 2021/01/21 19:43
 **/
public class TaskExceptionHandler implements AsyncUncaughtExceptionHandler {

	Logger log = LoggerFactory.getLogger(TaskExceptionHandler.class);

	@Override
	public void handleUncaughtException(Throwable t, Method m, Object... params) {
		System.out.println("task handler exception :{}"+t.getMessage());
		log.error("task handler exception :{}", t.getMessage());
	}
}
