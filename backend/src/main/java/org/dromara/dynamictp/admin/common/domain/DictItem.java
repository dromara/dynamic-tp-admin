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

package org.dromara.dynamictp.admin.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 数据字典项
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.common.domain.DictItem
 * @CreateTime 2025/4/5 - 21:34
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "数据字典项")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 5287225202439514602L;

    @Schema(description = "父字典ID")
    private Long dictId;

    @Schema(description = "父字典编码")
    private String dictCode;

    @Schema(description = "数据值")
    private String value;

    @Schema(description = "中文名称")
    private String zhCN;

    @Schema(description = "英文名称")
    private String enUS;

    @Schema(description = "类型(前端渲染类型)")
    private String type;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "字典描述")
    private String description;

    @Schema(description = "是否启用(0:禁用,1:启用)")
    private String status;
}
