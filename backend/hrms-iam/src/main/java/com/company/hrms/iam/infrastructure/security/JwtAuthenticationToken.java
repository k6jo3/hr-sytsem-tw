package com.company.hrms.iam.infrastructure.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.company.hrms.common.model.JWTModel;

import java.util.Collection;
import java.util.List;

/**
 * JWT 認證令牌
 * 存儲從 JWT Token 中解析出的用戶信息，
 * principal 為 JWTModel 以供 CurrentUserArgumentResolver 正確解析
 */
public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final JWTModel jwtModel;

    public JwtAuthenticationToken(JWTModel jwtModel,
                                  Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwtModel = jwtModel;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null; // JWT 認證不需要密碼
    }

    @Override
    public Object getPrincipal() {
        return jwtModel;
    }

    public String getUserId() {
        return jwtModel.getUserId();
    }

    public String getUsername() {
        return jwtModel.getUsername();
    }

    public List<String> getRoles() {
        return jwtModel.getRoles();
    }
}
