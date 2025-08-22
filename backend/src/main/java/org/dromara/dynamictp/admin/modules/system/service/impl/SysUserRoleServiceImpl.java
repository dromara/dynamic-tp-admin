package org.dromara.dynamictp.admin.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.util.CollectionUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysUserRoleBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysUserRole;
import org.dromara.dynamictp.admin.modules.system.repository.mapper.SysUserRoleMapper;
import org.dromara.dynamictp.admin.modules.system.service.ISysRoleService;
import org.dromara.dynamictp.admin.modules.system.service.ISysUserRoleService;
import org.dromara.dynamictp.admin.modules.system.util.DataScopeCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 用户角色管理 Service 服务接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.impl.SysUserRoleServiceImpl
 * @CreateTime 2023-07-24
 */
@Service
@RequiredArgsConstructor
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleMapper, SysUserRole> implements ISysUserRoleService {

    @NonNull
    private ISysRoleService sysRoleService;
    
    private final DataScopeCacheManager dataScopeCacheManager;

    @Override
    public IPage<SysUserRole> listSysUserRolePage(PageQuery pageQuery, SysUserRoleBO sysUserRoleBO) {
        return baseMapper.selectPage(pageQuery.buildPage(), new LambdaQueryWrapper<>());
    }

    @Override
    public List<Long> queryRoleIdsWithUserId(Long userId) {
        List<SysUserRole> sysUserRoleList = baseMapper.listUserRoleByUserId(userId);
        return sysUserRoleList.stream().map(SysUserRole::getRoleId).toList();
    }

    @Override
    public List<String> queryRoleCodesWithUserId(Long userId) {
        return sysRoleService.queryRoleCodesWithUserId(userId);
    }

    @Override
    public boolean updateUserRole(Long userId, List<Long> roleIds) {
        List<Long> originUserRoleIds = queryRoleIdsWithUserId(userId);
        // 处理数据
        Set<Long> roleIdSet = Sets.newHashSet(roleIds);
        // 处理结果
        AtomicBoolean saveResult = new AtomicBoolean(true);
        CollectionUtil.handleDifference(
                Sets.newHashSet(originUserRoleIds),
                roleIdSet,
                // 处理增加和删除的数据
                (addRoleIdSet, removeRoleIdSet) -> {
                    // 如有删除，则进行删除数据
                    if (!CollectionUtils.isEmpty(removeRoleIdSet)) {
                        LambdaQueryWrapper<SysUserRole> removeQueryWrapper = new LambdaQueryWrapper<SysUserRole>()
                                .eq(SysUserRole::getUserId, userId)
                                .in(SysUserRole::getRoleId, removeRoleIdSet);
                        baseMapper.delete(removeQueryWrapper);
                    }
                    // 进行新增数据
                    if (!CollectionUtils.isEmpty(addRoleIdSet)) {
                        List<SysUserRole> sysUserRoleList = addRoleIdSet.stream()
                                .map(roleId -> new SysUserRole(userId, roleId))
                                .toList();
                        saveResult.set(Db.saveBatch(sysUserRoleList));
                    }
                }
        );
        
        // 用户角色关系变更，异步清理该用户的数据权限缓存
        if (saveResult.get()) {
            dataScopeCacheManager.invalidateUserCacheAsync(this, userId, "用户角色关系变更");
        }
        
        return saveResult.get();
    }

    @Override
    public List<Long> listUserIdsByRoleIds(Set<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }

        // 查询拥有指定角色的用户ID列表
        LambdaQueryWrapper<SysUserRole> queryWrapper = new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getRoleId, roleIds)
                .select(SysUserRole::getUserId);

        return baseMapper.selectList(queryWrapper).stream()
                .map(SysUserRole::getUserId)
                .distinct() // 去重，因为一个用户可能拥有多个指定的角色
                .toList();
    }
}
