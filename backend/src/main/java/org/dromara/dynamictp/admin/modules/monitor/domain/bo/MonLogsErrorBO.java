package org.dromara.dynamictp.admin.modules.monitor.domain.bo;

import lombok.Data;
import org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsError;

import java.io.Serial;
import java.util.List;

/**
 * 错误异常日志 BO 业务处理对象
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsError
 * @CreateTime 2024-05-07
 */

@Data
public class MonLogsErrorBO extends MonLogsError {

    @Serial
    private static final long serialVersionUID = -8853366390854008297L;

    /**
     * Ids
     */
    private List<Long> ids;

}