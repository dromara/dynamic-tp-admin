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
import org.dromara.dynamictp.admin.infrastructure.context.DataScopeConditionContext;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysDataScopeBO;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleDataScopeQueryBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysDataScope;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.domain.DataScopeCondition;
import org.dromara.dynamictp.admin.starter.database.mybatis.plus.enums.DataScopeTypeEnum;

import java.util.List;
import java.util.Set;

/**
 * 数据权限管理 Service 服务接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.ISysDataScopeService
 * @CreateTime 2025-05-10 - 21:38:29
 */

public interface ISysDataScopeService extends IService<SysDataScope> {

    /**
     * 数据权限管理 - 分页查询
     *
     * @param pageQuery      分页对象
     * @param sysDataScopeBO BO 查询对象
     * @return {@link IPage} 分页结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:38:29
     */
    IPage<SysDataScope> listSysDataScopePage(PageQuery pageQuery, SysDataScopeBO sysDataScopeBO);

    /**
     * 数据权限管理 - 查询
     *
     * @param sysDataScopeBO BO 查询对象
     * @return {@link List }<{@link SysDataScope }>
     * @author payne.zhuang
     * @CreateTime 2025-05-23 - 17:29:59
     */
    List<SysDataScope> listSysDataScope(SysDataScopeBO sysDataScopeBO);

    /**
     * 数据权限管理 - 新增
     *
     * @param sysDataScopeBO BO 新增对象
     * @return boolean 是否成功
     * @author payne.zhuang
     * @CreateTime 2025-05-22 - 20:53:01
     */
    boolean add(SysDataScopeBO sysDataScopeBO);

    /**
     * 数据权限管理 - 更新
     *
     * @param sysDataScopeBO BO 更新对象
     * @return boolean 是否成功
     * @author payne.zhuang
     * @CreateTime 2025-05-22 - 20:53:49
     */
    boolean update(SysDataScopeBO sysDataScopeBO);

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
     * 根据权限类型获取用户ID集合
     *
     * @param userId    当前用户ID
     * @param scopeType 权限类型
     * @return {@link Set}<{@link Long }> 用户ID集合
     * @author payne.zhuang
     * @CreateTime 2025-05-29 16:12:47
     */
    Set<Long> getUserIdsByScopeType(Long userId, DataScopeTypeEnum scopeType);

    /**
     * 获取本组织数据权限用户ID列表
     *
     * @param userId 用户ID
     * @return {@link Set}<{@link Long }> 用户ID集合
     * @author payne.zhuang
     * @CreateTime 2025-05-29 16:12:41
     */
    Set<Long> getUserIdsByUnitScope(Long userId);

    /**
     * 获取本组织及以下数据权限用户ID列表
     *
     * @param userId 用户ID
     * @return {@link Set}<{@link Long }> 用户ID集合
     * @author payne.zhuang
     * @CreateTime 2025-05-29 16:12:55
     */
    Set<Long> getUserIdsByUnitAndChildScope(Long userId);

    /**
     * 获取本人及下级组织数据权限用户ID列表
     *
     * @param userId 用户ID
     * @return {@link Set}<{@link Long }> 用户ID集合
     * @author payne.zhuang
     * @CreateTime 2025-05-29 16:13:41
     */
    Set<Long> getUserIdsBySelfAndChildScope(Long userId);

    List<DataScopeConditionContext> buildDataScopeVariableValue(Long userId, List<DataScopeCondition> customConditions);
}
