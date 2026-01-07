package com.company.hrms.project.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.CreateCustomerRequest;
import com.company.hrms.project.api.request.UpdateCustomerRequest;
import com.company.hrms.project.api.response.CreateCustomerResponse;
import com.company.hrms.project.api.response.UpdateCustomerResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR06 專案管理 - 客戶維護 Controller
 * 
 * 負責客戶的新增、修改等寫入操作
 */
@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "HR06-客戶維護", description = "專案管理 - 客戶維護 API")
public class HR06CustomerCmdController extends CommandBaseController {

    @Operation(summary = "建立客戶", operationId = "createCustomer", description = "建立新客戶資料")
    @PostMapping
    public ResponseEntity<CreateCustomerResponse> createCustomer(@RequestBody CreateCustomerRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @Operation(summary = "更新客戶", operationId = "updateCustomer", description = "更新客戶資訊")
    @PutMapping("/{id}")
    public ResponseEntity<UpdateCustomerResponse> updateCustomer(@PathVariable String id,
            @RequestBody UpdateCustomerRequest request, @CurrentUser JWTModel currentUser) throws Exception {
        request.setCustomerId(id);
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
