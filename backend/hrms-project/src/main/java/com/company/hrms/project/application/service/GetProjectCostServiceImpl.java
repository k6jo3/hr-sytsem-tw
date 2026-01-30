package com.company.hrms.project.application.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.project.api.request.GetProjectCostRequest;
import com.company.hrms.project.api.response.GetProjectCostResponse;
import com.company.hrms.project.api.response.GetProjectCostResponse.BudgetInfo;
import com.company.hrms.project.api.response.GetProjectCostResponse.CostSummary;
import com.company.hrms.project.api.response.GetProjectCostResponse.MemberCost;
import com.company.hrms.project.api.response.GetProjectCostResponse.MonthlyCost;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.aggregate.ProjectMember;
import com.company.hrms.project.domain.model.valueobject.ProjectId;
import com.company.hrms.project.domain.repository.IProjectRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢專案成本分析服務
 */
@Service("getProjectCostServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetProjectCostServiceImpl implements QueryApiService<GetProjectCostRequest, GetProjectCostResponse> {

        private final IProjectRepository projectRepository;

        @Override
        public GetProjectCostResponse getResponse(GetProjectCostRequest req, JWTModel currentUser, String... args)
                        throws Exception {
                // TODO: 未符合business pipeline的設計
                // 1. 載入專案
                Project project = projectRepository.findById(new ProjectId(req.getProjectId()))
                                .orElseThrow(() -> new DomainException("專案不存在: " + req.getProjectId()));

                // 2. 計算預算資訊
                BudgetInfo budgetInfo = BudgetInfo.builder()
                                .budgetType(project.getBudget().getBudgetType())
                                .budgetAmount(project.getBudget().getBudgetAmount())
                                .budgetHours(project.getBudget().getBudgetHours())
                                .build();

                // 3. 計算成本摘要
                BigDecimal totalHours = project.getActualHours() != null ? project.getActualHours() : BigDecimal.ZERO;
                BigDecimal totalCost = project.getActualCost() != null ? project.getActualCost() : BigDecimal.ZERO;
                BigDecimal budgetAmount = project.getBudget().getBudgetAmount() != null
                                ? project.getBudget().getBudgetAmount()
                                : BigDecimal.ZERO;
                BigDecimal budgetHours = project.getBudget().getBudgetHours() != null
                                ? project.getBudget().getBudgetHours()
                                : BigDecimal.ZERO;

                BigDecimal budgetUtilization = BigDecimal.ZERO;
                if (budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
                        budgetUtilization = totalCost.multiply(BigDecimal.valueOf(100))
                                        .divide(budgetAmount, 2, RoundingMode.HALF_UP);
                }

                BigDecimal hoursUtilization = BigDecimal.ZERO;
                if (budgetHours.compareTo(BigDecimal.ZERO) > 0) {
                        hoursUtilization = totalHours.multiply(BigDecimal.valueOf(100))
                                        .divide(budgetHours, 2, RoundingMode.HALF_UP);
                }

                BigDecimal estimatedGrossProfit = budgetAmount.subtract(totalCost);
                BigDecimal estimatedGrossProfitMargin = BigDecimal.ZERO;
                if (budgetAmount.compareTo(BigDecimal.ZERO) > 0) {
                        estimatedGrossProfitMargin = estimatedGrossProfit.multiply(BigDecimal.valueOf(100))
                                        .divide(budgetAmount, 2, RoundingMode.HALF_UP);
                }

                CostSummary summary = CostSummary.builder()
                                .totalHours(totalHours)
                                .totalCost(totalCost)
                                .budgetUtilization(budgetUtilization)
                                .hoursUtilization(hoursUtilization)
                                .estimatedGrossProfit(estimatedGrossProfit)
                                .estimatedGrossProfitMargin(estimatedGrossProfitMargin)
                                .burnRate(budgetUtilization)
                                .build();

                // 4. 計算成員成本
                List<MemberCost> memberCosts = new ArrayList<>();
                for (ProjectMember member : project.getMembers()) {
                        BigDecimal memberHours = member.getAllocatedHours() != null ? member.getAllocatedHours()
                                        : BigDecimal.ZERO;
                        BigDecimal hourlyRate = BigDecimal.valueOf(800); // 預設時薪
                        BigDecimal memberCost = memberHours.multiply(hourlyRate);
                        BigDecimal costPercentage = BigDecimal.ZERO;
                        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                                costPercentage = memberCost.multiply(BigDecimal.valueOf(100))
                                                .divide(totalCost, 2, RoundingMode.HALF_UP);
                        }

                        memberCosts.add(MemberCost.builder()
                                        .employeeId(member.getEmployeeId().toString())
                                        .employeeName("員工") // 需要從員工服務取得
                                        .role(member.getRole())
                                        .hours(memberHours)
                                        .hourlyRate(hourlyRate)
                                        .cost(memberCost)
                                        .costPercentage(costPercentage)
                                        .build());
                }

                // 5. 模擬月份成本 (實際應從工時記錄計算)
                List<MonthlyCost> monthlyCosts = new ArrayList<>();

                return GetProjectCostResponse.builder()
                                .projectId(project.getId().getValue().toString())
                                .projectCode(project.getProjectCode())
                                .projectName(project.getProjectName())
                                .budget(budgetInfo)
                                .summary(summary)
                                .byMember(memberCosts)
                                .byMonth(monthlyCosts)
                                .build();
        }
}
