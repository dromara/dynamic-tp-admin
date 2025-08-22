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
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.position.SysUserPositionAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.position.SysUserPositionDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.position.SysUserPositionSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.position.SysUserPositionUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysUserPositionVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysUserPositionFacade;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户岗位管理 Controller 控制层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.system.SysUserPositionController
 * @CreateTime 2024-06-27 - 21:26:12
 */

@RestController
@Tag(name = "用户岗位管理")
@RequiredArgsConstructor
@RequestMapping("/sys_user_position")
public class SysUserPositionController {

    @NonNull
    private ISysUserPositionFacade sysUserPositionFacade;

    @GetMapping("/page")
    @SaCheckPermission("sys:user:position:page")
    @Operation(operationId = "1", summary = "获取用户岗位管理列表")
    public Result<RPage<SysUserPositionVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                                 @Parameter(description = "查询对象") SysUserPositionSearchDTO sysUserPositionSearchDTO) {
        return Result.data(sysUserPositionFacade.listSysUserPositionPage(pageQuery, sysUserPositionSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:user:position:get")
    @Operation(operationId = "2", summary = "根据ID获取用户岗位管理详细信息")
    public Result<SysUserPositionVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(sysUserPositionFacade.get(id));
    }

    @PostMapping("/")
    @SaCheckPermission("sys:user:position:add")
    @Operation(operationId = "3", summary = "新增用户岗位管理")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody SysUserPositionAddDTO sysUserPositionAddDTO) {
        return Result.status(sysUserPositionFacade.add(sysUserPositionAddDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("sys:user:position:update")
    @Operation(operationId = "4", summary = "更新用户岗位管理信息")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody SysUserPositionUpdateDTO sysUserPositionUpdateDTO) {
        return Result.status(sysUserPositionFacade.update(sysUserPositionUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("sys:user:position:delete")
    @Operation(operationId = "5", summary = "批量删除用户岗位管理信息")
    public Result<Boolean> batchDelete(@Parameter(description = "删除对象") @RequestBody SysUserPositionDeleteDTO sysUserPositionDeleteDTO) {
        return Result.status(sysUserPositionFacade.batchDelete(sysUserPositionDeleteDTO));
    }

}