package com.company.hrms.organization.application.service.contract;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.contract.CreateContractRequest;
import com.company.hrms.organization.api.response.contract.ContractDetailResponse;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.aggregate.EmployeeContract;
import com.company.hrms.organization.domain.model.valueobject.ContractType;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeContractRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * 新增合約服務實作
 */
@Service("createContractServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateContractServiceImpl
        implements CommandApiService<CreateContractRequest, ContractDetailResponse> {

    private final IEmployeeContractRepository contractRepository;
    private final IEmployeeRepository employeeRepository;

    @Override
    public ContractDetailResponse execCommand(CreateContractRequest request,
                                              JWTModel currentUser,
                                              String... args) throws Exception {
        String employeeId = args[0];
        log.info("Creating contract for employee: {}", employeeId);

        // 驗證員工存在
        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeId));

        // 驗證定期合約必須有結束日期
        ContractType contractType = ContractType.valueOf(request.getContractType());
        if (contractType == ContractType.FIXED_TERM && request.getEndDate() == null) {
            throw new IllegalArgumentException("定期合約必須指定結束日期");
        }

        // 建立合約
        EmployeeContract contract = EmployeeContract.create(
                new EmployeeId(employeeId),
                contractType,
                request.getStartDate(),
                request.getEndDate(),
                request.getProbationMonths() != null ? request.getProbationMonths() : 0,
                request.getNotes()
        );

        // 儲存合約
        contractRepository.save(contract);

        log.info("Contract created successfully: {}", contract.getId().getValue());

        return buildContractDetailResponse(contract, employee);
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
