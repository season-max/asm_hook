package com.zhangyue.ireader.toolslibrary.optimizeThread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ShadowThreadPoolExecutor extends ThreadPoolExecutor {

    //----------

    public ShadowThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, String name) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, name, false);
    }

    public ShadowThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, String name, boolean enableOptimized) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(name));
        if (enableOptimized && getKeepAliveTime(unit) > 0) {
            this.allowCoreThreadTimeOut(true);
        }
    }

    //----------

    public ShadowThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, String name) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, name, false);
    }

    public ShadowThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, String name, boolean enableOptimized) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(threadFactory, name));
        if (enableOptimized && getKeepAliveTime(unit) > 0) {
            this.allowCoreThreadTimeOut(true);
        }
    }


    //----------

    public ShadowThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler, String name) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, handler, name, false);
    }


    public ShadowThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, RejectedExecutionHandler handler, String name, boolean enableOptimized) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(name), handler);
        if (enableOptimized && getKeepAliveTime(unit) > 0) {
            this.allowCoreThreadTimeOut(true);
        }
    }


    //----------

    public ShadowThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, String name) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler, name, false);
    }

    public ShadowThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler, String name, boolean enableOptimized) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, new NamedThreadFactory(threadFactory, name), handler);
        if (enableOptimized && getKeepAliveTime(unit) > 0) {
            this.allowCoreThreadTimeOut(true);
        }
    }


}
