package com.company.hrms.iam.api.controller.profile;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.iam.api.response.profile.ProfileResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * IAM - 個人資料 Query Controller
 * 負責個人資料的查詢操作
 * 
 * <p>
 * 命名規範：HR{DD}{Screen}QryController
 * </p>
 * <p>
 * DD = 01 (IAM Domain)
 * </p>
 */
@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "HR01-Profile-Query", description = "個人資料查詢操作")
public class HR01ProfileQryController extends QueryBaseController {

    /**
     * 查詢個人資料
     */
    @Operation(summary = "查詢個人資料", operationId = "getProfile")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "成功"),
            @ApiResponse(responseCode = "401", description = "未授權")
    })
    @GetMapping
    public ResponseEntity<com.company.hrms.common.api.response.ApiResponse<ProfileResponse>> getProfile(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
        ProfileResponse profile = getResponse(null, currentUser);
        return ResponseEntity.ok(com.company.hrms.common.api.response.ApiResponse.success(profile));
    }
}
