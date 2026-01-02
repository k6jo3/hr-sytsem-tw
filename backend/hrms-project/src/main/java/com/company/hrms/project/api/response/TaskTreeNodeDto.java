package com.company.hrms.project.api.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.company.hrms.project.domain.model.valueobject.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskTreeNodeDto {
    private String taskId;
    private String taskName;
    private String parentId;
    private TaskStatus status;
    private int progress;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal estimatedHours;
    private UUID assigneeId;

    @Builder.Default
    private List<TaskTreeNodeDto> children = new ArrayList<>();
}
