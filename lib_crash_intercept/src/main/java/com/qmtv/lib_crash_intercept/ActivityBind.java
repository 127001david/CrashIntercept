package com.qmtv.lib_crash_intercept;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * description：实体类用于存储
 * Activity 实例
 * Activity 所处生命周期
 * Activity 携带数据
 *
 * @author 王旭
 *         create date：2018/3/12
 */
public class ActivityBind {
    public static final String ON_CREATED = "onActivityCreated";
    public static final String ON_STARTED = "onActivityStarted";
    public static final String ON_RESUMED = "onActivityResumed";
    public static final String ON_PAUSED = "onActivityPaused";
    public static final String ON_STOPPED = "onActivityStopped";
    public static final String ON_SAVE_INSTANCE_STATE = "onActivitySaveInstanceState";
    public static final String ON_DESTROYED = "onActivityDestroyed";

    public Activity activity;
    public Bundle bundle;
    private String state;

    @StringDef({ON_CREATED, ON_STARTED, ON_RESUMED, ON_PAUSED, ON_STOPPED, ON_SAVE_INSTANCE_STATE, ON_DESTROYED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Type {
    }

    public String getState() {
        return state;
    }

    public void setState(@Type String state) {
        this.state = state;
    }
}
