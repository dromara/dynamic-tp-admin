package org.dromara.dynamictp.admin.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;

/**
 * 登录用户
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.infrastructure.domain.LoginUser
 * @CreateTime 2023/7/18 - 12:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUser implements Serializable {

    @Serial
    private static final long serialVersionUID = 8648911578225057361L;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 账号
     */
    private String userName;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 真名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机
     */
    private String phone;

    /**
     * 角色IDs
     */
    private Set<Long> roleIds;

    /**
     * 角色Codes
     */
    private Set<String> roleCodes;

    /**
     * 组织IDs
     */
    private Set<Long> orgIds;

}
