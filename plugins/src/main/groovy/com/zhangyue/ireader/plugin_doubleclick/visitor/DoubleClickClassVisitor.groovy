package com.zhangyue.ireader.plugin_doubleclick.visitor

import com.zhangyue.ireader.plugin_doubleclick.transform.LambdaInsnNode
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class DoubleClickClassVisitor extends ClassVisitor {

    String className

    List<LambdaInsnNode> lists

    DoubleClickClassVisitor(api, classVisitor, lambdaInsnNodeList) {
        super(api, classVisitor)
        this.lists = lambdaInsnNodeList
    }

    @Override
    void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces)
        className = name
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        DoubleClickMethodVisitor visitor = new DoubleClickMethodVisitor(Opcodes.ASM9, methodVisitor, access, name, descriptor, className,lists)
        return visitor
    }
}