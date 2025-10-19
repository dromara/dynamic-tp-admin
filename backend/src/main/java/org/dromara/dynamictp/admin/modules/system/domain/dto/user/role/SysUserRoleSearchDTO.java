package org.dromara.dynamictp.admin.modules.system.domain.dto.user.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户角色管理 查询 DTO 对象
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.dto.SysUserRoleSearchDTO
 * @CreateTime 2023-07-24
 */
@Getter
@Setter
@Schema(name = "SysUserRoleSearchDTO", description = "用户角色管理 查询 DTO 对象")
public class SysUserRoleSearchDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6478610068516396360L;
}