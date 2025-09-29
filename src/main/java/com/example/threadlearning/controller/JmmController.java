package com.example.threadlearning.controller;

import com.example.threadlearning.jmm.MemoryVisibilityDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Java内存模型（JMM）演示控制器
 * 提供API接口来测试内存可见性相关功能
 */
@RestController
@RequestMapping("/jmm")
public class JmmController {

    @Autowired
    private MemoryVisibilityDemo memoryVisibilityDemo;

    /**
     * 演示普通变量的可见性问题
     * 访问地址: GET /jmm/normal-visibility
     */
    @GetMapping("/normal-visibility")
    public String testNormalVariableVisibility() {
        memoryVisibilityDemo.demonstrateNormalVariableVisibility();
        return "普通变量可见性测试已开始，请查看日志了解详情（可能存在可见性问题）";
    }

    /**
     * 演示volatile变量的可见性保证
     * 访问地址: GET /jmm/volatile-visibility
     */
    @GetMapping("/volatile-visibility")
    public String testVolatileVisibility() {
        memoryVisibilityDemo.demonstrateVolatileVisibility();
        return "volatile变量可见性测试完成，请查看日志了解详情";
    }

    /**
     * 演示synchronized的可见性和原子性保证
     * 访问地址: GET /jmm/synchronized-visibility
     */
    @GetMapping("/synchronized-visibility")
    public String testSynchronizedVisibility() {
        memoryVisibilityDemo.demonstrateSynchronizedVisibility();
        return "synchronized可见性和原子性测试完成，请查看日志了解详情";
    }

    /**
     * 演示原子类的原子性和可见性保证
     * 访问地址: GET /jmm/atomic-visibility
     */
    @GetMapping("/atomic-visibility")
    public String testAtomicVisibility() {
        memoryVisibilityDemo.demonstrateAtomicVisibility();
        return "原子类可见性和原子性测试完成，请查看日志了解详情";
    }

    /**
     * 演示指令重排序问题
     * 访问地址: GET /jmm/reordering
     */
    @GetMapping("/reordering")
    public String testReordering() {
        memoryVisibilityDemo.demonstrateReordering();
        return "指令重排序测试完成，请查看日志了解详情";
    }

    /**
     * 运行所有JMM演示
     * 访问地址: GET /jmm/all
     */
    @GetMapping("/all")
    public String runAllJmmDemos() {
        memoryVisibilityDemo.demonstrateNormalVariableVisibility();
        
        try {
            Thread.sleep(5000); // 等待上一个测试完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        memoryVisibilityDemo.demonstrateVolatileVisibility();
        memoryVisibilityDemo.demonstrateSynchronizedVisibility();
        memoryVisibilityDemo.demonstrateAtomicVisibility();
        memoryVisibilityDemo.demonstrateReordering();
        
        return "所有JMM演示已完成，请查看日志了解详情";
    }
}