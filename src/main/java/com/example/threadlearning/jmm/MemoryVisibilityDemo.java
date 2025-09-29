package com.example.threadlearning.jmm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Java内存模型（JMM）可见性演示
 * 演示volatile、synchronized、原子类对内存可见性的影响
 */
@Slf4j
@Component
public class MemoryVisibilityDemo {

    // 普通变量 - 可能存在可见性问题
    private static boolean normalFlag = false;
    private static int normalCounter = 0;

    // volatile变量 - 保证可见性
    private static volatile boolean volatileFlag = false;
    private static volatile int volatileCounter = 0;

    // 原子类 - 保证原子性和可见性
    private static final AtomicInteger atomicCounter = new AtomicInteger(0);

    // synchronized对象锁
    private static final Object lock = new Object();
    private static int synchronizedCounter = 0;

    /**
     * 演示普通变量的可见性问题
     */
    public void demonstrateNormalVariableVisibility() {
        log.info("=== 演示普通变量可见性问题 ===");
        
        normalFlag = false;
        normalCounter = 0;

        // 写线程
        Thread writerThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                normalCounter = 100;
                normalFlag = true;
                log.info("写线程: 设置 normalCounter = {}, normalFlag = {}", normalCounter, normalFlag);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "NormalWriter");

        // 读线程
        Thread readerThread = new Thread(() -> {
            log.info("读线程: 开始监听普通变量变化...");
            while (!normalFlag) {
                // 可能会无限循环，因为normalFlag的变化可能对当前线程不可见
            }
            log.info("读线程: 检测到 normalFlag = {}, normalCounter = {}", normalFlag, normalCounter);
        }, "NormalReader");

        readerThread.start();
        writerThread.start();

        try {
            // 等待3秒，如果读线程还在运行说明存在可见性问题
            Thread.sleep(3000);
            if (readerThread.isAlive()) {
                log.warn("读线程仍在运行，存在可见性问题！强制中断...");
                readerThread.interrupt();
            }
            writerThread.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 演示volatile变量的可见性保证
     */
    public void demonstrateVolatileVisibility() {
        log.info("\n=== 演示volatile变量可见性保证 ===");
        
        volatileFlag = false;
        volatileCounter = 0;

        // 写线程
        Thread writerThread = new Thread(() -> {
            try {
                Thread.sleep(1000);
                volatileCounter = 200;
                volatileFlag = true; // volatile写操作会立即刷新到主内存
                log.info("写线程: 设置 volatileCounter = {}, volatileFlag = {}", volatileCounter, volatileFlag);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "VolatileWriter");

        // 读线程
        Thread readerThread = new Thread(() -> {
            log.info("读线程: 开始监听volatile变量变化...");
            while (!volatileFlag) {
                // volatile读操作会从主内存读取最新值
            }
            log.info("读线程: 检测到 volatileFlag = {}, volatileCounter = {}", volatileFlag, volatileCounter);
        }, "VolatileReader");

        readerThread.start();
        writerThread.start();

        try {
            readerThread.join();
            writerThread.join();
            log.info("volatile演示完成：读线程正常结束，可见性得到保证");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 演示synchronized的可见性和原子性保证
     */
    public void demonstrateSynchronizedVisibility() {
        log.info("\n=== 演示synchronized可见性和原子性保证 ===");
        
        synchronizedCounter = 0;

        // 多个线程并发递增计数器
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    synchronized (lock) {
                        synchronizedCounter++; // synchronized保证原子性和可见性
                    }
                }
                log.info("线程 {} 完成1000次递增操作", Thread.currentThread().getName());
            }, "SyncThread-" + i);
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        try {
            for (Thread thread : threads) {
                thread.join();
            }
            log.info("所有线程完成，最终计数器值: {} (期望值: 10000)", synchronizedCounter);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 演示原子类的原子性和可见性保证
     */
    public void demonstrateAtomicVisibility() {
        log.info("\n=== 演示原子类原子性和可见性保证 ===");
        
        atomicCounter.set(0);

        // 多个线程并发递增原子计数器
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    atomicCounter.incrementAndGet(); // 原子操作，保证原子性和可见性
                }
                log.info("线程 {} 完成1000次原子递增操作", Thread.currentThread().getName());
            }, "AtomicThread-" + i);
        }

        // 启动所有线程
        for (Thread thread : threads) {
            thread.start();
        }

        // 等待所有线程完成
        try {
            for (Thread thread : threads) {
                thread.join();
            }
            log.info("所有线程完成，最终原子计数器值: {} (期望值: 10000)", atomicCounter.get());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 演示指令重排序问题
     */
    public void demonstrateReordering() {
        log.info("\n=== 演示指令重排序问题 ===");
        
        for (int i = 0; i < 100000; i++) {
            ReorderingExample example = new ReorderingExample();
            
            Thread thread1 = new Thread(example::writer);
            Thread thread2 = new Thread(example::reader);
            
            thread1.start();
            thread2.start();
            
            try {
                thread1.join();
                thread2.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            
            // 检查是否出现了重排序现象
            if (example.getResult() != 0) {
                log.info("第 {} 次测试检测到指令重排序: result = {}", i + 1, example.getResult());
                break;
            }
        }
    }

    /**
     * 指令重排序示例类
     */
    private static class ReorderingExample {
        private int a = 0;
        private boolean flag = false;
        private int result = 0;

        public void writer() {
            a = 1;          // 操作1
            flag = true;    // 操作2 - 可能被重排序到操作1之前
        }

        public void reader() {
            if (flag) {     // 操作3
                result = a; // 操作4 - 如果发生重排序，可能读到a=0
            }
        }

        public int getResult() {
            return result;
        }
    }
}