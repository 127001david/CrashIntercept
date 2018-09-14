package com.qmtv.lib_crash_intercept.base;

import com.qmtv.lib_crash_intercept.ActivityBind;

/**
 * description：各 module 需创建 ResolveCrash 类并实现该接口
 * ，崩溃模块会反射接口中的方法以实现各模块独立处理异常。
 *
 * @author 王旭
 *         create date：2018/3/13
 */
public interface IResoleCrash {
    void resolveCrash(ActivityBind errorActivityBind, Thread thread, Throwable throwable);
}
