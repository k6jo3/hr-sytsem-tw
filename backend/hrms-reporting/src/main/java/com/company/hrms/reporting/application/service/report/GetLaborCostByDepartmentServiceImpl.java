package com.company.hrms.reporting.application.service.report;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetLaborCostByDepartmentRequest;
import com.company.hrms.reporting.api.response.LaborCostByDepartmentResponse;
import com.company.hrms.reporting.infrastructure.readmodel.PayrollSummaryReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.PayrollSummaryReadModelRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢部門人力成本分析 Service - RPT_QRY_012
 */
@Service("getLaborCostByDepartmentServiceImpl")
@RequiredArgsConstructor
public class GetLaborCostByDepartmentServiceImpl
        implements QueryApiService<GetLaborCostByDepartmentRequest, LaborCostByDepartmentResponse> {

    private final PayrollSummaryReadModelRepository repository;

    @Override
    public LaborCostByDepartmentResponse getResponse(GetLaborCostByDepartmentRequest req, JWTModel currentUser,
            String... args)
            throws Exception {

        String tenantId = currentUser.getTenantId();
        String deptId = req.getDepartmentId();
        String yearMonth = req.getYearMonth();

        List<PayrollSummaryReadModel> payrolls = repository.findAll().stream()
                .filter(p -> p.getTenantId().equals(tenantId)
                        && p.getDepartmentId().equals(deptId)
                        && p.getYearMonth().equals(yearMonth))
                .toList();

        BigDecimal totalCost = payrolls.stream()
                .map(p -> p.getGrossPay() != null ? p.getGrossPay() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LaborCostByDepartmentResponse resp = new LaborCostByDepartmentResponse();
        resp.setDepartmentId(deptId);
        if (!payrolls.isEmpty()) {
            resp.setDepartmentName(payrolls.get(0).getDepartmentName());
        }
        resp.setYearMonth(yearMonth);
        resp.setTotalCost(totalCost);
        resp.setEmployeeCount(payrolls.size());

        return resp;
    }
}
