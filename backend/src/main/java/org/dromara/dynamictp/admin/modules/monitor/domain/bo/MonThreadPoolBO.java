package org.dromara.dynamictp.admin.modules.monitor.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 线程池监控 BO 对象
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonThreadPoolBO
 */
@Data
@Schema(name = "MonThreadPoolBO", description = "线程池监控 BO 对象")
public class MonThreadPoolBO {

  @Schema(description = "线程池名称")
  private String poolName;

  @Schema(description = "线程池别名")
  private String poolAliasName;

  @Schema(description = "队列类型")
  private String queueType;

  @Schema(description = "线程池状态")
  private String poolStatus;

  @Schema(description = "最小活跃线程数")
  private Integer minActiveThreads;

  @Schema(description = "最大活跃线程数")
  private Integer maxActiveThreads;

  @Schema(description = "最小队列使用率")
  private Double minQueueUsageRate;

  @Schema(description = "最大队列使用率")
  private Double maxQueueUsageRate;

  @Schema(description = "最小线程池使用率")
  private Double minPoolUsageRate;

  @Schema(description = "最大线程池使用率")
  private Double maxPoolUsageRate;
}