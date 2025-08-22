package org.dromara.dynamictp.admin.modules.monitor.facade;

import org.dromara.dynamictp.admin.modules.monitor.domain.vo.MonSystemVO;

/**
 * 系统服务监控 门面接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.facade.IMonSystemFacade
 * @CreateTime 2024/5/1 - 23:31
 */
public interface IMonSystemFacade {

    /**
     * 获取服务器信息
     *
     * @return {@linkplain MonSystemVO} 服务器信息
     * @author payne.zhuang
     * @CreateTime 2024-05-01 23:39
     */
    MonSystemVO getServerInfo();
}
