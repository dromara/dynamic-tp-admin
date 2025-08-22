package org.dromara.dynamictp.admin.modules.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.dynamictp.admin.infrastructure.domain.BaseVO;

import java.io.Serial;

/**
 * 角色菜单管理 VO 展示类
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleMenuVO
 * @CreateTime 2023-08-05
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SysRoleMenuVO", description = "角色菜单管理 VO 对象")
public class SysRoleMenuVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 4267469222601196055L;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "菜单ID")
    private Long menuId;
}