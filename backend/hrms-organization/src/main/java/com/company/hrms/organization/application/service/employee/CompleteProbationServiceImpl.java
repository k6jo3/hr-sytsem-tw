package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.domain.event.EmployeeProbationPassedEvent;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.model.valueobject.EmploymentStatus;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 試用期轉正服務實作
 */
@Service("completeProbationServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CompleteProbationServiceImpl
        implements CommandApiService<Void, Void> {

    private final IEmployeeRepository employeeRepository;
    private final IEmployeeHistoryRepository employeeHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Void execCommand(Void request,
                            JWTModel currentUser,
                            String... args) throws Exception {
        String employeeId = args[0];
        log.info("Completing probation for employee: {}", employeeId);

        // 查詢員工
        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeId));

        // 驗證員工在試用期
        if (employee.getEmploymentStatus() != EmploymentStatus.PROBATION) {
            throw new IllegalStateException("員工不在試用期: " + employeeId);
        }

        // 執行轉正
        employee.completeProbation(LocalDate.now());

        // 儲存更新
        employeeRepository.save(employee);

        // 記錄人事歷程
        EmployeeHistory history = EmployeeHistory.createProbationCompletedHistory(
                employee.getId(),
                LocalDate.now()
        );
        employeeHistoryRepository.save(history);

        // 發布領域事件
        eventPublisher.publishEvent(new EmployeeProbationPassedEvent(
                employeeId,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                LocalDate.now()
        ));

        log.info("Probation completed successfully for employee: {}", employeeId);

        return null;
    }
}
