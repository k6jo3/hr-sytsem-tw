package com.company.hrms.payroll.application.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.payroll.application.dto.request.GetSalaryAdvanceListRequest;
import com.company.hrms.payroll.application.dto.response.SalaryAdvanceResponse;
import com.company.hrms.payroll.application.factory.SalaryAdvanceDtoFactory;
import com.company.hrms.payroll.domain.repository.ISalaryAdvanceRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢預借薪資列表服務
 * 支援 employeeId、status 篩選
 */
@Service("getSalaryAdvancesServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetSalaryAdvancesServiceImpl
        implements QueryApiService<GetSalaryAdvanceListRequest, List<SalaryAdvanceResponse>> {

    private final ISalaryAdvanceRepository repository;

    @Override
    public List<SalaryAdvanceResponse> getResponse(GetSalaryAdvanceListRequest request, JWTModel currentUser,
            String... args) throws Exception {

        QueryBuilder builder = QueryBuilder.where();

        if (request != null) {
            if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
                builder.and("employeeId", Operator.EQ, request.getEmployeeId());
            }
            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                builder.and("status", Operator.EQ, request.getStatus());
            }
        }

        QueryGroup query = builder.build();

        return repository.findByQuery(query).stream()
                .map(SalaryAdvanceDtoFactory::toResponse)
                .collect(Collectors.toList());
    }
}
