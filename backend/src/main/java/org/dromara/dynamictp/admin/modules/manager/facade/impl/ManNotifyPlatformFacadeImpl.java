package org.dromara.dynamictp.admin.modules.manager.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManNotifyPlatformBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyPlatform;
import org.dromara.dynamictp.admin.modules.manager.domain.vo.ManNotifyPlatformVO;
import org.dromara.dynamictp.admin.modules.manager.facade.IManNotifyPlatformFacade;
import org.dromara.dynamictp.admin.modules.manager.service.IManNotifyPlatformService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 告警渠道管理 门面实现层
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.manager.facade.impl.ManNotifyPlatformFacadeImpl
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManNotifyPlatformFacadeImpl implements IManNotifyPlatformFacade {

  private final IManNotifyPlatformService manNotifyPlatformService;

  @Override
  public IPage<ManNotifyPlatform> listManagerNotifyPlatformPage(PageQuery pageQuery,
      ManNotifyPlatformBO manNotifyPlatformBO) {
    return manNotifyPlatformService.listManagerNotifyPlatformPage(pageQuery, manNotifyPlatformBO);
  }

  @Override
  public Boolean addManagerNotifyPlatform(ManNotifyPlatformBO manNotifyPlatformBO) {
    return manNotifyPlatformService.addManagerNotifyPlatform(manNotifyPlatformBO);
  }

  @Override
  public Boolean updateManagerNotifyPlatform(ManNotifyPlatformBO manNotifyPlatformBO) {
    return manNotifyPlatformService.updateManagerNotifyPlatform(manNotifyPlatformBO);
  }

  @Override
  public Boolean deleteManagerNotifyPlatform(List<Long> ids) {
    return manNotifyPlatformService.deleteManagerNotifyPlatform(ids);
  }

  @Override
  public ManNotifyPlatform getManagerNotifyPlatform(Long id) {
    return manNotifyPlatformService.getManagerNotifyPlatform(id);
  }

  @Override
  public ManNotifyPlatformVO getManagerNotifyPlatformVO(Long id) {
    return manNotifyPlatformService.getManagerNotifyPlatformVO(id);
  }

  @Override
  public Boolean refreshNotifyPlatform(String clientServiceName) {
    return manNotifyPlatformService.refreshNotifyPlatform(clientServiceName);
  }

  @Override
  public Boolean refreshAllNotifyPlatforms() {
    return manNotifyPlatformService.refreshAllNotifyPlatforms();
  }

  @Override
  public List<ManNotifyPlatform> getByClientServiceName(String clientServiceName) {
    return manNotifyPlatformService.getByClientServiceName(clientServiceName);
  }

  @Override
  public List<ManNotifyPlatform> getByClientName(String clientServiceName) {
    return manNotifyPlatformService.getByClientName(clientServiceName);
  }

  @Override
  public List<ManNotifyPlatformVO> getByClientServiceVO(String clientServiceName) {
    return manNotifyPlatformService.getByClientServiceVO(clientServiceName);
  }
}
