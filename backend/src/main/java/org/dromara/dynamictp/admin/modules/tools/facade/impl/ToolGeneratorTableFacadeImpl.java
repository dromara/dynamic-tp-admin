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

package org.dromara.dynamictp.admin.modules.tools.facade.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.util.CglibUtil;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.tools.domain.bo.ToolGeneratorTableBO;
import org.dromara.dynamictp.admin.modules.tools.domain.dto.generator.table.ToolGeneratorTableAddDTO;
import org.dromara.dynamictp.admin.modules.tools.domain.dto.generator.table.ToolGeneratorTableDeleteDTO;
import org.dromara.dynamictp.admin.modules.tools.domain.dto.generator.table.ToolGeneratorTableSearchDTO;
import org.dromara.dynamictp.admin.modules.tools.domain.dto.generator.table.ToolGeneratorTableUpdateDTO;
import org.dromara.dynamictp.admin.modules.tools.domain.entity.ToolGeneratorTable;
import org.dromara.dynamictp.admin.modules.tools.domain.vo.ToolGeneratorTableVO;
import org.dromara.dynamictp.admin.modules.tools.facade.IToolGeneratorTableFacade;
import org.dromara.dynamictp.admin.modules.tools.service.IToolGeneratorTableColumnService;
import org.dromara.dynamictp.admin.modules.tools.service.IToolGeneratorTableService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;

/**
 * 代码生成表管理 门面接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.tools.facade.impl.ToolGeneratorTableFacadeImpl
 * @CreateTime 2024-08-26 - 16:14:53
 */

@Service
@RequiredArgsConstructor
public class ToolGeneratorTableFacadeImpl implements IToolGeneratorTableFacade {

    @NonNull
    private IToolGeneratorTableService toolGeneratorTableService;

    @NonNull
    private IToolGeneratorTableColumnService toolGeneratorTableColumnService;


    @Override
    public RPage<ToolGeneratorTableVO> listToolGeneratorTablePage(PageQuery pageQuery, ToolGeneratorTableSearchDTO toolGeneratorTableSearchDTO) {
        ToolGeneratorTableBO toolGeneratorTableBO = CglibUtil.convertObj(toolGeneratorTableSearchDTO, ToolGeneratorTableBO::new);
        IPage<ToolGeneratorTable> toolGeneratorTableIPage = toolGeneratorTableService.listToolGeneratorTablePage(pageQuery, toolGeneratorTableBO);
        return RPage.build(toolGeneratorTableIPage, ToolGeneratorTableVO::new);
    }

    @Override
    public ToolGeneratorTableVO get(Long id) {
        ToolGeneratorTable toolGeneratorTable = toolGeneratorTableService.getById(id);
        return CglibUtil.convertObj(toolGeneratorTable, ToolGeneratorTableVO::new);
    }

    @Override
    @Transactional
    public ToolGeneratorTableVO add(ToolGeneratorTableAddDTO toolGeneratorTableAddDTO) {
        ToolGeneratorTableBO toolGeneratorTableBO = CglibUtil.convertObj(toolGeneratorTableAddDTO, ToolGeneratorTableBO::new);
        toolGeneratorTableService.save(toolGeneratorTableBO);
        return CglibUtil.convertObj(toolGeneratorTableBO, ToolGeneratorTableVO::new);
    }

    @Override
    @Transactional
    public boolean update(ToolGeneratorTableUpdateDTO toolGeneratorTableUpdateDTO) {
        ToolGeneratorTableBO toolGeneratorTableBO = CglibUtil.convertObj(toolGeneratorTableUpdateDTO, ToolGeneratorTableBO::new);
        return toolGeneratorTableService.updateById(toolGeneratorTableBO);
    }

    @Override
    @Transactional
    public boolean batchDelete(ToolGeneratorTableDeleteDTO toolGeneratorTableDeleteDTO) {
        ToolGeneratorTableBO toolGeneratorTableBO = CglibUtil.convertObj(toolGeneratorTableDeleteDTO, ToolGeneratorTableBO::new);
        boolean removed = toolGeneratorTableService.removeBatchByIds(toolGeneratorTableBO.getIds(), true);
        toolGeneratorTableColumnService.removeByTableIds(toolGeneratorTableBO.getIds());
        return removed;
    }

    @Override
    public byte[] zipCodeGenerate(Long tableId) {
        ByteArrayOutputStream byteArrayOutputStream = toolGeneratorTableService.zipCodeGenerator(tableId);
        return byteArrayOutputStream.toByteArray();
    }

}