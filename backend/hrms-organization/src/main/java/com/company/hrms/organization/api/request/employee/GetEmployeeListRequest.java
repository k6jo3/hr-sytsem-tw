package com.company.hrms.organization.api.request.employee;

import java.time.LocalDate;

import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryFilter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 取得員工列表請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "取得員工列表請求")
public class GetEmployeeListRequest {

    @Schema(description = "關鍵字 (姓名/工號)")
    private String keyword;

    @Schema(description = "姓名 (模糊查詢)")
    @QueryFilter(property = "full_name", operator = Operator.LIKE)
    private String name;

    @Schema(description = "工號")
    @QueryFilter(property = "employee_number", operator = Operator.EQ)
    private String employeeNumber;

    @Schema(description = "部門 ID")
    @QueryFilter(property = "department_id", operator = Operator.EQ)
    private String departmentId;

    @Schema(description = "職位 ID")
    @QueryFilter(property = "job_title", operator = Operator.EQ)
    private String positionId;

    @Schema(description = "員工狀態 (ACTIVE/TERMINATED/PROBATION etc)")
    @QueryFilter(property = "employment_status", operator = Operator.EQ)
    private String employmentStatus;

    @Schema(description = "僱用類型 (REGULAR/PROBATION/CONTRACT)")
    @QueryFilter(property = "employment_type", operator = Operator.EQ)
    private String employmentType;

    @Schema(description = "到職日期起")
    @QueryFilter(property = "hire_date", operator = Operator.GTE)
    private LocalDate hireDateFrom;

    @Schema(description = "到職日期迄")
    @QueryFilter(property = "hire_date", operator = Operator.LTE)
    private LocalDate hireDateTo;

    @Schema(description = "頁碼 (預設 1)")
    @Builder.Default
    private int page = 1;

    @Schema(description = "每頁筆數 (預設 20)")
    @Builder.Default
    private int size = 20;

    @Schema(description = "排序欄位")
    private String sort;
}
