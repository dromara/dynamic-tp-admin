package org.dromara.dynamictp.admin.modules.manager.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 通知类型枚举
 *
 * @Author eachann
 * @ProjectName panis-boot
 * @ClassName org.dromara.dynamictp.admin.modules.manager.domain.enums.NotifyItemEnum
 * @CreateTime 2025/01/30 - 10:00
 */
@Getter
@AllArgsConstructor
public enum NotifyItemEnum {

  /**
   * 线程池变更通知
   */
  CHANGE("CHANGE", "线程池变更", 1, 1, 120, 1),

  /**
   * 线程池活跃度通知
   */
  LIVENESS("LIVENESS", "线程池活跃度", 70, 1, 120, 120),

  /**
   * 线程池容量通知
   */
  CAPACITY("CAPACITY", "线程池容量", 70, 1, 120, 120),

  /**
   * 任务拒绝通知
   */
  REJECT("REJECT", "任务拒绝", 1, 1, 120, 120),

  /**
   * 执行超时通知
   */
  RUN_TIMEOUT("RUN_TIMEOUT", "执行超时", 10, 10, 120, 120),

  /**
   * 队列超时通知
   */
  QUEUE_TIMEOUT("QUEUE_TIMEOUT", "队列超时", 10, 10, 120, 120);

  /**
   * 通知类型值
   */
  private final String value;

  /**
   * 通知类型名称
   */
  private final String name;

  /**
   * 默认阈值
   */
  private final Integer defaultThreshold;

  /**
   * 默认触发次数
   */
  private final Integer defaultCount;

  /**
   * 默认检测周期（秒）
   */
  private final Integer defaultPeriod;

  /**
   * 默认静默期（秒）
   */
  private final Integer defaultSilencePeriod;

  /**
   * 根据值获取枚举
   */
  public static NotifyItemEnum of(String value) {
    for (NotifyItemEnum item : values()) {
      if (item.getValue().equals(value)) {
        return item;
      }
    }
    return null;
  }

  /**
   * 获取所有通知类型
   */
  public static NotifyItemEnum[] getAllTypes() {
    return values();
  }
}
