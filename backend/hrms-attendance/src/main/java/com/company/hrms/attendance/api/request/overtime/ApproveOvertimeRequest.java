package com.company.hrms.attendance.api.request.overtime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 核准加班請求 DTO
 */
@Data
@Schema(description = "核准加班請求")
public class ApproveOvertimeRequest {

    @Schema(description = "審核備註")
    private String comment;
}
