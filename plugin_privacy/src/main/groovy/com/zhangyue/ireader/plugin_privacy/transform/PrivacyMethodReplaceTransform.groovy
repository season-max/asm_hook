package com.zhangyue.ireader.plugin_privacy.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.plugin_privacy.PrivacyGlobalConfig
import com.zhangyue.ireader.plugin_privacy.util.Logger
import com.zhangyue.ireader.plugin_privacy.visitor.PrivacyClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

/**
 * 替换隐私合规相关方法 transform
 */
class PrivacyMethodReplaceTransform extends BaseTransform {


    @Override
    boolean shouldHookClass(String className) {
        return PrivacyGlobalConfig.shouldInject
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
    }

    @Override
    void onTransformEnd(TransformInvocation transformInvocation) {
        Logger.info("${getName()} end--------------->")
    }
}