package com.company.hrms.organization.api.response.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * 合約詳情回應 DTO
 */
@Data
@Builder
@Schema(description = "合約詳情回應")
public class ContractDetailResponse {

    @Schema(description = "合約ID")
    private String contractId;

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "員工編號")
    private String employeeNumber;

    @Schema(description = "員工姓名")
    private String employeeName;

    @Schema(description = "合約類型")
    private String contractType;

    @Schema(description = "合約類型顯示名稱")
    private String contractTypeDisplay;

    @Schema(description = "合約開始日期")
    private LocalDate startDate;

    @Schema(description = "合約結束日期")
    private LocalDate endDate;

    @Schema(description = "合約狀態")
    private String status;

    @Schema(description = "合約狀態顯示名稱")
    private String statusDisplay;

    @Schema(description = "試用期月數")
    private Integer probationMonths;

    @Schema(description = "續約次數")
    private int renewalCount;

    @Schema(description = "備註")
    private String notes;

    @Schema(description = "剩餘天數 (定期合約)")
    private Integer remainingDays;
}
