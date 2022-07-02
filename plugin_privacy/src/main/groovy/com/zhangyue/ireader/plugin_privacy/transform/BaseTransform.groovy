package com.zhangyue.ireader.plugin_privacy.transform

import com.android.build.api.transform.Context
import com.android.build.api.transform.DirectoryInput
import com.android.build.api.transform.Format
import com.android.build.api.transform.JarInput
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Status
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.api.transform.TransformOutputProvider
import com.android.build.gradle.internal.pipeline.TransformManager
import com.zhangyue.ireader.plugin_privacy.PrivacyGlobalConfig
import com.zhangyue.ireader.plugin_privacy.util.CommonUtil
import com.zhangyue.ireader.plugin_privacy.util.Logger
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils

import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

abstract class BaseTransform extends Transform {
//    CountDownLatch countDownLatch = new CountDownLatch(2)

    //N(cpu 核心数)
    AbstractExecutorService executorService = Executors.newFixedThreadPool(6)

    @Override
    String getName() {
        return getClass().simpleName
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return true
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        println("transform start--------------->")
        if (firstTransform()) {
            printCopyRight()
        }
        onTransformStart(transformInvocation)
        def startTime = System.currentTimeMillis()
        def inputs = transformInvocation.inputs
        def outputProvider = transformInvocation.outputProvider
        def context = transformInvocation.context
        def isIncremental = transformInvocation.isIncremental()
        if (!isIncremental) {
            outputProvider.deleteAll()
        }
        //1
//        inputs.each { input ->
//            input.jarInputs.each { JarInput jarInput ->
//                forEachJar(jarInput, outputProvider, context, isIncremental)
//            }
//
//            input.directoryInputs.each { DirectoryInput dirInput ->
//                forEachDir(dirInput, outputProvider, context, isIncremental)
//            }
//        }

        //2
//        new Thread(new Runnable() {
//            @Override
//            void run() {
//                inputs.each { input ->
//                    input.jarInputs.each { JarInput jarInput ->
//                        forEachJar(jarInput, outputProvider, context, isIncremental)
//                    }
//                }
//                countDownLatch.countDown()
//            }
//        }).start()
//
//        new Thread(new Runnable() {
//            @Override
//            void run() {
//                inputs.each { input ->
//                    input.directoryInputs.each { DirectoryInput dirInput ->
//                        forEachDir(dirInput, outputProvider, context, isIncremental)
//                    }
//                }
//                countDownLatch.countDown()
//            }
//        }).start()
//        countDownLatch.await()

        //3
        List<FutureTask> taskList = new ArrayList<>()
        inputs.each { input ->
            input.jarInputs.each { jarInput ->
                taskList.add(new FutureTask(new Runnable() {
                    @Override
                    void run() {
                        forEachJar(jarInput, outputProvider, context, isIncremental)
                    }
                }, null))
            }

            input.directoryInputs.each { dirInput ->
                taskList.add(new FutureTask(new Runnable() {
                    @Override
                    void run() {
                        forEachDir(dirInput, outputProvider, context, isIncremental)
                    }
                }, null))
            }
        }
        for (FutureTask task : taskList) {
            executorService.execute(task)
        }

        taskList.each { it ->
            it.get()
        }
        onTransformEnd(transformInvocation)
        println(getName() + "transform end--------------->" + "duration : " + (System.currentTimeMillis() - startTime) + " ms")
    }


    void forEachDir(DirectoryInput directoryInput, TransformOutputProvider outputProvider, Context context, boolean isIncremental) {
        def inputDir = directoryInput.file
        File dest = outputProvider.getContentLocation(
                directoryInput.name,
                directoryInput.contentTypes,
                directoryInput.scopes,
                Format.DIRECTORY
        )
        def srcDirPath = inputDir.absolutePath
        def destDirPath = dest.absolutePath
        def temporaryDir = context.temporaryDir
        FileUtils.forceMkdir(dest)
        Logger.info("srcDirPath:${srcDirPath}, destDirPath:${destDirPath}")
        if (isIncremental) {
            directoryInput.getChangedFiles().each { entry ->
                def classFile = entry.key
                switch (entry.value) {
                    case Status.NOTCHANGED:
                        Logger.info("处理 class： " + classFile.absoluteFile + " NOTCHANGED")
                        break
                    case Status.REMOVED:
                        Logger.info("处理 class： " + classFile.absoluteFile + " REMOVED")
                        //最终文件应该存放的路径
                        def destFilePath = classFile.absolutePath.replace(srcDirPath, destDirPath)
                        def destFile = File(destFilePath)
                        if (destFile.exists()) {
                            destFile.delete()
                        }
                        break
                    case Status.ADDED:
                    case Status.CHANGED:
                        Logger.info("处理 class： " + classFile.absoluteFile + " ADDED or CHANGED")
                        modifyClassFile(classFile, srcDirPath, destDirPath, temporaryDir)
                        break
                    default:
                        break
                }
            }
        } else {
            com.android.utils.FileUtils.getAllFiles(inputDir).each { File file ->
                modifyClassFile(file, srcDirPath, destDirPath, temporaryDir)
            }
        }
    }

    void modifyClassFile(classFile, srcDirPath, destDirPath, temporaryDir) {
        Logger.info("处理 class： " + classFile.absoluteFile)
        //目标路径
        def destFilePath = classFile.absolutePath.replace(srcDirPath, destDirPath)
        def destFile = new File(destFilePath)
        if (destFile.exists()) {
            destFile.delete()
        }
        Logger.info("处理 class： destFile" + destFile.absoluteFile)
        String className = CommonUtil.path2ClassName(classFile.absolutePath.replace(srcDirPath + File.separator, ""))
        Logger.info("处理 className： " + className)
        File modifyFile = null
        if (CommonUtil.isLegalClass(classFile) && shouldHookClass(className)) {
            modifyFile = getModifyFile(classFile, temporaryDir, className)
        }
        if (modifyFile == null) {
            modifyFile = classFile
        }
        FileUtils.copyFile(modifyFile, destFile)
    }

