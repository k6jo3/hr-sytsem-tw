package com.company.hrms.reporting.application.service.report;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.reporting.api.request.GetAttendanceStatisticsRequest;
import com.company.hrms.reporting.api.response.AttendanceStatisticsResponse;
import com.company.hrms.reporting.api.response.AttendanceStatisticsResponse.AttendanceStatItem;
import com.company.hrms.reporting.infrastructure.readmodel.AttendanceStatisticsReadModel;
import com.company.hrms.reporting.infrastructure.readmodel.repository.AttendanceStatisticsReadModelRepository;

import lombok.RequiredArgsConstructor;

/**
 * 差勤統計報表 Service
 * 
 * @author SA Team
 * @since 2026-01-29
 */
@Service("getAttendanceStatisticsServiceImpl")
@RequiredArgsConstructor
public class GetAttendanceStatisticsServiceImpl
                implements QueryApiService<GetAttendanceStatisticsRequest, AttendanceStatisticsResponse> {

        private final AttendanceStatisticsReadModelRepository repository;

        @Override
        public AttendanceStatisticsResponse getResponse(
                        GetAttendanceStatisticsRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                request.setTenantId(currentUser.getTenantId());

                QueryGroup query = QueryBuilder.where()
                                .fromDto(request)
                                .build();

                Pageable pageable = PageRequest.of(request.getPage(), request.getSize());

                Page<AttendanceStatisticsReadModel> page = repository.findPage(query, pageable);

                List<AttendanceStatItem> items = page.getContent().stream()
                                .map(this::toDto)
                                .toList();

                return AttendanceStatisticsResponse.builder()
                                .content(items)
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .build();
        }

        private AttendanceStatItem toDto(AttendanceStatisticsReadModel model) {
                return AttendanceStatItem.builder()
                                .employeeId(model.getEmployeeId())
                                .employeeName(model.getEmployeeName())
                                .departmentName(model.getDepartmentName())
                                .expectedDays(model.getExpectedDays())
                                .actualDays(model.getActualDays())
                                .lateCount(model.getLateCount())
                                .earlyLeaveCount(model.getEarlyLeaveCount())
                                .absentCount(model.getAbsentCount())
                                .leaveDays(model.getLeaveDays())
                                .overtimeHours(model.getOvertimeHours())
                                .attendanceRate(model.getAttendanceRate())
                                .build();
        }
}
