/*
 * All Rights Reserved: Copyright [2024] [Zhuang Pan (paynezhuang@gmail.com)]
 * Open Source Agreement: Apache License, Version 2.0
 * For educational purposes only, commercial use shall comply with the author's copyright information.
 * The author does not guarantee or assume any responsibility for the risks of using software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.dynamictp.admin.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.Db;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.common.util.CollectionUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleDataScopeBO;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleDataScopeQueryBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysRoleDataScope;
import org.dromara.dynamictp.admin.modules.system.repository.mapper.SysRoleDataScopeMapper;
import org.dromara.dynamictp.admin.modules.system.service.ISysRoleDataScopeService;
import org.dromara.dynamictp.admin.modules.system.util.DataScopeCacheManager;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * 角色数据权限关联管理 Service 服务接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.impl.SysRoleDataScopeServiceImpl
 * @CreateTime 2025-05-10 - 21:40:18
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class SysRoleDataScopeServiceImpl extends ServiceImpl<SysRoleDataScopeMapper, SysRoleDataScope> implements ISysRoleDataScopeService {

    @NonNull
    private DataScopeCacheManager dataScopeCacheManager;

    @Override
    public IPage<SysRoleDataScope> listSysRoleDataScopePage(PageQuery pageQuery, SysRoleDataScopeBO sysRoleDataScopeBO) {
        LambdaQueryWrapper<SysRoleDataScope> queryWrapper = new LambdaQueryWrapper<>();
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

    @Override
    public List<SysRoleDataScopeQueryBO> listByPermissionCode(String permissionCode) {
        return baseMapper.listByPermissionCode(permissionCode);
    }

    @Override
    public List<Long> listDataScopeIdsByRoleId(Long roleId) {
        // 根据角色ID查询已配置且启用的数据权限
        LambdaQueryWrapper<SysRoleDataScope> wrapper = new LambdaQueryWrapper<SysRoleDataScope>()
                .eq(SysRoleDataScope::getRoleId, roleId)
                .select(SysRoleDataScope::getDataScopeId);

        // 提取数据权限ID列表
        return baseMapper.selectList(wrapper).stream()
                .map(SysRoleDataScope::getDataScopeId)
                .toList();
    }

    @Override
    public boolean addDataScopeForRoleId(Long roleId, List<Long> dataScopeIds) {
        // 查询原有的角色数据权限关联
        LambdaQueryWrapper<SysRoleDataScope> queryWrapper = new LambdaQueryWrapper<SysRoleDataScope>()
                .eq(SysRoleDataScope::getRoleId, roleId);
        List<SysRoleDataScope> originSysRoleDataScopes = baseMapper.selectList(queryWrapper);

        // 提取原有的数据权限ID集合
        Set<Long> originDataScopeIdSet = originSysRoleDataScopes.stream()
                .map(SysRoleDataScope::getDataScopeId)
                .collect(Collectors.toSet());

        // 前端传输的数据权限ID集合，转换为Set
        Set<Long> dataScopeIdSet = Sets.newHashSet(dataScopeIds);

        // 避免前端错误传值，移除无效ID
        dataScopeIdSet.remove(0L);
        dataScopeIdSet.remove(null);

        // 处理结果标识
        AtomicBoolean saveBatch = new AtomicBoolean(true);

        // 使用差异处理工具处理增删操作
        CollectionUtil.handleDifference(
                originDataScopeIdSet,
                dataScopeIdSet,
                // 处理增加和删除的数据权限
                (addDataScopeIdSet, removeDataScopeIdSet) -> {
                    // 如有删除，则进行删除数据
                    if (!CollectionUtils.isEmpty(removeDataScopeIdSet)) {
                        LambdaQueryWrapper<SysRoleDataScope> removeQueryWrapper = new LambdaQueryWrapper<SysRoleDataScope>()
                                .eq(SysRoleDataScope::getRoleId, roleId)
                                .in(SysRoleDataScope::getDataScopeId, removeDataScopeIdSet);
                        baseMapper.delete(removeQueryWrapper);
                    }

                    // 如有新增，则进行新增数据
                    if (!CollectionUtils.isEmpty(addDataScopeIdSet)) {
                        // 构建新增的角色数据权限关联列表
                        List<SysRoleDataScope> addRoleDataScopeList = addDataScopeIdSet.stream()
                                .map(dataScopeId -> new SysRoleDataScope(roleId, dataScopeId))
                                .toList();

                        // 批量保存新增数据
                        saveBatch.set(Db.saveBatch(addRoleDataScopeList));
                    }
                }
        );

        // 使用缓存管理工具异步清理角色缓存
        if (saveBatch.get()) {
            // 角色数据权限配置变更，异步清理该角色关联用户的缓存
            dataScopeCacheManager.invalidateRoleCacheAsync(this, roleId, "角色数据权限配置变更");
        }

        return saveBatch.get();
    }
}

