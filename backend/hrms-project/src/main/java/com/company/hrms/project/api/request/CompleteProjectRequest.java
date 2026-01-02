package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "完成專案請求")
public class CompleteProjectRequest {
    @Schema(description = "專案ID", hidden = true)
    private String projectId;
}
