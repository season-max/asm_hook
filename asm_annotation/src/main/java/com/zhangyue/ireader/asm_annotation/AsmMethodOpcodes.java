package com.zhangyue.ireader.asm_annotation;

import org.objectweb.asm.Opcodes;

/**
 * 应用层可以不依赖 org.ow2.asm:asm:9.0
 */
public class AsmMethodOpcodes {
    public static final int INVOKEVIRTUAL = Opcodes.INVOKEVIRTUAL;
    public static final int INVOKESPECIAL = Opcodes.INVOKESPECIAL;
    public static final int INVOKESTATIC = Opcodes.INVOKESTATIC;
    public static final int INVOKEINTERFACE = Opcodes.INVOKEINTERFACE;
    public static final int INVOKEDYNAMIC = Opcodes.INVOKEDYNAMIC;
}
