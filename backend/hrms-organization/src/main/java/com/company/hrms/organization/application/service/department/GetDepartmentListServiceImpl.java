package com.company.hrms.organization.application.service.department;

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
import com.company.hrms.organization.api.request.department.GetDepartmentListRequest;
import com.company.hrms.organization.api.response.department.DepartmentListItemResponse;
import com.company.hrms.organization.domain.model.aggregate.Department;
import com.company.hrms.organization.domain.repository.IDepartmentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 取得部門列表服務實作
 */
@Service("getDepartmentListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetDepartmentListServiceImpl
                extends AbstractQueryService<GetDepartmentListRequest, PageResponse<DepartmentListItemResponse>> {

        private final IDepartmentRepository departmentRepository;

        @Override
        protected QueryGroup buildQuery(GetDepartmentListRequest request, JWTModel currentUser) {
                log.info("Building query for department list: {}", request);
                QueryBuilder builder = QueryBuilder.where();

                if (request != null) {
                        builder.fromDto(request);

                        // 如果 parentId 為 "null" 字符串，則查詢頂層部門 (parent_department_id IS NULL)
                        if ("null".equalsIgnoreCase(request.getParentId())) {
                                builder.isNull("parent_department_id");
                        }

                        // 如果沒有指定狀態，預設查詢啟用的部門
                        if (request.getStatus() == null || request.getStatus().isEmpty()) {
                                builder.eq("status", "ACTIVE");
                        }

                        // 處理 keyword 模糊查詢 (代碼或名稱)
                        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                                final String kw = "%" + request.getKeyword() + "%";
                                builder.orGroup(sub -> sub
                                                .like("department_code", kw)
                                                .like("department_name", kw));
                        }
                } else {
                        // 如果 Request 為空，預設查詢啟用的部門
                        builder.eq("status", "ACTIVE");
                }

                return builder.build();
        }

        @Override
        protected PageResponse<DepartmentListItemResponse> executeQuery(
                        QueryGroup query,
                        GetDepartmentListRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                int page = request != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
                int size = request != null && request.getSize() > 0 ? request.getSize() : 20;

                Sort sort = Sort.by(Sort.Direction.ASC, "display_order");
                if (request != null && request.getSort() != null && !request.getSort().isEmpty()) {
                        // Placeholder for sort parsing
                }

                PageRequest pageable = PageRequest.of(page, size, sort);

                List<Department> departments = departmentRepository.findByQuery(query, pageable);
                long total = departmentRepository.countByQuery(query);

                List<DepartmentListItemResponse> items = departments.stream()
                                .map(this::toResponse)
                                .collect(Collectors.toList());

                return PageResponse.<DepartmentListItemResponse>builder()
                                .items(items)
                                .total(total)
                                .page(request != null ? request.getPage() : 1)
                                .size(size)
                                .totalPages((int) Math.ceil((double) total / size))
                                .build();
        }

        private DepartmentListItemResponse toResponse(Department department) {
                return DepartmentListItemResponse.builder()
                                .departmentId(department.getId().getValue().toString())
                                .code(department.getCode())
                                .name(department.getName())
                                .level(department.getLevel())
                                .sortOrder(department.getSortOrder())
                                .organizationId(
                                                department.getOrganizationId() != null
                                                                ? department.getOrganizationId().toString()
                                                                : null)
                                .parentId(department.getParentId() != null ? department.getParentId().toString() : null)
                                .managerId(department.getManagerId() != null ? department.getManagerId().toString()
                                                : null)
                                .status(department.getStatus().name())
                                .statusDisplay(department.getStatus().getDisplayName())
                                .employeeCount(0)
                                .build();
        }
}
