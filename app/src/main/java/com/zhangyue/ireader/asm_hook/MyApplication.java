package com.zhangyue.ireader.asm_hook;

import android.app.Application;
import android.content.Context;

import com.zhangyue.ireader.toolslibrary.ConfigGlobal;

public class MyApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ConfigGlobal.getInstance().setContext(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
