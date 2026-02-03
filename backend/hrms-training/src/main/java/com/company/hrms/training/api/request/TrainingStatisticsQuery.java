package com.company.hrms.training.api.request;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TrainingStatisticsQuery {
    @com.company.hrms.common.query.QueryFilter(property = "createdAt", operator = com.company.hrms.common.query.Operator.GTE)
    private LocalDate startDate;

    @com.company.hrms.common.query.QueryFilter(property = "createdAt", operator = com.company.hrms.common.query.Operator.LTE)
    private LocalDate endDate;
}
