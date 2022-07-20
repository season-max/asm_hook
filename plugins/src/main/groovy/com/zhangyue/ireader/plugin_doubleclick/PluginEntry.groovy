package com.zhangyue.ireader.plugin_doubleclick

import com.android.build.gradle.AppExtension
import com.zhangyue.ireader.plugin_doubleclick.transform.CheckDoubleClickTransform
import com.zhangyue.ireader.util.CommonUtil
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginEntry implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "-----> project.name=${project.name}"

        project.extensions.create('double_click_config', DoubleClickConfig)
        def android = project.extensions.getByType(AppExtension.class)
        android.registerTransform(new CheckDoubleClickTransform(project))


        project.afterEvaluate {
            CheckDoubleClickTransform.checkAnnotation = CommonUtil.formatName(project.double_click_config.checkAnnotation)
            println "checkAnnotation : ${CheckDoubleClickTransform.checkAnnotation}"
            CheckDoubleClickTransform.checkAnnotationName = project.double_click_config.annotationName
            println "checkAnnotation name: ${CheckDoubleClickTransform.checkAnnotationName}"
        }
    }
}