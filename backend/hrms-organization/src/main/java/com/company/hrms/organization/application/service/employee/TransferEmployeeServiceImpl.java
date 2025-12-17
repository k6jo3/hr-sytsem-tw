package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.organization.api.request.employee.TransferEmployeeRequest;
import com.company.hrms.organization.api.response.employee.TransferEmployeeResponse;
import com.company.hrms.organization.domain.event.EmployeeDepartmentChangedEvent;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.model.valueobject.*;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 員工部門調動服務實作
 */
@Service("transferEmployeeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TransferEmployeeServiceImpl
        implements CommandApiService<TransferEmployeeRequest, TransferEmployeeResponse> {

    private final IEmployeeRepository employeeRepository;
    private final IDepartmentRepository departmentRepository;
    private final IEmployeeHistoryRepository employeeHistoryRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public TransferEmployeeResponse execCommand(TransferEmployeeRequest request,
                                                 JWTModel currentUser,
                                                 String... args) throws Exception {
        String employeeId = args[0];
        log.info("Transferring employee: {} to department: {}",
                employeeId, request.getNewDepartmentId());

        // 查詢員工
        Employee employee = employeeRepository.findById(new EmployeeId(employeeId))
                .orElseThrow(() -> new IllegalArgumentException("員工不存在: " + employeeId));

        // 驗證新部門存在
        DepartmentId newDepartmentId = new DepartmentId(request.getNewDepartmentId());
        Department newDepartment = departmentRepository.findById(newDepartmentId)
                .orElseThrow(() -> new IllegalArgumentException("部門不存在: " + request.getNewDepartmentId()));

        // 取得舊部門資訊
        String oldDepartmentId = employee.getDepartmentId().getValue();
        Department oldDepartment = departmentRepository.findById(employee.getDepartmentId())
                .orElse(null);
        String oldDepartmentName = oldDepartment != null ? oldDepartment.getName() : "未知";

        // 執行部門調動
        EmployeeId newSupervisorId = request.getNewSupervisorId() != null
                ? new EmployeeId(request.getNewSupervisorId())
                : null;

        employee.transferDepartment(
                newDepartmentId,
                request.getNewJobTitle(),
                newSupervisorId,
                request.getEffectiveDate()
        );

        // 儲存更新
        employeeRepository.save(employee);

        // 記錄人事歷程
        EmployeeHistory history = EmployeeHistory.createTransferHistory(
                employee.getId(),
                oldDepartmentId,
                request.getNewDepartmentId(),
                request.getNewJobTitle(),
                request.getEffectiveDate(),
                request.getReason()
        );
        employeeHistoryRepository.save(history);

        // 發布領域事件
        eventPublisher.publishEvent(new EmployeeDepartmentChangedEvent(
                employeeId,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                oldDepartmentId,
                oldDepartmentName,
                request.getNewDepartmentId(),
                newDepartment.getName(),
                request.getEffectiveDate()
        ));

        log.info("Employee transferred successfully: {} -> {}", employeeId, request.getNewDepartmentId());

        return TransferEmployeeResponse.success(
                employeeId,
                employee.getEmployeeNumber(),
                employee.getFullName(),
                oldDepartmentName,
                newDepartment.getName(),
                request.getEffectiveDate()
        );
    }
}
