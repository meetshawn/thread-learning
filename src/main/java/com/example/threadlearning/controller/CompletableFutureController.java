package com.example.threadlearning.controller;

import com.example.threadlearning.async.CompletableFutureDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CompletableFuture异步编程演示控制器
 * 提供API接口来测试CompletableFuture的各种功能
 */
@RestController
@RequestMapping("/completable-future")
public class CompletableFutureController {

    @Autowired
    private CompletableFutureDemo completableFutureDemo;

    /**
     * 基础CompletableFuture使用演示
     * 访问地址: GET /completable-future/basic-usage
     */
    @GetMapping("/basic-usage")
    public String testBasicUsage() {
        completableFutureDemo.demonstrateBasicUsage();
        return "CompletableFuture基础使用演示已完成，请查看日志了解详情";
    }

    /**
     * 链式操作演示
     * 访问地址: GET /completable-future/chaining
     */
    @GetMapping("/chaining")
    public String testChaining() {
        completableFutureDemo.demonstrateChaining();
        return "CompletableFuture链式操作演示已完成，请查看日志了解详情";
    }

    /**
     * 组合多个CompletableFuture演示
     * 访问地址: GET /completable-future/combining
     */
    @GetMapping("/combining")
    public String testCombining() {
        completableFutureDemo.demonstrateCombining();
        return "CompletableFuture组合操作演示已完成，请查看日志了解详情";
    }

    /**
     * 异常处理演示
     * 访问地址: GET /completable-future/exception-handling
     */
    @GetMapping("/exception-handling")
    public String testExceptionHandling() {
        completableFutureDemo.demonstrateExceptionHandling();
        return "CompletableFuture异常处理演示已完成，请查看日志了解详情";
    }

    /**
     * 超时处理演示
     * 访问地址: GET /completable-future/timeout
     */
    @GetMapping("/timeout")
    public String testTimeout() {
        completableFutureDemo.demonstrateTimeout();
        return "CompletableFuture超时处理演示已完成，请查看日志了解详情";
    }

    /**
     * 并行处理演示
     * 访问地址: GET /completable-future/parallel-processing
     */
    @GetMapping("/parallel-processing")
    public String testParallelProcessing() {
        completableFutureDemo.demonstrateParallelProcessing();
        return "CompletableFuture并行处理演示已完成，请查看日志了解详情";
    }

    /**
     * anyOf竞速演示
     * 访问地址: GET /completable-future/any-of
     */
    @GetMapping("/any-of")
    public String testAnyOf() {
        completableFutureDemo.demonstrateAnyOf();
        return "CompletableFuture.anyOf竞速演示已完成，请查看日志了解详情";
    }

    /**
     * 运行所有CompletableFuture演示
     * 访问地址: GET /completable-future/all
     */
    @GetMapping("/all")
    public String runAllDemos() {
        new Thread(() -> {
            try {
                completableFutureDemo.demonstrateBasicUsage();
                Thread.sleep(2000);
                
                completableFutureDemo.demonstrateChaining();
                Thread.sleep(3000);
                
                completableFutureDemo.demonstrateCombining();
                Thread.sleep(4000);
                
                completableFutureDemo.demonstrateExceptionHandling();
                Thread.sleep(3000);
                
                completableFutureDemo.demonstrateTimeout();
                Thread.sleep(7000);
                
                completableFutureDemo.demonstrateParallelProcessing();
                Thread.sleep(5000);
                
                completableFutureDemo.demonstrateAnyOf();
                Thread.sleep(3000);
                
                completableFutureDemo.cleanup();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return "所有CompletableFuture演示已开始，请查看日志了解详情";
    }
}