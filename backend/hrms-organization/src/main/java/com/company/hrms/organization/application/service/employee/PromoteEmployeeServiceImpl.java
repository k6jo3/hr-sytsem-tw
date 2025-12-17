package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.PromoteEmployeeRequest;
import com.company.hrms.organization.api.response.employee.PromoteEmployeeResponse;
import com.company.hrms.organization.domain.event.EmployeePromotedEvent;
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
 * 員工升遷服務實作
 */
@Service("promoteEmployeeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PromoteEmployeeServiceImpl
        implements CommandApiService<PromoteEmployeeRequest, PromoteEmployeeResponse> {

    private final IEmployeeRepository employeeRepository;
    private final IEmployeeHistoryRepository employeeHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public PromoteEmployeeResponse execCommand(PromoteEmployeeRequest request,
                                                JWTModel currentUser,
                                                String... args) throws Exception {
        String employeeId = args[0];
        log.info("Promoting employee: {} to {} ({})",
                employeeId, request.getNewJobTitle(), request.getNewJobLevel());

        // 查詢員工
        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeId));

        // 記錄舊職稱與職級
        String oldJobTitle = employee.getJobTitle();
        String oldJobLevel = employee.getJobLevel();

        // 執行升遷
        employee.promote(
                request.getNewJobTitle(),
                request.getNewJobLevel(),
                request.getEffectiveDate()
        );

        // 儲存更新
        employeeRepository.save(employee);

        // 記錄人事歷程
        EmployeeHistory history = EmployeeHistory.createPromotionHistory(
                employee.getId(),
                oldJobTitle,
                request.getNewJobTitle(),
                oldJobLevel,
                request.getNewJobLevel(),
                request.getEffectiveDate(),
                request.getReason()
        );
        employeeHistoryRepository.save(history);

        // 發布領域事件
        eventPublisher.publishEvent(new EmployeePromotedEvent(
                employeeId,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                oldJobTitle,
                request.getNewJobTitle(),
                oldJobLevel,
                request.getNewJobLevel(),
                request.getEffectiveDate()
        ));

        log.info("Employee promoted successfully: {} from {} to {}",
                employeeId, oldJobTitle, request.getNewJobTitle());

        return PromoteEmployeeResponse.success(
                employeeId,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                oldJobTitle,
                request.getNewJobTitle(),
                oldJobLevel,
                request.getNewJobLevel(),
                request.getEffectiveDate()
        );
    }
}
