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

package org.dromara.dynamictp.admin.infrastructure.convert;

import cn.idev.excel.converters.Converter;
import cn.idev.excel.enums.CellDataTypeEnum;
import cn.idev.excel.metadata.GlobalConfiguration;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.metadata.data.WriteCellData;
import cn.idev.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.dynamictp.admin.common.constants.SystemCacheConstant;
import org.dromara.dynamictp.admin.common.domain.DictItem;
import org.dromara.dynamictp.admin.common.pool.StringPools;
import org.dromara.dynamictp.admin.infrastructure.annotation.DictMapping;
import org.dromara.dynamictp.admin.infrastructure.holder.ContextHolder;
import org.dromara.dynamictp.admin.infrastructure.util.RedisUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通用数据字典转换器
 * 根据字段上的@DictField注解进行字典转换
 * 支持Excel导入导出时进行字典值与显示文本的相互转换
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.system.convert.ExcelDictConverter
 * @CreateTime 2025/4/5 - 15:05
 */
@Slf4j
public class ExcelDictConverter implements Converter<Object> {

    /**
     * 指定支持的Excel单元格数据类型
     *
     * @return 支持的Excel单元格数据类型
     */
    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    /**
     * 指定支持的Java数据类型
     *
     * @return 支持的Java数据类型
     */
    @Override
    public Class<?> supportJavaTypeKey() {
        return Object.class;
    }

    /**
     * 导出Excel时将Java对象转换为Excel单元格数据
     * 将字典值转换为对应的显示文本
     *
     * @param value               Java对象值
     * @param contentProperty     Excel内容属性
     * @param globalConfiguration 全局配置
     * @return Excel单元格数据
     * @author payne.zhuang
     * @CreateTime 2025-04-05 - 15:35:35
     */
    @Override
    public WriteCellData<?> convertToExcelData(Object value, ExcelContentProperty contentProperty,
                                               GlobalConfiguration globalConfiguration) {
        // 将null值转换为空字符串，非null值转换为字符串
        String stringValue = value == null ? StringPools.EMPTY : String.valueOf(value);
        // 调用字典转换处理方法，并指定为导出操作(true)
        String result = processDictConversion(stringValue, contentProperty, true);
        // 返回转换后的Excel单元格数据
        return new WriteCellData<>(result);
    }

