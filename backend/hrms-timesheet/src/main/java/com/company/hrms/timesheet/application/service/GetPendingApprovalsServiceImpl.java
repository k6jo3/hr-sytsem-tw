package com.company.hrms.timesheet.application.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.timesheet.api.request.GetPendingApprovalsRequest;
import com.company.hrms.timesheet.api.response.GetPendingApprovalsResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;

import lombok.RequiredArgsConstructor;

@Service("getPendingApprovalsServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPendingApprovalsServiceImpl
                implements QueryApiService<GetPendingApprovalsRequest, GetPendingApprovalsResponse> {

        private final ITimesheetRepository timesheetRepository;
        private final JdbcTemplate jdbcTemplate;

        @Override
        public GetPendingApprovalsResponse getResponse(GetPendingApprovalsRequest request, JWTModel currentUser,
                        String... args)
                        throws Exception {

                UUID approverId = UUID.fromString(currentUser.getUserId());

                // 分頁處理
                PageRequest pageRequest = PageRequest.of(request.getPage() - 1, request.getSize(),
                                Sort.by(Sort.Direction.ASC, "submittedAt"));

                // 使用自定義 Repository 方法
                Page<Timesheet> page = timesheetRepository.findPendingApprovals(approverId, pageRequest);

                // 批量查詢員工名稱
                Map<String, String> employeeNameMap = buildEmployeeNameMap(page.getContent());

                List<GetPendingApprovalsResponse.TimesheetSummaryDto> items = page.getContent().stream()
                                .map(t -> toDto(t, employeeNameMap))
                                .collect(Collectors.toList());

                return GetPendingApprovalsResponse.builder()
                                .items(items)
                                .total(page.getTotalElements())
                                .build();
        }

        private GetPendingApprovalsResponse.TimesheetSummaryDto toDto(Timesheet t, Map<String, String> employeeNameMap) {
                String empId = t.getEmployeeId().toString();
                return GetPendingApprovalsResponse.TimesheetSummaryDto.builder()
                                .timesheetId(t.getId().toString())
                                .employeeId(empId)
                                .employeeName(employeeNameMap.getOrDefault(empId, empId))
                                .periodStartDate(t.getPeriodStartDate().toString())
                                .periodEndDate(t.getPeriodEndDate().toString())
                                .totalHours(t.getTotalHours())
                                .status(t.getStatus())
                                .submittedAt(t.getSubmittedAt() != null ? t.getSubmittedAt().toString() : null)
                                .build();
        }

        /**
         * 從 CQRS ReadModel 查詢員工名稱
         */
        private Map<String, String> buildEmployeeNameMap(List<Timesheet> timesheets) {
                Set<String> employeeIds = timesheets.stream()
                                .map(t -> t.getEmployeeId().toString())
                                .collect(Collectors.toSet());

                if (employeeIds.isEmpty()) {
                        return Map.of();
                }

                Map<String, String> map = new HashMap<>();
                try {
                        jdbcTemplate.query(
                                        "SELECT employee_id, employee_name FROM employee_read_models",
                                        rs -> {
                                                String id = rs.getString("employee_id");
                                                if (employeeIds.contains(id)) {
                                                        map.put(id, rs.getString("employee_name"));
                                                }
                                        });
                } catch (Exception e) {
                        // ReadModel 表可能不存在（非 local profile），降級為不顯示名稱
                }
                return map;
        }
}
