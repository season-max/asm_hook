package com.zhangyue.ireader.toolslibrary.handleThread;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程优化类，多携带一个 className
 */
public class OptimizeThread extends Thread {

    private static final AtomicInteger threadNumber = new AtomicInteger(0);

    public OptimizeThread(@Nullable Runnable target, String name, String className) {
        super(target, generateName(name, className));
    }

    private static String generateName(String name, String className) {
        return className + "_" + threadNumber.getAndIncrement() + (TextUtils.isEmpty(name) ? "" : ("_" + name));
    }

    public OptimizeThread(String className) {
        this(null, "", className);
    }

    public OptimizeThread(@Nullable Runnable target, String className) {
        this(target, "", className);
    }

    public OptimizeThread(String name, String className) {
        this(null, name, className);
    }

    public OptimizeThread(@Nullable ThreadGroup group, @Nullable Runnable target, @NonNull String name, long stackSize) {
        super(group, target, name, stackSize);
    }

    public OptimizeThread() {
        super();
    }
}
