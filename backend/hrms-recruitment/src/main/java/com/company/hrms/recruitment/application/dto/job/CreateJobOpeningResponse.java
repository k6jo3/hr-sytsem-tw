package com.company.hrms.recruitment.application.dto.job;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "建立職缺回應")
public class CreateJobOpeningResponse implements Serializable {

    @Schema(description = "職缺 ID")
    private String openingId;

    @Schema(description = "職缺標題")
    private String jobTitle;

    @Schema(description = "部門 ID")
    private String departmentId;

    @Schema(description = "部門名稱")
    private String departmentName;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;
}
