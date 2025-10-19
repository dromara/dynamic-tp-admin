package org.dromara.dynamictp.admin.modules.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManClientBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManClient;
import org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManClientMapper;
import org.dromara.dynamictp.admin.modules.manager.service.IManClientService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 客户端管理 Service 服务实现层
 *
 * @Author eachann
 * @ClassName org.dromara.dynamictp.admin.modules.manager.service.impl.ManClientServiceImpl
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@Service
public class ManClientServiceImpl extends ServiceImpl<ManClientMapper, ManClient>
    implements IManClientService {

  private final ClientHttpConnector clientHttpConnector;

  public ManClientServiceImpl(ClientHttpConnector clientHttpConnector) {
    this.clientHttpConnector = clientHttpConnector;
  }

  /**
   * 生成服务名称，格式：clientName:serviceName
   * 如果serviceName为空或null，则使用默认的"service"
   *
   * @param clientName  客户端名称
   * @param serviceName 服务名称（可为空）
   * @return 格式化后的服务名称
   */
  private String generateServiceName(String clientName, String serviceName) {
    if (serviceName == null || serviceName.trim().isEmpty()) {
      return clientName + ":service";
    }
    return clientName + ":" + serviceName;
  }

  @Override
  public IPage<ManClient> listManagerClientPage(PageQuery pageQuery, ManClientBO manClientBO) {
    LambdaQueryWrapper<ManClient> queryWrapper = new LambdaQueryWrapper<>();

    // 根据客户端ID过滤
    if (manClientBO != null && manClientBO.getClientId() != null) {
      queryWrapper.like(ManClient::getClientId, manClientBO.getClientId());
    }

    // 根据客户端名称过滤
    if (manClientBO != null && manClientBO.getClientName() != null) {
      queryWrapper.like(ManClient::getClientName, manClientBO.getClientName());
    }

    // 根据服务名称过滤
    if (manClientBO != null && manClientBO.getServiceName() != null) {
      queryWrapper.like(ManClient::getServiceName, manClientBO.getServiceName());
    }

    // 根据在线状态过滤
    if (manClientBO != null && manClientBO.getIsOnline() != null) {
      queryWrapper.eq(ManClient::getIsOnline, manClientBO.getIsOnline());
    }

    // 根据状态过滤
    if (manClientBO != null && manClientBO.getStatus() != null) {
      queryWrapper.eq(ManClient::getStatus, manClientBO.getStatus());
    }

    queryWrapper.orderByDesc(ManClient::getCreateTime);

    return this.page(new Page<>(pageQuery.getPage(), pageQuery.getPageSize()), queryWrapper);
  }

  @Override
  public Boolean addManagerClient(ManClientBO manClientBO) {
    if (manClientBO.getClientName() != null) {
      LambdaQueryWrapper<ManClient> sameNameWrapper = new LambdaQueryWrapper<>();
      sameNameWrapper.eq(ManClient::getClientName, manClientBO.getClientName());
      ManClient existed = this.getOne(sameNameWrapper);
      if (existed != null) {
        ManClient client = new ManClient();
        BeanUtils.copyProperties(manClientBO, client);
        client.setId(existed.getId());
        return this.updateById(client);
      }
    }

    ManClient client = new ManClient();
    BeanUtils.copyProperties(manClientBO, client);

    // 确保serviceName格式正确
    if (client.getServiceName() == null || client.getServiceName().trim().isEmpty()) {
      client.setServiceName(generateServiceName(client.getClientName(), null));
    } else if (!client.getServiceName().contains(":")) {
      // 如果serviceName不包含冒号，则按clientName:serviceName格式重新生成
      client.setServiceName(generateServiceName(client.getClientName(), client.getServiceName()));
    }

    return this.save(client);
  }

  @Override
  public Boolean updateManagerClient(ManClientBO manClientBO) {
    if (manClientBO.getId() == null) {
      log.error("更新客户端配置失败：ID不能为空");
      return false;
    }

    if (manClientBO.getClientName() != null) {
      LambdaQueryWrapper<ManClient> sameNameWrapper = new LambdaQueryWrapper<>();
      sameNameWrapper.eq(ManClient::getClientName, manClientBO.getClientName());
      ManClient existed = this.getOne(sameNameWrapper);
      if (existed != null && !existed.getId().equals(manClientBO.getId())) {
        ManClient client = new ManClient();
        BeanUtils.copyProperties(manClientBO, client);
        client.setId(existed.getId());
        log.info("检测到相同客户端名称，转为在原数据(ID: {})上作修改", existed.getId());
        return this.updateById(client);
      }
    }

    ManClient client = new ManClient();
    BeanUtils.copyProperties(manClientBO, client);

    // 确保serviceName格式正确
    if (client.getServiceName() != null && !client.getServiceName().trim().isEmpty()
        && !client.getServiceName().contains(":")) {
      // 如果serviceName不包含冒号，则按clientName:serviceName格式重新生成
      client.setServiceName(generateServiceName(client.getClientName(), client.getServiceName()));
    }

    log.info("更新客户端配置，ID: {}, 客户端ID: {}", client.getId(), client.getClientId());
    return this.updateById(client);
  }

  @Override
  public Boolean deleteManagerClient(List<Long> ids) {
    return this.removeByIds(ids);
  }

  @Override
  public ManClient getManagerClient(Long id) {
    return this.getById(id);
  }

  @Override
  public ManClient getByClientName(String clientServiceName) {
    LambdaQueryWrapper<ManClient> queryWrapper = new LambdaQueryWrapper<>();
    // 如果传入的是clientServiceName格式（包含冒号），则通过serviceName查找
    if (clientServiceName != null && clientServiceName.contains(":")) {
      queryWrapper.eq(ManClient::getServiceName, clientServiceName);
    } else {
      // 如果传入的是纯clientName，则通过clientName查找
      queryWrapper.eq(ManClient::getClientName, clientServiceName);
    }
    return this.getOne(queryWrapper);
  }

  @Override
  public List<ManClient> getOnlineClients() {
    LambdaQueryWrapper<ManClient> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ManClient::getIsOnline, true)
        .eq(ManClient::getStatus, "ENABLE")
        .orderByDesc(ManClient::getLastConnectTime);
    return this.list(queryWrapper);
  }

  @Override
  public Boolean updateOnlineStatus(String clientName, Boolean isOnline) {
    LambdaUpdateWrapper<ManClient> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(ManClient::getClientName, clientName)
        .set(ManClient::getIsOnline, isOnline)
        .set(ManClient::getUpdateTime, LocalDateTime.now());

    if (isOnline) {
      updateWrapper.set(ManClient::getLastConnectTime, LocalDateTime.now())
          .setSql("connect_count = connect_count + 1");
    } else {
      updateWrapper.set(ManClient::getLastDisconnectTime, LocalDateTime.now());
    }

    boolean result = this.update(updateWrapper);
    log.info("更新客户端 {} 在线状态为: {}", clientName, isOnline);
    return result;
  }

  @Override
  public Boolean updateHeartbeatTime(String clientName) {
    LambdaUpdateWrapper<ManClient> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(ManClient::getClientName, clientName)
        .set(ManClient::getLastHeartbeatTime, LocalDateTime.now())
        .set(ManClient::getUpdateTime, LocalDateTime.now());
    return this.update(updateWrapper);
  }

  @Override
  public Boolean updateConnectTime(String clientName) {
    LambdaUpdateWrapper<ManClient> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(ManClient::getClientName, clientName)
        .set(ManClient::getLastConnectTime, LocalDateTime.now())
        .set(ManClient::getIsOnline, true)
        .set(ManClient::getUpdateTime, LocalDateTime.now())
        .setSql("connect_count = connect_count + 1");
    return this.update(updateWrapper);
  }

  @Override
  public Boolean updateDisconnectTime(String clientName) {
    LambdaUpdateWrapper<ManClient> updateWrapper = new LambdaUpdateWrapper<>();
    updateWrapper.eq(ManClient::getClientName, clientName)
        .set(ManClient::getLastDisconnectTime, LocalDateTime.now())
        .set(ManClient::getIsOnline, false)
        .set(ManClient::getUpdateTime, LocalDateTime.now());
    return this.update(updateWrapper);
  }

  @Override
  public Boolean checkClientStatus(String clientServiceName) {
    try {
      LambdaQueryWrapper<ManClient> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(ManClient::getServiceName, clientServiceName);
      ManClient client = this.getOne(queryWrapper);
      if (client == null) {
        log.warn("客户端服务 {} 不存在", clientServiceName);
        return false;
      }

      // 检查客户端是否在线
      if (!client.getIsOnline()) {
        log.warn("客户端服务 {} 已离线", clientServiceName);
        return false;
      }

      // 检查最后心跳时间，如果超过5分钟没有心跳，认为客户端无响应
      if (client.getLastHeartbeatTime() != null) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastHeartbeat = client.getLastHeartbeatTime();
        long minutesSinceLastHeartbeat = java.time.Duration.between(lastHeartbeat, now).toMinutes();

        if (minutesSinceLastHeartbeat > 5) {
          log.warn("客户端 {} 超过5分钟无心跳，标记为离线", clientServiceName);
          updateDisconnectTime(clientServiceName);
          return false;
        }
      }

      return true;
    } catch (Exception e) {
      log.error("检查客户端 {} 状态时发生异常: {}", clientServiceName, e.getMessage(), e);
      return false;
    }
  }

  @Override
  public Boolean markClientAsOffline(String clientName) {
    log.info("标记客户端 {} 为离线状态", clientName);
    return updateDisconnectTime(clientName);
  }

  @Override
  public List<String> getUnresponsiveClients() {
    List<String> unresponsiveClients = new ArrayList<>();

    try {
      // 获取所有在线客户端
      List<ManClient> onlineClients = getOnlineClients();
      LocalDateTime now = LocalDateTime.now();

      for (ManClient client : onlineClients) {
        if (client.getLastHeartbeatTime() != null) {
          long minutesSinceLastHeartbeat = java.time.Duration.between(client.getLastHeartbeatTime(), now).toMinutes();

          if (minutesSinceLastHeartbeat > 5) {
            log.warn("客户�� {} 超过5分钟无心跳，标记为离线", client.getClientName());
            updateDisconnectTime(client.getClientName());
            unresponsiveClients.add(client.getClientId());
          }
        }
      }
    } catch (Exception e) {
      log.error("检查无响应客户端时发生异常: {}", e.getMessage(), e);
    }

    return unresponsiveClients;
  }

  @Override
  public Boolean handleClientConnection(String clientId, String clientName, String serviceName, String clientIp,
      Integer clientPort,
      String serverIp, Integer serverPort) {
    try {
      String formattedServiceName = generateServiceName(clientName, serviceName);
      log.info("处理客户端连接: clientAddress={}, clientName={}, serviceName={}",
          clientId, clientName, serviceName);
      LambdaQueryWrapper<ManClient> existedWrapper = new LambdaQueryWrapper<>();
      existedWrapper.eq(ManClient::getServiceName, formattedServiceName);
      ManClient existingClient = this.getOne(existedWrapper);
      if (existingClient == null) {
        log.info("客户端不存在，新增: clientId={}, clientName={}", clientId, clientName);
        ManClient newClient = ManClient.builder()
            .clientId(clientId)
            .clientName(clientName)
            .serviceName(formattedServiceName)
            .clientIp(clientIp)
            .clientPort(clientPort)
            .serverIp(serverIp)
            .serverPort(serverPort)
            .isOnline(true)
            .lastConnectTime(LocalDateTime.now())
            .connectCount(1)
            .totalOnlineTime(0L)
            .status("ENABLE")
            .build();
        boolean result = this.save(newClient);
        if (result) {
          log.info("新增客户端成功: {}", clientId);
        } else {
          log.error("新增客户端失败: {}", clientId);
        }
        return result;
      } else {
        log.info("客户端已存在，更新: clientId={}, clientName={}", clientId, clientName);
        LambdaUpdateWrapper<ManClient> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ManClient::getServiceName, formattedServiceName)
            .set(ManClient::getClientName, clientName)
            .set(ManClient::getServiceName, formattedServiceName)
            .set(ManClient::getClientIp, clientIp)
            .set(ManClient::getClientPort, clientPort)
            .set(ManClient::getServerIp, serverIp)
            .set(ManClient::getServerPort, serverPort)
            .set(ManClient::getIsOnline, true)
            .set(ManClient::getLastConnectTime, LocalDateTime.now())
            .set(ManClient::getUpdateTime, LocalDateTime.now())
            .setSql("connect_count = connect_count + 1");
        boolean result = this.update(updateWrapper);
        if (result) {
          log.info("更新客户端成功: {}", clientId);
        } else {
          log.error("更新客户端失败: {}", clientId);
        }
        return result;
      }
    } catch (Exception e) {
      log.error("处理客户端连接异常 clientId={}, clientName={}, err={}", clientId, clientName, e.getMessage(), e);
      return false;
    }
  }

  /**
   * 批量下线所有在线客户端，用于应用关闭时快速清理状态
   */
  @Override
  public int markAllOnlineClientsOffline() {
    try {
      LambdaQueryWrapper<ManClient> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(ManClient::getIsOnline, true);
      List<ManClient> onlineList = this.list(queryWrapper);
      if (onlineList.isEmpty()) {
        return 0;
      }
      LocalDateTime now = LocalDateTime.now();
      LambdaUpdateWrapper<ManClient> updateWrapper = new LambdaUpdateWrapper<>();
      updateWrapper.eq(ManClient::getIsOnline, true)
          .set(ManClient::getIsOnline, false)
          .set(ManClient::getLastDisconnectTime, now)
          .set(ManClient::getUpdateTime, now);
      boolean ok = this.update(updateWrapper);
      int affected = ok ? onlineList.size() : 0;
      log.info("批量下线在线客户端完成, 计划下线={}, 实际下线={}, success={} ", onlineList.size(), affected, ok);
      return affected;
    } catch (Exception e) {
      log.error("批量下线在线客户端异常: {}", e.getMessage(), e);
      return 0;
    }
  }
}