    File getModifyFile(File classFile, File temporaryDir, String className) {
        byte[] sourceBytes = IOUtils.toByteArray(new FileInputStream(classFile))
        def tempFile = new File(temporaryDir, CommonUtil.generateClassFileName(classFile))
        if (tempFile.exists()) {
            FileUtils.forceDelete(tempFile)
        }
        def modifyBytes = modifyClass(className, sourceBytes)
        if (modifyBytes == null) {
            modifyBytes = sourceBytes
        }
        tempFile.createNewFile()
        def fos = new FileOutputStream(tempFile)
        fos.write(modifyBytes)
        fos.flush()
        IOUtils.closeQuietly(fos)
        return tempFile
    }


    void forEachJar(JarInput jarInput, TransformOutputProvider outputProvider, Context context, boolean isIncremental) {
        Logger.info("jarInput:" + jarInput.file)
        File destFile = outputProvider.getContentLocation(
                //防止同名被覆盖
                CommonUtil.generateJarFileName(jarInput.file), jarInput.contentTypes, jarInput.scopes, Format.JAR)
        //增量编译处理
        if (isIncremental) {
            Status status = jarInput.status
            switch (status) {
                case Status.NOTCHANGED:
                    Logger.info("处理 jar： " + jarInput.file.absoluteFile + " NotChanged")
                    //Do nothing
                    return
                case Status.REMOVED:
                    Logger.info("处理 jar： " + jarInput.file.absoluteFile + " REMOVED")
                    if (destFile.exists()) {
                        FileUtils.forceDelete(destFile)
                    }
                    return
                case Status.ADDED:
                case Status.CHANGED:
                    Logger.info("处理 jar： " + jarInput.file.absoluteFile + " ADDED or CHANGED")
                    break
            }
        }
        if (destFile.exists()) {
            FileUtils.forceDelete(destFile)
        }
        CommonUtil.isLegalJar(jarInput.file) ? transformJar(jarInput.file, context.getTemporaryDir(), destFile)
                : FileUtils.copyFile(jarInput.file, destFile)
    }

    def transformJar(File jarFile, File temporaryDir, File destFile) {
        Logger.info("处理 jar： " + jarFile.absoluteFile)
        File tempOutputJarFile = new File(temporaryDir, CommonUtil.generateJarFileName(jarFile))
        if (tempOutputJarFile.exists()) {
            FileUtils.forceDelete(tempOutputJarFile)
        }
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tempOutputJarFile))
        JarFile inputJarFile = new JarFile(jarFile, false)
        try {
            def entries = inputJarFile.entries()
            while (entries.hasMoreElements()) {
                def jarEntry = entries.nextElement()
                def entryName = jarEntry.getName()
                def inputStream = inputJarFile.getInputStream(jarEntry)
                try {
                    byte[] sourceByteArray = IOUtils.toByteArray(inputStream)
                    def modifiedByteArray = null
                    if (!jarEntry.isDirectory() && CommonUtil.isLegalClass(entryName)) {
                        String className = CommonUtil.path2ClassName(entryName)
                        if (shouldHookClass(className)) {
                            modifiedByteArray = modifyClass(className, sourceByteArray)
                        }
                    }
                    if (modifiedByteArray == null) {
                        modifiedByteArray = sourceByteArray
                    }
                    jarOutputStream.putNextEntry(new JarEntry(entryName))
                    jarOutputStream.write(modifiedByteArray)
                    jarOutputStream.closeEntry()
                } finally {
                    IOUtils.closeQuietly(inputStream)
                }
            }
        } finally {
            jarOutputStream.flush()
            IOUtils.closeQuietly(jarOutputStream)
            IOUtils.closeQuietly(inputJarFile)
        }
        FileUtils.copyFile(tempOutputJarFile, destFile)
    }

    private byte[] modifyClass(String className, byte[] sourceBytes) {
        byte[] classBytesCode
        try {
            classBytesCode = hookClassInner(className, sourceBytes)
        } catch (Throwable e) {
            e.printStackTrace()
            classBytesCode = null
            println "throw exception when modify class ${className}"
        }
        return classBytesCode
    }

    /**
     * 打印日志信息
     */
    static void printCopyRight() {
        println()
        println '#######################################################################'
        println '##########                                                    '
        println '##########                欢迎使用隐私合规处理插件'
        println '##########                                                    '
        println '#######################################################################'
        println '##########                                                    '
        println '##########                 插件配置参数                         '
        println '##########                                                    '
        println '##########                -isDebug: ' + PrivacyGlobalConfig.isDebug
        println '##########                -inject: ' + PrivacyGlobalConfig.shouldInject
        println '##########                -handleAnnotationName: ' + PrivacyGlobalConfig.handleAnnotationName
        println '##########                -exclude: ' + PrivacyGlobalConfig.exclude
        println '##########                                                    '
        println '##########                                                    '
        println '##########                                                    '
        println '#######################################################################'
        println()
    }

    protected boolean firstTransform(){
        return false
    }

    protected abstract boolean shouldHookClass(String className)

    protected abstract byte[] hookClassInner(String className, byte[] bytes)

    protected abstract void onTransformStart(TransformInvocation transformInvocation)

    protected abstract void onTransformEnd(TransformInvocation transformInvocation)
}