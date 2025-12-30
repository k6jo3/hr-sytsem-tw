package com.company.hrms.iam.application.service.permission;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.response.permission.PermissionListResponse;
import com.company.hrms.iam.domain.model.entity.Permission;
import com.company.hrms.iam.domain.repository.IPermissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢權限列表 Application Service (Pipeline 模式)
 * 
 * <p>
 * 注意：簡單查詢可選擇不使用 Pipeline
 * </p>
 */
@Service("getPermissionListServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetPermissionListServiceImpl
        implements QueryApiService<Void, List<PermissionListResponse>> {

    private final IPermissionRepository permissionRepository;

    @Override
    public List<PermissionListResponse> getResponse(Void request, JWTModel currentUser, String... args)
            throws Exception {

        log.info("查詢權限列表");

        List<Permission> permissions = permissionRepository.findAll();

        return permissions.stream()
                .map(this::toResponse)
                .toList();
    }

    private PermissionListResponse toResponse(Permission permission) {
        return PermissionListResponse.builder()
                .permissionId(permission.getId().getValue())
                .permissionCode(permission.getPermissionCode())
                .resource(permission.getResource())
                .action(permission.getAction())
                .description(permission.getDescription())
                .build();
    }
}
