package org.dromara.dynamictp.admin.controller.monitor;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dromara.dynamictp.admin.common.api.Result;
import org.dromara.dynamictp.admin.infrastructure.page.PageQuery;
import org.dromara.dynamictp.admin.infrastructure.page.RPage;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.scheduler.MonLogsSchedulerAddDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.scheduler.MonLogsSchedulerDeleteDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.scheduler.MonLogsSchedulerSearchDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.scheduler.MonLogsSchedulerUpdateDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.vo.MonLogsSchedulerVO;
import org.dromara.dynamictp.admin.modules.monitor.facade.IMonLogsSchedulerFacade;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 调度日志 Controller 控制层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.monitor.MonLogsSchedulerController
 * @CreateTime 2024-05-30
 */

@RestController
@Tag(name = "调度日志")
@RequiredArgsConstructor
@RequestMapping("/mon_logs_scheduler")
public class MonLogsSchedulerController {

    @NonNull
    private IMonLogsSchedulerFacade monLogsSchedulerFacade;

    @GetMapping("/page")
    @SaCheckPermission("mon:logs:scheduler:page")
    @Operation(operationId = "1", summary = "获取调度日志列表")
    public Result<RPage<MonLogsSchedulerVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                                  @Parameter(description = "查询对象") MonLogsSchedulerSearchDTO monLogsSchedulerSearchDTO) {
        return Result.data(monLogsSchedulerFacade.listMonLogsSchedulerPage(pageQuery, monLogsSchedulerSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("mon:logs:scheduler:get")
    @Operation(operationId = "2", summary = "根据ID获取调度日志详细信息")
    public Result<MonLogsSchedulerVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(monLogsSchedulerFacade.get(id));
    }

    @PostMapping("/")
    @SaCheckPermission("mon:logs:scheduler:add")
    @Operation(operationId = "3", summary = "新增调度日志")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody MonLogsSchedulerAddDTO monLogsSchedulerAddDTO) {
        return Result.status(monLogsSchedulerFacade.add(monLogsSchedulerAddDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("mon:logs:scheduler:update")
    @Operation(operationId = "4", summary = "更新调度日志信息")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody MonLogsSchedulerUpdateDTO monLogsSchedulerUpdateDTO) {
        return Result.status(monLogsSchedulerFacade.update(monLogsSchedulerUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("mon:logs:scheduler:delete")
    @Operation(operationId = "5", summary = "批量删除调度日志信息")
    public Result<Boolean> batchDelete(@Parameter(description = "删除对象") @RequestBody MonLogsSchedulerDeleteDTO monLogsSchedulerDeleteDTO) {
        return Result.status(monLogsSchedulerFacade.batchDelete(monLogsSchedulerDeleteDTO));
    }

}
