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
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.exception.MonLogsErrorAddDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.exception.MonLogsErrorDeleteDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.exception.MonLogsErrorSearchDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.dto.logs.exception.MonLogsErrorUpdateDTO;
import org.dromara.dynamictp.admin.modules.monitor.domain.vo.MonLogsErrorVO;
import org.dromara.dynamictp.admin.modules.monitor.facade.IMonLogsErrorFacade;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 错误异常日志 Controller 控制层
 *
 * @Author eachann <eachannchan@qq.com>
 * @ProjectName dynamic-tp-admin
 * @ClassName org.dromara.dynamictp.admin.admin.controller.monitor.MonLogsErrorController
 * @CreateTime 2024-05-07
 */

@RestController
@Tag(name = "错误异常日志")
@RequiredArgsConstructor
@RequestMapping("/mon_logs_error")
public class MonLogsErrorController {

    @NonNull
    private IMonLogsErrorFacade monLogsErrorFacade;

    @GetMapping("/page")
    @SaCheckPermission("mon:logs:error:page")
    @Operation(operationId = "1", summary = "获取错误异常日志列表")
    public Result<RPage<MonLogsErrorVO>> page(@Parameter(description = "分页对象", required = true) @Valid PageQuery pageQuery,
                                              @Parameter(description = "查询对象") MonLogsErrorSearchDTO monLogsErrorSearchDTO) {
        return Result.data(monLogsErrorFacade.listMonLogsErrorPage(pageQuery, monLogsErrorSearchDTO));
    }

    @GetMapping("/{id}")
    @SaCheckPermission("mon:logs:error:get")
    @Operation(operationId = "2", summary = "根据ID获取错误异常日志详细信息")
    public Result<MonLogsErrorVO> get(@Parameter(description = "ID") @PathVariable("id") Long id) {
        return Result.data(monLogsErrorFacade.get(id));
    }

    @PostMapping("/")
    @SaCheckPermission("mon:logs:error:add")
    @Operation(operationId = "3", summary = "新增错误异常日志")
    public Result<Boolean> add(@Parameter(description = "新增对象") @RequestBody MonLogsErrorAddDTO monLogsErrorAddDTO) {
        return Result.status(monLogsErrorFacade.add(monLogsErrorAddDTO));
    }

    @PutMapping("/")
    @SaCheckPermission("mon:logs:error:update")
    @Operation(operationId = "4", summary = "更新错误异常日志信息")
    public Result<Boolean> update(@Parameter(description = "更新对象") @RequestBody MonLogsErrorUpdateDTO monLogsErrorUpdateDTO) {
        return Result.status(monLogsErrorFacade.update(monLogsErrorUpdateDTO));
    }

    @DeleteMapping("/")
    @SaCheckPermission("mon:logs:error:delete")
    @Operation(operationId = "5", summary = "批量删除错误异常日志信息")
    public Result<Boolean> batchDelete(@Parameter(description = "删除对象") @RequestBody MonLogsErrorDeleteDTO monLogsErrorDeleteDTO) {
        return Result.status(monLogsErrorFacade.batchDelete(monLogsErrorDeleteDTO));
    }

}
