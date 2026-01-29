package com.company.hrms.reporting;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Reporting 服務應用程式啟動測試
 *
 * <p>驗證 Spring Context 能正確載入
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Reporting 服務啟動測試")
class ReportingApplicationTest {

    @Test
    @DisplayName("Spring Context 應能正確載入")
    void contextLoads() {
        // 若 Spring Context 無法載入，此測試會失敗
    }
}
