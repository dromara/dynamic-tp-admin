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

package org.dromara.dynamictp.admin.modules.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.dynamictp.admin.infrastructure.domain.BaseVO;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 数据权限管理 VO 展示类
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeVO
 * @CreateTime 2025-05-10 - 21:38:29
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SysDataScopeVO", description = "数据权限管理 VO 对象")
public class SysDataScopeVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = -2583136745900232811L;

    @Schema(description = "名称")
    private String name;

    @Schema(description = "标识")
    private String code;

    @Schema(description = "菜单 ID")
    private Long menuId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "权限资源 ID")
    private Long permissionId;

    @Schema(description = "权限资源标识")
    private String permissionResource;

    @Schema(description = "权限资源名称")
    private String permissionName;

    @Schema(description = "数据权限类型")
    private String scopeType;

    @Schema(description = "数据权限类型名称(全部数据权限/本部门及下级数据权限/本部门数据权限/本人及下级组织单位数据权限/自定义数据权限/仅本人数据权限)")
    private String scopeTypeName;

    @Schema(description = "描述")
    private String description;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "创建用户")
    private String createUser;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "是否启用(0:禁用,1:启用)")
    private String status;

}