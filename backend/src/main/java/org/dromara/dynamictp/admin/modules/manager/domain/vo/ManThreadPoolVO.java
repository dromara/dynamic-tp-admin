package org.dromara.dynamictp.admin.modules.manager.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 线程池管理 VO 对象
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.manager.domain.vo.ManThreadPoolVO
 * @CreateTime 2025/01/30 - 10:00
 */
@Data
@Schema(name = "ManThreadPoolVO", description = "线程池管理 VO 对象")
public class ManThreadPoolVO {

  @Schema(description = "主键ID")
  private Long id;

  @Schema(description = "线程池名称")
  private String threadPoolName;

  @Schema(description = "线程池别名")
  private String threadPoolAliasName;

  @Schema(description = "核心线程数")
  private Integer corePoolSize;

  @Schema(description = "最大线程数")
  private Integer maximumPoolSize;

  @Schema(description = "队列容量")
  private Integer queueCapacity;

  @Schema(description = "队列类型")
  private String queueType;

  @Schema(description = "拒绝策略")
  private String rejectedExecutionType;

  @Schema(description = "执行器类型")
  private String executorType;

  @Schema(description = "线程存活时间（秒）")
  private Long keepAliveTime;

  @Schema(description = "是否允许核心线程超时")
  private Boolean allowCoreThreadTimeOut;

  @Schema(description = "线程名称前缀")
  private String threadNamePrefix;

  @Schema(description = "执行超时时间（毫秒）")
  private Long runTimeout;

  @Schema(description = "队列超时时间（毫秒）")
  private Long queueTimeout;

  @Schema(description = "任务包装器名称列表，逗号分隔")
  private String taskWrapperNames;

  @Schema(description = "关闭时是否等待任务完成")
  private Boolean waitForTasksToCompleteOnShutdown;

  @Schema(description = "等待终止的秒数")
  private Long awaitTerminationSeconds;

  @Schema(description = "是否预启动所有核心线程")
  private Boolean preStartAllCoreThreads;

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

  @Schema(description = "通知配置列表")
  private List<ManNotifyItemVO> notifyItems;
}