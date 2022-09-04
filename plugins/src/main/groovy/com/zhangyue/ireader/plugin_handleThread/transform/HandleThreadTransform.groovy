package com.zhangyue.ireader.plugin_handleThread.transform

import com.android.build.api.transform.TransformInvocation
import com.zhangyue.ireader.BaseTransform
import com.zhangyue.ireader.plugin_handleThread.Config
import com.zhangyue.ireader.plugin_handleThread.RecordThreadPosition
import com.zhangyue.ireader.util.CommonUtil
import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type
import org.objectweb.asm.tree.*

/**
 * 线程处理 transform
 */
class HandleThreadTransform extends BaseTransform {

    /**
     * 长度为 0 字符
     */
    public static final String MARK = "\u200B"

    final static String STRING_CLASS_DES = 'Ljava/lang/String;'

    /**
     * Thread class 的规范名称
     */
    final static String THREAD_CLASS = "java/lang/Thread"

    /**
     * threadPoolExecutor
     */
    final static String THREAD_POOL_EXECUTOR = 'java/util/concurrent/ThreadPoolExecutor'

    /**
     * Executors
     */
    final static String THREAD_POOL_UTIL_EXECUTORS = 'java/util/concurrent/Executors'

    /**
     * 线程处理工具集合
     */
    static String TOOL_LIBRARY_PACKAGE = 'com/zhangyue/ireader/toolslibrary/'
    // 包名
    static String OPTIMIZE_THREAD_PACKAGE = TOOL_LIBRARY_PACKAGE + 'optimizeThread/'
    // 线程处理类
    static String SHADOW_THREAD = OPTIMIZE_THREAD_PACKAGE + "ShadowThread"
    // Executors 处理类
    static String SHADOW_EXECUTORS = OPTIMIZE_THREAD_PACKAGE + "ShadowExecutors"

    static String SHADOW_THREAD_POOL_EXECUTOR = OPTIMIZE_THREAD_PACKAGE + 'ShadowThreadPoolExecutor';
    //线程工厂工具类
    static String NAMED_THREAD_FACTORY = OPTIMIZE_THREAD_PACKAGE + 'NamedThreadFactory'

    HandleThreadTransform(Project project) {
        super(project)
    }

    @Override
    protected boolean shouldHookClassInner(String className) {
        return Config.turnOn
    }

    @Override
    protected byte[] hookClassInner(String className, byte[] bytes) {
        if (CommonUtil.getClassInternalName(className).startsWith(TOOL_LIBRARY_PACKAGE)) {
            Config.logger("过滤工具类-->" + className)
            return bytes
        }
        ClassReader cr = new ClassReader(bytes)
        ClassNode cn = new ClassNode(Opcodes.ASM9)
        cr.accept(cn, ClassReader.EXPAND_FRAMES)
        cn.methods.each { methodNode ->
            methodNode.instructions.each { insnNode ->
                switch (insnNode.opcode) {
                    case Opcodes.NEW:
                        transformNew(cn, methodNode, insnNode)
                        break
                    case Opcodes.INVOKESPECIAL:
                        transformInvokeSpecial(cn, methodNode, insnNode)
                        break
                    case Opcodes.INVOKESTATIC:
                        transformInvokeStatic(cn, methodNode, (MethodInsnNode) insnNode)
                        break
//                    case Opcodes.INVOKEVIRTUAL:
//                        transformInvokeVirtual(cn, methodNode, (MethodInsnNode) insnNode)
//                        break
                    case Opcodes.ARETURN:
                        transformAReturn(cn, methodNode, (InsnNode) insnNode)
                        break
                    default:
                        break
                }
            }
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS)
        cn.accept(cw)
        return cw.toByteArray()
    }


