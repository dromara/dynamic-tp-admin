package org.dromara.dynamictp.admin.modules.system.domain.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysMenu;

import java.io.Serial;
import java.util.List;

/**
 * 菜单管理 BO 业务处理对象
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.bo.SysMenuBO
 * @CreateTime 2023-08-05
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SysMenuBO extends SysMenu {

    @Serial
    private static final long serialVersionUID = 1005843878098609281L;

    /**
     * Ids
     */
    private List<Long> ids;

}