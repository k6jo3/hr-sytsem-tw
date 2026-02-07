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
 * 取得子部門列表服務實作
 */
@Service("getSubDepartmentsServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetSubDepartmentsServiceImpl
                extends AbstractQueryService<GetDepartmentListRequest, PageResponse<DepartmentListItemResponse>> {

        private final IDepartmentRepository departmentRepository;

        @Override
        protected QueryGroup buildQuery(GetDepartmentListRequest request, JWTModel currentUser) {
                // 子部門查詢通常直接由 PathVariable 傳入 parentId，在 executeQuery 處理
                // 這裡預設返回空或基本過濾
                return QueryBuilder.where()
                                .eq("status", "ACTIVE")
                                .build();
        }

        @Override
        protected PageResponse<DepartmentListItemResponse> executeQuery(
                        QueryGroup query,
                        GetDepartmentListRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                String parentId = (args.length > 0) ? args[0] : null;
                if (parentId == null && request != null) {
                        parentId = request.getParentId();
                }

                log.info("Executing sub-departments query for parentId: {}", parentId);

                // 重新組裝 QueryGroup 以便包含 parentId
                QueryGroup finalQuery = QueryBuilder.where()
                                .eq("parent_department_id", parentId)
                                .eq("status", "ACTIVE")
                                .build();

                int page = request != null && request.getPage() > 0 ? request.getPage() - 1 : 0;
                int size = request != null && request.getSize() > 0 ? request.getSize() : 20;
                PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "display_order"));

                List<Department> subDepts = departmentRepository.findByQuery(finalQuery, pageable);
                long total = departmentRepository.countByQuery(finalQuery);

                List<DepartmentListItemResponse> items = subDepts.stream()
                                .map(this::toResponse)
                                .collect(Collectors.toList());

                return PageResponse.<DepartmentListItemResponse>builder()
                                .items(items)
                                .total(total)
                                .page(page + 1)
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
