package com.company.hrms.reporting.infrastructure.readmodel;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 專案成本分析讀模型
 * 
 * <p>
 * 從專案服務與工時服務的事件更新
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Entity
@Table(name = "rm_project_cost_analysis", indexes = {
        @Index(name = "idx_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_customer_id", columnList = "customer_id"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_start_date", columnList = "start_date")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCostAnalysisReadModel {

    @Id
    @Column(name = "project_id", length = 50)
    private String projectId;

    @Column(name = "tenant_id", length = 50, nullable = false)
    private String tenantId;

    @Column(name = "project_name", length = 200, nullable = false)
    private String projectName;

    @Column(name = "customer_id", length = 50)
    private String customerId;

    @Column(name = "customer_name", length = 200)
    private String customerName;

    @Column(name = "project_manager_id", length = 50)
    private String projectManagerId;

    @Column(name = "project_manager", length = 100)
    private String projectManager;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "budget_amount", precision = 15, scale = 2)
    private BigDecimal budgetAmount;

    @Column(name = "labor_cost", precision = 15, scale = 2)
    private BigDecimal laborCost;

    @Column(name = "other_cost", precision = 15, scale = 2)
    private BigDecimal otherCost;

    @Column(name = "total_cost", precision = 15, scale = 2)
    private BigDecimal totalCost;

    @Column(name = "cost_variance", precision = 15, scale = 2)
    private BigDecimal costVariance;

    @Column(name = "cost_variance_rate", precision = 5, scale = 2)
    private Double costVarianceRate;

    @Column(name = "total_hours", precision = 10, scale = 2)
    private Double totalHours;

    @Column(name = "utilization_rate", precision = 5, scale = 2)
    private Double utilizationRate;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
