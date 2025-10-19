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

package org.dromara.dynamictp.admin.modules.system.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.dynamictp.admin.infrastructure.domain.BaseEntity;

import java.io.Serial;

/**
 * 数据权限管理 Entity 实体类
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.entity.SysDataScope
 * @CreateTime 2025-05-10 - 21:38:29
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("sys_data_scope")
public class SysDataScope extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -7570276659858682066L;

    /**
     * 名称
     */
    private String name;

    /**
     * 标识
     */
    private String code;

    /**
     * 菜单 ID
     */
    private Long menuId;

    /**
     * 菜单名称
     */
    private String menuName;

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
     * 数据权限类型名称(全部数据权限/本部门及下级数据权限/本部门数据权限/本人及下级组织单位数据权限/自定义数据权限/仅本人数据权限)
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

    /**
     * 描述
     */
    private String description;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 是否启用(0:禁用,1:启用)
     */
    private String status;

}