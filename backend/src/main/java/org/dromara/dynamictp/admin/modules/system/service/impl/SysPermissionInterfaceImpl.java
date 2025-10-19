package org.dromara.dynamictp.admin.modules.system.service.impl;


import cn.dev33.satoken.stp.StpInterface;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.common.exception.BizException;
import org.dromara.dynamictp.admin.infrastructure.holder.GlobalUserHolder;
import org.dromara.dynamictp.admin.modules.system.service.ISysRolePermissionService;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义权限加载接口实现类
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.impl.SysPermissionInterfaceImpl
 * @CreateTime 2024/4/19 - 12:16
 */

@Slf4j
@Component
@AllArgsConstructor
public class SysPermissionInterfaceImpl implements StpInterface {

    @NonNull
    private ISysRolePermissionService sysRolePermissionService;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        if (GlobalUserHolder.getRoleIds().isEmpty()) {
            throw new BizException("当前用户角色为空，请联系管理员");
        }
        List<String> permissionList = Lists.newArrayList();
        GlobalUserHolder.getRoleIds().stream()
                .map(sysRolePermissionService::queryPermissionResourcesWithRoleId)
                .forEach(permissionList::addAll);
        return permissionList;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return GlobalUserHolder.getRoleCodes().stream().toList();
    }
}
