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

package org.dromara.dynamictp.admin.infrastructure.context;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.enums.DataScopeVariableEnum;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.domain.DataScopeCondition;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.enums.QueryConditionsEnum;

import java.io.Serial;

/**
 * 数据权限条件上下文
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.starter.database.mybatis.plus.context.DataScopeConditionContext
 * @CreateTime 2025/6/2 - 16:08
 */

@Data
@Slf4j
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class DataScopeConditionContext extends DataScopeCondition {

    @Serial
    private static final long serialVersionUID = 4880155577086810505L;

    /**
     * 条件枚举类
     */
    private QueryConditionsEnum conditionsEnum;

    /**
     * 变量枚举类
     * 用于标识数据权限变量的类型，如部门、角色等
     */
    private DataScopeVariableEnum variableEnum;

    /**
     * 变量值
     */
    private transient Object variableValue;
}
