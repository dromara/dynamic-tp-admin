package org.dromara.dynamictp.admin.modules.system.facade;

import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.dto.permission.SysPermissionAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.permission.SysPermissionDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.permission.SysPermissionSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.permission.SysPermissionUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysPermissionVO;

/**
 * 权限管理 门面接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.facade.ISysPermissionFacade
 * @CreateTime 2023-08-05
 */

public interface ISysPermissionFacade {

    /**
     * 权限管理 - 分页查询
     *
     * @param pageQuery              分页对象
     * @param sysPermissionSearchDTO 查询对象
     * @return {@link RPage} 查询结果
     * @author payne.zhuang
     * @CreateTime 2023-08-05 15:10
     */
    RPage<SysPermissionVO> listSysPermissionPage(PageQuery pageQuery, SysPermissionSearchDTO sysPermissionSearchDTO);

    /**
     * 根据 ID 获取详情信息
     *
     * @param id 权限ID
     * @return {@link SysPermissionVO} 权限 VO 对象
     * @author payne.zhuang
     * @CreateTime 2023-08-05 15:10
     */
    SysPermissionVO get(Long id);

    /**
     * 新增权限
     *
     * @param sysPermissionAddDTO 新增权限 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2023-08-05 15:10
     */
    boolean add(SysPermissionAddDTO sysPermissionAddDTO);

    /**
     * 编辑更新权限信息
     *
     * @param sysPermissionUpdateDTO 编辑更新 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2023-08-05 15:10
     */
    boolean update(SysPermissionUpdateDTO sysPermissionUpdateDTO);

    /**
     * 批量删除权限信息
     *
     * @param sysPermissionDeleteDTO 删除 DTO 对象
     * @return @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2023-08-05 15:10
     */
    boolean batchDelete(SysPermissionDeleteDTO sysPermissionDeleteDTO);

}