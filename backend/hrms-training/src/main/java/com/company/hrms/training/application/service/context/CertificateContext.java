package com.company.hrms.training.application.service.context;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.training.domain.model.aggregate.Certificate;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 證照操作 Context 基類
 */
@Data
@EqualsAndHashCode(callSuper = false)
public abstract class CertificateContext extends PipelineContext {
    private Certificate certificate;
    private String operatorId;

    public CertificateContext() {
    }

    public CertificateContext(String operatorId) {
        this.operatorId = operatorId;
    }
}
