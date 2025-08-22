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

package org.dromara.dynamictp.admin.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysOrgUnitsBO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysOrgUnits;
import org.dromara.dynamictp.admin.modules.system.repository.mapper.SysOrgUnitsMapper;
import org.dromara.dynamictp.admin.modules.system.service.ISysOrgUnitsService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 组织/部门/子部门管理 Service 服务接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.service.impl.SysOrgUnitsServiceImpl
 * @CreateTime 2024-07-16 - 16:35:30
 */

@Slf4j
@Service
public class SysOrgUnitsServiceImpl extends ServiceImpl<SysOrgUnitsMapper, SysOrgUnits> implements ISysOrgUnitsService {

    @Override
    public IPage<SysOrgUnits> listSysOrgUnitsPage(PageQuery pageQuery, SysOrgUnitsBO sysOrgUnitsBO) {
        return baseMapper.listSysOrgUnitsPage(pageQuery.buildPage(), sysOrgUnitsBO);
    }

    @Override
    public List<SysOrgUnits> listAllDescendants(List<Long> parentIds) {
        return baseMapper.listAllDescendants(parentIds);
    }

    @Override
    public List<SysOrgUnits> querySysOrgUnitsListWithStatus(String status) {
        LambdaQueryWrapper<SysOrgUnits> queryWrapper = new LambdaQueryWrapper<SysOrgUnits>()
                .eq(SysOrgUnits::getStatus, status);
        return baseMapper.selectList(queryWrapper);
    }

}

