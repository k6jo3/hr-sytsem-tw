package com.company.hrms.organization.api.request.contract;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

/**
 * 新增合約請求 DTO
 */
@Data
@Schema(description = "新增合約請求")
public class CreateContractRequest {

    @NotBlank(message = "合約類型為必填")
    @Schema(description = "合約類型", example = "INDEFINITE", allowableValues = {"INDEFINITE", "FIXED_TERM"})
    private String contractType;

    @NotNull(message = "合約開始日期為必填")
    @Schema(description = "合約開始日期", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "合約結束日期 (定期合約必填)", example = "2024-12-31")
    private LocalDate endDate;

    @Schema(description = "試用期月數", example = "3")
    private Integer probationMonths;

    @Schema(description = "備註")
    private String notes;
}
