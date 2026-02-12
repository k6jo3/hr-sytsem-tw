package com.company.hrms.iam.application.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.EntityNotFoundException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.query.QueryBuilder;
import com.company.hrms.common.query.QueryGroup;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.controller.user.HR01UserQryController.GetUserRequest;
import com.company.hrms.iam.api.response.user.UserDetailResponse;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.repository.IUserRepository;

/**
 * 查詢單一使用者 Application Service
 * 
 * <p>
 * 命名規範：Get{名詞}ServiceImpl
 * </p>
 * <p>
 * 對應 Controller 方法：getUser
 * </p>
 */
@Service("getUserServiceImpl")
@Transactional(readOnly = true)
public class GetUserServiceImpl
        implements QueryApiService<GetUserRequest, UserDetailResponse> {

    private final IUserRepository userRepository;

    @Autowired
    public GetUserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 執行查詢單一使用者
     *
     * @param request     查詢請求 (未使用)
     * @param currentUser 當前登入使用者
     * @param args        額外參數 (args[0] = userId)
     * @return 使用者詳情
     */
    @Override
    public UserDetailResponse getResponse(GetUserRequest request,
            JWTModel currentUser,
            String... args) throws Exception {
        // 1. 取得使用者 ID
        if (args == null || args.length == 0) {
            throw new IllegalArgumentException("User ID is required");
        }
        String userId = args[0];

        // 2. 建立查詢條件（包含安全性過濾）
        QueryGroup query = QueryBuilder.where()
                .eq("userId", userId)
                .eq("isDeleted", false) // 軟刪除過濾
                .build();

        // 3. 查詢使用者
        User user = userRepository.findAll(query).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User", userId));

        // 4. 轉換為 Response VO
        return toDetailResponse(user);
    }

    /**
     * 轉換為詳情回應
     */
    private UserDetailResponse toDetailResponse(User user) {
        return UserDetailResponse.builder()
                .userId(user.getId().getValue())
                .username(user.getUsername())
                .email(user.getEmail().getValue())
                .displayName(user.getDisplayName())
                .status(user.getStatus().name())
                .tenantId(user.getTenantId())
                .employeeId(user.getEmployeeId())
                .lastLoginIp(user.getLastLoginIp())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .roles(user.getRoles())
                .lastLoginAt(user.getLastLoginAt())
                .passwordChangedAt(user.getPasswordChangedAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
