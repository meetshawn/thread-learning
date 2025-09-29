package com.example.threadlearning.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 异步服务类
 * 演示如何使用自定义线程池执行异步任务
 */
@Service
public class AsyncService {

    private static final Logger logger = LoggerFactory.getLogger(AsyncService.class);

    /**
     * 使用自定义线程池执行异步任务
     * 默认使用@Async注解，会使用默认的线程池
     */
    @Async
    public void executeAsyncTask() {
        logger.info("执行异步任务 - 线程名称: {}", Thread.currentThread().getName());
        
        try {
            // 模拟任务执行时间
            Thread.sleep(2000);
            logger.info("异步任务执行完成 - 线程名称: {}", Thread.currentThread().getName());
        } catch (InterruptedException e) {
            logger.error("异步任务被中断", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 使用指定的自定义线程池执行异步任务
     * 通过value属性指定要使用的线程池名称
     */
    @Async("customThreadPool")
    public void executeTaskWithCustomThreadPool() {
        logger.info("使用自定义线程池执行任务 - 线程名称: {}", Thread.currentThread().getName());
        
        try {
            // 模拟任务执行时间
            Thread.sleep(3000);
            logger.info("自定义线程池任务执行完成 - 线程名称: {}", Thread.currentThread().getName());
        } catch (InterruptedException e) {
            logger.error("自定义线程池任务被中断", e);
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 使用IO线程池执行异步任务
     * 适用于IO密集型任务
     */
    @Async("ioThreadPool")
    public void executeIoTask() {
        logger.info("使用IO线程池执行任务 - 线程名称: {}", Thread.currentThread().getName());
        
        try {
            // 模拟IO操作
            Thread.sleep(5000);
            logger.info("IO任务执行完成 - 线程名称: {}", Thread.currentThread().getName());
        } catch (InterruptedException e) {
            logger.error("IO任务被中断", e);
            Thread.currentThread().interrupt();
        }
    }
}