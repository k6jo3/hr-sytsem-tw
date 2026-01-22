package com.company.hrms.project.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 暫停專案請求
 */
@Data
public class HoldProjectRequest {

    /**
     * 專案 ID
     */
    private String projectId;

    /**
     * 暫停原因
     */
    @NotBlank(message = "暫停原因為必填")
    @Size(max = 500, message = "暫停原因最多 500 字元")
    private String reason;
}
