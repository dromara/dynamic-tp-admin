package org.dromara.dynamictp.admin.infrastructure.server.handler;

import java.util.Map;

/**
 * 线程池刷新处理器接口
 * 用于处理线程池配置的刷新逻辑
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.infrastructure.server.handler.PropertiesHandler
 * @CreateTime 2025/01/30 - 10:00
 */
public interface PropertiesHandler {

  /**
   * 将线程池配置转换为Map格式
   * 
   * @param clientServiceName 客户端服务名
   * @return 配置Map
   */
  Map<Object, Object> convertConfigsToMap(String clientServiceName);
}
