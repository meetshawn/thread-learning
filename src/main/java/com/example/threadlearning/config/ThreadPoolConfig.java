package com.example.threadlearning.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 自定义线程池配置类
 * 配置并管理应用程序中的线程池
 * 所有线程池参数均可通过配置文件动态调整
 */
@Configuration
@EnableAsync  // 启用异步执行
public class ThreadPoolConfig {

    // 自定义线程池配置
    @Value("${thread.pool.custom.core-pool-size:5}")
    private int customCorePoolSize;

    @Value("${thread.pool.custom.max-pool-size:10}")
    private int customMaxPoolSize;

    @Value("${thread.pool.custom.queue-capacity:100}")
    private int customQueueCapacity;

    @Value("${thread.pool.custom.keep-alive-seconds:60}")
    private int customKeepAliveSeconds;

    @Value("${thread.pool.custom.thread-name-prefix:CustomThread-}")
    private String customThreadNamePrefix;

    @Value("${thread.pool.custom.await-termination-seconds:60}")
    private int customAwaitTerminationSeconds;

    // IO线程池配置
    @Value("${thread.pool.io.core-pool-size:10}")
    private int ioCorePoolSize;

    @Value("${thread.pool.io.max-pool-size:20}")
    private int ioMaxPoolSize;

    @Value("${thread.pool.io.queue-capacity:200}")
    private int ioQueueCapacity;

    @Value("${thread.pool.io.keep-alive-seconds:120}")
    private int ioKeepAliveSeconds;

    @Value("${thread.pool.io.thread-name-prefix:IOThread-}")
    private String ioThreadNamePrefix;

    @Value("${thread.pool.io.await-termination-seconds:60}")
    private int ioAwaitTerminationSeconds;

    // 默认线程池配置（Spring默认异步执行器）
    @Value("${thread.pool.default.core-pool-size:8}")
    private int defaultCorePoolSize;

    @Value("${thread.pool.default.max-pool-size:16}")
    private int defaultMaxPoolSize;

    @Value("${thread.pool.default.queue-capacity:50}")
    private int defaultQueueCapacity;

    @Value("${thread.pool.default.keep-alive-seconds:30}")
    private int defaultKeepAliveSeconds;

    @Value("${thread.pool.default.thread-name-prefix:DefaultThread-}")
    private String defaultThreadNamePrefix;

    /**
     * 创建自定义线程池
     * 该线程池将被Spring Boot管理，可在应用中使用@Async注解调用
     * 所有参数均可通过配置文件动态调整
     * 
     * @return Executor 线程池执行器
     */
    @Bean("customThreadPool")
    public Executor customThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数：线程池保持的最小线程数
        executor.setCorePoolSize(customCorePoolSize);
        
        // 最大线程数：线程池允许的最大线程数
        executor.setMaxPoolSize(customMaxPoolSize);
        
        // 队列容量：用于保存等待执行的任务的队列大小
        executor.setQueueCapacity(customQueueCapacity);
        
        // 线程名称前缀，便于日志识别
        executor.setThreadNamePrefix(customThreadNamePrefix);
        
        // 线程空闲时间：当线程数超过核心线程数时，多余的线程空闲时间超过此值将被回收
        executor.setKeepAliveSeconds(customKeepAliveSeconds);
        
        // 拒绝策略：当线程池达到最大线程数且队列已满时的处理策略
        // CallerRunsPolicy：由调用线程处理该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 等待所有任务完成后再关闭线程池
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // 设置等待时间（秒）
        executor.setAwaitTerminationSeconds(customAwaitTerminationSeconds);
        
        // 初始化线程池
        executor.initialize();
        
        return executor;
    }

    /**
     * 创建IO密集型专用线程池
     * 用于处理IO密集型任务，如网络请求、文件读写等
     * 所有参数均可通过配置文件动态调整
     * 
     * @return Executor IO线程池执行器
     */
    @Bean("ioThreadPool")
    public Executor ioThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // IO密集型任务，核心线程数可以设置大一些
        executor.setCorePoolSize(ioCorePoolSize);
        
        // 最大线程数
        executor.setMaxPoolSize(ioMaxPoolSize);
        
        // 队列容量
        executor.setQueueCapacity(ioQueueCapacity);
        
        // 线程名称前缀
        executor.setThreadNamePrefix(ioThreadNamePrefix);
        
        // 空闲时间
        executor.setKeepAliveSeconds(ioKeepAliveSeconds);
        
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 优雅关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(ioAwaitTerminationSeconds);
        
        executor.initialize();
        
        return executor;
    }

    /**
     * 配置Spring默认异步执行器
     * 用于处理@Async注解的默认线程池
     * 所有参数均可通过配置文件动态调整
     * 
     * @return Executor 默认线程池执行器
     */
    @Bean("taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // 核心线程数
        executor.setCorePoolSize(defaultCorePoolSize);
        
        // 最大线程数
        executor.setMaxPoolSize(defaultMaxPoolSize);
        
        // 队列容量
        executor.setQueueCapacity(defaultQueueCapacity);
        
        // 线程名称前缀
        executor.setThreadNamePrefix(defaultThreadNamePrefix);
        
        // 空闲时间
        executor.setKeepAliveSeconds(defaultKeepAliveSeconds);
        
        // 拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        
        // 优雅关闭
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        
        executor.initialize();
        
        return executor;
    }
}