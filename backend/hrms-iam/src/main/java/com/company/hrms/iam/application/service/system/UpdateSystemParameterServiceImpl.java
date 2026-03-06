package com.company.hrms.iam.application.service.system;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import com.company.hrms.iam.api.request.system.UpdateSystemParameterRequest;
import com.company.hrms.iam.api.response.system.SystemParameterResponse;
import com.company.hrms.iam.domain.model.aggregate.SystemParameter;
import com.company.hrms.iam.domain.repository.ISystemParameterRepository;

import lombok.RequiredArgsConstructor;

/**
 * 更新系統參數 Application Service
 * 對應 Controller 方法：updateSystemParameter
 *
 * 流程：
 * 1. 根據 paramCode 查詢參數
 * 2. 呼叫 Domain 方法更新值（記錄異動）
 * 3. 持久化更新
 * 4. 回傳更新後的參數
 */
@Service("updateSystemParameterServiceImpl")
@RequiredArgsConstructor
@Transactional
public class UpdateSystemParameterServiceImpl
        implements CommandApiService<UpdateSystemParameterRequest, SystemParameterResponse> {

    private final ISystemParameterRepository repository;

    @Override
    public SystemParameterResponse execCommand(
            UpdateSystemParameterRequest request, JWTModel currentUser, String... args) throws Exception {

        String paramCode = args[0];
        String operator = currentUser.getUsername();

        // 1. 查詢參數
        SystemParameter parameter = repository.findByParamCode(paramCode)
                .orElseThrow(() -> new IllegalArgumentException("系統參數不存在: " + paramCode));

        // 2. Domain 層更新（追蹤異動記錄）
        parameter.updateValue(request.getParamValue(), operator);

        // 3. 持久化
        repository.update(parameter);

        // 4. 組裝回應
        return SystemParameterResponse.builder()
                .paramCode(parameter.getParamCode())
                .paramName(parameter.getParamName())
                .paramValue(parameter.getParamValue())
                .paramType(parameter.getParamType())
                .module(parameter.getModule())
                .category(parameter.getCategory())
                .description(parameter.getDescription())
                .defaultValue(parameter.getDefaultValue())
                .isEncrypted(parameter.isEncrypted())
                .updatedAt(parameter.getUpdatedAt())
                .updatedBy(parameter.getUpdatedBy())
                .build();
    }
}
