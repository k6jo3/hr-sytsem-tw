package com.company.hrms.project.api.request;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "指派工項請求")
public class AssignTaskRequest {
    @Schema(description = "工項ID", hidden = true)
    private String taskId;

    @Schema(description = "負責人ID")
    private UUID assigneeId;
}
