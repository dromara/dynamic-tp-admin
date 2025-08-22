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
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.data.scope.SysDataScopeUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeEditVO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeTreeVO;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysDataScopeVO;

import java.util.List;
import java.util.Map;

/**
 * 数据权限管理 门面接口层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.facade.ISysDataScopeFacade
 * @CreateTime 2025-05-10 - 21:38:29
 */

public interface ISysDataScopeFacade {

    /**
     * 数据权限管理 - 分页查询
     *
     * @param pageQuery             分页对象
     * @param sysDataScopeSearchDTO 查询对象
     * @return {@link RPage} 查询结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:38:29
     */
    RPage<SysDataScopeVO> listSysDataScopePage(PageQuery pageQuery, SysDataScopeSearchDTO sysDataScopeSearchDTO);

    /**
     * 根据 ID 获取详情信息
     *
     * @param id 数据权限管理ID
     * @return {@link SysDataScopeEditVO} 数据权限管理编辑 VO 对象
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:38:29
     */
    SysDataScopeEditVO get(Long id);

    /**
     * 新增数据权限管理
     *
     * @param sysDataScopeAddDTO 新增数据权限管理 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:38:29
     */
    boolean add(SysDataScopeAddDTO sysDataScopeAddDTO);

    /**
     * 编辑更新数据权限管理信息
     *
     * @param sysDataScopeUpdateDTO 编辑更新 DTO 对象
     * @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:38:29
     */
    boolean update(SysDataScopeUpdateDTO sysDataScopeUpdateDTO);

    /**
     * 批量删除数据权限管理信息
     *
     * @param sysDataScopeDeleteDTO 删除 DTO 对象
     * @return @return {@link Boolean} 结果
     * @author payne.zhuang
     * @CreateTime 2025-05-10 - 21:38:29
     */
    boolean batchDelete(SysDataScopeDeleteDTO sysDataScopeDeleteDTO);

    /**
     * 查询数据权限树形结构
     *
     * @return {@link List }<{@link SysDataScopeTreeVO }>  数据权限树形结构列表
     * @author payne.zhuang
     * @CreateTime 2025-05-23 - 17:26:34
     */
    List<SysDataScopeTreeVO> queryAllDataScopeListConvertToTree();

    /**
     * 获取所有数据权限变量及其可用操作符（k-v结构）
     *
     * @return {@link List }<{@link Map }<{@link String },{@link Object }>> 数据权限变量及其可用操作符（k-v结构）
     * @author payne.zhuang
     * @CreateTime 2025-05-30 - 22:40:01
     */
    Map<String, Object> getVariableConditions();

}