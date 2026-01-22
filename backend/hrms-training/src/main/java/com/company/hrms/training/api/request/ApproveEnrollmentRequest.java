package com.company.hrms.training.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "核准報名請求")
public class ApproveEnrollmentRequest {
    // Usually empty, but can carry remarks or override info
    @Schema(description = "審核備註")
    private String remarks;
}
