package com.company.hrms.training.application.assembler;

import org.springframework.stereotype.Component;

import com.company.hrms.training.api.response.TrainingEnrollmentResponse;
import com.company.hrms.training.infrastructure.entity.TrainingEnrollmentEntity;

/**
 * 訓練報名 Assembler
 * 負責 Entity 與 DTO 之間的轉換
 */
@Component
public class TrainingEnrollmentAssembler {

    public TrainingEnrollmentResponse toResponse(TrainingEnrollmentEntity enrollment) {
        if (enrollment == null) {
            return null;
        }

        TrainingEnrollmentResponse res = new TrainingEnrollmentResponse();
        res.setEnrollmentId(enrollment.getEnrollmentId());
        res.setCourseId(enrollment.getCourseId());
        res.setEmployeeId(enrollment.getEmployeeId());
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
