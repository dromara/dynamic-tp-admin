package org.dromara.dynamictp.admin.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.dromara.dynamictp.admin.common.exception.BizException;
import org.dromara.dynamictp.admin.common.pool.StringPools;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysRole;
import org.dromara.dynamictp.admin.modules.system.repository.mapper.SysRoleMapper;
import org.dromara.dynamictp.admin.modules.system.service.ISysRoleDataScopeService;
import org.dromara.dynamictp.admin.modules.system.service.ISysRoleMenuService;
import org.dromara.dynamictp.admin.modules.system.service.ISysRolePermissionService;
import org.dromara.dynamictp.admin.modules.system.service.ISysRoleService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

/**
 * 角色管理 Service 服务接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.impl.SysRoleServiceImpl
 * @CreateTime 2023-07-15
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    private final ISysRoleDataScopeService sysRoleDataScopeService;
    private final ISysRoleMenuService sysRoleMenuService;
    private final ISysRolePermissionService sysRolePermissionService;

    @Override
    public IPage<SysRole> listSysRolePage(PageQuery pageQuery, SysRoleBO sysRoleBO) {
        var queryWrapper = new LambdaQueryWrapper<SysRole>()
                .like(ObjectUtils.isNotEmpty(sysRoleBO.getRoleName()), SysRole::getRoleName, sysRoleBO.getRoleName())
                .like(ObjectUtils.isNotEmpty(sysRoleBO.getRoleCode()), SysRole::getRoleCode, sysRoleBO.getRoleCode())
                .eq(ObjectUtils.isNotEmpty(sysRoleBO.getStatus()), SysRole::getStatus, sysRoleBO.getStatus())
                .orderByAsc(SysRole::getSort);
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

    @Override
    public boolean removeBatchByIds(Collection<?> list) {
        // 查询要删除的角色信息
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<SysRole>().in(SysRole::getId, list);
        List<SysRole> rolesToDelete = baseMapper.selectList(queryWrapper);

        // 检查系统管理员角色
        rolesToDelete.stream()
                .filter(sysRole -> StringPools.ADMIN.equalsIgnoreCase(sysRole.getRoleCode()))
                .findFirst()
                .ifPresent(sysRole -> {
                    throw new BizException("系统管理员角色不允许删除");
                });

        // 检查角色关联数据
        rolesToDelete.forEach(role -> {
            Long roleId = role.getId();
            String roleName = role.getRoleName();

            // 检查角色菜单关联
            List<Long> menuIds = sysRoleMenuService.queryMenuIdsWithRoleId(roleId);
            if (!CollectionUtils.isEmpty(menuIds)) {
                throw new BizException("角色[%s]已配置菜单权限，不允许删除，请先撤销菜单权限配置".formatted(roleName));
            }

            // 检查角色按钮权限关联
            List<Long> permissionIds = sysRolePermissionService.queryPermissionIdsWithRoleId(roleId);
            if (!CollectionUtils.isEmpty(permissionIds)) {
                throw new BizException("角色[%s]已配置按钮权限，不允许删除，请先撤销按钮权限配置".formatted(roleName));
            }

            // 检查角色数据权限关联
            List<Long> dataScopeIds = sysRoleDataScopeService.listDataScopeIdsByRoleId(roleId);
            if (!CollectionUtils.isEmpty(dataScopeIds)) {
                throw new BizException("角色[%s]已配置数据权限，不允许删除，请先撤销数据权限配置".formatted(roleName));
            }
        });

        return super.removeByIds(list, true);
    }

    @Override
    public List<SysRoleBO> queryAllRoleList() {
        var queryWrapper = new LambdaQueryWrapper<SysRole>()
                .orderByAsc(SysRole::getSort);
        return CglibUtil.convertList(baseMapper.selectList(queryWrapper), SysRoleBO::new);
    }

    @Override
    public List<String> queryRoleCodesWithUserId(Long userId) {
        List<SysRole> sysRoles = baseMapper.queryRoleListWithUserId(userId);
        return sysRoles.stream().map(SysRole::getRoleCode).toList();
    }

    @Override
    public List<SysRoleBO> queryRoleListWithUserId(Long userId) {
        List<SysRole> sysRoles = baseMapper.queryRoleListWithUserId(userId);
        return CglibUtil.convertList(sysRoles, SysRoleBO::new);
    }
}
