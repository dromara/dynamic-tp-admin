package org.dromara.dynamictp.admin.modules.manager.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 线程池通知配置 VO 对象
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.manager.domain.vo.ManNotifyItemVO
 * @CreateTime 2025/01/30 - 10:00
 */
@Data
@Schema(name = "ManNotifyItemVO", description = "线程池通知配置 VO 对象")
public class ManNotifyItemVO {

  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "线程池ID")
  private Long threadPoolId;

  @Schema(description = "通知类型")
  private String type;

  @Schema(description = "通知类型名称")
  private String typeName;

  @Schema(description = "是否启用通知")
  private Boolean enabled;

  @Schema(description = "指标检测阈值")
  private Integer threshold;

  @Schema(description = "触发告警的次数")
  private Integer count;

  @Schema(description = "检测周期（秒）")
  private Integer period;

  @Schema(description = "静默期（秒）")
  private Integer silencePeriod;

  @Schema(description = "集群通知限制")
  private Integer clusterLimit;

  @Schema(description = "接收者，多个用逗号分隔")
  private String receivers;

  @Schema(description = "通知平台ID列表")
  private List<String> platformIds;

  @Schema(description = "通知平台名称列表")
  private List<String> platformNames;

  @Schema(description = "客户端ID")
  private String clientId;

  @Schema(description = "客户端名称")
  private String clientName;

  @Schema(description = "配置状态（ENABLE:启用,DISABLE:禁用）")
  private String status;

  @Schema(description = "备注")
  private String remark;

  @Schema(description = "创建时间")
  private LocalDateTime createTime;

  @Schema(description = "更新时间")
  private LocalDateTime updateTime;

  @Schema(description = "创建人")
  private Long createUser;

  @Schema(description = "更新人")
  private Long updateUser;
}
