package org.dromara.dynamictp.admin.modules.manager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManNotifyPlatformBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyPlatform;
import org.dromara.dynamictp.admin.modules.manager.domain.vo.ManNotifyPlatformVO;

import java.util.List;

/**
 * 告警渠道管理 Service 服务接口层
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.service.IManNotifyPlatformService
 * @CreateTime 2025/01/30 - 10:00
 */
public interface IManNotifyPlatformService extends IService<ManNotifyPlatform> {

  /**
   * 告警渠道管理 - 分页查询
   *
   * @param pageQuery                   分页对象
   * @param manNotifyPlatformBO         BO 查询对象
   * @return {@link IPage} 分页结果
   */
  IPage<ManNotifyPlatform> listManagerNotifyPlatformPage(PageQuery pageQuery,
      ManNotifyPlatformBO manNotifyPlatformBO);

  /**
   * 告警渠道管理 - 新增
   *
   * @param manNotifyPlatformBO         BO 新增对象
   * @param manNotifyPlatformBO         BO 新增对象
   * @return {@link Boolean} 结果
   */
  Boolean addManagerNotifyPlatform(ManNotifyPlatformBO manNotifyPlatformBO);

  /**
   * 告警渠道管理 - 修改
   *
   * @param manNotifyPlatformBO         BO 修改对象
   * @return {@link Boolean} 结果
   */
  Boolean updateManagerNotifyPlatform(ManNotifyPlatformBO manNotifyPlatformBO);

  /**
   * 告警渠道管理 - 删除
   *
   * @param ids 主键集合
   * @return {@link Boolean} 结果
   */
  Boolean deleteManagerNotifyPlatform(List<Long> ids);

  /**
   * 告警渠道管理 - 详情
   *
   * @param id 主键
   * @return {@link ManNotifyPlatform} 详情
   */
  ManNotifyPlatform getManagerNotifyPlatform(Long id);

  /**
   * 告警渠道管理 - 详情（VO）
   *
   * @param id 主键
   * @return {@link ManNotifyPlatformVO} 详情
   */
  ManNotifyPlatformVO getManagerNotifyPlatformVO(Long id);

  /**
   * 刷新告警渠道到指定客户端
   *
   * @param clientId 客户端ID
   * @return {@link Boolean} 结果
   */
  Boolean refreshNotifyPlatform(String clientId);

  /**
   * 刷新所有客户端的告警渠道
   *
   * @return {@link Boolean} 结果
   */
  Boolean refreshAllNotifyPlatforms();

  /**
   * 根据客户端ID获取告警渠道
   *
   * @param clientId 客户端ID
   * @return {@link List<ManNotifyPlatform>} 告警渠道列表
   */
  List<ManNotifyPlatform> getByClientId(String clientId);

  /**
   * 根据客户端名称获取告警渠道
   *
   * @param clientName 客户端名称
   * @return {@link List<ManNotifyPlatform>} 告警渠道列表
   */
  List<ManNotifyPlatform> getByClientName(String clientName);

  /**
   * 根据客户端ID获取告警渠道（VO）
   *
   * @param clientId 客户端ID
   * @return {@link List<ManNotifyPlatformVO>} 告警渠道列表
   */
  List<ManNotifyPlatformVO> getByClientIdVO(String clientId);
}
