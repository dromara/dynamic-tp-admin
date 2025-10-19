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
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.dromara.dynamictp.admin.common.exception.BizException;
import org.dromara.dynamictp.admin.common.pool.StringPools;
import org.dromara.dynamictp.admin.infrastructure.context.DataScopeConditionContext;
import org.dromara.dynamictp.admin.infrastructure.enums.DataScopeVariableEnum;
import org.dromara.dynamictp.admin.infrastructure.holder.GlobalUserHolder;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.util.DateTimeUtil;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysDataScopeBO;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleDataScopeQueryBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysDataScope;
import org.dromara.dynamictp.admin.modules.system.repository.mapper.SysDataScopeMapper;
import org.dromara.dynamictp.admin.modules.system.service.ISysDataScopeService;
import org.dromara.dynamictp.admin.modules.system.service.ISysRoleDataScopeService;
import org.dromara.dynamictp.admin.modules.system.service.ISysUserOrgService;
import org.dromara.dynamictp.admin.modules.system.util.DataScopeCacheManager;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.domain.DataScopeCondition;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.enums.DataScopeTypeEnum;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.enums.QueryConditionsEnum;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * 数据权限管理 Service 服务接口实现层
 * <p>
 * 主要功能：
 * 1. 数据权限配置的增删改查操作
 * 2. 根据权限标识查询角色权限配置
 * 3. 根据权限类型获取用户ID集合（全部、本人、本组织、本组织及下级、本人及下级）
 * 4. 构建数据权限变量上下文，支持变量值的动态获取
 * 5. 支持各种预定义变量：用户相关、时间相关等
 * </p>
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.impl.SysDataScopeServiceImpl
 * @CreateTime 2025-05-10 - 21:38:29
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysDataScopeServiceImpl extends ServiceImpl<SysDataScopeMapper, SysDataScope> implements ISysDataScopeService {

    @NonNull
    private ISysRoleDataScopeService sysRoleDataScopeService;

    @NonNull
    private ISysUserOrgService sysUserOrgService;

    private final DataScopeCacheManager dataScopeCacheManager;

    /**
     * 分页查询数据权限列表
     *
     * @param pageQuery      分页查询参数
     * @param sysDataScopeBO 数据权限查询条件
     * @return 分页结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:39
     */
    @Override
    public IPage<SysDataScope> listSysDataScopePage(PageQuery pageQuery, SysDataScopeBO sysDataScopeBO) {
        LambdaQueryWrapper<SysDataScope> queryWrapper = new LambdaQueryWrapper<SysDataScope>()
                .eq(SysDataScope::getPermissionId, sysDataScopeBO.getPermissionId());
        return baseMapper.selectPage(pageQuery.buildPage(), queryWrapper);
    }

    /**
     * 查询数据权限列表
     *
     * @param sysDataScopeBO 数据权限查询条件
     * @return 数据权限列表
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:40
     */
    @Override
    public List<SysDataScope> listSysDataScope(SysDataScopeBO sysDataScopeBO) {
        LambdaQueryWrapper<SysDataScope> queryWrapper = new LambdaQueryWrapper<SysDataScope>()
                .eq(SysDataScope::getStatus, StringPools.ONE);
        return baseMapper.selectList(queryWrapper);
    }

    /**
     * 新增数据权限
     * <p>
     * 新增前检查权限标识是否已存在，避免重复创建
     * </p>
     *
     * @param sysDataScopeBO 数据权限信息
     * @return 是否新增成功
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:41
     */
    @Override
    public boolean add(SysDataScopeBO sysDataScopeBO) {
        LambdaQueryWrapper<SysDataScope> queryWrapper = new LambdaQueryWrapper<SysDataScope>()
                .eq(SysDataScope::getCode, sysDataScopeBO.getCode());
        SysDataScope existingDataScope = baseMapper.selectOne(queryWrapper);
        if (ObjectUtils.isNotEmpty(existingDataScope)) {
            throw new BizException("数据权限标识[%s]已存在,不允许重复创建".formatted(sysDataScopeBO.getCode()));
        }
        return super.save(sysDataScopeBO);
    }

    /**
     * 更新数据权限
     *
     * @param sysDataScopeBO 数据权限信息
     * @return 是否更新成功
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:42
     */
    @Override
    public boolean update(SysDataScopeBO sysDataScopeBO) {
        boolean result = super.updateById(sysDataScopeBO);
        
        // 数据权限配置变更，异步清理相关权限的缓存
        if (result && sysDataScopeBO.getCode() != null) {
            dataScopeCacheManager.invalidatePermissionCacheAsync(this, sysDataScopeBO.getPermissionResource(), "数据权限配置变更");
        }
        
        return result;
    }

    // ================================ 权限查询相关方法 ================================

    /**
     * 根据权限标识查询角色权限配置
     *
     * @param permissionCode 权限标识
     * @return 角色权限配置列表
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:43
     */
    @Override
    // @Cacheable(value = SystemCacheConstant.SYSTEM_DATA_SCOPE, key = "#permissionCode")
    public List<SysRoleDataScopeQueryBO> listByPermissionCode(String permissionCode) {
        return sysRoleDataScopeService.listByPermissionCode(permissionCode);
    }

    // ================================ 用户ID集合获取方法 ================================

    /**
     * 根据权限类型获取用户ID集合
     * <p>
     * 支持多种权限类型的用户ID获取：
     * - ALL: 全部权限，返回空集合
     * - SELF: 本人权限，只返回当前用户ID
     * - UNIT: 本组织权限，返回同组织用户ID
     * - UNIT_AND_CHILD: 本组织及下级权限，需要负责人身份
     * - SELF_AND_CHILD: 本人及下级权限，需要负责人身份
     * </p>
     *
     * @param userId    用户ID
     * @param scopeType 权限类型
     * @return 用户ID集合
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:44
     */
    @Override
    public Set<Long> getUserIdsByScopeType(Long userId, DataScopeTypeEnum scopeType) {
        return switch (scopeType) {
            // 全部权限：返回空集合，不添加用户过滤条件
            case ALL -> Collections.emptySet();
            // 本人权限：只能查看自己的数据
            case SELF -> Collections.singleton(userId);
            // 本组织权限：查看同组织用户数据
            case UNIT -> getUserIdsByUnitScope(userId);
            // 本组织及下级权限：需要负责人身份
            case UNIT_AND_CHILD -> getUserIdsByUnitAndChildScope(userId);
            // 本人及下级权限：需要负责人身份
            case SELF_AND_CHILD -> getUserIdsBySelfAndChildScope(userId);
            // 未知类型：返回空集合
            default -> Collections.emptySet();
        };
    }

    /**
     * 获取本组织权限的用户ID集合
     * <p>
     * 查询用户所属的所有组织内的用户ID，不区分负责人身份
     * 异常时降级为本人权限，确保系统稳定性
     * </p>
     *
     * @param userId 用户ID
     * @return 本组织用户ID集合
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:45
     */
    @Override
    public Set<Long> getUserIdsByUnitScope(Long userId) {
        try {
            // 获取用户所属的所有组织ID
            List<Long> orgIds = sysUserOrgService.getUserOrgIds(userId);
            if (orgIds.isEmpty()) {
                log.info("[DataScope] 用户无组织关系，返回本人权限: userId={}", userId);
                return Collections.singleton(userId);
            }

            // 查询这些组织内的所有用户（不区分负责人身份）
            List<Long> userIds = sysUserOrgService.getUserIdsByOrgIds(orgIds);
            return Sets.newHashSet(userIds);

        } catch (Exception e) {
            log.error("[DataScope] 本组织权限查询失败: userId={}", userId, e);
            // 异常时降级为本人权限
            return Collections.singleton(userId);
        }
    }

    /**
     * 获取本组织及下级权限的用户ID集合
     * <p>
     * 只有组织负责人才能查看下级组织数据：
     * 1. 检查用户是否担任任何组织的负责人
     * 2. 非负责人降级为本组织权限
     * 3. 负责人可查看本组织用户 + 下级组织用户
     * </p>
     *
     * @param userId 用户ID
     * @return 本组织及下级用户ID集合
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:46
     */
    @Override
    public Set<Long> getUserIdsByUnitAndChildScope(Long userId) {
        try {
            // 检查用户是否担任任何组织的负责人
            List<Long> principalOrgIds = sysUserOrgService.getPrincipalOrgIds(userId);
            if (principalOrgIds.isEmpty()) {
                log.info("[DataScope] 用户未担任任何组织负责人，降级为本组织权限: userId={}", userId);
                // 非负责人只能查看本组织数据，不能查看下级组织
                return getUserIdsByUnitScope(userId);
            }

            // 用户可以查看：本组织用户（无需负责人身份） + 下级组织用户（需要负责人身份）
            List<Long> allUserIds = sysUserOrgService.getUserIdsByUnitAndChild(userId);

            log.info("[DataScope] 本组织及下级权限查询完成: userId={}, 负责组织数={}, 用户数={}",
                    userId, principalOrgIds.size(), allUserIds.size());
            return Sets.newHashSet(allUserIds);

        } catch (Exception e) {
            log.error("[DataScope] 本组织及下级权限查询失败: userId={}", userId, e);
            // 异常时降级为本人权限
            return Collections.singleton(userId);
        }
    }

    /**
     * 获取本人及下级权限的用户ID集合
     * <p>
     * 只有组织负责人才能查看下级组织数据：
     * 1. 检查用户是否担任任何组织的负责人
     * 2. 非负责人只能查看本人数据
     * 3. 负责人可查看本人数据 + 下级组织用户数据
     * </p>
     *
     * @param userId 用户ID
     * @return 本人及下级用户ID集合
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:47
     */
    @Override
    public Set<Long> getUserIdsBySelfAndChildScope(Long userId) {
        try {
            // 检查用户是否担任任何组织的负责人
            List<Long> principalOrgIds = sysUserOrgService.getPrincipalOrgIds(userId);
            if (principalOrgIds.isEmpty()) {
                log.info("[DataScope] 用户未担任任何组织负责人，仅返回本人权限: userId={}", userId);
                // 非负责人只能查看本人数据
                return Collections.singleton(userId);
            }

            // 负责人可以查看：本人数据 + 下级组织用户数据
            List<Long> allUserIds = sysUserOrgService.getUserIdsBySelfAndChildWithPrincipal(userId);

            log.info("[DataScope] 本人及下级权限查询完成: userId={}, 负责组织数={}, 用户数={}",
                    userId, principalOrgIds.size(), allUserIds.size());
            return Sets.newHashSet(allUserIds);

        } catch (Exception e) {
            log.error("[DataScope] 本人及下级权限查询失败: userId={}", userId, e);
            // 异常时降级为本人权限
            return Collections.singleton(userId);
        }
    }

    // ================================ 变量上下文构建方法 ================================

    /**
     * 构建数据权限变量上下文
     * <p>
     * 核心功能：
     * 1. 遍历自定义条件列表，为每个条件构建上下文对象
     * 2. 解析变量名，获取对应的变量枚举类型
     * 3. 根据变量类型获取实际的变量值
     * 4. 对于无效的变量名，视为固定值处理
     * 5. 返回完整的条件上下文列表，供后续SQL构建使用
     * </p>
     *
     * @param userId           用户ID
     * @param customConditions 自定义条件列表
     * @return 数据权限条件上下文列表
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:48
     */
    @Override
    public List<DataScopeConditionContext> buildDataScopeVariableValue(Long userId, List<DataScopeCondition> customConditions) {
        List<DataScopeConditionContext> conditionContexts = Lists.newArrayList();
        customConditions.forEach(condition -> {
            QueryConditionsEnum conditionsEnum = QueryConditionsEnum.of(condition.getOperator());
            // 构建基础条件上下文
            DataScopeConditionContext conditionContext = DataScopeConditionContext.builder()
                    .field(condition.getField())
                    .operator(condition.getOperator())
                    .value(condition.getValue())
                    .logic(condition.getLogic())
                    .variable(condition.getVariable())
                    .conditionsEnum(conditionsEnum)
                    .variableEnum(null)
                    .variableValue(null)
                    .build();

            // 处理变量值
            String variable = condition.getVariable();
            if (org.springframework.util.StringUtils.hasText(variable)) {
                try {
                    // 解析变量枚举类型
                    DataScopeVariableEnum variableEnum = DataScopeVariableEnum.of(variable);
                    conditionContext.setVariableEnum(variableEnum);
                    // 获取变量的实际值
                    conditionContext.setVariableValue(getVariableValue(userId, variableEnum, conditionsEnum));
                } catch (IllegalArgumentException e) {
                    // 无效变量名，视为固定值处理
                    log.info("[DataScope] 无效变量名，视为固定值: variable={}", variable);
                }
            }
            conditionContexts.add(conditionContext);
        });

        log.debug("[DataScope] 变量上下文构建完成: userId={}, 条件数量={}", userId, conditionContexts.size());
        return conditionContexts;
    }

    /**
     * 获取变量的实际值
     * <p>
     * 支持多种预定义变量类型：{@link DataScopeVariableEnum}
     * 1. 用户相关：当前用户ID、用户名、角色ID列表、组织ID列表
     * 2. 时间相关：当前日期、当前年份、今天、昨天、近一周、近一月、当前月、当前季度
     * 3. 使用DateTimeUtil工具类处理时间范围，确保时间处理的一致性
     * 4. 根据变量类型返回单个值或时间范围，支持等值查询和区间查询
     * </p>
     *
     * @param userId         用户ID
     * @param variableEnum   变量枚举类型
     * @param conditionsEnum 查询条件枚举类型
     * @return 变量的实际值
     * @author payne.zhuang
     * @CreateTime 2025-06-02 17:55:59
     */
    private Object getVariableValue(Long userId, DataScopeVariableEnum variableEnum, QueryConditionsEnum conditionsEnum) {
        if (null == variableEnum) {
            return null;
        }

        try {
            return switch (variableEnum) {
                // 用户相关变量：返回具体的用户属性值
                case CURRENT_USER_ID -> userId;
                // 返回当前用户名称
                case CURRENT_USER_NAME -> GlobalUserHolder.getUserRealName();
                // 返回当前用户角色ID列表
                case CURRENT_USER_ROLE_IDS -> GlobalUserHolder.getRoleIds();
                // 返回当前用户组织ID列表
                case CURRENT_USER_ORG_IDS -> GlobalUserHolder.getOrgIds();
                // 返回今天的时间范围，用于区间查询
                case TODAY -> DateTimeUtil.getTodayRange();
                // 返回昨天的时间范围，用于区间查询
                case LAST_DAY -> DateTimeUtil.getLastDayRange();
                // 返回近一周的时间范围，用于区间查询
                case LAST_WEEK -> DateTimeUtil.getLastWeekRange();
                // 返回近一个月的时间范围，用于区间查询
                case LAST_MONTH -> DateTimeUtil.getLastMonthRange();
                // 返回当前月的时间范围，用于区间查询
                case CURRENT_MONTH -> DateTimeUtil.getCurrentMonthRange();
                // 返回当前季度的时间范围，用于区间查询
                case CURRENT_QUARTER -> DateTimeUtil.getCurrentQuarterRange();
                // 返回当前年份相关时间，根据操作符智能选择格式
                case CURRENT_YEAR -> // 根据查询条件直接返回对应的时间格式
                        switch (conditionsEnum) {
                            // 大于查询：直接返回当前年的年末时间
                            case GREATER_THAN, GREATER_THAN_OR_EQUAL -> DateTimeUtil.getCurrentYearEndTime();
                            // 小于查询：直接返回当前年的年初时间
                            case LESS_THAN, LESS_THAN_OR_EQUAL -> DateTimeUtil.getCurrentYearStartTime();
                            // 默认返回当前年的时间范围（适用于BETWEEN等区间查询和其他所有操作符）
                            default -> DateTimeUtil.getCurrentYearRange();
                        };
            };
        } catch (Exception e) {
            log.error("[DataScope] 获取变量值失败: userId={}, variable={}, error={}",
                    userId, variableEnum.getCode(), e.getMessage(), e);
            return null;
        }
    }
}

