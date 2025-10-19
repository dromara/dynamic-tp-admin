package org.dromara.dynamictp.admin.controller.manager;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.common.api.Result;
import org.dromara.dynamictp.admin.infrastructure.annotation.RepeatSubmit;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManClientBO;
import org.dromara.dynamictp.admin.modules.manager.facade.IManClientFacade;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 客户端管理
 *
 * @Author eachann
 * @ClassName org.dromara.dynamictp.admin.admin.controller.manager.ManClientController
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@RestController
@Tag(name = "客户端管理")
@RequiredArgsConstructor
@RequestMapping("/man_client")
public class ManClientController {

  @NonNull
  private IManClientFacade iManClientFacade;

  /**
   * 查询客户端
   * 支持：分页查询、详情查询、在线客户端查询、无响应客户端查询
   */
  @GetMapping
  @SaCheckPermission("man:client:query")
  @Operation(operationId = "1", summary = "查询客户端")
  @RepeatSubmit(interval = -1)
  public Result<Object> queryClient(
      @Parameter(description = "查询类型：page-分页查询, detail-详情查询, online-在线客户端, unresponsive-无响应客户端") @RequestParam(defaultValue = "page") String type,
      @Parameter(description = "分页参数") PageQuery pageQuery,
      @Parameter(description = "查询条件") ManClientBO manClientBO,
      @Parameter(description = "客户端ID(详情查询时使用)") @RequestParam(required = false) Long id) {

    log.info("查询客户端，参数：type={}, pageQuery={}, manClientBO={}, id={}", type, pageQuery, manClientBO, id);

    switch (type) {
      case "detail":
        if (id == null) {
          return Result.failure("详情查询需要提供客户端ID");
        }
        return Result.data(iManClientFacade.getManagerClient(id));

      case "online":
        return Result.data(iManClientFacade.getOnlineClients());

      case "unresponsive":
        return Result.data(iManClientFacade.getUnresponsiveClients());

      case "page":
      default:
        return Result.data(iManClientFacade.listManagerClientPage(pageQuery, manClientBO));
    }
  }

  /**
   * 新增客户端
   */
  @PostMapping
  @SaCheckPermission("man:client:add")
  @Operation(operationId = "2", summary = "新增客户端")
  public Result<Boolean> addClient(
      @Parameter(description = "客户端信息") @RequestBody ManClientBO manClientBO) {
    log.info("新增客户端，参数：manClientBO={}", manClientBO);
    return Result.status(iManClientFacade.addManagerClient(manClientBO));
  }

  /**
   * 更新客户端
   * 支持：基本信息更新、状态更新、连接状态更新
   */
  @PutMapping
  @SaCheckPermission("man:client:update")
  @Operation(operationId = "3", summary = "更新客户端")
  @RepeatSubmit(interval = -1)
  public Result<Boolean> updateClient(
      @Parameter(description = "更新类型：info-基本信息, status-状态更新, connect-连接状态") @RequestParam(defaultValue = "info") String action,
      @Parameter(description = "客户端信息") @RequestBody ManClientBO manClientBO,
      @Parameter(description = "客户端名称(状态/连接相关更新时使用)") @RequestParam(required = false) String clientName) {

    log.info("更新客户端，参数：action={}, manClientBO={}, clientName={}", action, manClientBO, clientName);

    switch (action) {
      case "status":
        if (clientName == null) {
          return Result.failure("状态更新需要提供客户端名称");
        }
        // 根据名称查到 clientId 再调用
        var clientForStatus = iManClientFacade.getOnlineClients().stream()
            .filter(c -> clientName.equals(c.getClientName()))
            .findFirst()
            .orElse(null);
        if (clientForStatus == null) {
          return Result.failure("客户端不存在或不在线");
        }
        return Result.status(iManClientFacade.checkClientStatus(clientForStatus.getServiceName()));

      case "connect":
        if (clientName == null) {
          return Result.failure("连接状态更新需要提供客户端名称");
        }
        var clientForConnect = iManClientFacade.getOnlineClients().stream()
            .filter(c -> clientName.equals(c.getClientName()))
            .findFirst()
            .orElse(null);
        if (clientForConnect == null) {
          return Result.failure("客户端不存在或不在线");
        }
        return Result.status(iManClientFacade.updateConnectTime(clientForConnect.getClientName()));

      case "disconnect":
        if (clientName == null) {
          return Result.failure("断开状态更新需要提供客户端名称");
        }
        var clientForDisconnect = iManClientFacade.getOnlineClients().stream()
            .filter(c -> clientName.equals(c.getClientName()))
            .findFirst()
            .orElse(null);
        if (clientForDisconnect == null) {
          return Result.failure("客户端不存在或不在线");
        }
        return Result.status(iManClientFacade.updateDisconnectTime(clientForDisconnect.getClientName()));

      case "heartbeat":
        if (clientName == null) {
          return Result.failure("心跳更新需要提供客户端名称");
        }
        var clientForHeartbeat = iManClientFacade.getOnlineClients().stream()
            .filter(c -> clientName.equals(c.getClientName()))
            .findFirst()
            .orElse(null);
        if (clientForHeartbeat == null) {
          return Result.failure("客户端不存在或不在线");
        }
        return Result.status(iManClientFacade.updateHeartbeatTime(clientForHeartbeat.getClientName()));

      case "offline":
        if (clientName == null) {
          return Result.failure("离线标记需要提供客户端名称");
        }
        var clientForOffline = iManClientFacade.getOnlineClients().stream()
            .filter(c -> clientName.equals(c.getClientName()))
            .findFirst()
            .orElse(null);
        if (clientForOffline == null) {
          return Result.failure("客户端不存在或不在线");
        }
        return Result.status(iManClientFacade.markClientAsOffline(clientForOffline.getClientName()));

      case "info":
      default:
        return Result.status(iManClientFacade.updateManagerClient(manClientBO));
    }
  }

  /**
   * 删除客户端
   */
  @DeleteMapping("/{ids}")
  @SaCheckPermission("man:client:delete")
  @Operation(operationId = "4", summary = "删除客户端")
  public Result<Boolean> deleteClient(
      @Parameter(description = "主键ID集合") @PathVariable List<Long> ids) {
    log.info("删除客户端，参数：ids={}", ids);
    return Result.status(iManClientFacade.deleteManagerClient(ids));
  }

  /**
   * 处理客户端连接
   * 当客户端连接时，首先检查数据库是否有相关数据
   * 若没有则新增客户端数据，若有则更新需要更新的字段
   */
  @PostMapping("/connection")
  @SaCheckPermission("man:client:connection")
  @Operation(operationId = "5", summary = "处理客户端连接")
  @RepeatSubmit(interval = -1)
  public Result<Boolean> handleClientConnection(
      @Parameter(description = "客户端连接信息") @RequestBody ManClientBO manClientBO) {

    log.info("处理客户端连接，参数：manClientBO={}", manClientBO);

    return Result.status(iManClientFacade.handleClientConnection(
        manClientBO.getClientId(),
        manClientBO.getClientName(),
        manClientBO.getServiceName(),
        manClientBO.getClientIp(),
        manClientBO.getClientPort(),
        manClientBO.getServerIp(),
        manClientBO.getServerPort()));
  }
}