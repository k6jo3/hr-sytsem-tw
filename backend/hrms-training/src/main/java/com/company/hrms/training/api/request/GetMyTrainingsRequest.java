package com.company.hrms.training.api.request;

import java.time.LocalDate;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;
import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查詢我的訓練請求
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetMyTrainingsRequest extends PageRequest {

    // 不需要 employeeId，因為這是從當前登入者取得

    @QueryFilter(property = "is_deleted", operator = Operator.EQ)
    private Integer is_deleted = 0;

    @QueryFilter(property = "status", operator = Operator.EQ)
    private EnrollmentStatus status;

    @QueryFilter(property = "courseName", operator = Operator.LIKE)
    private String courseName;

    @QueryFilter(property = "startDate", operator = Operator.GTE)
    private LocalDate startDateFrom;

    @QueryFilter(property = "startDate", operator = Operator.LTE)
    private LocalDate startDateTo;
}