    /**
     * 导入Excel时将Excel单元格数据转换为Java对象
     * 将显示文本转换为对应的字典值
     *
     * @param cellData            Excel单元格数据
     * @param contentProperty     Excel内容属性
     * @param globalConfiguration 全局配置
     * @return Java对象值
     */
    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty,
                                    GlobalConfiguration globalConfiguration) {
        // 处理空值情况
        if (cellData == null) {
            return null;
        }
        // 获取单元格字符串值
        String stringValue = cellData.getStringValue();
        // 调用字典转换处理方法，并指定为导入操作(false)
        return processDictConversion(stringValue, contentProperty, false);
    }

    /**
     * 统一处理字典转换逻辑
     * 根据操作类型进行不同方向的转换
     *
     * @param value           要转换的值
     * @param contentProperty Excel内容属性
     * @param isExport        是否为导出操作 (true=导出值转标签，false=导入标签转值)
     * @return 转换后的字符串值
     */
    private String processDictConversion(String value, ExcelContentProperty contentProperty, boolean isExport) {
        // 处理空值情况
        if (StringUtils.isBlank(value)) {
            return StringUtils.EMPTY;
        }

        // 获取字段上的DictField注解
        DictMapping annotation = contentProperty.getField().getAnnotation(DictMapping.class);
        if (null == annotation) {
            // 没有注解则直接返回原值
            return value;
        }

        // 获取注解中的字典编码
        String dictCode = annotation.value();
        if (StringUtils.isBlank(dictCode)) {
            // 字典编码为空时记录警告日志
            log.warn("字段 [{}] 的字典注解缺少有效的字典编码", contentProperty.getField().getName());
            return value;
        }

        try {
            // 从缓存中获取字典项
            String dictKey = SystemCacheConstant.dictItemKey(dictCode);
            List<DictItem> dictItems = RedisUtil.getList(dictKey, DictItem.class);

            // 检查字典项是否为空
            if (dictItems == null || dictItems.isEmpty()) {
                log.warn("字典编码 [{}] 未找到对应的字典项数据", dictCode);
                return value;
            }

            // 解析输入值为列表（支持多值，以逗号分隔）
            List<String> valueList = StringUtils.isBlank(value) ?
                    Collections.emptyList() : Arrays.asList(value.split(StringPools.COMMA));

            // 根据操作类型选择不同的转换方法
            String result = isExport
                    ? convertValuesToLabels(valueList, dictItems) // 导出：值→标签
                    : convertLabelsToValues(valueList, dictItems); // 导入：标签→值

            // 如果转换结果为空，则返回原值
            return StringUtils.isNotBlank(result) ? result : value;

        } catch (Exception e) {
            // 异常处理，记录详细信息并返回原值
            String operation = isExport ? "导出" : "导入";
            String fieldName = contentProperty.getField().getName();

            log.error("Excel{}过程中字典转换异常 - 字段: [{}], 字典编码: [{}], 原始值: [{}], 原因: {}",
                    operation, fieldName, dictCode, value, e.getMessage());

            return value;
        }
    }

    /**
     * 将字典值列表转换为对应的显示标签文本
     * 用于Excel导出时将数据库存储的值转换为用户可读的文本
     *
     * @param values    字典值列表
     * @param dictItems 字典项列表
     * @return 标签文本，多个标签用逗号分隔
     */
    private String convertValuesToLabels(List<String> values, List<DictItem> dictItems) {
        // 将每个值转换为对应的标签，找不到对应标签时保留原值
        return values.stream()
                .map(val -> dictItems.stream()
                        // 根据值匹配字典项
                        .filter(item -> item.getValue().equals(val))
                        // 根据当前语言选择返回英文或中文标签
                        .map(this::getLocalizedLabel)
                        .findFirst()
                        // 找不到对应标签时保留原值
                        .orElse(val))
                // 使用逗号连接所有标签
                .collect(Collectors.joining(StringPools.COMMA));
    }

    /**
     * 将显示标签文本转换为对应的字典值
     * 用于Excel导入时将用户输入的文本转换为数据库存储的值
     *
     * @param labels    标签列表
     * @param dictItems 字典项列表
     * @return 字典值字符串，多个值用逗号分隔
     */
    private String convertLabelsToValues(List<String> labels, List<DictItem> dictItems) {

        // 将每个标签转换为对应的值，找不到对应值时保留原始标签
        return labels.stream()
                .map(label -> dictItems.stream()
                        // 根据当前语言和标签匹配字典项
                        .filter(item -> {
                            String itemLabel = getLocalizedLabel(item);
                            // 不区分大小写匹配，并去除标签两端空格
                            return StringUtils.equalsIgnoreCase(itemLabel, label.trim());
                        })
                        // 获取匹配字典项的值
                        .map(DictItem::getValue)
                        .findFirst()
                        // 找不到对应值时保留原始标签
                        .orElse(label))
                // 使用逗号连接所有值
                .collect(Collectors.joining(StringPools.COMMA));
    }

    /**
     * 根据当前系统语言获取对应的字典项标签
     *
     * @param dictItem 字典项
     * @return 当前语言环境下的标签文本
     */
    private String getLocalizedLabel(DictItem dictItem) {
        String language = ContextHolder.language();

        // 根据语言代码返回对应的标签
        return switch (language) {
            case StringPools.EN_US -> dictItem.getEnUS();
            case StringPools.ZH_CN -> dictItem.getZhCN();
            // 可以在此扩展更多语言支持
            // ...
            // 默认返回中文，或者根据项目需求设置默认语言
            default -> dictItem.getZhCN();
        };
    }
}