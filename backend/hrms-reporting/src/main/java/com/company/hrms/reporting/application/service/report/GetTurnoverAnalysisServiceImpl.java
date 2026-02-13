package com.company.hrms.reporting.application.service.report;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetTurnoverAnalysisRequest;
import com.company.hrms.reporting.api.response.TurnoverAnalysisResponse;
import com.company.hrms.reporting.infrastructure.readmodel.EmployeeRosterReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.EmployeeRosterReadModelRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢離職率分析 Service - RPT_QRY_003
 */
@Service("getTurnoverAnalysisServiceImpl")
@RequiredArgsConstructor
@Slf4j
public class GetTurnoverAnalysisServiceImpl
        implements QueryApiService<GetTurnoverAnalysisRequest, TurnoverAnalysisResponse> {

    private final EmployeeRosterReadModelRepository employeeRepository;

    @Override
    public TurnoverAnalysisResponse getResponse(GetTurnoverAnalysisRequest request, JWTModel currentUser,
            String... args)
            throws Exception {

        String tenantId = currentUser.getTenantId();
        String yearMonth = request.getYearMonth(); // "YYYY-MM"
        LocalDate startOfMonth = LocalDate.parse(yearMonth + "-01");
        LocalDate endOfMonth = startOfMonth.plusMonths(1).minusDays(1);

        List<EmployeeRosterReadModel> allEmployees = employeeRepository.findAll().stream()
                .filter(e -> tenantId.equals(e.getTenantId()))
                .toList();

        // 1. Calculate Headcount at Start of Month
        long startHeadcount = allEmployees.stream()
                .filter(e -> e.getHireDate() != null && e.getHireDate().isBefore(startOfMonth))
                .filter(e -> e.getResignationDate() == null
                        || e.getResignationDate().isAfter(startOfMonth.minusDays(1)))
                .count();

        // 2. Calculate Headcount at End of Month
        long endHeadcount = allEmployees.stream()
                .filter(e -> e.getHireDate() != null && !e.getHireDate().isAfter(endOfMonth))
                .filter(e -> e.getResignationDate() == null || e.getResignationDate().isAfter(endOfMonth))
                .count();

        // 3. Calculate Leavers during the month
        long leaversCount = allEmployees.stream()
                .filter(e -> e.getResignationDate() != null)
                .filter(e -> !e.getResignationDate().isBefore(startOfMonth)
                        && !e.getResignationDate().isAfter(endOfMonth))
                .count();

        // 4. Calculate New Hires during the month
        long newHiresCount = allEmployees.stream()
                .filter(e -> e.getHireDate() != null)
                .filter(e -> !e.getHireDate().isBefore(startOfMonth) && !e.getHireDate().isAfter(endOfMonth))
                .count();

        double avgHeadcount = (startHeadcount + endHeadcount) / 2.0;

        double turnoverRate = 0.0;
        if (avgHeadcount > 0) {
            turnoverRate = (double) leaversCount / avgHeadcount * 100.0;
            turnoverRate = Math.round(turnoverRate * 100.0) / 100.0;
        }

        // 使用 Setter 建立 Response (因為 Response Class 沒用 @Builder)
        TurnoverAnalysisResponse response = new TurnoverAnalysisResponse();
        response.setOrganizationId(request.getOrganizationId());
        response.setYearMonth(yearMonth);
        response.setTurnoverRate(turnoverRate);
        response.setTotalEmployees((int) endHeadcount); // 使用月底人數做為總人數
        response.setNewHires((int) newHiresCount);
        response.setTerminations((int) leaversCount);

        return response;
    }
}
