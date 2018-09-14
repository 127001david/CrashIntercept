package com.qmtv.lib_crash_intercept;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;

import com.qmtv.lib_crash_intercept.base.IResoleCrash;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * description：对拦截下的异常做处理
 *
 * @author 王旭
 * create date：2018/3/12
 */
public class ResolveCrash implements CrashInterceptImpl.ExceptionHandler {
    private final String TAG = "ResolveCrash";
    private Activity mCurrentActivity;
    private ArrayMap<String, ActivityBind> mActivityMap;

    ResolveCrash(ArrayMap<String, ActivityBind> activityMap) {
        mActivityMap = activityMap;
    }

    void setCurrentActivity(Activity currentActivity) {
        mCurrentActivity = currentActivity;
    }

    @Override
    public void handlerException(final Thread thread, final Throwable throwable) {
        try {
            Log.e(TAG, "handlerException: ", throwable);

            StringWriter stringWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(stringWriter);
            throwable.printStackTrace(writer);
            StringBuffer buffer = stringWriter.getBuffer();

            String errorActivity = null;

            String[] split = buffer.toString().split("\n");
            for (String s : split) {
                s = s.trim();
                if (s.startsWith("com.qmtv.crashintercept")) {
                    Pattern p = Pattern.compile("\\(.*?\\)");
                    Matcher m = p.matcher(s);
                    while (m.find()) {
                        String className = m.group();
                        if (className.contains("Activity")) {
                            errorActivity = s.substring(2, s.indexOf("Activity")) + "Activity";
                            break;
                        }
                    }
                }
            }

            if (null != mCurrentActivity && !mCurrentActivity.isFinishing()) {
                final String finalErrorActivity = errorActivity;
                mCurrentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resolveCrash(finalErrorActivity, thread, throwable);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "handlerException: ", e);
        }
    }

    private void resolveCrash(String errorActivity, Thread thread, Throwable throwable) {
        if (TextUtils.isEmpty(errorActivity) && null != mCurrentActivity) {
            errorActivity = mCurrentActivity.getClass().getName();
        }

        if (!TextUtils.isEmpty(errorActivity)) {
            String activityName = errorActivity.trim();
            ActivityBind activityBind = mActivityMap.get(activityName);

            if (null == activityBind || null == activityBind.activity) {
                return;
            }

            Log.i(TAG, "resolveCrash: happen activity " + activityName + " on "
                    + activityBind.getState());

            if (throwable instanceof OutOfMemoryError) {
                AlertDialog.Builder builder = new AlertDialog.Builder(activityBind.activity);

                builder.setTitle("内存空间不足")
                        .setMessage("请清理释放空间后重新打开app")
                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                new Handler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        for (String s : mActivityMap.keySet()) {
                                            if (null != mActivityMap.get(s)) {
                                                Activity activity = mActivityMap.get(s).activity;
                                                if (null != activity && !activity.isFinishing()) {
                                                    activity.finish();
                                                }
                                            }
                                            mActivityMap.remove(s);
                                        }
                                        System.exit(0);
                                    }
                                });
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();

                return;
            }

            String resolveClassName = activityName.substring(0, activityName.lastIndexOf(".") + 1)
                    + "resolve_crash" + ".ResolveCrash";

            IResoleCrash resolveCrashObject = null;
            Class<?> resolveCrashClass;
            try {
                resolveCrashClass = Class.forName(resolveClassName);
                resolveCrashObject = (IResoleCrash) resolveCrashClass.newInstance();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                activityBind.activity.finish();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                activityBind.activity.finish();
            } catch (InstantiationException e) {
                e.printStackTrace();
                activityBind.activity.finish();
            }

            if (null != resolveCrashObject) {
                try {
                    resolveCrashObject.resolveCrash(activityBind, thread, throwable);
                } catch (Exception e) {
                    e.printStackTrace();
                    CrashReport.postCatchedException(e);
                    activityBind.activity.finish();
                }
            }
        }
    }
}
