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
* 角色数据权限关联管理 VO 展示类
*
* @Author payne.zhuang <paynezhuang@gmail.com>
* @ProjectName dynamic-tp-admin
* @ClassName org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleDataScopeVO
* @CreateTime 2025-05-10 - 21:40:18
*/

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SysRoleDataScopeVO", description = "角色数据权限关联管理 VO 对象")
public class SysRoleDataScopeVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 948693285107746249L;

    @Schema(description = "数据权限管理 ID")
    private Long dataScopeId;

    @Schema(description = "数据权限管理标识")
    private String dataScopeCode;

    @Schema(description = "角色 ID")
    private Long roleId;

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "创建用户")
    private String createUser;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "是否启用(0:禁用,1:启用)")
    private String status;

}