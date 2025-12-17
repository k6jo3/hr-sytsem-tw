package com.company.hrms.organization.api.controller.ess;

import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.security.CurrentUser;
import com.company.hrms.organization.api.request.ess.RequestCertificateRequest;
import com.company.hrms.organization.api.request.ess.UpdateMyProfileRequest;
import com.company.hrms.organization.api.response.ess.CertificateRequestResponse;
import com.company.hrms.organization.api.response.ess.MyProfileResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 員工自助服務寫入控制器
 */
@RestController
@RequestMapping("/api/v1/employees/me")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "HR02-ESS-Command", description = "員工自助服務寫入操作")
public class HR02EssCmdController extends CommandBaseController {

    @PutMapping
    @Operation(summary = "更新個人資料", operationId = "updateMyProfile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤")
    })
    public ResponseEntity<MyProfileResponse> updateMyProfile(
            @Valid @RequestBody UpdateMyProfileRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Updating my profile for user: {}", currentUser.getUserId());
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @PostMapping("/certificate-requests")
    @Operation(summary = "申請證明文件", operationId = "requestCertificate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "400", description = "請求格式錯誤")
    })
    public ResponseEntity<CertificateRequestResponse> requestCertificate(
            @Valid @RequestBody RequestCertificateRequest request,
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        log.info("Requesting certificate for user: {}", currentUser.getUserId());
        return ResponseEntity.ok(execCommand(request, currentUser));
    }
}
