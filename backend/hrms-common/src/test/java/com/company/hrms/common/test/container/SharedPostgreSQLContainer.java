package com.company.hrms.common.test.container;

import org.testcontainers.containers.PostgreSQLContainer;

/**
 * 共用 PostgreSQL 容器（Singleton）
 * 整個 JVM 生命週期只啟動一個容器，所有測試共用
 *
 * <p>容器生命週期：
 * <ul>
 *   <li>第一次呼叫 getInstance() 時啟動（約 3-5 秒）</li>
 *   <li>JVM 結束時自動銷毀</li>
 *   <li>同一個 JVM 內所有測試 class 共用</li>
 * </ul>
 *
 * <p>CI 並行策略：
 * 不要用 mvn -T 1C（多 JVM 會導致容器競爭），
 * 改用 GitHub Actions matrix strategy 讓每個模組跑在獨立 job。
 */
public final class SharedPostgreSQLContainer {

    private static final String IMAGE = "postgres:15-alpine";
    private static final String DATABASE = "hrms_test";
    private static final String USERNAME = "test";
    private static final String PASSWORD = "test";

    private static volatile PostgreSQLContainer<?> instance;

    private SharedPostgreSQLContainer() {
    }

    /**
     * 取得共用容器實例（DCL 雙重檢查鎖）
     */
    public static PostgreSQLContainer<?> getInstance() {
        if (instance == null) {
            synchronized (SharedPostgreSQLContainer.class) {
                if (instance == null) {
                    instance = new PostgreSQLContainer<>(IMAGE)
                            .withDatabaseName(DATABASE)
                            .withUsername(USERNAME)
                            .withPassword(PASSWORD);
                    instance.start();
                }
            }
        }
        return instance;
    }

    /**
     * 註冊 Spring datasource 屬性
     * 在 test base class 的 @DynamicPropertySource 中呼叫
     */
    public static void registerProperties(
            org.springframework.test.context.DynamicPropertyRegistry registry) {
        PostgreSQLContainer<?> container = getInstance();
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
        registry.add("spring.datasource.driver-class-name",
                () -> "org.postgresql.Driver");
        registry.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.PostgreSQLDialect");
    }
}
