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
import org.dromara.dynamictp.admin.modules.tools.domain.bo.ToolGeneratorTableColumnBO;
import org.dromara.dynamictp.admin.modules.tools.domain.dto.generator.table.column.ToolGeneratorTableColumnAddDTO;
import org.dromara.dynamictp.admin.modules.tools.domain.dto.generator.table.column.ToolGeneratorTableColumnDeleteDTO;
import org.dromara.dynamictp.admin.modules.tools.domain.dto.generator.table.column.ToolGeneratorTableColumnSearchDTO;
import org.dromara.dynamictp.admin.modules.tools.domain.dto.generator.table.column.ToolGeneratorTableColumnUpdateDTO;
import org.dromara.dynamictp.admin.modules.tools.domain.entity.ToolGeneratorTableColumn;
import org.dromara.dynamictp.admin.modules.tools.domain.vo.ToolGeneratorTableColumnVO;
import org.dromara.dynamictp.admin.modules.tools.facade.IToolGeneratorTableColumnFacade;
import org.dromara.dynamictp.admin.modules.tools.service.IToolGeneratorTableColumnService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 代码生成表字段列管理 门面接口实现层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.tools.facade.impl.ToolGeneratorTableColumnFacadeImpl
 * @CreateTime 2024-08-26 - 22:13:50
 */

@Service
@RequiredArgsConstructor
public class ToolGeneratorTableColumnFacadeImpl implements IToolGeneratorTableColumnFacade {

    @NonNull
    private IToolGeneratorTableColumnService toolGeneratorTableColumnService;

    @Override
    public RPage<ToolGeneratorTableColumnVO> listToolGeneratorTableColumnPage(PageQuery pageQuery, ToolGeneratorTableColumnSearchDTO toolGeneratorTableColumnSearchDTO) {
        ToolGeneratorTableColumnBO toolGeneratorTableColumnBO = CglibUtil.convertObj(toolGeneratorTableColumnSearchDTO, ToolGeneratorTableColumnBO::new);
        IPage<ToolGeneratorTableColumn> toolGeneratorTableColumnIPage = toolGeneratorTableColumnService.listToolGeneratorTableColumnPage(pageQuery, toolGeneratorTableColumnBO);
        return RPage.build(toolGeneratorTableColumnIPage, ToolGeneratorTableColumnVO::new);
    }

    @Override
    public ToolGeneratorTableColumnVO get(Long id) {
        ToolGeneratorTableColumn byId = toolGeneratorTableColumnService.getById(id);
        return CglibUtil.convertObj(byId, ToolGeneratorTableColumnVO::new);
    }

    @Override
    @Transactional
    public boolean add(ToolGeneratorTableColumnAddDTO toolGeneratorTableColumnAddDTO) {
        ToolGeneratorTableColumnBO toolGeneratorTableColumnBO = CglibUtil.convertObj(toolGeneratorTableColumnAddDTO, ToolGeneratorTableColumnBO::new);
        return toolGeneratorTableColumnService.save(toolGeneratorTableColumnBO);
    }

    @Override
    @Transactional
    public boolean update(ToolGeneratorTableColumnUpdateDTO toolGeneratorTableColumnUpdateDTO) {
        ToolGeneratorTableColumnBO toolGeneratorTableColumnBO = CglibUtil.convertObj(toolGeneratorTableColumnUpdateDTO, ToolGeneratorTableColumnBO::new);
        return toolGeneratorTableColumnService.updateById(toolGeneratorTableColumnBO);
    }

    @Override
    @Transactional
    public boolean batchDelete(ToolGeneratorTableColumnDeleteDTO toolGeneratorTableColumnDeleteDTO) {
        ToolGeneratorTableColumnBO toolGeneratorTableColumnBO = CglibUtil.convertObj(toolGeneratorTableColumnDeleteDTO, ToolGeneratorTableColumnBO::new);
        return toolGeneratorTableColumnService.removeBatchByIds(toolGeneratorTableColumnBO.getIds(), true);
    }

    @Override
    public List<ToolGeneratorTableColumnVO> queryListWithTableId(Long tableId) {
        List<ToolGeneratorTableColumn> toolGeneratorTableColumns = toolGeneratorTableColumnService.queryListByTableId(tableId);
        return CglibUtil.convertList(toolGeneratorTableColumns, ToolGeneratorTableColumnVO::new);
    }

    @Override
    public boolean batchUpdateTableColumnList(List<ToolGeneratorTableColumnUpdateDTO> generatorTableColumnUpdateDTOList) {
        List<ToolGeneratorTableColumn> generatorTableColumnList = CglibUtil.convertList(generatorTableColumnUpdateDTOList, ToolGeneratorTableColumn::new);
        return toolGeneratorTableColumnService.batchUpdateTableColumnList(generatorTableColumnList);
    }

    @Override
    public boolean cleanTableColumnList(Long tableId) {
        return toolGeneratorTableColumnService.cleanTableColumnList(tableId);
    }

    @Override
    public List<ToolGeneratorTableColumnVO> syncDataTableColumns(Long tableId) {
        List<ToolGeneratorTableColumn> toolGeneratorTableColumns = toolGeneratorTableColumnService.syncDataTableColumns(tableId);
        return CglibUtil.convertList(toolGeneratorTableColumns, ToolGeneratorTableColumnVO::new);
    }
}