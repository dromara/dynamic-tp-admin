/*
 * All Rights Reserved: Copyright [2025] [Zhuang Pan (paynezhuang@gmail.com)]
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

package org.dromara.dynamictp.admin.modules.system.domain.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.ContentRowHeight;
import cn.idev.excel.annotation.write.style.HeadRowHeight;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.dromara.dynamictp.admin.infrastructure.annotation.DictMapping;
import org.dromara.dynamictp.admin.infrastructure.convert.ExcelDictConverter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色导出 VO 展示类
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.domain.vo.SysRoleExportVO
 * @CreateTime 2025/4/5 - 14:34
 */

@Data
@ColumnWidth(20)  // 全局列宽
@HeadRowHeight(value = 20) // 头部行高
@ContentRowHeight(value = 18) // 内容行高
@ExcelIgnoreUnannotated // 忽略未注解的类
@Schema(description = "角色导出 VO 展示类")
public class SysRoleExportVO implements Serializable {

    @Serial
    private static final long serialVersionUID = -8944710832771980786L;

    @ExcelProperty(value = "角色名称", index = 0)
    @Schema(description = "角色名称")
    private String roleName;

    @ExcelProperty(value = "角色编码", index = 1)
    @Schema(description = "角色编码")
    private String roleCode;

    @ExcelProperty(value = "描述", index = 2)
    @Schema(description = "描述")
    private String description;

    @ExcelProperty(value = "排序", index = 4)
    @Schema(description = "排序")
    private Integer sort;

    @DictMapping("status")
    @ExcelProperty(value = "启用状态", index = 3, converter = ExcelDictConverter.class)
    @Schema(description = "是否启用(0:禁用,1:启用)")
    private String status;

    @ExcelProperty(value = "创建用户", index = 5)
    @Schema(description = "创建用户名称")
    private String createUser;

    @ColumnWidth(25)
    @ExcelProperty(value = "创建时间", index = 6)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}
