package com.company.hrms.organization.application.service.contract;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.contract.CreateContractRequest;
import com.company.hrms.organization.api.response.contract.ContractDetailResponse;
import com.company.hrms.organization.domain.event.ContractCreatedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.aggregate.EmployeeContract;
import com.company.hrms.organization.domain.model.valueobject.ContractType;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeContractRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 建立合約 Service
 */
@Service("createContractServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CreateContractServiceImpl implements CommandApiService<CreateContractRequest, ContractDetailResponse> {

    private final IEmployeeContractRepository contractRepository;
    private final IEmployeeRepository employeeRepository;
    private final EventPublisher eventPublisher;

    @Override
    public ContractDetailResponse execCommand(CreateContractRequest request, JWTModel currentUser, String... args)
            throws Exception {
        // 1. 驗證參數
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        String employeeIdStr = args[0];
        EmployeeId employeeId = new EmployeeId(employeeIdStr);

        // 2. 查詢員工是否存在
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("EMPLOYEE_NOT_FOUND", "員工不存在: " + employeeIdStr));

        // 3. 解析合約類型
        ContractType type;
        try {
            type = ContractType.valueOf(request.getContractType());
        } catch (IllegalArgumentException e) {
            throw new DomainException("INVALID_CONTRACT_TYPE", "無效的合約類型: " + request.getContractType());
        }

        // 4. 建立合約
        EmployeeContract contract;
        if (type == ContractType.INDEFINITE) {
            contract = EmployeeContract.createIndefinite(
                    employeeId,
                    request.getStartDate(),
                    request.getProbationMonths());
        } else {
            contract = EmployeeContract.createFixedTerm(
                    employeeId,
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getProbationMonths());
        }

        // 設定備註
        if (request.getNotes() != null) {
            contract.updateNotes(request.getNotes());
        }

        // 5. 儲存合約
        contractRepository.save(contract);
        log.info("Contract created: {}, type: {}, employee: {}", contract.getId(), type, employeeId);

        // 6. 發布領域事件
        eventPublisher.publish(new ContractCreatedEvent(
                contract.getId().getValue().toString(),
                employeeId.getValue().toString(),
                type.name(),
                contract.getStartDate(),
                contract.getEndDate()));

        // 7. 回傳回應
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
