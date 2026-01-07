package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "更新工項進度請求")
public class UpdateTaskProgressRequest {
    @Schema(description = "工項ID", hidden = true)
    private String taskId;

    @Schema(description = "進度 (0-100)")
    private int progress;
}
