package org.dromara.dynamictp.admin.modules.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonLogsLoginBO;
import org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsLogin;
import org.dromara.dynamictp.admin.modules.monitor.repository.mapper.MonLogsLoginMapper;
import org.dromara.dynamictp.admin.modules.monitor.service.IMonLogsLoginService;
import org.springframework.stereotype.Service;

/**
 * 登录日志 Service 服务接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsLogin
 * @CreateTime 2024-05-05
 */

@Service
public class MonLogsLoginServiceImpl extends ServiceImpl<MonLogsLoginMapper, MonLogsLogin> implements IMonLogsLoginService {
    @Override
    public IPage<MonLogsLogin> listMonLogsLoginPage(PageQuery pageQuery, MonLogsLoginBO loginBO) {
        LambdaQueryWrapper<MonLogsLogin> queryWrapper = new LambdaQueryWrapper<MonLogsLogin>()
                .eq(ObjectUtils.isNotEmpty(loginBO.getUserName()), MonLogsLogin::getUserName, loginBO.getUserName())
                .eq(ObjectUtils.isNotEmpty(loginBO.getUserRealName()), MonLogsLogin::getUserRealName, loginBO.getUserRealName())
                .orderByDesc(MonLogsLogin::getCreateTime);
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

}
