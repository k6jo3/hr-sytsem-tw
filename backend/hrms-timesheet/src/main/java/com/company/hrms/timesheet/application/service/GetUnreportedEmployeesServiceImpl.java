package com.company.hrms.timesheet.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.timesheet.api.request.GetUnreportedEmployeesRequest;
import com.company.hrms.timesheet.api.response.GetUnreportedEmployeesResponse;
import com.company.hrms.timesheet.domain.model.aggregate.Timesheet;
import com.company.hrms.timesheet.domain.repository.ITimesheetRepository;
import com.company.hrms.timesheet.infrastructure.client.OrganizationServiceClient;
import com.company.hrms.timesheet.infrastructure.client.dto.OrganizationEmployeeListResponse;

import lombok.RequiredArgsConstructor;

@Service("getUnreportedEmployeesServiceImpl")
@RequiredArgsConstructor
public class GetUnreportedEmployeesServiceImpl
                implements QueryApiService<GetUnreportedEmployeesRequest, GetUnreportedEmployeesResponse> {

        private final OrganizationServiceClient organizationServiceClient;
        private final ITimesheetRepository timesheetRepository;

        @Override
        public GetUnreportedEmployeesResponse getResponse(GetUnreportedEmployeesRequest request, JWTModel currentUser,
                        String... args)
                        throws Exception {

                // 1. 取得所有在職員工
                var orgResponse = organizationServiceClient.getEmployeeList("ACTIVE", 1, 1000);
                var body = orgResponse.getBody();
                if (body == null || body.getItems() == null) {
                        return GetUnreportedEmployeesResponse.builder()
                                        .employees(new ArrayList<>())
                                        .totalCount(0)
                                        .build();
                }

                List<OrganizationEmployeeListResponse.OrganizationEmployeeDto> allEmployees = body.getItems();

                // 2. 取得該期間已有提交工時的員工 ID 列表
                // 注意：這裡假設查詢條件與 Timesheet 的週期間隔相符 (例如週一到週日)
                QueryGroup query = QueryBuilder.where()
                                .eq("periodStartDate", request.getStartDate())
                                .eq("periodEndDate", request.getEndDate())
                                .build();

                // 取得所有已建立工時表的員工 ID (包含各個狀態，因為只要建立了就算有回報過，草稿也算，除非業務規定要已送審)
                // 若要更嚴格，可以過濾狀態 != DRAFT
                var reportedEmployeeIds = timesheetRepository.findAll(query, Pageable.unpaged())
                                .getContent().stream()
                                .map(Timesheet::getEmployeeId)
                                .collect(Collectors.toSet());

                // 3. 比對找出未回報員工
                List<GetUnreportedEmployeesResponse.UnreportedEmployee> unreported = allEmployees.stream()
                                .filter(emp -> !reportedEmployeeIds.contains(UUID.fromString(emp.getEmployeeId())))
                                .map(emp -> GetUnreportedEmployeesResponse.UnreportedEmployee.builder()
                                                .employeeId(UUID.fromString(emp.getEmployeeId()))
                                                .employeeName(emp.getFullName())
                                                .department(emp.getDepartmentPath() != null ? emp.getDepartmentPath()
                                                                : emp.getDepartmentId())
                                                .build())
                                .toList();

                return GetUnreportedEmployeesResponse.builder()
                                .employees(unreported)
                                .totalCount(unreported.size())
                                .build();
        }
}
