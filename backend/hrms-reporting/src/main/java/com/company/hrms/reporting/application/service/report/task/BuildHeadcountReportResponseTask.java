package com.company.hrms.reporting.application.service.report.task;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.reporting.api.response.HeadcountReportResponse;
import com.company.hrms.reporting.application.service.report.context.GetHeadcountReportContext;

/**
 * 構建人力盤點報表回應任務
 */
@Component
public class BuildHeadcountReportResponseTask implements PipelineTask<GetHeadcountReportContext> {

    @Override
    public void execute(GetHeadcountReportContext ctx) {
        HeadcountReportResponse response = HeadcountReportResponse.builder()
                .content(ctx.getItems())
                .totalElements((long) ctx.getItems().size())
                .totalPages(1)
                .summary(ctx.getSummary())
                .build();

        ctx.setResponse(response);
    }
}
