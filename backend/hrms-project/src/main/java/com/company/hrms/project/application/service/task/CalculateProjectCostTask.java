package com.company.hrms.project.application.service.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.company.hrms.common.application.pipeline.PipelineTask;
import com.company.hrms.project.api.response.GetProjectCostResponse;
import com.company.hrms.project.api.response.GetProjectCostResponse.BudgetInfo;
import com.company.hrms.project.api.response.GetProjectCostResponse.CostSummary;
import com.company.hrms.project.api.response.GetProjectCostResponse.MemberCost;
import com.company.hrms.project.api.response.GetProjectCostResponse.MonthlyCost;
import com.company.hrms.project.application.service.context.ProjectCostContext;
import com.company.hrms.project.domain.model.aggregate.Project;
import com.company.hrms.project.domain.model.aggregate.ProjectMember;

/**
 * 計算專案成本 Task
 */
@Component
public class CalculateProjectCostTask implements PipelineTask<ProjectCostContext> {

        @Override
        public void execute(ProjectCostContext context) throws Exception {
                Project project = context.getProject();

                // 1. 計算預算資訊
                BudgetInfo budgetInfo = BudgetInfo.builder()
                                .budgetType(project.getBudget().getBudgetType())
                                .budgetAmount(project.getBudget().getBudgetAmount())
                                .budgetHours(project.getBudget().getBudgetHours())
                                .build();

                // 2. 計算成本摘要
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

                // 3. 計算成員成本
                List<MemberCost> memberCosts = new ArrayList<>();
                for (ProjectMember member : project.getMembers()) {
                        BigDecimal memberHours = member.getAllocatedHours() != null ? member.getAllocatedHours()
                                        : BigDecimal.ZERO;
                        // TODO: 預設時薪，未來應從 Employee Service 查詢
                        BigDecimal hourlyRate = BigDecimal.valueOf(800);
                        BigDecimal memberCost = memberHours.multiply(hourlyRate);
                        BigDecimal costPercentage = BigDecimal.ZERO;
                        if (totalCost.compareTo(BigDecimal.ZERO) > 0) {
                                costPercentage = memberCost.multiply(BigDecimal.valueOf(100))
                                                .divide(totalCost, 2, RoundingMode.HALF_UP);
                        }

                        memberCosts.add(MemberCost.builder()
                                        .employeeId(member.getEmployeeId().toString())
                                        .employeeName("員工") // TODO: 需要從員工服務取得實際姓名
                                        .role(member.getRole())
                                        .hours(memberHours)
                                        .hourlyRate(hourlyRate)
                                        .cost(memberCost)
                                        .costPercentage(costPercentage)
                                        .build());
                }

                // 4. 模擬月份成本 (目前無資料，留空)
                // TODO: 需實作按月彙總邏輯
                List<MonthlyCost> monthlyCosts = new ArrayList<>();

                // 5. Build Response
                GetProjectCostResponse response = GetProjectCostResponse.builder()
                                .projectId(project.getId().getValue().toString())
                                .projectCode(project.getProjectCode())
                                .projectName(project.getProjectName())
                                .budget(budgetInfo)
                                .summary(summary)
                                .byMember(memberCosts)
                                .byMonth(monthlyCosts)
                                .build();

                context.setResponse(response);
        }

        @Override
        public String getName() {
                return "計算專案成本";
        }
}
