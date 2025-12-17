package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.domain.event.EmployeeSalaryChangedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 員工調薪服務實作
 * 注意：實際薪資管理由薪酬服務(Payroll Service)處理
 * 此服務僅發布事件通知薪酬服務進行調薪
 */
@Service("adjustSalaryServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdjustSalaryServiceImpl
        implements CommandApiService<Void, Void> {

    private final IEmployeeRepository employeeRepository;
    private final IEmployeeHistoryRepository employeeHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Void execCommand(Void request,
                            JWTModel currentUser,
                            String... args) throws Exception {
        String employeeId = args[0];
        log.info("Adjusting salary for employee: {}", employeeId);

        // 查詢員工
        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeId));

        // 記錄人事歷程
        EmployeeHistory history = EmployeeHistory.createSalaryAdjustmentHistory(
                employee.getId(),
                LocalDate.now(),
                "調薪申請"
        );
        employeeHistoryRepository.save(history);

        // 發布調薪事件 (由 Payroll Service 處理實際薪資變更)
        eventPublisher.publishEvent(new EmployeeSalaryChangedEvent(
                employeeId,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                LocalDate.now()
        ));

        log.info("Salary adjustment event published for employee: {}", employeeId);

        return null;
    }
}
