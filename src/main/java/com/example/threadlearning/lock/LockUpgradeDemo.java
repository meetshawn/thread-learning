package com.example.threadlearning.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 锁升级过程演示
 * 演示偏向锁 -> 轻量级锁 -> 重量级锁的升级过程
 * 以及不同锁机制的性能对比
 */
@Slf4j
@Component
public class LockUpgradeDemo {

    private volatile int sharedCounter = 0;
    private final Object lockObject = new Object();
    private final ReentrantLock reentrantLock = new ReentrantLock();
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * 演示锁升级过程
     * 注意：实际的锁升级过程在JVM内部，这里主要演示不同竞争情况下的行为
     */
    public void demonstrateLockUpgrade() {
        log.info("=== 锁升级过程演示 ===");
        
        // 场景1：无竞争情况（理论上使用偏向锁）
        demonstrateBiasedLock();
        
        // 场景2：轻度竞争情况（轻量级锁）
        demonstrateLightweightLock();
        
        // 场景3：激烈竞争情况（重量级锁）
        demonstrateHeavyweightLock();
    }

    /**
     * 演示偏向锁场景（单线程反复获取同一个锁）
     */
    private void demonstrateBiasedLock() {
        log.info("\n--- 偏向锁演示（单线程反复获取锁）---");
        
        Object biasedLockObject = new Object();
        long startTime = System.nanoTime();
        
        // 单线程反复获取锁，模拟偏向锁场景
        for (int i = 0; i < 1000000; i++) {
            synchronized (biasedLockObject) {
                sharedCounter++;
            }
        }
        
        long endTime = System.nanoTime();
        log.info("偏向锁场景 - 执行时间: {} ms, 最终计数: {}", 
               (endTime - startTime) / 1_000_000, sharedCounter);
        
        sharedCounter = 0; // 重置计数器
    }

    /**
     * 演示轻量级锁场景（少量线程轻度竞争）
     */
    private void demonstrateLightweightLock() {
        log.info("\n--- 轻量级锁演示（少量线程轻度竞争）---");
        
        int threadCount = 2;
        int operationsPerThread = 500000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        synchronized (lockObject) {
                            sharedCounter++;
                        }
                        
                        // 添加一些间隔，减少锁竞争
                        if (j % 100000 == 0) {
                            Thread.yield();
                        }
                    }
                    log.info("轻量级锁 - 线程 {} 完成操作", threadId);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.nanoTime();
        log.info("轻量级锁场景 - 执行时间: {} ms, 最终计数: {}", 
               (endTime - startTime) / 1_000_000, sharedCounter);
        
