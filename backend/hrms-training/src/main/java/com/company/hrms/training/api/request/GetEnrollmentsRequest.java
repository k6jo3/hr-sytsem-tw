package com.company.hrms.training.api.request;

import java.time.LocalDate;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;
import com.company.hrms.training.domain.model.valueobject.EnrollmentStatus;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查詢報名列表請求
 * 使用 @QueryFilter 註解進行宣告式查詢
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetEnrollmentsRequest extends PageRequest {

    @QueryFilter(property = "is_deleted", operator = Operator.EQ)
    private Integer is_deleted = 0;

    @QueryFilter(property = "course_id", operator = Operator.EQ)
    private String courseId;

    @QueryFilter(property = "employee_id", operator = Operator.EQ)
    private String employeeId;

    @QueryFilter(property = "status", operator = Operator.EQ)
    private EnrollmentStatus status;

    @QueryFilter(property = "courseName", operator = Operator.LIKE)
    private String courseName;

    @QueryFilter(property = "createdAt", operator = Operator.GTE)
    private LocalDate createdAtFrom;

    @QueryFilter(property = "createdAt", operator = Operator.LTE)
    private LocalDate createdAtTo;
}
