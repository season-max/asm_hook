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
        //收集 Thread 类型的匿名内部类，再下一个 transform 中将其更换成优化的线程类
        if (anonymousThreadClass(cn)) {
            def outerClass = cn.name.substring(0,cn.name.indexOf("\$"))
            println("outClass::${outerClass}")
            cn.methods.each { methodNode ->
                if (methodNode.name == "<init>" && methodNode.desc.contains(outerClass)) {
                    println "${cn.name} add into threadClassList"
                    HandleThreadTransform.threadClassList.add(cn.name)
                }
            }
        }
        ClassWriter cw = new ClassWriter(0)
        cn.accept(cw)
        return cw.toByteArray()
    }

    /**
     * 判断是不是 Thread 类型的匿名内部类
     */
    static boolean anonymousThreadClass(cn) {
        return (cn.name != HandleThreadTransform.handleThreadClass
                && cn.superName == HandleThreadTransform.threadClass
                && cn.name.indexOf("\$") > 0)
    }

    @Override
    protected void onTransformStart(TransformInvocation transformInvocation) {

    }

    @Override
    protected void onTransformEnd(TransformInvocation transformInvocation) {

    }
}