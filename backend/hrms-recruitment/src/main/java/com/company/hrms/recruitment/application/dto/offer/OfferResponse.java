package com.company.hrms.recruitment.application.dto.offer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.company.hrms.recruitment.domain.model.valueobject.OfferStatus;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Offer 回應 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Offer 回應")
public class OfferResponse {

    @Schema(description = "Offer ID", example = "offer-001")
    private String id;

    @Schema(description = "應徵者 ID", example = "cand-001")
    private String candidateId;

    @Schema(description = "應徵者姓名", example = "王小明")
    private String candidateName;

    @Schema(description = "錄取職位", example = "資深軟體工程師")
    private String offeredPosition;

    @Schema(description = "錄取薪資", example = "80000")
    private BigDecimal offeredSalary;

    @Schema(description = "預計到職日", example = "2026-02-01")
    private LocalDate offeredStartDate;

    @Schema(description = "Offer 發送日期", example = "2026-01-15")
    private LocalDate offerDate;

    @Schema(description = "Offer 到期日", example = "2026-01-25")
    private LocalDate expiryDate;

    @Schema(description = "Offer 狀態", example = "PENDING")
    private OfferStatus status;

    @Schema(description = "回覆日期", example = "2026-01-20")
    private LocalDate responseDate;

    @Schema(description = "拒絕原因")
    private String rejectionReason;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "更新時間")
    private LocalDateTime updatedAt;
}
