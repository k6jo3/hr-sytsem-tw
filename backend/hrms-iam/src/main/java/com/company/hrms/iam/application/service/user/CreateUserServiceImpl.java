package com.company.hrms.iam.application.service.user;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.user.CreateUserRequest;
import com.company.hrms.iam.api.response.user.CreateUserResponse;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.repository.IUserRepository;

/**
 * 新增使用者 Application Service
 * 
 * <p>
 * 命名規範：{動詞}{名詞}ServiceImpl
 * </p>
 * <p>
 * 對應 Controller 方法：createUser
 * </p>
 */
@Service("createUserServiceImpl")
@Transactional
public class CreateUserServiceImpl
        implements CommandApiService<CreateUserRequest, CreateUserResponse> {

    private final IUserRepository userRepository;

    public CreateUserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * 執行新增使用者
     * 
     * @param request     新增使用者請求
     * @param currentUser 當前登入使用者
     * @param args        額外參數 (未使用)
     * @return 新增使用者回應
     */
    @Override
    public CreateUserResponse execCommand(CreateUserRequest request,
            JWTModel currentUser,
            String... args) throws Exception {
        // 1. 檢查使用者名稱是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DomainException("USERNAME_EXISTS",
                    "使用者名稱已存在: " + request.getUsername());
        }

        // 2. 檢查 Email 是否已存在
        // 這裡暫時使用字串比較，實際應使用 Email 值物件
        // if (userRepository.existsByEmail(new Email(request.getEmail()))) {
        // throw new DomainException("EMAIL_EXISTS", "Email 已存在: " +
        // request.getEmail());
        // }

        // 3. 建立 User 聚合根 (密碼加密應由 Domain Service 處理)
        // 這裡簡化處理，實際應注入 PasswordEncoder
        String passwordHash = hashPassword(request.getPassword());

        User user = User.create(
                request.getUsername(),
                request.getEmail(),
                passwordHash,
                request.getDisplayName());

        // 4. 啟用使用者
        user.activate();

        // 5. 儲存使用者
        userRepository.save(user);

        // 6. TODO: 發布領域事件 UserCreatedEvent

        // 7. 回傳結果
        return CreateUserResponse.builder()
                .userId(user.getId().getValue())
                .username(user.getUsername())
                .message("使用者建立成功")
                .build();
    }

    /**
     * 密碼雜湊 (簡化實作，實際應使用 BCrypt)
     */
    private String hashPassword(String password) {
        // TODO: 使用 BCryptPasswordEncoder
        return "{bcrypt}" + password;
    }
}
