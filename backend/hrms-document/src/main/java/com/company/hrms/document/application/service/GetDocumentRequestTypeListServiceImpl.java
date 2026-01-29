package com.company.hrms.document.application.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;

import lombok.RequiredArgsConstructor;

/**
 * 獲取可申請文件類型服務實作
 */
@Service("getDocumentRequestTypesServiceImpl")
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetDocumentRequestTypeListServiceImpl implements QueryApiService<Void, List<Object>> {

    @Override
    public List<Object> getResponse(Void req, JWTModel currentUser, String... args) {
        // 模擬返回可申請類型
        return Arrays.asList(
                Map.of("code", "EMPLOYMENT_CERTIFICATE", "name", "在職證明", "description", "證明員工目前在職"),
                Map.of("code", "SALARY_CERTIFICATE", "name", "薪資證明", "description", "包含基本薪資與津貼資訊"),
                Map.of("code", "LEAVE_CERTIFICATE", "name", "離職證明", "description", "證明員工已離職"));
    }
}
