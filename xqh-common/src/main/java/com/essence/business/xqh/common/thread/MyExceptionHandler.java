package com.essence.business.xqh.common.thread;

import java.lang.Thread.UncaughtExceptionHandler;

/**
 * 用于捕获异常---捕获的是uncheckedException
 */
public class MyExceptionHandler implements UncaughtExceptionHandler {

    //处理线程中unchecked exception
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("捕获到异常：" + e);

    }
}
