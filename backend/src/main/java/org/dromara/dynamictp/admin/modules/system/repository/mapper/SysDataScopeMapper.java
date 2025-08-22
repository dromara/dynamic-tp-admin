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
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysDataScope;

import java.util.List;

/**
 * 数据权限管理 Mapper 接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.repository.mapper.SysDataScopeMapper
 * @CreateTime 2025-05-10 - 21:38:29
 */

public interface SysDataScopeMapper extends BaseMapper<SysDataScope> {

    /**
     * 获取用户组织ID列表（基础查询，用于缓存）
     */
    List<Long> getUserOrgIds(@Param("userId") Long userId);

    /**
     * 本组织数据权限 - 单一SQL（简单高效）
     */
    List<Long> getUserIdsByUnit(@Param("orgIds") List<Long> orgIds);

    /**
     * 本组织及以下数据权限 - 分层查询第一步
     */
    List<Long> getSameOrgUsers(@Param("orgIds") List<Long> orgIds);

    /**
     * 本组织及以下数据权限 - 分层查询第二步
     */
    List<Long> getChildOrgUsers(@Param("ancestorsPattern") String ancestorsPattern,
                                @Param("excludeOrgIds") List<Long> excludeOrgIds);

    /**
     * 本人及下级组织数据权限 - 优化查询
     */
    List<Long> getSubordinateUsers(@Param("userId") Long userId,
                                   @Param("ancestorsPattern") String ancestorsPattern);

}