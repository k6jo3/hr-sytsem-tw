package com.company.hrms.insurance.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.insurance.api.request.CalculateFeeRequest;
import com.company.hrms.insurance.api.request.CalculateSupplementaryPremiumRequest;
import com.company.hrms.insurance.api.response.FeeCalculationResponse;
import com.company.hrms.insurance.api.response.SupplementaryPremiumResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR05 費用計算 Command Controller
 */
@RestController
@RequestMapping("/api/v1/insurance")
@Tag(name = "HR05-Fee", description = "費用計算")
public class HR05FeeCmdController extends CommandBaseController {

        @PostMapping("/fees/calculate")
        @Operation(summary = "計算保費", operationId = "calculateFee")
        public ResponseEntity<FeeCalculationResponse> calculateFee(
                        @RequestBody CalculateFeeRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

                return ResponseEntity.ok(execCommand(request, currentUser));
        }

        @PostMapping("/supplementary-premium/calculate")
        @Operation(summary = "計算補充保費", operationId = "calculateSupplementaryPremium")
        public ResponseEntity<SupplementaryPremiumResponse> calculateSupplementaryPremium(
                        @RequestBody CalculateSupplementaryPremiumRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

                return ResponseEntity.ok(execCommand(request, currentUser));
        }
}
