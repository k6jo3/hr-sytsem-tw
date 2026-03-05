package com.company.hrms.iam.infrastructure.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

/**
 * LDAP/AD 連線配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "ldap")
public class LdapProperties {

    /**
     * 是否啟用 LDAP 認證
     */
    private boolean enabled = false;

    /**
     * LDAP 伺服器 URL（如 ldap://ad.company.com:389 或 ldaps://ad.company.com:636）
     */
    private String url;

    /**
     * Base DN（搜尋根目錄，如 ou=users,dc=company,dc=com）
     */
    private String baseDn;

    /**
     * 使用者搜尋過濾器（{0} 為使用者名稱）
     */
    private String userSearchFilter = "(sAMAccountName={0})";

    /**
     * 使用者搜尋基底（相對於 baseDn）
     */
    private String userSearchBase = "";

    /**
     * 群組搜尋過濾器
     */
    private String groupSearchFilter = "(member={0})";

    /**
     * 群組搜尋基底（相對於 baseDn）
     */
    private String groupSearchBase = "ou=groups";

    /**
     * 管理者 Bind DN（用於搜尋使用者）
     */
    private String bindDn;

    /**
     * 管理者 Bind 密碼
     */
    private String bindPassword;

    /**
     * LDAP 屬性映射：LDAP 屬性名 → User 欄位
     */
    private Map<String, String> attributeMapping = new HashMap<>() {{
        put("displayName", "displayName");
        put("mail", "email");
        put("employeeID", "employeeId");
    }};

    /**
     * LDAP 群組 → RBAC 角色映射
     * 如：{"CN=HR_DEPT,OU=Groups,DC=company,DC=com": "HR"}
     */
    private Map<String, String> groupRoleMapping = new HashMap<>();

    /**
     * 是否啟用 JIT Provisioning（LDAP 首次登入自動建立本地帳號）
     */
    private boolean jitProvisioning = true;

    /**
     * 是否同步 LDAP 群組到 RBAC 角色
     */
    private boolean syncRoles = true;

    /**
     * 群組同步間隔（分鐘）
     */
    private int syncIntervalMinutes = 60;

    /**
     * 連線超時（毫秒）
     */
    private int connectTimeout = 5000;

    /**
     * 讀取超時（毫秒）
     */
    private int readTimeout = 10000;

    /**
     * 預設租戶 ID（LDAP 使用者的預設租戶）
     */
    private String defaultTenantId = "00000000-0000-0000-0000-000000000001";
}
