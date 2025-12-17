package com.company.hrms.organization.application.service.contract;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 取得合約清單服務實作
 */
@Service("getContractListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetContractListServiceImpl
        implements QueryApiService<Void, ContractListResponse> {

    private final IEmployeeContractRepository contractRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public ContractListResponse getResponse(Void request,
                                            JWTModel currentUser,
                                            String... args) throws Exception {
        String employeeId = args[0];
        log.info("Getting contracts for employee: {}", employeeId);

        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeId));

        List<EmployeeContract> contracts = contractRepository.findByEmployeeId(new EmployeeId(employeeId));

        List<ContractDetailResponse> items = contracts.stream()
                .map(contract -> buildContractDetailResponse(contract, employee))
                .collect(Collectors.toList());

        return ContractListResponse.builder()
                .items(items)
                .totalCount(items.size())
                .build();
    }

    private ContractDetailResponse buildContractDetailResponse(EmployeeContract contract, Employee employee) {
        Integer remainingDays = null;
        if (contract.getEndDate() != null && contract.getEndDate().isAfter(LocalDate.now())) {
            remainingDays = (int) ChronoUnit.DAYS.between(LocalDate.now(), contract.getEndDate());
        }

        return ContractDetailResponse.builder()
                .contractId(contract.getId().getValue())
                .employeeId(employee.getId().getValue())
                .employeeNumber(employee.getEmployeeNumber())
                .employeeName(employee.getFullName())
                .contractType(contract.getContractType().name())
                .contractTypeDisplay(contract.getContractType().getDisplayName())
                .startDate(contract.getStartDate())
                .endDate(contract.getEndDate())
                .status(contract.getStatus().name())
                .statusDisplay(contract.getStatus().getDisplayName())
                .probationMonths(contract.getProbationMonths())
                .renewalCount(contract.getRenewalCount())
                .notes(contract.getNotes())
                .remainingDays(remainingDays)
                .build();
    }
}
