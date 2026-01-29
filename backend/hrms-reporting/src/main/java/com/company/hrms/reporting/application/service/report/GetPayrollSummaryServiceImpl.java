package com.company.hrms.reporting.application.service.report;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetPayrollSummaryRequest;
import com.company.hrms.reporting.api.response.PayrollSummaryResponse;
import com.company.hrms.reporting.api.response.PayrollSummaryResponse.PayrollSummaryItem;
import com.company.hrms.reporting.infrastructure.readmodel.PayrollSummaryReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.PayrollSummaryReadModelRepository;

import lombok.RequiredArgsConstructor;

/**
 * 薪資匯總報表 Service
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("getPayrollSummaryServiceImpl")
@RequiredArgsConstructor
public class GetPayrollSummaryServiceImpl
                implements QueryApiService<GetPayrollSummaryRequest, PayrollSummaryResponse> {

        private final PayrollSummaryReadModelRepository repository;

        @Override
        public PayrollSummaryResponse getResponse(
                        GetPayrollSummaryRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                request.setTenantId(currentUser.getTenantId());

                QueryGroup query = QueryBuilder.where()
                                .fromDto(request)
                                .build();

                Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

                Page<PayrollSummaryReadModel> page = repository.findPage(query, pageable);

                List<PayrollSummaryItem> items = page.getContent().stream()
                                .map(this::toDto)
                                .toList();

                return PayrollSummaryResponse.builder()
                                .content(items)
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .build();
        }

        private PayrollSummaryItem toDto(PayrollSummaryReadModel model) {
                return PayrollSummaryItem.builder()
                                .employeeId(model.getEmployeeId())
                                .employeeName(model.getEmployeeName())
                                .departmentName(model.getDepartmentName())
                                .baseSalary(model.getBaseSalary())
                                .overtimePay(model.getOvertimePay())
                                .allowances(model.getAllowances())
                                .bonus(model.getBonus())
                                .grossPay(model.getGrossPay())
                                .laborInsurance(model.getLaborInsurance())
                                .healthInsurance(model.getHealthInsurance())
                                .incomeTax(model.getIncomeTax())
                                .otherDeductions(model.getOtherDeductions())
                                .netPay(model.getNetPay())
                                .build();
        }
}
