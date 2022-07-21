package com.zhangyue.ireader.plugin_privacy

import com.zhangyue.ireader.plugin_privacy.asmItem.MethodReplaceItem
import org.objectweb.asm.tree.MethodNode

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue

class PrivacyGlobalConfig {

    public static final Queue<MethodReplaceItem> methodReplaceItemList = new LinkedBlockingQueue<>()

    public static Map<String, MethodNode> filterMethod = new ConcurrentHashMap<>()

    public static boolean isDebug = false

    public static String handleAnnotationName


    public static String recordOwner
    public static String recordMethod
    public static String recordDesc

    /**
     * 用来存储替换的字节码
     */
    public static StringBuilder stringBuilder = new StringBuilder()

    /**
     * 过滤白名单
     */
    private static String[] exclude


    static boolean isDebug() {
        return isDebug
    }

    static void setDebug(boolean debug) {
        isDebug = debug
    }


    static void setHandleAnnotationName(String name) {
        handleAnnotationName = name
    }

    static String getHandleAnnotationName() {
        return handleAnnotationName
    }

    static String[] getExclude() {
        return exclude
    }

    static void setExclude(String[] excludes) {
        exclude = excludes
    }
}