package org.dromara.dynamictp.admin.modules.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.dynamictp.admin.infrastructure.domain.BaseEntity;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 客户端管理 Entity 实体类
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.domain.entity.ManClient
 * @CreateTime 2025/01/30 - 10:00
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("man_client")
public class ManClient extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 客户端ID
   */
  private String clientId;

  /**
   * 客户端名称
   */
  private String clientName;

  /**
   * 客户端类型
   */
  private String clientType;

  /**
   * 客户端版本
   */
  private String clientVersion;

  /**
   * 客户端IP地址
   */
  private String clientIp;

  /**
   * 客户端端口
   */
  private Integer clientPort;

  /**
   * 服务端IP地址
   */
  private String serverIp;

  /**
   * 服务端端口
   */
  private Integer serverPort;

  /**
   * 是否在线
   */
  private Boolean isOnline;

  /**
   * 最后心跳时间
   */
  private LocalDateTime lastHeartbeatTime;

  /**
   * 最后连接时间
   */
  private LocalDateTime lastConnectTime;

  /**
   * 最后断开时间
   */
  private LocalDateTime lastDisconnectTime;

  /**
   * 连接次数
   */
  private Integer connectCount;

  /**
   * 总在线时长(秒)
   */
  private Long totalOnlineTime;

  /**
   * 状态
   */
  private String status;

  /**
   * 备注
   */
  private String remark;
}