package com.company.hrms.attendance.application.service.checkin.query;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.company.hrms.attendance.api.request.attendance.GetCorrectionListRequest;
import com.company.hrms.attendance.api.response.checkin.CorrectionListResponse;
import com.company.hrms.attendance.domain.repository.ICorrectionRepository;
import com.company.hrms.common.api.response.PageResponse;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;

/**
 * 查詢補卡申請列表服務
 */
@Service("getCorrectionApplicationsServiceImpl")
@RequiredArgsConstructor
public class GetCorrectionApplicationsServiceImpl
        implements QueryApiService<GetCorrectionListRequest, PageResponse<CorrectionListResponse>> {

    private final ICorrectionRepository correctionRepository;
    private final com.company.hrms.attendance.infrastructure.client.organization.OrganizationServiceClient organizationServiceClient;

    @Override
    public PageResponse<CorrectionListResponse> getResponse(GetCorrectionListRequest request, JWTModel currentUser,
            String... args) throws Exception {
        QueryBuilder builder = QueryBuilder.where();

        if (request.getEmployeeId() != null && !request.getEmployeeId().isBlank()) {
            builder.and("employeeId", Operator.EQ, request.getEmployeeId());
        }

        if (request.getStatus() != null && !request.getStatus().isBlank()) {
            builder.and("status", Operator.EQ, request.getStatus());
        }

        if (request.getStartDate() != null) {
            builder.and("correctionDate", Operator.GTE, request.getStartDate());
        }

        if (request.getEndDate() != null) {
            builder.and("correctionDate", Operator.LTE, request.getEndDate());
        }

        QueryGroup query = builder.build();

        var allApplications = correctionRepository.findByQuery(query);
        long total = allApplications.size();

        int page = request.getPage() != null ? request.getPage() : 1;
        int size = request.getSize() != null ? request.getSize() : 20;

        var pagedApps = allApplications.stream()
                .skip((long) (page - 1) * size)
                .limit(size)
                .collect(Collectors.toList());

        java.util.Map<String, String> employeeNameMap = new java.util.HashMap<>();
        java.util.Set<String> employeeIds = pagedApps.stream()
                .map(app -> app.getEmployeeId())
                .collect(Collectors.toSet());

        for (String empId : employeeIds) {
            if (empId == null)
                continue;
            try {
                com.company.hrms.attendance.infrastructure.client.organization.dto.EmployeeDetailDto detail = organizationServiceClient
                        .getEmployeeDetail(empId);
                if (detail != null) {
                    employeeNameMap.put(empId, detail.getFullName());
                }
            } catch (Exception e) {
                // Ignore error, name will be null or default
            }
        }

        List<CorrectionListResponse> items = pagedApps.stream()
                .map(app -> CorrectionListResponse.builder()
                        .correctionId(app.getId().getValue())
                        .employeeId(app.getEmployeeId())
                        .employeeName(employeeNameMap.getOrDefault(app.getEmployeeId(), "Unknown"))
                        .correctionDate(app.getCorrectionDate())
                        .correctionType(app.getCorrectionType().name())
                        .status(app.getStatus().name())
                        .build())
                .collect(Collectors.toList());

        return PageResponse.of(items, page, size, total);
    }
}
