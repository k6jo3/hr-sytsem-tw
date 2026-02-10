package com.company.hrms.reporting.application.service.report;

import org.springframework.stereotype.Service;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetLaborCostByDepartmentRequest;
import com.company.hrms.reporting.api.response.LaborCostByDepartmentResponse;

/**
 * 查詢部門人力成本分析 Service - RPT_QRY_012
 */
@Service("getLaborCostByDepartmentServiceImpl")
public class GetLaborCostByDepartmentServiceImpl 
        implements QueryApiService<GetLaborCostByDepartmentRequest, LaborCostByDepartmentResponse> {

    @Override
    public LaborCostByDepartmentResponse getResponse(GetLaborCostByDepartmentRequest req, JWTModel currentUser, String... args)
            throws Exception {
        // TODO: 實作部門人力成本分析查詢邏輯
        // 1. 從 LaborCostViewRepository 按部門分組查詢
        // 2. 計算各部門成本占比
        // 3. 轉換為 LaborCostByDepartmentResponse
        throw new UnsupportedOperationException("待實作：需要 LaborCostViewRepository");
    }
}
