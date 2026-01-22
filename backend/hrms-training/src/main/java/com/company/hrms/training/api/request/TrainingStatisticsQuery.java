package com.company.hrms.training.api.request;

import java.time.LocalDate;

import com.company.hrms.common.query.QueryGroup;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrainingStatisticsQuery extends QueryGroup {
    private LocalDate startDate;
    private LocalDate endDate;
}
