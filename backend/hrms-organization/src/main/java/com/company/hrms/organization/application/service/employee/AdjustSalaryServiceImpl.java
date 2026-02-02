package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.domain.event.EventPublisher;
import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.AdjustSalaryRequest;
import com.company.hrms.organization.domain.event.EmployeeSalaryChangedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 員工調薪 Application Service
 */
@Service("adjustSalaryServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdjustSalaryServiceImpl implements CommandApiService<AdjustSalaryRequest, Void> {

    private final IEmployeeRepository employeeRepository;
    private final EventPublisher eventPublisher;

    @Override
    public Void execCommand(AdjustSalaryRequest request, JWTModel currentUser, String... args) throws Exception {
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        String employeeIdStr = args[0];
        EmployeeId employeeId = new EmployeeId(employeeIdStr);

        // 1. 查詢員工
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new DomainException("EMPLOYEE_NOT_FOUND", "員工不存在: " + employeeIdStr));

        // 2. 驗證員工狀態
        if (employee.isTerminated()) {
            throw new DomainException("EMPLOYEE_TERMINATED", "已離職員工無法調薪");
        }

        // 3. 處理調薪邏輯
        // 因為 Employee 聚合根目前不持有薪資資訊 (薪資在 Payroll 服務管理)，
        // 這裡主要的責任是發布領域事件通知 Payroll 服務進行薪資調整。
        // 不過我們可以在 Event 中包含相關資訊。

        log.info("Adjusting salary for employee: {}, new salary: {}, effective: {}",
                employeeIdStr, request.getNewSalary(), request.getEffectiveDate());

        // 4. 發布領域事件
        // 通知 Payroll 服務進行薪資調整，事件中包含新的薪資金額。

        EmployeeSalaryChangedEvent event = new EmployeeSalaryChangedEvent(
                employee.getId().getValue(),
                employee.getEmployeeNumber(),
                employee.getFullName(),
                request.getEffectiveDate(),
                request.getReason(),
                request.getNewSalary());
        eventPublisher.publish(event);

        return null; // Void response
    }
}