    static def transformInvokeStatic(cn, methodNode, insnNode) {
        if (insnNode.owner == THREAD_POOL_UTIL_EXECUTORS) {
            switch (insnNode.name) {
                case 'newCachedThreadPool':
                case 'newFixedThreadPool':
                case 'newSingleThreadExecutor':
                    transformThreadPool(cn, methodNode, insnNode, Config.enableThreadPoolOptimized)

                    recordPosition(cn, methodNode)
                    break
                case 'newScheduledThreadPool':
                case 'newSingleThreadScheduledExecutor':
                    //对于执行定时任务的线程池，不做允许核心线程超时的优化。如果做了优化，可能带来线程周期性的销毁和重建的负面效果
                    transformThreadPool(cn, methodNode, insnNode, false)

                    recordPosition(cn, methodNode)
                    break
                case 'defaultThreadFactory':
                    methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))
                    insnNode.owner = SHADOW_EXECUTORS
                    def index = insnNode.desc.lastIndexOf(')')
                    insnNode.desc = insnNode.desc.substring(0, index) + 'Ljava/lang/String;' + insnNode.desc.substring(index)
                    break
                default:
                    break
            }
        }
    }

    static def transformThreadPool(ClassNode cn, MethodNode methodNode, MethodInsnNode insnNode, boolean enableThreadPoolOptimized) {
        methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))
        insnNode.owner = SHADOW_EXECUTORS
        def index = insnNode.desc.lastIndexOf(')')
        insnNode.desc = insnNode.desc.substring(0, index) + STRING_CLASS_DES + insnNode.desc.substring(index)
        insnNode.name = enableThreadPoolOptimized ? insnNode.name.replace('new', 'newOptimized') : insnNode.name.replace('new': 'newNamed')
    }

    /**
     * return 一个 Thread 前，修改名称
     */
    static def transformAReturn(cn, methodNode, insnNode) {
        //return时 操作数栈栈顶是 Thread 引用
        if (methodNode.desc == '()Ljava/lang/Thread;') {
            methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))
            methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, SHADOW_THREAD,
                    'setThreadName', '(Ljava/lang/Thread;Ljava/lang/String;)Ljava/lang/Thread;', false))
        }
    }


    static def transformInvokeVirtual(cn, methodNode, insnNode) {
//        Class<?> clazz = Class.forName(THREAD_CLASS)
//        if (clazz.isAssignableFrom(Class.forName(cn.name))) {
//            if (insnNode.name == 'setName' && insnNode.desc == '(Ljava/lang/String;)V') {
//                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))
//                methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
//                        SHADOW_THREAD, 'makeThreadName', '(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;'), false)
//            }
//        }
    }

    static def transformInvokeSpecial(cn, methodNode, insnNode) {
        if (!(insnNode instanceof MethodInsnNode)) {
            return
        }
        if (insnNode.name != '<init>') {
            return
        }
        switch (insnNode.owner) {
            case THREAD_CLASS:
                transformThreadInvokeSpecial(cn, methodNode, insnNode)
                recordPosition(cn, methodNode)
                break
            case THREAD_POOL_EXECUTOR:
                transformThreadPoolExecutorInvokeSpecial(cn, methodNode, (MethodInsnNode) insnNode)
                recordPosition(cn, methodNode)
                break
            default:
                break
        }
    }

    /**
     * 处理 threadPoolExecutor
     * 参数都已经压入操作数栈
     */
    static def transformThreadPoolExecutorInvokeSpecial(ClassNode cn, MethodNode methodNode, MethodInsnNode insnNode) {
        switch (insnNode.desc) {
        //--> int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue
            case '(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;)V':
                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))
                methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, NAMED_THREAD_FACTORY, 'newInstance',
                        '(Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;', false))
                insnNode.desc = '(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;Ljava/util/concurrent/ThreadFactory;)V'
                break
                //--> int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory
            case '(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;Ljava/util/concurrent/ThreadFactory;)V':
                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))
                methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, NAMED_THREAD_FACTORY, 'newInstance',
                        '(Ljava/util/concurrent/ThreadFactory;Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;', false))
                break
                //--> liveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler
            case '(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;Ljava/util/concurrent/RejectedExecutionHandler;)V':
                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))
                methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, NAMED_THREAD_FACTORY, 'newInstance',
                        '(Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;', false))
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.SWAP))
                break
                //--> int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler
            case '(IIJLjava/util/concurrent/TimeUnit;Ljava/util/concurrent/BlockingQueue;Ljava/util/concurrent/ThreadFactory;Ljava/util/concurrent/RejectedExecutionHandler;)V':
                // ..., threadFactory,handler -> ..., handler,threadFactory
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.SWAP))
                // ..., handler,threadFactory -> ...,handler,threadFactory,name
                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))
                // ...,handler,threadFactory,name -> ..., handler,threadFactory
                methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC, NAMED_THREAD_FACTORY, 'newInstance',
                        '(Ljava/util/concurrent/ThreadFactory;Ljava/lang/String;)Ljava/util/concurrent/ThreadFactory;', false))
                //交换回来，符合参数顺序
                // ..., handler,threadFactory -> ..., threadFactory,handler
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.SWAP))
                break
            default:
                break
        }
    }

    /**
     * 处理 Thread
     */
    static def transformThreadInvokeSpecial(ClassNode cn, MethodNode methodNode, MethodInsnNode insnNode) {
        switch (insnNode.desc) {
            case '()V':     // Thread()
            case '(Ljava/lang/Runnable;)V':     // Thread(Runnable)
            case '(Ljava/lang/ThreadGroup;Ljava/lang/Runnable;)V':  //Thread(Group,Runnable)
                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))
                def index = insnNode.desc.lastIndexOf(')')
                def desc = insnNode.desc.substring(0, index) + 'Ljava/lang/String;' + insnNode.desc.substring(index)
                insnNode.desc = desc
                break

            case '(Ljava/lang/String;)V':   //Thread(String)
            case '(Ljava/lang/ThreadGroup;Ljava/lang/String;)V':    //Thread(ThreadGroup,String)
            case '(Ljava/lang/Runnable;Ljava/lang/String;)V':       //Thread(Runnable,String)
            case '(Ljava/lang/ThreadGroup;Ljava/lang/Runnable;Ljava/lang/String;)V':    //Thread(ThreadGroup,Runnable,String)
                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))
                methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        SHADOW_THREAD, 'makeThreadName', '(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;', false))
                break
            case '(Ljava/lang/ThreadGroup;Ljava/lang/Runnable;Ljava/lang/String;J)V':   //Thread(ThreadGroup, Runnable, String, long)
                // in order to modify the thread name, the penultimate argument `name` have to be moved on the top
                // of operand stack, so that the `ShadowThread.makeThreadName(String, String)` could be invoked to
                // consume the `name` on the top of operand stack, and then a new name returned on the top of
                // operand stack.
                // due to JVM does not support swap long/double on the top of operand stack, so, we have to combine
                // DUP* and POP* to swap `name` and `stackSize`

                //  ..., name,stackSize => ...,stackSize, name,stackSize
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.DUP2_X1))

                //  ...,stackSize, name,stackSize => ...,stackSize, name
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.POP2))

                //  ...,stackSize, name => ...,stackSize, name,prefix
                methodNode.instructions.insertBefore(insnNode, new LdcInsnNode(makeThreadName(cn.name)))

                //  ...,stackSize, name,prefix => ...,stackSize, name
                methodNode.instructions.insertBefore(insnNode, new MethodInsnNode(Opcodes.INVOKESTATIC,
                        SHADOW_THREAD, 'makeThreadName', '(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;', false))

                //  ...,stackSize, name => ...,stackSize, name,name
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.DUP))

                //  ...,stackSize, name,name => ...,name,name,stackSize, name,name
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.DUP2_X2))

                //  ...,name,name,stackSize, name,name => ...,name,name,stackSize
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.POP2))

                //  ...,name,name,stackSize => ...,name,stackSize,name,stackSize
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.DUP2_X1))

                //  ...,name,stackSize,name,stackSize => ...,name,stackSize,name
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.POP2))

                //  ...,name,stackSize,name => ...,name,stackSize
                methodNode.instructions.insertBefore(insnNode, new InsnNode(Opcodes.POP))
                break
        }
    }

    /**
     * 处理 New 指令
     */
    static def transformNew(cn, methodNode, insnNode) {
        if (insnNode instanceof TypeInsnNode) {
            switch (insnNode.desc) {
                case THREAD_CLASS:
                    transformNewInner(cn, methodNode, insnNode, SHADOW_THREAD, false)
                    break
                case THREAD_POOL_EXECUTOR:
                    transformNewInner(cn, methodNode, insnNode, SHADOW_THREAD_POOL_EXECUTOR, Config.enableThreadPoolOptimized)
                    break
                default:
                    break
            }
        }
    }

    static def transformNewInner(cn, methodNode, insnNode, type, optimized) {
        def insnList = methodNode.instructions
        int index = insnList.indexOf(insnNode)
        def typeNodeDesc = insnNode.desc
        //向后遍历，寻找 <init> 方法
        for (int i = index + 1; i < insnList.size(); i++) {
            AbstractInsnNode node = insnList.get(i)
            if (node instanceof MethodInsnNode && node.opcode == Opcodes.INVOKESPECIAL && node.owner == typeNodeDesc && node.name == "<init>") {
                insnNode.desc = type
                node.owner = type
                //向 descriptor 中添加 String.class 入参
                node.desc = insertArgument(node.desc, String.class)
                //是否优化线程池
                if (optimized) {
                    //不要写成 Boolean.class
                    node.desc = insertArgument(node.desc, boolean.class)
                    Config.logger("optimized desc=" + node.desc)
                }
                def NewName = makeThreadName(cn.name)
                insnList.insertBefore(node, new LdcInsnNode(NewName))
                //是否优化线程池
                if (optimized) {
                    //true
                    insnList.insertBefore(node, new LdcInsnNode(Boolean.TRUE))
                }
                Config.logger("替换构造对象字节码，owner 改为：${node.owner}，desc 改为 ${node.desc}")
                recordPosition(cn, methodNode)
                //找到一个就 break
                break
            }
        }
    }

    static String makeThreadName(String className) {
        return MARK + CommonUtil.path2ClassName(className)
    }

    static def recordPosition(cn, methodNode) {
        RecordThreadPosition position = new RecordThreadPosition()
        def outerClass = cn.name
        position.outerClassName = outerClass
        position.sourceFile = cn.sourceFile
        position.invokeMethodName = methodNode.name
        RecordThreadPosition.positionList.add(position)
    }

    /**
     * 在描述符末尾添加文件描述符
     * @param descriptor
     * @param clazz
     * @return
     */
    static String insertArgument(descriptor, Class<?> clazz) {
        def type = Type.getMethodType(descriptor)
        //返回值类型
        def returnType = type.getReturnType()
        //参数数组
        def argumentTypes = type.getArgumentTypes()
        //构造新的参数数组
        def newArgumentTypes = new Type[argumentTypes.length + 1]
        System.arraycopy(argumentTypes, 0, newArgumentTypes, 0, argumentTypes.length)
        newArgumentTypes[newArgumentTypes.length - 1] = Type.getType(clazz)
        return Type.getMethodDescriptor(returnType, newArgumentTypes)
    }

    @Override
    protected void onTransformStart(TransformInvocation transformInvocation) {
    }

    @Override
    protected void onTransformEnd(TransformInvocation transformInvocation) {
        if (!RecordThreadPosition.positionList.isEmpty()) {
            StringBuilder builder = new StringBuilder()
            for (RecordThreadPosition position : RecordThreadPosition.positionList) {
                builder.append(position.toFileString())
                builder.append("\r\n")
                builder.append("\r\n")
            }
            try {
                byte[] bytes = builder.toString().getBytes("UTF-8")
                File file = new File(project.rootDir, "hookThread.txt")
                if (file.exists()) {
                    file.delete()
                }
                file.withOutputStream { it ->
                    it.write(bytes)
                }
                Config.logger("写 hookThread 文件结束！！！")
            } catch (Exception e) {
                println "写 hookThread 文件异常,${e.getMessage()}"
            }
        }
    }
}
