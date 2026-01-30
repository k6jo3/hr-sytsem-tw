package com.company.hrms.training.application.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.training.api.request.GetEnrollmentsRequest;
import com.company.hrms.training.api.response.TrainingEnrollmentResponse;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;
import com.company.hrms.training.infrastructure.repository.TrainingEnrollmentQueryRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢報名列表服務
 * 使用 QueryBuilder.fromDto() 進行宣告式查詢
 */
@Service("getEnrollmentsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEnrollmentsServiceImpl
        implements QueryApiService<GetEnrollmentsRequest, Page<TrainingEnrollmentResponse>> {

    private final TrainingEnrollmentQueryRepository enrollmentRepository;

    @Override
    public Page<TrainingEnrollmentResponse> getResponse(GetEnrollmentsRequest request, JWTModel currentUser,
            String... args) {
        // 使用 QueryBuilder 從 Request DTO 自動建構查詢條件 (宣告式查詢)
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .build();

        // 取得分頁資訊 (從 PageRequest 轉換)
        Pageable pageable = request.toPageable();

        // 執行查詢
        Page<TrainingEnrollmentEntity> page = enrollmentRepository.findPage(query, pageable);

        // 轉換為 DTO
        List<TrainingEnrollmentResponse> responseList = new ArrayList<>();
        for (TrainingEnrollmentEntity enrollment : page.getContent()) {
            responseList.add(toResponse(enrollment));
        }

        return new PageImpl<>(responseList, pageable, page.getTotalElements());
    }

    private TrainingEnrollmentResponse toResponse(TrainingEnrollmentEntity enrollment) {
        // TODO: 程式太長，建議用objectMapper或structMapper
        TrainingEnrollmentResponse res = new TrainingEnrollmentResponse();
        res.setEnrollmentId(enrollment.getEnrollmentId());
        res.setCourseId(enrollment.getCourse_id());
        res.setEmployeeId(enrollment.getEmployee_id());
        res.setStatus(enrollment.getStatus());
        res.setReason(enrollment.getReason());
        res.setRemarks(enrollment.getRemarks());
        res.setApprovedBy(enrollment.getApprovedBy());
        res.setApprovedAt(enrollment.getApprovedAt());
        res.setRejectedBy(enrollment.getRejectedBy());
        res.setRejectedAt(enrollment.getRejectedAt());
        res.setRejectReason(enrollment.getRejectReason());
        res.setCancelledBy(enrollment.getCancelledBy());
        res.setCancelledAt(enrollment.getCancelledAt());
        res.setCancelReason(enrollment.getCancelReason());
        res.setAttendance(enrollment.isAttendance());
        res.setAttendedHours(enrollment.getAttendedHours());
        res.setCompletedHours(enrollment.getCompletedHours());
        res.setScore(enrollment.getScore());
        res.setPassed(enrollment.getPassed());
        res.setFeedback(enrollment.getFeedback());
        res.setCompletedAt(enrollment.getCompletedAt());
        res.setCreatedAt(enrollment.getCreatedAt());
        res.setUpdatedAt(enrollment.getUpdatedAt());
        return res;
    }
}
