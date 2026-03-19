package com.company.hrms.document.infrastructure.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;

import com.company.hrms.common.exception.DomainException;
import com.company.hrms.document.domain.service.IFileStorageService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 本地檔案系統儲存實作
 * <p>
 * 將檔案儲存至本機磁碟，路徑可透過 {@code document.storage.local.base-path} 設定。
 * 內建路徑穿越攻擊防護，確保所有操作均限制在根目錄內。
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements IFileStorageService {

    private final StorageProperties storageProperties;

    private Path rootLocation;

    /**
     * 初始化儲存根目錄，若不存在則自動建立
     */
    @PostConstruct
    public void init() {
        this.rootLocation = Paths.get(storageProperties.getBasePath()).toAbsolutePath().normalize();
        try {
            Files.createDirectories(rootLocation);
            log.info("本地檔案儲存根目錄已初始化：{}", rootLocation);
        } catch (IOException e) {
            throw new DomainException("STORAGE_INIT_FAILED",
                    "無法建立儲存根目錄：" + rootLocation + " (" + e.getMessage() + ")");
        }
    }

    @Override
    public String save(String storagePath, byte[] content) {
        Path targetPath = resolveAndValidate(storagePath);
        try {
            // 確保父目錄存在
            Files.createDirectories(targetPath.getParent());
            Files.write(targetPath, content);
            log.debug("檔案已儲存：{}", targetPath);
            return storagePath;
        } catch (IOException e) {
            throw new DomainException("STORAGE_SAVE_FAILED",
                    "檔案儲存失敗：" + storagePath + " (" + e.getMessage() + ")");
        }
    }

    @Override
    public String save(String storagePath, InputStream inputStream) {
        Path targetPath = resolveAndValidate(storagePath);
        try {
            // 確保父目錄存在
            Files.createDirectories(targetPath.getParent());
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.debug("檔案已儲存（串流）：{}", targetPath);
            return storagePath;
        } catch (IOException e) {
            throw new DomainException("STORAGE_SAVE_FAILED",
                    "檔案儲存失敗（串流）：" + storagePath + " (" + e.getMessage() + ")");
        }
    }

    @Override
    public byte[] load(String storagePath) {
        Path targetPath = resolveAndValidate(storagePath);
        if (!Files.exists(targetPath)) {
            throw new DomainException("STORAGE_FILE_NOT_FOUND",
                    "檔案不存在：" + storagePath);
        }
        try {
            return Files.readAllBytes(targetPath);
        } catch (IOException e) {
            throw new DomainException("STORAGE_LOAD_FAILED",
                    "檔案讀取失敗：" + storagePath + " (" + e.getMessage() + ")");
        }
    }

    @Override
    public boolean delete(String storagePath) {
        Path targetPath = resolveAndValidate(storagePath);
        try {
            boolean deleted = Files.deleteIfExists(targetPath);
            if (deleted) {
                log.debug("檔案已刪除：{}", targetPath);
            } else {
                log.warn("欲刪除的檔案不存在：{}", targetPath);
            }
            return deleted;
        } catch (IOException e) {
            throw new DomainException("STORAGE_DELETE_FAILED",
                    "檔案刪除失敗：" + storagePath + " (" + e.getMessage() + ")");
        }
    }

    @Override
    public boolean exists(String storagePath) {
        Path targetPath = resolveAndValidate(storagePath);
        return Files.exists(targetPath);
    }

    /**
     * 解析相對路徑並驗證是否在根目錄範圍內（防止路徑穿越攻擊）
     *
     * @param storagePath 相對儲存路徑
     * @return 安全的絕對路徑
     * @throws DomainException 若路徑包含穿越攻擊嘗試
     */
    private Path resolveAndValidate(String storagePath) {
        if (storagePath == null || storagePath.isBlank()) {
            throw new DomainException("STORAGE_INVALID_PATH", "儲存路徑不可為空");
        }

        // 正規化路徑，消除 ../ 等相對路徑片段
        Path resolved = rootLocation.resolve(storagePath).normalize();

        // 安全檢查：確保解析後的路徑仍在根目錄下
        if (!resolved.startsWith(rootLocation)) {
            log.error("偵測到路徑穿越攻擊：storagePath={}, resolved={}", storagePath, resolved);
            throw new DomainException("STORAGE_PATH_TRAVERSAL",
                    "非法的儲存路徑：禁止存取根目錄以外的位置");
        }

        return resolved;
    }
}
