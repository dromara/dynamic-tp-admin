package org.dromara.dynamictp.admin.modules.manager.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.server.handler.PropertiesHandler;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyItem;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyPlatform;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManThreadPool;
import org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManNotifyItemMapper;
import org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManNotifyPlatformMapper;
import org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManThreadPoolMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PropertiesHandler 接口实现类
 * 实现线程池配置的刷新逻辑
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.service.impl.PropertiesHandlerImpl
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@Service
public class PropertiesHandlerImpl implements PropertiesHandler {

  @Autowired
  private ManThreadPoolMapper manThreadPoolMapper;

  @Autowired
  private ManNotifyItemMapper manNotifyItemMapper;

  @Autowired
  private ManNotifyPlatformMapper manNotifyPlatformMapper;

  @Override
  public Map<Object, Object> convertConfigsToMap(String clientAddress) {
    List<ManThreadPool> configs = getByClientAddress(clientAddress);
    Map<Object, Object> properties = new HashMap<>();

    // 遍历每个线程池配置，使用数组索引格式
    for (int i = 0; i < configs.size(); i++) {
      ManThreadPool config = configs.get(i);
      String prefix = "dynamictp.executors[" + i + "].";

      // 添加线程池基本配置
      properties.put(prefix + "threadPoolName", config.getThreadPoolName());
      properties.put(prefix + "threadPoolAliasName", config.getThreadPoolAliasName());
      properties.put(prefix + "corePoolSize", config.getCorePoolSize());
      properties.put(prefix + "maximumPoolSize", config.getMaximumPoolSize());
      properties.put(prefix + "queueCapacity", config.getQueueCapacity());
      properties.put(prefix + "queueType", config.getQueueType());
      properties.put(prefix + "rejectedExecutionType", config.getRejectedExecutionType());
      properties.put(prefix + "executorType", config.getExecutorType());
      properties.put(prefix + "keepAliveTime", config.getKeepAliveTime());
      properties.put(prefix + "allowCoreThreadTimeOut", config.getAllowCoreThreadTimeOut());
      properties.put(prefix + "threadNamePrefix", config.getThreadNamePrefix());
      properties.put(prefix + "runTimeout", config.getRunTimeout());
      properties.put(prefix + "queueTimeout", config.getQueueTimeout());
      properties.put(prefix + "taskWrapperNames", config.getTaskWrapperNames());
      properties.put(prefix + "waitForTasksToCompleteOnShutdown", config.getWaitForTasksToCompleteOnShutdown());
      properties.put(prefix + "awaitTerminationSeconds", config.getAwaitTerminationSeconds());
      properties.put(prefix + "preStartAllCoreThreads", config.getPreStartAllCoreThreads());

      // 添加notifyitem及相关平台配置
      addNotifyItemsToProperties(properties, prefix, config.getId());
    }

    return properties;
  }

  /**
   * 根据客户端地址获取线程池配置
   * 
   * @param clientAddress 客户端地址
   * @return 线程池配置列表
   */
  private List<ManThreadPool> getByClientAddress(String clientAddress) {
    // 这里需要根据客户端地址获取客户端名称，然后查询配置
    // 由于没有AdminServer的依赖，我们直接查询所有启用的配置
    LambdaQueryWrapper<ManThreadPool> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ManThreadPool::getStatus, "ENABLE")
        .orderByAsc(ManThreadPool::getCreateTime);

    return manThreadPoolMapper.selectList(queryWrapper);
  }

  /**
   * 将通知配置添加到properties中
   * 
   * @param properties     配置Map
   * @param executorPrefix 执行器前缀
   * @param threadPoolId   线程池ID
   */
  private void addNotifyItemsToProperties(Map<Object, Object> properties, String executorPrefix, Long threadPoolId) {
    try {
      // 查询该线程池的通知配置
      LambdaQueryWrapper<ManNotifyItem> queryWrapper = new LambdaQueryWrapper<>();
      queryWrapper.eq(ManNotifyItem::getThreadPoolId, threadPoolId)
          .eq(ManNotifyItem::getStatus, "ENABLE")
          .eq(ManNotifyItem::getEnabled, true);

      List<ManNotifyItem> notifyItems = manNotifyItemMapper.selectList(queryWrapper);

      if (notifyItems != null && !notifyItems.isEmpty()) {
        // 添加通知配置
        for (int j = 0; j < notifyItems.size(); j++) {
          ManNotifyItem notifyItem = notifyItems.get(j);
          String notifyPrefix = executorPrefix + "notifyItems[" + j + "].";

          properties.put(notifyPrefix + "type", notifyItem.getType());
          properties.put(notifyPrefix + "enabled", notifyItem.getEnabled());
          properties.put(notifyPrefix + "threshold", notifyItem.getThreshold());
          properties.put(notifyPrefix + "count", notifyItem.getCount());
          properties.put(notifyPrefix + "period", notifyItem.getPeriod());
          properties.put(notifyPrefix + "silencePeriod", notifyItem.getSilencePeriod());
          properties.put(notifyPrefix + "clusterLimit", notifyItem.getClusterLimit());
          properties.put(notifyPrefix + "receivers", notifyItem.getReceivers());

          // 处理通知平台ID列表和平台信息
          if (notifyItem.getPlatformIds() != null && !notifyItem.getPlatformIds().trim().isEmpty()) {
            properties.put(notifyPrefix + "platformIds", notifyItem.getPlatformIds());

            // 添加平台详细信息
            addPlatformDetailsToProperties(properties, notifyPrefix, notifyItem.getPlatformIds());
          }
        }
      }
    } catch (Exception e) {
      log.warn("获取线程池 {} 的通知配置失败: {}", threadPoolId, e.getMessage());
    }
  }

  /**
   * 将通知平台详细信息添加到properties中
   * 
   * @param properties   配置Map
   * @param notifyPrefix 通知配置前缀
   * @param platformIds  平台ID列表（JSON格式字符串）
   */
  private void addPlatformDetailsToProperties(Map<Object, Object> properties, String notifyPrefix,
      String platformIds) {
    try {
      // 解析平台ID列表（假设是JSON格式或逗号分隔）
      String[] platformIdArray = platformIds.split(",");

      for (int k = 0; k < platformIdArray.length; k++) {
        String platformId = platformIdArray[k].trim();
        if (!platformId.isEmpty()) {
          String platformPrefix = notifyPrefix + "platforms[" + k + "].";

          // 查询平台详细信息
          LambdaQueryWrapper<ManNotifyPlatform> platformQueryWrapper = new LambdaQueryWrapper<>();
          platformQueryWrapper.eq(ManNotifyPlatform::getPlatformId, platformId)
              .eq(ManNotifyPlatform::getStatus, "ENABLE");

          ManNotifyPlatform platform = manNotifyPlatformMapper.selectOne(platformQueryWrapper);

          if (platform != null) {
            properties.put(platformPrefix + "platformId", platform.getPlatformId());
            properties.put(platformPrefix + "platform", platform.getPlatform());
            properties.put(platformPrefix + "webhook", platform.getWebhook());
            properties.put(platformPrefix + "receivers", platform.getReceivers());
            properties.put(platformPrefix + "timeout", platform.getTimeout());
            properties.put(platformPrefix + "proxyType", platform.getProxyType());
            properties.put(platformPrefix + "proxyHost", platform.getProxyHost());
            properties.put(platformPrefix + "proxyPort", platform.getProxyPort());
          }
        }
      }
    } catch (Exception e) {
      log.warn("处理通知平台信息失败: {}", e.getMessage());
    }
  }
}
