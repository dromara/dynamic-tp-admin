package org.dromara.dynamictp.admin.modules.system.domain.bo;

import lombok.Data;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysPermission;

import java.io.Serial;
import java.util.List;

/**
 * 权限管理 BO 业务处理对象
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.bo.SysPermissionBO
 * @CreateTime 2023-08-05
 */
@Data
public class SysPermissionBO extends SysPermission {

    @Serial
    private static final long serialVersionUID = -3823638721064021026L;

    /**
     * Ids
     */
    private List<Long> ids;

}