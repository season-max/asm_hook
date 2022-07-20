package com.zhangyue.ireader.plugin_doubleclick.visitor

import com.zhangyue.ireader.plugin_doubleclick.transform.CheckDoubleClickTransform
import org.objectweb.asm.AnnotationVisitor

class DoubleClickAnnotationVisitor extends AnnotationVisitor {

    private DoubleClickMethodVisitor methodVisitor

    DoubleClickAnnotationVisitor(int api, AnnotationVisitor annotationVisitor, DoubleClickMethodVisitor methodVisitor) {
        super(api, annotationVisitor)
        this.methodVisitor = methodVisitor
    }

    @Override
    void visit(String name, Object value) {
        if (name == CheckDoubleClickTransform.checkAnnotationName) {
            methodVisitor.duration = value
            println "duration--->" + methodVisitor.duration
        }
        super.visit(name, value)
    }

    @Override
    void visitEnum(String name, String descriptor, String value) {
        super.visitEnum(name, descriptor, value)
    }

    @Override
    AnnotationVisitor visitAnnotation(String name, String descriptor) {
        return super.visitAnnotation(name, descriptor)
    }

    @Override
    AnnotationVisitor visitArray(String name) {
        return super.visitArray(name)
    }

    @Override
    void visitEnd() {
        super.visitEnd()
    }
}