package com.example.threadlearning.controller;

import com.example.threadlearning.concurrent.ConcurrentMapDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 并发集合演示控制器
 * 提供API接口来测试ConcurrentHashMap vs HashMap的并发安全性
 */
@RestController
@RequestMapping("/concurrent-map")
public class ConcurrentMapController {

    @Autowired
    private ConcurrentMapDemo concurrentMapDemo;

    /**
     * 演示HashMap的线程不安全性
     * 访问地址: GET /concurrent-map/hashmap-unsafe
     */
    @GetMapping("/hashmap-unsafe")
    public String testHashMapUnsafety() {
        concurrentMapDemo.demonstrateHashMapUnsafety();
        return "HashMap线程不安全性演示已完成，请查看日志了解详情（可能出现数据丢失）";
    }

    /**
     * 演示ConcurrentHashMap的线程安全性
     * 访问地址: GET /concurrent-map/concurrenthashmap-safe
     */
    @GetMapping("/concurrenthashmap-safe")
    public String testConcurrentHashMapSafety() {
        concurrentMapDemo.demonstrateConcurrentHashMapSafety();
        return "ConcurrentHashMap线程安全性演示已完成，请查看日志了解详情";
    }

    /**
     * 演示并发修改异常
     * 访问地址: GET /concurrent-map/concurrent-modification-exception
     */
    @GetMapping("/concurrent-modification-exception")
    public String testConcurrentModificationException() {
        concurrentMapDemo.demonstrateConcurrentModificationException();
        return "ConcurrentModificationException演示已完成，请查看日志了解详情";
    }

    /**
     * 性能对比测试
     * 访问地址: GET /concurrent-map/performance-comparison
     */
    @GetMapping("/performance-comparison")
    public String performanceComparison() {
        concurrentMapDemo.performanceComparison();
        return "HashMap vs ConcurrentHashMap 性能对比测试已完成，请查看日志了解详情";
    }

    /**
     * 演示ConcurrentHashMap的原子操作
     * 访问地址: GET /concurrent-map/atomic-operations
     */
    @GetMapping("/atomic-operations")
    public String testAtomicOperations() {
        concurrentMapDemo.demonstrateAtomicOperations();
        return "ConcurrentHashMap原子操作演示已完成，请查看日志了解详情";
    }

    /**
     * 运行所有并发集合演示
     * 访问地址: GET /concurrent-map/all
     */
    @GetMapping("/all")
    public String runAllDemos() {
        new Thread(() -> {
            try {
                concurrentMapDemo.demonstrateHashMapUnsafety();
                Thread.sleep(2000);
                
                concurrentMapDemo.demonstrateConcurrentHashMapSafety();
                Thread.sleep(2000);
                
                concurrentMapDemo.demonstrateConcurrentModificationException();
                Thread.sleep(2000);
                
                concurrentMapDemo.demonstrateAtomicOperations();
                Thread.sleep(2000);
                
                concurrentMapDemo.performanceComparison();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return "所有并发集合演示已开始，请查看日志了解详情";
    }
}