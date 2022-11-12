package com.zhangyue.ireader

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.zhangyue.ireader.util.CommonUtil
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.concurrent.AbstractExecutorService
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

abstract class BaseTransform extends Transform {
    //2
    AbstractExecutorService threadPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2)

    protected Project project

    BaseTransform(Project project) {
        this.project = project
    }

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
        println("${getName()} start--------------->")
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
        def taskList = new ArrayList<Callable<Void>>()
        inputs.each { input ->
            input.jarInputs.each {
                taskList.add(new Callable<Void>() {
                    @Override
                    Void call() throws Exception {
                        forEachJar(it, outputProvider, context, isIncremental)
                        return null
                    }
                })
            }

            input.directoryInputs.each {
                taskList.add(new Callable<Void>() {
                    @Override
                    Void call() throws Exception {
                        forEachDir(it, outputProvider, context, isIncremental)
                        return null
                    }
                })
            }
        }
        List<Future> futures = threadPool.invokeAll(taskList)
        //阻塞等待
        for (it in futures) {
            it.get()
        }

        //3
        onTransformEnd(transformInvocation)
        println("${getName()} end--------------->" + "duration : " + (System.currentTimeMillis() - startTime) + " ms")
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
        FileUtils.forceMkdir(dest)
        if (isIncremental) {
            directoryInput.getChangedFiles().each { entry ->
                def classFile = entry.key
                switch (entry.value) {
                    case Status.NOTCHANGED:
                        break
                    case Status.REMOVED:
                        //最终文件应该存放的路径
                        def destFilePath = classFile.absolutePath.replace(srcDirPath, destDirPath)
                        def destFile = File(destFilePath)
                        if (destFile.exists()) {
                            destFile.delete()
                        }
                        break
                    case Status.ADDED:
                    case Status.CHANGED:
                        transformClassFile(classFile, srcDirPath, destDirPath)
                        break
                    default:
                        break
                }
            }
        } else {
            com.android.utils.FileUtils.getAllFiles(inputDir).each { File file ->
                transformClassFile(file, srcDirPath, destDirPath)
            }
        }
    }

    void transformClassFile(classFile, srcDirPath, destDirPath) {
        //目标路径
        def destFilePath = classFile.absolutePath.replace(srcDirPath, destDirPath)
        def destFile = new File(destFilePath)
        if (destFile.exists()) {
            destFile.delete()
        }
        String className = CommonUtil.path2ClassName(classFile.absolutePath.replace(srcDirPath + File.separator, ""))
        byte[] sourceBytes = IOUtils.toByteArray(new FileInputStream(classFile))
        def modifyBytes = null
        if (CommonUtil.isLegalClass(classFile) && shouldHookClass(className)) {
            modifyBytes = hookClass(className, sourceBytes)
        }
        if (modifyBytes == null) {
            modifyBytes = sourceBytes
        }
        FileUtils.writeByteArrayToFile(destFile, modifyBytes, false)
    }

    void forEachJar(JarInput jarInput, TransformOutputProvider outputProvider, Context context, boolean isIncremental) {
        File destFile = outputProvider.getContentLocation(
                //防止同名被覆盖
                CommonUtil.generateJarFileName(jarInput.file), jarInput.contentTypes, jarInput.scopes, Format.JAR)
        //增量编译处理
        if (isIncremental) {
            Status status = jarInput.status
            switch (status) {
                case Status.NOTCHANGED:
                    break
                case Status.REMOVED:
                    if (destFile.exists()) {
                        FileUtils.forceDelete(destFile)
                    }
                    break
                case Status.ADDED:
                case Status.CHANGED:
                    CommonUtil.isLegalJar(jarInput.file) ? transformJar(jarInput.file, destFile)
                            : FileUtils.copyFile(jarInput.file, destFile)
                    break
            }
        } else {
            if (destFile.exists()) {
                FileUtils.forceDelete(destFile)
            }
            CommonUtil.isLegalJar(jarInput.file) ? transformJar(jarInput.file, destFile)
                    : FileUtils.copyFile(jarInput.file, destFile)
        }
    }

    def transformJar(File jarFile, File destFile) {
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(destFile))
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
                            modifiedByteArray = hookClass(className, sourceByteArray)
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
    }

    private byte[] hookClass(String className, byte[] sourceBytes) {
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

    private boolean shouldHookClass(String className) {
        //默认过滤 androidx、android.support
        def excludes = ['android.support', 'androidx']
        if (excludes != null) {
            for (String string : excludes) {
                if (className.startsWith(string)) {
                    return false
                }
            }
        }
        return shouldHookClassInner(className)
    }

    protected abstract boolean shouldHookClassInner(String className)

    protected abstract byte[] hookClassInner(String className, byte[] bytes)

    protected abstract void onTransformStart(TransformInvocation transformInvocation)

    protected abstract void onTransformEnd(TransformInvocation transformInvocation)
}