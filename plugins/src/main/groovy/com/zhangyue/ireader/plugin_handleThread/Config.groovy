package com.zhangyue.ireader.plugin_handleThread

import java.util.concurrent.CopyOnWriteArrayList

class Config {

    /**
     * 是否打印日志
     */
    static boolean printLog

    /**
     * 是否启用插件
     */
    static boolean turnOn

    /**
     * Thread class 的规范名称
     */
    static String threadClass = "java/lang/Thread"

    /**
     * 线程处理类
     */
    static String handleThreadClass


    /**
     * 匿名内部线程类集合
     */
    static List<String> anonymousThreadClassList = new CopyOnWriteArrayList<>()

    static void logger(String msg) {
        if (!printLog) {
            return
        }
        println msg
    }

}