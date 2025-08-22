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

package org.dromara.dynamictp.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaIgnore;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.api.Result;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.data.scope.SysRoleDataScopeAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.data.scope.SysRoleDataScopeDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.data.scope.SysRoleDataScopeSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.data.scope.SysRoleDataScopeUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleDataScopeVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysRoleDataScopeFacade;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 角色数据权限关联管理 Controller 控制层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.system.SysRoleDataScopeController
 * @CreateTime 2025-05-10 - 21:40:18
 */

@RestController
@Tag(name = "角色数据权限关联管理")
@RequiredArgsConstructor
@RequestMapping("sys_role_data_scope")
public class SysRoleDataScopeController {

    @NonNull
    private ISysRoleDataScopeFacade sysRoleDataScopeFacade;

    @GetMapping("/page")
    @SaCheckPermission("sys:role:data:scope:page")
    @Operation(operationId = "1", summary = "获取角色数据权限关联管理列表")
    public Result<RPage<SysRoleDataScopeVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                                  @Parameter(description = "查询对象") SysRoleDataScopeSearchDTO sysRoleDataScopeSearchDTO) {
        return Result.data(sysRoleDataScopeFacade.listSysRoleDataScopePage(pageQuery, sysRoleDataScopeSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:role:data:scope:get")
    @Operation(operationId = "2", summary = "根据ID获取角色数据权限关联管理详细信息")
    public Result<SysRoleDataScopeVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(sysRoleDataScopeFacade.get(id));
    }

    @PostMapping("/")
    @SaCheckPermission("sys:role:data:scope:add")
    @Operation(operationId = "3", summary = "新增角色数据权限关联管理")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody SysRoleDataScopeAddDTO addDTO) {
        return Result.status(sysRoleDataScopeFacade.addDataScopeForRoleId(addDTO.getRoleId(), addDTO.getDataScopeIds()));
    }

    @PutMapping("/")
    @SaCheckPermission("sys:role:data:scope:update")
    @Operation(operationId = "4", summary = "更新角色数据权限关联管理信息")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody SysRoleDataScopeUpdateDTO sysRoleDataScopeUpdateDTO) {
        return Result.status(sysRoleDataScopeFacade.update(sysRoleDataScopeUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("sys:role:data:scope:delete")
    @Operation(operationId = "5", summary = "批量删除角色数据权限关联管理信息")
    public Result<Boolean> batchDelete(@Parameter(description = "删除对象") @RequestBody SysRoleDataScopeDeleteDTO sysRoleDataScopeDeleteDTO) {
        return Result.status(sysRoleDataScopeFacade.batchDelete(sysRoleDataScopeDeleteDTO));
    }

    @SaIgnore
    @GetMapping("/role/{roleId}")
    @SaCheckPermission("sys:role:data:scope:role")
    @Operation(operationId = "6", summary = "根据角色ID查询已配置的数据权限ID列表")
    public Result<List<Long>> listDataScopeIdsByRoleId(@Parameter(description = "角色ID") @PathVariable("roleId") Long roleId) {
        return Result.data(sysRoleDataScopeFacade.listDataScopeIdsByRoleId(roleId));
    }

}