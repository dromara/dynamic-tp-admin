package com.izpan.modules.manager.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.manager.domain.entity.ManClient;
import org.apache.ibatis.annotations.Mapper;

/**
 * 客户端管理 Mapper 接口
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.manager.repository.mapper.ManClientMapper
 * @CreateTime 2025/01/30 - 10:00
 */
@Mapper
public interface ManClientMapper extends BaseMapper<ManClient> {
}