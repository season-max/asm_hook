package com.zhangyue.ireader.toolslibrary.optimizeThread;

import static com.zhangyue.ireader.toolslibrary.optimizeThread.ShadowThread.MARK;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadFactory implements ThreadFactory {

    public static ThreadFactory newInstance(String name) {
        return new NamedThreadFactory(name);
    }

    public static ThreadFactory newInstance(ThreadFactory factory, String name) {
        return new NamedThreadFactory(factory, name);
    }

    private final String prefix;
    private final ThreadGroup group;
    private final ThreadFactory threadFactory;
    private static final AtomicInteger threadNumber = new AtomicInteger(1);

    public NamedThreadFactory(String name) {
        this(null, name);
    }

    public NamedThreadFactory(ThreadFactory threadFactory, String name) {
        this.prefix = name;
        this.threadFactory = threadFactory;
        this.group = Thread.currentThread().getThreadGroup();
    }

    @Override
    public Thread newThread(Runnable r) {
        if (this.threadFactory == null) {
            Thread t = new Thread(group, r, prefix + "#thread_" + threadNumber.getAndIncrement(), 0);
            if (t.isDaemon())
                t.setDaemon(false);
            if (t.getPriority() != Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }
        Thread t = this.threadFactory.newThread(r);
        String name = t.getName();
        name = name.startsWith(MARK) ? name : prefix + "#" + name;
        t.setName(name);
        return t;
    }
}
