package com.essence.jdbc;

public class ThreadCallStackUtil {

    public static String printThreadCallStack() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stackTrace) {
            System.out.println(e.getClassName() + "\t"
                    + e.getMethodName() + "\t" + e.getLineNumber());
        }
        StackTraceElement log = stackTrace[1];
        String tag = null;
        for (int i = 1; i < stackTrace.length; i++) {
            StackTraceElement e = stackTrace[i];
            if (!e.getClassName().equals(log.getClassName())) {
                tag = e.getClassName() + "." + e.getMethodName();
                break;
            }
        }
        if (tag == null) {
            tag = log.getClassName() + "." + log.getMethodName();

        }
        System.out.println(tag);
        return tag;
    }
}
