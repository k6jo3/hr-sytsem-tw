package com.company.hrms.training.api.request;

import java.time.LocalDate;

import com.company.hrms.common.api.request.PageRequest;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;
import com.company.hrms.training.domain.model.valueobject.CourseCategory;
import com.company.hrms.training.domain.model.valueobject.CourseStatus;
import com.company.hrms.training.domain.model.valueobject.CourseType;
import com.company.hrms.training.domain.model.valueobject.DeliveryMode;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 查詢課程列表請求
 * 使用 @QueryFilter 註解進行宣告式查詢
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GetCoursesRequest extends PageRequest {

    @QueryFilter(property = "status", operator = Operator.EQ)
    private CourseStatus status;

    @QueryFilter(property = "courseType", operator = Operator.EQ)
    private CourseType courseType;

    @QueryFilter(property = "deliveryMode", operator = Operator.EQ)
    private DeliveryMode deliveryMode;

    @QueryFilter(property = "category", operator = Operator.EQ)
    private CourseCategory category;

    @QueryFilter(property = "courseName", operator = Operator.LIKE)
    private String courseName;

    @QueryFilter(property = "instructor", operator = Operator.LIKE)
    private String instructor;

    @QueryFilter(property = "isMandatory", operator = Operator.EQ)
    private Boolean isMandatory;

    @QueryFilter(property = "startDate", operator = Operator.GTE)
    private LocalDate startDateFrom;

    @QueryFilter(property = "startDate", operator = Operator.LTE)
    private LocalDate startDateTo;
}
