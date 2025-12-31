package com.company.hrms.attendance.application.service.correction.context;

import com.company.hrms.attendance.api.request.attendance.ApproveCorrectionRequest;
import com.company.hrms.attendance.domain.model.aggregate.CorrectionApplication;
import com.company.hrms.common.application.pipeline.PipelineContext;
import lombok.Getter;
import lombok.Setter;

/**
 * 補卡審核 Context
 */
@Getter
@Setter
public class ApproveCorrectionContext extends PipelineContext {

    private ApproveCorrectionRequest request;
    private String tenantId;
    private String correctionId;
    private CorrectionApplication application;

    public ApproveCorrectionContext(ApproveCorrectionRequest request, String tenantId) {
        this.request = request;
        this.tenantId = tenantId;
    }
}
