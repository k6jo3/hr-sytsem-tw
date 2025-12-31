package com.company.hrms.attendance.application.service.correction.context;

import com.company.hrms.attendance.api.request.attendance.CreateCorrectionRequest;
import com.company.hrms.attendance.domain.model.aggregate.CorrectionApplication;
import com.company.hrms.common.application.pipeline.PipelineContext;
import lombok.Getter;
import lombok.Setter;

/**
 * 補卡申請 Context
 */
@Getter
@Setter
public class CorrectionContext extends PipelineContext {

    private CreateCorrectionRequest request;
    private String tenantId;
    private CorrectionApplication application;

    public CorrectionContext(CreateCorrectionRequest request, String tenantId) {
        this.request = request;
        this.tenantId = tenantId;
    }
}
