package com.company.hrms.insurance.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 批量調整投保級距 Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchAdjustLevelsResponse {

    /**
     * 舊級距停用筆數
     */
    private int oldLevelsDeactivated;

    /**
     * 新級距建立筆數
     */
    private int newLevelsCreated;

    /**
     * 處理訊息
     */
    private String message;
}
