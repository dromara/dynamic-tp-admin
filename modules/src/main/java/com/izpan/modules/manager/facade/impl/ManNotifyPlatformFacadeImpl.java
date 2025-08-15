package com.izpan.modules.manager.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.izpan.infrastructure.page.PageQuery;
import com.izpan.modules.manager.domain.bo.ManNotifyPlatformBO;
import com.izpan.modules.manager.domain.entity.ManNotifyPlatform;
import com.izpan.modules.manager.domain.vo.ManNotifyPlatformVO;
import com.izpan.modules.manager.facade.IManNotifyPlatformFacade;
import com.izpan.modules.manager.service.IManNotifyPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 告警渠道管理 门面实现层
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.manager.facade.impl.ManNotifyPlatformFacadeImpl
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
  public Boolean refreshNotifyPlatform(String clientId) {
    return manNotifyPlatformService.refreshNotifyPlatform(clientId);
  }

  @Override
  public Boolean refreshAllNotifyPlatforms() {
    return manNotifyPlatformService.refreshAllNotifyPlatforms();
  }

  @Override
  public List<ManNotifyPlatform> getByClientId(String clientId) {
    return manNotifyPlatformService.getByClientId(clientId);
  }

  @Override
  public List<ManNotifyPlatform> getByClientName(String clientName) {
    return manNotifyPlatformService.getByClientName(clientName);
  }

  @Override
  public List<ManNotifyPlatformVO> getByClientIdVO(String clientId) {
    return manNotifyPlatformService.getByClientIdVO(clientId);
  }
}
