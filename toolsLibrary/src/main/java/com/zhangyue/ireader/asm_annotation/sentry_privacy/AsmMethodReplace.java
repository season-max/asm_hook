package com.zhangyue.ireader.asm_annotation.sentry_privacy;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 收集和注解匹配的方法
 * visitMethodInsn(int opcode, String owner, String name,String desc)
 *
 * ======如果 originName 和 originDesc 传""，逻辑会在插件中处理=====
 */
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.METHOD})
public @interface AsmMethodReplace {

    /**
     * 指令操作码
     */
    int targetMethodOpcode();

    /**
     * 方法方法所有者类
     */
    String targetClass();

    /**
     * 方法名称
     */
    String targetName() default "";

    /**
     * 方法描述符
     */
    String targetDesc() default "";

    /**
     * 是否进行 hook
     */
    boolean hook() default false;

}
