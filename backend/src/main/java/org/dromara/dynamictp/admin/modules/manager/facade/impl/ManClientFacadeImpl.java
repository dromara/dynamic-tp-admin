package org.dromara.dynamictp.admin.modules.manager.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManClientBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManClient;
import org.dromara.dynamictp.admin.modules.manager.facade.IManClientFacade;
import org.dromara.dynamictp.admin.modules.manager.service.IManClientService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户端管理 门面接口实现层
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.manager.facade.impl.ManClientFacadeImpl
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManClientFacadeImpl implements IManClientFacade {

  @NonNull
  private IManClientService manClientService;

  @Override
  public IPage<ManClient> listManagerClientPage(PageQuery pageQuery, ManClientBO manClientBO) {
    log.info("客户端管理 - 分页查询，参数：pageQuery={}, manClientBO={}", pageQuery, manClientBO);
    return manClientService.listManagerClientPage(pageQuery, manClientBO);
  }

  @Override
  public Boolean addManagerClient(ManClientBO manClientBO) {
    log.info("客户端管理 - 新增，参数：manClientBO={}", manClientBO);
    return manClientService.addManagerClient(manClientBO);
  }

  @Override
  public Boolean updateManagerClient(ManClientBO manClientBO) {
    log.info("客户端管理 - 修改，参数：manClientBO={}", manClientBO);
    return manClientService.updateManagerClient(manClientBO);
  }

  @Override
  public Boolean deleteManagerClient(List<Long> ids) {
    log.info("客户端管理 - 删除，参数：ids={}", ids);
    return manClientService.deleteManagerClient(ids);
  }

  @Override
  public ManClient getManagerClient(Long id) {
    log.info("客户端管理 - 详情，参数：id={}", id);
    return manClientService.getManagerClient(id);
  }

  @Override
  public List<ManClient> getOnlineClients() {
    log.info("获取所有在线客户端");
    return manClientService.getOnlineClients();
  }

  @Override
  public Boolean updateConnectTime(String clientName) {
    log.info("更新客户端连接时间，参数：clientName={}", clientName);
    return manClientService.updateConnectTime(clientName);
  }

  @Override
  public Boolean updateDisconnectTime(String clientName) {
    log.info("更新客户端断开时间，参数：clientName={}", clientName);
    return manClientService.updateDisconnectTime(clientName);
  }

  @Override
  public Boolean updateHeartbeatTime(String clientName) {
    log.info("更新客户端心跳时间，参数：clientName={}", clientName);
    return manClientService.updateHeartbeatTime(clientName);
  }

  @Override
  public Boolean checkClientStatus(String clientServiceName) {
    log.info("检查客户端状态，参数：clientName={}", clientServiceName);
    return manClientService.checkClientStatus(clientServiceName);
  }

  @Override
  public Boolean markClientAsOffline(String clientName) {
    log.info("标记客户端为离线，参数：clientName={}", clientName);
    return manClientService.markClientAsOffline(clientName);
  }

  @Override
  public List<String> getUnresponsiveClients() {
    log.info("获取无响应的客户端列表");
    return manClientService.getUnresponsiveClients();
  }

  @Override
  public Boolean handleClientConnection(String clientId, String clientName, String serviceName, String clientIp,
      Integer clientPort,
      String serverIp, Integer serverPort) {
    log.info(
        "处理客户端连接，参数：clientId={}, clientName={}, serviceName={}, clientIp={}, clientPort={}, serverIp={}, serverPort={}",
        clientId, clientName, serviceName, clientIp, clientPort, serverIp, serverPort);
    return manClientService.handleClientConnection(clientId, clientName, serviceName, clientIp, clientPort, serverIp,
        serverPort);
  }
}