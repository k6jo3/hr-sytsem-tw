---
name: 驗證合約測試 (Verify Contract Test)
description: 指導如何執行、除錯並修復 HRMS 合約測試中常見的問題。
---

# 驗證合約測試 (Verify Contract Test) 指南

在撰寫或維護合約測試後，執行測試並除錯是確保開發品質的重要環節。請遵守以下指南以有效排解問題。

## 1. 執行測試的指令
使用 Maven 進行特定模組或單一測試類別的驗證。**確保加上 `--projects` 限定模組，避免全系統掃描浪費時間。**
*   執行單一類別的所有測試：
    `mvn "-Dtest=MyContractTest" test --projects hrms-module-name 2>&1`
*   執行單一個別測試方法：
    `mvn "-Dtest=MyContractTest#testMyMethod" test --projects hrms-module-name 2>&1`

## 2. 常見錯誤類型總覽與修復方針

### (A) ScriptStatementFailedException / Check Constraint Violation (H2 資料庫)
*   **徵兆**：出現 `Failed to execute SQL script statement #N ... CONSTRAINT_X`
*   **原因**：測試 `@Sql` 腳本（例如 `xxx_test_data.sql`）在插入資料時，欄位值**違反了列舉 (Enum) 約束或 Bean Validation**。例如，手抖把 `status` 寫成一個 Java Enum 中不存在的值（例如 Enum 沒有 `ONBOARDING` 卻插入 `ONBOARDING`）。
*   **解決方式**：去查閱對應的 Java Enum (例如 `CourseCategory`, `EnrollmentStatus`)，確保 `.sql` 的 INSERT 資料對應值 100% 存在於該 Enum 內。修復測試資料庫腳本即可。

### (B) InvalidDataAccessApiUsageException (Parameter Type Mismatch)
*   **徵兆**：`Argument [xxxx] of type [java.lang.String] did not match parameter type [java.util.UUID]`
*   **原因**：JPA Entity 中的 PK 或是關聯鍵被定義成了 `UUID`。但在 Repository/Query 建立時 `QueryBuilder.where().eq("department_id", "字串內容")`。
*   **解決方式**：呼叫 `eq()` 或 `in()` 時，第二個參數請直接傳遞 `UUID.fromString("字串內容")` 封裝實體物件，不可直接傳 String。

### (C) ContractViolationException (資料變更合約不符)
*   **徵兆**：測試引擎報錯，表示 Snapshot 或 Expected 擷取不通過。例如：`Status expected:<PROCESSING> but was:<COMPLETED>`。
*   **原因**：業務 Pipeline (Java 實作) 的行為結果與 markdown 規格檔案內預期不符合。或者是合約已經調整更新了，但程式與舊版測試沒跟上。
*   **解決方式**：如果是舊有邏輯，更新程式碼（或 Task）。如果是規格打錯，經過衡量可以修改 Markdown 規格合約對齊要求（但要確保這符合原先的分析書要求）。

### (D) NullPointerException 或是 Security Boundary 空值與無權限
*   **徵兆**：呼叫 API 時丟出 500 操作失敗，追蹤 Log 發現 `getCurrentUserId()` 為空或是角色沒設定對。
*   **解決方式**：測試類中必須使用 `BaseContractTest` 所提供的 Context Injection 模擬認證。例如在 request 加入 `employeeId`，或者呼叫類似 `setupSecurityContext` 給予對應 mock session。遇到這類測試方法上的 `TODO: 測試失敗`，需往權限 Mock 下手解決。

## 3. 測試重跑驗證策略
測試失敗時請不要隨機盲改！
1. 透過 `mvn` 指令抓取最後的 LOG 錯誤。如果是用終端機與 PowerShell 管道，可將輸出導向到暫存文件方便觀察：
   `mvn "-Dtest=MyTest" test --projects hrms-xxx 2>&1 | Out-File "$env:TEMP\out.txt"; Get-Content "$env:TEMP\out.txt" | Select-Object -Last 100`。
2. 確認問題在 (A) 到 (D) 中屬於哪一類，並精確定點修改 `Entity` / `.sql` / `Assembler`。
3. 移除舊有的 `TODO: 測試失敗` 註解。確認測試轉綠，保證產出高品質。
