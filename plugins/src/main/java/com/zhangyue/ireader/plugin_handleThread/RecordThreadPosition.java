package com.zhangyue.ireader.plugin_handleThread;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 记录线程替换位置
 */
public class RecordThreadPosition {

    private static final List<RecordThreadPosition> sPositionList = new CopyOnWriteArrayList<>();

    public String outerClassName;

    public String invokeMethodName;

    public String anonymousClassName;

    public String sourceFile;

    public boolean isAnonymousClass;

    public String replaceThreadName;

    public static List<RecordThreadPosition> getPositionList() {
        return sPositionList;
    }


    public String toFileString() {
        StringBuilder builder = new StringBuilder();
        builder.append("sourceFile----> ").append(sourceFile);
        builder.append("\r\n");
        builder.append("outerClassName----> ").append(outerClassName);
        builder.append("\r\n");
        builder.append("invokeMethodName----> ").append(invokeMethodName);
        builder.append("\r\n");
        builder.append("isAnonymousClass----> ").append(isAnonymousClass);
        builder.append("\r\n");
        builder.append("anonymousThreadName----> ").append(anonymousClassName);
        builder.append("\r\n");
        builder.append("replaceThreadName----> ").append(replaceThreadName);
        return builder.toString();
    }
}
