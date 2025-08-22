package org.dromara.dynamictp.admin.modules.monitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonLogsErrorBO;
import org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsError;

/**
 * 错误异常日志 Service 服务接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsError
 * @CreateTime 2024-05-07
 */

public interface IMonLogsErrorService extends IService<MonLogsError> {
    /**
     * 错误异常日志 - 分页查询
     *
     * @param pageQuery      分页对象
     * @param MonLogsErrorBO BO 查询对象
     * @return {@link IPage} 分页结果
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    IPage<MonLogsError> listMonLogsErrorPage(PageQuery pageQuery, MonLogsErrorBO monLogsErrorBO);
}
