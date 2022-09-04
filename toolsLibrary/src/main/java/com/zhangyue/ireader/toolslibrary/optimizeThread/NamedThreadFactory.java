package com.zhangyue.ireader.toolslibrary.optimizeThread;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    public static ThreadFactory newInstance(String name) {
        return new NamedThreadFactory(name);
    }

    public static ThreadFactory newInstance(ThreadFactory factory, String name) {
        return new NamedThreadFactory(factory, name);
    }

    private final String name;
    private final ThreadGroup group;
    private final ThreadFactory threadFactory;
    private final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory(String name) {
        this(null, name);
    }

    public NamedThreadFactory(ThreadFactory threadFactory, String name) {
        this.name = name;
        this.threadFactory = threadFactory;
        this.group = Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        if (this.threadFactory == null) {
            Thread t = new Thread(group, r, name + "_" + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
        Thread t = this.threadFactory.newThread(r);
        t.setName(ShadowThread.makeThreadName(t.getName(), name));
        return t;
    }
}
