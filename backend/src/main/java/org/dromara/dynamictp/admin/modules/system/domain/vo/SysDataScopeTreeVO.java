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
import java.io.Serializable;
import java.util.List;

/**
 * 数据权限管理树形 VO 展示类
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeTreeVO
 * @CreateTime 2025-05-23 17:22:34
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SysDataScopeTreeVO", description = "数据权限管理树形 VO 对象")
public class SysDataScopeTreeVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = -15960222800294547L;

    @Schema(description = "菜单 ID")
    private Long menuId;

    @Schema(description = "菜单名称")
    private String menuName;

    @Schema(description = "资源集合")
    private List<Permission> children;

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Permission implements Serializable {

        @Serial
        private static final long serialVersionUID = -7984427555891547062L;

        @Schema(description = "权限资源 ID")
        private Long permissionId;

        @Schema(description = "权限资源标识")
        private String permissionResource;

        @Schema(description = "权限资源名称")
        private String permissionName;

        @Schema(description = "权限规则集合")
        private transient List<Scope> children;
    }

    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Scope implements Serializable {

        @Serial
        private static final long serialVersionUID = -2566749861291986526L;

        @Schema(description = "数据权限 ID")
        private Long id;

        @Schema(description = "名称")
        private String name;

        @Schema(description = "标识")
        private String code;

        @Schema(description = "排序")
        private Integer sort;

        @Schema(description = "数据权限类型")
        private String scopeType;

    }

}