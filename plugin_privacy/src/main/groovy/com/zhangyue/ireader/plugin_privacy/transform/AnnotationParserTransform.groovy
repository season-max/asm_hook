package com.zhangyue.ireader.plugin_privacy.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.plugin_privacy.PrivacyGlobalConfig
import com.zhangyue.ireader.plugin_privacy.asmItem.MethodReplaceItem
import com.zhangyue.ireader.plugin_privacy.util.CommonUtil
import com.zhangyue.ireader.plugin_privacy.util.Logger
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

/**
 * 解析关联注解的方法，收集配置信息
 */
class AnnotationParserTransform extends BaseTransform {


    @Override
    boolean shouldHookClass(String className) {
        return true
    }

    @Override
    byte[] hookClassInner(String className, byte[] bytes) {
        ClassReader cr = new ClassReader(bytes)
        ClassNode classNode = new ClassNode()
        cr.accept(classNode, 0)
        classNode.methods.each { methodNode ->
            methodNode.invisibleAnnotations.each { annotationNode ->
                if (PrivacyGlobalConfig.getHandleAnnotationName() == annotationNode.desc) {
                    collectPrivacyMethod(annotationNode, methodNode, CommonUtil.fileSeparatorName(className))
                    PrivacyGlobalConfig.filterClassName.add(className)
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
    static collectPrivacyMethod(AnnotationNode annotationNode, MethodNode methodNode, String owner) {
        List<Object> values = annotationNode.values
        Logger.info("annotation values : ${values}")
        MethodReplaceItem item = new MethodReplaceItem(values, methodNode, owner)
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
