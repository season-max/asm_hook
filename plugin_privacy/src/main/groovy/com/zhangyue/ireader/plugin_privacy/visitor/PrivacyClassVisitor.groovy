package com.zhangyue.ireader.plugin_privacy.visitor


import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class PrivacyClassVisitor extends ClassVisitor {

    static final int API = Opcodes.ASM9

    String className

    boolean hasFound

    boolean hasGeneratorWriteToFileMethod = false


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

        return new PrivacyMethodVisitor(API, methodVisitor, access, name, descriptor, this)
    }

    @Override
    void visitEnd() {
        generatorWriteToFileMethod()
        super.visitEnd()

//        Logger.info("------结束扫描类${this.className}-----")
    }

    private String writeToFileMethodName = "writeToFile"

    private String writeToFileMethodDesc = "(Ljava/lang/String;Ljava/lang/Throwable;)V"

    void generatorWriteToFileMethod() {
        if (hasFound) {
            return
        }
        if (hasGeneratorWriteToFileMethod) {
            return
        }
        println '----->generatorWriteToFileMethod'
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PRIVATE,writeToFileMethodName,writeToFileMethodDesc,null,null)
        mv.visitCode()
        methodVisitor.visitLdcInsn("season")
        methodVisitor.visitLdcInsn("--->writeToFile")
        methodVisitor.visitMethodInsn(INVOKESTATIC, "android/util/Log", "i", "(Ljava/lang/String;Ljava/lang/String;)I", false)
        methodVisitor.visitInsn(POP)
        methodVisitor.visitInsn(RETURN)
        methodVisitor.visitMaxs(2, 2)
        mv.visitEnd()
        println '----->generatorWriteToFileMethod  End'
        hasGeneratorWriteToFileMethod = true
    }
}