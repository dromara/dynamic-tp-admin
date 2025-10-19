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
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysUserOrgBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysUserOrg;

import java.util.List;
import java.util.Set;

/**
 * 用户组织/部门/子部门管理 Service 服务接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.ISysUserOrgService
 * @CreateTime 2024-07-16 - 16:35:30
 */

public interface ISysUserOrgService extends IService<SysUserOrg> {

    /**
     * 用户组织/部门/子部门管理 - 分页查询
     *
     * @param pageQuery    分页对象
     * @param sysUserOrgBO BO 查询对象
     * @return {@link IPage} 分页结果
     * @author payne.zhuang
     * @CreateTime 2024-07-16 - 16:35:30
     */
    IPage<SysUserOrg> listSysUserOrgPage(PageQuery pageQuery, SysUserOrgBO sysUserOrgBO);

    /**
     * 查询用户组织
     *
     * @param userId 用户id
     * @return {@link List }<{@link SysUserOrgBO }> 用户组织列表
     * @author payne.zhuang
     * @CreateTime 2024-07-21 - 18:22:14
     */
    List<SysUserOrgBO> queryOrgUnitsListWithUserId(Long userId);

    /**
     * 查询用户组织id
     *
     * @param userId 用户id
     * @return {@link Set }<{@link Long }> 用户组织id集合
     * @author payne.zhuang
     * @CreateTime 2024-07-20 - 15:10:55
     */
    Set<Long> queryOrgUnitsIdsWithUserId(Long userId);

    /**
     * 查询用户组织负责人
     *
     * @param userId 用户id
     * @return {@link List }<{@link Long }> 用户组织负责人id集合
     * @author payne.zhuang
     * @CreateTime 2024-07-21 - 18:23:42
     */
    List<Long> queryOrgUnitsPrincipalWithUserId(Long userId);

    /**
     * 更新用户组织
     *
     * @param userId       用户id
     * @param orgIds       组织id
     * @param principalIds 负责人id
     * @return {@link Boolean} 更新结果
     * @author payne.zhuang
     * @CreateTime 2024-07-20 - 23:19:25
     */
    boolean updateUserOrg(Long userId, List<Long> orgIds, List<Long> principalIds);

    /**
     * 获取用户组织ID列表
     *
     * @param userId 用户ID
     * @return {@link List }<{@link Long }> 组织ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-29 16:14:56
     */
    List<Long> getUserOrgIds(Long userId);

    /**
     * 根据组织ID列表获取用户ID列表
     *
     * @param orgIds 组织ID列表
     * @return {@link List }<{@link Long }> 用户ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-29 16:15:00
     */
    List<Long> getUserIdsByOrgIds(List<Long> orgIds);

    /**
     * 获取用户担任负责人的组织ID列表
     *
     * @param userId 用户ID
     * @return {@link List }<{@link Long }> 负责组织ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-29 22:57:38
     */
    List<Long> getPrincipalOrgIds(Long userId);

    /**
     * 查询本组织及下级组织用户ID（数据权限专用 - UNIT_AND_CHILD类型）
     * 本组织用户无需负责人身份，下级组织用户需要负责人身份
     *
     * @param userId 用户ID
     * @return {@link List }<{@link Long }> 用户ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-29 22:57:46
     */
    List<Long> getUserIdsByUnitAndChild(Long userId);

    /**
     * 查询本人及下级组织用户ID（数据权限专用 - SELF_AND_CHILD类型）
     * 仅限负责人可查看下级组织数据，普通用户只能查看本人数据
     *
     * @param userId 用户ID
     * @return {@link List }<{@link Long }> 用户ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-29 22:57:50
     */
    List<Long> getUserIdsBySelfAndChildWithPrincipal(Long userId);

}
