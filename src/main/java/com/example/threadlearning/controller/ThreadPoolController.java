package com.example.threadlearning.controller;

import com.example.threadlearning.service.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

/**
 * 线程池测试控制器
 * 提供API接口来测试自定义线程池功能
 */
@RestController
@RequestMapping("/thread-pool")
public class ThreadPoolController {

    @Autowired
    private AsyncService asyncService;

    /**
     * 测试默认异步任务
     * 访问地址: GET /thread-pool/test-default
     */
    @GetMapping("/test-default")
    public String testDefaultAsync() {
        asyncService.executeAsyncTask();
        return "默认异步任务已提交，请查看日志了解执行详情";
    }

    /**
     * 测试自定义线程池
     * 访问地址: GET /thread-pool/test-custom
     */
    @GetMapping("/test-custom")
    public String testCustomThreadPool() {
        asyncService.executeTaskWithCustomThreadPool();
        return "自定义线程池任务已提交，请查看日志了解执行详情";
    }

    /**
     * 测试IO线程池
     * 访问地址: GET /thread-pool/test-io
     */
    @GetMapping("/test-io")
    public String testIoThreadPool() {
        asyncService.executeIoTask();
        return "IO线程池任务已提交，请查看日志了解执行详情";
    }

    /**
     * 同时提交多个任务测试线程池
     * 访问地址: GET /thread-pool/test-multiple
     */
    @GetMapping("/test-multiple")
    public String testMultipleTasks() {
        // 同时提交多个任务
        for (int i = 1; i <= 5; i++) {
            final int taskNumber = i;
            
            // 使用不同的线程池
            if (i % 2 == 0) {
                asyncService.executeTaskWithCustomThreadPool();
            } else {
                asyncService.executeIoTask();
            }
        }
        
        return "已提交5个任务到不同线程池，请查看日志了解执行详情";
    }
}