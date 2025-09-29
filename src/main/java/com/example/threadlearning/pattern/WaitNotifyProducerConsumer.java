package com.example.threadlearning.pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 生产者消费者模式演示
 * 使用wait/notify机制实现
 */
@Slf4j
@Component
public class WaitNotifyProducerConsumer {

    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity = 5;
    private final Object lock = new Object();

    /**
     * 生产者
     */
    public void producer() {
        log.info("=== Wait/Notify生产者消费者模式演示 ===");
        
        Thread producerThread = new Thread(() -> {
            int value = 0;
            while (value < 10) {
                synchronized (lock) {
                    try {
                        // 当队列满时，生产者等待
                        while (queue.size() == capacity) {
                            log.info("队列已满，生产者等待...");
                            lock.wait();
                        }
                        
                        queue.offer(value);
                        log.info("生产者生产了: {}, 当前队列大小: {}", value, queue.size());
                        value++;
                        
                        // 通知消费者
                        lock.notifyAll();
                        
                        Thread.sleep(1000); // 模拟生产时间
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            log.info("生产者完成生产");
        }, "WaitNotify-Producer");

        // 消费者
        Thread consumerThread = new Thread(() -> {
            int consumed = 0;
            while (consumed < 10) {
                synchronized (lock) {
                    try {
                        // 当队列空时，消费者等待
                        while (queue.isEmpty()) {
                            log.info("队列为空，消费者等待...");
                            lock.wait();
                        }
                        
                        Integer value = queue.poll();
                        log.info("消费者消费了: {}, 当前队列大小: {}", value, queue.size());
                        consumed++;
                        
                        // 通知生产者
                        lock.notifyAll();
                        
                        Thread.sleep(1500); // 模拟消费时间
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            log.info("消费者完成消费");
        }, "WaitNotify-Consumer");

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
            log.info("Wait/Notify模式演示完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