        executor.shutdown();
        sharedCounter = 0; // 重置计数器
    }

    /**
     * 演示重量级锁场景（多线程激烈竞争）
     */
    private void demonstrateHeavyweightLock() {
        log.info("\n--- 重量级锁演示（多线程激烈竞争）---");
        
        int threadCount = 8;
        int operationsPerThread = 125000;
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        long startTime = System.nanoTime();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        synchronized (lockObject) {
                            sharedCounter++;
                            
                            // 增加锁持有时间，加剧竞争
                            try {
                                Thread.sleep(0, 1000); // 1微秒
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    }
                    log.info("重量级锁 - 线程 {} 完成操作", threadId);
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.nanoTime();
        log.info("重量级锁场景 - 执行时间: {} ms, 最终计数: {}", 
               (endTime - startTime) / 1_000_000, sharedCounter);
        
        executor.shutdown();
        sharedCounter = 0; // 重置计数器
    }

    /**
     * 不同锁机制性能对比
     */
    public void demonstrateLockPerformanceComparison() {
        log.info("\n=== 不同锁机制性能对比 ===");
        
        int threadCount = 4;
        int operationsPerThread = 250000;
        
        // 测试synchronized
        long syncTime = testLockPerformance("synchronized", threadCount, operationsPerThread, () -> {
            synchronized (lockObject) {
                sharedCounter++;
            }
        });
        
        sharedCounter = 0;
        
        // 测试ReentrantLock
        long reentrantTime = testLockPerformance("ReentrantLock", threadCount, operationsPerThread, () -> {
            reentrantLock.lock();
            try {
                sharedCounter++;
            } finally {
                reentrantLock.unlock();
            }
        });
        
        sharedCounter = 0;
        
        // 测试ReadWriteLock（写锁）
        long readWriteTime = testLockPerformance("ReadWriteLock(写锁)", threadCount, operationsPerThread, () -> {
            readWriteLock.writeLock().lock();
            try {
                sharedCounter++;
            } finally {
                readWriteLock.writeLock().unlock();
            }
        });
        
        log.info("\n性能对比结果:");
        log.info("synchronized: {} ms", syncTime);
        log.info("ReentrantLock: {} ms", reentrantTime);
        log.info("ReadWriteLock(写锁): {} ms", readWriteTime);
        
        double syncBaseline = syncTime;
        log.info("相对性能 (以synchronized为基准):");
        log.info("ReentrantLock: {:.2f}x", syncBaseline / reentrantTime);
        log.info("ReadWriteLock: {:.2f}x", syncBaseline / readWriteTime);
    }

    /**
     * 锁性能测试辅助方法
     */
    private long testLockPerformance(String lockType, int threadCount, int operationsPerThread, Runnable operation) {
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        log.info("开始测试 {} 性能...", lockType);
        long startTime = System.nanoTime();
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        operation.run();
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000;
        
        executor.shutdown();
        log.info("{} 测试完成 - 耗时: {} ms, 最终计数: {}", lockType, duration, sharedCounter);
        
        return duration;
    }

    /**
     * 演示读写锁的优势
     */
    public void demonstrateReadWriteLockAdvantage() {
        log.info("\n=== 读写锁优势演示 ===");
        
        SharedResource resource = new SharedResource();
        int readerCount = 6;
        int writerCount = 2;
        CountDownLatch latch = new CountDownLatch(readerCount + writerCount);
        ExecutorService executor = Executors.newFixedThreadPool(readerCount + writerCount);
        
        long startTime = System.currentTimeMillis();
        
        // 创建多个读线程
        for (int i = 0; i < readerCount; i++) {
            final int readerId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 5; j++) {
                        String value = resource.read();
                        log.info("读线程 {} 读取到值: {}", readerId, value);
                        Thread.sleep(200);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 创建少量写线程
        for (int i = 0; i < writerCount; i++) {
            final int writerId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 3; j++) {
                        String newValue = "Writer-" + writerId + "-Value-" + j;
                        resource.write(newValue);
                        log.info("写线程 {} 写入值: {}", writerId, newValue);
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long endTime = System.currentTimeMillis();
        log.info("读写锁演示完成 - 总耗时: {} ms", endTime - startTime);
        
        executor.shutdown();
    }

    /**
     * 共享资源类，演示读写锁的使用
     */
    private static class SharedResource {
        private String value = "初始值";
        private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
        private final ReentrantReadWriteLock.ReadLock readLock = rwLock.readLock();
        private final ReentrantReadWriteLock.WriteLock writeLock = rwLock.writeLock();

        public String read() {
            readLock.lock();
            try {
                // 模拟读操作耗时
                Thread.sleep(100);
                return value;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            } finally {
                readLock.unlock();
            }
        }

        public void write(String newValue) {
            writeLock.lock();
            try {
                // 模拟写操作耗时
                Thread.sleep(200);
                this.value = newValue;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                writeLock.unlock();
            }
        }
    }

    /**
     * 演示锁的可重入性
     */
    public void demonstrateReentrantLock() {
        log.info("\n=== 锁的可重入性演示 ===");
        
        ReentrantObject reentrantObj = new ReentrantObject();
        
        Thread thread = new Thread(() -> {
            reentrantObj.outerMethod();
        }, "ReentrantDemo");
        
        thread.start();
        
        try {
            thread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("可重入锁演示完成");
    }

    /**
     * 可重入对象，演示锁的可重入特性
     */
    private static class ReentrantObject {
        private final ReentrantLock lock = new ReentrantLock();
        private int count = 0;

        public void outerMethod() {
            lock.lock();
            try {
                log.info("外层方法开始 - 持有锁数: {}", lock.getHoldCount());
                count++;
                innerMethod();
                log.info("外层方法结束 - 持有锁数: {}", lock.getHoldCount());
            } finally {
                lock.unlock();
            }
        }

        public void innerMethod() {
            lock.lock();
            try {
                log.info("内层方法开始 - 持有锁数: {}", lock.getHoldCount());
                count++;
                log.info("内层方法执行 - 当前计数: {}", count);
                log.info("内层方法结束 - 持有锁数: {}", lock.getHoldCount());
            } finally {
                lock.unlock();
            }
        }
    }
}