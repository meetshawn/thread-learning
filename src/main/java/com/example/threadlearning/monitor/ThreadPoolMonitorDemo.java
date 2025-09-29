package com.example.threadlearning.monitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 自定义拒绝策略和线程池监控演示
 * 展示如何实现自定义拒绝策略和监控线程池运行状态
 */
@Slf4j
@Component
public class ThreadPoolMonitorDemo {

    /**
     * 自定义拒绝策略演示
     */
    public void demonstrateCustomRejectionPolicies() {
        log.info("=== 自定义拒绝策略演示 ===");

        // 1. 自定义日志记录拒绝策略
        demonstrateLoggingRejectionPolicy();

        // 2. 自定义重试拒绝策略
        demonstrateRetryRejectionPolicy();

        // 3. 自定义异步保存拒绝策略
        demonstrateAsyncSaveRejectionPolicy();
    }

    /**
     * 日志记录拒绝策略
     */
    private void demonstrateLoggingRejectionPolicy() {
        log.info("\n--- 日志记录拒绝策略演示 ---");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 2, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                new CustomThreadFactory("LoggingReject"),
                new LoggingRejectionHandler()
        );

        // 提交足够多的任务触发拒绝策略
        for (int i = 0; i < 8; i++) {
            final int taskId = i;
            try {
                executor.submit(() -> {
                    log.info("执行任务 {}", taskId);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                log.info("任务 {} 已提交", taskId);
            } catch (RejectedExecutionException e) {
                log.error("任务 {} 被拒绝: {}", taskId, e.getMessage());
            }
        }

        shutdownAndAwait(executor, "LoggingRejection");
    }

    /**
     * 重试拒绝策略
     */
    private void demonstrateRetryRejectionPolicy() {
        log.info("\n--- 重试拒绝策略演示 ---");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 2, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2),
                new CustomThreadFactory("RetryReject"),
                new RetryRejectionHandler(3, 1000)
        );

