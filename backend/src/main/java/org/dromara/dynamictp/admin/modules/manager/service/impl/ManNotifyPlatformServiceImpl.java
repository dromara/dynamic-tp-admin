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
import org.dromara.dynamictp.admin.modules.manager.domain.bo.ManNotifyPlatformBO;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyPlatform;
import org.dromara.dynamictp.admin.modules.manager.domain.vo.ManNotifyPlatformVO;
import org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManNotifyPlatformMapper;
import org.dromara.dynamictp.admin.modules.manager.service.IManNotifyPlatformService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * 告警渠道管理 Service 服务实现层
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.service.impl.ManNotifyPlatformServiceImpl
 * @CreateTime 2025/01/30 - 10:00
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ManNotifyPlatformServiceImpl extends ServiceImpl<ManNotifyPlatformMapper, ManNotifyPlatform>
    implements IManNotifyPlatformService {

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
  public Boolean refreshNotifyPlatform(String clientId) {
    // TODO: 实现刷新告警渠道到指定客户端的逻辑
    log.info("刷新告警渠道到指定客户端，clientId: {}", clientId);
    return true;
  }

  @Override
  public Boolean refreshAllNotifyPlatforms() {
    // TODO: 实现刷新所有客户端告警渠道的逻辑
    log.info("刷新所有客户端的告警渠道");
    return true;
  }

  @Override
  public List<ManNotifyPlatform> getByClientId(String clientId) {
    LambdaQueryWrapper<ManNotifyPlatform> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ManNotifyPlatform::getClientId, clientId);
    queryWrapper.orderByDesc(ManNotifyPlatform::getCreateTime);
    return this.list(queryWrapper);
  }

  @Override
  public List<ManNotifyPlatform> getByClientName(String clientName) {
    LambdaQueryWrapper<ManNotifyPlatform> queryWrapper = new LambdaQueryWrapper<>();
    queryWrapper.eq(ManNotifyPlatform::getClientName, clientName);
    queryWrapper.orderByDesc(ManNotifyPlatform::getCreateTime);
    return this.list(queryWrapper);
  }

  @Override
  public List<ManNotifyPlatformVO> getByClientIdVO(String clientId) {
    List<ManNotifyPlatform> list = this.getByClientId(clientId);
    return BeanUtil.copyToList(list, ManNotifyPlatformVO.class);
  }
}
