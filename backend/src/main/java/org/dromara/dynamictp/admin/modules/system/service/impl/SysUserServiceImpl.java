package org.dromara.dynamictp.admin.modules.system.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.dromara.dynamictp.admin.common.constants.RequestConstant;
import org.dromara.dynamictp.admin.common.domain.LoginUser;
import org.dromara.dynamictp.admin.common.exception.BizException;
import org.dromara.dynamictp.admin.common.pool.StringPools;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.common.util.IPUtil;
import org.dromara.dynamictp.admin.infrastructure.holder.GlobalUserHolder;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.util.RedisUtil;
import org.dromara.dynamictp.admin.infrastructure.util.ServletHolderUtil;
import org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsLogin;
import org.dromara.dynamictp.admin.modules.monitor.service.IMonLogsLoginService;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleBO;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysUserBO;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysUserOrgBO;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysUserResponsibilitiesBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysUser;
import org.dromara.dynamictp.admin.modules.system.repository.mapper.SysUserMapper;
import org.dromara.dynamictp.admin.modules.system.service.ISysRoleService;
import org.dromara.dynamictp.admin.modules.system.service.ISysUserOrgService;
import org.dromara.dynamictp.admin.modules.system.service.ISysUserRoleService;
import org.dromara.dynamictp.admin.modules.system.service.ISysUserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户管理 Service 服务接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.impl.SysUserServiceImpl
 * @CreateTime 2023/7/6 - 16:04
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {

    @NonNull
    private ISysRoleService sysRoleService;

    @NonNull
    private ISysUserRoleService sysUserRoleService;

    @NonNull
    private ISysUserOrgService sysUserOrgService;

    @NonNull
    private IMonLogsLoginService monLogsLoginService;

    @Override
    public IPage<SysUser> listSysUserPage(PageQuery pageQuery, SysUserBO sysUserBO) {
        IPage<SysUser> iPage = pageQuery.buildPage();
        iPage.setRecords(baseMapper.listSysUserPage(iPage, sysUserBO));
        return iPage;
    }

    @Override
    public SysUserBO currentUserInfo() {
        SysUser byId = super.getById(GlobalUserHolder.getUserId());
        return CglibUtil.convertObj(byId, SysUserBO::new);
    }

    @Override
    public boolean addUser(SysUserBO sysUserBO) {
        // 密码盐值
        sysUserBO.setSalt(RandomStringUtils.secureStrong().nextAlphabetic(6));
        // 默认随机12位密码
        String sha256HexPwd = DigestUtils.sha256Hex(RandomStringUtils.secureStrong().nextAlphabetic(12));
        String password = DigestUtils.sha256Hex(sha256HexPwd + sysUserBO.getSalt());
        sysUserBO.setPassword(password);
        return super.save(sysUserBO);
    }

    @Override
    public boolean updateUser(SysUserBO sysUserBO) {
        boolean updateById = super.updateById(sysUserBO);
        // 用户管理修改用户，则退出用户，要求重登
        StpUtil.logout(sysUserBO.getId());
        return updateById;
    }

    @Override
    public boolean updateCurrentUserInfo(SysUserBO sysUserBO) {
        boolean updateById = super.updateById(sysUserBO);
        // 自我更新个人资料，需要更新缓存资料
        saveUserToSession(sysUserBO, true);
        return updateById;
    }

    @Override
    public boolean removeBatchByIds(List<Long> ids) {
        if (!StpUtil.hasRole(StringPools.ADMIN.toUpperCase())) {
            throw new BizException("非管理员角色禁止删除用户");
        }
        boolean containAdmin = baseMapper.queryIsContainAdmin(ids);
        if (containAdmin) {
            throw new BizException("禁止删除《管理员》用户");
        }
        return super.removeBatchByIds(ids, true);
    }

    @Override
    public Map<String, String> userLogin(SysUserBO sysUserBO) {
        MonLogsLogin loginLogs = initLoginLog(sysUserBO);
        SysUser userForUserName = baseMapper.getUserByUserName(sysUserBO.getUserName());
        try {
            if (ObjectUtils.isEmpty(userForUserName)) {
                throw new BizException("查找不到用户名 %s".formatted(sysUserBO.getUserName()));
            }
            if (StringPools.ZERO.equals(userForUserName.getStatus())) {
                throw new BizException("当前用户 %s 已被禁止登录".formatted(sysUserBO.getUserName()));
            }
            // 密码拼接
            String inputPassword = sysUserBO.getPassword() + userForUserName.getSalt();
            // 密码比对
            if (!DigestUtils.sha256Hex(inputPassword).equals(userForUserName.getPassword())) {
                throw new BizException("登录失败，请核实用户名以及密码");
            }
            // sa token 进行登录
            StpUtil.login(userForUserName.getId());
            // 更新用户登录时间
            userForUserName.setLastLoginTime(LocalDateTime.now());
            saveUserToSession(userForUserName, false);
            loginLogs.setUserId(userForUserName.getId());
            loginLogs.setUserRealName(userForUserName.getRealName());
            super.updateById(userForUserName);
        } catch (BizException e) {
            loginLogs.setStatus(StringPools.ZERO);
            loginLogs.setMessage(e.getMessage());
            throw e;
        } finally {
            monLogsLoginService.save(loginLogs);
        }
        return Map.of("token", StpUtil.getTokenValue());
    }

    /**
     * 初始化登录日志
     *
     * @param sysUserBO 用户对象
     * @return {@linkplain MonLogsLogin} 登录日志对象
     * @author payne.zhuang
     * @CreateTime 2024-05-05 18:44
     */
    private MonLogsLogin initLoginLog(SysUserBO sysUserBO) {
        String ip = JakartaServletUtil.getClientIP(ServletHolderUtil.getRequest());
        return MonLogsLogin.builder()
                .userName(sysUserBO.getUserName())
                .status(StringPools.ONE)
                .userAgent(ServletHolderUtil.getRequest().getHeader(RequestConstant.USER_AGENT))
                .ip(ip)
                .ipAddr(IPUtil.getIpAddr(ip))
                .message("登陆成功")
                .build();
    }

    /**
     * 将用户信息存入 Session
     *
     * @param sysUser   用户对象
     * @param needCheck 是否需要查找数据库用户信息
     * @author payne.zhuang
     * @CreateTime 2024-04-21 22:19
     */
    private void saveUserToSession(SysUser sysUser, boolean needCheck) {
        if (needCheck) {
            sysUser = super.getById(sysUser.getId());
        }
        // 用户转换
        LoginUser loginUser = CglibUtil.convertObj(sysUser, LoginUser::new);
        // 获取用户角色
        List<SysRoleBO> sysRoleBOS = sysRoleService.queryRoleListWithUserId(sysUser.getId());
        loginUser.setRoleIds(sysRoleBOS.stream().map(SysRoleBO::getId).collect(Collectors.toSet()));
        loginUser.setRoleCodes(sysRoleBOS.stream().map(SysRoleBO::getRoleCode).collect(Collectors.toSet()));
        Set<Long> userOrgIds = sysUserOrgService.queryOrgUnitsIdsWithUserId(sysUser.getId());
        loginUser.setOrgIds(userOrgIds);
        // Session 放入用户对象
        StpUtil.getSessionByLoginId(sysUser.getId()).set("user", loginUser);
    }

    @Override
    public Map<String, String> refreshToken(String refreshToken, String refreshTokenCacheKey, LoginUser loginUser) {
        // 删除 旧的 refresh token
        RedisUtil.del(refreshTokenCacheKey);
        return Map.of();
    }

    @Override
    public String resetPassword(Long userId) {
        if (!StpUtil.hasRole(StringPools.ADMIN.toUpperCase())) {
            throw new BizException("非管理员禁止重置用户密码");
        }
        SysUser sysUser = baseMapper.selectById(userId);
        if (ObjectUtils.isEmpty(sysUser)) {
            throw new BizException("查找不到用户信息");
        }
        if (StringPools.ADMIN.equalsIgnoreCase(sysUser.getUserName())) {
            throw new BizException("禁止重置《%s》账户密码".formatted(StringPools.ADMIN));
        }
        // 密码盐值
        sysUser.setSalt(RandomStringUtils.secureStrong().nextAlphabetic(6));
        // 默认随机12位密码
        String randomPwd = RandomStringUtils.secureStrong().nextAlphabetic(12);
        String sha256HexPwd = DigestUtils.sha256Hex(randomPwd);
        String password = DigestUtils.sha256Hex(sha256HexPwd + sysUser.getSalt());
        sysUser.setPassword(password);
        sysUser.setUpdatePasswordTime(LocalDateTime.now());
        super.updateById(sysUser);
        return randomPwd;
    }

    @Override
    public SysUserResponsibilitiesBO queryUserResponsibilitiesWithUserId(Long userId) {
        List<Long> userRoleIds = sysUserRoleService.queryRoleIdsWithUserId(userId);
        // 用户所属组织
        List<SysUserOrgBO> sysUserOrgBOList = sysUserOrgService.queryOrgUnitsListWithUserId(userId);
        List<Long> userOrgUnitsPrincipalIds = sysUserOrgBOList.stream()
                .filter(item -> StringPools.ONE.equals(item.getPrincipal()))
                .map(SysUserOrgBO::getOrgId).toList();
        List<Long> userOrgUnitsIds = sysUserOrgBOList.stream().map(SysUserOrgBO::getOrgId).toList();
        return SysUserResponsibilitiesBO.builder()
                .userId(userId)
                .roleIds(userRoleIds)
                .positionIds(List.of()) // 岗位功能已移除，返回空列表
                .orgUnitsIds(userOrgUnitsIds)
                .orgUnitsPrincipalIds(userOrgUnitsPrincipalIds)
                .build();
    }

    @Override
    public boolean updateUserResponsibilities(SysUserResponsibilitiesBO responsibilitiesBO) {
        Long userId = responsibilitiesBO.getUserId();
        boolean role = sysUserRoleService.updateUserRole(userId, responsibilitiesBO.getRoleIds());
        // 岗位功能已移除，跳过岗位更新
        boolean userOrg = sysUserOrgService.updateUserOrg(userId, responsibilitiesBO.getOrgUnitsIds(),
                responsibilitiesBO.getOrgUnitsPrincipalIds());
        return role && userOrg;
    }
}