package com.zhangyue.ireader.plugin_doubleclick.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.BaseTransform
import com.zhangyue.ireader.plugin_doubleclick.visitor.DoubleClickClassVisitor
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

class CheckDoubleClickTransform extends BaseTransform {

    /**
     * 要检测的注解
     */
    static String checkAnnotation

    static String checkAnnotationName

    CheckDoubleClickTransform(Project project) {
        super(project)
    }

    @Override
    protected boolean shouldHookClassInner(String className) {
        return checkAnnotation != null

    }

    @Override
    protected byte[] hookClassInner(String className, byte[] bytes) {
        ClassReader cr = new ClassReader(bytes)
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new DoubleClickClassVisitor(Opcodes.ASM9, cw)
        cr.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        return cw.toByteArray()
    }

    @Override
    protected void onTransformStart(TransformInvocation transformInvocation) {
        println "------${getName()} start"
    }

    @Override
    protected void onTransformEnd(TransformInvocation transformInvocation) {
        println "------${getName()} end"
    }
}