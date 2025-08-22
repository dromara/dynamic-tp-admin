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
import org.dromara.dynamictp.admin.common.domain.Options;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.system.domain.bo.SysDictBO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.dict.SysDictAddDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.dict.SysDictDeleteDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.dict.SysDictSearchDTO;
import org.dromara.dynamictp.admin.modules.system.domain.dto.dict.SysDictUpdateDTO;
import org.dromara.dynamictp.admin.modules.system.domain.entity.SysDict;
import org.dromara.dynamictp.admin.modules.system.domain.vo.SysDictVO;
import org.dromara.dynamictp.admin.modules.system.facade.ISysDictFacade;
import org.dromara.dynamictp.admin.modules.system.service.ISysDictItemService;
import org.dromara.dynamictp.admin.modules.system.service.ISysDictService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 数据字典管理 门面接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.facade.impl.SysDictFacadeImpl
 * @CreateTime 2024-06-27 - 22:03:29
 */

@Service
@RequiredArgsConstructor
public class SysDictFacadeImpl implements ISysDictFacade {

    @NonNull
    private ISysDictService sysDictService;

    @NonNull
    private ISysDictItemService sysDictItemService;

    @Override
    public RPage<SysDictVO> listSysDictPage(PageQuery pageQuery, SysDictSearchDTO sysDictSearchDTO) {
        SysDictBO sysDictBO = CglibUtil.convertObj(sysDictSearchDTO, SysDictBO::new);
        IPage<SysDict> sysDictIPage = sysDictService.listSysDictPage(pageQuery, sysDictBO);
        return RPage.build(sysDictIPage, SysDictVO::new);
    }

    @Override
    public List<SysDictVO> listSysDict(SysDictSearchDTO sysDictSearchDTO) {
        SysDictBO sysDictBO = CglibUtil.convertObj(sysDictSearchDTO, SysDictBO::new);
        List<SysDict> allSysDict = sysDictService.getAllSysDict(sysDictBO);
        return CglibUtil.convertList(allSysDict, SysDictVO::new);
    }

    @Override
    public SysDictVO get(Long id) {
        SysDict byId = sysDictService.getById(id);
        return CglibUtil.convertObj(byId, SysDictVO::new);
    }

    @Override
    @Transactional
    public boolean add(SysDictAddDTO sysDictAddDTO) {
        SysDictBO sysDictBO = CglibUtil.convertObj(sysDictAddDTO, SysDictBO::new);
        return sysDictService.save(sysDictBO);
    }

    @Override
    @Transactional
    public boolean update(SysDictUpdateDTO sysDictUpdateDTO) {
        SysDictBO sysDictBO = CglibUtil.convertObj(sysDictUpdateDTO, SysDictBO::new);
        return sysDictService.updateById(sysDictBO);
    }

    @Override
    @Transactional
    public boolean batchDelete(SysDictDeleteDTO sysDictDeleteDTO) {
        return sysDictService.removeBatchByIds(sysDictDeleteDTO.getIds(), true);
    }

    @Override
    public List<Options<String>> getAllDictOptions() {
        List<SysDict> allSysDict = sysDictService.getAllSysDict();
        return allSysDict.stream().map(item -> Options.<String>builder()
                .label(item.getName())
                .value(item.getCode())
                .build()).toList();
    }
}