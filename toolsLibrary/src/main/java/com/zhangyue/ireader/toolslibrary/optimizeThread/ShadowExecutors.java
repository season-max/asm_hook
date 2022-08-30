package com.zhangyue.ireader.toolslibrary.optimizeThread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ShadowExecutors {

    public static ExecutorService newFixedThreadPool(int nThreads, String prefix) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new ShadowThreadFactory(prefix));
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }


    static class ShadowThreadFactory implements ThreadFactory {

        public ShadowThreadFactory(String prefix) {

        }

        @Override
        public Thread newThread(Runnable r) {
            return null;
        }
    }
}
