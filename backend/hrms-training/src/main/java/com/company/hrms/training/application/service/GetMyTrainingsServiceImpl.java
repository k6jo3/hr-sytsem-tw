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
import com.company.hrms.training.api.request.GetMyTrainingsRequest;
import com.company.hrms.training.api.response.TrainingEnrollmentResponse;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;
import com.company.hrms.training.infrastructure.repository.TrainingEnrollmentQueryRepository;

import lombok.RequiredArgsConstructor;

/**
 * ?•Ë©¢?ëÁ?Ë®ìÁ∑¥Á¥Ä?ÑÊ???
 */
@Service("getMyTrainingsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetMyTrainingsServiceImpl
        implements QueryApiService<GetMyTrainingsRequest, Page<TrainingEnrollmentResponse>> {

    private final TrainingEnrollmentQueryRepository enrollmentRepository;

    @Override
    public Page<TrainingEnrollmentResponse> getResponse(GetMyTrainingsRequest request, JWTModel currentUser,
            String... args) {
        // ‰ΩøÁî® QueryBuilder Âæ?Request DTO ?™Â?Âª∫Ê??•Ë©¢Ê¢ù‰ª∂ (ÂÆ??ÂºèÊü•Ë©?
        QueryGroup query = QueryBuilder.where()
                .fromDto(request)
                .eq("employee_id", currentUser.getUserId()) // Âº∑Âà∂?†ÂÖ•?°Â∑•ID?éÊøæ
                .build();

        // ?ñÂ??ÜÈ?Ë≥áË? (Âæ?BaseRequest ËΩâÊ?)
        Pageable pageable = request.toPageable();

        // ?∑Ë??•Ë©¢
        Page<TrainingEnrollmentEntity> page = enrollmentRepository.findPage(query, pageable);

        // ËΩâÊ???DTO
        List<TrainingEnrollmentResponse> responseList = new ArrayList<>();
        for (TrainingEnrollmentEntity enrollment : page.getContent()) {
            responseList.add(toResponse(enrollment));
        }

        return new PageImpl<>(responseList, pageable, page.getTotalElements());
    }

    // ÁßªÈô§‰∏çÂ?Ë¶ÅÁ? overload ?πÊ?ÔºåÂ???Pageable Â∑≤Â??´Âú® request ‰∏?

    private TrainingEnrollmentResponse toResponse(TrainingEnrollmentEntity enrollment) {
        // TODO: Á®ãÂ?Â§™Èï∑ÔºåÂª∫Ë≠∞Áî®objectMapper?ñstructMapper
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
