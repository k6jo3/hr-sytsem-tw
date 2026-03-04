package com.company.hrms.training.api.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.company.hrms.common.annotation.CurrentUser;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.training.api.request.GetCoursesRequest;
import com.company.hrms.training.api.response.TrainingCourseResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 課程管理查詢 Controller
 * 使用 GetCoursesRequest DTO 進行宣告式查詢
 */
@RestController
@RequestMapping("/api/v1/training/courses")
@Tag(name = "HR10 - Course Management", description = "課程管理 (Query)")
@RequiredArgsConstructor
public class HR10CourseQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢課程列表", operationId = "getCourses")
    public ResponseEntity<Page<TrainingCourseResponse>> getCourses(
            @CurrentUser JWTModel currentUser,
            GetCoursesRequest request) throws Exception {
        if (request == null) {
            request = new GetCoursesRequest();
        }
        return ResponseEntity.ok(getResponse(request, currentUser));
    }

    @GetMapping("/{courseId}")
    @Operation(summary = "查詢課程詳情", operationId = "getCourseDetail")
    public ResponseEntity<TrainingCourseResponse> getCourseDetail(
            @CurrentUser JWTModel currentUser,
            @PathVariable String courseId) throws Exception {
        return ResponseEntity.ok(getResponse(courseId, currentUser));
    }
}
