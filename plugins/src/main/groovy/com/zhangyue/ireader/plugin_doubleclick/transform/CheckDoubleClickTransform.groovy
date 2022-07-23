package com.zhangyue.ireader.plugin_doubleclick.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.BaseTransform
import com.zhangyue.ireader.plugin_doubleclick.DoubleClickConfig
import com.zhangyue.ireader.plugin_doubleclick.visitor.DoubleClickClassVisitor
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Type
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FrameNode
import org.objectweb.asm.tree.InsnList
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.JumpInsnNode
import org.objectweb.asm.tree.LabelNode
import org.objectweb.asm.tree.LdcInsnNode
import org.objectweb.asm.tree.MethodInsnNode
import org.objectweb.asm.tree.VarInsnNode
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
        List<LambdaInsnNode> lambdaInsnNodeList = new ArrayList<>()
        ClassReader cr = new ClassReader(bytes)
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        ClassVisitor classVisitor = new DoubleClickClassVisitor(Opcodes.ASM9, cw, lambdaInsnNodeList)
        cr.accept(classVisitor, ClassReader.EXPAND_FRAMES)
        //遍历 lambda 指令
        ClassNode classNode = new ClassNode()
        cr = new ClassReader(cw.toByteArray())
        cr.accept(classNode, ClassReader.EXPAND_FRAMES)
        if (!lambdaInsnNodeList.isEmpty()) {
            classNode.methods.each { methodNode ->
                def argumentTypes = Type.getArgumentTypes(methodNode.desc)
                def viewParamIndex = viewParamIndex(argumentTypes, DoubleClickConfig.ViewDescriptor)
                insertLambda(cr.className, methodNode, viewParamIndex, argumentTypes, lambdaInsnNodeList)
            }
        }
        def classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        classNode.accept(classWriter)
        return classWriter.toByteArray()
    }


    private static void insertLambda(className, methodNode, viewParamIndex, argumentTypes, lambdaInsnNodeList) {
        lambdaInsnNodeList.each { node ->
            if (node.owner == className
                    && node.handleName == methodNode.name
                    && node.handleDesc == methodNode.desc) {
                println "查找到 lambda 方法，准备插入"
                println "className=${className},name=${methodNode.name} ,desc=${methodNode.desc} ,"
                println "viewParamIndex=${viewParamIndex}"
                InsnList insnList = new InsnList()
                def position = getVisitPosition(viewParamIndex, argumentTypes, isStatic(methodNode.access))
                println "param positon:${position}"
                insnList.add(new VarInsnNode(Opcodes.ALOAD, position))
                insnList.add(new LdcInsnNode(new Long(DoubleClickConfig.doubleClickCheckDuration)))
                insnList.add(new MethodInsnNode(Opcodes.INVOKESTATIC, DoubleClickConfig.doubleClickCheckClass, DoubleClickConfig.doubleClickCheckMethod, DoubleClickConfig.doubleClickCheckMethodDesc, false))
                def labelNode = new LabelNode()
                insnList.add(new JumpInsnNode(Opcodes.IFEQ, labelNode))
                insnList.add(new InsnNode(Opcodes.RETURN))
                insnList.add(labelNode)
                insnList.add(new FrameNode(Opcodes.F_SAME, 0, null, 0, null))
                methodNode.instructions.insert(insnList)
                println "--->插入完成"
            }
        }
    }

    static viewParamIndex(types, viewDescriptor) {
        return types.findIndexOf { type ->
            type.getDescriptor() == viewDescriptor
        }
    }


    static int getVisitPosition(int index, types, isStatic) {
        if (index < 0 || index >= types.size()) {
            throw new Error("wrong view param index!")
        }
        return isStatic ? index : index + 1
    }


    static boolean isStatic(int methodAccess) {
        return (methodAccess & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC
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