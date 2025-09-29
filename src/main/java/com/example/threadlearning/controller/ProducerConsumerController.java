package com.example.threadlearning.controller;

import com.example.threadlearning.pattern.BlockingQueueProducerConsumer;
import com.example.threadlearning.pattern.LockConditionProducerConsumer;
import com.example.threadlearning.pattern.WaitNotifyProducerConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 生产者消费者模式演示控制器
 * 提供API接口来测试不同的生产者消费者实现方式
 */
@RestController
@RequestMapping("/producer-consumer")
public class ProducerConsumerController {

    @Autowired
    private WaitNotifyProducerConsumer waitNotifyProducerConsumer;

    @Autowired
    private LockConditionProducerConsumer lockConditionProducerConsumer;

    @Autowired
    private BlockingQueueProducerConsumer blockingQueueProducerConsumer;

    /**
     * 演示wait/notify实现的生产者消费者模式
     * 访问地址: GET /producer-consumer/wait-notify
     */
    @GetMapping("/wait-notify")
    public String testWaitNotify() {
        waitNotifyProducerConsumer.producer();
        return "Wait/Notify生产者消费者模式演示已开始，请查看日志了解详情";
    }

    /**
     * 演示Lock/Condition实现的生产者消费者模式
     * 访问地址: GET /producer-consumer/lock-condition
     */
    @GetMapping("/lock-condition")
    public String testLockCondition() {
        lockConditionProducerConsumer.producer();
        return "Lock/Condition生产者消费者模式演示已开始，请查看日志了解详情";
    }

    /**
     * 演示ArrayBlockingQueue实现的生产者消费者模式
     * 访问地址: GET /producer-consumer/array-blocking-queue
     */
    @GetMapping("/array-blocking-queue")
    public String testArrayBlockingQueue() {
        blockingQueueProducerConsumer.arrayBlockingQueueDemo();
        return "ArrayBlockingQueue生产者消费者模式演示已开始，请查看日志了解详情";
    }

    /**
     * 演示LinkedBlockingQueue实现的生产者消费者模式
     * 访问地址: GET /producer-consumer/linked-blocking-queue
     */
    @GetMapping("/linked-blocking-queue")
    public String testLinkedBlockingQueue() {
        blockingQueueProducerConsumer.linkedBlockingQueueDemo();
        return "LinkedBlockingQueue生产者消费者模式演示已开始，请查看日志了解详情";
    }

    /**
     * 演示SynchronousQueue实现的生产者消费者模式
     * 访问地址: GET /producer-consumer/synchronous-queue
     */
    @GetMapping("/synchronous-queue")
    public String testSynchronousQueue() {
        blockingQueueProducerConsumer.synchronousQueueDemo();
        return "SynchronousQueue生产者消费者模式演示已开始，请查看日志了解详情";
    }

    /**
     * 演示多生产者多消费者模式
     * 访问地址: GET /producer-consumer/multiple
     */
    @GetMapping("/multiple")
    public String testMultipleProducersConsumers() {
        blockingQueueProducerConsumer.multipleProducersConsumersDemo();
        return "多生产者多消费者模式演示已开始，请查看日志了解详情";
    }

    /**
     * 运行所有生产者消费者演示
     * 访问地址: GET /producer-consumer/all
     */
    @GetMapping("/all")
    public String runAllDemos() {
        // 依次运行所有演示，中间有延迟
        new Thread(() -> {
            try {
                waitNotifyProducerConsumer.producer();
                Thread.sleep(2000);
                
                lockConditionProducerConsumer.producer();
                Thread.sleep(2000);
                
                blockingQueueProducerConsumer.arrayBlockingQueueDemo();
                Thread.sleep(2000);
                
                blockingQueueProducerConsumer.linkedBlockingQueueDemo();
                Thread.sleep(2000);
                
                blockingQueueProducerConsumer.synchronousQueueDemo();
                Thread.sleep(2000);
                
                blockingQueueProducerConsumer.multipleProducersConsumersDemo();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        return "所有生产者消费者模式演示已开始，请查看日志了解详情";
    }
}