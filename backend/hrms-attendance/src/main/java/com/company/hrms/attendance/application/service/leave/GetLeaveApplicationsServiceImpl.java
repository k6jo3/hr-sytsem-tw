package com.company.hrms.attendance.application.service.leave;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.attendance.api.controller.leave.HR03LeaveQryController;
import com.company.hrms.attendance.api.response.leave.LeaveApplicationListResponse;
import com.company.hrms.attendance.domain.model.aggregate.LeaveApplication;
import com.company.hrms.attendance.domain.model.aggregate.LeaveType;
import com.company.hrms.attendance.domain.repository.ILeaveApplicationRepository;
import com.company.hrms.attendance.domain.repository.ILeaveTypeRepository;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢請假申請列表服務實作
 */
@Service("getLeaveApplicationsServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetLeaveApplicationsServiceImpl implements
        QueryApiService<HR03LeaveQryController.LeaveApplicationQueryRequest, PageResponse<LeaveApplicationListResponse>> {

    private final ILeaveApplicationRepository leaveApplicationRepository;
    private final ILeaveTypeRepository leaveTypeRepository;

    @Override
    public PageResponse<LeaveApplicationListResponse> getResponse(
            HR03LeaveQryController.LeaveApplicationQueryRequest request,
            JWTModel currentUser, String... args) throws Exception {
        log.info("查詢請假申請列表: {}", request);

        QueryBuilder builder = QueryBuilder.where();

        if (request.employeeId() != null) {
            builder.and("employeeId", Operator.EQ, request.employeeId());
        }
        if (request.leaveTypeId() != null) {
            builder.and("leaveTypeId", Operator.EQ, request.leaveTypeId());
        }
        if (request.status() != null) {
            builder.and("status", Operator.EQ, request.status());
        }
        if (request.startDate() != null) {
            builder.and("startDate", Operator.GTE, request.startDate());
        }
        if (request.endDate() != null) {
            builder.and("endDate", Operator.LTE, request.endDate());
        }

        QueryGroup query = builder.build();

        // Handle pagination, default 1 and 20 if not provided
        int page = request.page() != null ? request.page() : 1;
        int size = request.size() != null ? request.size() : 20;
        Pageable pageable = PageRequest.of(page - 1, size); // PageRequest is 0-indexed

        Page<LeaveApplication> resultPage = leaveApplicationRepository.searchPage(query, pageable);

        // 批量查詢假別名稱對照表
        Map<String, String> leaveTypeNameMap = buildLeaveTypeNameMap();

        List<LeaveApplicationListResponse> responseList = resultPage.getContent().stream()
                .map(entity -> toResponse(entity, leaveTypeNameMap))
                .collect(Collectors.toList());

        return PageResponse.of(responseList, page, size, resultPage.getTotalElements());
    }

    private Map<String, String> buildLeaveTypeNameMap() {
        return leaveTypeRepository.findAll().stream()
                .collect(Collectors.toMap(
                        lt -> lt.getId().getValue(),
                        LeaveType::getName,
                        (a, b) -> a));
    }

    private LeaveApplicationListResponse toResponse(LeaveApplication entity, Map<String, String> leaveTypeNameMap) {
        BigDecimal days = BigDecimal.ZERO;
        if (entity.getStartDate() != null && entity.getEndDate() != null) {
            long daysDiff = ChronoUnit.DAYS.between(entity.getStartDate(), entity.getEndDate()) + 1;
            days = BigDecimal.valueOf(daysDiff);
        }

        String leaveTypeId = entity.getLeaveTypeId().getValue();
        String leaveTypeName = leaveTypeNameMap.getOrDefault(leaveTypeId, leaveTypeId);

        return LeaveApplicationListResponse.builder()
                .applicationId(entity.getId().getValue())
                .employeeId(entity.getEmployeeId())
                .leaveTypeCode(leaveTypeId)
                .leaveTypeName(leaveTypeName)
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .leaveDays(days)
                .status(entity.getStatus().name())
                .build();
    }
}
