package org.dromara.dynamictp.admin.modules.system.facade;

import org.dromara.dynamictp.admin.common.domain.Options;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.SysRoleAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.SysRoleDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.SysRoleSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.SysRoleUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleExportVO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleVO;

import java.util.List;

/**
 * 角色管理 门面接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.facade.ISysRoleFacade
 * @CreateTime 2023-07-23
 */

public interface ISysRoleFacade {

    /**
     * 角色管理 - 分页查询
     *
     * @param pageQuery        分页对象
     * @param sysRoleSearchDTO 查询对象
     * @return {@link RPage} 查询结果
     * @author payne.zhuang
     * @CreateTime 2023-07-23 15:10
     */
    RPage<SysRoleVO> listSysRolePage(PageQuery pageQuery, SysRoleSearchDTO sysRoleSearchDTO);

    /**
     * 根据 ID 获取详情信息
     *
     * @param id 角色ID
     * @return {@link SysRoleVO} 角色 VO 对象
     * @author payne.zhuang
     * @CreateTime 2023-07-23 15:10
     */
    SysRoleVO get(Long id);

    /**
     * 新增角色
     *
     * @param sysRoleAddDTO 新增角色 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2023-07-23 15:10
     */
    boolean add(SysRoleAddDTO sysRoleAddDTO);

    /**
     * 编辑更新角色信息
     *
     * @param sysRoleUpdateDTO 编辑更新 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2023-07-23 15:10
     */
    boolean update(SysRoleUpdateDTO sysRoleUpdateDTO);

    /**
     * 批量删除角色信息
     *
     * @param sysRoleDeleteDTO 删除 DTO 对象
     * @return @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2023-07-23 15:10
     */
    boolean batchDelete(SysRoleDeleteDTO sysRoleDeleteDTO);

    /**
     * 获取所有角色信息集合
     *
     * @return {@linkplain List} 角色集合
     * @author payne.zhuang
     * @CreateTime 2024-04-06 18:57
     */
    List<Options<Long>> queryAllRoleListConvertOptions();

    /**
     * 获取所有角色信息集合
     *
     * @return {@link List }<{@link SysRoleExportVO }> 角色集合
     * @author payne.zhuang
     * @CreateTime 2025-04-05 - 12:28:51
     */
    List<SysRoleExportVO> queryAllExportRoleList();
}