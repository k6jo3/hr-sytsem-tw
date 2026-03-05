package com.company.hrms.iam.domain.service;

import java.util.List;

/**
 * LDAP 認證 Domain Service 介面
 * 負責 LDAP 伺服器的認證與使用者資訊查詢
 */
public interface LdapAuthenticationDomainService {

    /**
     * 透過 LDAP 認證使用者
     * 
     * @param username 使用者名稱
     * @param password 密碼
     * @return 認證成功時回傳 LDAP 使用者資訊
     * @throws LdapAuthenticationException 認證失敗時拋出
     */
    LdapUserInfo authenticate(String username, String password);

    /**
     * 查詢使用者所屬的 LDAP 群組
     * 
     * @param userDn 使用者 DN
     * @return 群組 DN 清單
     */
    List<String> getUserGroups(String userDn);

    /**
     * LDAP 使用者資訊（Value Object）
     */
    record LdapUserInfo(
            String username,
            String dn,
            String displayName,
            String email,
            String employeeId,
            List<String> groups) {
    }

    /**
     * LDAP 認證例外
     */
    class LdapAuthenticationException extends RuntimeException {
        public LdapAuthenticationException(String message) {
            super(message);
        }

        public LdapAuthenticationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
