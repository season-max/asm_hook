package com.zhangyue.ireader.plugin_doubleclick.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class DoubleClickClassVisitor extends ClassVisitor {

    DoubleClickClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor)
    }

    @Override
    MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions)
        DoubleClickMethodVisitor visitor = new DoubleClickMethodVisitor(Opcodes.ASM9, methodVisitor, access, name, descriptor)
        return visitor
    }
}