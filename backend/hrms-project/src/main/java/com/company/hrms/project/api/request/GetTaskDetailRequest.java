package com.company.hrms.project.api.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "查詢工項詳情請求")
public class GetTaskDetailRequest {

    @Schema(description = "工項 ID")
    private String taskId;
}
