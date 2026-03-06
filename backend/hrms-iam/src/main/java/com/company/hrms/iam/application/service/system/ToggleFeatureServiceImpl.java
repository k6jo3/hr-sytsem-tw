package com.company.hrms.iam.application.service.system;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.system.ToggleFeatureRequest;
import com.company.hrms.iam.api.response.system.FeatureToggleResponse;
import com.company.hrms.iam.domain.model.aggregate.FeatureToggle;
import com.company.hrms.iam.domain.repository.IFeatureToggleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 切換功能開關 Application Service
 * 對應 Controller 方法：toggleFeature
 *
 * 流程：
 * 1. 根據 featureCode 查詢開關
 * 2. 若 request 指定 enabled 值則設定，否則切換（toggle）
 * 3. 持久化更新
 * 4. 回傳更新後的開關狀態
 */
@Service("toggleFeatureServiceImpl")
@RequiredArgsConstructor
@Transactional
public class ToggleFeatureServiceImpl
        implements CommandApiService<ToggleFeatureRequest, FeatureToggleResponse> {

    private final IFeatureToggleRepository repository;

    @Override
    public FeatureToggleResponse execCommand(
            ToggleFeatureRequest request, JWTModel currentUser, String... args) throws Exception {

        String featureCode = args[0];
        String operator = currentUser.getUsername();

        // 1. 查詢開關
        FeatureToggle toggle = repository.findByFeatureCode(featureCode)
                .orElseThrow(() -> new IllegalArgumentException("功能開關不存在: " + featureCode));

        // 2. Domain 層操作
        if (request != null && request.getEnabled() != null) {
            if (request.getEnabled()) {
                toggle.enable(operator);
            } else {
                toggle.disable(operator);
            }
        } else {
            toggle.toggle(operator);
        }

        // 3. 持久化
        repository.update(toggle);

        // 4. 組裝回應
        return FeatureToggleResponse.builder()
                .featureCode(toggle.getFeatureCode())
                .featureName(toggle.getFeatureName())
                .module(toggle.getModule())
                .enabled(toggle.isEnabled())
                .description(toggle.getDescription())
                .updatedAt(toggle.getUpdatedAt())
                .updatedBy(toggle.getUpdatedBy())
                .build();
    }
}
