package com.example.threadlearning.controller;

import com.example.threadlearning.lock.LockUpgradeDemo;
import com.example.threadlearning.monitor.ThreadPoolMonitorDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 高级并发特性演示控制器
 * 包含锁升级和线程池监控功能
 */
@RestController
@RequestMapping("/advanced")
public class AdvancedConcurrencyController {

    @Autowired
    private LockUpgradeDemo lockUpgradeDemo;

    @Autowired
    private ThreadPoolMonitorDemo threadPoolMonitorDemo;

    /**
     * 锁升级过程演示
     * 访问地址: GET /advanced/lock-upgrade
     */
    @GetMapping("/lock-upgrade")
    public String testLockUpgrade() {
        lockUpgradeDemo.demonstrateLockUpgrade();
        return "锁升级过程演示已完成，请查看日志了解详情";
    }

    /**
     * 锁性能对比演示
     * 访问地址: GET /advanced/lock-performance
     */
    @GetMapping("/lock-performance")
    public String testLockPerformance() {
        lockUpgradeDemo.demonstrateLockPerformanceComparison();
        return "锁性能对比演示已完成，请查看日志了解详情";
    }

    /**
     * 读写锁优势演示
     * 访问地址: GET /advanced/read-write-lock
     */
    @GetMapping("/read-write-lock")
    public String testReadWriteLock() {
        lockUpgradeDemo.demonstrateReadWriteLockAdvantage();
        return "读写锁优势演示已完成，请查看日志了解详情";
    }

    /**
     * 可重入锁演示
     * 访问地址: GET /advanced/reentrant-lock
     */
    @GetMapping("/reentrant-lock")
    public String testReentrantLock() {
        lockUpgradeDemo.demonstrateReentrantLock();
        return "可重入锁演示已完成，请查看日志了解详情";
    }

    /**
     * 自定义拒绝策略演示
     * 访问地址: GET /advanced/custom-rejection
     */
    @GetMapping("/custom-rejection")
    public String testCustomRejection() {
        threadPoolMonitorDemo.demonstrateCustomRejectionPolicies();
        return "自定义拒绝策略演示已完成，请查看日志了解详情";
    }

    /**
     * 线程池监控演示
     * 访问地址: GET /advanced/thread-pool-monitoring
     */
    @GetMapping("/thread-pool-monitoring")
    public String testThreadPoolMonitoring() {
        threadPoolMonitorDemo.demonstrateThreadPoolMonitoring();
        return "线程池监控演示已开始，请查看日志了解详情";
    }

    /**
     * 运行所有高级特性演示
     * 访问地址: GET /advanced/all
     */
    @GetMapping("/all")
    public String runAllDemos() {
        new Thread(() -> {
            try {
                lockUpgradeDemo.demonstrateLockUpgrade();
                Thread.sleep(3000);
                
                lockUpgradeDemo.demonstrateReentrantLock();
                Thread.sleep(2000);
                
                lockUpgradeDemo.demonstrateReadWriteLockAdvantage();
                Thread.sleep(8000);
                
                lockUpgradeDemo.demonstrateLockPerformanceComparison();
                Thread.sleep(5000);
                
                threadPoolMonitorDemo.demonstrateCustomRejectionPolicies();
                Thread.sleep(15000);
                
                threadPoolMonitorDemo.demonstrateThreadPoolMonitoring();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return "所有高级并发特性演示已开始，请查看日志了解详情";
    }
}