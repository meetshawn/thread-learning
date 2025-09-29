package com.example.threadlearning.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * CompletableFuture异步编程演示
 * 包含组合、异常处理、超时处理等各种场景
 */
@Slf4j
@Component
public class CompletableFutureDemo {

    private final Random random = new Random();
    private final ExecutorService customExecutor = Executors.newFixedThreadPool(4);

    /**
     * 基础CompletableFuture使用演示
     */
    public void demonstrateBasicUsage() {
        log.info("=== CompletableFuture基础使用演示 ===");

        // 1. 创建已完成的CompletableFuture
        CompletableFuture<String> completedFuture = CompletableFuture.completedFuture("Hello");
        log.info("已完成的Future结果: {}", completedFuture.join());

        // 2. 异步执行Supplier
        CompletableFuture<Integer> supplyAsyncFuture = CompletableFuture.supplyAsync(() -> {
            log.info("异步计算中... 线程: {}", Thread.currentThread().getName());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return 42;
        });

        // 3. 异步执行Runnable
        CompletableFuture<Void> runAsyncFuture = CompletableFuture.runAsync(() -> {
            log.info("异步执行任务... 线程: {}", Thread.currentThread().getName());
            try {
                Thread.sleep(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            log.info("异步任务完成");
        });

        // 等待结果
        log.info("供应异步结果: {}", supplyAsyncFuture.join());
        runAsyncFuture.join();
        log.info("基础演示完成");
    }

    /**
     * 链式操作演示
     */
    public void demonstrateChaining() {
        log.info("\n=== CompletableFuture链式操作演示 ===");

        CompletableFuture<String> chainedFuture = CompletableFuture
                .supplyAsync(() -> {
                    log.info("步骤1: 获取用户ID... 线程: {}", Thread.currentThread().getName());
                    simulateDelay(1000);
                    return "user123";
                })
                .thenApply(userId -> {
                    log.info("步骤2: 根据用户ID {} 获取用户信息... 线程: {}", userId, Thread.currentThread().getName());
                    simulateDelay(800);
                    return "User{id=" + userId + ", name='张三'}";
                })
                .thenApply(userInfo -> {
                    log.info("步骤3: 格式化用户信息 {} ... 线程: {}", userInfo, Thread.currentThread().getName());
                    simulateDelay(500);
                    return "格式化后的用户信息: " + userInfo;
                });

        String result = chainedFuture.join();
        log.info("链式操作最终结果: {}", result);
    }

    /**
     * 组合多个CompletableFuture演示
     */
    public void demonstrateCombining() {
        log.info("\n=== CompletableFuture组合操作演示 ===");

        // 创建多个异步任务
        CompletableFuture<String> userServiceFuture = CompletableFuture.supplyAsync(() -> {
            log.info("用户服务: 获取用户信息... 线程: {}", Thread.currentThread().getName());
            simulateDelay(1500);
            return "UserInfo{name='李四', age=25}";
        }, customExecutor);

        CompletableFuture<String> orderServiceFuture = CompletableFuture.supplyAsync(() -> {
            log.info("订单服务: 获取订单信息... 线程: {}", Thread.currentThread().getName());
            simulateDelay(1200);
            return "OrderInfo{orderId='ORDER001', amount=299.99}";
        }, customExecutor);

        CompletableFuture<String> productServiceFuture = CompletableFuture.supplyAsync(() -> {
            log.info("产品服务: 获取产品信息... 线程: {}", Thread.currentThread().getName());
            simulateDelay(1000);
            return "ProductInfo{productId='PROD001', name='智能手机'}";
        }, customExecutor);

        // 使用thenCombine组合两个Future
        CompletableFuture<String> userOrderCombined = userServiceFuture.thenCombine(
                orderServiceFuture,
                (userInfo, orderInfo) -> {
                    log.info("组合用户和订单信息... 线程: {}", Thread.currentThread().getName());
                    return "组合结果1: " + userInfo + " + " + orderInfo;
                }
        );

        // 使用allOf等待所有Future完成
        CompletableFuture<Void> allServicesFuture = CompletableFuture.allOf(
                userServiceFuture, orderServiceFuture, productServiceFuture
        );

        // 当所有服务完成后，组合所有结果
        CompletableFuture<String> allCombinedFuture = allServicesFuture.thenApply(v -> {
            String userInfo = userServiceFuture.join();
            String orderInfo = orderServiceFuture.join();
            String productInfo = productServiceFuture.join();
            
            log.info("所有服务完成，组合最终结果... 线程: {}", Thread.currentThread().getName());
            return String.format("最终组合结果: [%s] + [%s] + [%s]", userInfo, orderInfo, productInfo);
        });

        // 输出结果
        log.info("thenCombine结果: {}", userOrderCombined.join());
        log.info("allOf组合结果: {}", allCombinedFuture.join());
    }

    /**
     * 异常处理演示
     */
    public void demonstrateExceptionHandling() {
        log.info("\n=== CompletableFuture异常处理演示 ===");

        // 1. handle方法处理异常
        CompletableFuture<String> handleExceptionFuture = CompletableFuture.supplyAsync(() -> {
            log.info("开始执行可能失败的任务... 线程: {}", Thread.currentThread().getName());
            if (random.nextBoolean()) {
                throw new RuntimeException("模拟业务异常");
            }
            return "成功执行";
        }).handle((result, exception) -> {
            if (exception != null) {
                log.error("任务执行失败: {}", exception.getMessage());
                return "默认值: 任务失败时的回退结果";
            } else {
                log.info("任务执行成功: {}", result);
                return result;
            }
        });

        // 2. exceptionally方法处理异常
        CompletableFuture<String> exceptionallyFuture = CompletableFuture.supplyAsync(() -> {
            log.info("执行另一个可能失败的任务... 线程: {}", Thread.currentThread().getName());
            simulateDelay(500);
            if (random.nextInt(10) < 7) { // 70%概率失败
                throw new RuntimeException("网络连接超时");
            }
            return "网络请求成功";
        }).exceptionally(throwable -> {
            log.error("网络请求失败，使用默认值: {}", throwable.getMessage());
            return "缓存数据";
        });

        // 3. 链式操作中的异常传播
        CompletableFuture<String> chainExceptionFuture = CompletableFuture
                .supplyAsync(() -> {
                    log.info("步骤1: 验证参数... 线程: {}", Thread.currentThread().getName());
                    return "valid";
                })
                .thenApply(param -> {
                    log.info("步骤2: 处理参数 {}... 线程: {}", param, Thread.currentThread().getName());
                    if (random.nextBoolean()) {
                        throw new RuntimeException("参数处理失败");
                    }
                    return "processed-" + param;
                })
                .thenApply(processed -> {
                    log.info("步骤3: 保存结果 {}... 线程: {}", processed, Thread.currentThread().getName());
                    return "saved-" + processed;
                })
                .exceptionally(throwable -> {
                    log.error("链式操作中发生异常: {}", throwable.getMessage());
                    return "error-fallback";
                });

        // 输出结果
        log.info("Handle异常处理结果: {}", handleExceptionFuture.join());
        log.info("Exceptionally异常处理结果: {}", exceptionallyFuture.join());
        log.info("链式异常处理结果: {}", chainExceptionFuture.join());
    }

    /**
     * 超时处理演示
     */
    public void demonstrateTimeout() {
        log.info("\n=== CompletableFuture超时处理演示 ===");

        // 创建一个可能超时的任务
        CompletableFuture<String> timeoutTask = CompletableFuture.supplyAsync(() -> {
            log.info("开始执行长时间任务... 线程: {}", Thread.currentThread().getName());
            int sleepTime = 2000 + random.nextInt(4000); // 2-6秒
            log.info("预计执行时间: {} ms", sleepTime);
            simulateDelay(sleepTime);
            return "长时间任务完成";
        });

        // 使用orTimeout设置超时
        CompletableFuture<String> timeoutFuture = timeoutTask
                .orTimeout(3, TimeUnit.SECONDS)
                .exceptionally(throwable -> {
                    if (throwable.getCause() instanceof java.util.concurrent.TimeoutException) {
                        log.warn("任务执行超时，使用默认值");
                        return "超时默认值";
                    } else {
                        log.error("任务执行异常: {}", throwable.getMessage());
                        return "异常默认值";
                    }
                });

        try {
            String result = timeoutFuture.join();
            log.info("超时处理结果: {}", result);
        } catch (CompletionException e) {
            log.error("任务完成异常: {}", e.getMessage());
        }
    }

    /**
     * 并行处理演示
     */
    public void demonstrateParallelProcessing() {
        log.info("\n=== CompletableFuture并行处理演示 ===");

        List<String> urls = Arrays.asList(
                "http://api1.example.com",
                "http://api2.example.com", 
                "http://api3.example.com",
                "http://api4.example.com",
                "http://api5.example.com"
        );

        long startTime = System.currentTimeMillis();

        // 并行处理所有URL
        List<CompletableFuture<String>> futures = urls.stream()
                .map(url -> CompletableFuture.supplyAsync(() -> {
                    log.info("开始请求: {} 线程: {}", url, Thread.currentThread().getName());
                    int responseTime = 1000 + random.nextInt(2000);
                    simulateDelay(responseTime);
                    String response = "Response from " + url + " (took " + responseTime + "ms)";
                    log.info("完成请求: {}", url);
                    return response;
                }, customExecutor))
                .collect(Collectors.toList());

        // 等待所有请求完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        // 收集所有结果
        CompletableFuture<List<String>> allResults = allFutures.thenApply(v ->
                futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList())
        );

        List<String> results = allResults.join();
        long endTime = System.currentTimeMillis();

        log.info("所有并行请求完成，总耗时: {} ms", endTime - startTime);
        results.forEach(result -> log.info("结果: {}", result));
    }

    /**
     * anyOf演示 - 竞速场景
     */
    public void demonstrateAnyOf() {
        log.info("\n=== CompletableFuture.anyOf竞速演示 ===");

        // 创建多个不同速度的任务
        CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
            int delay = 1000 + random.nextInt(2000);
            log.info("任务1开始执行，预计耗时: {} ms", delay);
            simulateDelay(delay);
            return "任务1结果 (耗时: " + delay + "ms)";
        });

        CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
            int delay = 1000 + random.nextInt(2000);
            log.info("任务2开始执行，预计耗时: {} ms", delay);
            simulateDelay(delay);
            return "任务2结果 (耗时: " + delay + "ms)";
        });

        CompletableFuture<String> task3 = CompletableFuture.supplyAsync(() -> {
            int delay = 1000 + random.nextInt(2000);
            log.info("任务3开始执行，预计耗时: {} ms", delay);
            simulateDelay(delay);
            return "任务3结果 (耗时: " + delay + "ms)";
        });

        // 使用anyOf等待第一个完成的任务
        CompletableFuture<Object> firstCompleted = CompletableFuture.anyOf(task1, task2, task3);

        Object winner = firstCompleted.join();
        log.info("🏆 第一个完成的任务结果: {}", winner);

        // 取消其他还在执行的任务
        task1.cancel(true);
        task2.cancel(true);
        task3.cancel(true);
    }

    /**
     * 模拟延迟
     */
    private void simulateDelay(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("任务被中断", e);
        }
    }

    /**
     * 清理资源
     */
    public void cleanup() {
        customExecutor.shutdown();
        try {
            if (!customExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                customExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            customExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("CompletableFuture演示资源清理完成");
    }
}