package com.zhangyue.ireader.asm_hook.handleThread;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;

public class TestScheduleThreadPoolExecutor extends ScheduledThreadPoolExecutor {
    public TestScheduleThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize);
    }

    public TestScheduleThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory) {
        super(corePoolSize, threadFactory);
    }

    public TestScheduleThreadPoolExecutor(int corePoolSize, RejectedExecutionHandler handler) {
        super(corePoolSize, handler);
    }

    public TestScheduleThreadPoolExecutor(int corePoolSize, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, threadFactory, handler);
    }
}
