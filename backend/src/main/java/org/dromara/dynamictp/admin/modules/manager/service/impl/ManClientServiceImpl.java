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
 * @ProjectName panis-boot
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
  public ManClient getByClientName(String clientName) {
    LambdaQueryWrapper<ManClient> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ManClient::getClientName, clientName);
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
  public Boolean checkClientStatus(String clientName) {
    try {
      LambdaQueryWrapper<ManClient> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(ManClient::getClientName, clientName);
      ManClient client = this.getOne(queryWrapper);
      if (client == null) {
        log.warn("客户端 {} 不存在", clientName);
        return false;
      }

      // 检查客户端是否在线
      if (!client.getIsOnline()) {
        log.warn("客户端 {} 已离线", clientName);
        return false;
      }

      // 检查最后心跳时间，如果超过5分钟没有心跳，认为客户端无响应
      if (client.getLastHeartbeatTime() != null) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastHeartbeat = client.getLastHeartbeatTime();
        long minutesSinceLastHeartbeat = java.time.Duration.between(lastHeartbeat, now).toMinutes();

        if (minutesSinceLastHeartbeat > 5) {
          log.warn("客户端 {} 超过5分钟无心跳，标记为离线", clientName);
          updateDisconnectTime(clientName);
          return false;
        }
      }

      return true;
    } catch (Exception e) {
      log.error("检查客户端 {} 状态时发生异常: {}", clientName, e.getMessage(), e);
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
            log.warn("客户端 {} 超过5分钟无心跳，标记为离线", client.getClientName());
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
  public Boolean handleClientConnection(String clientId, String clientName, String clientType,
      String clientVersion, String clientIp, Integer clientPort,
      String serverIp, Integer serverPort) {
    try {
      log.info("处理客户端连接: {}", clientName);

      // 检查数据库中是否已存在该客户端（按ID查询）
      LambdaQueryWrapper<ManClient> existedWrapper = new LambdaQueryWrapper<>();
      existedWrapper.eq(ManClient::getClientName, clientName);
      ManClient existingClient = this.getOne(existedWrapper);

      if (existingClient == null) {
        // 客户端不存在，新增客户端数据
        log.info("客户端 {} 不存在，新增客户端数据", clientName);

        ManClient newClient = ManClient.builder()
            .clientId(clientId)
            .clientName(clientName)
            .clientType(clientType)
            .clientVersion(clientVersion)
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
          log.info("客户端 {} 新增成功", clientName);
        } else {
          log.error("客户端 {} 新增失败", clientName);
        }
        return result;
      } else {
        // 客户端已存在，更新需要更新的字段
        log.info("客户端 {} 已存在，更新客户端数据", clientName);

        LambdaUpdateWrapper<ManClient> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ManClient::getClientName, clientName)
            .set(ManClient::getClientId, clientId)
            .set(ManClient::getClientType, clientType)
            .set(ManClient::getClientVersion, clientVersion)
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
          log.info("客户端 {} 更新成功", clientName);
        } else {
          log.error("客户端 {} 更新失败", clientName);
        }
        return result;
      }
    } catch (Exception e) {
      log.error("处理客户端 {} 连接时发生异常: {}", clientName, e.getMessage(), e);
      return false;
    }
  }
}