package org.dromara.dynamictp.admin.modules.system.domain.bo;

import lombok.Data;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysRoleMenu;

import java.io.Serial;
import java.util.List;

/**
 * 角色菜单管理 BO 业务处理对象
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleMenuBO
 * @CreateTime 2023-08-05
 */
@Data
public class SysRoleMenuBO extends SysRoleMenu {

    @Serial
    private static final long serialVersionUID = -1447245677812383240L;

    /**
     * Ids
     */
    private List<Long> ids;

    /**
     * 角色 ID
     */
    private Long roleId;

    /**
     * 菜单 IDs
     */
    private List<Long> menuIds;

}