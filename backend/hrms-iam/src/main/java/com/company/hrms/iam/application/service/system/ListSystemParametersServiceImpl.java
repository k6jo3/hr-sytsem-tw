package com.company.hrms.iam.application.service.system;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.iam.api.request.system.ListSystemParametersRequest;
import com.company.hrms.iam.api.response.system.SystemParameterResponse;
import com.company.hrms.iam.domain.model.aggregate.SystemParameter;
import com.company.hrms.iam.domain.repository.ISystemParameterRepository;

import lombok.RequiredArgsConstructor;

/**
 * 查詢系統參數列表 Application Service
 * 對應 Controller 方法：listSystemParameters
 */
@Service("listSystemParametersServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListSystemParametersServiceImpl
        implements QueryApiService<ListSystemParametersRequest, List<SystemParameterResponse>> {

    private final ISystemParameterRepository repository;

    @Override
    public List<SystemParameterResponse> getResponse(
            ListSystemParametersRequest request, JWTModel currentUser, String... args) throws Exception {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private SystemParameterResponse toResponse(SystemParameter param) {
        return SystemParameterResponse.builder()
                .paramCode(param.getParamCode())
                .paramName(param.getParamName())
                .paramValue(param.getParamValue())
                .paramType(param.getParamType())
                .module(param.getModule())
                .category(param.getCategory())
                .description(param.getDescription())
                .defaultValue(param.getDefaultValue())
                .isEncrypted(param.isEncrypted())
                .updatedAt(param.getUpdatedAt())
                .updatedBy(param.getUpdatedBy())
                .build();
    }
}
