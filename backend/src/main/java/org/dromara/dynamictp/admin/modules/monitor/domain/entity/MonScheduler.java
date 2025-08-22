package org.dromara.dynamictp.admin.modules.monitor.domain.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.dromara.dynamictp.admin.common.domain.KVPairs;
import org.dromara.dynamictp.admin.infrastructure.domain.BaseEntity;
import org.dromara.dynamictp.admin.infrastructure.typehandler.JobDataTypeHandler;

import java.io.Serial;
import java.util.List;

/**
 * 调度任务 Entity 实体类
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.modules.monitor.domain.entity.MonScheduler
 * @CreateTime 2024/5/21 - 11:36
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@TableName("mon_scheduler")
public class MonScheduler extends BaseEntity {

    @Serial
    private static final long serialVersionUID = -4732204370005416544L;

    /**
     * 任务名称(唯一)
     */
    private String jobName;

    /**
     * 任务组别
     */
    private String jobGroup;

    /**
     * 任务参数
     */
    @TableField(typeHandler = JobDataTypeHandler.class)
    private List<KVPairs> jobData;

    /**
     * 触发器名称
     */
    private String triggerName;

    /**
     * 触发器组
     */
    private String triggerGroup;

    /**
     * 触发器参数
     */
    @TableField(typeHandler = JobDataTypeHandler.class)
    private List<KVPairs> triggerData;

}
