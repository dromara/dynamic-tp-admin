package org.dromara.dynamictp.admin.modules.monitor.domain.bo;

import lombok.Data;
import org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsLogin;

import java.io.Serial;
import java.util.List;

/**
 * 登录日志 BO 业务处理对象
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsLogin
 * @CreateTime 2024-05-05
 */

@Data
public class MonLogsLoginBO extends MonLogsLogin {

    @Serial
    private static final long serialVersionUID = 7129959475004671045L;

    /**
     * Ids
     */
    private List<Long> ids;

}