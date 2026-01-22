package com.company.hrms.project.api.response;

import java.time.LocalDate;

import com.company.hrms.project.domain.model.valueobject.ProjectStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 暫停專案回應
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HoldProjectResponse {

    /**
     * 專案 ID
     */
    private String projectId;

    /**
     * 專案狀態
     */
    private ProjectStatus status;

    /**
     * 暫停原因
     */
    private String holdReason;

    /**
     * 暫停日期
     */
    private LocalDate holdDate;
}
