package com.qmtv.lib_crash_intercept;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

/**
 * description：崩溃拦截
 *
 * @author 王旭
 *         create date：2018/3/7
 */
public final class CrashInterceptImpl {
    private static String TAG = "CrashInterceptImpl";

    private static Thread.UncaughtExceptionHandler sUncaughtExceptionHandler;
    /**
     * 标记位，避免重复安装卸载
     */
    private static boolean subThreadInstalled = false;
    private static boolean mainThreadInstalled = false;

    private CrashInterceptImpl() {
    }

    /**
     * 单独调用此方法则捕捉全局异常，如果同时调用了 installMainThread 方法则该方法只捕获子线程方法
     * 当线程抛出异常时会调用exceptionHandler.handlerException(Thread thread, Throwable throwable)
     * ExceptionHandler 回调可能发生在子线程
     *
     * 必须在主线程加载
     *
     * @param exceptionHandler 崩溃回调
     */
    public static void installGlobalCrashIntercept(final ExceptionHandler exceptionHandler) {
        if (subThreadInstalled) {
            return;
        }

        subThreadInstalled = true;

        sUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                CrashReport.postCatchedException(e);

                if (exceptionHandler != null) {
                    exceptionHandler.handlerException(t, e);
                }
            }
        });
    }

    /**
     * 当主线程抛出异常时会调用exceptionHandler.handlerException(Thread thread, Throwable throwable)
     * ExceptionHandler 回调可能发生在子线程
     *
     * 必须在主线程加载
     *
     * @param exceptionHandler 崩溃回调
     */
    public static void installMainThread(final ExceptionHandler exceptionHandler) {
        if (mainThreadInstalled) {
            return;
        }

        mainThreadInstalled = true;

        while (true) {
            try {
                Looper.loop();
            } catch (Throwable e) {
                if (e instanceof QuitCockroachException) {
                    return;
                }

                Log.e(TAG, "install: ", e);
                CrashReport.postCatchedException(e);

                if (exceptionHandler != null) {
                    exceptionHandler.handlerException(Looper.getMainLooper().getThread(), e);
                }
            }
        }
    }

    /**
     * 卸载主线程崩溃拦截,仅能在主线程卸载
     */
    @SuppressWarnings("unused")
    public static synchronized void uninstallMainThread() {
        if (!mainThreadInstalled) {
            return;
        }

        mainThreadInstalled = false;
        // 卸载后恢复默认的异常处理逻辑，否则主线程再次抛出异常后将导致ANR，并且无法捕获到异常位置
        Thread.setDefaultUncaughtExceptionHandler(sUncaughtExceptionHandler);
    }

    /**
     * 卸载子线程崩溃拦截
     */
    @SuppressWarnings("unused")
    public static synchronized void uninstallSubThread() {
        if (!subThreadInstalled) {
            return;
        }

        subThreadInstalled = false;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                // 主线程抛出异常，迫使 while (true) {}结束
                throw new QuitCockroachException("Quit CrashInterceptImpl.....");
            }
        });
    }

    public interface ExceptionHandler {
        void handlerException(Thread thread, Throwable throwable);
    }

    private static class QuitCockroachException extends RuntimeException {
        QuitCockroachException(String message) {
            super(message);
        }
    }
}
