package com.zhangyue.ireader.toolslibrary.doubleclick;

import android.util.Log;
import android.view.View;

public class DoubleClickConfig {

    public static final String TAG = "DoubleClickConfig";

    private static long sLastCLickTime;

    public static boolean inDoubleClick(Object view, long duration) {
        long now = System.currentTimeMillis();
        boolean in = sLastCLickTime != 0 && (now - sLastCLickTime) <= duration;
        sLastCLickTime = now;
        Log.i(TAG, "click " + view.toString() + " inDoubleClick::" + in);
        return in;
    }

}
