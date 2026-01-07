package com.company.hrms.payroll.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.payroll.application.dto.request.CreateSalaryStructureRequest;
import com.company.hrms.payroll.application.dto.request.UpdateSalaryStructureRequest;
import com.company.hrms.payroll.application.dto.response.SalaryStructureResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

/**
 * HR04 薪資結構命令 Controller
 * 處理薪資結構的建立、更新、刪除操作
 */
@RestController
@RequestMapping("/api/v1/salary-structures")
@Slf4j
@Tag(name = "HR04-SalaryStructure", description = "薪資結構管理")
public class HR04SalaryStructureCmdController extends CommandBaseController {

    /**
     * 建立薪資結構
     * Service Bean: createSalaryStructureServiceImpl
     */
    @PostMapping
    @Operation(summary = "建立薪資結構", operationId = "createSalaryStructure")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "建立成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "409", description = "員工已有有效薪資結構")
    })
    public ResponseEntity<SalaryStructureResponse> createSalaryStructure(
            @Valid @RequestBody CreateSalaryStructureRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    /**
     * 更新薪資結構
     * Service Bean: updateSalaryStructureServiceImpl
     */
    @PutMapping("/{structureId}")
    @Operation(summary = "更新薪資結構", operationId = "updateSalaryStructure")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "薪資結構不存在")
    })
    public ResponseEntity<SalaryStructureResponse> updateSalaryStructure(
            @PathVariable String structureId,
            @Valid @RequestBody UpdateSalaryStructureRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, structureId));
    }

    /**
     * 刪除薪資結構
     * Service Bean: deleteSalaryStructureServiceImpl
     */
    @DeleteMapping("/{structureId}")
    @Operation(summary = "刪除薪資結構", operationId = "deleteSalaryStructure")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "刪除成功"),
            @ApiResponse(responseCode = "401", description = "未授權"),
            @ApiResponse(responseCode = "404", description = "薪資結構不存在"),
            @ApiResponse(responseCode = "409", description = "薪資結構已生效或使用中，無法刪除")
    })
    public ResponseEntity<SalaryStructureResponse> deleteSalaryStructure(
            @PathVariable String structureId,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(null, currentUser, structureId));
    }
}
