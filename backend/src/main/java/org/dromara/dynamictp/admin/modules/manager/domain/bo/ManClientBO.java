package org.dromara.dynamictp.admin.modules.manager.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 客户端管理 BO 对象
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.domain.bo.ManClientBO
 */
@Data
@Schema(name = "ManClientBO", description = "客户端管理 BO 对象")
public class ManClientBO {

  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "客户端ID")
  private String clientId;

  @Schema(description = "客户端名称")
  private String clientName;

  @Schema(description = "客户端类型")
  private String clientType;

  @Schema(description = "客户端版本")
  private String clientVersion;

  @Schema(description = "客户端IP地址")
  private String clientIp;

  @Schema(description = "客户端端口")
  private Integer clientPort;

  @Schema(description = "服务端IP地址")
  private String serverIp;

  @Schema(description = "服务端端口")
  private Integer serverPort;

  @Schema(description = "是否在线")
  private Boolean isOnline;

  @Schema(description = "最后心跳时间")
  private LocalDateTime lastHeartbeatTime;

  @Schema(description = "最后连接时间")
  private LocalDateTime lastConnectTime;

  @Schema(description = "最后断开时间")
  private LocalDateTime lastDisconnectTime;

  @Schema(description = "连接次数")
  private Integer connectCount;

  @Schema(description = "总在线时长(秒)")
  private Long totalOnlineTime;

  @Schema(description = "状态")
  private String status;

  @Schema(description = "备注")
  private String remark;
}