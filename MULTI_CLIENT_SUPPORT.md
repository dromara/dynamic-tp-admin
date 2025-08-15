# 多客户端支持功能说明

## 概述

本次更新解决了 `AdminServerUserProcessor` 在多客户端连接时的问题，现在支持同时管理多个客户端连接，并提供更好的并发处理能力。

## 主要改进

### 1. 多客户端连接管理

**之前的问题**：

- 只能保存最后一个连接的客户端地址
- 新客户端连接会覆盖之前的客户端信息
- 无法向所有客户端发送请求

**现在的解决方案**：

- 使用 `ConcurrentHashMap.newKeySet()` 管理多个客户端连接
- 支持同时连接多个客户端
- 提供客户端连接状态监控

### 2. 并发处理能力提升

**之前的问题**：

- 使用单线程执行器 `Executors.newSingleThreadExecutor()`
- 请求串行处理，性能瓶颈
- 长请求会阻塞其他客户端

**现在的解决方案**：

- 使用 `ThreadPoolExecutor` 支持并发处理
- 核心线程数：2，最大线程数：10
- 支持最多 100 个并发请求队列
- 使用 `CallerRunsPolicy` 拒绝策略

### 3. 连接生命周期管理

**新增功能**：

- 自动检测客户端连接和断开
- 维护活跃客户端列表
- 提供连接状态查询接口

## API 使用说明

### 1. 向后兼容的 API

```java
// 向第一个可用的客户端发送请求（原有功能）
Object result = adminServer.requestToClient(AdminRequestTypeEnum.EXECUTOR_MONITOR, null);
```

### 2. 新增的多客户端 API

```java
// 获取所有已连接的客户端
Set<String> clients = adminServer.getConnectedClients();
System.out.println("Connected clients: " + clients);

// 获取客户端数量
int count = adminServer.getConnectedClientCount();
System.out.println("Client count: " + count);

// 向指定客户端发送请求
Object result = adminServer.requestToSpecificClient("192.168.1.100:8080",
    AdminRequestTypeEnum.EXECUTOR_MONITOR, null);

// 向所有客户端广播请求
List<Object> results = adminServer.broadcastToAllClients(
    AdminRequestTypeEnum.EXECUTOR_MONITOR, null);
```

### 3. 处理器级别的 API

```java
// 获取处理器实例
AdminServerUserProcessor processor = adminServer.getAdminServerUserProcessor();

// 获取连接的客户端
Set<String> clients = processor.getConnectedClients();

// 获取第一个可用客户端（向后兼容）
String firstClient = processor.getFirstAvailableClient();

// 手动移除客户端连接
processor.removeClientConnection("测试客户端-001");
```

## 配置说明

### 线程池配置

```java
// 在 AdminServerUserProcessor 构造函数中配置
this.executor = new ThreadPoolExecutor(
    2,                    // 核心线程数
    10,                   // 最大线程数
    60L,                  // 空闲线程存活时间（秒）
    TimeUnit.SECONDS,
    new LinkedBlockingQueue<>(100),  // 工作队列大小
    r -> {
        Thread t = new Thread(r, "AdminServerProcessor-" + threadCounter.getAndIncrement());
        t.setDaemon(true);
        return t;
    },
    new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
);
```

### 超时配置

```java
// 广播请求超时时间：30秒
allFutures.get(30, TimeUnit.SECONDS);

// 单个结果获取超时：1秒
Object result = future.get(1, TimeUnit.SECONDS);

// RPC调用超时：30秒
server.invokeSync(clientAddress, requestBody, 30000);
```

## 监控和日志

### 连接事件日志

```
INFO  - Client connected: 测试客户端-001, total connected clients: 1
INFO  - Client connected: 测试客户端-002, total connected clients: 2
INFO  - Client disconnected: 测试客户端-001, remaining clients: 1
```

### 请求处理日志

```
INFO  - DynamicTp admin request received:EXECUTOR_MONITOR from client: 测试客户端-001
INFO  - Broadcasting request to 2 clients: [测试客户端-001, 测试客户端-002]
DEBUG - Sending request to specific client: 测试客户端-001
```

## 最佳实践

### 1. 错误处理

```java
try {
    List<Object> results = adminServer.broadcastToAllClients(
        AdminRequestTypeEnum.EXECUTOR_MONITOR, null);

    // 处理结果，过滤掉null值
    List<Object> validResults = results.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

} catch (Exception e) {
    log.error("Failed to broadcast request", e);
}
```

### 2. 连接状态检查

```java
// 发送请求前检查连接状态
if (adminServer.getConnectedClientCount() == 0) {
    log.warn("No clients connected, skipping request");
    return;
}
```

### 3. 资源清理

```java
// 应用关闭时清理资源
@PreDestroy
public void cleanup() {
    adminServer.shutdown();
}
```

## 性能考虑

1. **并发处理**：支持最多 10 个并发请求处理线程
2. **队列缓冲**：最多 100 个请求在队列中等待
3. **超时控制**：避免长时间等待无响应的客户端
4. **内存管理**：使用线程安全的集合，避免内存泄漏

## 注意事项

1. **向后兼容**：原有的 `requestToClient` 方法仍然可用
2. **线程安全**：所有新增的集合操作都是线程安全的
3. **异常处理**：广播请求中的单个客户端失败不会影响其他客户端
4. **资源管理**：记得在应用关闭时调用 `shutdown()` 方法
