package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.CommandApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 批次匯入員工服務實作
 * TODO: 實作 Excel 匯入功能
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
        log.info("Importing employees - TODO: Implement Excel import");

        // TODO: 實作 Excel 匯入邏輯
        // 1. 解析 Excel 檔案
        // 2. 驗證資料格式
        // 3. 批次建立員工
        // 4. 回傳匯入結果 (成功/失敗筆數)

        throw new UnsupportedOperationException("Excel 匯入功能尚未實作");
    }
}
