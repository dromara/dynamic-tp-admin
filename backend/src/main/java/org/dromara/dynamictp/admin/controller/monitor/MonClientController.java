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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
 * @ProjectName panis-boot
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

    // 创建当前连接客户端名称集合（从 clientId 映射为 clientName）用于快速查找
    Set<String> connectedClientNames = connectedClientAddresses.stream()
        .map(clientAddress -> adminServer.getClientName(clientAddress))
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
      // 构建客户端信息 - 基于真实连接状态
      client.put("clientId", clientAddress);
      client.put("clientName", clientName);
      client.put("clientIp", clientIp);
      client.put("clientPort", clientPort);
      // 客户端在connectedClients中表示真实在线状态
      client.put("status", "online");
      client.put("lastHeartbeat", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      client.put("registerTime", LocalDateTime.now().minusHours(1).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
      client.put("applicationName", "panis-boot");
      client.put("environment", "dev");
      client.put("version", "1.0.0");

      clients.add(client);

      // 调用IManClientFacade处理客户端连接，添加或更新客户端数据
      try {
        String clientId = clientAddress;
        String clientType = "web";
        String clientVersion = "1.0.0";
        String serverIp = "127.0.0.1";
        Integer serverPort = 8989;

        boolean result = iManClientFacade.handleClientConnection(
            clientId, clientName, clientType, clientVersion,
            clientIp, clientPort, serverIp, serverPort);

        if (result) {
          log.info("客户端数据添加或更新成功: {}", clientId);
        } else {
          log.warn("客户端数据添加或更新失败: {}", clientId);
        }
      } catch (Exception e) {
        log.error("处理客户端连接时发生异常: {}", e.getMessage(), e);
      }
    }

    // 处理数据库中存储但当前未连接的客户端（离线状态）
    for (ManClient dbClient : allClients) {
      String dbClientClientName = dbClient.getClientName();
      // 如果数据库中的客户端不在当前连接列表中，则标记为离线
      if (!connectedClientNames.contains(dbClientClientName)) {
        Map<String, Object> client = new HashMap<>();

        client.put("clientId", dbClient.getClientId());
        client.put("clientName", dbClientClientName);
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
        client.put("applicationName", "panis-boot");
        client.put("environment", "dev");
        client.put("version", dbClient.getClientVersion() != null ? dbClient.getClientVersion() : "1.0.0");

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
}