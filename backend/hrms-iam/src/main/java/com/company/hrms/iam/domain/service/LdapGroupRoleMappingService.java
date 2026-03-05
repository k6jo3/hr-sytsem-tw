package com.company.hrms.iam.domain.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * LDAP 群組 → RBAC 角色映射 Domain Service
 */
public class LdapGroupRoleMappingService {

    /**
     * 將 LDAP 群組 DN 清單映射為 RBAC 角色代碼
     *
     * @param groupDns         使用者所屬的 LDAP 群組 DN 清單
     * @param groupRoleMapping 群組 DN → 角色代碼 映射表
     * @return 對應的角色代碼清單（去重）
     */
    public List<String> mapGroupsToRoles(List<String> groupDns, Map<String, String> groupRoleMapping) {
        if (groupDns == null || groupRoleMapping == null || groupRoleMapping.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> roles = new ArrayList<>();
        for (String groupDn : groupDns) {
            // 精確匹配
            String role = groupRoleMapping.get(groupDn);
            if (role != null && !roles.contains(role)) {
                roles.add(role);
                continue;
            }

            // CN 模糊匹配（支援只配 CN 名稱）
            String cn = extractCn(groupDn);
            if (cn != null) {
                for (Map.Entry<String, String> entry : groupRoleMapping.entrySet()) {
                    String mappingKey = entry.getKey();
                    if (mappingKey.equalsIgnoreCase(cn) || mappingKey.equalsIgnoreCase("CN=" + cn)) {
                        if (!roles.contains(entry.getValue())) {
                            roles.add(entry.getValue());
                        }
                    }
                }
            }
        }
        return roles;
    }

    /**
     * 從 DN 中提取 CN
     * 如 "CN=HR_DEPT,OU=Groups,DC=company,DC=com" → "HR_DEPT"
     */
    private String extractCn(String dn) {
        if (dn == null) return null;
        for (String part : dn.split(",")) {
            String trimmed = part.trim();
            if (trimmed.toUpperCase().startsWith("CN=")) {
                return trimmed.substring(3);
            }
        }
        return null;
    }
}
