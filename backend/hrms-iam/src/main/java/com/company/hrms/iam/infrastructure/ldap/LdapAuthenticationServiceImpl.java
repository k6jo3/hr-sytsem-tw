package com.company.hrms.iam.infrastructure.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.company.hrms.iam.domain.service.LdapAuthenticationDomainService;
import com.company.hrms.iam.infrastructure.config.LdapProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * LDAP 認證 Domain Service 實作
 * 使用 JNDI 直接連線 LDAP/AD 伺服器
 */
@Service
@ConditionalOnProperty(name = "ldap.enabled", havingValue = "true")
@RequiredArgsConstructor
@Slf4j
public class LdapAuthenticationServiceImpl implements LdapAuthenticationDomainService {

    private final LdapProperties ldapProperties;

    @Override
    public LdapUserInfo authenticate(String username, String password) {
        // 步驟一：用管理者帳號搜尋使用者 DN
        String userDn = findUserDn(username);
        if (userDn == null) {
            throw new LdapAuthenticationException("LDAP 使用者不存在: " + username);
        }

        // 步驟二：用使用者帳密 Bind 驗證
        DirContext userContext = null;
        try {
            Hashtable<String, String> env = createBaseEnvironment();
            env.put(Context.SECURITY_PRINCIPAL, userDn);
            env.put(Context.SECURITY_CREDENTIALS, password);
            userContext = new InitialDirContext(env);
        } catch (NamingException e) {
            throw new LdapAuthenticationException("LDAP 認證失敗: " + e.getMessage(), e);
        } finally {
            closeContext(userContext);
        }

        // 步驟三：取得使用者屬性
        return fetchUserInfo(username, userDn);
    }

    @Override
    public List<String> getUserGroups(String userDn) {
        List<String> groups = new ArrayList<>();
        DirContext context = null;

        try {
            context = createAdminContext();
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            controls.setReturningAttributes(new String[]{"cn", "distinguishedName"});

            String filter = ldapProperties.getGroupSearchFilter().replace("{0}", userDn);
            String searchBase = ldapProperties.getGroupSearchBase().isEmpty()
                    ? ldapProperties.getBaseDn()
                    : ldapProperties.getGroupSearchBase() + "," + ldapProperties.getBaseDn();

            NamingEnumeration<SearchResult> results = context.search(searchBase, filter, controls);
            while (results.hasMore()) {
                SearchResult result = results.next();
                groups.add(result.getNameInNamespace());
            }
        } catch (NamingException e) {
            log.warn("LDAP 群組查詢失敗: {}", e.getMessage());
        } finally {
            closeContext(context);
        }

        return groups;
    }

    private String findUserDn(String username) {
        DirContext context = null;
        try {
            context = createAdminContext();
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            controls.setReturningAttributes(new String[]{"distinguishedName"});

            String filter = ldapProperties.getUserSearchFilter().replace("{0}", escapeLdapFilter(username));
            String searchBase = ldapProperties.getUserSearchBase().isEmpty()
                    ? ldapProperties.getBaseDn()
                    : ldapProperties.getUserSearchBase() + "," + ldapProperties.getBaseDn();

            NamingEnumeration<SearchResult> results = context.search(searchBase, filter, controls);
            if (results.hasMore()) {
                return results.next().getNameInNamespace();
            }
            return null;
        } catch (NamingException e) {
            throw new LdapAuthenticationException("LDAP 搜尋失敗: " + e.getMessage(), e);
        } finally {
            closeContext(context);
        }
    }

    private LdapUserInfo fetchUserInfo(String username, String userDn) {
        DirContext context = null;
        try {
            context = createAdminContext();
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            controls.setReturningAttributes(new String[]{
                    "displayName", "mail", "employeeID", "sAMAccountName", "memberOf"
            });

            String filter = ldapProperties.getUserSearchFilter().replace("{0}", escapeLdapFilter(username));
            String searchBase = ldapProperties.getUserSearchBase().isEmpty()
                    ? ldapProperties.getBaseDn()
                    : ldapProperties.getUserSearchBase() + "," + ldapProperties.getBaseDn();

            NamingEnumeration<SearchResult> results = context.search(searchBase, filter, controls);
            if (results.hasMore()) {
                Attributes attrs = results.next().getAttributes();
                String displayName = getAttr(attrs, "displayName");
                String email = getAttr(attrs, "mail");
                String employeeId = getAttr(attrs, "employeeID");

                // 取得群組（從 memberOf 屬性）
                List<String> groups = new ArrayList<>();
                if (attrs.get("memberOf") != null) {
                    NamingEnumeration<?> memberOfValues = attrs.get("memberOf").getAll();
                    while (memberOfValues.hasMore()) {
                        groups.add(memberOfValues.next().toString());
                    }
                }

                return new LdapUserInfo(username, userDn, displayName, email, employeeId, groups);
            }
            return new LdapUserInfo(username, userDn, null, null, null, new ArrayList<>());
        } catch (NamingException e) {
            log.warn("LDAP 使用者資訊查詢失敗: {}", e.getMessage());
            return new LdapUserInfo(username, userDn, null, null, null, new ArrayList<>());
        } finally {
            closeContext(context);
        }
    }

    private DirContext createAdminContext() throws NamingException {
        Hashtable<String, String> env = createBaseEnvironment();
        env.put(Context.SECURITY_PRINCIPAL, ldapProperties.getBindDn());
        env.put(Context.SECURITY_CREDENTIALS, ldapProperties.getBindPassword());
        return new InitialDirContext(env);
    }

    private Hashtable<String, String> createBaseEnvironment() {
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, ldapProperties.getUrl());
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put("com.sun.jndi.ldap.connect.timeout", String.valueOf(ldapProperties.getConnectTimeout()));
        env.put("com.sun.jndi.ldap.read.timeout", String.valueOf(ldapProperties.getReadTimeout()));

        // LDAPS 支援
        if (ldapProperties.getUrl() != null && ldapProperties.getUrl().startsWith("ldaps://")) {
            env.put(Context.SECURITY_PROTOCOL, "ssl");
        }
        return env;
    }

    private String getAttr(Attributes attrs, String name) throws NamingException {
        return attrs.get(name) != null ? attrs.get(name).get().toString() : null;
    }

    /**
     * 轉義 LDAP 過濾器特殊字元，防止 LDAP Injection
     */
    private String escapeLdapFilter(String input) {
        if (input == null) return null;
        StringBuilder sb = new StringBuilder();
        for (char c : input.toCharArray()) {
            switch (c) {
                case '\\': sb.append("\\5c"); break;
                case '*':  sb.append("\\2a"); break;
                case '(':  sb.append("\\28"); break;
                case ')':  sb.append("\\29"); break;
                case '\0': sb.append("\\00"); break;
                default:   sb.append(c);
            }
        }
        return sb.toString();
    }

    private void closeContext(DirContext context) {
        if (context != null) {
            try {
                context.close();
            } catch (NamingException e) {
                log.debug("LDAP context close failed", e);
            }
        }
    }
}
