package com.company.hrms.organization.application.service.employee;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.model.PageResponse;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.organization.api.request.employee.GetEmployeeListRequest;
import com.company.hrms.organization.api.response.employee.EmployeeListItemResponse;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得員工列表服務實作
 */
@Service("getEmployeeListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEmployeeListServiceImpl
        extends AbstractQueryService<GetEmployeeListRequest, PageResponse<EmployeeListItemResponse>> {

    private final IEmployeeRepository employeeRepository;

    @Override
    protected QueryGroup buildQuery(GetEmployeeListRequest request, JWTModel currentUser) {
        log.info("Building query for employee list: {}", request);
        QueryBuilder builder = QueryBuilder.where()
                .fromDto(request);

        // 如果沒有指定狀態，預設排除離職人員 (符合 HR02 v2 合約規範)
        if (request.getEmploymentStatus() == null || request.getEmploymentStatus().isEmpty()) {
            builder.ne("employment_status", "TERMINATED");
        }

        // 處理 keyword 模糊查詢 (工號或姓名)
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            final String kw = "%" + request.getKeyword() + "%";
            builder.orGroup(sub -> sub
                    .like("employee_number", kw)
                    .like("full_name", kw));
        }

        // 處理權限過濾 (ORG_QRY_E008, ORG_QRY_E009)
        if (currentUser.hasRole("MANAGER")) {
            // 主管只能看到管轄部門的員工
            List<String> managedDeptIds = currentUser.getManagedDepartmentIds();
            if (managedDeptIds != null && !managedDeptIds.isEmpty()) {
                builder.in("department_id", managedDeptIds.toArray());
                if (request.getEmploymentStatus() == null) {
                    builder.eq("employment_status", "ACTIVE");
                }
            }
        } else if (currentUser.hasRole("EMPLOYEE")) {
            // 一般員工只能看到同部門的在職員工
            String deptId = currentUser.getDepartmentId();
            if (deptId != null) {
                builder.eq("department_id", deptId);
                builder.eq("employment_status", "ACTIVE");
            }
        }

        return builder.build();
    }

    @Override
    protected PageResponse<EmployeeListItemResponse> executeQuery(
            QueryGroup query,
            GetEmployeeListRequest request,
            JWTModel currentUser,
            String... args) throws Exception {

        try {
            // 處理分頁
            int page = request.getPage() > 0 ? request.getPage() - 1 : 0;
            int size = request.getSize() > 0 ? request.getSize() : 20;

            // 簡單處理排序 (預設 create_time DESC)
            Sort sort = Sort.by(Sort.Direction.DESC, "create_time");
            if (request.getSort() != null && !request.getSort().isEmpty()) {
                // 實作簡單解析，或使用預設
                // 這裡先簡化，實際專案可能需要 SortParser
            }

            PageRequest pageable = PageRequest.of(page, size, sort);

            // 執行查詢
            List<Employee> employees = employeeRepository.findByQuery(query, pageable);
            long total = employeeRepository.countByQuery(query);

            List<EmployeeListItemResponse> items = employees.stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());

            return PageResponse.<EmployeeListItemResponse>builder()
                    .items(items)
                    .total(total)
                    .page(request.getPage())
                    .size(request.getSize())
                    .totalPages((int) Math.ceil((double) total / size))
                    .build();
        } catch (Exception e) {
            log.error("Error in executeQuery: {}", e.getMessage(), e);
            throw e;
        }
    }

    private EmployeeListItemResponse toResponse(Employee employee) {
        return EmployeeListItemResponse.builder()
                .employeeId(employee.getId().getValue().toString())
                .employeeNumber(employee.getEmployeeNumber())
                .fullName(employee.getFullName())
                .departmentId(employee.getDepartmentId() != null ? employee.getDepartmentId().toString() : null)
                .positionId(null) // Position 聚合根不存在，佔位
                .email(employee.getEmail() != null ? employee.getEmail().getValue() : null)
                .status(employee.getEmploymentStatus().name())
                .statusDisplay(employee.getEmploymentStatus().getDisplayName())
                .hireDate(employee.getHireDate())
                .build();
    }
}
