package com.company.hrms.training.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.training.api.request.ApproveEnrollmentRequest;
import com.company.hrms.training.api.request.CancelEnrollmentRequest;
import com.company.hrms.training.api.request.CompleteTrainingRequest;
import com.company.hrms.training.api.request.ConfirmAttendanceRequest;
import com.company.hrms.training.api.request.EnrollCourseRequest;
import com.company.hrms.training.api.request.RejectEnrollmentRequest;
import com.company.hrms.training.api.response.TrainingEnrollmentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/training/enrollments")
@Tag(name = "HR10 - Enrollment Management", description = "報名管理 (Command)")
@RequiredArgsConstructor
public class HR10EnrollmentCmdController extends CommandBaseController {

    @PostMapping
    @Operation(summary = "報名課程", operationId = "enrollCourse")
    public ResponseEntity<TrainingEnrollmentResponse> enrollCourse(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @RequestBody @org.springframework.validation.annotation.Validated({
                    Default.class }) EnrollCourseRequest request)
            throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @PostMapping("/{enrollmentId}/approve")
    @Operation(summary = "審核通過", operationId = "approveEnrollment")
    public ResponseEntity<Void> approveEnrollment(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @PathVariable String enrollmentId,
            @RequestBody(required = false) ApproveEnrollmentRequest request) throws Exception {
        if (request == null)
            request = new ApproveEnrollmentRequest();
        return ResponseEntity.ok(execCommand(request, currentUser, enrollmentId));
    }

    @PostMapping("/{enrollmentId}/reject")
    @Operation(summary = "拒絕報名", operationId = "rejectEnrollment")
    public ResponseEntity<Void> rejectEnrollment(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @PathVariable String enrollmentId,
            @RequestBody @Valid RejectEnrollmentRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, enrollmentId));
    }

    @PostMapping("/{enrollmentId}/cancel")
    @Operation(summary = "取消報名", operationId = "cancelEnrollment")
    public ResponseEntity<Void> cancelEnrollment(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @PathVariable String enrollmentId,
            @RequestBody @Valid CancelEnrollmentRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, enrollmentId));
    }

    @PostMapping("/{enrollmentId}/attendance")
    @Operation(summary = "確認出席", operationId = "confirmAttendance")
    public ResponseEntity<Void> confirmAttendance(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @PathVariable String enrollmentId,
            @RequestBody @Valid ConfirmAttendanceRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, enrollmentId));
    }

    @PostMapping("/{enrollmentId}/complete")
    @Operation(summary = "完成結訓", operationId = "completeTraining")
    public ResponseEntity<Void> completeTraining(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @PathVariable String enrollmentId,
            @RequestBody @Valid CompleteTrainingRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, enrollmentId));
    }
}
