package com.company.hrms.organization.api.request.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;

/**
 * 更新合約請求 DTO
 */
@Data
@Schema(description = "更新合約請求")
public class UpdateContractRequest {

    @Schema(description = "合約結束日期", example = "2025-12-31")
    private LocalDate endDate;

    @Schema(description = "備註")
    private String notes;

    @Schema(description = "合約狀態", example = "ACTIVE", allowableValues = {"ACTIVE", "EXPIRED", "TERMINATED"})
    private String status;
}
