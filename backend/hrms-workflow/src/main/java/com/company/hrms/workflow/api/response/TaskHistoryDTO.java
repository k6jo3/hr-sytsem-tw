package com.company.hrms.workflow.api.response;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任務歷史紀錄 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "任務歷史紀錄")
public class TaskHistoryDTO {

    @Schema(description = "任務 ID")
    private String taskId;

    @Schema(description = "節點名稱")
    private String nodeName;

    @Schema(description = "簽核者 ID")
    private String approverId;

    @Schema(description = "簽核者姓名")
    private String approverName;

    @Schema(description = "任務狀態")
    private String status;

    @Schema(description = "簽核意見")
    private String comment;

    @Schema(description = "建立時間")
    private LocalDateTime createdAt;

    @Schema(description = "完成時間 (簽核/駁回時間)")
    private LocalDateTime completedAt;
}
