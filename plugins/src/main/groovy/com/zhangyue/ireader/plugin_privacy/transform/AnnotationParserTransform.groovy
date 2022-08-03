package com.zhangyue.ireader.plugin_privacy.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.BaseTransform
import com.zhangyue.ireader.plugin_privacy.PrivacyGlobalConfig
import com.zhangyue.ireader.plugin_privacy.asmItem.MethodReplaceItem
import com.zhangyue.ireader.util.CommonUtil
import com.zhangyue.ireader.util.Logger
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

/**
 * 解析关联注解的方法，收集配置信息
 */
class AnnotationParserTransform extends BaseTransform {


    AnnotationParserTransform(Project project) {
        super(project)
    }

    @Override
    boolean shouldHookClassInner(String className) {
        return true
    }

    @Override
    byte[] hookClassInner(String className, byte[] bytes) {
        ClassReader cr = new ClassReader(bytes)
        ClassNode classNode = new ClassNode()
        cr.accept(classNode, 0)
        classNode.methods.each { methodNode ->
            //编译期注解
            methodNode.invisibleAnnotations.
                    each { annotationNode ->
                        if (PrivacyGlobalConfig.getHandleAnnotationName() == annotationNode.desc) {
                            collectPrivacyMethod(annotationNode, methodNode, cr.className)
                        }
                    }
        }
        return bytes
    }

    /**
     * 收集注解和注解关联的方法
     * @param annotationNode 注解信息
     * @param methodNode 方法信息
     */
    static collectPrivacyMethod(AnnotationNode annotationNode, MethodNode methodNode, String className) {
        List<Object> values = annotationNode.values
        Logger.info("annotation values : ${values}")
        MethodReplaceItem item = new MethodReplaceItem(values, methodNode, CommonUtil.getClassInternalName(className))
        PrivacyGlobalConfig.methodReplaceItemList.offer(item)
        Logger.info("collectPrivacyMethod success: ${item}")
        println("collectPrivacyMethod success: ${item}")
    }

    @Override
    void onTransformStart(TransformInvocation transformInvocation) {
        Logger.info("${getName()} start--------------->")
    }

    @Override
    void onTransformEnd(TransformInvocation transformInvocation) {
        Logger.info("${getName()} end--------------->")
    }

    @Override
    protected boolean firstTransform() {
        return true
    }
}
