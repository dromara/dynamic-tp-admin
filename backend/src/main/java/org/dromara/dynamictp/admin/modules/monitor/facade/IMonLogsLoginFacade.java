package org.dromara.dynamictp.admin.modules.monitor.facade;

import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.login.MonLogsLoginAddDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.login.MonLogsLoginDeleteDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.login.MonLogsLoginSearchDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.login.MonLogsLoginUpdateDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.vo.MonLogsLoginVO;

/**
 * 登录日志 门面接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.IMonLogsLoginFacade
 * @CreateTime 2024-05-05
 */

public interface IMonLogsLoginFacade {

    /**
     * 登录日志 - 分页查询
     *
     * @param pageQuery             分页对象
     * @param monLogsLoginSearchDTO 查询对象
     * @return {@link RPage} 查询结果
     * @author payne.zhuang
     * @CreateTime 2024-05-05 15:10
     */
    RPage<MonLogsLoginVO> listMonLogsLoginPage(PageQuery pageQuery, MonLogsLoginSearchDTO monLogsLoginSearchDTO);

    /**
     * 根据 ID 获取详情信息
     *
     * @param id 登录日志ID
     * @return {@link MonLogsLoginVO} 登录日志 VO 对象
     * @author payne.zhuang
     * @CreateTime 2024-05-05 15:10
     */
    MonLogsLoginVO get(Long id);

    /**
     * 新增登录日志
     *
     * @param monLogsLoginAddDTO 新增登录日志 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2024-05-05 15:10
     */
    boolean add(MonLogsLoginAddDTO monLogsLoginAddDTO);

    /**
     * 编辑更新登录日志信息
     *
     * @param monLogsLoginUpdateDTO 编辑更新 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2024-05-05 15:10
     */
    boolean update(MonLogsLoginUpdateDTO monLogsLoginUpdateDTO);

    /**
     * 批量删除登录日志信息
     *
     * @param monLogsLoginDeleteDTO 删除 DTO 对象
     * @return @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2024-05-05 15:10
     */
    boolean batchDelete(MonLogsLoginDeleteDTO monLogsLoginDeleteDTO);

}