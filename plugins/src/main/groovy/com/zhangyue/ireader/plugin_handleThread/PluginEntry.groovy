package com.zhangyue.ireader.plugin_handleThread

import com.android.build.gradle.AppExtension
import com.zhangyue.ireader.plugin_handleThread.transform.HandleThreadTransform
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginEntry implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "project--->${project.name}"

        project.extensions.create('handle_thread_config', PluginParams.class)
        def android = project.extensions.getByType(AppExtension.class)
        android.registerTransform(new HandleThreadTransform(project))

        project.afterEvaluate {
            Config.printLog = project.handle_thread_config.printLog
            Config.turnOn = project.handle_thread_config.turnOn
            Config.logger("printLog=${Config.printLog},turnOn=${Config.turnOn}")
        }

    }
}