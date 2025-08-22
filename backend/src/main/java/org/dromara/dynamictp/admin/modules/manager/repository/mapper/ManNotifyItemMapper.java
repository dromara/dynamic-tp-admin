package org.dromara.dynamictp.admin.modules.manager.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyItem;

/**
 * 线程池通知配置 Mapper 接口层
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.repository.mapper.ManNotifyItemMapper
 * @CreateTime 2025/01/30 - 10:00
 */
@Mapper
public interface ManNotifyItemMapper extends BaseMapper<ManNotifyItem> {
}
