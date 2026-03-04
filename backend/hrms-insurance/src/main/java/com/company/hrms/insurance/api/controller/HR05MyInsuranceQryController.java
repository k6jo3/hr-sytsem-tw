package com.company.hrms.insurance.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.insurance.api.response.MyInsuranceDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR05 我的保險 (ESS) Query Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/my")
@Tag(name = "HR05-MyInsurance", description = "我的保險")
public class HR05MyInsuranceQryController extends QueryBaseController {

        @GetMapping
        @Operation(summary = "查詢我的保險資訊", operationId = "getMyInsurance")
        public ResponseEntity<MyInsuranceDetailResponse> getMyInsurance(
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

                return ResponseEntity.ok(getResponse(null, currentUser));
        }
}
