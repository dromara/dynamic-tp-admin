package org.dromara.dynamictp.admin.modules.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.dynamictp.admin.infrastructure.domain.BaseEntity;

import java.io.Serial;

/**
 * 告警渠道管理 Entity 实体类
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyPlatform
 * @CreateTime 2025/01/30 - 10:00
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("man_notify_platform")
public class ManNotifyPlatform extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 告警平台ID
   */
  private String platformId;

  /**
   * 告警平台名称
   */
  private String platform;

  /**
   * URL密钥
   */
  private String urlKey;

  /**
   * 密钥
   */
  private String secret;

  /**
   * Webhook地址
   */
  private String webhook;

  /**
   * 接收者，多个用逗号分隔
   */
  private String receivers;

  /**
   * HTTP请求超时时间（毫秒）
   */
  private Integer timeout;

  /**
   * HTTP请求代理类型
   */
  private String proxyType;

  /**
   * HTTP请求代理主机
   */
  private String proxyHost;

  /**
   * HTTP请求代理端口
   */
  private Integer proxyPort;

  /**
   * 客户端ID
   */
  private String clientId;

  /**
   * 客户端名称
   */
  private String clientName;

  /**
   * 配置状态（ENABLE:启用,DISABLE:禁用）
   */
  private String status;

  /**
   * 备注
   */
  private String remark;
}
