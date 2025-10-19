package org.dromara.dynamictp.admin.controller.manager;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.common.api.Result;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.server.AdminServer;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManNotifyPlatformBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyPlatform;
import org.dromara.dynamictp.admin.modules.manager.facade.IManNotifyPlatformFacade;
import org.dromara.dynamictp.admin.modules.manager.service.IManClientService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 告警渠道管理
 *
 * @Author eachann
 * @ClassName org.dromara.dynamictp.admin.admin.controller.manager.ManNotifyPlatformController
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@RestController
@Tag(name = "告警渠道管理")
@RequiredArgsConstructor
@RequestMapping("/man_notify_platform")
public class ManNotifyPlatformController {

  @NonNull
  private IManNotifyPlatformFacade iManNotifyPlatformFacade;

  @NonNull
  private AdminServer adminServer;

  @NonNull
  private IManClientService manClientService;

  @GetMapping("/page")
  @SaCheckPermission("man:notify_platform:page")
  @Operation(operationId = "1", summary = "分页获取告警渠道列表")
  public Result<IPage<ManNotifyPlatform>> getNotifyPlatformPage(
      @Parameter(description = "分页参数") PageQuery pageQuery,
      @Parameter(description = "查询条件") ManNotifyPlatformBO manNotifyPlatformBO) {
    log.info("分页获取告警渠道列表，参数：pageQuery={}, manNotifyPlatformBO={}", pageQuery, manNotifyPlatformBO);
    return Result
        .data(iManNotifyPlatformFacade.listManagerNotifyPlatformPage(pageQuery, manNotifyPlatformBO));
  }

  @GetMapping("/by-client/{clientServiceName}/page")
  @SaCheckPermission("man:notify_platform:page")
  @Operation(operationId = "1.1", summary = "按客户端分页获取告警渠道列表")
  public Result<IPage<ManNotifyPlatform>> getNotifyPlatformPageByClient(
      @Parameter(description = "客户端服务名称") @PathVariable String clientServiceName,
      @Parameter(description = "分页参数") PageQuery pageQuery,
      @Parameter(description = "查询条件") ManNotifyPlatformBO manNotifyPlatformBO) {
    log.info("按客户端分页获取告警渠道列表，参数：clientServiceName={}, pageQuery={}, manNotifyPlatformBO={}", clientServiceName,
        pageQuery,
        manNotifyPlatformBO);

    // 验证客户端是否存在且在线（通过 clientServiceName 获取）
    var client = manClientService.getByClientName(clientServiceName);
    if (client == null) {
      return Result.failure("客户端不存在");
    }
    if (!client.getIsOnline()) {
      return Result.failure("客户端已离线");
    }

    // 设置客户端名称到查询条件中
    if (manNotifyPlatformBO == null) {
      manNotifyPlatformBO = new ManNotifyPlatformBO();
    }
    manNotifyPlatformBO.setClientName(client.getServiceName());

    return Result
        .data(iManNotifyPlatformFacade.listManagerNotifyPlatformPage(pageQuery, manNotifyPlatformBO));
  }

  @PostMapping
  @SaCheckPermission("man:notify_platform:add")
  @Operation(operationId = "2", summary = "新增告警渠道")
  public Result<Boolean> addNotifyPlatform(
      @Parameter(description = "告警渠道信息") @RequestBody ManNotifyPlatformBO manNotifyPlatformBO) {
    log.info("新增告警渠道，参数：manNotifyPlatformBO={}", manNotifyPlatformBO);

    // 如携带 clientId，仅进行状态检查，避免直接查询方法链依赖
    String clientName = manNotifyPlatformBO.getClientName();
    if (clientName != null) {
      Boolean ok = manClientService.checkClientStatus(clientName);
      if (!ok) {
        return Result.failure("客户端不可用，无法新增告警渠道配置");
      }
    }

    return Result.status(iManNotifyPlatformFacade.addManagerNotifyPlatform(manNotifyPlatformBO));
  }

