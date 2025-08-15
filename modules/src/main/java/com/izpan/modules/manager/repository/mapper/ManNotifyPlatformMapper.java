package com.izpan.modules.manager.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.manager.domain.entity.ManNotifyPlatform;
import org.apache.ibatis.annotations.Mapper;

/**
 * 告警渠道管理 Mapper 接口层
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.manager.repository.mapper.ManNotifyPlatformMapper
 * @CreateTime 2025/01/30 - 10:00
 */
@Mapper
public interface ManNotifyPlatformMapper extends BaseMapper<ManNotifyPlatform> {
}
