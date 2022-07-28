package com.zhangyue.ireader.plugin_handleThread

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginEntry implements Plugin<Project>{

    @Override
    void apply(Project project) {
        println "project--->${project.name}"

        project.extensions.create('handle_thread_config',HandleThreadConfig.class)
        def android = project.extensions.getByType(AppExtension.class)
        android.registerTransform(new HandleThreadTransform(project))

        project.afterEvaluate {
            HandleThreadTransform.turnOn = project.handle_thread_config.turnOn
        }


    }
}