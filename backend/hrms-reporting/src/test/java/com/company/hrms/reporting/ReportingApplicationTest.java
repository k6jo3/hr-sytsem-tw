package com.company.hrms.reporting;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Reporting 服務應用程式啟動測試
 *
 * <p>
 * 驗證 Spring Context 能正確載入
 * 
 * <p>
 * 注意：此測試需要完整的基礎設施 (Kafka, Redis, PostgreSQL)
 * 在開發階段暫時禁用，待整合測試環境建立後再啟用
 *
 * @author SA Team
 * @since 2026-01-29
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Reporting 服務啟動測試")
@Disabled("需要完整的基礎設施環境，待整合測試環境建立後啟用")
class ReportingApplicationTest {

    @Test
    @DisplayName("Spring Context 應能正確載入")
    void contextLoads() {
        // 若 Spring Context 無法載入，此測試會失敗
    }
}
