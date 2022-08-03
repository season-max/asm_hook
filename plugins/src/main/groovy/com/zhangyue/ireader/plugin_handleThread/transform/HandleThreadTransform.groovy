package com.zhangyue.ireader.plugin_handleThread.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.BaseTransform
import com.zhangyue.ireader.util.CommonUtil
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.TypeInsnNode

import java.util.concurrent.CopyOnWriteArrayList

/**
 * hook 匿名线程，统一命名管理
 */
class HandleThreadTransform extends BaseTransform {

    static boolean turnOn

    static String threadClass = "java/lang/Thread"

    static String handleThreadClass

    static List<String> threadClassList = new CopyOnWriteArrayList<>()

    static {
        threadClassList.add(threadClass)
    }

    HandleThreadTransform(Project project) {
        super(project)
    }

    @Override
    protected boolean shouldHookClassInner(String className) {
        return turnOn
    }

    @Override
    protected byte[] hookClassInner(String className, byte[] bytes) {
        ClassReader cr = new ClassReader(bytes)
        ClassNode cn = new ClassNode(Opcodes.ASM9)
        cr.accept(cn, ClassReader.EXPAND_FRAMES)
        cn.methods.each { methodNode ->
            methodNode.instructions.each { insnNode ->
                if (insnNode instanceof TypeInsnNode
                        && insnNode.opcode == Opcodes.NEW
                        && threadClassList.contains(insnNode.desc)) {
                    println "find thread in ${cr.className} --> ${methodNode.name} --->${insnNode.desc}"
                    hookNewThread(cr.className, methodNode, insnNode)
                }
            }
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        cn.accept(cw)
        return cw.toByteArray()
    }

    /**
     * 对线程名称做处理 ，添加 className + methodName 前缀
     * @param className
     * @param methodNode
     * @param insnNode
     */
    static void hookNewThread(className, methodNode, insnNode) {
        def insnList = methodNode.instructions
        int index = insnList.indexOf(insnNode)
        def typeNodeDesc = insnNode.desc
        def normalName = CommonUtil.path2ClassName(className)
        println "---->index=${index}"
        for (int i = index + 1; i < insnList.size(); i++) {
            AbstractInsnNode node = insnList.get(i)
            if (node instanceof MethodInsnNode && node.opcode == Opcodes.INVOKESPECIAL && node.owner == typeNodeDesc && node.name == "<init>") {
                println "----> ${className}---->${methodNode.name}"
                insnNode.desc = handleThreadClass
                node.owner = handleThreadClass
                //向 method descriptor 中添加 类名 + 调用方法名 参数
                node.desc = insertArgument(node.desc, String.class)
                insnList.insertBefore(node, new LdcInsnNode(normalName + '_' + methodNode.name + "_"))
                //找到一个就 break
                break
            }
        }
    }

    /**
     * 在描述符末尾添加文件描述符
     * @param descriptor
     * @param clazz
     * @return
     */
    static String insertArgument(descriptor, Class<?> clazz) {
        def type = Type.getMethodType(descriptor)
        def returnType = type.getReturnType()
        def argumentTypes = type.getArgumentTypes()
        def newArgumentTypes = new Type[argumentTypes.length + 1]
        System.arraycopy(argumentTypes, 0, newArgumentTypes, 0, argumentTypes.length)
        newArgumentTypes[newArgumentTypes.length - 1] = Type.getType(clazz)
        return Type.getMethodDescriptor(returnType, newArgumentTypes)
    }

    @Override
    protected void onTransformStart(TransformInvocation transformInvocation) {
        print "threadClassList::${threadClassList}"
    }

    @Override
    protected void onTransformEnd(TransformInvocation transformInvocation) {
    }
}
