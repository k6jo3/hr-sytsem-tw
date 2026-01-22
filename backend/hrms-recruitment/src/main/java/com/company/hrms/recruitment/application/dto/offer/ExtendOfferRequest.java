package com.company.hrms.recruitment.application.dto.offer;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 延長 Offer 到期日請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "延長 Offer 到期日請求")
public class ExtendOfferRequest {

    @NotNull(message = "新到期日不可為空")
    @Future(message = "新到期日必須是未來日期")
    @Schema(description = "新的到期日", example = "2026-02-01")
    private LocalDate newExpiryDate;
}
