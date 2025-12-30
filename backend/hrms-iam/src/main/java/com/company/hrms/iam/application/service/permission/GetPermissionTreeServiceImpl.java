package com.company.hrms.iam.application.service.permission;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.response.permission.PermissionTreeResponse;
import com.company.hrms.iam.domain.model.entity.Permission;
import com.company.hrms.iam.domain.repository.IPermissionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 查詢權限樹 Application Service (Pipeline 模式)
 * 
 * <p>
 * 注意：簡單查詢可選擇不使用 Pipeline
 * </p>
 */
@Service("getPermissionTreeServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetPermissionTreeServiceImpl
                implements QueryApiService<Void, List<PermissionTreeResponse>> {

        private final IPermissionRepository permissionRepository;

        private static final Map<String, String> RESOURCE_DISPLAY_NAMES = Map.of(
                        "user", "使用者管理",
                        "role", "角色管理",
                        "permission", "權限管理",
                        "employee", "員工管理",
                        "attendance", "考勤管理",
                        "payroll", "薪資管理",
                        "project", "專案管理",
                        "report", "報表管理");

        @Override
        public List<PermissionTreeResponse> getResponse(Void request, JWTModel currentUser, String... args)
                        throws Exception {

                log.info("查詢權限樹");

                List<Permission> permissions = permissionRepository.findAll();

                // 按資源分組
                Map<String, List<Permission>> groupedByResource = new LinkedHashMap<>();
                for (Permission permission : permissions) {
                        groupedByResource
                                        .computeIfAbsent(permission.getResource(), k -> new ArrayList<>())
                                        .add(permission);
                }

                // 轉換為回應格式
                List<PermissionTreeResponse> result = new ArrayList<>();
                for (Map.Entry<String, List<Permission>> entry : groupedByResource.entrySet()) {
                        String resource = entry.getKey();
                        List<Permission> resourcePermissions = entry.getValue();

                        List<PermissionTreeResponse.PermissionItem> items = resourcePermissions.stream()
                                        .map(p -> PermissionTreeResponse.PermissionItem.builder()
                                                        .permissionId(p.getId().getValue())
                                                        .permissionCode(p.getPermissionCode())
                                                        .action(p.getAction())
                                                        .description(p.getDescription())
                                                        .build())
                                        .toList();

                        result.add(PermissionTreeResponse.builder()
                                        .resource(resource)
                                        .resourceDisplayName(RESOURCE_DISPLAY_NAMES.getOrDefault(resource, resource))
                                        .permissions(items)
                                        .build());
                }

                return result;
        }
}
