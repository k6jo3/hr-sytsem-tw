package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.TerminateEmployeeRequest;
import com.company.hrms.organization.api.response.employee.TerminateEmployeeResponse;
import com.company.hrms.organization.domain.event.EmployeeTerminatedEvent;
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

/**
 * 員工離職服務實作
 */
@Service("terminateEmployeeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TerminateEmployeeServiceImpl
        implements CommandApiService<TerminateEmployeeRequest, TerminateEmployeeResponse> {

    private final IEmployeeRepository employeeRepository;
    private final IEmployeeHistoryRepository employeeHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public TerminateEmployeeResponse execCommand(TerminateEmployeeRequest request,
                                                  JWTModel currentUser,
                                                  String... args) throws Exception {
        String employeeId = args[0];
        log.info("Terminating employee: {} on {}", employeeId, request.getTerminationDate());

        // 查詢員工
        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeId));

        // 執行離職
        employee.terminate(request.getTerminationDate(), request.getTerminationReason());

        // 儲存更新
        employeeRepository.save(employee);

        // 記錄人事歷程
        EmployeeHistory history = EmployeeHistory.createTerminationHistory(
                employee.getId(),
                request.getTerminationDate(),
                request.getTerminationReason(),
                request.getRemarks()
        );
        employeeHistoryRepository.save(history);

        // 發布領域事件
        eventPublisher.publishEvent(new EmployeeTerminatedEvent(
                employeeId,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                employee.getDepartmentId().getValue(),
                request.getTerminationDate(),
                request.getTerminationReason()
        ));

        log.info("Employee terminated successfully: {} on {}", employeeId, request.getTerminationDate());

        return TerminateEmployeeResponse.success(
                employeeId,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                request.getTerminationDate()
        );
    }
}
