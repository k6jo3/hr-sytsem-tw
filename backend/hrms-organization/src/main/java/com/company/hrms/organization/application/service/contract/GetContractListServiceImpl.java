package com.company.hrms.organization.application.service.contract;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.contract.ContractDetailResponse;
import com.company.hrms.organization.api.response.contract.ContractListResponse;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.aggregate.EmployeeContract;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeContractRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢員工合約清單 Service
 */
@Service("getContractListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetContractListServiceImpl implements QueryApiService<Void, ContractListResponse> {

    private final IEmployeeContractRepository contractRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public ContractListResponse getResponse(Void request, JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        String employeeIdStr = args[0];
        EmployeeId employeeId = new EmployeeId(employeeIdStr);

        // 1. 查詢員工是否存在
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("EMPLOYEE_NOT_FOUND", "員工不存在: " + employeeIdStr));

        // 2. 查詢合約清單
        List<EmployeeContract> contracts = contractRepository.findByEmployeeId(employeeId);

        // 3. 轉換為 Response
        List<ContractDetailResponse> detailResponses = contracts.stream()
                .map(contract -> toDetailResponse(contract, employee))
                .collect(Collectors.toList());

        return ContractListResponse.builder()
                .items(detailResponses)
                .totalCount(detailResponses.size())
                .build();
    }

    private ContractDetailResponse toDetailResponse(EmployeeContract contract, Employee employee) {
        return ContractDetailResponse.builder()
                .contractId(contract.getId().getValue().toString())
                .employeeId(employee.getId().getValue().toString())
                .employeeNumber(employee.getEmployeeNumber())
                .employeeName(employee.getFullName())
                .contractType(contract.getContractType().name())
                .contractTypeDisplay(contract.getContractType().getDisplayName())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .status(contract.getStatus().name())
                .statusDisplay(contract.getStatus().getDisplayName())
                .probationMonths(contract.getProbationMonths())
                .renewalCount(contract.getRenewalCount() != null ? contract.getRenewalCount() : 0)
                .notes(contract.getNotes())
                .remainingDays(contract.getRemainingDays() > 0 ? (int) contract.getRemainingDays() : null)
                .build();
    }
}
