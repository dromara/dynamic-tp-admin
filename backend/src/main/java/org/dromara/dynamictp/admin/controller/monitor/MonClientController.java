package org.dromara.dynamictp.admin.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.common.api.Result;
import org.dromara.dynamictp.admin.infrastructure.annotation.RepeatSubmit;
import org.dromara.dynamictp.admin.infrastructure.server.AdminServer;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManClient;
import org.dromara.dynamictp.admin.modules.manager.facade.IManClientFacade;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.InetAddress;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 客户端连接监控
 *
 * @Author eachann
 * @ProjectName dynamictp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.monitor.MonClientController
 * @CreateTime 2025/07/29 - 10:00
 */
@Slf4j
@RestController
@Tag(name = "客户端连接监控")
@RequiredArgsConstructor
@RequestMapping("/")
public class MonClientController {

  @NonNull
  private AdminServer adminServer;

  @NonNull
  private IManClientFacade iManClientFacade;

  @Value("${dynamictp.adminPort:8989}")
  private Integer serverPort;

  @GetMapping("/clients")
  @SaCheckPermission("mon:client:list")
  @Operation(operationId = "1", summary = "获取客户端列表")
  @RepeatSubmit(interval = -1)
  public Result<List<Map<String, Object>>> getClients() {
    log.info("获取客户端列表");
    Set<String> connectedClientAddresses = adminServer.getConnectedClientAddresses();
    List<Map<String, Object>> clients = new ArrayList<>();

    // 获取数据库中所有客户端信息
    List<ManClient> allClients = iManClientFacade.getOnlineClients();

    // 创建当前连接客户端名称集合（从 clientId 映射为 clientServiceName）用于快速查找
    Set<String> connectedClientNames = connectedClientAddresses.stream()
        .map(clientAddress -> adminServer.getClientServiceName(clientAddress))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    // 处理当前连接的客户端
    for (String clientAddress : connectedClientAddresses) {
      Map<String, Object> client = new HashMap<>();

      // 解析客户端地址
      String[] parts = clientAddress.split(":");
      String clientIp = parts.length > 0 ? parts[0] : "unknown";
      int clientPort = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

      String clientName = adminServer.getAttribute(clientAddress, "clientName");
      String serviceName = adminServer.getAttribute(clientAddress, "serviceName");
      String clientServiceName = adminServer.getClientServiceName(clientAddress);

      // 构建客户端信息 - 基于真实连接状态
      client.put("clientId", clientAddress);
      client.put("clientName", clientName);
      client.put("serviceName", serviceName);
      client.put("clientServiceName", clientServiceName); // 添加完整的客户端服务名称
      client.put("clientIp", clientIp);
      client.put("clientPort", clientPort);
      // 客户端在connectedClients中表示真实在线状态
      client.put("status", "online");
      client.put("lastHeartbeat", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      client.put("registerTime", LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      client.put("applicationName", extractServiceName(serviceName));

      clients.add(client);

      // 调用IManClientFacade处理客户端连接，添加或更新客户端数据
      saveConnectedClient(clientAddress, clientName, serviceName, clientIp, clientPort);
    }

    // 处理数据库中存储但当前未连接的客户端（离线状态）
    for (ManClient dbClient : allClients) {
      String dbClientClientName = dbClient.getServiceName();
      // 如果数据库中的客户端不在当前连接列表中，则标记为离线
      if (!connectedClientNames.contains(dbClientClientName)) {
        Map<String, Object> client = new HashMap<>();

        client.put("clientId", dbClient.getClientId());
        client.put("clientName", dbClient.getClientName());
        client.put("serviceName", dbClient.getServiceName());
        client.put("clientServiceName", dbClient.getServiceName()); // 添加完整的客户端服务名称
        client.put("clientIp", dbClient.getClientIp());
        client.put("clientPort", dbClient.getClientPort());
        client.put("status", "offline");
        client.put("lastHeartbeat",
            dbClient.getLastHeartbeatTime() != null
                ? dbClient.getLastHeartbeatTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        client.put("registerTime",
            dbClient.getCreateTime() != null ? dbClient.getCreateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        client.put("applicationName", extractServiceName(dbClient.getServiceName()));

        clients.add(client);
      }
    }

    return Result.data(clients);
  }

  @GetMapping("/mon_client/count")
  @SaCheckPermission("mon:client:count")
  @Operation(operationId = "2", summary = "获取连接的客户端数量")
  @RepeatSubmit(interval = -1)
  public Result<Integer> getConnectedClientCount() {
    log.info("获取连接的客户端数量");
    int clientCount = adminServer.getConnectedClientCount();
    return Result.data(clientCount);
  }

  @GetMapping("/mon_client/list")
  @SaCheckPermission("mon:client:list")
  @Operation(operationId = "3", summary = "获取连接的客户端列表")
  @RepeatSubmit(interval = -1)
  public Result<Set<String>> getConnectedClients() {
    log.info("获取连接的客户端列表");
    Set<String> connectedClients = adminServer.getConnectedClients();
    return Result.data(connectedClients);
  }

  @GetMapping("/mon_client/info")
  @SaCheckPermission("mon:client:info")
  @Operation(operationId = "4", summary = "获取客户端连接详细信息")
  @RepeatSubmit(interval = -1)
  public Result<Map<String, Object>> getClientInfo() {
    log.info("获取客户端连接详细信息");
    Map<String, Object> result = new HashMap<>();

    Set<String> connectedClients = adminServer.getConnectedClients();
    int clientCount = adminServer.getConnectedClientCount();

    result.put("clientCount", clientCount);
    result.put("connectedClients", connectedClients);
    result.put("timestamp", System.currentTimeMillis());

    return Result.data(result);
  }

  @GetMapping("/services")
  @SaCheckPermission("mon:service:list")
  @Operation(operationId = "5", summary = "获取服务列表（按服务名分组）")
  @RepeatSubmit(interval = -1)
  public Result<List<Map<String, Object>>> getServices() {
    Set<String> connectedClientAddresses = adminServer.getConnectedClientAddresses();
    List<Map<String, Object>> services = new ArrayList<>();

    // 按服务名分组
    Map<String, List<Map<String, Object>>> serviceGroups = new HashMap<>();

    // 处理当前连接的客户
    for (String clientAddress : connectedClientAddresses) {
      String clientName = adminServer.getAttribute(clientAddress, "clientName");
      String serviceName = adminServer.getAttribute(clientAddress, "serviceName");
      if (clientName == null || serviceName.trim().isEmpty()) {
        continue;
      }

      // 解析客户端地址
      String[] parts = clientAddress.split(":");
      String clientIp = parts.length > 0 ? parts[0] : "unknown";
      int clientPort = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

      Map<String, Object> instance = new HashMap<>();
      instance.put("clientId", clientAddress);
      instance.put("clientServiceName", adminServer.getClientServiceName(clientAddress)); // 添加完整的客户端服务名称
      instance.put("clientIp", clientIp);
      instance.put("clientPort", clientPort);
      instance.put("status", "online");
      instance.put("lastHeartbeat", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      instance.put("applicationName", extractServiceName(serviceName));

      serviceGroups.computeIfAbsent(clientName, k -> new ArrayList<>()).add(instance);
      // 调用IManClientFacade处理客户端连接，添加或更新客户端数据
      saveConnectedClient(clientAddress, clientName, serviceName, clientIp, clientPort);
    }

    // 处理数据库中的离线客户端
    List<ManClient> allClients = iManClientFacade.getOnlineClients();
    Set<String> connectedClientServiceNames = connectedClientAddresses.stream()
        .map(clientAddress -> adminServer.getClientServiceName(clientAddress))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    for (ManClient dbClient : allClients) {
      String dbClientServiceName = dbClient.getServiceName();

      if (!connectedClientServiceNames.contains(dbClientServiceName)) {
        Map<String, Object> instance = new HashMap<>();
        instance.put("clientId", dbClient.getClientId());
        instance.put("clientServiceName", dbClient.getServiceName()); // 添加完整的客户端服务名称
        instance.put("clientIp", dbClient.getClientIp());
        instance.put("clientPort", dbClient.getClientPort());
        instance.put("status", Boolean.parseBoolean(dbClient.getStatus()) ? "online" : "offline");
        instance.put("lastHeartbeat", dbClient.getLastHeartbeatTime() != null
            ? dbClient.getLastHeartbeatTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            : LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        // 新增 applicationName 字段（离线）
        instance.put("applicationName", extractServiceName(dbClient.getServiceName()));

        serviceGroups.computeIfAbsent(dbClient.getClientName(), k -> new ArrayList<>()).add(instance);
      }
    }

    // 构建服务列表
    for (Map.Entry<String, List<Map<String, Object>>> entry : serviceGroups.entrySet()) {
      Map<String, Object> service = new HashMap<>();
      service.put("serviceName", extractServiceName(entry.getKey()));
      service.put("instanceCount", entry.getValue().size());
      service.put("onlineCount", entry.getValue().stream()
          .mapToInt(instance -> "online".equals(instance.get("status")) ? 1 : 0)
          .sum());
      service.put("offlineCount", entry.getValue().size() -
          entry.getValue().stream()
              .mapToInt(instance -> "online".equals(instance.get("status")) ? 1 : 0)
              .sum());
      service.put("instances", entry.getValue());
      service.put("status", entry.getValue().stream()
          .anyMatch(instance -> "online".equals(instance.get("status"))) ? "online" : "offline");

      services.add(service);
    }

    return Result.data(services);
  }

  private void saveConnectedClient(String clientAddress, String clientName, String serviceName, String clientIp,
      int clientPort) {
    try {
      String clientId = clientAddress;
      String serverIp = String.valueOf(InetAddress.getLocalHost());

      boolean result = iManClientFacade.handleClientConnection(
          clientId, clientName, serviceName, clientIp, clientPort, serverIp, serverPort);

      if (result) {
        log.info("客户端数据添加或更新成功: {}", clientId);
      } else {
        log.warn("客户端数据添加或更新失败: {}", clientId);
      }
    } catch (Exception e) {
      log.error("处理客户端连接时发生异常: {}", e.getMessage(), e);
    }
  }

  @GetMapping("/services/{clientName}/instances")
  @SaCheckPermission("mon:service:instances")
  @Operation(operationId = "6", summary = "获取指定服务的实例列表")
  @RepeatSubmit(interval = -1)
  public Result<List<Map<String, Object>>> getServiceInstances(@PathVariable String clientName) {
    log.info("获取服务实例列表，服务名：{}", clientName);
    Set<String> connectedClientAddresses = adminServer.getConnectedClientAddresses();
    List<Map<String, Object>> instances = new ArrayList<>();

    // 处理当前连接的客户端
    for (String clientAddress : connectedClientAddresses) {
      String clientServiceName = adminServer.getClientServiceName(clientAddress);
      String extractClientName = extractClientName(clientServiceName);
      if (!clientName.equals(extractClientName)) {
        continue;
      }

      // 解析客户端地址
      String[] parts = clientAddress.split(":");
      String clientIp = parts.length > 0 ? parts[0] : "unknown";
      int clientPort = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

      Map<String, Object> instance = new HashMap<>();
      instance.put("clientId", clientAddress);
      instance.put("clientServiceName", clientServiceName); // 添加完整的客户端服务名称
      instance.put("clientIp", clientIp);
      instance.put("clientPort", clientPort);
      instance.put("status", "online");
      instance.put("lastHeartbeat", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      instance.put("registerTime", LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      instance.put("applicationName", extractServiceName(clientServiceName));

      instances.add(instance);
    }

    // 处理数据库中的离线客户端
    List<ManClient> allClients = iManClientFacade.getOnlineClients();
    Set<String> connectedClientNames = connectedClientAddresses.stream()
        .map(clientAddress -> adminServer.getClientServiceName(clientAddress))
        .filter(Objects::nonNull)
        .collect(Collectors.toSet());

    for (ManClient dbClient : allClients) {
      String dbServiceName = dbClient.getServiceName();
      String dbClientName = dbClient.getClientName();
      if (clientName.equals(dbClientName) && !connectedClientNames.contains(dbServiceName)) {
        Map<String, Object> instance = new HashMap<>();
        instance.put("clientId", dbClient.getClientId());
        instance.put("clientServiceName", dbClient.getServiceName()); // 添加完整的客户端服务名称
        instance.put("clientIp", dbClient.getClientIp());
        instance.put("clientPort", dbClient.getClientPort());
        instance.put("status", "offline");
        instance.put("lastHeartbeat", dbClient.getLastHeartbeatTime() != null
            ? dbClient.getLastHeartbeatTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            : LocalDateTime.now().minusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        instance.put("registerTime", dbClient.getCreateTime() != null
            ? dbClient.getCreateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
            : LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        instance.put("applicationName", extractServiceName(dbClient.getServiceName()));

        instances.add(instance);
      }
    }

    return Result.data(instances);
  }

  private String extractClientName(String clientServiceName) {
    if (clientServiceName == null || clientServiceName.trim().isEmpty()) {
      return "unknown";
    }

    int colonIndex = clientServiceName.indexOf(":");
    if (colonIndex > 0) {
      return clientServiceName.substring(0, colonIndex);
    }

    return clientServiceName;
  }

  /**
   * 从完整服务名称中提取服务名称部分
   *
   * @param fullServiceName 完整的服务名称
   * @return 服务名称部分，如果没有冒号则返回原字符串
   */
  private String extractServiceName(String fullServiceName) {
    if (fullServiceName == null || fullServiceName.trim().isEmpty()) {
      return "unknown";
    }

    int colonIndex = fullServiceName.indexOf(":");
    if (colonIndex > 0 && colonIndex < fullServiceName.length() - 1) {
      return fullServiceName.substring(colonIndex + 1);
    }

    return fullServiceName;
  }

}