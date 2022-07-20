package com.zhangyue.ireader.asm_annotation.double_click;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CheckDoubleClick {

    /**
     * 间隔 ，默认 500 ms
     */
    long checkClick() default 500;

}
