package com.example.threadlearning.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ConcurrentHashMap vs HashMap 并发安全性对比演示
 * 演示线程安全和性能差异
 */
@Slf4j
@Component
public class ConcurrentMapDemo {

    private static final int THREAD_COUNT = 10;
    private static final int OPERATIONS_PER_THREAD = 1000;

    /**
     * 演示HashMap的线程不安全性
     */
    public void demonstrateHashMapUnsafety() {
        log.info("=== HashMap线程不安全性演示 ===");
        
        Map<String, Integer> hashMap = new HashMap<>();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        long startTime = System.currentTimeMillis();

        // 创建多个线程同时操作HashMap
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        String key = "thread-" + threadId + "-key-" + j;
                        hashMap.put(key, j);
                        
                        // 模拟一些读操作
                        if (j % 10 == 0) {
                            hashMap.get(key);
                        }
                    }
                    log.info("线程 {} 完成 HashMap 操作", threadId);
                } catch (Exception e) {
                    log.error("线程 {} 在操作 HashMap 时发生异常: {}", threadId, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
            long endTime = System.currentTimeMillis();
            
            log.info("HashMap 操作完成");
            log.info("预期元素数量: {}", THREAD_COUNT * OPERATIONS_PER_THREAD);
            log.info("实际元素数量: {}", hashMap.size());
            log.info("耗时: {} ms", endTime - startTime);
            
            if (hashMap.size() < THREAD_COUNT * OPERATIONS_PER_THREAD) {
                log.warn("⚠️ HashMap 出现数据丢失！这是由于线程不安全导致的");
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 演示ConcurrentHashMap的线程安全性
     */
    public void demonstrateConcurrentHashMapSafety() {
        log.info("\n=== ConcurrentHashMap线程安全性演示 ===");
        
        Map<String, Integer> concurrentMap = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        long startTime = System.currentTimeMillis();

        // 创建多个线程同时操作ConcurrentHashMap
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < OPERATIONS_PER_THREAD; j++) {
                        String key = "thread-" + threadId + "-key-" + j;
                        concurrentMap.put(key, j);
                        
                        // 模拟一些读操作
                        if (j % 10 == 0) {
                            concurrentMap.get(key);
                        }
                    }
                    log.info("线程 {} 完成 ConcurrentHashMap 操作", threadId);
                } catch (Exception e) {
                    log.error("线程 {} 在操作 ConcurrentHashMap 时发生异常: {}", threadId, e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
            long endTime = System.currentTimeMillis();
            
            log.info("ConcurrentHashMap 操作完成");
            log.info("预期元素数量: {}", THREAD_COUNT * OPERATIONS_PER_THREAD);
            log.info("实际元素数量: {}", concurrentMap.size());
            log.info("耗时: {} ms", endTime - startTime);
            
            if (concurrentMap.size() == THREAD_COUNT * OPERATIONS_PER_THREAD) {
                log.info("✅ ConcurrentHashMap 保证了数据完整性！");
            }
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }

    /**
     * 演示并发修改异常（ConcurrentModificationException）
     */
    public void demonstrateConcurrentModificationException() {
        log.info("\n=== ConcurrentModificationException演示 ===");
        
        Map<String, Integer> hashMap = new HashMap<>();
        
        // 预先填充一些数据
        for (int i = 0; i < 100; i++) {
            hashMap.put("key-" + i, i);
        }

        Thread readerThread = new Thread(() -> {
            try {
                int count = 0;
                while (count < 5) {
                    log.info("开始遍历HashMap (第{}次)", count + 1);
                    try {
                        for (Map.Entry<String, Integer> entry : hashMap.entrySet()) {
                            Thread.sleep(10); // 延长遍历时间，增加并发修改的概率
                        }
                        log.info("遍历完成 (第{}次)", count + 1);
                        count++;
                    } catch (Exception e) {
                        log.error("遍历时发生异常: {}", e.getClass().getSimpleName() + " - " + e.getMessage());
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("读线程异常: {}", e.getMessage());
            }
        }, "HashMap-Reader");

        Thread writerThread = new Thread(() -> {
            try {
                for (int i = 100; i < 200; i++) {
                    hashMap.put("new-key-" + i, i);
                    if (i % 10 == 0) {
                        log.info("写入了10个新元素，当前大小: {}", hashMap.size());
                    }
                    Thread.sleep(50);
                }
            } catch (Exception e) {
                log.error("写线程异常: {}", e.getMessage());
            }
        }, "HashMap-Writer");

        readerThread.start();
        writerThread.start();

        try {
            readerThread.join();
            writerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 性能对比测试
     */
    public void performanceComparison() {
        log.info("\n=== HashMap vs ConcurrentHashMap 性能对比 ===");
        
        // 测试参数
        final int warmupRounds = 3;
        final int testRounds = 5;
        final int elementsCount = 10000;

        // 预热
        for (int i = 0; i < warmupRounds; i++) {
            performSinglePerformanceTest(elementsCount, false);
            performSinglePerformanceTest(elementsCount, true);
        }

        // 正式测试
        long hashMapTotalTime = 0;
        long concurrentHashMapTotalTime = 0;

        for (int i = 0; i < testRounds; i++) {
            log.info("开始第 {} 轮性能测试", i + 1);
            
            hashMapTotalTime += performSinglePerformanceTest(elementsCount, false);
            concurrentHashMapTotalTime += performSinglePerformanceTest(elementsCount, true);
        }

        double hashMapAvgTime = hashMapTotalTime / (double) testRounds;
        double concurrentHashMapAvgTime = concurrentHashMapTotalTime / (double) testRounds;

        log.info("\n=== 性能测试结果 ===");
        log.info("HashMap 平均耗时: {:.2f} ms", hashMapAvgTime);
        log.info("ConcurrentHashMap 平均耗时: {:.2f} ms", concurrentHashMapAvgTime);
        log.info("性能差异: {:.2f}%", 
                ((concurrentHashMapAvgTime - hashMapAvgTime) / hashMapAvgTime) * 100);
    }

    /**
     * 执行单次性能测试
     */
    private long performSinglePerformanceTest(int elementsCount, boolean useConcurrentHashMap) {
        Map<String, Integer> map = useConcurrentHashMap ? 
                new ConcurrentHashMap<>() : new HashMap<>();
        
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        
        long startTime = System.nanoTime();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    int elementsPerThread = elementsCount / THREAD_COUNT;
                    int startIndex = threadId * elementsPerThread;
                    int endIndex = startIndex + elementsPerThread;

                    // 写操作
                    for (int j = startIndex; j < endIndex; j++) {
                        map.put("key-" + j, j);
                    }

                    // 读操作
                    for (int j = startIndex; j < endIndex; j++) {
                        map.get("key-" + j);
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
        long duration = (endTime - startTime) / 1_000_000; // 转换为毫秒

        executor.shutdown();
        
        String mapType = useConcurrentHashMap ? "ConcurrentHashMap" : "HashMap";
        log.info("{} 测试完成，耗时: {} ms，元素数量: {}", mapType, duration, map.size());
        
        return duration;
    }

    /**
     * 演示ConcurrentHashMap的原子操作
     */
    public void demonstrateAtomicOperations() {
        log.info("\n=== ConcurrentHashMap原子操作演示 ===");
        
        ConcurrentHashMap<String, AtomicInteger> concurrentMap = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

        // 初始化一些计数器
        for (int i = 0; i < 5; i++) {
            concurrentMap.put("counter-" + i, new AtomicInteger(0));
        }

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        String key = "counter-" + (j % 5);
                        
                        // 使用compute方法进行原子操作
                        concurrentMap.compute(key, (k, v) -> {
                            if (v == null) {
                                return new AtomicInteger(1);
                            } else {
                                v.incrementAndGet();
                                return v;
                            }
                        });

                        // 使用computeIfAbsent方法
                        String newKey = "new-counter-" + threadId + "-" + j;
                        concurrentMap.computeIfAbsent(newKey, k -> new AtomicInteger(0))
                                   .incrementAndGet();
                    }
                    log.info("线程 {} 完成原子操作", threadId);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
            
            log.info("原子操作完成，结果统计:");
            concurrentMap.entrySet().stream()
                    .filter(entry -> entry.getKey().startsWith("counter-"))
                    .forEach(entry -> 
                            log.info("{}: {}", entry.getKey(), entry.getValue().get()));
                            
            log.info("新创建的计数器数量: {}", 
                    concurrentMap.keySet().stream()
                            .mapToInt(key -> key.startsWith("new-counter-") ? 1 : 0)
                            .sum());
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
    }
}