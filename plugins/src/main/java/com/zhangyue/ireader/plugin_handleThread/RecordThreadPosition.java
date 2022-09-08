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

    public String sourceFile;

    public String originInsnNode;

    public String afterInsnNode;

    public static List<RecordThreadPosition> getPositionList() {
        return sPositionList;
    }


    public String toFileString() {
        return "sourceFile----> " + sourceFile +
                "\r\n" +
                "outerClassName----> " + outerClassName +
                "\r\n" +
                "invokeMethodName----> " + invokeMethodName +
                "\r\n" +
                "originInsnNode----> " + originInsnNode +
                "\r\n" +
                "afterInsnNode----> " + afterInsnNode
                ;
    }
}
