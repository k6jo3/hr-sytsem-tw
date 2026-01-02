package com.company.hrms.project.ui.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetCustomerListRequest;
import com.company.hrms.project.api.response.GetCustomerListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "專案管理-客戶查詢")
public class HR06CustomerQryController extends QueryBaseController {

    @Operation(summary = "查詢客戶列表", operationId = "getCustomerList")
    @GetMapping
    public ResponseEntity<GetCustomerListResponse> getCustomerList(@ModelAttribute GetCustomerListRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
