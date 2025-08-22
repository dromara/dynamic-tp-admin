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

package org.dromara.dynamictp.admin.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleDataScopeBO;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleDataScopeQueryBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysRoleDataScope;

import java.util.List;

/**
 * 角色数据权限关联管理 Service 服务接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.ISysRoleDataScopeService
 * @CreateTime 2025-05-10 - 21:40:18
 */

public interface ISysRoleDataScopeService extends IService<SysRoleDataScope> {

    /**
     * 角色数据权限关联管理 - 分页查询
     *
     * @param pageQuery          分页对象
     * @param sysRoleDataScopeBO BO 查询对象
     * @return {@link IPage} 分页结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:40:18
     */
    IPage<SysRoleDataScope> listSysRoleDataScopePage(PageQuery pageQuery, SysRoleDataScopeBO sysRoleDataScopeBO);

    /**
     * 根据权限编码查询数据权限
     *
     * @param permissionCode 权限编码集合
     * @return {@link List }<{@link SysRoleDataScopeQueryBO }> 数据权限配置
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 13:58:06
     */
    List<SysRoleDataScopeQueryBO> listByPermissionCode(String permissionCode);

    /**
     * 根据角色ID查询已配置的数据权限ID列表
     *
     * @param roleId 角色ID
     * @return {@link List }<{@link Long }> 数据权限ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-23 17:05:28
     */
    List<Long> listDataScopeIdsByRoleId(Long roleId);

    /**
     * 保存角色ID及数据权限ID集合
     *
     * @param roleId       角色ID
     * @param dataScopeIds 数据权限ID集合
     * @return {@link Boolean} 保存结果
     * @author payne.zhuang
     * @CreateTime 2025-05-23 21:05:08
     */
    boolean addDataScopeForRoleId(Long roleId, List<Long> dataScopeIds);

}
