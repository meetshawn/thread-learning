package com.example.threadlearning.threadlocal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * ThreadLocal使用示例和内存泄漏防范演示
 * 展示ThreadLocal的正确使用方式和常见陷阱
 */
@Slf4j
@Component
public class ThreadLocalDemo {

    private final Random random = new Random();

    /**
     * ThreadLocal基础使用演示
     */
    public void demonstrateBasicUsage() {
        log.info("=== ThreadLocal基础使用演示 ===");

        // 创建ThreadLocal变量
        ThreadLocal<String> userContext = new ThreadLocal<>();
        ThreadLocal<Integer> requestId = new ThreadLocal<>();

        // 创建多个线程，每个线程设置自己的ThreadLocal值
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CountDownLatch latch = new CountDownLatch(3);

        for (int i = 0; i < 3; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    // 设置当前线程的ThreadLocal值
                    userContext.set("User-" + threadNum);
                    requestId.set(1000 + threadNum);

                    log.info("线程 {} 设置 ThreadLocal: userContext={}, requestId={}", 
                           Thread.currentThread().getName(), userContext.get(), requestId.get());

                    // 模拟业务操作
                    doBusinessOperation(userContext, requestId);

                    // 模拟嵌套方法调用
                    nestedMethodCall(userContext, requestId);

                } finally {
                    // 清理ThreadLocal，防止内存泄漏
                    userContext.remove();
                    requestId.remove();
                    log.info("线程 {} ThreadLocal已清理", Thread.currentThread().getName());
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }
        
        log.info("ThreadLocal基础演示完成");
    }

