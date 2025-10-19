package org.dromara.dynamictp.admin.modules.manager.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManThreadPoolBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManThreadPool;
import org.dromara.dynamictp.admin.modules.manager.domain.vo.ManThreadPoolVO;

import java.util.List;

/**
 * 线程池管理 Service 服务接口层
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.manager.service.IManThreadPoolService
 * @CreateTime 2025/01/30 - 10:00
 */
public interface IManThreadPoolService extends IService<ManThreadPool> {

  /**
   * 线程池管理 - 分页查询
   *
   * @param pageQuery           分页对象
   * @param managerThreadPoolBO BO 查询对象
   * @return {@link IPage} 分页结果
   */
  IPage<ManThreadPool> listManagerThreadPoolPage(PageQuery pageQuery,
      ManThreadPoolBO managerThreadPoolBO);

  /**
   * 线程池管理 - 新增
   *
   * @param managerThreadPoolBO BO 新增对象
   * @return {@link Boolean} 结果
   */
  Boolean addManagerThreadPool(ManThreadPoolBO managerThreadPoolBO);

  /**
   * 线程池管理 - 修改
   *
   * @param managerThreadPoolBO BO 修改对象
   * @return {@link Boolean} 结果
   */
  Boolean updateManagerThreadPool(ManThreadPoolBO managerThreadPoolBO);

  /**
   * 线程池管理 - 删除
   *
   * @param ids 主键集合
   * @return {@link Boolean} 结果
   */
  Boolean deleteManagerThreadPool(List<Long> ids);

  /**
   * 线程池管理 - 详情
   *
   * @param id 主键
   * @return {@link ManThreadPool} 详情
   */
  ManThreadPool getManagerThreadPool(Long id);

  /**
   * 线程池管理 - 详情（VO）
   *
   * @param id 主键
   * @return {@link ManThreadPoolVO} 详情
   */
  ManThreadPoolVO getManagerThreadPoolVO(Long id);

  /**
   * 刷新线程池到指定客户端
   *
   * @param clientId 客户端ID
   * @return {@link Boolean} 结果
   */
  Boolean refreshThreadPool(String clientId);

  /**
   * 刷新所有客户端的线程池
   *
   * @return {@link Boolean} 结果
   */
  Boolean refreshAllThreadPools();

  /**
   * 根据客户端ID获取线程池
   *
   * @param clientId 客户端ID
   * @return {@link List< ManThreadPool >} 线程池列表
   */
  List<ManThreadPool> getByClientId(String clientId);

  /**
   * 根据客户端名称或客户端服务名称获取线程池
   * 支持两种格式：
   * 1. clientName - 纯客户端名称
   * 2. clientServiceName - 客户端服务名称（格式：clientName:serviceName）
   *
   * @param clientServiceName 客户端名称或客户端服务名称
   * @return {@link List< ManThreadPool >} 线程池列表
   */
  List<ManThreadPool> getByClientServiceName(String clientServiceName);

  /**
   * 根据客户端ID获取线程池（VO）
   *
   * @param clientId 客户端ID
   * @return {@link List< ManThreadPoolVO >} 线程池列表
   */
  List<ManThreadPoolVO> getByClientIdVO(String clientId);

}