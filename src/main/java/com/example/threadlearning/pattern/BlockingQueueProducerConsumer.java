package com.example.threadlearning.pattern;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * 使用BlockingQueue实现的生产者消费者模式
 * 演示不同类型的阻塞队列
 */
@Slf4j
@Component
public class BlockingQueueProducerConsumer {

    /**
     * 使用ArrayBlockingQueue实现
     */
    public void arrayBlockingQueueDemo() {
        log.info("\n=== ArrayBlockingQueue生产者消费者模式演示 ===");
        
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);
        
        // 生产者
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    queue.put(i); // 队列满时会阻塞
                    log.info("ArrayBlockingQueue生产者生产了: {}, 当前队列大小: {}", i, queue.size());
                    Thread.sleep(800);
                }
                log.info("ArrayBlockingQueue生产者完成生产");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "ArrayBlocking-Producer");

        // 消费者
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    Integer value = queue.take(); // 队列空时会阻塞
                    log.info("ArrayBlockingQueue消费者消费了: {}, 当前队列大小: {}", value, queue.size());
                    Thread.sleep(1200);
                }
                log.info("ArrayBlockingQueue消费者完成消费");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "ArrayBlocking-Consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
            log.info("ArrayBlockingQueue演示完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 使用LinkedBlockingQueue实现
     */
    public void linkedBlockingQueueDemo() {
        log.info("\n=== LinkedBlockingQueue生产者消费者模式演示 ===");
        
        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(5);
        
        // 生产者
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    queue.put(i);
                    log.info("LinkedBlockingQueue生产者生产了: {}, 当前队列大小: {}", i, queue.size());
                    Thread.sleep(800);
                }
                log.info("LinkedBlockingQueue生产者完成生产");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "LinkedBlocking-Producer");

        // 消费者
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    Integer value = queue.take();
                    log.info("LinkedBlockingQueue消费者消费了: {}, 当前队列大小: {}", value, queue.size());
                    Thread.sleep(1200);
                }
                log.info("LinkedBlockingQueue消费者完成消费");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "LinkedBlocking-Consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
            log.info("LinkedBlockingQueue演示完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 使用SynchronousQueue实现
     * SynchronousQueue没有容量，必须有消费者等待才能生产
     */
    public void synchronousQueueDemo() {
        log.info("\n=== SynchronousQueue生产者消费者模式演示 ===");
        
        BlockingQueue<Integer> queue = new SynchronousQueue<>();
        
        // 生产者
        Thread producer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    log.info("SynchronousQueue生产者准备生产: {}", i);
                    queue.put(i); // 必须等待消费者接收
                    log.info("SynchronousQueue生产者成功生产了: {}", i);
                    Thread.sleep(1000);
                }
                log.info("SynchronousQueue生产者完成生产");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Synchronous-Producer");

        // 消费者
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 0; i < 5; i++) {
                    Thread.sleep(2000); // 延迟消费，观察生产者阻塞
                    log.info("SynchronousQueue消费者准备消费...");
                    Integer value = queue.take();
                    log.info("SynchronousQueue消费者消费了: {}", value);
                }
                log.info("SynchronousQueue消费者完成消费");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "Synchronous-Consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
            log.info("SynchronousQueue演示完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 多生产者多消费者演示
     */
    public void multipleProducersConsumersDemo() {
        log.info("\n=== 多生产者多消费者模式演示 ===");
        
        BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);
        
        // 创建多个生产者
        Thread[] producers = new Thread[3];
        for (int i = 0; i < 3; i++) {
            final int producerId = i;
            producers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 5; j++) {
                        int value = producerId * 100 + j;
                        queue.put(value);
                        log.info("生产者{} 生产了: {}, 当前队列大小: {}", producerId, value, queue.size());
                        Thread.sleep(500);
                    }
                    log.info("生产者{} 完成生产", producerId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Multi-Producer-" + i);
        }

        // 创建多个消费者
        Thread[] consumers = new Thread[2];
        for (int i = 0; i < 2; i++) {
            final int consumerId = i;
            consumers[i] = new Thread(() -> {
                try {
                    for (int j = 0; j < 7; j++) { // 总共15个产品，2个消费者分别消费7和8个
                        Integer value = queue.take();
                        log.info("消费者{} 消费了: {}, 当前队列大小: {}", consumerId, value, queue.size());
                        Thread.sleep(800);
                    }
                    if (consumerId == 1) {
                        // 消费者1多消费一个
                        Integer value = queue.take();
                        log.info("消费者{} 消费了: {}, 当前队列大小: {}", consumerId, value, queue.size());
                    }
                    log.info("消费者{} 完成消费", consumerId);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }, "Multi-Consumer-" + i);
        }

        // 启动所有线程
        for (Thread producer : producers) {
            producer.start();
        }
        for (Thread consumer : consumers) {
            consumer.start();
        }

        try {
            // 等待所有线程完成
            for (Thread producer : producers) {
                producer.join();
            }
            for (Thread consumer : consumers) {
                consumer.join();
            }
            log.info("多生产者多消费者演示完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}