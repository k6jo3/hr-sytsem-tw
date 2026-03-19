package com.company.hrms.document.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * 檔案儲存設定屬性
 * <p>
 * 透過 application.yml 中的 {@code document.storage.local} 前綴進行設定。
 * </p>
 *
 * <pre>
 * document:
 *   storage:
 *     local:
 *       base-path: /data/documents
 * </pre>
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "document.storage.local")
public class StorageProperties {

    /**
     * 本地儲存根目錄路徑
     */
    private String basePath = "./data/documents";
}
