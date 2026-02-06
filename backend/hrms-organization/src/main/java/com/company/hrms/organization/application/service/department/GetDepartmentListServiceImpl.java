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
                QueryGroup query = QueryGroup.and();
                query.eq("is_deleted", false);

                if (request.getCode() != null && !request.getCode().isEmpty()) {
                        query.eq("code", request.getCode());
                }
                if (request.getName() != null && !request.getName().isEmpty()) {
                        query.like("name", request.getName());
                }
                if (request.getOrganizationId() != null && !request.getOrganizationId().isEmpty()) {
                        query.eq("organizationId", request.getOrganizationId());
                }
                if (request.getParentId() != null && !request.getParentId().isEmpty()) {
                        query.eq("parentId", request.getParentId());
                }
                if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                        query.eq("status", request.getStatus());
                }

                return query;
        }

        @Override
        protected PageResponse<DepartmentListItemResponse> executeQuery(
                        QueryGroup query,
                        GetDepartmentListRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                int page = request.getPage() > 0 ? request.getPage() - 1 : 0;
                int size = request.getSize() > 0 ? request.getSize() : 20;

                Sort sort = Sort.by(Sort.Direction.ASC, "display_order");
                if (request.getSort() != null && !request.getSort().isEmpty()) {
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
                                .page(request.getPage())
                                .size(request.getSize())
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
                                // Note: employeeCount 設為 0,避免在列表查詢中產生 N+1 查詢問題
                                // 如需員工數量,請使用部門詳情 API (getDepartmentDetail)
                                .employeeCount(0)
                                .build();
        }
}
