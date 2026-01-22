package com.company.hrms.recruitment.api.controller;

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
import com.company.hrms.recruitment.application.dto.interview.InterviewResponse;
import com.company.hrms.recruitment.application.dto.interview.RescheduleInterviewRequest;
import com.company.hrms.recruitment.application.dto.interview.ScheduleInterviewRequest;
import com.company.hrms.recruitment.application.dto.interview.SubmitEvaluationRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * 面試指令 Controller
 */
@RestController
@RequestMapping("/api/v1/recruitment/interviews")
@Tag(name = "HR09-Interview", description = "面試管理")
public class HR09InterviewCmdController extends CommandBaseController {

        @PostMapping
        @Operation(summary = "安排面試", operationId = "scheduleInterview")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "安排成功", content = @Content(schema = @Schema(implementation = InterviewResponse.class))),
                        @ApiResponse(responseCode = "400", description = "請求參數錯誤"),
                        @ApiResponse(responseCode = "404", description = "應徵者不存在")
        })
        public ResponseEntity<InterviewResponse> scheduleInterview(
                        @Valid @RequestBody ScheduleInterviewRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(request, currentUser));
        }

        @PutMapping("/{id}/reschedule")
        @Operation(summary = "重新排程面試", operationId = "rescheduleInterview")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "重新排程成功", content = @Content(schema = @Schema(implementation = InterviewResponse.class))),
                        @ApiResponse(responseCode = "400", description = "無法重新排程"),
                        @ApiResponse(responseCode = "404", description = "面試不存在")
        })
        public ResponseEntity<InterviewResponse> rescheduleInterview(
                        @Parameter(description = "面試 ID") @PathVariable String id,
                        @Valid @RequestBody RescheduleInterviewRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(request, currentUser, id));
        }

        @PostMapping("/{id}/evaluations")
        @Operation(summary = "提交面試評估", operationId = "submitEvaluation")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "提交成功", content = @Content(schema = @Schema(implementation = InterviewResponse.class))),
                        @ApiResponse(responseCode = "400", description = "評估資料錯誤"),
                        @ApiResponse(responseCode = "404", description = "面試不存在")
        })
        public ResponseEntity<InterviewResponse> submitEvaluation(
                        @Parameter(description = "面試 ID") @PathVariable String id,
                        @Valid @RequestBody SubmitEvaluationRequest request,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(request, currentUser, id));
        }

        @PostMapping("/{id}/cancel")
        @Operation(summary = "取消面試", operationId = "cancelInterview")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "取消成功", content = @Content(schema = @Schema(implementation = InterviewResponse.class))),
                        @ApiResponse(responseCode = "400", description = "無法取消"),
                        @ApiResponse(responseCode = "404", description = "面試不存在")
        })
        public ResponseEntity<InterviewResponse> cancelInterview(
                        @Parameter(description = "面試 ID") @PathVariable String id,
                        @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {
                return ResponseEntity.ok(execCommand(null, currentUser, id));
        }
}
