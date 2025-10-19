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

package org.dromara.dynamictp.admin.modules.system.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.infrastructure.enums.DataScopeVariableEnum;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.infrastructure.util.GsonUtil;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysDataScopeBO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysDataScope;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeEditVO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeTreeVO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysDataScopeFacade;
import org.dromara.dynamictp.admin.modules.system.service.ISysDataScopeService;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.domain.DataScopeCondition;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.enums.QueryConditionsEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据权限管理 门面接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.facade.impl.SysDataScopeFacadeImpl
 * @CreateTime 2025-05-10 - 21:38:29
 */

@Service
@RequiredArgsConstructor
public class SysDataScopeFacadeImpl implements ISysDataScopeFacade {

    @NonNull
    private ISysDataScopeService sysDataScopeService;

    @Override
    public RPage<SysDataScopeVO> listSysDataScopePage(PageQuery pageQuery, SysDataScopeSearchDTO sysDataScopeSearchDTO) {
        SysDataScopeBO sysDataScopeBO = CglibUtil.convertObj(sysDataScopeSearchDTO, SysDataScopeBO::new);
        IPage<SysDataScope> sysDataScopeIPage = sysDataScopeService.listSysDataScopePage(pageQuery, sysDataScopeBO);
        return RPage.build(sysDataScopeIPage, SysDataScopeVO::new);
    }

    @Override
    public SysDataScopeEditVO get(Long id) {
        SysDataScope byId = sysDataScopeService.getById(id);
        SysDataScopeEditVO editVO = CglibUtil.convertObj(byId, SysDataScopeEditVO::new);
        editVO.setCustomRules(GsonUtil.fromJsonList(byId.getCustomRules(), DataScopeCondition.class));
        return editVO;
    }

    @Override
    @Transactional
    public boolean add(SysDataScopeAddDTO sysDataScopeAddDTO) {
        SysDataScopeBO sysDataScopeBO = CglibUtil.convertObj(sysDataScopeAddDTO, SysDataScopeBO::new);
        sysDataScopeBO.setCustomRules(GsonUtil.toJson(sysDataScopeAddDTO.getCustomRules()));
        return sysDataScopeService.add(sysDataScopeBO);
    }

    @Override
    @Transactional
    public boolean update(SysDataScopeUpdateDTO sysDataScopeUpdateDTO) {
        SysDataScopeBO sysDataScopeBO = CglibUtil.convertObj(sysDataScopeUpdateDTO, SysDataScopeBO::new);
        sysDataScopeBO.setCustomRules(GsonUtil.toJson(sysDataScopeUpdateDTO.getCustomRules()));
        return sysDataScopeService.update(sysDataScopeBO);
    }

    @Override
    @Transactional
    public boolean batchDelete(SysDataScopeDeleteDTO sysDataScopeDeleteDTO) {
        SysDataScopeBO sysDataScopeBO = CglibUtil.convertObj(sysDataScopeDeleteDTO, SysDataScopeBO::new);
        return sysDataScopeService.removeBatchByIds(sysDataScopeBO.getIds(), true);
    }

    @Override
    public List<SysDataScopeTreeVO> queryAllDataScopeListConvertToTree() {
        // 查询所有可用的数据权限
        List<SysDataScope> sysDataScopes = sysDataScopeService.listSysDataScope(SysDataScopeBO.builder().build());

        // 检查数据是否为空
        if (sysDataScopes.isEmpty()) {
            return Collections.emptyList();
        }

        // 过滤必要字段为空的数据
        List<SysDataScope> validDataScopes = sysDataScopes.stream()
                .filter(scope -> null != scope.getMenuId() && null != scope.getPermissionId())
                .toList();

        // 如果过滤后没有有效数据，返回空列表
        if (validDataScopes.isEmpty()) {
            return Collections.emptyList();
        }

        // 按菜单ID分组，构建菜单级别的数据结构
        Map<Long, List<SysDataScope>> menuGroupMap = validDataScopes.stream()
                .collect(Collectors.groupingBy(SysDataScope::getMenuId));

        // 构建树形结构：遍历每个菜单组
        return menuGroupMap.entrySet().stream()
                .map(this::buildMenuTreeNode)
                .sorted(Comparator.comparing(SysDataScopeTreeVO::getMenuId))
                .toList();
    }

