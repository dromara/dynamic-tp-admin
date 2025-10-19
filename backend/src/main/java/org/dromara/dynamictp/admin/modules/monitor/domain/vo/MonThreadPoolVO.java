package org.dromara.dynamictp.admin.modules.monitor.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.dynamictp.common.entity.ThreadPoolStats;

/**
 * 线程池监控 VO 对象，完全基于 ThreadPoolStats
 *
 * @Author eachann
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.vo.MonThreadPoolVO
 * @CreateTime 2025/07/29 - 10:00
 */
@Data
@Schema(name = "MonThreadPoolVO", description = "线程池监控 VO 对象（基于ThreadPoolStats）")
public class MonThreadPoolVO extends ThreadPoolStats {
}