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
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysRoleDataScopeQueryBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysRoleDataScope;

import java.util.List;

/**
 * 角色数据权限关联管理 Mapper 接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.repository.mapper.SysRoleDataScopeMapper
 * @CreateTime 2025-05-10 - 21:40:18
 */

public interface SysRoleDataScopeMapper extends BaseMapper<SysRoleDataScope> {

    /**
     * 根据权限编码查询数据权限
     *
     * @param permissionCode 权限编码
     * @return {@link List }<{@link SysRoleDataScopeQueryBO }> 数据权限配置
     * @author payne.zhuang
     * @CreateTime 2025-05-12 - 20:37:51
     */
    List<SysRoleDataScopeQueryBO> listByPermissionCode(String permissionCode);
}