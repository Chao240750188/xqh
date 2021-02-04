package com.essence.business.xqh.common.thread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义一个线程工厂
 */
public class HandlerThreadFactory implements ThreadFactory {

    private static AtomicInteger count = new AtomicInteger(0);


    public HandlerThreadFactory() {
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = new Thread(r);
        String threadName = HandlerThreadFactory.class.getSimpleName() + count.addAndGet(1);
        t.setName(threadName);
        t.setUncaughtExceptionHandler(new MyExceptionHandler());
        return t;
    }


}