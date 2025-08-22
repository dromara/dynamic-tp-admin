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

package org.dromara.dynamictp.admin.modules.system.domain.dto.role.data.scope;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
* 角色数据权限关联管理 编辑更新 DTO 对象
*
* @Author payne.zhuang <paynezhuang@gmail.com>
* @ProjectName panis-boot
* @ClassName org.dromara.dynamictp.admin.modules.system.domain.dto.role.data.scope.SysRoleDataScopeUpdateDTO
* @CreateTime 2025-05-10 - 21:40:18
*/

@Getter
@Setter
@Schema(name = "SysRoleDataScopeUpdateDTO", description = "角色数据权限关联管理 编辑更新 DTO 对象")
public class SysRoleDataScopeUpdateDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 49573060191466433L;

    @Schema(description = "角色 ID")
    private Long roleId;

    @Schema(description = "数据权限 ID 集合")
    private List<Long> dataScopeIds;

}