    /**
     * 构建菜单树节点
     *
     * @param menuEntry 菜单分组条目
     * @return 菜单树节点
     */
    private SysDataScopeTreeVO buildMenuTreeNode(Map.Entry<Long, List<SysDataScope>> menuEntry) {
        Long menuId = menuEntry.getKey();
        List<SysDataScope> menuDataScopes = menuEntry.getValue();

        // 获取菜单名称（同一菜单组的菜单名称相同）
        String menuName = menuDataScopes.getFirst().getMenuName();

        // 在当前菜单组内，按权限资源ID分组
        Map<Long, List<SysDataScope>> permissionGroupMap = menuDataScopes.stream()
                .collect(Collectors.groupingBy(SysDataScope::getPermissionId));

        // 构建权限资源集合
        List<SysDataScopeTreeVO.Permission> permissions = buildPermissionList(permissionGroupMap);

        // 构建菜单级别的树形对象
        return SysDataScopeTreeVO.builder()
                .menuId(menuId)
                .menuName(menuName)
                .children(permissions)
                .build();
    }

    /**
     * 构建权限资源列表
     *
     * @param permissionGroupMap 权限资源分组Map
     * @return 权限资源列表
     */
    private List<SysDataScopeTreeVO.Permission> buildPermissionList(Map<Long, List<SysDataScope>> permissionGroupMap) {
        return permissionGroupMap.entrySet().stream()
                .map(this::buildPermissionNode)
                .sorted(Comparator.comparing(SysDataScopeTreeVO.Permission::getPermissionId))
                .toList();
    }

    /**
     * 构建权限资源节点
     *
     * @param permissionEntry 权限资源分组条目
     * @return 权限资源节点
     */
    private SysDataScopeTreeVO.Permission buildPermissionNode(Map.Entry<Long, List<SysDataScope>> permissionEntry) {
        Long permissionId = permissionEntry.getKey();
        List<SysDataScope> permissionDataScopes = permissionEntry.getValue();

        // 获取权限资源信息（同一权限组的信息相同）
        SysDataScope firstPermissionScope = permissionDataScopes.getFirst();
        String permissionResource = firstPermissionScope.getPermissionResource();
        String permissionName = firstPermissionScope.getPermissionName();

        // 构建数据权限规则集合
        List<SysDataScopeTreeVO.Scope> scopes = buildScopeList(permissionDataScopes);

        // 构建权限资源对象
        return SysDataScopeTreeVO.Permission.builder()
                .permissionId(permissionId)
                .permissionResource(permissionResource)
                .permissionName(permissionName)
                .children(scopes)
                .build();
    }

    /**
     * 构建数据权限规则列表
     *
     * @param permissionDataScopes 权限数据范围列表
     * @return 数据权限规则列表
     */
    private List<SysDataScopeTreeVO.Scope> buildScopeList(List<SysDataScope> permissionDataScopes) {
        return permissionDataScopes.stream()
                .map(scope -> SysDataScopeTreeVO.Scope.builder()
                        .id(scope.getId())
                        .name(scope.getName())
                        .code(scope.getCode())
                        .sort(scope.getSort())
                        .scopeType(scope.getScopeType())
                        .build())
                // 按排序值升序排列，空值排在最后
                .sorted(Comparator.comparing(SysDataScopeTreeVO.Scope::getSort,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, Object> getVariableConditions() {
        DataScopeVariableEnum[] values = DataScopeVariableEnum.values();
        List<Map<String, Serializable>> builtInVariablesList = Arrays.stream(values)
                .map(variable -> Map.of(
                        "code", variable.getCode(),
                        "name", variable.getName(),
                        "conditions", variable.getOptionalConditions().stream()
                                .map(QueryConditionsEnum::getCode)
                                .toArray(String[]::new)
                ))
                .toList();

        // 遍历所有 QueryConditionsEnum
        List<Map<String, String>> allConditions = Arrays.stream(QueryConditionsEnum.values())
                .map(cond -> Map.of(
                        "code", cond.getCode(),
                        "operator", cond.getSqlOperator(),
                        "description", cond.getDescription()
                ))
                .toList();

        return Map.of(
                "variables", builtInVariablesList,
                "conditions", allConditions
        );
    }
}