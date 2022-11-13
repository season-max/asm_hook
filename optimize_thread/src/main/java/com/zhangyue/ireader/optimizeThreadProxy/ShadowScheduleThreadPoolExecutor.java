package com.zhangyue.ireader.optimizeThreadProxy;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class ShadowScheduleThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    //----------

    public ShadowScheduleThreadPoolExecutor(int corePoolSize, String name) {
        this(corePoolSize, name, false);
    }

    public ShadowScheduleThreadPoolExecutor(int corePoolSize, String name, boolean enableOptimized) {
        super(corePoolSize, new NamedThreadFactory(name));
        if (enableOptimized && getKeepAliveTime(TimeUnit.NANOSECONDS) > 0) {
            this.allowCoreThreadTimeOut(true);
        }
    }

    //----------

    public ShadowScheduleThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, String name) {
        this(corePoolSize, threadFactory, name, false);
    }


    public ShadowScheduleThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, String name, boolean optimized) {
        super(corePoolSize, new NamedThreadFactory(threadFactory, name));
        if (optimized && getKeepAliveTime(TimeUnit.NANOSECONDS) > 0) {
            allowCoreThreadTimeOut(true);
        }
    }

    //---------
    public ShadowScheduleThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler, String name) {
        this(corePoolSize, handler, name, false);
    }

    public ShadowScheduleThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler, String name, boolean optimized) {
        super(corePoolSize, new NamedThreadFactory(name), handler);
        if (optimized && getKeepAliveTime(TimeUnit.NANOSECONDS) > 0) {
            allowCoreThreadTimeOut(true);
        }
    }

    //
    public ShadowScheduleThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler, String name) {
        this(corePoolSize, threadFactory, handler, name, false);
    }

    public ShadowScheduleThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler, String name, boolean optimized) {
        super(corePoolSize, new NamedThreadFactory(threadFactory, name), handler);
        if (optimized && getKeepAliveTime(TimeUnit.NANOSECONDS) > 0) {
            allowCoreThreadTimeOut(true);
        }
    }

}
