package com.zhangyue.ireader.plugin_privacy

import com.zhangyue.ireader.plugin_privacy.asmItem.MethodReplaceItem

class PrivacyGlobalConfig {

    public static final List<MethodReplaceItem> methodReplaceItemList = new ArrayList<>()

    public static Set<String> filterClassName = new HashSet<>()

    public static boolean isDebug = false

    public static String handleAnnotationName

    public static boolean shouldInject

    /**
     * 过滤白名单
     */
    private static Object[] exclude


    static boolean isDebug() {
        return isDebug
    }

    static void setDebug(boolean debug) {
        isDebug = debug
    }

    static boolean getShouldInject() {
        return shouldInject
    }

    static void setShouldInject(boolean inject) {
        shouldInject = inject
    }

    static void setHandleAnnotationName(String name) {
        handleAnnotationName = name
    }

    static String getHandleAnnotationName() {
        return handleAnnotationName
    }

    static HashSet<String> getExclude() {
        return exclude
    }

    static void setExclude(Object[] excludes) {
        exclude = excludes
    }
}