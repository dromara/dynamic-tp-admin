package org.dromara.dynamictp.admin.modules.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.dynamictp.admin.infrastructure.domain.BaseEntity;

import java.io.Serial;

/**
 * 线程池通知配置 Entity 实体类
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.manager.domain.entity.ManNotifyItem
 * @CreateTime 2025/01/30 - 10:00
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("man_notify_item")
public class ManNotifyItem extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 线程池ID
   */
  private Long threadPoolId;

  /**
   * 通知类型
   */
  private String type;

  /**
   * 是否启用通知
   */
  private Boolean enabled;

  /**
   * 指标检测阈值
   */
  private Integer threshold;

  /**
   * 触发告警的次数
   */
  private Integer count;

  /**
   * 检测周期（秒）
   */
  private Integer period;

  /**
   * 静默期（秒）
   */
  private Integer silencePeriod;

  /**
   * 集群通知限制
   */
  private Integer clusterLimit;

  /**
   * 接收者，多个用逗号分隔
   */
  private String receivers;

  /**
   * 通知平台ID列表，JSON格式存储
   */
  private String platformIds;

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
