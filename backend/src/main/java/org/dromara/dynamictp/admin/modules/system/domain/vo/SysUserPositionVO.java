package org.dromara.dynamictp.admin.modules.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.dynamictp.admin.infrastructure.domain.BaseVO;

import java.io.Serial;

/**
 * 用户岗位管理 VO 展示类
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.vo.SysUserPositionVO
 * @CreateTime 2024-06-26 - 22:14:38
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SysUserPositionVO", description = "用户岗位管理 VO 对象")
public class SysUserPositionVO extends BaseVO {

    @Serial
    private static final long serialVersionUID = 1738188588248060412L;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "岗位ID")
    private Long positionId;
}