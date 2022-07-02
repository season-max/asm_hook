package com.zhangyue.ireader.toolslibrary.config;

import android.os.Looper;

public class Util {


    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    public static String getStackTrace(String tag) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append("TAG: ");
        stringBuilder.append(tag);
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append("Thread: ");
        stringBuilder.append(Thread.currentThread()).append(", 主线程: ").append(Looper.getMainLooper() == Looper.myLooper());
        stringBuilder.append(LINE_SEPARATOR);
        stringBuilder.append("-------------");
        stringBuilder.append(LINE_SEPARATOR);
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        for (StackTraceElement element : stackTrace) {
            stringBuilder.append(element.toString());
            stringBuilder.append(LINE_SEPARATOR);
        }

        return stringBuilder.toString();
    }
}
