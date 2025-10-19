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

package org.dromara.dynamictp.admin.modules.system.domain.bo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 角色数据权限关联管理查询 BO 对象
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleDataScopeQueryBO
 * @CreateTime 2025/5/12 - 21:10
 */

@Data
public class SysRoleDataScopeQueryBO implements Serializable {

    @Serial
    private static final long serialVersionUID = 2872329822510445903L;

    /**
     * 角色 ID
     */
    private Long roleId;

    /**
     * 数据权限管理标识名称
     */
    private String dataScopeName;

    /**
     * 权限资源 ID
     */
    private Long permissionId;

    /**
     * 权限资源标识
     */
    private String permissionResource;

    /**
     * 权限资源名称
     */
    private String permissionName;

    /**
     * 数据权限类型
     */
    private String scopeType;

    /**
     * 数据权限类型名称
     */
    private String scopeTypeName;

    /**
     * 自定义可见字段
     */
    private String customFields;

    /**
     * 自定义规则条件
     */
    private String customRules;
}
