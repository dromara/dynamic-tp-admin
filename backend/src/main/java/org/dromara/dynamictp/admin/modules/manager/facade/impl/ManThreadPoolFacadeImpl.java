package org.dromara.dynamictp.admin.modules.manager.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManThreadPoolBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManThreadPool;
import org.dromara.dynamictp.admin.modules.manager.domain.vo.ManThreadPoolVO;
import org.dromara.dynamictp.admin.modules.manager.facade.IManThreadPoolFacade;
import org.dromara.dynamictp.admin.modules.manager.service.IManThreadPoolService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 线程池管理 门面接口实现层
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.facade.impl.ManThreadPoolFacadeImpl
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManThreadPoolFacadeImpl implements IManThreadPoolFacade {

  @NonNull
  private IManThreadPoolService managerThreadPoolService;

  @Override
  public IPage<ManThreadPool> listManagerThreadPoolPage(PageQuery pageQuery,
      ManThreadPoolBO managerThreadPoolBO) {
    log.info("线程池管理 - 分页查询，参数：pageQuery={}, managerThreadPoolBO={}", pageQuery, managerThreadPoolBO);
    return managerThreadPoolService.listManagerThreadPoolPage(pageQuery, managerThreadPoolBO);
  }

  @Override
  public Boolean addManagerThreadPool(ManThreadPoolBO managerThreadPoolBO) {
    log.info("线程池管理 - 新增，参数：managerThreadPoolBO={}", managerThreadPoolBO);
    return managerThreadPoolService.addManagerThreadPool(managerThreadPoolBO);
  }

  @Override
  public Boolean updateManagerThreadPool(ManThreadPoolBO managerThreadPoolBO) {
    log.info("线程池管理 - 修改，参数：managerThreadPoolBO={}", managerThreadPoolBO);
    return managerThreadPoolService.updateManagerThreadPool(managerThreadPoolBO);
  }

  @Override
  public Boolean deleteManagerThreadPool(List<Long> ids) {
    log.info("线程池管理 - 删除，参数：ids={}", ids);
    return managerThreadPoolService.deleteManagerThreadPool(ids);
  }

  @Override
  public ManThreadPool getManagerThreadPool(Long id) {
    log.info("线程池管理 - 详情，参数：id={}", id);
    return managerThreadPoolService.getManagerThreadPool(id);
  }

  @Override
  public ManThreadPoolVO getManagerThreadPoolVO(Long id) {
    log.info("线程池管理 - 详情（VO），参数：id={}", id);
    return managerThreadPoolService.getManagerThreadPoolVO(id);
  }

  @Override
  public Boolean refreshThreadPool(String clientId) {
    log.info("刷新线程池到指定客户端，参数：clientId={}", clientId);
    return managerThreadPoolService.refreshThreadPool(clientId);
  }

  @Override
  public Boolean refreshAllThreadPools() {
    log.info("刷新所有客户端的线程池");
    return managerThreadPoolService.refreshAllThreadPools();
  }

  @Override
  public List<ManThreadPool> getByClientId(String clientId) {
    log.info("根据客户端ID获取线程池，参数：clientId={}", clientId);
    return managerThreadPoolService.getByClientId(clientId);
  }

  @Override
  public List<ManThreadPool> getByClientName(String clientName) {
    log.info("根据客户端名称获取线程池，参数：clientName={}", clientName);
    return managerThreadPoolService.getByClientName(clientName);
  }

  @Override
  public List<ManThreadPoolVO> getByClientIdVO(String clientId) {
    log.info("根据客户端ID获取线程池（VO），参数：clientId={}", clientId);
    return managerThreadPoolService.getByClientIdVO(clientId);
  }
}