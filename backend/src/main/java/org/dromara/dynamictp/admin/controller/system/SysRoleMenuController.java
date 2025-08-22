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
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.menu.SysRoleMenuAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.menu.SysRoleMenuDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.menu.SysRoleMenuSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.menu.SysRoleMenuUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleMenuVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysRoleMenuFacade;
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
 * 角色菜单管理 Controller 控制层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.system.SysRoleMenuController
 * @CreateTime 2023-08-05
 */
@RestController
@Tag(name = "角色菜单管理")
@RequiredArgsConstructor
@RequestMapping("/sys_role_menu")
public class SysRoleMenuController {

    @NonNull
    private ISysRoleMenuFacade sysRoleMenuFacade;

    @GetMapping("/page")
    @SaCheckPermission("sys:role:menu:page")
    @Operation(operationId = "1", summary = "获取角色菜单管理列表")
    public Result<RPage<SysRoleMenuVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                             @Parameter(description = "查询对象") SysRoleMenuSearchDTO sysRoleMenuSearchDTO) {
        return Result.data(sysRoleMenuFacade.listSysRoleMenuPage(pageQuery, sysRoleMenuSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("sys:role:menu:get")
    @Operation(operationId = "2", summary = "根据ID获取角色菜单管理详细信息")
    public Result<SysRoleMenuVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(sysRoleMenuFacade.get(id));
    }

    @PostMapping("/")
    @SaCheckPermission("sys:role:menu:add")
    @Operation(operationId = "3", summary = "根据角色 ID 保存菜单")
    public Result<Boolean> add(@Parameter(description = "更新对象") @RequestBody SysRoleMenuAddDTO sysRoleMenuAddDTO) {
        return Result.data(sysRoleMenuFacade.addMenuForRoleId(sysRoleMenuAddDTO.getRoleId(), sysRoleMenuAddDTO.getMenuIds()));
    }

    @PutMapping("/")
    @SaCheckPermission("sys:role:menu:update")
    @Operation(operationId = "4", summary = "更新角色菜单管理信息")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody SysRoleMenuUpdateDTO sysRoleMenuUpdateDTO) {
        return Result.status(sysRoleMenuFacade.update(sysRoleMenuUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("sys:role:menu:delete")
    @Operation(operationId = "5", summary = "批量删除角色菜单管理信息")
    public Result<Boolean> batchDelete(@Parameter(description = "删除对象") @RequestBody SysRoleMenuDeleteDTO sysRoleMenuDeleteDTO) {
        return Result.status(sysRoleMenuFacade.batchDelete(sysRoleMenuDeleteDTO));
    }

    @GetMapping("/menu/{roleId}")
    @SaCheckPermission("sys:role:menu:queryMenuIdsWithRoleId")
    @Operation(operationId = "6", summary = "根据角色 ID 获取菜单Ids")
    public Result<List<Long>> queryMenuIdsWithRoleId(@Parameter(description = "角色ID", required = true) @PathVariable("roleId") Long roleId) {
        return Result.data(sysRoleMenuFacade.queryMenuIdsWithRoleId(roleId));
    }

}
