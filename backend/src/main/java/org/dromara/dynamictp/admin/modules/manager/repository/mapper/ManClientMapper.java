package org.dromara.dynamictp.admin.modules.manager.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManClient;

/**
 * 客户端管理 Mapper 接口
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManClientMapper
 * @CreateTime 2025/01/30 - 10:00
 */
@Mapper
public interface ManClientMapper extends BaseMapper<ManClient> {
}