package com.zhangyue.ireader.plugin_handleThread.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.BaseTransform
import com.zhangyue.ireader.plugin_handleThread.Config
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


/**
 * hook 匿名线程，统一命名管理
 */
class HandleThreadTransform extends BaseTransform {

    HandleThreadTransform(Project project) {
        super(project)
    }

    @Override
    protected boolean shouldHookClassInner(String className) {
        return Config.turnOn
    }

    @Override
    protected byte[] hookClassInner(String className, byte[] bytes) {
        ClassReader cr = new ClassReader(bytes)
        ClassNode cn = new ClassNode(Opcodes.ASM9)
        cr.accept(cn, ClassReader.EXPAND_FRAMES)
        if (Config.anonymousThreadClassList.contains(cn.name)) {
            //匿名线程类
            Config.logger("处理匿名线程类 ${cn.name}")
            hookAnonymousThread(cn)
        } else {
            cn.methods.each { methodNode ->
                methodNode.instructions.each { insnNode ->
                    if (insnNode instanceof TypeInsnNode
                            && insnNode.opcode == Opcodes.NEW
                            && Config.threadClass == insnNode.desc) {
                        //匿名线程对象
                        Config.logger("处理匿名线程对象字节码 ${cr.className} --> ${methodNode.name} --->${insnNode.desc}")
                        hookNewThread(cr.className, methodNode, insnNode)
                    }
                }
            }
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        cn.accept(cw)
        return cw.toByteArray()
    }

    /**
     * 对匿名线程类做处理，将匿名线程类的父类替换为工具类，同时在调用 super 的地方添加 className + methodName 前缀
     */
    static void hookAnonymousThread(cn) {
        if (cn.superName == Config.threadClass) {
            cn.superName = Config.handleThreadClass
            cn.methods.each { methodNode ->
                if (methodNode.name == "<init>") {
                    //构造方法，在 super 方法里添加 className 后缀
                    def insnList = methodNode.instructions
                    methodNode.instructions.each { insnNode ->
                        if (insnNode instanceof MethodInsnNode && insnNode.opcode == Opcodes.INVOKESPECIAL
                                && insnNode.name == "<init>" && insnNode.owner == Config.threadClass) {
                            //super 方法
                            Config.logger("找到 super 方法，desc:${insnNode.desc}")
                            insnNode.owner = Config.handleThreadClass
                            insnNode.desc = insertArgument(insnNode.desc, String.class)
                            insnList.insertBefore(insnNode, new LdcInsnNode(cn.name))
                            Config.logger("替换 super 方法字节码，owner 改为：${insnNode.owner}，desc 改为 ${insnNode.desc}")
                        }
                    }
                }
            }
        }
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
        for (int i = index + 1; i < insnList.size(); i++) {
            AbstractInsnNode node = insnList.get(i)
            if (node instanceof MethodInsnNode && node.opcode == Opcodes.INVOKESPECIAL && node.owner == typeNodeDesc && node.name == "<init>") {
                println "----> ${className}---->${methodNode.name}"
                insnNode.desc = Config.handleThreadClass
                node.owner = Config.handleThreadClass
                //向 method descriptor 中添加 类名 + 调用方法名 参数
                node.desc = insertArgument(node.desc, String.class)
                insnList.insertBefore(node, new LdcInsnNode(normalName + '_' + methodNode.name))
                Config.logger("替换构造对象字节码，owner 改为：${node.owner}，desc 改为 ${node.desc}")
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
        print "threadClassList::${Config.anonymousThreadClassList}"
    }

    @Override
    protected void onTransformEnd(TransformInvocation transformInvocation) {
    }
}
