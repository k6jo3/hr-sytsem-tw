package com.company.hrms.payroll.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.payroll.application.dto.request.GetLegalDeductionListRequest;
import com.company.hrms.payroll.application.dto.response.LegalDeductionResponse;
import com.company.hrms.payroll.application.factory.LegalDeductionDtoFactory;
import com.company.hrms.payroll.domain.model.aggregate.LegalDeduction;
import com.company.hrms.payroll.domain.repository.ILegalDeductionRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢法扣款列表服務
 */
@Service("getLegalDeductionListServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetLegalDeductionListServiceImpl
        extends AbstractQueryService<GetLegalDeductionListRequest, PageResponse<LegalDeductionResponse>> {

    private final ILegalDeductionRepository repository;

    @Override
    protected QueryGroup buildQuery(GetLegalDeductionListRequest request, JWTModel currentUser) {
        QueryBuilder builder = QueryBuilder.where();

        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            builder.and("employeeId", Operator.EQ, request.getEmployeeId());
        }
        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            builder.and("status", Operator.EQ, request.getStatus());
        }
        if (request.getGarnishmentType() != null && !request.getGarnishmentType().isBlank()) {
            builder.and("garnishmentType", Operator.EQ, request.getGarnishmentType());
        }

        return builder.build();
    }

    @Override
    protected PageResponse<LegalDeductionResponse> executeQuery(QueryGroup query,
            GetLegalDeductionListRequest request, JWTModel currentUser, String... args) throws Exception {

        List<LegalDeduction> allResults = repository.findByQuery(query);

        // 手動分頁（Repository 回傳全量資料）
        int page = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 20;
        int fromIndex = Math.min((page - 1) * size, allResults.size());
        int toIndex = Math.min(fromIndex + size, allResults.size());

        List<LegalDeductionResponse> items = allResults.subList(fromIndex, toIndex).stream()
                .map(LegalDeductionDtoFactory::toResponse)
                .collect(Collectors.toList());

        return PageResponse.of(items, page, size, allResults.size());
    }
}
