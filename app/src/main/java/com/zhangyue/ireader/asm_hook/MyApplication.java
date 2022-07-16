package com.zhangyue.ireader.asm_hook;

import android.app.Application;

import com.zhangyue.ireader.toolslibrary.ConfigGlobal;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ConfigGlobal.getInstance().setContext(this);
    }
}
