package com.zhangyue.ireader.plugin_handleThread

import com.android.build.gradle.AppExtension
import com.zhangyue.ireader.plugin_handleThread.transform.CollectThreadTransform
import com.zhangyue.ireader.plugin_handleThread.transform.HandleThreadTransform
import com.zhangyue.ireader.util.CommonUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginEntry implements Plugin<Project>{

    @Override
    void apply(Project project) {
        println "project--->${project.name}"

        project.extensions.create('handle_thread_config', HandleThreadConfig.class)
        def android = project.extensions.getByType(AppExtension.class)
        android.registerTransform(new CollectThreadTransform(project))
        android.registerTransform(new HandleThreadTransform(project))

        project.afterEvaluate {
            HandleThreadTransform.turnOn = project.handle_thread_config.turnOn
            HandleThreadTransform.handleThreadClass = CommonUtil.getClassInternalName(project.handle_thread_config.handleThreadClass)
            println "turnOn=${HandleThreadTransform.turnOn},handleThreadClass=${HandleThreadTransform.handleThreadClass}"
        }


    }
}