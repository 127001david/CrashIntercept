package com.qmtv.crashintercept;

/**
 * description：模拟几种可以导致 crash 的情况
 *
 * @author 王旭
 * create date：2018/9/14
 */
public class CrashMockUtil {

    public static void mockNullPointer() {
        Object o = null;
        o.toString();
    }

    public static void mockOutOfMemory() {
        long[][] outArray = new long[10000][10000];
    }
}
