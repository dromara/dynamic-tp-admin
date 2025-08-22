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
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserResponsibilitiesUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysUserResponsibilitiesVO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysUserVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysUserFacade;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统管理 - 用户管理 Controller 控制层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.system.SysUserController
 * @CreateTime 2023/7/6 - 14:25
 */

@RestController
@Tag(name = "用户管理")
@RequiredArgsConstructor
@RequestMapping("/sys_user")
public class SysUserController {

    @NonNull
    private ISysUserFacade sysUserFacade;

    @GetMapping("/page")
    @SaCheckPermission("sys:user:page")
    @Operation(operationId = "1", summary = "获取用户管理列表")
    public Result<RPage<SysUserVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                         @Parameter(description = "查询对象") SysUserSearchDTO sysUserSearchDTO) {
        return Result.data(sysUserFacade.listSysUserPage(pageQuery, sysUserSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:user:get")
    @Operation(operationId = "2", summary = "根据ID获取用户详细信息")
    public Result<SysUserVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(sysUserFacade.get(id));
    }

    @PostMapping("/")
    @SaCheckPermission("sys:user:add")
    @Operation(operationId = "3", summary = "新增用户")
    public Result<Boolean> addUser(@Parameter(description = "新增用户对象") @RequestBody SysUserAddDTO sysUserAddDTO) {
        return Result.status(sysUserFacade.addUser(sysUserAddDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("sys:user:update")
    @Operation(operationId = "4", summary = "更新用户信息")
    public Result<Boolean> updateUser(@Parameter(description = "更新用户对象") @RequestBody SysUserUpdateDTO sysUserUpdateDTO) {
        return Result.status(sysUserFacade.updateUser(sysUserUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("sys:user:delete")
    @Operation(operationId = "5", summary = "批量删除用户信息")
    public Result<Boolean> batchDeleteUser(@Parameter(description = "删除用户对象") @RequestBody SysUserDeleteDTO sysUserDeleteDTO) {
        return Result.status(sysUserFacade.batchDeleteUser(sysUserDeleteDTO));
    }

    @PutMapping("/reset_password/{userId}")
    @SaCheckPermission("sys:user:resetPassword")
    @Operation(operationId = "6", summary = "重置密码")
    public Result<String> resetPassword(@Parameter(description = "用户ID") @PathVariable("userId") Long userId) {
        return Result.data(sysUserFacade.resetPassword(userId));
    }

    @GetMapping("/responsibilities/{userId}")
    @SaCheckPermission("sys:user:responsibilities")
    @Operation(operationId = "7", summary = "根据用户ID获取用户职责信息")
    public Result<SysUserResponsibilitiesVO> queryUserResponsibilities(@Parameter(description = "ID") @PathVariable("userId") Long userId) {
        return Result.data(sysUserFacade.queryUserResponsibilitiesWithUserId(userId));
    }

    @PutMapping("/responsibilities")
    @SaCheckPermission("sys:user:responsibilities")
    @Operation(operationId = "7", summary = "更新用户职责信息")
    public Result<Boolean> updateUserResponsibilities(@Parameter(description = "用户职责对象") @RequestBody SysUserResponsibilitiesUpdateDTO updateDTO) {
        return Result.data(sysUserFacade.updateUserResponsibilities(updateDTO));
    }

}
