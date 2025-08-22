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
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManThreadPoolBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManThreadPool;
import org.dromara.dynamictp.admin.modules.manager.facade.IManThreadPoolFacade;
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
 * 线程池管理
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.admin.controller.manager.ManThreadPoolController
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@RestController
@Tag(name = "线程池管理")
@RequiredArgsConstructor
@RequestMapping("/man_thread_pool")
public class ManThreadPoolController {

  @NonNull
  private IManThreadPoolFacade iManThreadPoolFacade;

  @NonNull
  private AdminServer adminServer;

  @NonNull
  private IManClientService manClientService;

  @GetMapping("/page")
  @SaCheckPermission("man:thread_pool:page")
  @Operation(operationId = "1", summary = "分页获取线程池列表")
  public Result<IPage<ManThreadPool>> getThreadPoolPage(
      @Parameter(description = "分页参数") PageQuery pageQuery,
      @Parameter(description = "查询条件") ManThreadPoolBO managerThreadPoolBO) {
    log.info("分页获取线程池列表，参数：pageQuery={}, managerThreadPoolBO={}", pageQuery, managerThreadPoolBO);
    return Result
        .data(iManThreadPoolFacade.listManagerThreadPoolPage(pageQuery, managerThreadPoolBO));
  }

  @GetMapping("/by-client/{clientName}/page")
  @SaCheckPermission("man:thread_pool:page")
  @Operation(operationId = "1.1", summary = "按客户端分页获取线程池列表")
  public Result<IPage<ManThreadPool>> getThreadPoolPageByClient(
      @Parameter(description = "客户端名称") @PathVariable String clientName,
      @Parameter(description = "分页参数") PageQuery pageQuery,
      @Parameter(description = "查询条件") ManThreadPoolBO managerThreadPoolBO) {
    log.info("按客户端分页获取线程池列表，参数：clientName={}, pageQuery={}, managerThreadPoolBO={}", clientName, pageQuery,
        managerThreadPoolBO);

    // 验证客户端是否存在且在线（通过 clientName 获取）
    var client = manClientService.getByClientName(clientName);
    if (client == null) {
      return Result.failure("客户端不存在");
    }
    if (!client.getIsOnline()) {
      return Result.failure("客户端已离线");
    }

    // 设置客户端名称到查询条件中
    if (managerThreadPoolBO == null) {
      managerThreadPoolBO = new ManThreadPoolBO();
    }
    managerThreadPoolBO.setClientName(client.getClientName());

    return Result
        .data(iManThreadPoolFacade.listManagerThreadPoolPage(pageQuery, managerThreadPoolBO));
  }

  @PostMapping
  @SaCheckPermission("man:thread_pool:add")
  @Operation(operationId = "2", summary = "新增线程池")
  public Result<Boolean> addThreadPool(
      @Parameter(description = "线程池信息") @RequestBody ManThreadPoolBO managerThreadPoolBO) {
    log.info("新增线程池，参数：managerThreadPoolBO={}", managerThreadPoolBO);

    // 如携带 clientId，仅进行状态检查，避免直接查询方法链依赖
    String clientName = managerThreadPoolBO.getClientName();
    if (clientName != null) {
      Boolean ok = manClientService.checkClientStatus(clientName);
      if (!ok) {
        return Result.failure("客户端不可用，无法新增线程池配置");
      }
    }

    return Result.status(iManThreadPoolFacade.addManagerThreadPool(managerThreadPoolBO));
  }

  @PutMapping
  @SaCheckPermission("man:thread_pool:update")
  @Operation(operationId = "3", summary = "修改线程池")
  public Result<Boolean> updateThreadPool(
      @Parameter(description = "线程池信息") @RequestBody ManThreadPoolBO managerThreadPoolBO) {
    log.info("修改线程池，参数：managerThreadPoolBO={}", managerThreadPoolBO);
    return Result.status(iManThreadPoolFacade.updateManagerThreadPool(managerThreadPoolBO));
  }

  @DeleteMapping("/{ids}")
  @SaCheckPermission("man:thread_pool:delete")
  @Operation(operationId = "4", summary = "删除线程池")
  public Result<Boolean> deleteThreadPool(
      @Parameter(description = "主键ID集合") @PathVariable List<Long> ids) {
    log.info("删除线程池，参数：ids={}", ids);
    return Result.status(iManThreadPoolFacade.deleteManagerThreadPool(ids));
  }

  @GetMapping("/{id}")
  @SaCheckPermission("man:thread_pool:detail")
  @Operation(operationId = "5", summary = "获取线程池详情")
  public Result<ManThreadPool> getThreadPoolDetail(
      @Parameter(description = "主键ID") @PathVariable Long id) {
    log.info("获取线程池详情，参数：id={}", id);
    return Result.data(iManThreadPoolFacade.getManagerThreadPool(id));
  }

  @PostMapping("/refresh/{clientName}")
  @SaCheckPermission("man:thread_pool:refresh")
  @Operation(operationId = "6", summary = "刷新指定客户端的线程池")
  public Result<Boolean> refreshThreadPool(
      @Parameter(description = "客户端名称") @PathVariable String clientName) {
    log.info("刷新指定客户端的线程池，参数：clientName={}", clientName);

    // 验证客户端是否存在且在线（通过 clientName 获取）
    var client = manClientService.getByClientName(clientName);
    if (client == null) {
      return Result.failure("客户端不存在");
    }
    if (!client.getIsOnline()) {
      return Result.failure("客户端已离线，无法刷新线程池");
    }

    // 验证AdminServer中是否连接
    Set<String> connectedClients = adminServer.getConnectedClients();
    if (!connectedClients.contains(client.getClientName())) {
      return Result.failure("客户端未连接到AdminServer");
    }

    return Result.status(iManThreadPoolFacade.refreshThreadPool(client.getClientId()));
  }

  @PostMapping("/refresh/all")
  @SaCheckPermission("man:all_thread_pool:refresh")
  @Operation(operationId = "7", summary = "刷新所有客户端的线程池")
  public Result<Boolean> refreshAllThreadPools() {
    log.info("刷新所有客户端的线程池");
    return Result.status(iManThreadPoolFacade.refreshAllThreadPools());
  }

  @GetMapping("/by-client/{clientName}")
  @SaCheckPermission("man:thread_pool:list")
  @Operation(operationId = "8", summary = "获取指定客户端的线程池")
  public Result<List<ManThreadPool>> getByClient(
      @Parameter(description = "客户端名称") @PathVariable String clientName) {
    log.info("获取指定客户端的线程池，参数：clientName={}", clientName);

    // 验证客户端是否存在（通过 clientName 获取）
    var client = manClientService.getByClientName(clientName);
    if (client == null) {
      return Result.failure("客户端不存在");
    }

    return Result.data(iManThreadPoolFacade.getByClientName(clientName));
  }

  @GetMapping("/clients")
  @SaCheckPermission("man:all_thread_pool:list")
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