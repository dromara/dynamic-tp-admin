package org.dromara.dynamictp.admin.modules.manager.facade;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManClientBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManClient;

import java.util.List;

/**
 * 客户端管理 门面接口层
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.facade.IManClientFacade
 * @CreateTime 2025/01/30 - 10:00
 */
public interface IManClientFacade {

  /**
   * 客户端管理 - 分页查询
   *
   * @param pageQuery   分页对象
   * @param manClientBO BO 查询对象
   * @return {@link IPage} 分页结果
   */
  IPage<ManClient> listManagerClientPage(PageQuery pageQuery, ManClientBO manClientBO);

  /**
   * 客户端管理 - 新增
   *
   * @param manClientBO BO 新增对象
   * @return {@link Boolean} 结果
   */
  Boolean addManagerClient(ManClientBO manClientBO);

  /**
   * 客户端管理 - 修改
   *
   * @param manClientBO BO 修改对象
   * @return {@link Boolean} 结果
   */
  Boolean updateManagerClient(ManClientBO manClientBO);

  /**
   * 客户端管理 - 删除
   *
   * @param ids 主键集合
   * @return {@link Boolean} 结果
   */
  Boolean deleteManagerClient(List<Long> ids);

  /**
   * 客户端管理 - 详情
   *
   * @param id 主键
   * @return {@link ManClient} 详情
   */
  ManClient getManagerClient(Long id);

  /**
   * 获取所有在线的客户端
   *
   * @return {@link List<ManClient>} 在线客户端列表
   */
  List<ManClient> getOnlineClients();

  /**
   * 更新客户端连接时间
   *
   * @param clientName 客户端名称
   * @return {@link Boolean} 结果
   */
  Boolean updateConnectTime(String clientName);

  /**
   * 更新客户端断开时间
   *
   * @param clientName 客户端名称
   * @return {@link Boolean} 结果
   */
  Boolean updateDisconnectTime(String clientName);

  /**
   * 更新客户端心跳时间
   *
   * @param clientName 客户端名称
   * @return {@link Boolean} 结果
   */
  Boolean updateHeartbeatTime(String clientName);

  /**
   * 检查客户端状态
   *
   * @param clientName 客户端名
   * @return {@link Boolean} 客户端是否正常
   */
  Boolean checkClientStatus(String clientName);

  /**
   * 标记客户端为离线
   *
   * @param clientName 客户端名称
   * @return {@link Boolean} 结果
   */
  Boolean markClientAsOffline(String clientName);

  /**
   * 获取无响应的客户端列表
   *
   * @return {@link List<String>} 无响应客户端ID列表
   */
  List<String> getUnresponsiveClients();

  /**
   * 处理客户端连接 - 检查并更新客户端数据
   * 当客户端连接时，首先检查数据库是否有相关数据
   * 若没有则新增客户端数据，若有则更新需要更新的字段
   *
   * @param clientId      客户端ID
   * @param clientName    客户端名称
   * @param clientType    客户端类型
   * @param clientVersion 客户端版本
   * @param clientIp      客户端IP地址
   * @param clientPort    客户端端口
   * @param serverIp      服务端IP地址
   * @param serverPort    服务端端口
   * @return {@link Boolean} 处理结果
   */
  Boolean handleClientConnection(String clientId, String clientName, String clientType,
      String clientVersion, String clientIp, Integer clientPort,
      String serverIp, Integer serverPort);
}