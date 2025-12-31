package com.company.hrms.attendance.api.request.attendance;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 審核補卡請求 DTO
 */
@Data
@Schema(description = "審核補卡請求")
public class ApproveCorrectionRequest {

    @Schema(description = "審核備註")
    private String comment;
}
