package com.company.hrms.training.api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.CommandBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.training.api.request.CloseCourseRequest;
import com.company.hrms.training.api.request.CourseActionRequest;
import com.company.hrms.training.api.request.CreateCourseRequest;
import com.company.hrms.training.api.request.UpdateCourseRequest;
import com.company.hrms.training.api.response.TrainingCourseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/training/courses")
@Tag(name = "HR10 - Course Management", description = "課程管理 (Command)")
@RequiredArgsConstructor
public class HR10CourseCmdController extends CommandBaseController {

    @PostMapping
    @Operation(summary = "建立課程", operationId = "createCourse")
    public ResponseEntity<TrainingCourseResponse> createCourse(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @RequestBody @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "課程資訊") @org.springframework.validation.annotation.Validated({
                    Default.class }) CreateCourseRequest request)
            throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser));
    }

    @PutMapping("/{courseId}")
    @Operation(summary = "更新課程", operationId = "updateCourse")
    public ResponseEntity<TrainingCourseResponse> updateCourse(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @PathVariable String courseId,
            @RequestBody @Valid UpdateCourseRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, courseId));
    }

    @PostMapping("/{courseId}/publish")
    @Operation(summary = "發布課程", operationId = "publishCourse")
    public ResponseEntity<Void> publishCourse(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @PathVariable String courseId) throws Exception {
        return ResponseEntity.ok(execCommand(new CourseActionRequest(), currentUser, courseId));
    }

    @PostMapping("/{courseId}/close")
    @Operation(summary = "關閉課程報名", operationId = "closeCourseEnrollment")
    public ResponseEntity<Void> closeCourse(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @PathVariable String courseId,
            @RequestBody @Valid CloseCourseRequest request) throws Exception {
        return ResponseEntity.ok(execCommand(request, currentUser, courseId));
    }

    @PostMapping("/{courseId}/complete")
    @Operation(summary = "完成結訓", operationId = "completeCourse")
    public ResponseEntity<Void> completeCourse(
            @RequestAttribute("currentUser") JWTModel currentUser,
            @PathVariable String courseId) throws Exception {
        return ResponseEntity.ok(execCommand(new CourseActionRequest(), currentUser, courseId));
    }
}
