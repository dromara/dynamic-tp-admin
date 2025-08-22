package org.dromara.dynamictp.admin.modules.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonLogsOperationBO;
import org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsOperation;
import org.dromara.dynamictp.admin.modules.monitor.repository.mapper.MonLogsOperationMapper;
import org.dromara.dynamictp.admin.modules.monitor.service.IMonLogsOperationService;
import org.springframework.stereotype.Service;

/**
 * 操作日志 Service 服务接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsOperation
 * @CreateTime 2024-05-07
 */

@Service
public class MonLogsOperationServiceImpl extends ServiceImpl<MonLogsOperationMapper, MonLogsOperation> implements IMonLogsOperationService {
    @Override
    public IPage<MonLogsOperation> listMonLogsOperationPage(PageQuery pageQuery, MonLogsOperationBO monLogsOperationBO) {
        LambdaQueryWrapper<MonLogsOperation> queryWrapper = new LambdaQueryWrapper<MonLogsOperation>()
                .eq(ObjectUtils.isNotEmpty(monLogsOperationBO.getCreateUser()), MonLogsOperation::getCreateUser, monLogsOperationBO.getCreateUser())
                .orderByDesc(MonLogsOperation::getCreateTime);
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }
}
