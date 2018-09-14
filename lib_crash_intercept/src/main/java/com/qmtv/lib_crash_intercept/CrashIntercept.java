package com.qmtv.lib_crash_intercept;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;

/**
 * description：初始化崩溃拦截组件
 *
 * @author 王旭
 * create date：2018/3/12
 */
public class CrashIntercept {
    private final String TAG = "CrashIntercept";

    /**
     * 存储当前存活的 Activity ,以 Activity 完整类名为 key 值
     * ，存储 Activity 栈中的 Activity 实例/所处生命周期/携带数据。
     */
    private ArrayMap<String, ActivityBind> mActivityMap = new ArrayMap<>();

    private ResolveCrash mResolveCrash;

    /**
     * 单例
     */
    private static class SingleInstance {
        static CrashIntercept mCrashIntercept = new CrashIntercept();
    }

    private CrashIntercept() {
    }

    /**
     * 获取单例
     */
    public static CrashIntercept getInstance() {
        return SingleInstance.mCrashIntercept;
    }

    public void init(Application application) {
        if (null != mResolveCrash) {
            return;
        }

        mResolveCrash = new ResolveCrash(mActivityMap);

        application.registerActivityLifecycleCallbacks(
                new Application.ActivityLifecycleCallbacks() {
                    @Override
                    public void onActivityCreated(Activity activity, Bundle bundle) {
                        Log.d(TAG, "onActivityCreated: " + activity.getClass().getSimpleName());
                        ActivityBind activityBind = new ActivityBind();

                        activityBind.activity = activity;
                        activityBind.bundle = activity.getIntent().getExtras();
                        activityBind.setState(ActivityBind.ON_CREATED);

                        mActivityMap.put(activity.getClass().getName(), activityBind);

                        mResolveCrash.setCurrentActivity(activity);
                    }

                    @Override
                    public void onActivityStarted(Activity activity) {
                        Log.d(TAG, "onActivityStarted: " + activity.getClass().getSimpleName());
                        ActivityBind activityBind = mActivityMap.get(activity.getClass().getName());
                        if (null != activityBind) {
                            activityBind.setState(ActivityBind.ON_STARTED);
                        }
                    }

                    @Override
                    public void onActivityResumed(Activity activity) {
                        Log.d(TAG, "onActivityResumed: " + activity.getClass().getSimpleName());
                        ActivityBind activityBind = mActivityMap.get(activity.getClass().getName());
                        if (null != activityBind) {
                            activityBind.setState(ActivityBind.ON_RESUMED);
                        }

                        mResolveCrash.setCurrentActivity(activity);
                    }

                    @Override
                    public void onActivityPaused(Activity activity) {
                        Log.d(TAG, "onActivityPaused: " + activity.getClass().getSimpleName());
                        ActivityBind activityBind = mActivityMap.get(activity.getClass().getName());
                        if (null != activityBind) {
                            activityBind.setState(ActivityBind.ON_PAUSED);
                        }
                    }

                    @Override
                    public void onActivityStopped(Activity activity) {
                        Log.d(TAG, "onActivityStopped: " + activity.getClass().getSimpleName());
                        ActivityBind activityBind = mActivityMap.get(activity.getClass().getName());
                        if (null != activityBind) {
                            activityBind.setState(ActivityBind.ON_STOPPED);
                        }
                    }

                    @Override
                    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
                        // 存在 Activity 并没有 onSaveInstanceState 却回调此方法的可能，加屏蔽
                        if (activity.isFinishing()) {
                            Log.d(TAG, "onActivitySaveInstanceState: "
                                    + activity.getClass().getSimpleName());
                            ActivityBind activityBind = mActivityMap.get(
                                    activity.getClass().getName());
                            if (null != activityBind) {
                                activityBind.setState(ActivityBind.ON_SAVE_INSTANCE_STATE);
                                mActivityMap.remove(activity.getClass().getName());
                            }
                        }
                    }

                    @Override
                    public void onActivityDestroyed(Activity activity) {
                        Log.d(TAG, "onActivityDestroyed: " + activity.getClass().getSimpleName());
                        mActivityMap.remove(activity.getClass().getName());
                    }
                });
    }

    /**
     * 需在 Bugly 之前加载，以便 Bugly 收集 Native 异常
     */
    public void installGlobalCrashIntercept() {
        CrashInterceptImpl.installGlobalCrashIntercept(mResolveCrash);
    }

    /**
     * 需在 Application.onCreate() 最后加载，因为崩溃拦截的实现是阻塞的
     */
    public void installMainThreadCrashIntercept() {
        CrashInterceptImpl.installMainThread(mResolveCrash);
    }
}
