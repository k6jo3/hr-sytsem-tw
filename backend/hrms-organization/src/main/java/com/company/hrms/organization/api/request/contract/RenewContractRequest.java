package com.company.hrms.organization.api.request.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 續約請求 DTO
 */
@Data
@Schema(description = "續約請求")
public class RenewContractRequest {

    @NotNull(message = "新合約開始日期為必填")
    @Schema(description = "新合約開始日期", example = "2025-01-01")
    private LocalDate newStartDate;

    @Schema(description = "新合約結束日期 (定期合約必填)", example = "2025-12-31")
    private LocalDate newEndDate;

    @Schema(description = "備註")
    private String notes;
}
