package com.zhangyue.ireader.plugin_privacy

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.zhangyue.ireader.plugin_privacy.transform.AnnotationParserTransform
import com.zhangyue.ireader.plugin_privacy.transform.PrivacyMethodReplaceTransform
import com.zhangyue.ireader.plugin_privacy.util.Logger
import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginEntry implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println 'apply plugin privacy'

        project.extensions.create('privacy_plugin_config', PluginSettingParams)

        def android = project.extensions.getByType(AppExtension)
        registerTransform(android)

        project.afterEvaluate {
            Logger.setIsDebug(project.privacy_plugin_config.isDebug)
            analysiUserConfig(project)
        }

    }

    static void analysiUserConfig(Project project) {
        PrivacyGlobalConfig.setDebug(project.privacy_plugin_config.isDebug)
        Logger.info("debug:: ${project.privacy_plugin_config.isDebug}")
        PrivacyGlobalConfig.setHandleAnnotationName(project.privacy_plugin_config.handle_annotation_desc)
        Logger.info("handle_annotation_desc:: ${project.privacy_plugin_config.handle_annotation_desc}")
        PrivacyGlobalConfig.setShouldInject(project.privacy_plugin_config.inject)
        Logger.info("should_inject:: ${project.privacy_plugin_config.inject}")
        PrivacyGlobalConfig.setExclude(project.privacy_plugin_config.exclude)
        Logger.info("exclude:: ${project.privacy_plugin_config.exclude}")
    }

    static void registerTransform(BaseExtension android) {
        android.registerTransform(new AnnotationParserTransform())

        android.registerTransform(new PrivacyMethodReplaceTransform())
    }
}