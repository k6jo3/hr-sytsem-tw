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
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.payroll.application.dto.request.GetPayrollRunListRequest;
import com.company.hrms.payroll.application.dto.response.PayrollRunResponse;
import com.company.hrms.payroll.application.factory.PayrollRunDtoFactory;
import com.company.hrms.payroll.domain.model.aggregate.PayrollRun;
import com.company.hrms.payroll.domain.repository.IPayrollRunRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢薪資批次列表服務
 * 
 * <p>
 * <b>Business Pipeline 說明：</b>
 * </p>
 * <p>
 * 依據 {@code 03_Business_Pipeline.md} 架構文件第 1.2 節的設計原則，
 * Business Pipeline 模式適用於「複雜的多步驟業務流程編排」，例如：
 * </p>
 * <ul>
 * <li>需載入多個相依資料來源</li>
 * <li>需執行複雜的業務計算邏輯</li>
 * <li>需協調多個 Domain Service</li>
 * <li>需發布領域事件</li>
 * </ul>
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
 * 直接使用 {@link AbstractQueryService}
 * 提供的 {@code buildQuery/executeQuery} 樣板方法即可。
 * </p>
 */
@Service("getPayrollRunListServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetPayrollRunListServiceImpl
        extends AbstractQueryService<GetPayrollRunListRequest, PageResponse<PayrollRunResponse>> {

    private final IPayrollRunRepository repository;

    @Override
    protected QueryGroup buildQuery(GetPayrollRunListRequest request, JWTModel currentUser) {
        QueryBuilder builder = QueryBuilder.where();

        if (request.getOrganizationId() != null) {
            builder.and("organizationId", Operator.EQ, request.getOrganizationId());
        }

        if (request.getStartDate() != null) {
            builder.and("periodStartDate", Operator.GTE, request.getStartDate());
        }
        if (request.getEndDate() != null) {
            builder.and("periodEndDate", Operator.LTE, request.getEndDate());
        }
        if (request.getStatus() != null) {
            builder.and("status", Operator.EQ, request.getStatus());
        }

        return builder.build();
    }

    @Override
    protected PageResponse<PayrollRunResponse> executeQuery(QueryGroup query, GetPayrollRunListRequest request,
            JWTModel currentUser, String... args) throws Exception {
        int pageIdx = request.getPage() > 0 ? request.getPage() - 1 : 0;
        PageRequest pageable = PageRequest.of(pageIdx, request.getSize());

        Page<PayrollRun> page = repository.findAll(query, pageable);

        List<PayrollRunResponse> items = page.getContent().stream()
                .map(PayrollRunDtoFactory::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(items, request.getPage(), request.getSize(), page.getTotalElements());
    }
}
