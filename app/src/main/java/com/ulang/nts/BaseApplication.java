package com.ulang.nts;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;


/**
 * Created by Administrator on 2017/12/6 0006.
 */

public class BaseApplication extends Application {

    public static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this));

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
        // Normal app init code...

    }

}
