package tv.panda.testspeex;

import android.app.Application;

public class BaseApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler(this));
    }
}
