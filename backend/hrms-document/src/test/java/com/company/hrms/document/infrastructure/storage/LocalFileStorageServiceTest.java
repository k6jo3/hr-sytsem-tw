package com.company.hrms.document.infrastructure.storage;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.company.hrms.common.exception.DomainException;

/**
 * 本地檔案儲存服務單元測試
 */
class LocalFileStorageServiceTest {

    private LocalFileStorageService storageService;
    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("hrms-storage-test");

        StorageProperties properties = new StorageProperties();
        properties.setBasePath(tempDir.toString());

        storageService = new LocalFileStorageService(properties);
        storageService.init();
    }

    @AfterEach
    void tearDown() throws IOException {
        // 清理測試暫存目錄
        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try { Files.deleteIfExists(path); } catch (IOException ignored) { }
                    });
        }
    }

    // === save（byte[]）===

    @Test
    @DisplayName("儲存檔案 - 成功寫入並回傳路徑")
    void save_bytes_shouldWriteFileAndReturnPath() {
        byte[] content = "Hello, HRMS!".getBytes(StandardCharsets.UTF_8);
        String path = "test-folder/test.txt";

        String result = storageService.save(path, content);

        assertEquals(path, result);
        assertTrue(storageService.exists(path));
    }

    @Test
    @DisplayName("儲存檔案 - 自動建立巢狀目錄")
    void save_bytes_shouldCreateNestedDirectories() {
        byte[] content = "nested".getBytes(StandardCharsets.UTF_8);
        String path = "a/b/c/deep.txt";

        storageService.save(path, content);

        assertTrue(Files.exists(tempDir.resolve(path)));
    }

    @Test
    @DisplayName("儲存檔案 - 覆蓋既有檔案")
    void save_bytes_shouldOverwriteExistingFile() {
        String path = "overwrite.txt";
        storageService.save(path, "old".getBytes(StandardCharsets.UTF_8));
        storageService.save(path, "new".getBytes(StandardCharsets.UTF_8));

        byte[] loaded = storageService.load(path);
        assertEquals("new", new String(loaded, StandardCharsets.UTF_8));
    }

    // === save（InputStream）===

    @Test
    @DisplayName("儲存串流 - 成功寫入")
    void save_stream_shouldWriteFile() {
        byte[] content = "Stream content".getBytes(StandardCharsets.UTF_8);
        String path = "stream/file.txt";

        storageService.save(path, new ByteArrayInputStream(content));

        byte[] loaded = storageService.load(path);
        assertEquals("Stream content", new String(loaded, StandardCharsets.UTF_8));
    }

    // === load ===

    @Test
    @DisplayName("載入檔案 - 成功讀取內容")
    void load_shouldReturnFileContent() {
        String path = "readable.txt";
        byte[] expected = "讀取測試內容".getBytes(StandardCharsets.UTF_8);
        storageService.save(path, expected);

        byte[] actual = storageService.load(path);

        assertArrayEquals(expected, actual);
    }

    @Test
    @DisplayName("載入檔案 - 檔案不存在應拋出例外")
    void load_fileNotFound_shouldThrowException() {
        DomainException ex = assertThrows(DomainException.class,
                () -> storageService.load("nonexistent.txt"));
        assertEquals("STORAGE_FILE_NOT_FOUND", ex.getErrorCode());
    }

    // === delete ===

    @Test
    @DisplayName("刪除檔案 - 成功刪除回傳 true")
    void delete_existingFile_shouldReturnTrue() {
        String path = "to-delete.txt";
        storageService.save(path, "temp".getBytes(StandardCharsets.UTF_8));

        boolean result = storageService.delete(path);

        assertTrue(result);
        assertFalse(storageService.exists(path));
    }

    @Test
    @DisplayName("刪除檔案 - 檔案不存在回傳 false")
    void delete_nonExistingFile_shouldReturnFalse() {
        boolean result = storageService.delete("ghost.txt");

        assertFalse(result);
    }

    // === exists ===

    @Test
    @DisplayName("檢查存在 - 檔案存在回傳 true")
    void exists_existingFile_shouldReturnTrue() {
        String path = "exists-check.txt";
        storageService.save(path, "data".getBytes(StandardCharsets.UTF_8));

        assertTrue(storageService.exists(path));
    }

    @Test
    @DisplayName("檢查存在 - 檔案不存在回傳 false")
    void exists_nonExistingFile_shouldReturnFalse() {
        assertFalse(storageService.exists("no-such-file.txt"));
    }

    // === 安全性 ===

    @Test
    @DisplayName("路徑穿越攻擊 - 使用 ../ 應拋出例外")
    void save_pathTraversal_shouldThrowException() {
        byte[] content = "malicious".getBytes(StandardCharsets.UTF_8);

        DomainException ex = assertThrows(DomainException.class,
                () -> storageService.save("../../etc/passwd", content));
        assertEquals("STORAGE_PATH_TRAVERSAL", ex.getErrorCode());
    }

    @Test
    @DisplayName("路徑穿越攻擊 - load 也應防護")
    void load_pathTraversal_shouldThrowException() {
        DomainException ex = assertThrows(DomainException.class,
                () -> storageService.load("../../../etc/shadow"));
        assertEquals("STORAGE_PATH_TRAVERSAL", ex.getErrorCode());
    }

    @Test
    @DisplayName("路徑穿越攻擊 - delete 也應防護")
    void delete_pathTraversal_shouldThrowException() {
        DomainException ex = assertThrows(DomainException.class,
                () -> storageService.delete("../../important.dat"));
        assertEquals("STORAGE_PATH_TRAVERSAL", ex.getErrorCode());
    }

    @Test
    @DisplayName("路徑穿越攻擊 - exists 也應防護")
    void exists_pathTraversal_shouldThrowException() {
        DomainException ex = assertThrows(DomainException.class,
                () -> storageService.exists("../../secret.key"));
        assertEquals("STORAGE_PATH_TRAVERSAL", ex.getErrorCode());
    }

    @Test
    @DisplayName("空路徑 - 應拋出例外")
    void save_emptyPath_shouldThrowException() {
        DomainException ex = assertThrows(DomainException.class,
                () -> storageService.save("", "data".getBytes()));
        assertEquals("STORAGE_INVALID_PATH", ex.getErrorCode());
    }

    @Test
    @DisplayName("null 路徑 - 應拋出例外")
    void save_nullPath_shouldThrowException() {
        DomainException ex = assertThrows(DomainException.class,
                () -> storageService.save(null, "data".getBytes()));
        assertEquals("STORAGE_INVALID_PATH", ex.getErrorCode());
    }
}
