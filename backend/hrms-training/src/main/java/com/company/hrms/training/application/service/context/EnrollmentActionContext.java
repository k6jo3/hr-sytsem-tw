package com.company.hrms.training.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.training.domain.model.aggregate.TrainingEnrollment;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 報名操作 Context 基類
 * 用於統一報名相關的 Pipeline Context
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class EnrollmentActionContext extends PipelineContext {
    private String enrollmentId;
    private TrainingEnrollment enrollment;
    private String operatorId;

    // No-args constructor for Lombok/Framework
    public EnrollmentActionContext() {
    }

    public EnrollmentActionContext(String enrollmentId, String operatorId) {
        this.enrollmentId = enrollmentId;
        this.operatorId = operatorId;
    }
}
