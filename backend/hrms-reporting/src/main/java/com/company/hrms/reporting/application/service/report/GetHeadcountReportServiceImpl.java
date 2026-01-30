package com.company.hrms.reporting.application.service.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetHeadcountReportRequest;
import com.company.hrms.reporting.api.response.HeadcountReportResponse;
import com.company.hrms.reporting.api.response.HeadcountReportResponse.HeadcountItem;
import com.company.hrms.reporting.api.response.HeadcountReportResponse.HeadcountSummary;
import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.EmployeeRosterReadModelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 人力盤點報表 Service
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("getHeadcountReportServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class GetHeadcountReportServiceImpl
                implements QueryApiService<GetHeadcountReportRequest, HeadcountReportResponse> {

        private final EmployeeRosterReadModelRepository repository;

        @Override
        public HeadcountReportResponse getResponse(
                        GetHeadcountReportRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {
                // TODO: 不符合business pipeline設計以及clean code
                request.setTenantId(currentUser.getTenantId());
                String dimension = request.getDimension(); // e.g., "DEPARTMENT", "POSITION"

                // Step 1: 查詢該 Tenant 下所有未刪除員工
                // Note: 若資料量大應改為 DB 端 GroupBy 查詢，此處為示範使用記憶體分組
                List<EmployeeRosterReadModel> employees = repository.findAll().stream()
                                .filter(e -> e.getTenantId().equals(request.getTenantId()))
                                .filter(e -> !Boolean.TRUE.equals(e.getIsDeleted()))
                                .collect(Collectors.toList());

                // Step 2: 根據維度分組
                Map<String, List<EmployeeRosterReadModel>> groupedData;
                if ("POSITION".equalsIgnoreCase(dimension)) {
                        groupedData = employees.stream()
                                        .collect(Collectors.groupingBy(
                                                        e -> e.getPositionName() != null ? e.getPositionName()
                                                                        : "Unknown"));
                } else {
                        // Default to DEPARTMENT
                        groupedData = employees.stream()
                                        .collect(Collectors.groupingBy(
                                                        e -> e.getDepartmentName() != null ? e.getDepartmentName()
                                                                        : "Unknown"));
                }

                // Step 3: 計算各組統計數據
                List<HeadcountItem> items = new ArrayList<>();
                for (Map.Entry<String, List<EmployeeRosterReadModel>> entry : groupedData.entrySet()) {
                        String groupName = entry.getKey();
                        List<EmployeeRosterReadModel> groupEmps = entry.getValue();

                        long total = groupEmps.size();
                        long active = groupEmps.stream().filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus())).count();
                        long probation = groupEmps.stream().filter(e -> "PROBATION".equalsIgnoreCase(e.getStatus()))
                                        .count();
                        long leave = groupEmps.stream().filter(e -> "LEAVE".equalsIgnoreCase(e.getStatus())).count();
                        long male = 0; // 目前 ReadModel 無性別欄位，暫為 0
                        long female = 0;

                        double avgServiceYears = groupEmps.stream()
                                        .mapToDouble(e -> e.getServiceYears() != null ? e.getServiceYears() : 0.0)
                                        .average().orElse(0.0);

                        items.add(HeadcountItem.builder()
                                        .dimensionName(groupName)
                                        .totalCount((int) total)
                                        .activeCount((int) active)
                                        .probationCount((int) probation)
                                        .leaveCount((int) leave)
                                        .maleCount((int) male)
                                        .femaleCount((int) female)
                                        .avgServiceYears(Math.round(avgServiceYears * 10.0) / 10.0)
                                        .avgAge(0.0) // 目前 ReadModel 無生日欄位
                                        .build());
                }

                // Step 4: 計算總計
                HeadcountSummary summary = HeadcountSummary.builder()
                                .grandTotal((int) employees.size())
                                .totalActive((int) employees.stream()
                                                .filter(e -> "ACTIVE".equalsIgnoreCase(e.getStatus())).count())
                                .totalProbation((int) employees.stream()
                                                .filter(e -> "PROBATION".equalsIgnoreCase(e.getStatus())).count())
                                .totalLeave((int) employees.stream()
                                                .filter(e -> "LEAVE".equalsIgnoreCase(e.getStatus())).count())
                                .newHires(0) // 需額外邏輯定義"新進"
                                .terminations(0)
                                .turnoverRate(0.0)
                                .build();

                return HeadcountReportResponse.builder()
                                .content(items)
                                .totalElements((long) items.size())
                                .totalPages(1) // 簡易實作暫不分頁
                                .summary(summary)
                                .build();
        }
}
