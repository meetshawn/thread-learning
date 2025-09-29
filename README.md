# Thread Learning

一个基于 Spring Boot 的线程池学习项目，演示如何配置和使用自定义线程池。

## 技术栈

- Spring Boot 3.2.0
- Java 17
- Maven
- Lombok

## 功能特性

- 🎯 自定义线程池配置
- 🔄 异步任务执行
- 📊 多种线程池类型（通用、IO密集型）
- ⚙️ 可配置的线程池参数
- 📝 详细的执行日志
- 🧠 Java内存模型（JMM）演示
- 🔒 可见性、原子性、有序性测试
- 🔄 volatile、synchronized、原子类对比
- 🏤 生产者消费者模式（wait/notify、Lock、BlockingQueue）
- 📊 ConcurrentHashMap vs HashMap 并发安全性对比
- 🔧 同步工具类应用场景（CountDownLatch、CyclicBarrier、Semaphore）
- 🚀 CompletableFuture异步编程（组合、异常处理、超时）
- 💾 ThreadLocal使用与内存泄漏防范
- 🔐 锁升级过程演示（偏向锁→轻量级锁→重量级锁）
- 📊 自定义拒绝策略和线程池监控

## 快速开始

### 启动应用
```bash
mvn spring-boot:run
```

### 测试接口

应用启动后，可以通过以下API测试功能：

#### 线程池功能测试
- **默认异步任务**: `GET http://localhost:8081/thread-pool/test-default`
- **自定义线程池**: `GET http://localhost:8081/thread-pool/test-custom`
- **IO线程池**: `GET http://localhost:8081/thread-pool/test-io`
- **多任务测试**: `GET http://localhost:8081/thread-pool/test-multiple`

#### Java内存模型（JMM）演示
- **普通变量可见性**: `GET http://localhost:8081/jmm/normal-visibility`
- **volatile可见性**: `GET http://localhost:8081/jmm/volatile-visibility`
- **synchronized保证**: `GET http://localhost:8081/jmm/synchronized-visibility`
- **原子类保证**: `GET http://localhost:8081/jmm/atomic-visibility`
- **指令重排序**: `GET http://localhost:8081/jmm/reordering`
- **所有JMM演示**: `GET http://localhost:8081/jmm/all`

#### 生产者消费者模式演示
- **wait/notify模式**: `GET http://localhost:8081/producer-consumer/wait-notify`
- **Lock/Condition模式**: `GET http://localhost:8081/producer-consumer/lock-condition`
- **ArrayBlockingQueue**: `GET http://localhost:8081/producer-consumer/array-blocking-queue`
- **LinkedBlockingQueue**: `GET http://localhost:8081/producer-consumer/linked-blocking-queue`
- **SynchronousQueue**: `GET http://localhost:8081/producer-consumer/synchronous-queue`
- **多生产者消费者**: `GET http://localhost:8081/producer-consumer/multiple`
- **所有模式演示**: `GET http://localhost:8081/producer-consumer/all`

#### 并发集合安全性对比
- **HashMap线程不安全**: `GET http://localhost:8081/concurrent-map/hashmap-unsafe`
- **ConcurrentHashMap安全**: `GET http://localhost:8081/concurrent-map/concurrenthashmap-safe`
- **并发修改异常**: `GET http://localhost:8081/concurrent-map/concurrent-modification-exception`
- **性能对比测试**: `GET http://localhost:8081/concurrent-map/performance-comparison`
- **原子操作演示**: `GET http://localhost:8081/concurrent-map/atomic-operations`
- **所有对比演示**: `GET http://localhost:8081/concurrent-map/all`

#### 同步工具类应用场景
- **CountDownLatch**: `GET http://localhost:8081/sync-tools/countdown-latch`
- **CyclicBarrier**: `GET http://localhost:8081/sync-tools/cyclic-barrier`
- **Semaphore连接池**: `GET http://localhost:8081/sync-tools/semaphore-pool`
- **Semaphore限流**: `GET http://localhost:8081/sync-tools/semaphore-ratelimit`
- **组合使用**: `GET http://localhost:8081/sync-tools/combined-usage`
- **所有工具演示**: `GET http://localhost:8081/sync-tools/all`

