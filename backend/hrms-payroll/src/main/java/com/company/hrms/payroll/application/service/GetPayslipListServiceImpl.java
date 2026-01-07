package com.company.hrms.payroll.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetPayslipListRequest;
import com.company.hrms.payroll.application.dto.response.PayslipResponse;
import com.company.hrms.payroll.application.factory.PayslipDtoFactory;
import com.company.hrms.payroll.domain.model.aggregate.Payslip;
import com.company.hrms.payroll.domain.repository.IPayslipRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢薪資單列表服務
 * 
 * <p>
 * <b>Business Pipeline 說明：</b>
 * </p>
 * <p>
 * 依據 {@code 03_Business_Pipeline.md} 架構文件第 1.2 節的設計原則，
 * Business Pipeline 模式適用於「複雜的多步驟業務流程編排」。
 * </p>
 * 
 * <p>
 * 本服務為<b>簡單查詢服務</b>，僅執行：
 * </p>
 * <ol>
 * <li>建立查詢條件 (QueryGroup)</li>
 * <li>呼叫 Repository 取得分頁資料</li>
 * <li>轉換為 Response DTO</li>
 * </ol>
 * 
 * <p>
 * 此流程單純、無副作用、無跨服務協調需求，因此<b>不需使用 Business Pipeline 模式</b>，
 * 直接使用 {@link com.company.hrms.common.application.service.AbstractQueryService}
 * 提供的 {@code buildQuery/executeQuery} 樣板方法即可。
 * </p>
 */
@Service("getPayslipListServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPayslipListServiceImpl
        extends AbstractQueryService<GetPayslipListRequest, PageResponse<PayslipResponse>> {

    private final IPayslipRepository repository;

    @Override
    protected QueryGroup buildQuery(GetPayslipListRequest request, JWTModel currentUser) {
        // 純宣告式查詢：自動解析 Request 上的 @QueryFilter 註解
        return QueryBuilder.where()
                .fromDto(request) // 自動處理 runId, employeeId 等所有 @QueryFilter 欄位
                .build();
    }

    @Override
    protected PageResponse<PayslipResponse> executeQuery(QueryGroup query, GetPayslipListRequest request,
            JWTModel currentUser, String... args) throws Exception {
        int pageIdx = request.getPage() > 0 ? request.getPage() - 1 : 0;
        PageRequest pageable = PageRequest.of(pageIdx, request.getSize());

        Page<Payslip> page = repository.findAll(query, pageable);

        List<PayslipResponse> items = page.getContent().stream()
                .map(PayslipDtoFactory::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(items, request.getPage(), request.getSize(), page.getTotalElements());
    }
}
