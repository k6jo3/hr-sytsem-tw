package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.organization.api.request.employee.EmployeeQueryRequest;
import com.company.hrms.organization.api.response.employee.EmployeeListItemResponse;
import com.company.hrms.organization.api.response.employee.EmployeeListResponse;
import com.company.hrms.organization.domain.model.aggregate.Employee;
import com.company.hrms.organization.domain.model.valueobject.EmploymentStatus;
import com.company.hrms.organization.domain.repository.IEmployeeRepository;
import com.company.hrms.organization.domain.repository.IEmployeeRepository.EmployeeQueryCriteria;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 取得員工清單服務實作
 */
@Service("getEmployeeListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetEmployeeListServiceImpl
        implements QueryApiService<EmployeeQueryRequest, EmployeeListResponse> {

    private final IEmployeeRepository employeeRepository;

    @Override
    public EmployeeListResponse getResponse(EmployeeQueryRequest request,
                                            JWTModel currentUser,
                                            String... args) throws Exception {
        log.info("Getting employee list with criteria");

        // 建立查詢條件
        EmployeeQueryCriteria criteria = EmployeeQueryCriteria.builder()
                .keyword(request.getKeyword())
                .departmentId(request.getDepartmentId())
                .employmentStatus(request.getEmploymentStatus() != null
                        ? EmploymentStatus.valueOf(request.getEmploymentStatus())
                        : null)
                .employmentType(request.getEmploymentType())
                .hireDateFrom(request.getHireDateFrom())
                .hireDateTo(request.getHireDateTo())
                .page(request.getPage() != null ? request.getPage() : 1)
                .pageSize(request.getPageSize() != null ? request.getPageSize() : 20)
                .build();

        // 執行查詢
        List<Employee> employees = employeeRepository.findByCriteria(criteria);
        long totalCount = employeeRepository.countByCriteria(criteria);

        // 轉換為回應 DTO
        List<EmployeeListItemResponse> items = employees.stream()
                .map(this::toListItemResponse)
                .collect(Collectors.toList());

        return EmployeeListResponse.builder()
                .items(items)
                .totalCount(totalCount)
                .page(criteria.getPage())
                .pageSize(criteria.getPageSize())
                .totalPages((int) Math.ceil((double) totalCount / criteria.getPageSize()))
                .build();
    }

    private EmployeeListItemResponse toListItemResponse(Employee employee) {
        return EmployeeListItemResponse.builder()
                .employeeId(employee.getId().getValue())
                .employeeNumber(employee.getEmployeeNumber())
                .fullName(employee.getFullName())
                .email(employee.getEmail().getValue())
                .departmentId(employee.getDepartmentId().getValue())
                .jobTitle(employee.getJobTitle())
                .employmentType(employee.getEmploymentType().name())
                .employmentStatus(employee.getEmploymentStatus().name())
                .hireDate(employee.getHireDate())
                .build();
    }
}
