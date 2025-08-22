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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.api.Result;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeEditVO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeTreeVO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysDataScopeFacade;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 数据权限管理 Controller 控制层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.system.SysDataScopeController
 * @CreateTime 2025-05-10 - 21:38:29
 */

@RestController
@Tag(name = "数据权限管理")
@RequiredArgsConstructor
@RequestMapping("sys_data_scope")
public class SysDataScopeController {

    @NonNull
    private ISysDataScopeFacade sysDataScopeFacade;

    @GetMapping("/page")
    @SaCheckPermission("sys:data:scope:page")
    @Operation(operationId = "1", summary = "获取数据权限管理列表")
    public Result<RPage<SysDataScopeVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                              @Parameter(description = "查询对象") SysDataScopeSearchDTO sysDataScopeSearchDTO) {
        return Result.data(sysDataScopeFacade.listSysDataScopePage(pageQuery, sysDataScopeSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:data:scope:get")
    @Operation(operationId = "2", summary = "根据ID获取数据权限管理详细信息")
    public Result<SysDataScopeEditVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(sysDataScopeFacade.get(id));
    }

    @PostMapping("/")
    @SaCheckPermission("sys:data:scope:add")
    @Operation(operationId = "3", summary = "新增数据权限管理")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody SysDataScopeAddDTO sysDataScopeAddDTO) {
        return Result.status(sysDataScopeFacade.add(sysDataScopeAddDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("sys:data:scope:update")
    @Operation(operationId = "4", summary = "更新数据权限管理信息")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody SysDataScopeUpdateDTO sysDataScopeUpdateDTO) {
        return Result.status(sysDataScopeFacade.update(sysDataScopeUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("sys:data:scope:delete")
    @Operation(operationId = "5", summary = "批量删除数据权限管理信息")
    public Result<Boolean> batchDelete(@Parameter(description = "删除对象") @RequestBody SysDataScopeDeleteDTO sysDataScopeDeleteDTO) {
        return Result.status(sysDataScopeFacade.batchDelete(sysDataScopeDeleteDTO));
    }

    @GetMapping("/tree")
    @SaCheckPermission("sys:data:scope:tree")
    @Operation(operationId = "6", summary = "树形菜单形式显示数据权限管理")
    public Result<List<SysDataScopeTreeVO>> tree() {
        return Result.data(sysDataScopeFacade.queryAllDataScopeListConvertToTree());
    }

    @GetMapping("/variable-conditions")
    @SaCheckPermission("sys:data:scope:variable:conditions")
    @Operation(operationId = "100", summary = "获取所有数据权限变量及其可用操作符（k-v结构）")
    public Result<Map<String, Object>> getVariableConditions() {
        return Result.data(sysDataScopeFacade.getVariableConditions());
    }

}