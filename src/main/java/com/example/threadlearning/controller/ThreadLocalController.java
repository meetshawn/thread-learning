package com.example.threadlearning.controller;

import com.example.threadlearning.threadlocal.ThreadLocalDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ThreadLocal演示控制器
 * 提供API接口来测试ThreadLocal的使用和内存泄漏防范
 */
@RestController
@RequestMapping("/thread-local")
public class ThreadLocalController {

    @Autowired
    private ThreadLocalDemo threadLocalDemo;

    /**
     * ThreadLocal基础使用演示
     * 访问地址: GET /thread-local/basic-usage
     */
    @GetMapping("/basic-usage")
    public String testBasicUsage() {
        threadLocalDemo.demonstrateBasicUsage();
        return "ThreadLocal基础使用演示已完成，请查看日志了解详情";
    }

    /**
     * 线程池中ThreadLocal使用演示
     * 访问地址: GET /thread-local/thread-pool-usage
     */
    @GetMapping("/thread-pool-usage")
    public String testThreadPoolUsage() {
        threadLocalDemo.demonstrateThreadPoolUsage();
        return "线程池中ThreadLocal使用演示已完成，请查看日志了解详情";
    }

    /**
     * ThreadLocal内存泄漏演示
     * 访问地址: GET /thread-local/memory-leak
     */
    @GetMapping("/memory-leak")
    public String testMemoryLeak() {
        threadLocalDemo.demonstrateMemoryLeak();
        return "ThreadLocal内存泄漏演示已完成，请查看日志了解详情";
    }

    /**
     * InheritableThreadLocal演示
     * 访问地址: GET /thread-local/inheritable
     */
    @GetMapping("/inheritable")
    public String testInheritableThreadLocal() {
        threadLocalDemo.demonstrateInheritableThreadLocal();
        return "InheritableThreadLocal演示已完成，请查看日志了解详情";
    }

    /**
     * DateFormat线程安全演示
     * 访问地址: GET /thread-local/dateformat-safety
     */
    @GetMapping("/dateformat-safety")
    public String testDateFormatSafety() {
        threadLocalDemo.demonstrateDateFormatSafety();
        return "ThreadLocal实现DateFormat线程安全演示已完成，请查看日志了解详情";
    }

    /**
     * ThreadLocal性能对比演示
     * 访问地址: GET /thread-local/performance-comparison
     */
    @GetMapping("/performance-comparison")
    public String testPerformanceComparison() {
        threadLocalDemo.demonstratePerformanceComparison();
        return "ThreadLocal性能对比演示已完成，请查看日志了解详情";
    }

    /**
     * 运行所有ThreadLocal演示
     * 访问地址: GET /thread-local/all
     */
    @GetMapping("/all")
    public String runAllDemos() {
        new Thread(() -> {
            try {
                threadLocalDemo.demonstrateBasicUsage();
                Thread.sleep(3000);
                
                threadLocalDemo.demonstrateThreadPoolUsage();
                Thread.sleep(10000);
                
                threadLocalDemo.demonstrateInheritableThreadLocal();
                Thread.sleep(2000);
                
                threadLocalDemo.demonstrateDateFormatSafety();
                Thread.sleep(3000);
                
                threadLocalDemo.demonstrateMemoryLeak();
                Thread.sleep(6000);
                
                threadLocalDemo.demonstratePerformanceComparison();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return "所有ThreadLocal演示已开始，请查看日志了解详情";
    }
}