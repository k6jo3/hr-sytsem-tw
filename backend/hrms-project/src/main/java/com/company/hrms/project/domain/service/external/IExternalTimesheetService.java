package com.company.hrms.project.domain.service.external;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

/**
 * 外部工時服務介面
 */
public interface IExternalTimesheetService {

    /**
     * 獲取專案按月彙總的工時與成本
     * 
     * @param projectId 專案 ID
     * @return 按月彙總列表
     */
    List<MonthlyCostData> getMonthlyCosts(UUID projectId);

    @Data
    @Builder
    public static class MonthlyCostData {
        private String yearMonth; // 格式: YYYY-MM
        private BigDecimal hours;
        private BigDecimal cost;
    }
}
