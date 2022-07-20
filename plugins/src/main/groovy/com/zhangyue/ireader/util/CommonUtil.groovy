package com.zhangyue.ireader.util

import org.apache.commons.codec.digest.DigestUtils

class CommonUtil {

    static String path2ClassName(String pathName) {
        return pathName.replace(File.separator, ".").replace(".class", "")
    }

    static String fileSeparatorName(String name) {
        return name.replace(".", File.separator)
    }

    static String formatName(String annotation) {
        return "L" + annotation.replace(".", "/") + ";"
    }

    static String generateJarFileName(File jarFile) {
        return getMd5ByFilePath(jarFile) + "_" + jarFile.name
    }

    static String generateClassFileName(File classFile) {
        return getMd5ByFilePath(classFile) + "_" + classFile.name
    }


    private static getMd5ByFilePath(File file) {
        return DigestUtils.md5Hex(file.absolutePath).substring(0, 8)
    }

    static boolean isLegalJar(File file) {
        //需要一个额外的括号，否则编译不通过
        return (file.isFile()
                && file.name != "R.jar"
                && file.length() > 0L
                && file.name.endsWith(".jar"))
    }

    static boolean isLegalClass(File file) {
        return (file.isFile() && isLegalClass(file.name))
    }

    static boolean isLegalClass(String name) {
        return (name.endsWith(".class") && !isAndroidGeneratedClass(name))
    }


    private static boolean isAndroidGeneratedClass(String className) {
        return (className.contains('R$') ||
                className.contains('R2$') ||
                className.contains('R.class') ||
                className.contains('R2.class') ||
                className == 'BuildConfig.class')
    }
}
