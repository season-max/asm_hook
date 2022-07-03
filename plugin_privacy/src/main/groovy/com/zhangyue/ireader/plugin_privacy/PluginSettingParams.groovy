package com.zhangyue.ireader.plugin_privacy
/**
 * 用于 build.gradle 中传递参数
 */
class PluginSettingParams {

    String name = "隐私合规处理插件"

    /**
     * 是否是 debug 模式
     */
    boolean isDebug = false

    /**
     * 是否注入
     */
    boolean inject = false

    /**
     * 需要处理的注解
     */
    String handle_annotation_desc = ''

    /**
     * 过滤白名单
     */
    String[] exclude


}