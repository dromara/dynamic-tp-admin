package org.dromara.dynamictp.admin.modules.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonLogsErrorBO;
import org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsError;
import org.dromara.dynamictp.admin.modules.monitor.repository.mapper.MonLogsErrorMapper;
import org.dromara.dynamictp.admin.modules.monitor.service.IMonLogsErrorService;
import org.springframework.stereotype.Service;

/**
 * 错误异常日志 Service 服务接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsError
 * @CreateTime 2024-05-07
 */

@Service
public class MonLogsErrorServiceImpl extends ServiceImpl<MonLogsErrorMapper, MonLogsError> implements IMonLogsErrorService {
    @Override
    public IPage<MonLogsError> listMonLogsErrorPage(PageQuery pageQuery, MonLogsErrorBO monLogsErrorBO) {
        LambdaQueryWrapper<MonLogsError> queryWrapper = new LambdaQueryWrapper<MonLogsError>()
                .orderByDesc(MonLogsError::getCreateTime);
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }
}
