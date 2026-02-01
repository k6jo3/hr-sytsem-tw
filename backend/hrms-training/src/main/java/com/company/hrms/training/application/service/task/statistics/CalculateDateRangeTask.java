package com.company.hrms.training.application.service.task.statistics;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.training.application.service.context.statistics.ExportTrainingStatisticsContext;

@Component
public class CalculateDateRangeTask implements PipelineTask<ExportTrainingStatisticsContext> {

    @Override
    public void execute(ExportTrainingStatisticsContext context) {
        LocalDate startDate = context.getQuery().getStartDate() != null
                ? context.getQuery().getStartDate()
                : LocalDate.of(LocalDate.now().getYear(), 1, 1);
        LocalDate endDate = context.getQuery().getEndDate() != null
                ? context.getQuery().getEndDate()
                : LocalDate.now();

        context.setStartDate(startDate);
        context.setEndDate(endDate);
    }
}
