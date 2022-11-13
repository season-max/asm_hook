package com.zhangyue.ireader.optimizeThreadProxy;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 线程池优化类
 * <p>
 * 对于执行定时任务的线程出，不做优化处理
 * 对于{@link Executors#newWorkStealingPool},使用的很少，不做处理
 */
public class ShadowExecutors {

    /**
     * 30 s
     */
    public static final int DEFAULT_KEEP_ALIVE_TIME = 30000;

    // <editor-fold desc="- named cache Thread pool ">
    public static ExecutorService newNamedCachedThreadPool(String name) {
        return Executors.newCachedThreadPool(new NamedThreadFactory(name));
    }

    public static ExecutorService newNamedCachedThreadPool(ThreadFactory threadFactory, String name) {
        return Executors.newCachedThreadPool(new NamedThreadFactory(threadFactory, name));
    }

    // </editor-fold>


    // <editor-fold desc="- named fixed Thread pool ">


    public static ExecutorService newNamedFixedThreadPool(int nThreads, String name) {
        return Executors.newFixedThreadPool(nThreads, new NamedThreadFactory(name));
    }

    public static ExecutorService newNamedFixedThreadPool(int nThreads, ThreadFactory threadFactory, String name) {
        return Executors.newFixedThreadPool(nThreads, new NamedThreadFactory(threadFactory, name));
    }
    // </editor-fold>

    // <editor-fold desc="- named Scheduled Thread pool ">
    public static ScheduledExecutorService newNamedScheduledThreadPool(int corePoolSize, String name) {
        return Executors.newScheduledThreadPool(corePoolSize, new NamedThreadFactory(name));
    }

    public static ScheduledExecutorService newNamedScheduledThreadPool(int corePoolSize, ThreadFactory threadFactory, String name) {
        return Executors.newScheduledThreadPool(corePoolSize, new NamedThreadFactory(threadFactory, name));
    }

    // </editor-fold>

    // <editor-fold desc="- named Single Thread pool ">
    public static ExecutorService newNamedSingleThreadExecutor(String name) {
        return Executors.newSingleThreadExecutor(new NamedThreadFactory(name));
    }

    public static ExecutorService newNamedSingleThreadExecutor(ThreadFactory threadFactory, String name) {
        return Executors.newSingleThreadExecutor(new NamedThreadFactory(threadFactory, name));
    }

    // </editor-fold>

    // <editor-fold desc="- named Single Thread schedule pool ">
    public static ScheduledExecutorService newNamedSingleThreadScheduledExecutor(String name) {
        return Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(name));
    }

    public static ScheduledExecutorService newNamedSingleThreadScheduledExecutor(ThreadFactory threadFactory, String name) {
        return Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory(threadFactory, name));
    }

    // </editor-fold>


    // <editor-fold desc="-optimized cache thread pool ">

    public static ExecutorService newOptimizedCachedThreadPool(String name) {
        ThreadPoolExecutor t = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new NamedThreadFactory(name));
        t.allowCoreThreadTimeOut(true);
        return t;
    }

    public static ExecutorService newOptimizedCachedThreadPool(ThreadFactory threadFactory, String name) {
        ThreadPoolExecutor t = new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                60L, TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>(),
                new NamedThreadFactory(threadFactory, name));
        t.allowCoreThreadTimeOut(true);
        return t;
    }

    // </editor-fold>


    // <editor-fold desc="-optimized fixed thread pool ">
    public static ExecutorService newOptimizedFixedThreadPool(int nThreads, String name) {
        ThreadPoolExecutor t = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(name));
        t.setKeepAliveTime(DEFAULT_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS);
        t.allowCoreThreadTimeOut(true);
        return t;
    }

    public static ExecutorService newOptimizedFixedThreadPool(int nThreads, ThreadFactory threadFactory, String name) {
        ThreadPoolExecutor t = new ThreadPoolExecutor(nThreads, nThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new NamedThreadFactory(threadFactory, name));
        t.setKeepAliveTime(DEFAULT_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS);
        t.allowCoreThreadTimeOut(true);
        return t;
    }
    // </editor-fold>

    // <editor-fold desc="-optimized schedule thread pool ">
    public static ScheduledExecutorService newOptimizedScheduledThreadPool(int corePoolSize, String name) {
        ScheduledThreadPoolExecutor t = new ScheduledThreadPoolExecutor(Math.max(corePoolSize, 1), new NamedThreadFactory(name));
        t.setKeepAliveTime(DEFAULT_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS);
        t.allowCoreThreadTimeOut(true);
        return t;
    }

    public static ScheduledExecutorService newOptimizedScheduledThreadPool(
            int corePoolSize, ThreadFactory threadFactory, String name) {
        ScheduledThreadPoolExecutor t = new ScheduledThreadPoolExecutor(Math.max(corePoolSize, 1), new NamedThreadFactory(threadFactory, name));
        t.setKeepAliveTime(DEFAULT_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS);
        t.allowCoreThreadTimeOut(true);
        return t;
    }


    // </editor-fold>

    // <editor-fold desc="-optimized single schedule thread pool ">

    public static ScheduledExecutorService newOptimizedSingleThreadScheduledExecutor(String name) {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory(name));
        executor.setKeepAliveTime(DEFAULT_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    public static ScheduledExecutorService newOptimizedSingleThreadScheduledExecutor(ThreadFactory threadFactory, String name) {
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1, new NamedThreadFactory(threadFactory, name));
        executor.setKeepAliveTime(DEFAULT_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS);
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }
    // </editor-fold>

    // <editor-fold desc="-optimized single thread pool ">
    public static ExecutorService newOptimizedSingleThreadExecutor(String name) {
        ThreadPoolExecutor t = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(name));
        t.setKeepAliveTime(DEFAULT_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS);
        t.allowCoreThreadTimeOut(true);
        return new FinalizableDelegatedExecutorService(t);
    }


    public static ExecutorService newOptimizedSingleThreadExecutor(ThreadFactory threadFactory, String name) {
        ThreadPoolExecutor t = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory(threadFactory, name));
        t.setKeepAliveTime(DEFAULT_KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS);
        t.allowCoreThreadTimeOut(true);
        return new FinalizableDelegatedExecutorService(t);
    }


    // </editor-fold>

    public static ThreadFactory defaultThreadFactory(String name) {
        return new NamedThreadFactory(name);
    }

    private static class FinalizableDelegatedExecutorService
            extends DelegatedExecutorService {
        FinalizableDelegatedExecutorService(ExecutorService executor) {
            super(executor);
        }

        protected void finalize() {
            super.shutdown();
        }
    }


    private static class DelegatedExecutorService
            extends AbstractExecutorService {
        // Android-added: @ReachabilitySensitive
        // Needed for FinalizableDelegatedExecutorService below.
        private final ExecutorService e;

        DelegatedExecutorService(ExecutorService executor) {
            e = executor;
        }

        public void execute(Runnable command) {
            e.execute(command);
        }

        public void shutdown() {
            e.shutdown();
        }

        public List<Runnable> shutdownNow() {
            return e.shutdownNow();
        }

        public boolean isShutdown() {
            return e.isShutdown();
        }

        public boolean isTerminated() {
            return e.isTerminated();
        }

        public boolean awaitTermination(long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.awaitTermination(timeout, unit);
        }

        public Future<?> submit(Runnable task) {
            return e.submit(task);
        }

        public <T> Future<T> submit(Callable<T> task) {
            return e.submit(task);
        }

        public <T> Future<T> submit(Runnable task, T result) {
            return e.submit(task, result);
        }

        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
                throws InterruptedException {
            return e.invokeAll(tasks);
        }

        public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks,
                                             long timeout, TimeUnit unit)
                throws InterruptedException {
            return e.invokeAll(tasks, timeout, unit);
        }

        public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
                throws InterruptedException, ExecutionException {
            return e.invokeAny(tasks);
        }

        public <T> T invokeAny(Collection<? extends Callable<T>> tasks,
                               long timeout, TimeUnit unit)
                throws InterruptedException, ExecutionException, TimeoutException {
            return e.invokeAny(tasks, timeout, unit);
        }
    }

}
