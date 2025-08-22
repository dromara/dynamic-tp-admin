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

package org.dromara.dynamictp.admin.modules.system.repository.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysUserOrg;

import java.util.List;
import java.util.Set;

/**
 * 用户组织/部门/子部门管理 Mapper 接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.repository.mapper.SysUserOrgMapper
 * @CreateTime 2024-07-16 - 16:35:30
 */

public interface SysUserOrgMapper extends BaseMapper<SysUserOrg> {

    /**
     * 根据用户id查询用户组织
     *
     * @param userId 用户id
     * @return {@link List }<{@link SysUserOrg }> 用户组织列表
     * @author payne.zhuang
     * @CreateTime 2024-07-20 - 15:15:01
     */
    List<SysUserOrg> listUserOrgWithUserId(@Param("userId") Long userId);

    /**
     * 更新用户组织负责人
     *
     * @param userId 用户id
     * @param orgIds 组织id
     * @return {@link Boolean } 是否更新成功 true:成功 false:失败
     * @author payne.zhuang
     * @CreateTime 2024-07-22 - 11:35:13
     */
    boolean updatePrincipal(@Param("userId") Long userId, @Param("orgIds") Set<Long> orgIds);

    /**
     * 获取用户组织ID列表
     *
     * @param userId 用户ID
     * @return {@link List }<{@link Long }> 组织ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-29 16:15:12
     */
    List<Long> getUserOrgIds(@Param("userId") Long userId);

    /**
     * 根据组织ID列表获取用户ID列表
     *
     * @param orgIds 组织ID列表
     * @return {@link List }<{@link Long }> 用户ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-29 16:15:14
     */
    List<Long> getUserIdsByOrgIds(@Param("orgIds") List<Long> orgIds);

    /**
     * 获取用户担任负责人的组织ID列表
     *
     * @param userId 用户ID
     * @return {@link List }<{@link Long }> 负责组织ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-29 22:58:23
     */
    List<Long> getPrincipalOrgIds(@Param("userId") Long userId);

    /**
     * 查询本组织及下级组织用户ID（数据权限专用 - UNIT_AND_CHILD类型）
     * 本组织用户无需负责人身份，下级组织用户需要负责人身份
     *
     * @param userId 用户ID
     * @return {@link List }<{@link Long }> 用户ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-29 22:58:25
     */
    List<Long> getUserIdsByUnitAndChild(@Param("userId") Long userId);

    /**
     * 查询本人及下级组织用户ID（数据权限专用 - SELF_AND_CHILD类型）
     * 仅限负责人可查看下级组织数据，普通用户只能查看本人数据
     *
     * @param userId 用户ID
     * @return {@link List }<{@link Long }> 用户ID列表
     * @author payne.zhuang
     * @CreateTime 2025-05-29 22:58:50
     */
    List<Long> getUserIdsBySelfAndChildWithPrincipal(@Param("userId") Long userId);
}