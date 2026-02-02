package com.company.hrms.organization.application.service.employee;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.response.employee.EmployeeHistoryResponse;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得員工人事歷程列表服務實作
 */
@Service("getEmployeeHistoryServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEmployeeHistoryServiceImpl implements QueryApiService<Object, List<EmployeeHistoryResponse>> {

    private final IEmployeeRepository employeeRepository;
    private final IEmployeeHistoryRepository employeeHistoryRepository;

    @Override
    public List<EmployeeHistoryResponse> getResponse(Object request, JWTModel currentUser, String... args)
            throws Exception {
        if (args == null || args.length == 0 || args[0] == null) {
            throw new IllegalArgumentException("Employee ID is required");
        }
        String employeeIdStr = args[0];
        EmployeeId employeeId = new EmployeeId(employeeIdStr);

        // 1. 驗證員工存在
        if (!employeeRepository.existsById(employeeId)) {
            throw new DomainException("EMPLOYEE_NOT_FOUND", "員工不存在: " + employeeIdStr);
        }

        // 2. 查詢人事歷程列表
        List<EmployeeHistory> historyList = employeeHistoryRepository.findByEmployeeId(UUID.fromString(employeeIdStr));

        if (historyList == null) {
            return Collections.emptyList();
        }

        // 3. 轉換為回應 DTO
        return historyList.stream()
                .map(history -> EmployeeHistoryResponse.builder()
                        .id(history.getId().getValue().toString())
                        .employeeId(history.getEmployeeId().toString())
                        .eventType(history.getEventType() != null ? history.getEventType().name() : "")
                        .eventTypeDisplayName(history.getEventTypeDisplayName())
                        .effectiveDate(history.getEffectiveDate())
                        .reason(history.getReason())
                        .oldValue(history.getOldValue())
                        .newValue(history.getNewValue())
                        .build())
                .collect(Collectors.toList());
    }
}
