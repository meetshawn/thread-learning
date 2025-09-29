package com.example.threadlearning.controller;

import com.example.threadlearning.sync.SynchronizationToolsDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 同步工具类演示控制器
 * 提供API接口来测试CountDownLatch、CyclicBarrier、Semaphore功能
 */
@RestController
@RequestMapping("/sync-tools")
public class SyncToolsController {

    @Autowired
    private SynchronizationToolsDemo syncToolsDemo;

    /**
     * 演示CountDownLatch - 应用启动场景
     * 访问地址: GET /sync-tools/countdown-latch
     */
    @GetMapping("/countdown-latch")
    public String testCountDownLatch() {
        syncToolsDemo.demonstrateCountDownLatch();
        return "CountDownLatch应用启动场景演示已开始，请查看日志了解详情";
    }

    /**
     * 演示CyclicBarrier - 分阶段任务场景
     * 访问地址: GET /sync-tools/cyclic-barrier
     */
    @GetMapping("/cyclic-barrier")
    public String testCyclicBarrier() {
        syncToolsDemo.demonstrateCyclicBarrier();
        return "CyclicBarrier分阶段任务场景演示已开始，请查看日志了解详情";
    }

    /**
     * 演示Semaphore - 数据库连接池场景
     * 访问地址: GET /sync-tools/semaphore-pool
     */
    @GetMapping("/semaphore-pool")
    public String testSemaphorePool() {
        syncToolsDemo.demonstrateSemaphore();
        return "Semaphore数据库连接池场景演示已开始，请查看日志了解详情";
    }

    /**
     * 演示Semaphore - API限流场景
     * 访问地址: GET /sync-tools/semaphore-ratelimit
     */
    @GetMapping("/semaphore-ratelimit")
    public String testSemaphoreRateLimit() {
        syncToolsDemo.demonstrateRateLimiting();
        return "Semaphore API限流场景演示已开始，请查看日志了解详情";
    }

    /**
     * 演示组合使用 - 复杂并发场景
     * 访问地址: GET /sync-tools/combined-usage
     */
    @GetMapping("/combined-usage")
    public String testCombinedUsage() {
        syncToolsDemo.demonstrateCombinedUsage();
        return "同步工具类组合使用演示已开始，请查看日志了解详情";
    }

    /**
     * 运行所有同步工具演示
     * 访问地址: GET /sync-tools/all
     */
    @GetMapping("/all")
    public String runAllDemos() {
        new Thread(() -> {
            try {
                syncToolsDemo.demonstrateCountDownLatch();
                Thread.sleep(8000);
                
                syncToolsDemo.demonstrateCyclicBarrier();
                Thread.sleep(15000);
                
                syncToolsDemo.demonstrateSemaphore();
                Thread.sleep(20000);
                
                syncToolsDemo.demonstrateRateLimiting();
                Thread.sleep(15000);
                
                syncToolsDemo.demonstrateCombinedUsage();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return "所有同步工具类演示已开始，请查看日志了解详情";
    }
}