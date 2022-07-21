package com.zhangyue.ireader.plugin_privacy.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.BaseTransform
import com.zhangyue.ireader.plugin_privacy.PrivacyGlobalConfig
import com.zhangyue.ireader.plugin_privacy.asmItem.MethodReplaceItem
import com.zhangyue.ireader.util.CommonUtil
import com.zhangyue.ireader.util.Logger
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.MethodNode
import org.objectweb.asm.tree.TypeInsnNode


/**
 * 替换隐私合规相关方法 transform
 */
class PrivacyMethodReplaceTransform extends BaseTransform {


    PrivacyMethodReplaceTransform(Project project) {
        super(project)
    }

    @Override
    boolean shouldHookClassInner(String className) {
        return true
    }

    @Override
    byte[] hookClassInner(String className, byte[] bytes) {
        Logger.info("${getName()} modifyClassInner--------------->")
        def findHookPoint = false
        Map<MethodNode, InsertInsnPoint> collectMap = new HashMap<>()
        ClassReader cr = new ClassReader(bytes)
        ClassNode classNode = new ClassNode()
        cr.accept(classNode, ClassReader.EXPAND_FRAMES)
        classNode.methods.each { methodNode ->
            if (isNotHookMethod(cr.className, methodNode)) {
                methodNode.instructions.each { insnNode ->
                    def methodReplaceItem = searchHookPoint(insnNode)
                    if (methodReplaceItem != null) {
                        def inject = methodReplaceItem.willHook
                        logHookPoint(classNode.name, methodReplaceItem, methodNode, insnNode.opcode, insnNode.owner, insnNode.name, insnNode.desc, inject)
                        if (inject) {
                            //hook
                            injectInsn(insnNode, methodReplaceItem)
                        }
                        //收集调用隐私方法的堆栈
                        collectInsertInsn(insnNode, methodNode, classNode, collectMap, inject)
                        findHookPoint = true
                    }
                }
            }
        }
        if (!collectMap.isEmpty() && findHookPoint) {
            //插入写文件指令，用来展示堆栈信息
            collectMap.each { key, value ->
                key.instructions.insert(value.hookInsnNode, value.instList)
            }
            //插入 writeToFile 方法
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
            classNode.accept(cw)
            insertWriteToFileMethod(cw)
            return cw.toByteArray()
        }
        return bytes
    }

    /**
     * 过滤有注解的用来 hook 的方法
     */
    static boolean isNotHookMethod(String className, MethodNode methodNode) {
        def findNode = null
        methodNode.invisibleAnnotations.find { anno ->
            if (anno.desc == PrivacyGlobalConfig.getHandleAnnotationName()) {
                findNode = anno
                println "过滤方法${className} -> ${methodNode.name} -> ${methodNode.desc}"
                //break looping
                return true
            }
            //continue looping
            return false
        }
        return findNode == null
    }

    /**
     * 替换字节码指令
     */
    static void injectInsn(insnNode, methodReplaceItem) {
        if (insnNode instanceof MethodInsnNode) {
            insnNode.opcode = methodReplaceItem.replaceOpcode
            insnNode.owner = methodReplaceItem.replaceClass
            insnNode.name = methodReplaceItem.replaceMethod
            insnNode.desc = methodReplaceItem.replaceDesc

            println "----> 替换字节码"
        }
    }

    static String writeToFileMethodName = "writeToFile"

    static String writeToFileMethodDesc = "(Ljava/lang/String;Ljava/lang/Throwable;)V"

