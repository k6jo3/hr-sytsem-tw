package com.company.hrms.iam.application.service.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.controller.user.HR01UserQryController.UserQueryRequest;
import com.company.hrms.iam.api.response.user.UserListResponse;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.repository.IUserRepository;

/**
 * 查詢使用者列表 Application Service
 * 
 * <p>
 * 命名規範：Get{名詞}ListServiceImpl
 * </p>
 * <p>
 * 對應 Controller 方法：getUserList
 * </p>
 */
@Service("getUserListServiceImpl")
@Transactional(readOnly = true)
public class GetUserListServiceImpl
        implements QueryApiService<UserQueryRequest, List<UserListResponse>> {

    private final IUserRepository userRepository;

    public GetUserListServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 執行查詢使用者列表
     * 
     * @param request     查詢請求
     * @param currentUser 當前登入使用者
     * @param args        額外參數 (未使用)
     * @return 使用者列表
     */
    @Override
    public List<UserListResponse> getResponse(UserQueryRequest request,
            JWTModel currentUser,
            String... args) throws Exception {
        // 1. 查詢所有使用者 (實際應加入篩選條件)
        List<User> users = userRepository.findAll();

        // 2. 根據條件過濾
        // TODO: 實作狀態和關鍵字篩選

        // 3. 轉換為 Response VO
        return users.stream()
                .map(this::toListResponse)
                .collect(Collectors.toList());
    }

    /**
     * 轉換為列表項目回應
     */
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
