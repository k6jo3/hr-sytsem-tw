package com.company.hrms.organization.api.request.employee;

import java.time.LocalDate;

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
    private String name;

    @Schema(description = "工號")
    private String employeeNo;

    @Schema(description = "部門 ID")
    private String deptId;

    @Schema(description = "職位 ID")
    private String positionId;

    @Schema(description = "員工狀態 (ACTIVE/RESIGNED/ON_LEAVE etc)")
    private String status;

    @Schema(description = "僱用類型 (REGULAR/PROBATION/CONTRACT)")
    private String employmentType;

    @Schema(description = "到職日期起")
    private LocalDate hireStartDate;

    @Schema(description = "到職日期迄")
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
