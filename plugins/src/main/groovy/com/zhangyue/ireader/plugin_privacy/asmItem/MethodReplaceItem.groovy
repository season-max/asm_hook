package com.zhangyue.ireader.plugin_privacy.asmItem

import com.zhangyue.ireader.util.Logger
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.MethodNode

/**
 * int targetMethodOpcode();
 *
 * String targetClass();
 *
 * String targetName();
 *
 * String targetDesc();
 */
class MethodReplaceItem {

    public String replaceClass

    public String replaceMethod

    public String replaceDesc

    public int replaceOpcode

    public String targetOwner

    public String targetMethod

    public String targetDesc

    public int targetOpcode

    public boolean willHook


    MethodReplaceItem(List<Object> annotationPair, MethodNode methodNode, String owner) {
        replaceOpcode = Opcodes.INVOKESTATIC
        replaceClass = owner
        replaceMethod = methodNode.name
        replaceDesc = methodNode.desc

        for (int i = 0; i < annotationPair.size(); i = i + 2) {
            def key = annotationPair[i]
            def value = annotationPair[i + 1]
            if (key == "targetMethodOpcode") {
                targetOpcode = value
            } else if (key == "targetClass") {
                targetOwner = value
            } else if (key == "targetName") {
                targetMethod = value
            } else if (key == "targetDesc") {
                targetDesc = value
            }else if(key == "hook"){
                willHook = value
            }
        }
        if (isEmpty(targetMethod)) {
            targetMethod = replaceMethod
        }
        if (isEmpty(targetDesc)) {
            //静态方法，oriDesc 跟 targetDesc 一样
            if (targetOpcode == Opcodes.INVOKESTATIC) {
                targetDesc = replaceDesc
            } else {
                //非静态方法，约定第一个参数是实例类名，oriDesc 比 targetDesc 少一个参数，处理一下
                // (Landroid/telephony/TelephonyManager;)Ljava/lang/String ->  ()Ljava/lang/String
                def param = replaceDesc.split('\\)')[0] + ")"
                def result = replaceDesc.split('\\)')[1]
                def index = replaceDesc.indexOf(targetOwner)
                if (index != -1) {
                    param = "(" + param.substring(index + targetOwner.length() + 1)
                }
                targetDesc = param + result
            }
        }
    }

    static boolean isEmpty(String str) {
        return str == null || str.isEmpty()
    }


    @Override
    String toString() {
        return "MethodReplaceItem" +
                "{" +
                "replaceClass='" + replaceClass + '\'' +
                ", replaceMethod='" + replaceMethod + '\'' +
                ", replaceDesc='" + replaceDesc + '\'' +
                ", replaceOpcode=" + replaceOpcode +
                ", targetOwner='" + targetOwner + '\'' +
                ", targetMethod='" + targetMethod + '\'' +
                ", targetDesc='" + targetDesc + '\'' +
                ", targetOpcode=" + targetOpcode +
                '}'
    }
}