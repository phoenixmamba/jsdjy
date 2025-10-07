package com.centit.admin.threadPool;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @version : 1.0
 * @Author : cui_jian
 * @Description :
 * @Date : 2024/11/27 21:43
 **/
public class ThreadPoolExecutorFactory {
    private ThreadPoolExecutorFactory() {}

    /**
     * 核心线程数
     */
    private static final int CORE_POOL_SIZE = 5;
    /**
     * 最大线程数
     */
    private static final int MAXIMUM_POOL_SIZE = 10;
    /**
     * 线程保持活动时间（秒）
     */
    private static final long KEEP_ALIVE_TIME = 60L;
    /**
     * 任务队列容量
     */
    private static final int QUEUE_CAPACITY = 100;
    /**
     * 线程池名称前缀
     */
    private static final String THREAD_NAME_PREFIX = "JobThreadPool-";

    public static ExecutorService createThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r, THREAD_NAME_PREFIX + threadNumber.getAndIncrement());
                        // 设置线程为守护线程（可选）
                        // thread.setDaemon(true);
                        return thread;
                    }
                },
                // 拒绝策略
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
