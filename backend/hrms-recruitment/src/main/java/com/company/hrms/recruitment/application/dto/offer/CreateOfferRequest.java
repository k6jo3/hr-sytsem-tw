package com.company.hrms.recruitment.application.dto.offer;

import java.math.BigDecimal;
import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 建立 Offer 請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "建立 Offer 請求")
public class CreateOfferRequest {

    @NotBlank(message = "應徵者 ID 不可為空")
    @Schema(description = "應徵者 ID", example = "cand-001")
    private String candidateId;

    @NotBlank(message = "錄取職位不可為空")
    @Schema(description = "錄取職位", example = "資深軟體工程師")
    private String offeredPosition;

    @NotNull(message = "錄取薪資不可為空")
    @Positive(message = "錄取薪資必須大於 0")
    @Schema(description = "錄取薪資", example = "80000")
    private BigDecimal offeredSalary;

    @Schema(description = "預計到職日", example = "2026-02-01")
    private LocalDate offeredStartDate;

    @NotNull(message = "到期日不可為空")
    @Future(message = "到期日必須是未來日期")
    @Schema(description = "Offer 到期日", example = "2026-01-25")
    private LocalDate expiryDate;
}
