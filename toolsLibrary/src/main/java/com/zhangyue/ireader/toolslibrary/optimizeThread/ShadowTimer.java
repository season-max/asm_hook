package com.zhangyue.ireader.toolslibrary.optimizeThread;

import android.util.Log;

import java.util.Timer;
import java.util.concurrent.atomic.AtomicInteger;

public class ShadowTimer extends Timer {

    /**
     * 标记，防止重复添加前缀
     */
    static final String MARK = "\u200B";

    public ShadowTimer(String prefix) {
        super(prefix + "#Timer-" + nextSerialNumber.getAndIncrement());
    }

    public ShadowTimer(boolean isDaemon, String prefix) {
        super(prefix + "#Timer-" + nextSerialNumber.getAndIncrement(), isDaemon);
    }

    public ShadowTimer(String name, String prefix) {
        super(makeTimerName(name, prefix));
    }

    public ShadowTimer(String name, boolean isDaemon, String prefix) {
        super(makeTimerName(name, prefix), isDaemon);
    }

    private static final AtomicInteger nextSerialNumber = new AtomicInteger(1);

    public static String makeTimerName(String name, String prefix) {
        return name.startsWith(MARK) ? name : prefix + "#" + name;
    }


}
