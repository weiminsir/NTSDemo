package com.ulang.nts;

import android.content.Context;
import android.content.Intent;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by ken on 19/7/15.
 */
public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final Context myContext;
    public static final boolean DEBUG = Boolean.parseBoolean("true");

    public UncaughtExceptionHandler(Context context) {
        myContext = context;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable exception) {
        final StringWriter stackTrace = new StringWriter();
        exception.printStackTrace(new PrintWriter(stackTrace));
        System.err.println(stackTrace);

        Intent intent = new Intent(myContext, BugActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("stackTrace", stackTrace.toString());
        myContext.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(10);
    }
}
