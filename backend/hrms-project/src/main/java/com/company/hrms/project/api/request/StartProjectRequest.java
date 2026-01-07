package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Schema(description = "啟動專案請求")
public class StartProjectRequest {
    @Schema(description = "專案ID", hidden = true)
    private String projectId;
}
