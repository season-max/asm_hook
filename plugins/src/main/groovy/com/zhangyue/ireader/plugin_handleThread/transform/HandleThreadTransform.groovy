package com.zhangyue.ireader.plugin_handleThread.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.BaseTransform
import com.zhangyue.ireader.plugin_handleThread.Config
import com.zhangyue.ireader.plugin_handleThread.RecordThreadPosition
import com.zhangyue.ireader.util.CommonUtil
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

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
        //过滤掉匿名内部类，后面单独处理
        if (!Config.anonymousThreadClassList.contains(cn.name)) {
            //处理匿名线程对象 ｜｜ 记录位置
            cn.methods.each { methodNode ->
                methodNode.instructions.each { insnNode ->
                    if (insnNode instanceof TypeInsnNode
                            && insnNode.opcode == Opcodes.NEW) {
                        def newThreadName = CommonUtil.path2ClassName(cn.name) + '_' + methodNode.name
                        if (Config.threadClass == insnNode.desc) {
                            //匿名线程对象
                            Config.logger("要处理匿名线程 类 ${cn.name} --> 方法 ${methodNode.name} --->${insnNode.desc}")
                            hookNewThread(methodNode, insnNode, newThreadName)
                            recordPosition(cn, methodNode, false, "",newThreadName)
                        } else if (Config.anonymousThreadClassList.contains(insnNode.desc)) {
                            Config.logger("记录匿名线程类位置  ${cn.name} ---> ${methodNode.name}")
                            recordPosition(cn, methodNode, true, insnNode.desc, newThreadName)
                        }
                    }
                }
            }
        }

        //处理匿名内部类
        if (Config.anonymousThreadClassList.contains(cn.name)) {
            Config.logger("处理匿名内部线程类 ${cn.name}")
            hookAnonymousThread(cn)
        }

        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        cn.accept(cw)
        return cw.toByteArray()
    }

    static void recordPosition(cn, methodNode,
                               isAnonymousClass, anonymousThreadClassName, newThreadName) {
        RecordThreadPosition position = new RecordThreadPosition()
        def outerClass = cn.name
        position.outerClassName = outerClass
        position.sourceFile = cn.sourceFile
        position.invokeMethodName = methodNode.name
        position.isAnonymousClass = isAnonymousClass
        position.anonymousClassName = anonymousThreadClassName
        position.replaceThreadName = newThreadName
        RecordThreadPosition.positionList.add(position)
    }

    /**
     * 对匿名线程类做处理，将匿名线程类的父类替换为工具类，同时在调用 super 的地方添加 className + methodName 前缀
     */
    static void hookAnonymousThread(ClassNode cn) {
        if (cn.superName == Config.threadClass) {
            cn.superName = Config.handleThreadClass
            cn.methods.each { methodNode ->
                if (methodNode.name == "<init>") {
                    //处理构造方法，在 super 方法里添加 className 后缀
                    def insnList = methodNode.instructions
                    methodNode.instructions.each { insnNode ->
                        if (insnNode instanceof MethodInsnNode && insnNode.opcode == Opcodes.INVOKESPECIAL
                                && insnNode.name == "<init>" && insnNode.owner == Config.threadClass) {
                            //super 方法
                            Config.logger("找到 super 方法，desc:${insnNode.desc}")
                            //替换 super class 为工具类
                            insnNode.owner = Config.handleThreadClass
                            //构造新的方法描述符，尾部增加 String 类型的参数
                            insnNode.desc = insertArgument(insnNode.desc, String.class)
                            def newThreadName = findAnonymousThreadClassInvokeMethod(cn)
                            insnList.insertBefore(insnNode, new LdcInsnNode(newThreadName))
                            Config.logger("替换 super 方法字节码，owner 改为：${insnNode.owner}，desc 改为 ${insnNode.desc}")
                        }
                    }
                }
            }
        }
    }

    static String findAnonymousThreadClassInvokeMethod(ClassNode cn) {
        RecordThreadPosition.positionList.each { position ->
            if (position.isAnonymousClass && position.sourceFile == cn.sourceFile && position.outerClassName == cn.name) {
                return position.replaceThreadName
            }
        }
        return ""
    }

    /**
     * 对线程名称做处理 ，添加 className + methodName 前缀
     * @return 替换后的线程名称
     */
    static void hookNewThread(methodNode, insnNode, newThreadName) {
        def insnList = methodNode.instructions
        int index = insnList.indexOf(insnNode)
        def typeNodeDesc = insnNode.desc
        for (int i = index + 1; i < insnList.size(); i++) {
            AbstractInsnNode node = insnList.get(i)
            if (node instanceof MethodInsnNode && node.opcode == Opcodes.INVOKESPECIAL && node.owner == typeNodeDesc && node.name == "<init>") {
                insnNode.desc = Config.handleThreadClass
                node.owner = Config.handleThreadClass
                //向 method descriptor 中添加 类名 + 调用方法名 参数
                node.desc = insertArgument(node.desc, String.class)
                insnList.insertBefore(node, new LdcInsnNode(newThreadName))
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
        //返回值类型
        def returnType = type.getReturnType()
        //参数数组
        def argumentTypes = type.getArgumentTypes()
        //构造新的参数数组
        def newArgumentTypes = new Type[argumentTypes.length + 1]
        System.arraycopy(argumentTypes, 0, newArgumentTypes, 0, argumentTypes.length)
        newArgumentTypes[newArgumentTypes.length - 1] = Type.getType(clazz)
        return Type.getMethodDescriptor(returnType, newArgumentTypes)
    }

    @Override
    protected void onTransformStart(TransformInvocation transformInvocation) {
        Config.logger("anonymousThreadClassList::${Config.anonymousThreadClassList}")
    }

    @Override
    protected void onTransformEnd(TransformInvocation transformInvocation) {
        if (!RecordThreadPosition.positionList.isEmpty()) {
            StringBuilder builder = new StringBuilder()
            for (RecordThreadPosition position : RecordThreadPosition.positionList) {
                builder.append(position.toFileString())
                builder.append("\r\n")
                builder.append("\r\n")
            }
            try {
                byte[] bytes = builder.toString().getBytes("UTF-8")
                File file = new File(project.rootDir, "hookThread.txt")
                if (file.exists()) {
                    file.delete()
                }
                file.withOutputStream { it ->
                    it.write(bytes)
                }
                Config.logger("写 hookThread 文件结束！！！")
            } catch (Exception e) {
                println "写 hookThread 文件异常,${e.getMessage()}"
            }
        }
    }
}
