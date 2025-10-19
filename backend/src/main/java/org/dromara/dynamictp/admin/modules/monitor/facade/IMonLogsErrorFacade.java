package org.dromara.dynamictp.admin.modules.monitor.facade;

import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.exception.MonLogsErrorAddDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.exception.MonLogsErrorDeleteDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.exception.MonLogsErrorSearchDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.exception.MonLogsErrorUpdateDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.vo.MonLogsErrorVO;

/**
 * 错误异常日志 门面接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.IMonLogsErrorFacade
 * @CreateTime 2024-05-07
 */

public interface IMonLogsErrorFacade {

    /**
     * 错误异常日志 - 分页查询
     *
     * @param pageQuery             分页对象
     * @param monLogsErrorSearchDTO 查询对象
     * @return {@link RPage} 查询结果
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    RPage<MonLogsErrorVO> listMonLogsErrorPage(PageQuery pageQuery, MonLogsErrorSearchDTO monLogsErrorSearchDTO);

    /**
     * 根据 ID 获取详情信息
     *
     * @param id 异常错误ID
     * @return {@link MonLogsErrorVO} 异常错误 VO 对象
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    MonLogsErrorVO get(Long id);

    /**
     * 新增异常错误
     *
     * @param monLogsErrorAddDTO 新增异常错误 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    boolean add(MonLogsErrorAddDTO monLogsErrorAddDTO);

    /**
     * 编辑更新异常错误信息
     *
     * @param monLogsErrorUpdateDTO 编辑更新 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    boolean update(MonLogsErrorUpdateDTO monLogsErrorUpdateDTO);

    /**
     * 批量删除异常错误信息
     *
     * @param monLogsErrorDeleteDTO 删除 DTO 对象
     * @return @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    boolean batchDelete(MonLogsErrorDeleteDTO monLogsErrorDeleteDTO);

}