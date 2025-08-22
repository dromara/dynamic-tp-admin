package org.dromara.dynamictp.admin.infrastructure.holder;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotWebContextException;
import cn.dev33.satoken.stp.StpUtil;
import org.dromara.dynamictp.admin.common.domain.LoginUser;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 全局用户
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.infrastructure.holder.GlobalUserHolder
 * @CreateTime 2023/7/19 - 16:53
 */
@Component
public class GlobalUserHolder {

    private GlobalUserHolder() {

    }

    /**
     * 获取登录用户信息
     *
     * @return {@link LoginUser} 登录用户对象
     * @author payne.zhuang
     * @CreateTime 2023-07-21 21:55
     */
    public static LoginUser getUser() {
        try {
            LoginUser loginUser = (LoginUser) StpUtil.getSession().get("user");
            if(loginUser == null){
                return LoginUser.builder().id(-1L).realName("系统用户").build();
            }
            return loginUser;
        } catch (NotLoginException | NotWebContextException exception) {
            return LoginUser.builder().id(-1L).realName("系统用户").build();
        }
    }

    /**
     * 获取登录用户 ID
     *
     * @return {@link Long} 登录用户 ID
     * @author payne.zhuang
     * @CreateTime 2023-07-21 21:56
     */
    public static Long getUserId() {
        return getUser().getId();
    }

    /**
     * 获取登录用户名称
     *
     * @return {@link String} 登录用户名称
     * @author payne.zhuang
     * @CreateTime 2023-07-21 21:57
     */
    public static String getUserName() {
        return getUser().getUserName();
    }

    /**
     * 获取登录用户真实名称
     *
     * @return {@link String} 登录用户真实名称
     * @author payne.zhuang
     * @CreateTime 2023-07-21 21:57
     */
    public static String getUserRealName() {
        return getUser().getRealName();
    }

    /**
     * 获取登录用户昵称
     *
     * @return {@link String} 登录用户昵称
     * @author payne.zhuang
     * @CreateTime 2023-07-21 21:57
     */
    public static String getNickName() {
        return getUser().getNickName();
    }

    /**
     * 获取登录用户角色ID列表
     *
     * @return {@link Set} 角色ID列表
     * @author payne.zhuang
     * @CreateTime 2024-02-04 22:05
     */
    public static Set<Long> getRoleIds() {
        return getUser().getRoleIds();
    }

    /**
     * 获取登录用户角色Code列表
     *
     * @return {@link Set<String>} 角色Code列表
     * @author payne.zhuang
     * @CreateTime 2024-04-19 22:39
     */
    public static Set<String> getRoleCodes() {
        return getUser().getRoleCodes();
    }

    /**
     * 获取登录用户组织ID列表
     *
     * @return {@link Set} 组织ID列表
     * @author payne.zhuang
     * @CreateTime 2025-06-02 23:02:54
     */
    public static Set<Long> getOrgIds() {
        return getUser().getOrgIds();
    }
}
