package com.company.hrms.organization.application.service.employee;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 批次匯入員工服務實作
 * <p>
 * 目前僅保留介面，待確認 Excel 格式後實作解析邏輯
 * </p>
 */
@Service("importEmployeesServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ImportEmployeesServiceImpl
        implements CommandApiService<Void, Void> {

    @Override
    public Void execCommand(Void request,
            JWTModel currentUser,
            String... args) throws Exception {
        log.info("Importing employees request received.");

        // Note: Excel 解析與匯入邏輯待實作
        // 目前先模擬成功以支援 API 測試
        log.warn("Excel 匯入功能僅為 Stub，尚未實作實際邏輯");

        return null;
    }
}
