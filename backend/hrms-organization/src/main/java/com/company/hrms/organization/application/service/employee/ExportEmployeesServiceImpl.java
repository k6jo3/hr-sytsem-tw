package com.company.hrms.organization.application.service.employee;

import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 匯出員工清單服務實作
 * TODO: 實作 Excel 匯出功能
 */
@Service("exportEmployeesServiceImpl")
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExportEmployeesServiceImpl
        implements QueryApiService<Void, byte[]> {

    @Override
    public byte[] getResponse(Void request,
                              JWTModel currentUser,
                              String... args) throws Exception {
        log.info("Exporting employees - TODO: Implement Excel export");

        // TODO: 實作 Excel 匯出邏輯
        // 1. 查詢員工清單
        // 2. 建立 Excel 檔案 (使用 Apache POI)
        // 3. 回傳 byte[]

        throw new UnsupportedOperationException("Excel 匯出功能尚未實作");
    }
}
