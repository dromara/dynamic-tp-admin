package com.izpan.modules.manager.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.izpan.modules.manager.domain.entity.ManThreadPool;
import org.apache.ibatis.annotations.Mapper;

/**
 * 线程池管理 Mapper 接口层
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName com.izpan.modules.manager.repository.mapper.ManThreadPoolMapper
 * @CreateTime 2025/01/30 - 10:00
 */
@Mapper
public interface ManThreadPoolMapper extends BaseMapper<ManThreadPool> {
}