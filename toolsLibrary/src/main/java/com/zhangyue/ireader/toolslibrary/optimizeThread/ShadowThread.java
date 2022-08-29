package com.zhangyue.ireader.toolslibrary.optimizeThread;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程优化类，
 * 参考 Thread 的构造方法，多携带一个 className 参数
 */
public class ShadowThread extends Thread {

    /**
     * 标记，防止重复添加前缀
     */
    static final String MARK = "\u200B";

    private static final AtomicInteger threadNumber = new AtomicInteger(0);

    public static String makeThreadName(String name, String prefix) {
        return name == null ? prefix : name.startsWith(MARK) ? name : (prefix + "#" + name);
    }

    public static Thread setThreadName(Thread thread, String prefix) {
        String name = thread.getName();
        name = makeThreadName(name, prefix);
        thread.setName(name);
        return thread;
    }

    /**
     * prefix + "#" + name + "_" + number
     */
    private static String generateName(String name, String prefix) {
        int number = threadNumber.getAndIncrement();
        String temp;
        if (name == null) {
            temp = prefix == null ? "" : prefix.startsWith(MARK) ? prefix : (MARK + prefix);
        } else {
            temp = name.startsWith(MARK) ? name : (prefix + "#" + name);
        }
        return temp + "_" + number;
    }

    public ShadowThread(String className) {
        super(generateName(null, className));
    }

    public ShadowThread(@Nullable Runnable target, String className) {
        super(target, generateName(null, className));
    }

    public ShadowThread(@Nullable Runnable target, String name, String className) {
        super(target, generateName(name, className));
    }

    public ShadowThread(String name, String className) {
        super(generateName(name, className));
    }

    public ShadowThread(ThreadGroup group, Runnable target, String className) {
        super(group, target, generateName(null, className));
    }

    public ShadowThread(ThreadGroup group, String name, String className) {
        super(group, generateName(name, className));
    }

    public ShadowThread(ThreadGroup group, Runnable target, String name, String className) {
        super(group, target, generateName(name, className));
    }

    public ShadowThread(ThreadGroup group, Runnable target, String name,
                        long stackSize, String className) {
        super(group, target, generateName(name, className), stackSize);
    }

}
