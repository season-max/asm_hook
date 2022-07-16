package com.zhangyue.ireader.toolslibrary;

import android.content.Context;

public class ConfigGlobal {

    private Context context;

    private static class Holder {
        private static final ConfigGlobal mInstance = new ConfigGlobal();
    }

    private ConfigGlobal() {

    }

    public static ConfigGlobal getInstance() {
        return Holder.mInstance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

}
