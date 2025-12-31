package com.company.hrms.attendance.api.response.checkin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 補卡申請列表項目 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "補卡申請列表項目")
public class CorrectionListResponse {

    @Schema(description = "補卡申請ID")
    private String correctionId;

    @Schema(description = "員工ID")
    private String employeeId;

    @Schema(description = "員工姓名")
    private String employeeName;

    @Schema(description = "申請日期")
    private LocalDate applicationDate;

    @Schema(description = "補卡日期")
    private LocalDate correctionDate;

    @Schema(description = "補卡類型", allowableValues = {"CHECK_IN", "CHECK_OUT", "BOTH"})
    private String correctionType;

    @Schema(description = "申請狀態", allowableValues = {"PENDING", "APPROVED", "REJECTED"})
    private String status;

    @Schema(description = "申請時間")
    private LocalDateTime appliedAt;

    @Schema(description = "審核時間")
    private LocalDateTime reviewedAt;
}
