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
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.operation.MonLogsOperationAddDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.operation.MonLogsOperationDeleteDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.operation.MonLogsOperationSearchDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.operation.MonLogsOperationUpdateDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.vo.MonLogsOperationVO;
import org.dromara.dynamictp.admin.modules.monitor.facade.IMonLogsOperationFacade;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 操作日志 Controller 控制层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.monitor.MonLogsOperationController
 * @CreateTime 2024-05-07
 */

@RestController
@Tag(name = "操作日志")
@RequiredArgsConstructor
@RequestMapping("/mon_logs_operation")
public class MonLogsOperationController {

    @NonNull
    private IMonLogsOperationFacade monLogsOperationFacade;

    @GetMapping("/page")
    @SaCheckPermission("mon:logs:operation:page")
    @Operation(operationId = "1", summary = "获取操作日志列表")
    public Result<RPage<MonLogsOperationVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                                  @Parameter(description = "查询对象") MonLogsOperationSearchDTO monLogsOperationSearchDTO) {
        return Result.data(monLogsOperationFacade.listMonLogsOperationPage(pageQuery, monLogsOperationSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("mon:logs:operation:get")
    @Operation(operationId = "2", summary = "根据ID获取操作日志详细信息")
    public Result<MonLogsOperationVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(monLogsOperationFacade.get(id));
    }

    @PostMapping("/")
    @SaCheckPermission("mon:logs:operation:add")
    @Operation(operationId = "3", summary = "新增操作日志")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody MonLogsOperationAddDTO monLogsOperationAddDTO) {
        return Result.status(monLogsOperationFacade.add(monLogsOperationAddDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("mon:logs:operation:update")
    @Operation(operationId = "4", summary = "更新操作日志信息")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody MonLogsOperationUpdateDTO monLogsOperationUpdateDTO) {
        return Result.status(monLogsOperationFacade.update(monLogsOperationUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("mon:logs:operation:delete")
    @Operation(operationId = "5", summary = "批量删除操作日志信息")
    public Result<Boolean> batchDelete(@Parameter(description = "删除对象") @RequestBody MonLogsOperationDeleteDTO monLogsOperationDeleteDTO) {
        return Result.status(monLogsOperationFacade.batchDelete(monLogsOperationDeleteDTO));
    }

}
