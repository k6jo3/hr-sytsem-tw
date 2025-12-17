package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.domain.model.entity.EmployeeHistory;
import com.company.hrms.organization.domain.model.valueobject.EmployeeId;
import com.company.hrms.organization.domain.repository.IEmployeeHistoryRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 取得員工人事歷程服務實作
 */
@Service("getEmployeeHistoryServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEmployeeHistoryServiceImpl
        implements QueryApiService<Void, GetEmployeeHistoryServiceImpl.EmployeeHistoryListResponse> {

    private final IEmployeeHistoryRepository employeeHistoryRepository;

    @Override
    public EmployeeHistoryListResponse getResponse(Void request,
                                                   JWTModel currentUser,
                                                   String... args) throws Exception {
        String employeeId = args[0];
        log.info("Getting employee history: {}", employeeId);

        List<EmployeeHistory> histories = employeeHistoryRepository
                .findByEmployeeId(new EmployeeId(employeeId));

        List<EmployeeHistoryItemResponse> items = histories.stream()
                .map(this::toHistoryItemResponse)
                .collect(Collectors.toList());

        return EmployeeHistoryListResponse.builder()
                .employeeId(employeeId)
                .items(items)
                .totalCount(items.size())
                .build();
    }

    private EmployeeHistoryItemResponse toHistoryItemResponse(EmployeeHistory history) {
        return EmployeeHistoryItemResponse.builder()
                .historyId(history.getId().getValue())
                .eventType(history.getEventType().name())
                .eventTypeDisplay(history.getEventType().getDisplayName())
                .eventDate(history.getEventDate())
                .description(history.getDescription())
                .remarks(history.getRemarks())
                .createdAt(history.getCreatedAt())
                .build();
    }

    @Data
    @Builder
    public static class EmployeeHistoryListResponse {
        private String employeeId;
        private List<EmployeeHistoryItemResponse> items;
        private int totalCount;
    }

    @Data
    @Builder
    public static class EmployeeHistoryItemResponse {
        private String historyId;
        private String eventType;
        private String eventTypeDisplay;
        private LocalDate eventDate;
        private String description;
        private String remarks;
        private java.time.LocalDateTime createdAt;
    }
}