  @PutMapping
  @SaCheckPermission("man:notify_platform:update")
  @Operation(operationId = "3", summary = "修改告警渠道")
  public Result<Boolean> updateNotifyPlatform(
      @Parameter(description = "告警渠道信息") @RequestBody ManNotifyPlatformBO manNotifyPlatformBO) {
    log.info("修改告警渠道，参数：manNotifyPlatformBO={}", manNotifyPlatformBO);
    return Result.status(iManNotifyPlatformFacade.updateManagerNotifyPlatform(manNotifyPlatformBO));
  }

  @DeleteMapping("/{ids}")
  @SaCheckPermission("man:notify_platform:delete")
  @Operation(operationId = "4", summary = "删除告警渠道")
  public Result<Boolean> deleteNotifyPlatform(
      @Parameter(description = "主键ID集合") @PathVariable List<Long> ids) {
    log.info("删除告警渠道，参数：ids={}", ids);
    return Result.status(iManNotifyPlatformFacade.deleteManagerNotifyPlatform(ids));
  }

  @GetMapping("/{id}")
  @SaCheckPermission("man:notify_platform:detail")
  @Operation(operationId = "5", summary = "获取告警渠道详情")
  public Result<ManNotifyPlatform> getNotifyPlatformDetail(
      @Parameter(description = "主键ID") @PathVariable Long id) {
    log.info("获取告警渠道详情，参数：id={}", id);
    return Result.data(iManNotifyPlatformFacade.getManagerNotifyPlatform(id));
  }

  @PostMapping("/refresh/{clientServiceName}")
  @SaCheckPermission("man:notify_platform:refresh")
  @Operation(operationId = "6", summary = "刷新指定客户端的告警渠道")
  public Result<Boolean> refreshNotifyPlatform(
      @Parameter(description = "客户端服务名称") @PathVariable String clientServiceName) {
    log.info("刷新指定客户端的告警渠道，参数：clientServiceName={}", clientServiceName);

    // 验证客户端是否存在且在线（通过 clientServiceName 获取）
    var client = manClientService.getByClientName(clientServiceName);
    if (client == null) {
      return Result.failure("客户端不存在");
    }
    if (!client.getIsOnline()) {
      return Result.failure("客户端已离线，无法刷新告警渠道");
    }

    // 验证AdminServer中是否连接
    Set<String> connectedClients = adminServer.getConnectedClients();
    if (!connectedClients.contains(client.getServiceName())) {
      return Result.failure("客户端未连接到AdminServer");
    }

    return Result.status(iManNotifyPlatformFacade.refreshNotifyPlatform(clientServiceName));
  }

  @PostMapping("/refresh/all")
  @SaCheckPermission("man:all_notify_platform:refresh")
  @Operation(operationId = "7", summary = "刷新所有客户端的告警渠道")
  public Result<Boolean> refreshAllNotifyPlatforms() {
    log.info("刷新所有客户端的告警渠道");
    return Result.status(iManNotifyPlatformFacade.refreshAllNotifyPlatforms());
  }

  @GetMapping("/by-client/{clientServiceName}")
  @SaCheckPermission("man:notify_platform:list")
  @Operation(operationId = "8", summary = "获取指定客户端的告警渠道")
  public Result<List<ManNotifyPlatform>> getByClient(
      @Parameter(description = "客户端服务名称") @PathVariable String clientServiceName) {
    log.info("获取指定客户端的告警渠道，参数：clientServiceName={}", clientServiceName);

    // 验证客户端是否存在（通过 clientServiceName 获取）
    var client = manClientService.getByClientName(clientServiceName);
    if (client == null) {
      return Result.failure("客户端不存在");
    }

    return Result.data(iManNotifyPlatformFacade.getByClientName(clientServiceName));
  }

  @GetMapping("/clients")
  @SaCheckPermission("man:all_notify_platform:list")
  @Operation(operationId = "9", summary = "获取所有连接的客户端")
  public Result<List<String>> getConnectedClients() {
    log.info("获取所有连接的客户端");

    // 从客户端管理表获取在线客户端
    var onlineClients = manClientService.getOnlineClients();
    List<String> clientIds = onlineClients.stream()
        .map(client -> client.getClientId())
        .collect(Collectors.toList());

    return Result.data(clientIds);
  }
}
