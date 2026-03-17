package com.company.hrms.workflow.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Local Profile 資料庫初始化器
 *
 * 在 local profile 啟動時自動載入 db/data-local.sql，
 * 寫入種子資料（流程定義、流程實例、審核任務、代理人設定等）。
 * 資料表由 JPA ddl-auto: update 自動建立，此處僅負責種子資料。
 *
 * 使用 continueOnError=true 確保重複啟動時不會因主鍵衝突而中斷。
 */
@Configuration
@Profile("local")
public class LocalDatabaseInitializer {

    private static final Logger log = LoggerFactory.getLogger(LocalDatabaseInitializer.class);

    @Bean
    public CommandLineRunner initLocalDatabase(DataSource dataSource) {
        return args -> {
            log.info("=== HR11 Workflow: 開始載入 Local Profile 種子資料 ===");
            try {
                ClassPathResource schemaResource = new ClassPathResource("db/schema-local.sql");
                ClassPathResource dataResource = new ClassPathResource("db/data-local.sql");

                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.setContinueOnError(true);
                populator.setSqlScriptEncoding("UTF-8");

                if (schemaResource.exists()) {
                    populator.addScript(schemaResource);
                    log.info("已加入 schema-local.sql");
                }

                if (dataResource.exists()) {
                    populator.addScript(dataResource);
                    log.info("已加入 data-local.sql");
                } else {
                    log.warn("找不到 db/data-local.sql，跳過種子資料載入");
                    return;
                }

                populator.execute(dataSource);
                log.info("=== HR11 Workflow: Local Profile 種子資料載入完成（流程定義、實例、任務、代理人）===");
            } catch (Exception e) {
                log.warn("Local Profile 種子資料載入失敗: {}", e.getMessage());
            }
        };
    }
}
