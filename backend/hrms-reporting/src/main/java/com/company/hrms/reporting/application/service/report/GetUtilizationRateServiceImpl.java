package com.company.hrms.reporting.application.service.report;

import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetUtilizationRateRequest;
import com.company.hrms.reporting.api.response.UtilizationRateResponse;
import com.company.hrms.reporting.infrastructure.readmodel.repository.ProjectCostAnalysisReadModelRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢稼動率分析 Service - RPT_QRY_005
 */
@Service("getUtilizationRateServiceImpl")
@RequiredArgsConstructor

public class GetUtilizationRateServiceImpl
        implements QueryApiService<GetUtilizationRateRequest, UtilizationRateResponse> {

    private final ProjectCostAnalysisReadModelRepository repository;

    @Override
    public UtilizationRateResponse getResponse(GetUtilizationRateRequest req, JWTModel currentUser, String... args)
            throws Exception {

        String tenantId = currentUser.getTenantId();
        String projectId = req.getProjectId();

        return repository.findById(projectId)
                .filter(p -> p.getTenantId().equals(tenantId))
                .map(p -> {
                    UtilizationRateResponse resp = new UtilizationRateResponse();
                    resp.setProjectId(p.getProjectId());
                    resp.setProjectName(p.getProjectName());
                    resp.setYearMonth(req.getYearMonth());
                    resp.setUtilizationRate(p.getUtilizationRate());
                    resp.setTotalHours(p.getTotalHours() != null ? p.getTotalHours().intValue() : 0);
                    // 計算計費工時: 總工時 * 稼動率 / 100
                    if (p.getTotalHours() != null && p.getUtilizationRate() != null) {
                        double billable = p.getTotalHours() * p.getUtilizationRate() / 100.0;
                        resp.setBillableHours((int) Math.round(billable));
                    } else {
                        resp.setBillableHours(0);
                    }
                    return resp;
                })
                .orElseThrow(() -> new RuntimeException("Project not found: " + projectId));
    }
}
