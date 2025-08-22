/*
 * All Rights Reserved: Copyright [2024] [Zhuang Pan (paynezhuang@gmail.com)]
 * Open Source Agreement: Apache License, Version 2.0
 * For educational purposes only, commercial use shall comply with the author's copyright information.
 * The author does not guarantee or assume any responsibility for the risks of using software.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.dynamictp.admin.modules.system.facade;

import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.data.scope.SysRoleDataScopeAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.data.scope.SysRoleDataScopeDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.data.scope.SysRoleDataScopeSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.role.data.scope.SysRoleDataScopeUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleDataScopeVO;

import java.util.List;

/**
 * 角色数据权限关联管理 门面接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.facade.ISysRoleDataScopeFacade
 * @CreateTime 2025-05-10 - 21:40:18
 */

public interface ISysRoleDataScopeFacade {

    /**
     * 角色数据权限关联管理 - 分页查询
     *
     * @param pageQuery                 分页对象
     * @param sysRoleDataScopeSearchDTO 查询对象
     * @return {@link RPage} 查询结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:40:18
     */
    RPage<SysRoleDataScopeVO> listSysRoleDataScopePage(PageQuery pageQuery, SysRoleDataScopeSearchDTO sysRoleDataScopeSearchDTO);

    /**
     * 根据 ID 获取详情信息
     *
     * @param id 角色数据权限关联管理ID
     * @return {@link SysRoleDataScopeVO} 角色数据权限关联管理 VO 对象
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:40:18
     */
    SysRoleDataScopeVO get(Long id);

    /**
     * 新增角色数据权限关联管理
     *
     * @param sysRoleDataScopeAddDTO 新增角色数据权限关联管理 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:40:18
     */
    boolean add(SysRoleDataScopeAddDTO sysRoleDataScopeAddDTO);

    /**
     * 编辑更新角色数据权限关联管理信息
     *
     * @param sysRoleDataScopeUpdateDTO 编辑更新 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:40:18
     */
    boolean update(SysRoleDataScopeUpdateDTO sysRoleDataScopeUpdateDTO);

    /**
     * 批量删除角色数据权限关联管理信息
     *
     * @param sysRoleDataScopeDeleteDTO 删除 DTO 对象
     * @return @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:40:18
     */
    boolean batchDelete(SysRoleDataScopeDeleteDTO sysRoleDataScopeDeleteDTO);

    /**
     * 根据角色ID查询已配置的数据权限ID列表
     *
     * @param roleId 角色ID
     * @return {@link List }<{@link Long }> 数据权限ID列表
     * @author payne.zhuang
     * @CreateTime 2025-01-27 - 当前时间
     */
    List<Long> listDataScopeIdsByRoleId(Long roleId);

    /**
     * 保存角色ID及数据权限ID集合
     *
     * @param roleId       角色ID
     * @param dataScopeIds 数据权限ID集合
     * @return {@link Boolean} 保存结果
     * @author payne.zhuang
     * @CreateTime 2025-01-27 - 当前时间
     */
    boolean addDataScopeForRoleId(Long roleId, List<Long> dataScopeIds);

}