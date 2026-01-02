package com.company.hrms.project.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.project.api.request.GetCustomerDetailRequest;
import com.company.hrms.project.api.request.GetCustomerListRequest;
import com.company.hrms.project.api.response.GetCustomerDetailResponse;
import com.company.hrms.project.api.response.GetCustomerListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/customers")
@Tag(name = "HR06-Customer-Query")
public class HR06CustomerQryController extends QueryBaseController {

    @Operation(summary = "Query customer list", operationId = "getCustomerList")
    @GetMapping
    public ResponseEntity<GetCustomerListResponse> getCustomerList(@ModelAttribute GetCustomerListRequest request,
            @CurrentUser JWTModel currentUser) throws Exception {
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @Operation(summary = "Query customer detail", operationId = "getCustomerDetail")
    @GetMapping("/{id}")
    public ResponseEntity<GetCustomerDetailResponse> getCustomerDetail(
            @PathVariable String id,
            @CurrentUser JWTModel currentUser) throws Exception {
        GetCustomerDetailRequest request = new GetCustomerDetailRequest();
        request.setCustomerId(id);
        return ResponseEntity.ok(getResponse(request, currentUser));
    }
}