#### CompletableFuture异步编程
- **基础使用**: `GET http://localhost:8081/completable-future/basic-usage`
- **链式操作**: `GET http://localhost:8081/completable-future/chaining`
- **组合操作**: `GET http://localhost:8081/completable-future/combining`
- **异常处理**: `GET http://localhost:8081/completable-future/exception-handling`
- **超时处理**: `GET http://localhost:8081/completable-future/timeout`
- **并行处理**: `GET http://localhost:8081/completable-future/parallel-processing`
- **竞速模式**: `GET http://localhost:8081/completable-future/any-of`
- **所有演示**: `GET http://localhost:8081/completable-future/all`

#### ThreadLocal使用与防泄漏
- **基础使用**: `GET http://localhost:8081/thread-local/basic-usage`
- **线程池中使用**: `GET http://localhost:8081/thread-local/thread-pool-usage`
- **内存泄漏演示**: `GET http://localhost:8081/thread-local/memory-leak`
- **可继承版本**: `GET http://localhost:8081/thread-local/inheritable`
- **DateFormat安全**: `GET http://localhost:8081/thread-local/dateformat-safety`
- **性能对比**: `GET http://localhost:8081/thread-local/performance-comparison`
- **所有演示**: `GET http://localhost:8081/thread-local/all`

#### 高级并发特性
- **锁升级过程**: `GET http://localhost:8081/advanced/lock-upgrade`
- **锁性能对比**: `GET http://localhost:8081/advanced/lock-performance`
- **读写锁优势**: `GET http://localhost:8081/advanced/read-write-lock`
- **可重入锁**: `GET http://localhost:8081/advanced/reentrant-lock`
- **自定义拒绝策略**: `GET http://localhost:8081/advanced/custom-rejection`
- **线程池监控**: `GET http://localhost:8081/advanced/thread-pool-monitoring`
- **所有高级演示**: `GET http://localhost:8081/advanced/all`

## 项目结构

```
src/main/java/com/example/threadlearning/
├── config/
│   └── ThreadPoolConfig.java      # 线程池配置
├── controller/
│   ├── ThreadPoolController.java  # 线程池REST API接口
│   ├── JmmController.java         # JMM演示API接口
│   ├── ProducerConsumerController.java # 生产者消费者API接口
│   ├── ConcurrentMapController.java    # 并发集合API接口
│   ├── SyncToolsController.java   # 同步工具API接口
│   ├── CompletableFutureController.java # 异步编程API接口
│   ├── ThreadLocalController.java # ThreadLocalAPI接口
│   └── AdvancedConcurrencyController.java # 高级并发API接口
├── service/
│   └── AsyncService.java          # 异步服务
├── jmm/
│   └── MemoryVisibilityDemo.java  # JMM内存可见性演示
├── pattern/
│   ├── WaitNotifyProducerConsumer.java # wait/notify生产者消费者
│   └── BlockingQueueProducerConsumer.java # 阻塞队列生产者消费者
├── concurrent/
│   └── ConcurrentMapDemo.java     # 并发集合对比演示
├── sync/
│   └── SynchronizationToolsDemo.java # 同步工具类演示
├── async/
│   └── CompletableFutureDemo.java # CompletableFuture演示
├── threadlocal/
│   └── ThreadLocalDemo.java       # ThreadLocal演示
├── lock/
│   └── LockUpgradeDemo.java       # 锁升级演示
├── monitor/
│   └── ThreadPoolMonitorDemo.java # 线程池监控演示
└── ThreadLearningApplication.java # 启动类
```

## 配置说明

所有线程池参数均可在 `application.properties` 中配置：

- `thread.pool.custom.*` - 自定义线程池配置
- `thread.pool.io.*` - IO线程池配置  
- `thread.pool.default.*` - 默认线程池配置

## 日志查看

应用运行时会输出详细的线程执行日志，可以观察不同线程池的工作情况。