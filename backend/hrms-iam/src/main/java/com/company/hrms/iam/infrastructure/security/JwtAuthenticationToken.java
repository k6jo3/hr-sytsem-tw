package com.company.hrms.iam.infrastructure.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

/**
 * JWT 認證令牌
 * 存儲從 JWT Token 中解析出的用戶信息
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final String userId;
    private final String username;
    private final List<String> roles;

    public JwtAuthenticationToken(String userId,
                                  String username,
                                  List<String> roles,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.username = username;
        this.roles = roles;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // JWT 認證不需要密碼
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    public String getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public List<String> getRoles() {
        return roles;
    }
}
