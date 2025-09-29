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

## 快速开始

### 启动应用
```bash
mvn spring-boot:run
```

### 测试接口

应用启动后，可以通过以下API测试线程池功能：

- **默认异步任务**: `GET http://localhost:8081/thread-pool/test-default`
- **自定义线程池**: `GET http://localhost:8081/thread-pool/test-custom`
- **IO线程池**: `GET http://localhost:8081/thread-pool/test-io`
- **多任务测试**: `GET http://localhost:8081/thread-pool/test-multiple`

## 项目结构

```
src/main/java/com/example/threadlearning/
├── config/
│   └── ThreadPoolConfig.java      # 线程池配置
├── controller/
│   └── ThreadPoolController.java  # REST API接口
├── service/
│   └── AsyncService.java          # 异步服务
└── ThreadLearningApplication.java # 启动类
```

## 配置说明

所有线程池参数均可在 `application.properties` 中配置：

- `thread.pool.custom.*` - 自定义线程池配置
- `thread.pool.io.*` - IO线程池配置  
- `thread.pool.default.*` - 默认线程池配置

## 日志查看

应用运行时会输出详细的线程执行日志，可以观察不同线程池的工作情况。