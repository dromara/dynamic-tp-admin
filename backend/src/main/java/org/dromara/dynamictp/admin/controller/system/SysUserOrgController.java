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
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.common.api.Result;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.org.SysUserOrgAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.org.SysUserOrgDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.org.SysUserOrgSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.org.SysUserOrgUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysUserOrgVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysUserOrgFacade;
import org.dromara.dynamictp.admin.modules.system.service.ISysDataScopeService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * 用户组织/部门/子部门管理 Controller 控制层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.system.SysUserOrgController
 * @CreateTime 2024-07-16 - 16:35:30
 */

@Slf4j
@RestController
@Tag(name = "用户组织/部门/子部门管理")
@RequiredArgsConstructor
@RequestMapping("/sys_user_org")
public class SysUserOrgController {

    @NonNull
    private ISysUserOrgFacade sysUserOrgFacade;

    @NonNull
    private ISysDataScopeService sysDataScopeService;

    @GetMapping("/page")
    @SaCheckPermission("sys:user:org:page")
    @Operation(operationId = "1", summary = "获取用户组织/部门/子部门管理列表")
    public Result<RPage<SysUserOrgVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                            @Parameter(description = "查询对象") SysUserOrgSearchDTO sysUserOrgSearchDTO) {
        return Result.data(sysUserOrgFacade.listSysUserOrgPage(pageQuery, sysUserOrgSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:user:org:get")
    @Operation(operationId = "2", summary = "根据ID获取用户组织/部门/子部门管理详细信息")
    public Result<SysUserOrgVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(sysUserOrgFacade.get(id));
    }

    @PostMapping("/")
    @SaCheckPermission("sys:user:org:add")
    @Operation(operationId = "3", summary = "新增用户组织/部门/子部门管理")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody SysUserOrgAddDTO sysUserOrgAddDTO) {
        return Result.status(sysUserOrgFacade.add(sysUserOrgAddDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("sys:user:org:update")
    @Operation(operationId = "4", summary = "更新用户组织/部门/子部门管理信息")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody SysUserOrgUpdateDTO sysUserOrgUpdateDTO) {
        return Result.status(sysUserOrgFacade.update(sysUserOrgUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("sys:user:org:delete")
    @Operation(operationId = "5", summary = "批量删除用户组织/部门/子部门管理信息")
    public Result<Boolean> batchDelete(@Parameter(description = "删除对象") @RequestBody SysUserOrgDeleteDTO sysUserOrgDeleteDTO) {
        return Result.status(sysUserOrgFacade.batchDelete(sysUserOrgDeleteDTO));
    }

    @SaIgnore
    @GetMapping("/test/{userId}")
    @SaCheckPermission("sys:user:org:test")
    @Operation(operationId = "2", summary = "根据ID获取用户组织/部门/子部门管理详细信息")
    public Result<SysUserOrgVO> test(@Parameter(description = "ID") @PathVariable("userId") Long userId) {

        Set<Long> userIds = sysDataScopeService.getUserIdsByUnitScope(userId);
        log.info("组织权限: {}", userIds);

        Set<Long> byUnitAndChildUserIds = sysDataScopeService.getUserIdsByUnitAndChildScope(userId);
        log.info("组织及子部门权限: {}", byUnitAndChildUserIds);

        Set<Long> bySelfAndChildUserIds = sysDataScopeService.getUserIdsBySelfAndChildScope(userId);
        log.info("本人及子部门权限: {}", bySelfAndChildUserIds);

        return Result.success();
    }

}