    /**
     * 业务操作方法，演示ThreadLocal在方法间的传递
     */
    private void doBusinessOperation(ThreadLocal<String> userContext, ThreadLocal<Integer> requestId) {
        String user = userContext.get();
        Integer reqId = requestId.get();
        
        log.info("执行业务操作 - 用户: {}, 请求ID: {}, 线程: {}", 
               user, reqId, Thread.currentThread().getName());
        
        try {
            Thread.sleep(1000 + random.nextInt(1000));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 嵌套方法调用，演示ThreadLocal的线程绑定特性
     */
    private void nestedMethodCall(ThreadLocal<String> userContext, ThreadLocal<Integer> requestId) {
        log.info("嵌套方法调用 - 用户: {}, 请求ID: {}, 线程: {}", 
               userContext.get(), requestId.get(), Thread.currentThread().getName());
    }

    /**
     * 线程池中的ThreadLocal演示（易发生内存泄漏的场景）
     */
    public void demonstrateThreadPoolUsage() {
        log.info("\n=== 线程池中ThreadLocal使用演示 ===");

        ThreadLocal<String> sessionContext = new ThreadLocal<>();
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        
        // 提交多个任务到固定大小的线程池
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            fixedThreadPool.submit(() -> {
                try {
                    String sessionId = "SESSION-" + taskId + "-" + System.currentTimeMillis();
                    sessionContext.set(sessionId);
                    
                    log.info("任务 {} 开始执行，会话ID: {}, 线程: {}", 
                           taskId, sessionContext.get(), Thread.currentThread().getName());
                    
                    // 模拟任务执行
                    simulateTask(sessionContext, taskId);
                    
                    log.info("任务 {} 执行完成，会话ID: {}, 线程: {}", 
                           taskId, sessionContext.get(), Thread.currentThread().getName());
                    
                } finally {
                    // 重要：在线程池中必须手动清理ThreadLocal
                    sessionContext.remove();
                    log.info("任务 {} ThreadLocal已清理，线程: {}", taskId, Thread.currentThread().getName());
                }
            });
        }

        // 等待一段时间让任务执行完成
        try {
            Thread.sleep(8000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        fixedThreadPool.shutdown();
        log.info("线程池ThreadLocal演示完成");
    }

    /**
     * 模拟任务执行
     */
    private void simulateTask(ThreadLocal<String> sessionContext, int taskId) {
        try {
            Thread.sleep(1000 + random.nextInt(2000));
            
            // 在任务执行过程中可以随时获取ThreadLocal的值
            String sessionId = sessionContext.get();
            log.info("任务 {} 执行中，当前会话: {}", taskId, sessionId);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 演示ThreadLocal内存泄漏问题
     */
    public void demonstrateMemoryLeak() {
        log.info("\n=== ThreadLocal内存泄漏演示 ===");

        // 创建一个包含大量数据的ThreadLocal
        ThreadLocal<LargeObject> largeObjectThreadLocal = new ThreadLocal<>();
        ExecutorService executor = Executors.newFixedThreadPool(2);

        log.info("开始演示内存泄漏场景...");

        // 场景1：不清理ThreadLocal的情况
        for (int i = 0; i < 3; i++) {
            final int iteration = i;
            executor.submit(() -> {
                // 创建大对象并存储到ThreadLocal
                LargeObject largeObject = new LargeObject("LargeData-" + iteration);
                largeObjectThreadLocal.set(largeObject);
                
                log.info("线程 {} 创建了大对象: {}", Thread.currentThread().getName(), largeObject.getName());
                
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // 注意：这里故意不调用remove()，模拟内存泄漏
                if (iteration < 2) {
                    log.warn("⚠️ 线程 {} 未清理ThreadLocal，可能导致内存泄漏", Thread.currentThread().getName());
                } else {
                    // 最后一个任务演示正确的清理方式
                    largeObjectThreadLocal.remove();
                    log.info("✅ 线程 {} 正确清理了ThreadLocal", Thread.currentThread().getName());
                }
            });
        }

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();
        log.info("内存泄漏演示完成");
    }

    /**
     * InheritableThreadLocal演示
     */
    public void demonstrateInheritableThreadLocal() {
        log.info("\n=== InheritableThreadLocal演示 ===");

        // 普通ThreadLocal
        ThreadLocal<String> normalThreadLocal = new ThreadLocal<>();
        // 可继承的ThreadLocal
        InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();

        // 在主线程中设置值
        normalThreadLocal.set("主线程普通值");
        inheritableThreadLocal.set("主线程可继承值");

        log.info("主线程设置 - 普通ThreadLocal: {}, 可继承ThreadLocal: {}", 
               normalThreadLocal.get(), inheritableThreadLocal.get());

        // 创建子线程
        Thread childThread = new Thread(() -> {
            log.info("子线程获取 - 普通ThreadLocal: {}, 可继承ThreadLocal: {}", 
                   normalThreadLocal.get(), inheritableThreadLocal.get());

            // 在子线程中修改值
            inheritableThreadLocal.set("子线程修改的值");
            log.info("子线程修改后 - 可继承ThreadLocal: {}", inheritableThreadLocal.get());
        });

        childThread.start();
        
        try {
            childThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // 主线程的值不受子线程影响
        log.info("主线程最终值 - 可继承ThreadLocal: {}", inheritableThreadLocal.get());

        // 清理
        normalThreadLocal.remove();
        inheritableThreadLocal.remove();
    }

    /**
     * 使用ThreadLocal实现DateFormat线程安全
     */
    public void demonstrateDateFormatSafety() {
        log.info("\n=== ThreadLocal实现DateFormat线程安全演示 ===");

        // SimpleDateFormat不是线程安全的，使用ThreadLocal保证线程安全
        ThreadLocal<SimpleDateFormat> dateFormatThreadLocal = ThreadLocal.withInitial(
                () -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
        );

        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(10);

        // 多个线程同时格式化日期
        for (int i = 0; i < 10; i++) {
            final int taskId = i;
            executor.submit(() -> {
                try {
                    SimpleDateFormat dateFormat = dateFormatThreadLocal.get();
                    Date now = new Date();
                    
                    // 模拟一些处理时间
                    Thread.sleep(100 + random.nextInt(200));
                    
                    String formatted = dateFormat.format(now);
                    log.info("任务 {} 格式化日期: {}, 线程: {}", 
                           taskId, formatted, Thread.currentThread().getName());
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    // 清理ThreadLocal
                    dateFormatThreadLocal.remove();
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executor.shutdown();
        }

        log.info("DateFormat线程安全演示完成");
    }

    /**
     * ThreadLocal性能对比演示
     */
    public void demonstratePerformanceComparison() {
        log.info("\n=== ThreadLocal性能对比演示 ===");

        int threadCount = 4;
        int operationsPerThread = 100000;
        
        // 测试1：使用ThreadLocal
        ThreadLocal<AtomicInteger> threadLocalCounter = ThreadLocal.withInitial(() -> new AtomicInteger(0));
        long threadLocalTime = performanceTest("ThreadLocal", threadCount, operationsPerThread, () -> {
            threadLocalCounter.get().incrementAndGet();
        }, () -> {
            threadLocalCounter.remove();
        });

        // 测试2：使用synchronized
        AtomicInteger sharedCounter = new AtomicInteger(0);
        long synchronizedTime = performanceTest("Synchronized", threadCount, operationsPerThread, () -> {
            synchronized (sharedCounter) {
                sharedCounter.incrementAndGet();
            }
        }, () -> {
            // 无需清理
        });

        // 测试3：使用AtomicInteger
        AtomicInteger atomicCounter = new AtomicInteger(0);
        long atomicTime = performanceTest("AtomicInteger", threadCount, operationsPerThread, () -> {
            atomicCounter.incrementAndGet();
        }, () -> {
            // 无需清理
        });

        log.info("性能对比结果:");
        log.info("ThreadLocal: {} ms", threadLocalTime);
        log.info("Synchronized: {} ms", synchronizedTime);
        log.info("AtomicInteger: {} ms", atomicTime);
    }

    /**
     * 性能测试辅助方法
     */
    private long performanceTest(String testName, int threadCount, int operationsPerThread, 
                                Runnable operation, Runnable cleanup) {
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        operation.run();
                    }
                } finally {
                    cleanup.run();
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
        executor.shutdown();
        
        return endTime - startTime;
    }

    /**
     * 大对象类，用于演示内存泄漏
     */
    private static class LargeObject {
        private final String name;
        private final byte[] data;

        public LargeObject(String name) {
            this.name = name;
            this.data = new byte[1024 * 1024]; // 1MB数据
        }

        public String getName() {
            return name;
        }
    }
}