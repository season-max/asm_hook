package com.zhangyue.ireader.plugin_handleThread

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.BaseTransform
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

/**
 * hook 匿名线程，统一命名管理
 */
class HandleThreadTransform extends BaseTransform {

    static boolean turnOn

    static String threadClass = "java/lang/Thread"

    static List<String> threadClassList = new ArrayList<>()

    HandleThreadTransform(Project project) {
        super(project)
    }

    @Override
    protected boolean shouldHookClassInner(String className) {
        return turnOn
    }

    @Override
    protected byte[] hookClassInner(String className, byte[] bytes) {
        //先收集 thread 的子类

        threadClassList.add(threadClass)
        ClassReader cr = new ClassReader(bytes)
        ClassNode cn = new ClassNode(Opcodes.ASM9)
        cr.accept(cn,ClassReader.EXPAND_FRAMES)
        cn.methods.each {methodNode ->
            methodNode.instructions.each {insnNode ->
                if(insnNode instanceof TypeInsnNode
                        && insnNode.opcode == Opcodes.NEW
                        && threadClassList.contains(insnNode.desc)){
                    println "find thread in ${cr.className} --> ${methodNode.name}"
                    hookNewThread(cr.className,methodNode,insnNode)
                }
            }
        }

    }

    static void hookNewThread(className,methodNode,insnNode){
        def insnList = methodNode.instructions
        int index = insnList.indexOf(insnNode)
        def typeNodeDesc = insnNode.desc
        for (int i = index + 1, length = insnList.size(); i < length; i++) {
            AbstractInsnNode node = insnList.get(i)
            if (node instanceof MethodInsnNode && node.opcode == Opcodes.INVOKESPECIAL && node.desc == typeNodeDesc && node.name == "<init>") {
                //向 method descriptor 中添加 类名 + 调用方法名 参数
                def methodDesc = node.desc
                def argumentTypes = Type.getArgumentTypes(methodDesc)

            }
        }
    }

    @Override
    protected void onTransformStart(TransformInvocation transformInvocation) {
        println "-------->transform ${getName()} start"
    }

    @Override
    protected void onTransformEnd(TransformInvocation transformInvocation) {
        println "-------->transform ${getName()} end"
    }
}
