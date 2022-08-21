package com.zhangyue.ireader.toolslibrary.handleThread;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程优化类，
 * 参考 Thread 的构造方法，多携带一个 className 参数
 */
public class OptimizeThread extends Thread {

    private static final AtomicInteger threadNumber = new AtomicInteger(0);

    private static String generateName(String name, String className) {
        return "R_" + className + "_" + threadNumber.getAndIncrement() + (TextUtils.isEmpty(name) ? "" : ("_" + name));
    }

    public OptimizeThread(String className) {
        super(generateName("", className));
    }

    public OptimizeThread(@Nullable Runnable target, String className) {
        super(target, generateName("", className));
    }

    public OptimizeThread(@Nullable Runnable target, String name, String className) {
        super(target, generateName(name, className));
    }

    public OptimizeThread(String name, String className) {
        super(generateName(name, className));
    }

    public OptimizeThread(ThreadGroup group, Runnable target, String className) {
        super(group, target, generateName("", className));
    }

    public OptimizeThread(ThreadGroup group, Runnable target, String name, String className) {
        super(group, target, generateName(name, className));
    }

    public OptimizeThread(ThreadGroup group, Runnable target, String name,
                          long stackSize, String className) {
        super(group, target, generateName(name, className), stackSize);
    }

    public OptimizeThread(ThreadGroup group, String name, String className) {
        super(group, generateName(name, className));
    }

}
