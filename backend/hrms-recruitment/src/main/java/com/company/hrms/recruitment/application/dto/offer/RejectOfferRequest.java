package com.company.hrms.recruitment.application.dto.offer;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 拒絕 Offer 請求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "拒絕 Offer 請求")
public class RejectOfferRequest {

    @Schema(description = "拒絕原因", example = "已接受其他公司 offer")
    private String reason;
}