    /**
     * 插入 writeToFile 方法
     * @param classWriter classWriter
     *
     */
    static def insertWriteToFileMethod(ClassWriter classWriter) {
        MethodVisitor methodVisitor = classWriter.visitMethod(Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC,
                writeToFileMethodName,
                writeToFileMethodDesc,
                null,
                null)
        methodVisitor.visitCode()
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/io/ByteArrayOutputStream")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/io/ByteArrayOutputStream",
                "<init>",
                "()V",
                false
        )
        // 0-String log     1-Throwable throwable
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 2)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 1)
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/io/PrintStream")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/io/PrintStream",
                "<init>",
                "(Ljava/io/OutputStream;)V",
                false
        )
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/Throwable",
                "printStackTrace",
                "(Ljava/io/PrintStream;)V",
                false
        )
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 2)
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/io/ByteArrayOutputStream",
                "toString",
                "()Ljava/lang/String;",
                false
        )
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 3)
        methodVisitor.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder")
        methodVisitor.visitInsn(Opcodes.DUP)
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "java/lang/StringBuilder",
                "<init>",
                "()V",
                false
        )
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 0)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
        )
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 3)
        methodVisitor.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "append",
                "(Ljava/lang/String;)Ljava/lang/StringBuilder;"
        )
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKEVIRTUAL,
                "java/lang/StringBuilder",
                "toString",
                "()Ljava/lang/String;",
                false
        )
        methodVisitor.visitVarInsn(Opcodes.ASTORE, 4)
        methodVisitor.visitVarInsn(Opcodes.ALOAD, 4)
        methodVisitor.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                CommonUtil.fileSeparatorName(PrivacyGlobalConfig.recordOwner),
                PrivacyGlobalConfig.recordMethod,
                PrivacyGlobalConfig.recordDesc,
                false
        )
        methodVisitor.visitInsn(Opcodes.RETURN)
        methodVisitor.visitMaxs(4, 5)
        methodVisitor.visitEnd()
        println "插入方法完成 =---------->"
    }

    /**
     * 收集 在调用特定的方法前插入调用写入文件的方法的指令
     * @param insnNode
     * @param methodNode
     * @param classNode
     * @param collectMap
     */
    static void collectInsertInsn(insnNode, methodNode, classNode, collectMap, Inject) {
        def className = classNode.name
        def methodName = methodNode.name
        def methodDesc = methodNode.desc
        def owner = null
        def name = null
        def desc = null
        if (insnNode instanceof MethodInsnNode) {
            owner = insnNode.owner
            name = insnNode.name
            desc = insnNode.desc
        }
        //------log
        StringBuilder lintLog = new StringBuilder()
        lintLog.append(className)
        lintLog.append("  ->  ")
        lintLog.append(methodName)
        lintLog.append("  ->  ")
        lintLog.append(methodDesc)
        lintLog.append("\r\n")
        lintLog.append(owner)
        lintLog.append("  ->  ")
        lintLog.append(name)
        lintLog.append("  ->  ")
        lintLog.append(desc)

        //------要插入字节码指令
        lintLog.append("\r\n")
        InsnList insnList = new InsnList()
        insnList.add(new LdcInsnNode(lintLog.toString()))
        insnList.add(new TypeInsnNode(Opcodes.NEW, "java/lang/Throwable"))
        insnList.add(new InsnNode(Opcodes.DUP))
        insnList.add(new MethodInsnNode(Opcodes.INVOKESPECIAL, "java/lang/Throwable", "<init>", "()V", false))
        insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, className, writeToFileMethodName, writeToFileMethodDesc))
        println "插入指令完成 =---------->"
        collectMap.put(methodNode, new InsertInsnPoint(insnList, insnNode))
    }

    static class InsertInsnPoint {

        InsertInsnPoint(InsnList instList, AbstractInsnNode hookInsnNode) {
            this.instList = instList
            this.hookInsnNode = hookInsnNode
        }
        InsnList instList
        AbstractInsnNode hookInsnNode
    }


    /**
     * 遍历方法指令，寻找 hook 点
     *
     */
    static MethodReplaceItem searchHookPoint(AbstractInsnNode insnNode) {
        def hookPoint = null
        if (insnNode instanceof MethodInsnNode) {
            def opcode = insnNode.opcode
            def owner = insnNode.owner
            def name = insnNode.name
            def desc = insnNode.desc
            PrivacyGlobalConfig.methodReplaceItemList.find { item ->
                if (item.targetOpcode == opcode
                        && item.targetOwner == owner
                        && item.targetMethod == name
                        && item.targetDesc == desc) {
                    println "searchHookPoint ---> " + item.toString()
                    hookPoint = item
                }
            }
        }
        return hookPoint
    }

    @Override
    void onTransformStart(TransformInvocation transformInvocation) {
        Logger.info("${getName()} start--------------->")
        PrivacyGlobalConfig.stringBuilder.setLength(0)
    }

    @Override
    void onTransformEnd(TransformInvocation transformInvocation) {
        Logger.info("${getName()} end--------------->")
        //写入文件
        byte[] bytes = PrivacyGlobalConfig.stringBuilder.toString().getBytes("UTF-8")
        try {
            println "project.path= ${project.rootDir}"
            def targetFile = new File(project.rootDir, "replaceInsn.txt")
            if (targetFile.exists()) {
                targetFile.delete()
            }
            targetFile.withOutputStream { it ->
                it.write(bytes)
            }
            println "写文件结束，path${targetFile.absolutePath}"
        } catch (Exception e) {
            println "写文件时异常，${e.getMessage()}"
        }
    }

    /**
     * 静态扫描。记录包含隐私合规方法的类、方法、字节码指令等相关信息
     */
    static void logHookPoint(className, item, methodNode, opcode, owner, name, descriptor, inject) {
        println("==========scan insn success " +
                "${opcode} ${owner} ${name} ${descriptor} ======="
        )
        PrivacyGlobalConfig.stringBuilder.append("targetClass= ${CommonUtil.path2ClassName(className)}")
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
        PrivacyGlobalConfig.stringBuilder.append("invokeMethod= ${methodNode.name}  ${methodNode.desc}")
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
        PrivacyGlobalConfig.stringBuilder.append("opcode=${opcode}, owner=${owner}, name=${name}, descriptor=${descriptor}")
        if (inject) {
            PrivacyGlobalConfig.stringBuilder.append("\r\n")
            PrivacyGlobalConfig.stringBuilder.append('------>')
            PrivacyGlobalConfig.stringBuilder.append("\r\n")
            PrivacyGlobalConfig.stringBuilder.append("opcode=${item.replaceOpcode}, owner=${item.replaceClass}, name=${item.replaceMethod}, descriptor=${item.replaceDesc}")
        }
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
        PrivacyGlobalConfig.stringBuilder.append("\r\n")
    }
}