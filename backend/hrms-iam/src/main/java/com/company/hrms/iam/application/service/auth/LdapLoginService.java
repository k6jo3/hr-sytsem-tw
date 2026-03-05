package com.company.hrms.iam.application.service.auth;

import java.util.List;
import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.company.hrms.iam.domain.model.aggregate.User;
import com.company.hrms.iam.domain.model.valueobject.UserId;
import com.company.hrms.iam.domain.repository.IUserRepository;
import com.company.hrms.iam.domain.service.LdapAuthenticationDomainService;
import com.company.hrms.iam.domain.service.LdapAuthenticationDomainService.LdapUserInfo;
import com.company.hrms.iam.domain.service.LdapGroupRoleMappingService;
import com.company.hrms.iam.domain.service.JwtTokenDomainService;
import com.company.hrms.iam.infrastructure.config.LdapProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * LDAP 登入服務
 * 負責 LDAP 認證 + JIT Provisioning + 群組角色同步
 *
 * 混合模式流程：
 * 1. 先嘗試 LDAP 認證
 * 2. LDAP 成功 → JIT Provisioning（首次建帳/更新資訊）→ 群組角色映射 → 產生 JWT
 * 3. LDAP 失敗 → 回退本地認證（由 LoginServiceImpl 處理）
 */
@Service
@ConditionalOnProperty(name = "ldap.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class LdapLoginService {

    private final LdapAuthenticationDomainService ldapAuthService;
    private final IUserRepository userRepository;
    private final JwtTokenDomainService jwtTokenService;
    private final LdapProperties ldapProperties;

    /**
     * LDAP 認證登入
     *
     * @param username 使用者名稱
     * @param password 密碼
     * @return 認證成功的 User（已 JIT Provisioning + 角色同步）
     * @throws LdapAuthenticationDomainService.LdapAuthenticationException 認證失敗
     */
    public User authenticateViaLdap(String username, String password) {
        // 步驟一：LDAP 認證
        LdapUserInfo ldapUser = ldapAuthService.authenticate(username, password);
        log.info("LDAP 認證成功: username={}, dn={}", username, ldapUser.dn());

        // 步驟二：JIT Provisioning
        User user = jitProvision(ldapUser);

        // 步驟三：群組角色映射
        if (ldapProperties.isSyncRoles()) {
            syncRoles(user, ldapUser.groups());
        }

        // 步驟四：記錄登入
        user.recordLogin();
        userRepository.save(user);

        return user;
    }

    /**
     * 檢查使用者是否為 LDAP 使用者
     */
    public boolean isLdapUser(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        return user.map(User::isLdapUser).orElse(false);
    }

    /**
     * JIT Provisioning：首次 LDAP 登入自動建立本地帳號
     */
    private User jitProvision(LdapUserInfo ldapUser) {
        Optional<User> existingUser = userRepository.findByUsername(ldapUser.username());

        if (existingUser.isPresent()) {
            // 已有帳號 → 同步 LDAP 資訊
            User user = existingUser.get();
            user.syncFromLdap(ldapUser.displayName(), ldapUser.email(), ldapUser.dn());
            log.info("LDAP 使用者資訊同步: username={}", ldapUser.username());
            return user;
        }

        if (!ldapProperties.isJitProvisioning()) {
            throw new LdapAuthenticationDomainService.LdapAuthenticationException(
                    "JIT Provisioning 未啟用，LDAP 使用者需先手動建立帳號: " + ldapUser.username());
        }

        // 首次登入 → 自動建帳
        User newUser = User.createFromLdap(
                ldapUser.username(),
                ldapUser.email(),
                ldapUser.displayName(),
                ldapUser.dn(),
                ldapProperties.getDefaultTenantId());

        if (ldapUser.employeeId() != null) {
            newUser.setEmployeeId(ldapUser.employeeId());
        }

        userRepository.save(newUser);
        log.info("JIT Provisioning 建立新帳號: username={}, userId={}", ldapUser.username(), newUser.getId().getValue());
        return newUser;
    }

    /**
     * 同步 LDAP 群組 → RBAC 角色
     */
    private void syncRoles(User user, List<String> ldapGroups) {
        LdapGroupRoleMappingService mappingService = new LdapGroupRoleMappingService();
        List<String> mappedRoles = mappingService.mapGroupsToRoles(ldapGroups, ldapProperties.getGroupRoleMapping());

        if (!mappedRoles.isEmpty()) {
            // 清除舊的 LDAP 同步角色，重新設定
            for (String role : mappedRoles) {
                user.assignRole(role);
            }
            log.info("LDAP 群組角色同步: username={}, roles={}", user.getUsername(), mappedRoles);
        }
    }
}
