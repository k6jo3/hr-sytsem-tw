package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢專案詳情請求")
public class GetProjectDetailRequest {

    @Schema(description = "專案ID")
    private String projectId;
}
