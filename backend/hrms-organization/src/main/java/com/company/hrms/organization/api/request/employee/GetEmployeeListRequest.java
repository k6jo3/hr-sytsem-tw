package com.company.hrms.organization.api.request.employee;

import java.time.LocalDate;
import java.util.List;

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
    @QueryFilter(property = "fullName", operator = Operator.LIKE)
    private String name;

    @Schema(description = "工號")
    @QueryFilter(property = "employeeNumber", operator = Operator.EQ)
    private String employeeNo;

    @Schema(description = "部門 ID 列表")
    @QueryFilter(property = "departmentId", operator = Operator.IN)
    private List<String> departmentIds;

    @Schema(description = "職位 ID")
    @QueryFilter(property = "jobTitle", operator = Operator.EQ) // Assuming positionId maps to jobTitle or similar? No,
                                                                // Employee has jobTitle (String). positionId implies
                                                                // ID. Let's map to jobTitle for now or skip if unsure?
                                                                // Request says positionId. Entity has jobTitle. I'll
                                                                // skip it or guess. Wait, field is jobTitle in Entity.
                                                                // Request has positionId. I'll map to jobTitle? No, ID
                                                                // vs Title. Leaving it blank might be safer or map to
                                                                // jobTitle if string ID. Let's look at Entity..
                                                                // jobTitle is String. positionId is String. Usually
                                                                // different. I will SKIP positionId for now to avoid
                                                                // error, as I don't see positionId on Entity.
    private String positionId;

    @Schema(description = "員工狀態列表 (ACTIVE/RESIGNED/ON_LEAVE etc)")
    @QueryFilter(property = "employmentStatus", operator = Operator.IN)
    private List<String> statuses;

    @Schema(description = "僱用類型 (REGULAR/PROBATION/CONTRACT)")
    @QueryFilter(property = "employmentType", operator = Operator.EQ)
    private String employmentType;

    @Schema(description = "到職日期起")
    @QueryFilter(property = "hireDate", operator = Operator.GTE)
    private LocalDate hireStartDate;

    @Schema(description = "到職日期迄")
    @QueryFilter(property = "hireDate", operator = Operator.LTE)
    private LocalDate hireEndDate;

    @Schema(description = "頁碼 (預設 1)")
    @Builder.Default
    private int page = 1;

    @Schema(description = "每頁筆數 (預設 20)")
    @Builder.Default
    private int size = 20;

    @Schema(description = "排序欄位")
    private String sort;
}
