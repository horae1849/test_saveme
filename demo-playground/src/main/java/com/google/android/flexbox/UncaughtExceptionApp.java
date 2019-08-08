package com.google.android.flexbox;

import android.app.Application;
import android.util.Log;

public class UncaughtExceptionApp extends Application {
    private String TAG = "ERROR1849";
    private Thread.UncaughtExceptionHandler androidDefaultUEH;
    private UncaughtExceptionHandler uncaughtExceptionHandler;

    public UncaughtExceptionHandler getUncaughtExceptionHandler() {
        return uncaughtExceptionHandler;
    }

    @Override
    public void onCreate() {
        androidDefaultUEH = Thread.getDefaultUncaughtExceptionHandler();
        uncaughtExceptionHandler = new UncaughtExceptionHandler();

        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
        super.onCreate();
    }

    public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            // 이곳에서 로그를 남긴다
            Log.e(TAG, "error -----------------> " + ex.toString());
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(10);
        }
    }
}
