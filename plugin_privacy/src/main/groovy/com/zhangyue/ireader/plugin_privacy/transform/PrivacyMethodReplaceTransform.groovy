package com.zhangyue.ireader.plugin_privacy.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.plugin_privacy.PrivacyGlobalConfig
import com.zhangyue.ireader.plugin_privacy.util.Logger
import com.zhangyue.ireader.plugin_privacy.visitor.PrivacyClassVisitor
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

/**
 * 替换隐私合规相关方法 transform
 */
class PrivacyMethodReplaceTransform extends BaseTransform {


    PrivacyMethodReplaceTransform(Project project) {
        super(project)
    }

    @Override
    boolean shouldHookClassInner(String className) {
        return true
    }

    @Override
    byte[] hookClassInner(String className, byte[] bytes) {
        Logger.info("${getName()} modifyClassInner--------------->")
        //过滤处理类自身
        if (PrivacyGlobalConfig.filterClassName.contains(className)) {
            Logger.info("过滤工具类${className}自身--------------->")
            return bytes
        }
        ClassReader classReader = new ClassReader(bytes)
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new PrivacyClassVisitor(classWriter)
        classReader.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }

    @Override
    void onTransformStart(TransformInvocation transformInvocation) {
        Logger.info("${getName()} start--------------->")
        PrivacyGlobalConfig.stringBuilder.setLength(0)
    }

    @Override
    void onTransformEnd(TransformInvocation transformInvocation) {
        Logger.info("${getName()} end--------------->")
        //写入文件
        byte[] bytes = PrivacyGlobalConfig.stringBuilder.toString().getBytes("UTF-8")
        try {
            println "project.path= ${project.rootDir}"
            def targetFile = new File(project.rootDir, "replaceInsn.txt")
            if (targetFile.exists()) {
                targetFile.delete()
            }
            targetFile.withOutputStream { it ->
                it.write(bytes)
            }
            println "写文件结束，path${targetFile.absolutePath}"
        } catch (Exception e) {

        }
    }
}