package com.company.hrms.iam.application.service.system;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.response.system.FeatureToggleResponse;
import com.company.hrms.iam.domain.model.aggregate.FeatureToggle;
import com.company.hrms.iam.domain.repository.IFeatureToggleRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢功能開關列表 Application Service
 * 對應 Controller 方法：listFeatureToggles
 */
@Service("listFeatureTogglesServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListFeatureTogglesServiceImpl
        implements QueryApiService<Object, List<FeatureToggleResponse>> {

    private final IFeatureToggleRepository repository;

    @Override
    public List<FeatureToggleResponse> getResponse(
            Object request, JWTModel currentUser, String... args) throws Exception {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private FeatureToggleResponse toResponse(FeatureToggle toggle) {
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