        // 提交任务
        for (int i = 0; i < 6; i++) {
            final int taskId = i;
            try {
                executor.submit(() -> {
                    log.info("执行任务 {}", taskId);
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                log.info("任务 {} 已提交", taskId);
            } catch (RejectedExecutionException e) {
                log.error("任务 {} 最终被拒绝: {}", taskId, e.getMessage());
            }
        }

        shutdownAndAwait(executor, "RetryRejection");
    }

    /**
     * 异步保存拒绝策略
     */
    private void demonstrateAsyncSaveRejectionPolicy() {
        log.info("\n--- 异步保存拒绝策略演示 ---");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                1, 1, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(1),
                new CustomThreadFactory("AsyncSaveReject"),
                new AsyncSaveRejectionHandler()
        );

        // 提交任务
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            try {
                executor.submit(() -> {
                    log.info("执行任务 {}", taskId);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                log.info("任务 {} 已提交", taskId);
            } catch (RejectedExecutionException e) {
                log.error("任务 {} 被拒绝: {}", taskId, e.getMessage());
            }
        }

        shutdownAndAwait(executor, "AsyncSaveRejection");
    }

    /**
     * 线程池监控演示
     */
    public void demonstrateThreadPoolMonitoring() {
        log.info("\n=== 线程池监控演示 ===");

        // 创建可监控的线程池
        MonitorableThreadPoolExecutor executor = new MonitorableThreadPoolExecutor(
                2, 4, 60L, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(3),
                new CustomThreadFactory("Monitor"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        // 启动监控线程
        Thread monitorThread = new Thread(new ThreadPoolMonitor(executor), "PoolMonitor");
        monitorThread.setDaemon(true);
        monitorThread.start();

        // 提交不同类型的任务
        submitVariousTasks(executor);

        // 等待一段时间观察监控效果
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        shutdownAndAwait(executor, "Monitored");
    }

    /**
     * 提交各种类型的任务
     */
    private void submitVariousTasks(ThreadPoolExecutor executor) {
        // 快速任务
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            executor.submit(() -> {
                log.info("快速任务 {} 执行", taskId);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 中等任务
        for (int i = 0; i < 3; i++) {
            final int taskId = i;
            executor.submit(() -> {
                log.info("中等任务 {} 执行", taskId);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        // 耗时任务
        for (int i = 0; i < 2; i++) {
            final int taskId = i;
            executor.submit(() -> {
                log.info("耗时任务 {} 执行", taskId);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    /**
     * 关闭线程池并等待
     */
    private void shutdownAndAwait(ThreadPoolExecutor executor, String name) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                log.warn("{} 线程池未能在10秒内正常关闭", name);
                executor.shutdownNow();
            } else {
                log.info("{} 线程池已正常关闭", name);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
        }
    }

    /**
     * 自定义线程工厂
     */
    private static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CustomThreadFactory(String namePrefix) {
            this.namePrefix = namePrefix + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    /**
     * 日志记录拒绝策略
     */
    private static class LoggingRejectionHandler implements RejectedExecutionHandler {
        private final AtomicInteger rejectedCount = new AtomicInteger(0);

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            int count = rejectedCount.incrementAndGet();
            log.warn("❌ 任务被拒绝 (第{}次) - 核心线程数: {}, 最大线程数: {}, 当前线程数: {}, 队列大小: {}/{}", 
                   count,
                   executor.getCorePoolSize(),
                   executor.getMaximumPoolSize(), 
                   executor.getPoolSize(),
                   executor.getQueue().size(),
                   executor.getQueue().size() + executor.getQueue().remainingCapacity());
            
            // 抛出异常，让调用者知道任务被拒绝
            throw new RejectedExecutionException("任务被拒绝，已记录日志");
        }
    }

    /**
     * 重试拒绝策略
     */
    private static class RetryRejectionHandler implements RejectedExecutionHandler {
        private final int maxRetries;
        private final long retryInterval;

        public RetryRejectionHandler(int maxRetries, long retryInterval) {
            this.maxRetries = maxRetries;
            this.retryInterval = retryInterval;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            for (int i = 0; i < maxRetries; i++) {
                try {
                    log.info("🔄 任务被拒绝，第{}次重试...", i + 1);
                    Thread.sleep(retryInterval);
                    executor.execute(r);
                    log.info("✅ 重试成功");
                    return;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (RejectedExecutionException e) {
                    log.warn("⚠️ 第{}次重试失败", i + 1);
                }
            }
            
            log.error("❌ 重试{}次后仍然失败，任务最终被拒绝", maxRetries);
            throw new RejectedExecutionException("重试" + maxRetries + "次后任务仍被拒绝");
        }
    }

    /**
     * 异步保存拒绝策略
     */
    private static class AsyncSaveRejectionHandler implements RejectedExecutionHandler {
        private final ExecutorService backupExecutor = Executors.newSingleThreadExecutor(
                r -> new Thread(r, "BackupExecutor"));

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            log.warn("💾 任务被拒绝，将异步保存到备用队列");
            
            backupExecutor.submit(() -> {
                try {
                    log.info("📝 异步保存被拒绝的任务到数据库/文件");
                    Thread.sleep(500); // 模拟保存操作
                    
                    // 可以选择稍后重新执行或记录到数据库
                    log.info("💽 任务已保存，稍后可重新处理");
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }
    }

    /**
     * 可监控的线程池
     */
    private static class MonitorableThreadPoolExecutor extends ThreadPoolExecutor {
        private final AtomicLong submittedTasks = new AtomicLong(0);
        private final AtomicLong completedTasks = new AtomicLong(0);
        private final AtomicLong totalExecutionTime = new AtomicLong(0);

        public MonitorableThreadPoolExecutor(int corePoolSize, int maximumPoolSize, 
                                           long keepAliveTime, TimeUnit unit,
                                           BlockingQueue<Runnable> workQueue,
                                           ThreadFactory threadFactory,
                                           RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        @Override
        public void execute(Runnable command) {
            submittedTasks.incrementAndGet();
            super.execute(new TimedRunnable(command, totalExecutionTime, completedTasks));
        }

        public long getSubmittedTaskCount() {
            return submittedTasks.get();
        }

        public long getCompletedTaskCount() {
            return completedTasks.get();
        }

        public long getAverageExecutionTime() {
            long completed = completedTasks.get();
            return completed > 0 ? totalExecutionTime.get() / completed : 0;
        }
    }

    /**
     * 计时任务包装器
     */
    private static class TimedRunnable implements Runnable {
        private final Runnable originalTask;
        private final AtomicLong totalExecutionTime;
        private final AtomicLong completedTasks;

        public TimedRunnable(Runnable originalTask, AtomicLong totalExecutionTime, AtomicLong completedTasks) {
            this.originalTask = originalTask;
            this.totalExecutionTime = totalExecutionTime;
            this.completedTasks = completedTasks;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();
            try {
                originalTask.run();
            } finally {
                long endTime = System.currentTimeMillis();
                totalExecutionTime.addAndGet(endTime - startTime);
                completedTasks.incrementAndGet();
            }
        }
    }

    /**
     * 线程池监控器
     */
    private static class ThreadPoolMonitor implements Runnable {
        private final MonitorableThreadPoolExecutor executor;

        public ThreadPoolMonitor(MonitorableThreadPoolExecutor executor) {
            this.executor = executor;
        }

        @Override
        public void run() {
            while (!executor.isShutdown()) {
                try {
                    logPoolStatistics();
                    Thread.sleep(2000); // 每2秒监控一次
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }

        private void logPoolStatistics() {
            int activeCount = executor.getActiveCount();
            int poolSize = executor.getPoolSize();
            long taskCount = executor.getTaskCount();
            long completedTaskCount = executor.getCompletedTaskCount();
            int queueSize = executor.getQueue().size();
            
            long submittedTasks = executor.getSubmittedTaskCount();
            long avgExecutionTime = executor.getAverageExecutionTime();

            log.info("📊 线程池监控 - 活跃线程: {}/{}, 总任务: {}, 已完成: {}, 队列: {}, 提交: {}, 平均执行时间: {}ms",
                   activeCount, poolSize, taskCount, completedTaskCount, queueSize, submittedTasks, avgExecutionTime);
        }
    }
}