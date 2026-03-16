package com.company.hrms.iam.application.service.user;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.iam.domain.model.aggregate.Role;
import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.repository.IRoleRepository;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.PasswordHashingDomainService;
import com.company.hrms.iam.infrastructure.event.EmployeeCreatedEventDto;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 自動建立員工帳號服務
 *
 * <p>當收到 EmployeeCreatedEvent 時，自動在 IAM 中建立對應的使用者帳號。
 *
 * <p>建立規則：
 * <ul>
 *   <li>username: 使用 employeeNumber（員工編號，如 E001）</li>
 *   <li>email: 使用 companyEmail（若為 null，使用 {employeeNumber}@placeholder.local）</li>
 *   <li>displayName: 使用 fullName</li>
 *   <li>password: 系統自動產生 12 位隨機密碼</li>
 *   <li>狀態: ACTIVE（首次登入需改密碼）</li>
 *   <li>角色: 預設指派 EMPLOYEE 角色</li>
 * </ul>
 *
 * <p>重複防護：若 employeeId 或 username 已存在，跳過建立。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AutoCreateUserFromEmployeeService {

    private static final String DEFAULT_ROLE_CODE = "EMPLOYEE";
    private static final int GENERATED_PASSWORD_LENGTH = 12;
    private static final String PASSWORD_CHARS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;
    private final PasswordHashingDomainService passwordHashingService;

    /**
     * 為新員工建立 IAM 帳號
     *
     * @param dto 員工建立事件資料
     */
    @Transactional
    public void createUserForEmployee(EmployeeCreatedEventDto dto) {
        // 驗證必要欄位
        if (dto.getEmployeeId() == null || dto.getEmployeeNumber() == null) {
            log.warn("[AutoCreateUser] 事件缺少必要欄位 (employeeId={}, employeeNumber={})，跳過建立",
                    dto.getEmployeeId(), dto.getEmployeeNumber());
            return;
        }

        // 檢查是否已存在相同 employeeId 的帳號（防止重複建立）
        Optional<User> existingByEmployeeId = userRepository.findByEmployeeId(dto.getEmployeeId());
        if (existingByEmployeeId.isPresent()) {
            log.info("[AutoCreateUser] employeeId={} 已有對應帳號 (username={})，跳過建立",
                    dto.getEmployeeId(), existingByEmployeeId.get().getUsername());
            return;
        }

        // 檢查是否已存在相同 username（員工編號）的帳號
        if (userRepository.existsByUsername(dto.getEmployeeNumber())) {
            log.warn("[AutoCreateUser] username={} 已存在，跳過建立", dto.getEmployeeNumber());
            return;
        }

        // 產生隨機密碼
        String rawPassword = generateRandomPassword();
        String passwordHash = passwordHashingService.hash(rawPassword);

        // 決定 email（若 companyEmail 為 null，使用預設 placeholder）
        String email = dto.getCompanyEmail() != null
                ? dto.getCompanyEmail()
                : dto.getEmployeeNumber().toLowerCase() + "@placeholder.local";

        // 建立使用者 Aggregate
        User user = User.create(
                dto.getEmployeeNumber(),  // username = 員工編號
                email,
                passwordHash,
                dto.getFullName()          // displayName = 全名
        );
        user.setEmployeeId(dto.getEmployeeId());
        user.activate();  // 設定為 ACTIVE，首次登入需改密碼（create 已設定 mustChangePassword=true）

        // 儲存使用者
        userRepository.save(user);
        log.info("[AutoCreateUser] 帳號建立成功: username={}, employeeId={}, displayName={}",
                user.getUsername(), dto.getEmployeeId(), dto.getFullName());

        // 指派預設 EMPLOYEE 角色
        assignDefaultRole(user);

        // TODO: 發送歡迎郵件（含臨時密碼），待 Notification 服務支援
        log.info("[AutoCreateUser] 系統產生臨時密碼（長度={}），待發送歡迎郵件通知員工",
                GENERATED_PASSWORD_LENGTH);
    }

    /**
     * 指派預設 EMPLOYEE 角色
     */
    private void assignDefaultRole(User user) {
        Optional<Role> employeeRole = roleRepository.findByRoleCode(DEFAULT_ROLE_CODE);
        if (employeeRole.isPresent()) {
            String roleId = employeeRole.get().getId().getValue();
            userRepository.updateUserRoles(user.getId(), Collections.singletonList(roleId));
            log.info("[AutoCreateUser] 已指派 EMPLOYEE 角色 (roleId={}) 給 username={}",
                    roleId, user.getUsername());
        } else {
            log.warn("[AutoCreateUser] 找不到 EMPLOYEE 角色，帳號 {} 未指派角色", user.getUsername());
        }
    }

    /**
     * 產生隨機密碼
     */
    private String generateRandomPassword() {
        StringBuilder sb = new StringBuilder(GENERATED_PASSWORD_LENGTH);
        for (int i = 0; i < GENERATED_PASSWORD_LENGTH; i++) {
            sb.append(PASSWORD_CHARS.charAt(SECURE_RANDOM.nextInt(PASSWORD_CHARS.length())));
        }
        return sb.toString();
    }
}
