package com.company.hrms.recruitment.application.dto.job;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "關閉職缺回應")
public class CloseJobOpeningResponse implements Serializable {

    @Schema(description = "職缺 ID")
    private String openingId;

    @Schema(description = "狀態")
    private String status;

    @Schema(description = "關閉時間")
    private LocalDateTime closedAt;
}
