package com.example.threadlearning;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Spring Boot启动类
 * 启用异步执行功能
 */
@SpringBootApplication
@EnableAsync  // 启用异步执行功能
public class ThreadLearningApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThreadLearningApplication.class, args);
        System.out.println("线程池学习应用启动成功！");
        System.out.println("测试接口：");
        System.out.println("- 测试默认异步任务: http://localhost:8080/thread-pool/test-default");
        System.out.println("- 测试自定义线程池: http://localhost:8080/thread-pool/test-custom");
        System.out.println("- 测试IO线程池: http://localhost:8080/thread-pool/test-io");
        System.out.println("- 测试多个任务: http://localhost:8080/thread-pool/test-multiple");
    }

}
