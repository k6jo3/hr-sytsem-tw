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
                return QueryBuilder.where()
                                .fromDto(request)
                                .ne("status", "DELETED")
                                // 這裡可以加入其他隱含過濾條件，例如 .eq("tenantId", currentUser.getTenantId())
                                .build();
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
                                .status(user.getStatus().name())
                                .build();
        }
}
