package com.zhangyue.ireader.plugin_doubleclick.visitor

import com.zhangyue.ireader.plugin_doubleclick.DoubleClickConfig
import com.zhangyue.ireader.plugin_doubleclick.transform.CheckDoubleClickTransform
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type
import org.objectweb.asm.commons.AdviceAdapter

class DoubleClickMethodVisitor extends AdviceAdapter {

    public boolean hasCheckClickAnnotation

    long duration


    DoubleClickMethodVisitor(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor)
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
        def viewParamIndex = viewParamIndex(argumentTypes, DoubleClickConfig.ViewDescriptor)
        //有对应的注解，插入检测代码
        if (hasCheckClickAnnotation) {
            mv.visitVarInsn(ALOAD, getVisitPosition(viewParamIndex, argumentTypes, isStatic()))
            mv.visitLdcInsn(new Long(duration))
            mv.visitMethodInsn(INVOKESTATIC, DoubleClickConfig.doubleClickCheckClass, DoubleClickConfig.doubleClickCheckMethod, DoubleClickConfig.doubleClickCheckMethodDesc, false)
            Label label1 = new Label()
            mv.visitJumpInsn(IFEQ, label1)
            mv.visitInsn(RETURN)
            mv.visitLabel(label1)
            mv.visitFrame(F_SAME, 0, null, 0, null);
        }
    }

    static int getVisitPosition(int index, types, isStatic) {
        if (index < 0 || index >= types.size()) {
            throw new Error("wrong view param index!")
        }
        return isStatic ? index : index + 1
    }

    static viewParamIndex(types, viewDescriptor) {
        return types.findIndexOf { type ->
            type.getDescriptor() == viewDescriptor
        }
    }

    private boolean isStatic() {
        return (methodAccess & INVOKESTATIC) == INVOKESTATIC
    }
}
