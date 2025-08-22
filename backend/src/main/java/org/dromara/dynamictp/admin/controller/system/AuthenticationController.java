package org.dromara.dynamictp.admin.controller.system;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.api.Result;
import org.dromara.dynamictp.admin.infrastructure.holder.GlobalUserHolder;
import org.dromara.dynamictp.admin.modules.system.domain.dto.LoginFormDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.RefreshTokenDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.menu.SysUserRouteVO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.SysUserUpdateCurrentInfoDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysUserVO;
import org.dromara.dynamictp.admin.modules.system.facade.IAuthenticationFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 认证管理 Controller 控制层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.system.AuthenticationController
 * @CreateTime 2023/7/17 - 18:33
 */
@RestController
@Tag(name = "登录管理")
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    @NonNull
    private IAuthenticationFacade authenticationFacade;

    @PostMapping("/user_name")
    @Operation(operationId = "1", summary = "用户密码登录")
    public Result<Map<String, String>> userNameLogin(@Parameter(description = "登录对象") @RequestBody LoginFormDTO loginFormDTO) {
        return Result.data(authenticationFacade.userNameLogin(loginFormDTO));
    }

    @PostMapping("/refresh_token")
    @Operation(operationId = "2", summary = "刷新用户Token")
    public Result<Map<String, String>> userNameLogin(@Parameter(description = "刷新TOKEN") @RequestBody RefreshTokenDTO refreshToken) {
        return Result.data(authenticationFacade.refreshToken(refreshToken.getRefreshToken()));
    }

    @PostMapping("/logout")
    @Operation(operationId = "3", summary = "用户退出登录")
    public Result<Boolean> logout() {
        return Result.data(authenticationFacade.logout());
    }

    @GetMapping("/user_info")
    @SaCheckPermission("auth:userInfo")
    @Operation(operationId = "10", summary = "获取当前用户详情信息")
    public Result<SysUserVO> getCurrentUserInfo() {
        return Result.data(authenticationFacade.getCurrentUserInfo());
    }

    @PutMapping("/user_info")
    @SaCheckPermission("auth:updateUserInfo")
    @Operation(operationId = "11", summary = "修改当前用户个人资料")
    public Result<SysUserVO> updateCurrentUserInfo(@Parameter(description = "更新用户对象") @RequestBody SysUserUpdateCurrentInfoDTO currentInfoDTO) {
        return Result.data(authenticationFacade.updateCurrentUserInfo(currentInfoDTO));
    }

    @GetMapping("/user_route")
    @SaCheckPermission("auth:userRoute")
    @Operation(operationId = "12", summary = "获取当前用户的权限路由")
    public Result<SysUserRouteVO> queryUserRoute() {
        return Result.data(authenticationFacade.queryUserRouteWithUserId(GlobalUserHolder.getUserId()));
    }

}
