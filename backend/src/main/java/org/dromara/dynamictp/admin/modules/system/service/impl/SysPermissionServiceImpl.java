package org.dromara.dynamictp.admin.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Streams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.dromara.dynamictp.admin.common.constants.SystemCacheConstant;
import org.dromara.dynamictp.admin.common.exception.BizException;
import org.dromara.dynamictp.admin.common.pool.StringPools;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.util.RedisUtil;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysPermissionBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysPermission;
import org.dromara.dynamictp.admin.modules.system.repository.mapper.SysPermissionMapper;
import org.dromara.dynamictp.admin.modules.system.service.ISysPermissionService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 权限管理 Service 服务接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.entity.SysPermission
 * @CreateTime 2023-08-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {

    @Override
    public IPage<SysPermission> listSysPermissionPage(PageQuery pageQuery, SysPermissionBO sysPermissionBO) {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getMenuId, sysPermissionBO.getMenuId())
                .like(ObjectUtils.isNotEmpty(sysPermissionBO.getName()), SysPermission::getName, sysPermissionBO.getName())
                .orderByAsc(SysPermission::getSort);
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

    @Override
    public boolean add(SysPermissionBO sysPermissionBO) {
        LambdaQueryWrapper<SysPermission> eq = new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getResource, sysPermissionBO.getResource());
        SysPermission one = super.getOne(eq);
        if (ObjectUtils.isNotEmpty(one)) {
            throw new BizException("已存在相同权限资源 %s 按钮".formatted(sysPermissionBO.getResource()));
        }
        return super.save(sysPermissionBO);
    }

    @Override
    public List<SysPermissionBO> queryPermissionListWithRoleId(Long roleId) {
        if (ObjectUtils.isEmpty(roleId)) {
            return Collections.emptyList();
        }
        // 根据角色查找所有权限信息
        List<SysPermission> sysPermissions = baseMapper.queryPermissionListWithRoleId(roleId);
        return CglibUtil.convertList(sysPermissions, SysPermissionBO::new);
    }

    @Override
    public List<SysPermissionBO> queryPermissionListWithRoleIds(List<Long> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        // 根据角色查找所有权限信息
        List<SysPermission> sysPermissions = baseMapper.queryPermissionListWithRoleIds(roleIds);
        return CglibUtil.convertList(sysPermissions, SysPermissionBO::new);
    }

    @Override
    public List<SysPermissionBO> queryList() {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getStatus, StringPools.ONE);
        return CglibUtil.convertList(baseMapper.selectList(queryWrapper), SysPermissionBO::new);
    }

    @Override
    public void saveRolePermissionToCache(Long roleId, List<Long> permissionIds) {
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getStatus, StringPools.ONE)
                .in(SysPermission::getId, permissionIds);
        List<SysPermission> sysPermissions = baseMapper.selectList(queryWrapper);
        // 保存权限对象集合至缓存
        String rolePermissionListKey = SystemCacheConstant.rolePermissionListKey(roleId);
        RedisUtil.set(rolePermissionListKey, CglibUtil.convertList(sysPermissions, SysPermissionBO::new), 30L, TimeUnit.DAYS);
        // 提取角色权限资源
        List<String> permissionResources = Lists.newArrayList(sysPermissions.stream()
                .map(SysPermission::getResource)
                .flatMap(resource -> Streams.stream(Splitter.on(StringPools.SEMICOLON)
                        .trimResults().omitEmptyStrings().split(resource)))
                .distinct().toList());
        permissionResources.sort(String::compareTo);
        // 保存角色权限到缓存
        String rolePermissionKey = SystemCacheConstant.rolePermissionResourcesKey(roleId);
        RedisUtil.set(rolePermissionKey, permissionResources, 30L, TimeUnit.DAYS);
    }

    @Override
    public boolean deletePermissionWithMenuIds(List<Long> menuIds) {
        LambdaUpdateWrapper<SysPermission> deleteWrapper = new LambdaUpdateWrapper<SysPermission>()
                .set(SysPermission::getDeleted, 1)
                .in(SysPermission::getMenuId, menuIds);
        return super.update(deleteWrapper);
    }
}
