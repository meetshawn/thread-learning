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
    }

}
