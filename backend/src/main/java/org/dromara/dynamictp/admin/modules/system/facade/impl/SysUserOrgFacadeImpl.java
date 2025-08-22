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

package org.dromara.dynamictp.admin.modules.system.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysUserOrgBO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.org.SysUserOrgAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.org.SysUserOrgDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.org.SysUserOrgSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.user.org.SysUserOrgUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysUserOrg;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysUserOrgVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysUserOrgFacade;
import org.dromara.dynamictp.admin.modules.system.service.ISysUserOrgService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户组织/部门/子部门管理 门面接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.facade.impl.SysUserOrgFacadeImpl
 * @CreateTime 2024-07-16 - 16:35:30
 */

@Service
@RequiredArgsConstructor
public class SysUserOrgFacadeImpl implements ISysUserOrgFacade {

    @NonNull
    private ISysUserOrgService sysUserOrgService;

    @Override
    public RPage<SysUserOrgVO> listSysUserOrgPage(PageQuery pageQuery, SysUserOrgSearchDTO sysUserOrgSearchDTO) {
        SysUserOrgBO sysUserOrgBO = CglibUtil.convertObj(sysUserOrgSearchDTO, SysUserOrgBO::new);
        IPage<SysUserOrg> sysUserOrgIPage = sysUserOrgService.listSysUserOrgPage(pageQuery, sysUserOrgBO);
        return RPage.build(sysUserOrgIPage, SysUserOrgVO::new);
    }

    @Override
    public SysUserOrgVO get(Long id) {
        SysUserOrg byId = sysUserOrgService.getById(id);
        return CglibUtil.convertObj(byId, SysUserOrgVO::new);
    }

    @Override
    @Transactional
    public boolean add(SysUserOrgAddDTO sysUserOrgAddDTO) {
        SysUserOrgBO sysUserOrgBO = CglibUtil.convertObj(sysUserOrgAddDTO, SysUserOrgBO::new);
        return sysUserOrgService.save(sysUserOrgBO);
    }

    @Override
    @Transactional
    public boolean update(SysUserOrgUpdateDTO sysUserOrgUpdateDTO) {
        SysUserOrgBO sysUserOrgBO = CglibUtil.convertObj(sysUserOrgUpdateDTO, SysUserOrgBO::new);
        return sysUserOrgService.updateById(sysUserOrgBO);
    }

    @Override
    @Transactional
    public boolean batchDelete(SysUserOrgDeleteDTO sysUserOrgDeleteDTO) {
        SysUserOrgBO sysUserOrgBO = CglibUtil.convertObj(sysUserOrgDeleteDTO, SysUserOrgBO::new);
        return sysUserOrgService.removeBatchByIds(sysUserOrgBO.getIds(), true);
    }

}