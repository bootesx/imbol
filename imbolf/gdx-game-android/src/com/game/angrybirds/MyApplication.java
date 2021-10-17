package com.game.angrybirds;

import android.app.Application;
import android.util.*;

public class MyApplication extends Application {
    private static MyApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        sInstance = this;

        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static MyApplication getInstance() {
        return sInstance;
    }
}
