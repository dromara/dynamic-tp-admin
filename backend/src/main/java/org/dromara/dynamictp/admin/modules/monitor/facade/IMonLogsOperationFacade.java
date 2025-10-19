package org.dromara.dynamictp.admin.modules.monitor.facade;

import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.operation.MonLogsOperationAddDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.operation.MonLogsOperationDeleteDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.operation.MonLogsOperationSearchDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.operation.MonLogsOperationUpdateDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.vo.MonLogsOperationVO;

/**
 * 操作日志 门面接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.IMonLogsOperationFacade
 * @CreateTime 2024-05-07
 */

public interface IMonLogsOperationFacade {

    /**
     * 操作日志 - 分页查询
     *
     * @param pageQuery                 分页对象
     * @param monLogsOperationSearchDTO 查询对象
     * @return {@link RPage} 查询结果
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    RPage<MonLogsOperationVO> listMonLogsOperationPage(PageQuery pageQuery, MonLogsOperationSearchDTO monLogsOperationSearchDTO);

    /**
     * 根据 ID 获取详情信息
     *
     * @param id 操作日志ID
     * @return {@link MonLogsOperationVO} 操作日志 VO 对象
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    MonLogsOperationVO get(Long id);

    /**
     * 新增操作日志
     *
     * @param monLogsOperationAddDTO 新增操作日志 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    boolean add(MonLogsOperationAddDTO monLogsOperationAddDTO);

    /**
     * 编辑更新操作日志信息
     *
     * @param monLogsOperationUpdateDTO 编辑更新 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    boolean update(MonLogsOperationUpdateDTO monLogsOperationUpdateDTO);

    /**
     * 批量删除操作日志信息
     *
     * @param monLogsOperationDeleteDTO 删除 DTO 对象
     * @return @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2024-05-07 15:10
     */
    boolean batchDelete(MonLogsOperationDeleteDTO monLogsOperationDeleteDTO);

}