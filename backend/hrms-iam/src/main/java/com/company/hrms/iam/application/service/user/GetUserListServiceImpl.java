package com.company.hrms.iam.application.service.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.service.AbstractQueryService;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.model.PageResponse;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.iam.api.request.user.GetUserListRequest;
import com.company.hrms.iam.api.response.user.UserListResponse;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.repository.IUserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢使用者列表 Application Service
 * 重構為繼承 AbstractQueryService 以支援快照測試
 */
@Service("getUserListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetUserListServiceImpl
                extends AbstractQueryService<GetUserListRequest, PageResponse<UserListResponse>> {

        private final IUserRepository userRepository;

        @Override
        protected QueryGroup buildQuery(GetUserListRequest request, JWTModel currentUser) {
                log.info("Building query for user list: {}", request);
                QueryBuilder builder = QueryBuilder.where();

                // 1. Basic Filters from DTO
                // Removed builder.fromDto(request) because keyword/departmentId/roleId are not
                // in UserPO
                // and would cause property not found errors.

                if (request.getStatus() != null && !request.getStatus().isEmpty()) {
                        builder.eq("status", request.getStatus());
                }

                // 1.5. Security: Soft Delete Filter (軟刪除過濾)
                builder.eq("isDeleted", false);

                // 2. Logic: Tenant Isolation
                boolean isSuperAdmin = currentUser.getRoles() != null && currentUser.getRoles().contains("SUPER_ADMIN");

                if (!isSuperAdmin) {
                        // Force tenant isolation
                        builder.eq("tenantId", currentUser.getTenantId());
                }

                // 3. Logic: Keyword Search (username OR email)
                if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
                        builder.orGroup(sub -> sub
                                        .like("username", request.getKeyword())
                                        .like("email", request.getKeyword()));
                }

                // 4. Logic: Department Search (IAM_QRY_006)
                // Note: UserPO does not have departmentId or Employee relation.
                // Commenting out to prevent crash.
                /*
                 * if (request.getDepartmentId() != null) {
                 * // builder.eq("employee.departmentId", request.getDepartmentId());
                 * }
                 */

                // 5. Logic: Role Search (IAM_QRY_003)
                // UserPO does not have roles collection mapped.
                // Commenting out to prevent crash.
                /*
                 * if (request.getRoleId() != null) {
                 * // builder.eq("roles", request.getRoleId());
                 * }
                 */

                return builder.build();
        }

        @Override
        protected PageResponse<UserListResponse> executeQuery(
                        QueryGroup query,
                        GetUserListRequest request,
                        JWTModel currentUser,
                        String... args) throws Exception {

                // 處理分頁
                int page = request.getPage() > 0 ? request.getPage() - 1 : 0;
                int size = request.getSize() > 0 ? request.getSize() : 20;

                // 簡單處理排序
                Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");

                Pageable pageable = PageRequest.of(page, size, sort);

                Page<User> userPage = userRepository.findPage(query, pageable);
                List<User> users = userPage.getContent();
                long total = userPage.getTotalElements();

                List<UserListResponse> items = users.stream()
                                .map(this::toListResponse)
                                .collect(Collectors.toList());

                return PageResponse.<UserListResponse>builder()
                                .items(items)
                                .total(total)
                                .page(request.getPage())
                                .size(request.getSize())
                                .totalPages(userPage.getTotalPages())
                                .build();
        }

        private UserListResponse toListResponse(User user) {
                return UserListResponse.builder()
                                .userId(user.getId().getValue())
                                .username(user.getUsername())
                                .email(user.getEmail().getValue())
                                .displayName(user.getDisplayName())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .status(user.getStatus().name())
                                .tenantId(user.getTenantId())
                                .roles(user.getRoles())
                                .lastLoginAt(user.getLastLoginAt())
                                .createdAt(user.getCreatedAt())
                                .build();
        }
}
