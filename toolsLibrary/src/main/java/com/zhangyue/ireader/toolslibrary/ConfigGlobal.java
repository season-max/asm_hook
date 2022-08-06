package com.zhangyue.ireader.toolslibrary;

public class ConfigGlobal {

    private String mStoreDirectory;

    private static class Holder {
        private static final ConfigGlobal mInstance = new ConfigGlobal();
    }

    private ConfigGlobal() {

    }

    public static ConfigGlobal getInstance() {
        return Holder.mInstance;
    }


    public void setStoreDirectory(String path) {
        this.mStoreDirectory = path;
    }

    public String getStoreDirectory() {
        return mStoreDirectory;
    }
}
