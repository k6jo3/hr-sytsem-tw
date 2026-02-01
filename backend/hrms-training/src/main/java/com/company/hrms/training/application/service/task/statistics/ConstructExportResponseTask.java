package com.company.hrms.training.application.service.task.statistics;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.api.response.ExportResponse;
import com.company.hrms.training.application.service.context.statistics.ExportTrainingStatisticsContext;

@Component
public class ConstructExportResponseTask implements PipelineTask<ExportTrainingStatisticsContext> {

    @Override
    public void execute(ExportTrainingStatisticsContext context) {
        ExportResponse response = new ExportResponse();
        response.setFileName(
                String.format("training_statistics_%s_%s.xlsx", context.getStartDate(), context.getEndDate()));
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setData(context.getExcelData());
        response.setRecordCount(context.getEnrollments().size());

        context.setResponse(response);
    }
}
