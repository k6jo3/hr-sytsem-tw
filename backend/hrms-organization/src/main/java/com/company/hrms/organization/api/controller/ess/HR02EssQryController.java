package com.company.hrms.organization.api.controller.ess;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.security.CurrentUser;
import com.company.hrms.organization.api.response.ess.CertificateRequestResponse;
import com.company.hrms.organization.api.response.ess.MyProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 員工自助服務查詢控制器
 */
@RestController
@RequestMapping("/api/v1/employees/me")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR02-ESS-Query", description = "員工自助服務查詢操作")
public class HR02EssQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢個人資料", operationId = "getMyProfile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功")
    })
    public ResponseEntity<MyProfileResponse> getMyProfile(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Getting my profile for user: {}", currentUser.getUserId());
        return ResponseEntity.ok(getResponse(null, currentUser));
    }

    @GetMapping("/certificate-requests")
    @Operation(summary = "查詢證明文件申請記錄", operationId = "getMyCertificateRequests")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功")
    })
    public ResponseEntity<CertificateRequestResponse.ListResponse> getMyCertificateRequests(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Getting my certificate requests for user: {}", currentUser.getUserId());
        return ResponseEntity.ok(getResponse(null, currentUser));
    }
}
