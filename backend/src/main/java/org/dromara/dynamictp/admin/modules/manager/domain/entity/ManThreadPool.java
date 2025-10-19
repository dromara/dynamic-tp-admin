package org.dromara.dynamictp.admin.modules.manager.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.dynamictp.admin.infrastructure.domain.BaseEntity;

import java.io.Serial;

/**
 * 线程池管理 Entity 实体类
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.manager.domain.entity.ManThreadPool
 * @CreateTime 2025/01/30 - 10:00
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("man_thread_pool")
public class ManThreadPool extends BaseEntity {

  @Serial
  private static final long serialVersionUID = 1L;

  /**
   * 线程池名称
   */
  private String threadPoolName;

  /**
   * 线程池别名
   */
  private String threadPoolAliasName;

  /**
   * 核心线程数
   */
  private Integer corePoolSize;

  /**
   * 最大线程数
   */
  private Integer maximumPoolSize;

  /**
   * 队列容量
   */
  private Integer queueCapacity;

  /**
   * 队列类型
   */
  private String queueType;

  /**
   * 拒绝策略
   */
  private String rejectedExecutionType;

  /**
   * 执行器类型
   */
  private String executorType;

  /**
   * 线程存活时间（秒）
   */
  private Long keepAliveTime;

  /**
   * 是否允许核心线程超时
   */
  private Boolean allowCoreThreadTimeOut;

  /**
   * 线程名称前缀
   */
  private String threadNamePrefix;

  /**
   * 执行超时时间（毫秒）
   */
  private Long runTimeout;

  /**
   * 队列超时时间（毫秒）
   */
  private Long queueTimeout;

  /**
   * 任务包装器名称列表，逗号分隔
   */
  private String taskWrapperNames;

  /**
   * 关闭时是否等待任务完成
   */
  private Boolean waitForTasksToCompleteOnShutdown;

  /**
   * 等待终止的秒数
   */
  private Long awaitTerminationSeconds;

  /**
   * 是否预启动所有核心线程
   */
  private Boolean preStartAllCoreThreads;

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