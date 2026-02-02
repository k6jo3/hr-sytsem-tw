package com.company.hrms.recruitment.application.service.task;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.recruitment.application.service.context.DashboardContext;

@Component
public class PrepareDashboardDatesTask implements PipelineTask<DashboardContext> {

    @Override
    public void execute(DashboardContext context) throws Exception {
        LocalDate dateFrom = context.getRequest().getDateFrom();
        LocalDate dateTo = context.getRequest().getDateTo();

        if (dateFrom == null) {
            dateFrom = LocalDate.now().withDayOfMonth(1);
        }
        if (dateTo == null) {
            dateTo = LocalDate.now();
        }

        context.setDateFrom(dateFrom);
        context.setDateTo(dateTo);
    }

    @Override
    public String getName() {
        return "準備日期範圍";
    }
}
