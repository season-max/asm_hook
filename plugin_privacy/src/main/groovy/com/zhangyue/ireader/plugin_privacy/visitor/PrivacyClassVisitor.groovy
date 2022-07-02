package com.zhangyue.ireader.plugin_privacy.visitor


import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class PrivacyClassVisitor extends ClassVisitor {

    public static final int API = Opcodes.ASM9

    private String className


    PrivacyClassVisitor(ClassVisitor classVisitor) {
        super(API, classVisitor)
    }


    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
//        Logger.info("------开始扫描类${name}-----")
        this.className = name
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)

        return new PrivacyMethodVisitor(API, methodVisitor, access, name, descriptor, className)
    }

    @Override
    void visitEnd() {
        super.visitEnd()

//        Logger.info("------结束扫描类${this.className}-----")
    }
}