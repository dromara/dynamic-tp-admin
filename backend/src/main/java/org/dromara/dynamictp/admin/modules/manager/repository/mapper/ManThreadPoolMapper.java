package org.dromara.dynamictp.admin.modules.manager.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManThreadPool;

/**
 * 线程池管理 Mapper 接口层
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManThreadPoolMapper
 * @CreateTime 2025/01/30 - 10:00
 */
@Mapper
public interface ManThreadPoolMapper extends BaseMapper<ManThreadPool> {
}