package com.example.threadlearning.pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock; /**
 * 使用Lock和Condition实现的生产者消费者模式
 */
@Slf4j
@Component
public class LockConditionProducerConsumer {

    private final Queue<Integer> queue = new LinkedList<>();
    private final int capacity = 5;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    /**
     * 生产者消费者模式 - Lock版本
     */
    public void producer() {
        log.info("\n=== Lock/Condition生产者消费者模式演示 ===");
        
        Thread producerThread = new Thread(() -> {
            int value = 0;
            while (value < 10) {
                lock.lock();
                try {
                    // 当队列满时，生产者等待
                    while (queue.size() == capacity) {
                        log.info("队列已满，生产者等待...");
                        notFull.await();
                    }
                    
                    queue.offer(value);
                    log.info("生产者生产了: {}, 当前队列大小: {}", value, queue.size());
                    value++;
                    
                    // 通知消费者队列不为空
                    notEmpty.signal();
                    
                    Thread.sleep(1000); // 模拟生产时间
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    lock.unlock();
                }
            }
            log.info("生产者完成生产");
        }, "Lock-Producer");

        Thread consumerThread = new Thread(() -> {
            int consumed = 0;
            while (consumed < 10) {
                lock.lock();
                try {
                    // 当队列空时，消费者等待
                    while (queue.isEmpty()) {
                        log.info("队列为空，消费者等待...");
                        notEmpty.await();
                    }
                    
                    Integer value = queue.poll();
                    log.info("消费者消费了: {}, 当前队列大小: {}", value, queue.size());
                    consumed++;
                    
                    // 通知生产者队列不满
                    notFull.signal();
                    
                    Thread.sleep(1500); // 模拟消费时间
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } finally {
                    lock.unlock();
                }
            }
            log.info("消费者完成消费");
        }, "Lock-Consumer");

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
            log.info("Lock/Condition模式演示完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
