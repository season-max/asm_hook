package com.zhangyue.ireader.plugin_handleThread.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.BaseTransform
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.ClassNode

class CollectThreadTransform extends BaseTransform {

    CollectThreadTransform(Project project) {
        super(project)
    }

    @Override
    protected boolean shouldHookClassInner(String className) {
        return true
    }

    @Override
    protected byte[] hookClassInner(String className, byte[] bytes) {
        ClassReader cr = new ClassReader(bytes)
        ClassNode cn = new ClassNode(Opcodes.ASM9)
        cr.accept(cn, 0)
        if (cn.name != HandleThreadTransform.handleThreadClass && cn.superName == HandleThreadTransform.threadClass) {

        }
        ClassWriter cw = new ClassWriter(0)
        cn.accept(cw)
        return cw.toByteArray()
    }

    @Override
    protected void onTransformStart(TransformInvocation transformInvocation) {

    }

    @Override
    protected void onTransformEnd(TransformInvocation transformInvocation) {

    }
}