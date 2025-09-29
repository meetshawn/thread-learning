package com.example.threadlearning.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 同步工具类演示
 * CountDownLatch、CyclicBarrier、Semaphore的实际应用场景
 */
@Slf4j
@Component
public class SynchronizationToolsDemo {

    private final Random random = new Random();

    /**
     * CountDownLatch演示 - 应用启动场景
     * 模拟应用启动时等待所有服务初始化完成
     */
    public void demonstrateCountDownLatch() {
        log.info("=== CountDownLatch演示：应用启动场景 ===");
        
        int serviceCount = 5;
        CountDownLatch startupLatch = new CountDownLatch(serviceCount);
        
        String[] services = {"数据库连接", "缓存服务", "消息队列", "配置中心", "监控服务"};
        
        // 启动各个服务的初始化线程
        for (int i = 0; i < serviceCount; i++) {
            final String serviceName = services[i];
            final int initTime = 2000 + random.nextInt(3000); // 2-5秒初始化时间
            
            new Thread(() -> {
                try {
                    log.info("正在初始化 {}...", serviceName);
                    Thread.sleep(initTime); // 模拟初始化时间
                    log.info("✅ {} 初始化完成", serviceName);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("❌ {} 初始化失败", serviceName);
                } finally {
                    startupLatch.countDown(); // 计数器减1
                }
            }, serviceName + "-InitThread").start();
        }
        
        // 主线程等待所有服务初始化完成
        new Thread(() -> {
            try {
                log.info("应用启动中，等待所有服务初始化完成...");
                startupLatch.await(); // 等待计数器归零
                log.info("🎉 所有服务初始化完成，应用启动成功！");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("应用启动被中断");
            }
        }, "MainApplication").start();
    }

