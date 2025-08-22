/*
 * All Rights Reserved: Copyright [2025] [Zhuang Pan (paynezhuang@gmail.com)]
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

package org.dromara.dynamictp.admin.infrastructure.enums;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.builder.QueryConditionBuilder;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.enums.ParameterTypeEnum;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.enums.QueryConditionsEnum;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

/**
 * 数据权限变量枚举
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.infrastructure.enums.DataScopeVariableEnum
 * @CreateTime 2025/5/30 - 16:11
 */

@Getter
@AllArgsConstructor
public enum DataScopeVariableEnum implements Serializable {

    // 登录用户 ID，默认单值操作符
    CURRENT_USER_ID("currentUserId", "登录用户 ID", ParameterTypeEnum.SCALAR),

    // 登录用户名称，支持模糊匹配
    CURRENT_USER_NAME("currentUserName", "登录用户名称", ParameterTypeEnum.SCALAR),

    // 登录用户角色 ID 集合，默认集合操作符
    CURRENT_USER_ROLE_IDS("currentUserRoleIds", "登录用户所有角色 ID", ParameterTypeEnum.LIST),

    // 登录用户组织 ID 集合，默认集合操作符
    CURRENT_USER_ORG_IDS("currentUserOrgIds", "登录用户所有组织 ID", ParameterTypeEnum.LIST),

    // 今天，默认区间操作符
    TODAY("today", "今天", ParameterTypeEnum.RANGE),

    // 昨天
    LAST_DAY("lastDay", "昨天", ParameterTypeEnum.RANGE),

    // 近一周，默认区间操作符
    LAST_WEEK("lastWeek", "近一周", ParameterTypeEnum.RANGE),

    // 近一个月，默认区间操作符
    LAST_MONTH("lastMonth", "近一个月", ParameterTypeEnum.RANGE),

    // 当前月
    CURRENT_MONTH("currentMonth", "当前月", ParameterTypeEnum.RANGE),

    // 当前季度
    CURRENT_QUARTER("currentQuarter", "当前季度", ParameterTypeEnum.RANGE),

    // 当前年（单值），支持大小和区间
    CURRENT_YEAR("currentYear", "当前年", ParameterTypeEnum.DATETIME);

    /**
     * 变量代码
     */
    private final String code;

    /**
     * 变量名称
     */
    private final String name;

    /**
     * 参数类型 单值、列表、范围、无参数
     */
    private final ParameterTypeEnum parameterType;

    // 缓存 allowedConditions
    private static final Map<DataScopeVariableEnum, Set<QueryConditionsEnum>> conditionsCache = Maps.newConcurrentMap();

    // 获取允许的操作符集合，此时 switch 只有一个 if 判断，使用 SuppressWarnings 注解避免警告，如后续有多个的话，可删除
    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    public Set<QueryConditionsEnum> getOptionalConditions() {
        return conditionsCache.computeIfAbsent(
                this,
                variable -> switch (variable) {
                    case CURRENT_USER_NAME -> QueryConditionBuilder.of(code, parameterType)
                            .addSet(QueryConditionBuilder.SCALAR_STRING)
                            .build();
                    default -> QueryConditionBuilder.of(code, parameterType).build();
                }
        );
    }

    /**
     * 根据变量代码获取枚举
     *
     * @param code 名称
     * @return {@link DataScopeVariableEnum } 枚举
     * @author payne.zhuang
     * @CreateTime 2025-06-02 15:00:37
     */
    public static DataScopeVariableEnum of(String code) {
        return Arrays.stream(values())
                .filter(e -> e.getCode().equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown DataScopeVariableEnum code: " + code));
    }

}
