package com.company.hrms.recruitment.application.service.context;

import java.time.LocalDate;

import com.company.hrms.common.application.pipeline.PipelineContext;
import com.company.hrms.recruitment.application.dto.report.DashboardResponse;
import com.company.hrms.recruitment.application.dto.report.DashboardSearchDto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardContext extends PipelineContext {
    private DashboardSearchDto request;
    private LocalDate dateFrom;
    private LocalDate dateTo;

    private long openJobsCount;
    private long totalApplicationsCount;
    private long interviewsCount;
    private long offersCount;
    private long hiredCount;

    // Aggregates placeholder
    // private List<SourceAnalytic> sourceAnalytics;
    // ...

    private DashboardResponse response;

    public DashboardContext(DashboardSearchDto request) {
        this.request = request;
    }
}
