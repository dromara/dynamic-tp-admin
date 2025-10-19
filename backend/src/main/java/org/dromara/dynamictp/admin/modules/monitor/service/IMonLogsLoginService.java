package org.dromara.dynamictp.admin.modules.monitor.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.monitor.domain.bo.MonLogsLoginBO;
import org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsLogin;

/**
 * 登录日志 Service 服务接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonLogsLogin
 * @CreateTime 2024-05-05
 */

public interface IMonLogsLoginService extends IService<MonLogsLogin> {
    /**
     * 登录日志 - 分页查询
     *
     * @param pageQuery      分页对象
     * @param monLogsLoginBO BO 查询对象
     * @return {@link IPage} 分页结果
     * @author payne.zhuang
     * @CreateTime 2024-05-05 15:10
     */
    IPage<MonLogsLogin> listMonLogsLoginPage(PageQuery pageQuery, MonLogsLoginBO monLogsLoginBO);

}
