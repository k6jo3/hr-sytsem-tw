package com.company.hrms.document.domain.service;

import java.io.InputStream;

/**
 * 檔案儲存服務介面
 * <p>
 * 定義於 Domain 層，遵循 DIP（依賴反轉原則）。
 * 實作位於 Infrastructure 層（本地檔案系統、S3、Azure Blob 等）。
 * </p>
 */
public interface IFileStorageService {

    /**
     * 儲存檔案
     *
     * @param storagePath 儲存路徑（相對於根目錄）
     * @param content     檔案內容
     * @return 實際儲存的完整路徑
     */
    String save(String storagePath, byte[] content);

    /**
     * 儲存檔案（串流方式）
     *
     * @param storagePath 儲存路徑（相對於根目錄）
     * @param inputStream 檔案串流
     * @return 實際儲存的完整路徑
     */
    String save(String storagePath, InputStream inputStream);

    /**
     * 載入檔案內容
     *
     * @param storagePath 儲存路徑
     * @return 檔案內容的位元組陣列
     */
    byte[] load(String storagePath);

    /**
     * 刪除檔案
     *
     * @param storagePath 儲存路徑
     * @return 是否成功刪除
     */
    boolean delete(String storagePath);

    /**
     * 檢查檔案是否存在
     *
     * @param storagePath 儲存路徑
     * @return 是否存在
     */
    boolean exists(String storagePath);
}
