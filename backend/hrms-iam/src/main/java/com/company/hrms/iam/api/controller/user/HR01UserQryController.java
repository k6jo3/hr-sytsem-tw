package com.company.hrms.iam.api.controller.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.model.PageResponse;
import com.company.hrms.iam.api.request.user.GetUserListRequest;
import com.company.hrms.iam.api.response.user.UserDetailResponse;
import com.company.hrms.iam.api.response.user.UserListResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * IAM - 使用者管理 Query Controller
 * 負責使用者的查詢操作
 * 
 * <p>
 * 命名規範：HR{DD}{Screen}QryController
 * </p>
 * <p>
 * DD = 01 (IAM Domain)
 * </p>
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "HR01-User-Query", description = "使用者管理查詢操作")
public class HR01UserQryController extends QueryBaseController {

        /**
         * 查詢使用者列表
         */
        @Operation(summary = "查詢使用者列表", operationId = "getUserList")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權")
        })
        @GetMapping
        public ResponseEntity<PageResponse<UserListResponse>> getUserList(
                        @org.springdoc.core.annotations.ParameterObject GetUserListRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                // 直接使用 request 物件，它會由 Spring 自動綁定 URL 參數
                PageResponse<UserListResponse> response = getResponse(request, currentUser);
                return ResponseEntity.ok(response);
        }

        /**
         * 查詢單一使用者
         */
        @Operation(summary = "查詢單一使用者", operationId = "getUser")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "成功"),
                        @ApiResponse(responseCode = "401", description = "未授權"),
                        @ApiResponse(responseCode = "404", description = "使用者不存在")
        })
        @GetMapping("/{userId}")
        public ResponseEntity<UserDetailResponse> getUser(
                        @PathVariable String userId,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(getResponse(new GetUserRequest(), currentUser, userId));
        }

        /**
         * 使用者查詢請求 (內部類別)
         */
        public record UserQueryRequest(String status, String keyword) {
        }

        /**
         * 取得使用者請求 (內部類別)
         */
        public record GetUserRequest() {
        }
}
