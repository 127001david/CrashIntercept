package com.qmtv.crashintercept;

import android.app.Application;
import android.util.Log;

import com.qmtv.lib_crash_intercept.CrashIntercept;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * description：
 *
 * @author 王旭
 * create date：2018/9/14
 */
public class MyApplication extends Application {
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            CrashIntercept.getInstance().init(this);
            // 启动全局拦截
            CrashIntercept.getInstance().installGlobalCrashIntercept();
            CrashReport.initCrashReport(getApplicationContext(), "4124fcdbc7", false);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }

        try {
            // 启动主线程拦截，此步骤必须放在 Application.onCreate() 的最后
            // ，因为一旦启动 CrashIntercept 将开启 Looper.loop()，线程将被阻塞，后面的操作都不会被执行。
            CrashIntercept.getInstance().installMainThreadCrashIntercept();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: ", e);
        }
    }
}
