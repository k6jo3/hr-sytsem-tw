package com.company.hrms.performance.application.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.performance.api.request.ExportReportRequest;
import com.company.hrms.performance.api.response.SuccessResponse;
import com.company.hrms.performance.domain.repository.IPerformanceReviewRepository;

import lombok.RequiredArgsConstructor;

// ...

@Service("exportReportServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExportReportServiceImpl
        extends AbstractQueryService<ExportReportRequest, SuccessResponse> {

    private final IPerformanceReviewRepository reviewRepository;

    @Override
    protected QueryGroup buildQuery(ExportReportRequest request, JWTModel currentUser) {
        // 匯出功能暫時不實作 QueryGroup
        return null;
    }

    @Override
    protected SuccessResponse executeQuery(
            QueryGroup query, ExportReportRequest request, JWTModel currentUser, String... args) throws Exception {

        // TODO: 實作報表匯出邏輯
        // 1. 根據 request.getCycleId() 查詢資料
        // 2. 生成 CSV/Excel
        // 3. 上傳到檔案服務
        // 4. 返回下載連結

        return SuccessResponse.of("報表匯出功能開發中");
    }
}
