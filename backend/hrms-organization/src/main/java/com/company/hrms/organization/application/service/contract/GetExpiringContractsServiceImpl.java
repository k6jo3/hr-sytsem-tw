package com.company.hrms.organization.application.service.contract;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
 * 查詢即將到期合約 Service
 */
@Service("getExpiringContractsServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetExpiringContractsServiceImpl implements QueryApiService<Void, ContractListResponse> {

    private final IEmployeeContractRepository contractRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public ContractListResponse getResponse(Void request, JWTModel currentUser, String... args) throws Exception {
        // 解析參數：天數 (default 30)
        int days = 30;
        if (args != null && args.length > 0 && args[0] != null) {
            try {
                days = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                log.warn("Invalid days parameter: {}, using default 30", args[0]);
            }
        }

        LocalDate now = LocalDate.now();
        LocalDate expiryDate = now.plusDays(days);

        log.info("Searching for contracts expiring between {} and {}", now, expiryDate);

        // 1. 查詢到期合約
        List<EmployeeContract> expiringContracts = contractRepository.findExpiringContracts(now, expiryDate);

        if (expiringContracts.isEmpty()) {
            return ContractListResponse.builder()
                    .items(Collections.emptyList())
                    .totalCount(0)
                    .build();
        }

        // 2. 批量查詢相關員工資訊 (優化：避免 N+1 查詢)
        Set<EmployeeId> employeeIds = expiringContracts.stream()
                .map(EmployeeContract::getEmployeeId)
                .collect(Collectors.toSet());

        Map<EmployeeId, Employee> employeeMap = employeeRepository.findByIdIn(employeeIds).stream()
                .collect(Collectors.toMap(Employee::getId, e -> e));

        // 3. 轉換為 Response
        List<ContractDetailResponse> detailResponses = expiringContracts.stream()
                .map(contract -> toDetailResponse(contract, employeeMap.get(contract.getEmployeeId())))
                .collect(Collectors.toList());

        return ContractListResponse.builder()
                .items(detailResponses)
                .totalCount(detailResponses.size())
                .build();
    }

    private ContractDetailResponse toDetailResponse(EmployeeContract contract, Employee employee) {
        String employeeNumber = employee != null ? employee.getEmployeeNumber() : "UNKNOWN";
        String employeeName = employee != null ? employee.getFullName() : "Unknown Employee";
        String employeeIdStr = employee != null ? employee.getId().getValue().toString() : "";

        return ContractDetailResponse.builder()
                .contractId(contract.getId().getValue().toString())
                .employeeId(employeeIdStr)
                .employeeNumber(employeeNumber)
                .employeeName(employeeName)
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
