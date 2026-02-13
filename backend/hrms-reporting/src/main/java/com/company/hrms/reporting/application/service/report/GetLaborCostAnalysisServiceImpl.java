package com.company.hrms.reporting.application.service.report;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetLaborCostAnalysisRequest;
import com.company.hrms.reporting.api.response.LaborCostAnalysisResponse;
import com.company.hrms.reporting.infrastructure.readmodel.PayrollSummaryReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.PayrollSummaryReadModelRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢人力成本分析 Service - RPT_QRY_006
 */
@Service("getLaborCostAnalysisServiceImpl")
@RequiredArgsConstructor
public class GetLaborCostAnalysisServiceImpl
        implements QueryApiService<GetLaborCostAnalysisRequest, LaborCostAnalysisResponse> {

    private final PayrollSummaryReadModelRepository repository;

    @Override
    public LaborCostAnalysisResponse getResponse(GetLaborCostAnalysisRequest req, JWTModel currentUser, String... args)
            throws Exception {

        String tenantId = currentUser.getTenantId();
        String yearMonth = req.getYearMonth();

        List<PayrollSummaryReadModel> payrolls = repository.findAll().stream()
                .filter(p -> p.getTenantId().equals(tenantId) && p.getYearMonth().equals(yearMonth))
                .toList();

        BigDecimal totalCost = payrolls.stream()
                .map(p -> p.getGrossPay() != null ? p.getGrossPay() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 通常人力成本還包含雇主負擔的勞健保與勞退，但在 ReadModel 中目前只看到 gross_pay 等
        // 若系統有定義雇主端欄位，應在此加入。目前以 gross_pay 作為基礎。

        LaborCostAnalysisResponse resp = new LaborCostAnalysisResponse();
        resp.setOrganizationId(req.getOrganizationId());
        resp.setYearMonth(yearMonth);
        resp.setTotalCost(totalCost);
        resp.setEmployeeCount(payrolls.size());

        if (!payrolls.isEmpty()) {
            resp.setAverageCost(
                    totalCost.divide(BigDecimal.valueOf(payrolls.size()), 2, java.math.RoundingMode.HALF_UP));
        } else {
            resp.setAverageCost(BigDecimal.ZERO);
        }

        return resp;
    }
}
