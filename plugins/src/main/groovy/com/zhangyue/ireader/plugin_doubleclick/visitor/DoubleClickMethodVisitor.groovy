package com.zhangyue.ireader.plugin_doubleclick.visitor

import com.zhangyue.ireader.plugin_doubleclick.DoubleClickConfig
import com.zhangyue.ireader.plugin_doubleclick.transform.CheckDoubleClickTransform
import com.zhangyue.ireader.plugin_doubleclick.transform.LambdaInsnNode
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Handle
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class DoubleClickMethodVisitor extends AdviceAdapter {

    String className

    public boolean hasCheckClickAnnotation

    long duration

    List<LambdaInsnNode> list


    DoubleClickMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String className, List<LambdaInsnNode> list) {
        super(api, methodVisitor, access, name, descriptor)
        this.className = className
        this.list = list
    }

    @Override
    void visitMethodInsn(int opcodeAndSource, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcodeAndSource, owner, name, descriptor, isInterface)
    }

    //访问动态指令
    @Override
    void visitInvokeDynamicInsn(String name, String descriptor, Handle bootstrapMethodHandle, Object... bootstrapMethodArguments) {
        if (name == DoubleClickConfig.clickLambdaName && descriptor.contains(DoubleClickConfig.clickLambdaInterfaces)) {
            println "----> 遍历到 lambda 指令，className=${className},name=${name},des=${descriptor}"
            def owner = bootstrapMethodHandle.owner
            def handleName = bootstrapMethodHandle.name
            def handleDesc = bootstrapMethodHandle.desc
            println "----> owner=${owner},handleName=${handleName},handleDesc=${handleDesc}"
            if (bootstrapMethodArguments[1] instanceof Handle) {
                Handle handle1 = bootstrapMethodArguments[1]
                LambdaInsnNode node = new LambdaInsnNode()
                node.owner = handle1.owner
                node.handleName = handle1.name
                node.handleDesc = handle1.desc
                list.add(node)
            }
        }
        super.visitInvokeDynamicInsn(name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments)

    }

    @Override
    AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        if (descriptor == CheckDoubleClickTransform.checkAnnotation) {
            AnnotationVisitor annotationVisitor = super.visitAnnotation(descriptor, visible)
            DoubleClickAnnotationVisitor doubleClickAnnotationVisitor = new DoubleClickAnnotationVisitor(api, annotationVisitor, this)
            hasCheckClickAnnotation = true
            return doubleClickAnnotationVisitor
        } else {
            return super.visitAnnotation(descriptor, visible)
        }
    }

    @Override
    void visitParameter(String name, int access) {
        super.visitParameter(name, access)
    }
/**
 *  static String doubleClickCheckClass = "com.zhangyue.ireader.toolslibrary.doubleclick.DoubleClickConfig"
 static String doubleClickCheckMethod = "inDoubleClick"
 static String doubleClickCheckMethodDesc = "(Landroid/view/View;J)Z"
 */
    @Override
    protected void onMethodEnter() {
        super.onMethodEnter()
        def argumentTypes = Type.getArgumentTypes(methodDesc)
        def viewParamIndex = CheckDoubleClickTransform.viewParamIndex(argumentTypes, DoubleClickConfig.ViewDescriptor)
        //有对应的注解，插入检测代码
        if (hasCheckClickAnnotation) {
            mv.visitVarInsn(ALOAD, CheckDoubleClickTransform.getVisitPosition(viewParamIndex, argumentTypes, CheckDoubleClickTransform.isStatic(access)))
            mv.visitLdcInsn(new Long(duration))
            mv.visitMethodInsn(INVOKESTATIC, DoubleClickConfig.doubleClickCheckClass, DoubleClickConfig.doubleClickCheckMethod, DoubleClickConfig.doubleClickCheckMethodDesc, false)
            Label label1 = new Label()
            mv.visitJumpInsn(IFEQ, label1)
            mv.visitInsn(RETURN)
            mv.visitLabel(label1)
            mv.visitFrame(F_SAME, 0, null, 0, null)
        }
    }


}
