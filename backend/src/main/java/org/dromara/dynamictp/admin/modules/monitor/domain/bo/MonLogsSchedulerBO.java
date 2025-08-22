package org.dromara.dynamictp.admin.modules.monitor.domain.bo;

import lombok.Data;
import org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsScheduler;

import java.io.Serial;
import java.util.List;

/**
 * 调度日志 BO 业务处理对象
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsScheduler
 * @CreateTime 2024-05-30
 */

@Data
public class MonLogsSchedulerBO extends MonLogsScheduler {

    @Serial
    private static final long serialVersionUID = -7464199160527154428L;

    /**
     * Ids
     */
    private List<Long> ids;

}