    /**
     * CyclicBarrier演示 - 分阶段任务场景
     * 模拟多个线程分阶段执行任务，每个阶段都要等待所有线程完成
     */
    public void demonstrateCyclicBarrier() {
        log.info("\n=== CyclicBarrier演示：分阶段任务场景 ===");
        
        int workerCount = 4;
        int phaseCount = 3;
        
        // 创建屏障，当4个线程都到达时执行屏障动作
        CyclicBarrier barrier = new CyclicBarrier(workerCount, () -> {
            log.info("🚧 所有工作线程都到达屏障，准备进入下一阶段...\n");
        });
        
        String[] phases = {"数据收集阶段", "数据处理阶段", "结果汇总阶段"};
        
        for (int i = 0; i < workerCount; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    for (int phase = 0; phase < phaseCount; phase++) {
                        // 模拟当前阶段的工作
                        int workTime = 1000 + random.nextInt(2000);
                        log.info("工作线程{} 开始 {}", workerId, phases[phase]);
                        Thread.sleep(workTime);
                        log.info("工作线程{} 完成 {}", workerId, phases[phase]);
                        
                        // 等待其他线程完成当前阶段
                        log.info("工作线程{} 等待其他线程完成 {}", workerId, phases[phase]);
                        barrier.await(); // 等待所有线程到达屏障
                    }
                    log.info("✅ 工作线程{} 完成所有阶段任务", workerId);
                } catch (Exception e) {
                    log.error("工作线程{} 执行异常: {}", workerId, e.getMessage());
                }
            }, "Worker-" + i).start();
        }
    }

    /**
     * Semaphore演示 - 资源池管理场景
     * 模拟数据库连接池，限制同时访问数据库的连接数
     */
    public void demonstrateSemaphore() {
        log.info("\n=== Semaphore演示：数据库连接池场景 ===");
        
        int maxConnections = 3; // 最大连接数
        int clientCount = 8;    // 客户端数量
        
        Semaphore connectionPool = new Semaphore(maxConnections);
        
        for (int i = 0; i < clientCount; i++) {
            final int clientId = i;
            new Thread(() -> {
                try {
                    log.info("客户端{} 请求数据库连接...", clientId);
                    
                    // 尝试获取连接（许可证）
                    connectionPool.acquire();
                    log.info("✅ 客户端{} 获得数据库连接，开始执行查询", clientId);
                    log.info("📊 当前可用连接数: {}", connectionPool.availablePermits());
                    
                    // 模拟数据库操作
                    int queryTime = 2000 + random.nextInt(3000);
                    Thread.sleep(queryTime);
                    
                    log.info("客户端{} 查询完成，释放连接", clientId);
                    
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("客户端{} 被中断", clientId);
                } finally {
                    // 释放连接（许可证）
                    connectionPool.release();
                    log.info("🔄 客户端{} 连接已释放，当前可用连接数: {}", 
                           clientId, connectionPool.availablePermits());
                }
            }, "Client-" + i).start();
        }
    }

    /**
     * Semaphore演示 - 限流场景
     * 模拟API限流，控制同时处理的请求数量
     */
    public void demonstrateRateLimiting() {
        log.info("\n=== Semaphore演示：API限流场景 ===");
        
        int maxConcurrentRequests = 2; // 最大并发请求数
        int totalRequests = 6;         // 总请求数
        
        Semaphore rateLimiter = new Semaphore(maxConcurrentRequests);
        
        for (int i = 0; i < totalRequests; i++) {
            final int requestId = i;
            new Thread(() -> {
                try {
                    log.info("📨 请求{} 到达API网关", requestId);
                    
                    // 尝试获取处理许可
                    if (rateLimiter.tryAcquire(1000, TimeUnit.MILLISECONDS)) {
                        try {
                            log.info("✅ 请求{} 获得处理许可，开始处理", requestId);
                            log.info("🔧 当前处理中的请求数: {}", 
                                   maxConcurrentRequests - rateLimiter.availablePermits());
                            
                            // 模拟API处理时间
                            int processTime = 3000 + random.nextInt(2000);
                            Thread.sleep(processTime);
                            
                            log.info("✅ 请求{} 处理完成", requestId);
                        } finally {
                            rateLimiter.release();
                        }
                    } else {
                        log.warn("⚠️ 请求{} 被限流，获取许可超时", requestId);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.error("请求{} 被中断", requestId);
                }
            }, "Request-" + i).start();
        }
    }

    /**
     * 组合使用演示 - 复杂并发场景
     * 模拟并行数据处理管道：多阶段处理 + 资源限制 + 最终汇总
     */
    public void demonstrateCombinedUsage() {
        log.info("\n=== 组合使用演示：并行数据处理管道 ===");
        
        int workerCount = 4;
        int dataSize = 12;
        
        // CountDownLatch: 等待所有数据处理完成
        CountDownLatch completionLatch = new CountDownLatch(dataSize);
        
        // CyclicBarrier: 分阶段处理屏障
        CyclicBarrier processBarrier = new CyclicBarrier(workerCount, () -> {
            log.info("🚧 一批数据处理完成，准备处理下一批...");
        });
        
        // Semaphore: 限制同时处理的数据量
        Semaphore processingSlots = new Semaphore(2);
        
        // 启动工作线程
        for (int i = 0; i < workerCount; i++) {
            final int workerId = i;
            new Thread(() -> {
                try {
                    int dataPerWorker = dataSize / workerCount;
                    
                    for (int j = 0; j < dataPerWorker; j++) {
                        int dataId = workerId * dataPerWorker + j;
                        
                        // 获取处理槽位
                        processingSlots.acquire();
                        try {
                            log.info("工作线程{} 开始处理数据{}", workerId, dataId);
                            Thread.sleep(1000 + random.nextInt(1000));
                            log.info("工作线程{} 完成数据{}", workerId, dataId);
                        } finally {
                            processingSlots.release();
                            completionLatch.countDown();
                        }
                    }
                    
                    // 等待同批次其他工作线程
                    log.info("工作线程{} 等待同批次完成", workerId);
                    processBarrier.await();
                    log.info("工作线程{} 进入下一阶段", workerId);
                    
                } catch (Exception e) {
                    log.error("工作线程{} 执行异常: {}", workerId, e.getMessage());
                }
            }, "DataWorker-" + i).start();
        }
        
        // 监控线程
        new Thread(() -> {
            try {
                log.info("📊 监控线程：等待所有数据处理完成...");
                completionLatch.await();
                log.info("🎉 所有数据处理完成！总共处理了{}条数据", dataSize);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Monitor").start();
    }
}