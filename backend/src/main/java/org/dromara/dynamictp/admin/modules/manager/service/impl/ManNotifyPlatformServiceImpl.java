package org.dromara.dynamictp.admin.modules.manager.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.server.AdminServer;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManNotifyPlatformBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyPlatform;
import org.dromara.dynamictp.admin.modules.manager.domain.vo.ManNotifyPlatformVO;
import org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManNotifyPlatformMapper;
import org.dromara.dynamictp.admin.modules.manager.service.IManNotifyPlatformService;
import org.dromara.dynamictp.common.em.AdminRequestTypeEnum;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 告警渠道管理 Service 服务实现层
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.manager.service.impl.ManNotifyPlatformServiceImpl
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManNotifyPlatformServiceImpl extends ServiceImpl<ManNotifyPlatformMapper, ManNotifyPlatform>
    implements IManNotifyPlatformService {

  @Resource
  private AdminServer adminServer;

  @Override
  public IPage<ManNotifyPlatform> listManagerNotifyPlatformPage(PageQuery pageQuery,
      ManNotifyPlatformBO manNotifyPlatformBO) {
    LambdaQueryWrapper<ManNotifyPlatform> queryWrapper = new LambdaQueryWrapper<>();

    if (manNotifyPlatformBO != null) {
      // 根据告警平台名称查询
      if (StrUtil.isNotBlank(manNotifyPlatformBO.getPlatform())) {
        queryWrapper.like(ManNotifyPlatform::getPlatform, manNotifyPlatformBO.getPlatform());
      }

      // 根据客户端ID查询
      if (StrUtil.isNotBlank(manNotifyPlatformBO.getClientId())) {
        queryWrapper.eq(ManNotifyPlatform::getClientId, manNotifyPlatformBO.getClientId());
      }

      // 根据客户端名称查询
      if (StrUtil.isNotBlank(manNotifyPlatformBO.getClientName())) {
        queryWrapper.eq(ManNotifyPlatform::getClientName, manNotifyPlatformBO.getClientName());
      }

      // 根据状态查询
      if (StrUtil.isNotBlank(manNotifyPlatformBO.getStatus())) {
        queryWrapper.eq(ManNotifyPlatform::getStatus, manNotifyPlatformBO.getStatus());
      }
    }

    // 按创建时间倒序
    queryWrapper.orderByDesc(ManNotifyPlatform::getCreateTime);

    Page<ManNotifyPlatform> page = new Page<>(pageQuery.getPage(), pageQuery.getPageSize());
    return this.page(page, queryWrapper);
  }

  @Override
  public Boolean addManagerNotifyPlatform(ManNotifyPlatformBO manNotifyPlatformBO) {
    ManNotifyPlatform manNotifyPlatform = BeanUtil.copyProperties(manNotifyPlatformBO, ManNotifyPlatform.class);

    // 生成平台ID
    if (StrUtil.isBlank(manNotifyPlatform.getPlatformId())) {
      manNotifyPlatform.setPlatformId(UUID.randomUUID().toString());
    }

    // 设置默认值
    if (manNotifyPlatform.getTimeout() == null) {
      manNotifyPlatform.setTimeout(3000);
    }
    if (StrUtil.isBlank(manNotifyPlatform.getProxyType())) {
      manNotifyPlatform.setProxyType("DIRECT");
    }
    if (manNotifyPlatform.getProxyPort() == null) {
      manNotifyPlatform.setProxyPort(0);
    }
    if (StrUtil.isBlank(manNotifyPlatform.getReceivers())) {
      manNotifyPlatform.setReceivers("all");
    }
    if (StrUtil.isBlank(manNotifyPlatform.getStatus())) {
      manNotifyPlatform.setStatus("ENABLE");
    }

    return this.save(manNotifyPlatform);
  }

  @Override
  public Boolean updateManagerNotifyPlatform(ManNotifyPlatformBO manNotifyPlatformBO) {
    ManNotifyPlatform manNotifyPlatform = BeanUtil.copyProperties(manNotifyPlatformBO, ManNotifyPlatform.class);
    return this.updateById(manNotifyPlatform);
  }

  @Override
  public Boolean deleteManagerNotifyPlatform(List<Long> ids) {
    return this.removeByIds(ids);
  }

  @Override
  public ManNotifyPlatform getManagerNotifyPlatform(Long id) {
    return this.getById(id);
  }

  @Override
  public ManNotifyPlatformVO getManagerNotifyPlatformVO(Long id) {
    ManNotifyPlatform manNotifyPlatform = this.getById(id);
    if (manNotifyPlatform == null) {
      return null;
    }
    return BeanUtil.copyProperties(manNotifyPlatform, ManNotifyPlatformVO.class);
  }

  @Override
  public Boolean refreshNotifyPlatform(String clientServiceName) {
    // 1. 通过 clientId 查找对应的客户端地址
    try {
      String clientAddress = adminServer.getClientAddressByName(clientServiceName);
      if (clientAddress == null) {
        log.warn("刷新告警渠道失败，未找到在线客户端，clientServiceName: {}", clientServiceName);
        return false;
      }
      if (!adminServer.isClientConnected(clientAddress)) {
        log.warn("客户端未连接，clientId: {}, address: {}", clientServiceName, clientAddress);
        return false;
      }

      // 2. 获取该客户端的告警平台配置 (仅启用状态)
      List<ManNotifyPlatform> platforms = getByClientServiceName(clientServiceName).stream()
          .filter(p -> "ENABLE".equalsIgnoreCase(p.getStatus()))
          .collect(Collectors.toList());
      if (platforms.isEmpty()) {
        log.warn("客户端 {} 无可刷新的启用告警渠道", clientServiceName);
        return false;
      }

      // 3. 转换为属性 Map
      Map<Object, Object> properties = convertPlatformsToMap(platforms);

      // 4. 发送刷新请求（复用 EXECUTOR_REFRESH 类型，客户端统一解析配置）
      adminServer.requestToSpecificClient(clientAddress, AdminRequestTypeEnum.EXECUTOR_REFRESH, properties);
      log.info("刷新告警渠道成功，clientId: {}, 平台数量: {}", clientServiceName, platforms.size());
      return true;
    } catch (Exception e) {
      log.error("刷新告警渠道异常，clientId: {}，原因: {}", clientServiceName, e.getMessage(), e);
      return false;
    }
  }

  @Override
  public Boolean refreshAllNotifyPlatforms() {
    try {
      Set<String> addresses = adminServer.getConnectedClientAddresses();
      if (addresses == null || addresses.isEmpty()) {
        log.warn("当前无任何在线客户端，无法刷新告警渠道");
        return false;
      }
      boolean allSuccess = true;
      for (String address : addresses) {
        String cid = adminServer.getAttribute(address, "clientId");
        if (StrUtil.isBlank(cid)) {
          log.warn("跳过无 clientId 属性的连接: {}", address);
          allSuccess = false;
          continue;
        }
        boolean single = refreshNotifyPlatform(cid);
        if (!single) {
          allSuccess = false;
        }
      }
      return allSuccess;
    } catch (Exception e) {
      log.error("刷新所有客户端告警渠道发生异常: {}", e.getMessage(), e);
      return false;
    }
  }

  @Override
  public List<ManNotifyPlatform> getByClientServiceName(String clientServiceName) {
    LambdaQueryWrapper<ManNotifyPlatform> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ManNotifyPlatform::getClientName, clientServiceName);
    queryWrapper.orderByDesc(ManNotifyPlatform::getCreateTime);
    return this.list(queryWrapper);
  }

  @Override
  public List<ManNotifyPlatform> getByClientName(String clientServiceName) {
    LambdaQueryWrapper<ManNotifyPlatform> queryWrapper = new LambdaQueryWrapper<>();

    queryWrapper.eq(ManNotifyPlatform::getClientName, clientServiceName);
    queryWrapper.orderByDesc(ManNotifyPlatform::getCreateTime);
    return this.list(queryWrapper);
  }

  @Override
  public List<ManNotifyPlatformVO> getByClientServiceVO(String clientServiceName) {
    List<ManNotifyPlatform> list = this.getByClientServiceName(clientServiceName);
    return BeanUtil.copyToList(list, ManNotifyPlatformVO.class);
  }

  /**
   * 构建告警平台配置属性 Map
   * 格式: dynamictp.platforms[0].platformId 等
   */
  private Map<Object, Object> convertPlatformsToMap(List<ManNotifyPlatform> platforms) {
    Map<Object, Object> properties = new HashMap<>();
    for (int i = 0; i < platforms.size(); i++) {
      ManNotifyPlatform p = platforms.get(i);
      String prefix = "dynamictp.platforms[" + i + "].";
      properties.put(prefix + "platformId", p.getPlatformId());
      properties.put(prefix + "platform", p.getPlatform());
      properties.put(prefix + "webhook", p.getWebhook());
      properties.put(prefix + "receivers", p.getReceivers());
      properties.put(prefix + "timeout", p.getTimeout());
      properties.put(prefix + "proxyType", p.getProxyType());
      properties.put(prefix + "proxyHost", p.getProxyHost());
      properties.put(prefix + "proxyPort", p.getProxyPort());
    }
    return properties;
  }
}
