package com.company.hrms.common.test.base;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import com.company.hrms.common.test.container.SharedPostgreSQLContainer;

/**
 * 整合測試基類
 * 啟動 Spring Context 進行整合測試
 *
 * <p>特點:
 * <ul>
 *   <li>完整的 Spring 容器</li>
 *   <li>使用 test profile</li>
 *   <li>可連接真實資料庫（H2 或 Testcontainers）</li>
 * </ul>
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ActiveProfiles("test")
public abstract class BaseIntegrationTest extends BaseTest {

    // Testcontainers：動態覆蓋 datasource 設定，指向共用 PostgreSQL 容器
    @DynamicPropertySource
    static void configureDatabase(DynamicPropertyRegistry registry) {
        SharedPostgreSQLContainer.registerProperties(registry);
    }

    /**
     * 等待非同步操作完成
     */
    protected void waitForAsync(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 取得整合測試快照目錄
     */
    protected String getSnapshotDirectory() {
        return "src/test/resources/snapshots/integration/" + getTestClassName();
    }
}
