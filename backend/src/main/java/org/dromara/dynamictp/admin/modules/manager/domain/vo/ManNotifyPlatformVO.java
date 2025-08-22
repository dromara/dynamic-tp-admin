package org.dromara.dynamictp.admin.modules.manager.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警渠道管理 VO 对象
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.domain.vo.ManNotifyPlatformVO
 */
@Data
@Schema(name = "ManNotifyPlatformVO", description = "告警渠道管理 VO 对象")
public class ManNotifyPlatformVO {

  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "告警平台ID")
  private String platformId;

  @Schema(description = "告警平台名称")
  private String platform;

  @Schema(description = "URL密钥")
  private String urlKey;

  @Schema(description = "密钥")
  private String secret;

  @Schema(description = "Webhook地址")
  private String webhook;

  @Schema(description = "接收者，多个用逗号分隔")
  private String receivers;

  @Schema(description = "HTTP请求超时时间（毫秒）")
  private Integer timeout;

  @Schema(description = "HTTP请求代理类型")
  private String proxyType;

  @Schema(description = "HTTP请求代理主机")
  private String proxyHost;

  @Schema(description = "HTTP请求代理端口")
  private Integer proxyPort;

  @Schema(description = "客户端ID")
  private String clientId;

  @Schema(description = "客户端名称")
  private String clientName;

  @Schema(description = "配置状态（ENABLE:启用,DISABLE:禁用）")
  private String status;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "创建用户名称")
  private String createUser;

  @Schema(description = "创建用户ID")
  private Long createUserId;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @Schema(description = "更新用户名称")
  private String updateUser;

  @Schema(description = "更新用户ID")
  private Long updateUserId;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;
}
