package com.game.angrybirds;

import android.app.Application;
import android.util.*;

public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
		Log.v("myapp", "onCreate");
        sInstance = this;
        // 在这里为应用设置异常处理，然后程序才能获取未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static MyApplication getInstance() {
        return sInstance;
    }
}

