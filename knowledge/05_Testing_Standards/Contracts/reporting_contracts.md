# 報表服務 (Reporting Service) 合約規格

> 版本: 1.0
> 最後更新: 2025-12-19
> 狀態: 草稿

---

## 1. 概述

本文件定義報表服務的查詢合約規格，包含儀表板指標、報表定義、排程報表、資料匯出等查詢場景的必要篩選條件。

---

## 2. 儀表板指標查詢合約

### 2.1 儀表板定義查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-DASH-001 | 查詢租戶儀表板清單 | tenantId = :tenantId |
| RPT-DASH-002 | 查詢指定儀表板 | tenantId = :tenantId, dashboardId = :dashboardId |
| RPT-DASH-003 | 查詢使用者可見儀表板 | tenantId = :tenantId, visibleToUserId = :userId |
| RPT-DASH-004 | 查詢部門儀表板 | tenantId = :tenantId, departmentId = :departmentId |
| RPT-DASH-005 | 查詢儀表板依類型 | tenantId = :tenantId, dashboardType = :type |

### 2.2 儀表板 Widget 查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-WDGT-001 | 查詢儀表板 Widget 清單 | tenantId = :tenantId, dashboardId = :dashboardId |
| RPT-WDGT-002 | 查詢指定 Widget | tenantId = :tenantId, widgetId = :widgetId |
| RPT-WDGT-003 | 查詢 Widget 依資料來源 | tenantId = :tenantId, dataSourceType = :sourceType |

---

## 3. 報表定義查詢合約

### 3.1 報表範本查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-TMPL-001 | 查詢報表範本清單 | tenantId = :tenantId |
| RPT-TMPL-002 | 查詢指定報表範本 | tenantId = :tenantId, templateId = :templateId |
| RPT-TMPL-003 | 查詢範本依分類 | tenantId = :tenantId, category = :category |
| RPT-TMPL-004 | 查詢範本依輸出格式 | tenantId = :tenantId, outputFormat = :format |
| RPT-TMPL-005 | 搜尋報表範本 | tenantId = :tenantId, templateName LIKE :keyword |
| RPT-TMPL-006 | 查詢已啟用範本 | tenantId = :tenantId, status = ACTIVE |

### 3.2 報表參數查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-PARM-001 | 查詢報表參數定義 | tenantId = :tenantId, templateId = :templateId |
| RPT-PARM-002 | 查詢必填參數 | tenantId = :tenantId, templateId = :templateId, required = true |

---

## 4. 報表執行查詢合約

### 4.1 報表執行記錄查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-EXEC-001 | 查詢報表執行記錄 | tenantId = :tenantId |
| RPT-EXEC-002 | 查詢指定執行記錄 | tenantId = :tenantId, executionId = :executionId |
| RPT-EXEC-003 | 查詢使用者執行記錄 | tenantId = :tenantId, executedBy = :userId |
| RPT-EXEC-004 | 查詢範本執行歷史 | tenantId = :tenantId, templateId = :templateId |
| RPT-EXEC-005 | 查詢執行中報表 | tenantId = :tenantId, status = RUNNING |
| RPT-EXEC-006 | 查詢失敗執行記錄 | tenantId = :tenantId, status = FAILED |
| RPT-EXEC-007 | 查詢日期區間執行記錄 | tenantId = :tenantId, executedAt >= :startDate, executedAt <= :endDate |

### 4.2 報表輸出查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-OUTP-001 | 查詢報表輸出檔案 | tenantId = :tenantId, executionId = :executionId |
| RPT-OUTP-002 | 查詢使用者報表下載 | tenantId = :tenantId, downloadedBy = :userId |
| RPT-OUTP-003 | 查詢過期輸出檔案 | tenantId = :tenantId, expiredAt < :currentDate |

---

## 5. 排程報表查詢合約

### 5.1 排程定義查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-SCHD-001 | 查詢排程報表清單 | tenantId = :tenantId |
| RPT-SCHD-002 | 查詢指定排程 | tenantId = :tenantId, scheduleId = :scheduleId |
| RPT-SCHD-003 | 查詢範本排程 | tenantId = :tenantId, templateId = :templateId |
| RPT-SCHD-004 | 查詢已啟用排程 | tenantId = :tenantId, enabled = true |
| RPT-SCHD-005 | 查詢使用者建立排程 | tenantId = :tenantId, createdBy = :userId |
| RPT-SCHD-006 | 查詢排程依頻率 | tenantId = :tenantId, frequency = :frequency |

### 5.2 排程執行歷史查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-SHST-001 | 查詢排程執行歷史 | tenantId = :tenantId, scheduleId = :scheduleId |
| RPT-SHST-002 | 查詢排程失敗記錄 | tenantId = :tenantId, scheduleId = :scheduleId, status = FAILED |
| RPT-SHST-003 | 查詢今日排程執行 | tenantId = :tenantId, executedAt >= :todayStart, executedAt <= :todayEnd |

---

## 6. 資料匯出查詢合約

### 6.1 匯出任務查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-EXPT-001 | 查詢匯出任務清單 | tenantId = :tenantId |
| RPT-EXPT-002 | 查詢指定匯出任務 | tenantId = :tenantId, exportId = :exportId |
| RPT-EXPT-003 | 查詢使用者匯出任務 | tenantId = :tenantId, requestedBy = :userId |
| RPT-EXPT-004 | 查詢匯出任務依狀態 | tenantId = :tenantId, status = :status |
| RPT-EXPT-005 | 查詢匯出任務依資料類型 | tenantId = :tenantId, dataType = :dataType |
| RPT-EXPT-006 | 查詢待處理匯出任務 | tenantId = :tenantId, status = PENDING |

### 6.2 匯入任務查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-IMPT-001 | 查詢匯入任務清單 | tenantId = :tenantId |
| RPT-IMPT-002 | 查詢指定匯入任務 | tenantId = :tenantId, importId = :importId |
| RPT-IMPT-003 | 查詢使用者匯入任務 | tenantId = :tenantId, requestedBy = :userId |
| RPT-IMPT-004 | 查詢匯入錯誤記錄 | tenantId = :tenantId, importId = :importId, hasErrors = true |

---

## 7. 統計分析查詢合約

### 7.1 人力統計查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-HRST-001 | 查詢人力統計摘要 | tenantId = :tenantId, statisticsDate = :date |
| RPT-HRST-002 | 查詢部門人力統計 | tenantId = :tenantId, departmentId = :departmentId |
| RPT-HRST-003 | 查詢人力趨勢 | tenantId = :tenantId, periodStart >= :startDate, periodEnd <= :endDate |
| RPT-HRST-004 | 查詢離職率統計 | tenantId = :tenantId, statisticsType = TURNOVER_RATE, year = :year |

### 7.2 出勤統計查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-ATST-001 | 查詢出勤統計摘要 | tenantId = :tenantId, statisticsMonth = :month |
| RPT-ATST-002 | 查詢部門出勤統計 | tenantId = :tenantId, departmentId = :departmentId, statisticsMonth = :month |
| RPT-ATST-003 | 查詢異常出勤統計 | tenantId = :tenantId, statisticsMonth = :month, anomalyType = :type |

### 7.3 薪資統計查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-PYST-001 | 查詢薪資統計摘要 | tenantId = :tenantId, statisticsMonth = :month |
| RPT-PYST-002 | 查詢部門薪資統計 | tenantId = :tenantId, departmentId = :departmentId, statisticsMonth = :month |
| RPT-PYST-003 | 查詢薪資趨勢 | tenantId = :tenantId, periodStart >= :startDate, periodEnd <= :endDate |

### 7.4 專案成本統計查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-PRST-001 | 查詢專案成本摘要 | tenantId = :tenantId, projectId = :projectId |
| RPT-PRST-002 | 查詢客戶專案成本 | tenantId = :tenantId, customerId = :customerId |
| RPT-PRST-003 | 查詢成本超支專案 | tenantId = :tenantId, budgetStatus = OVER_BUDGET |
| RPT-PRST-004 | 查詢專案成本趨勢 | tenantId = :tenantId, projectId = :projectId, periodStart >= :startDate, periodEnd <= :endDate |

---

## 8. 法規報表查詢合約

### 8.1 勞動法規報表查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-LBRG-001 | 查詢勞動統計報表 | tenantId = :tenantId, reportYear = :year |
| RPT-LBRG-002 | 查詢加班時數統計 | tenantId = :tenantId, statisticsMonth = :month |
| RPT-LBRG-003 | 查詢勞工保險申報資料 | tenantId = :tenantId, reportMonth = :month |
| RPT-LBRG-004 | 查詢健保申報資料 | tenantId = :tenantId, reportMonth = :month |

### 8.2 稅務報表查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-TAXR-001 | 查詢扣繳憑單資料 | tenantId = :tenantId, taxYear = :year |
| RPT-TAXR-002 | 查詢員工扣繳憑單 | tenantId = :tenantId, employeeId = :employeeId, taxYear = :year |
| RPT-TAXR-003 | 查詢各類所得申報 | tenantId = :tenantId, incomeType = :type, reportMonth = :month |

---

## 9. 資料來源查詢合約

### 9.1 資料來源定義查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-DSRC-001 | 查詢資料來源清單 | tenantId = :tenantId |
| RPT-DSRC-002 | 查詢指定資料來源 | tenantId = :tenantId, dataSourceId = :dataSourceId |
| RPT-DSRC-003 | 查詢資料來源依類型 | tenantId = :tenantId, sourceType = :type |
| RPT-DSRC-004 | 查詢已啟用資料來源 | tenantId = :tenantId, status = ACTIVE |

### 9.2 資料快取查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-CACH-001 | 查詢快取狀態 | tenantId = :tenantId, dataSourceId = :dataSourceId |
| RPT-CACH-002 | 查詢過期快取 | tenantId = :tenantId, expiredAt < :currentTime |
| RPT-CACH-003 | 查詢快取更新記錄 | tenantId = :tenantId, dataSourceId = :dataSourceId |

---

## 10. 使用者報表偏好查詢合約

### 10.1 報表偏好查詢

| 場景 ID | 場景描述 | 必要篩選條件 |
|---------|----------|--------------|
| RPT-PREF-001 | 查詢使用者報表偏好 | tenantId = :tenantId, userId = :userId |
| RPT-PREF-002 | 查詢使用者最愛報表 | tenantId = :tenantId, userId = :userId, isFavorite = true |
| RPT-PREF-003 | 查詢使用者最近報表 | tenantId = :tenantId, userId = :userId |
| RPT-PREF-004 | 查詢報表訂閱設定 | tenantId = :tenantId, userId = :userId, hasSubscription = true |

---

## 11. 合約場景總覽

| 領域 | 場景數量 | 場景 ID 前綴 |
|------|----------|--------------|
| 儀表板定義 | 5 | RPT-DASH |
| 儀表板 Widget | 3 | RPT-WDGT |
| 報表範本 | 6 | RPT-TMPL |
| 報表參數 | 2 | RPT-PARM |
| 報表執行記錄 | 7 | RPT-EXEC |
| 報表輸出 | 3 | RPT-OUTP |
| 排程定義 | 6 | RPT-SCHD |
| 排程執行歷史 | 3 | RPT-SHST |
| 匯出任務 | 6 | RPT-EXPT |
| 匯入任務 | 4 | RPT-IMPT |
| 人力統計 | 4 | RPT-HRST |
| 出勤統計 | 3 | RPT-ATST |
| 薪資統計 | 3 | RPT-PYST |
| 專案成本統計 | 4 | RPT-PRST |
| 勞動法規報表 | 4 | RPT-LBRG |
| 稅務報表 | 3 | RPT-TAXR |
| 資料來源 | 4 | RPT-DSRC |
| 資料快取 | 3 | RPT-CACH |
| 使用者報表偏好 | 4 | RPT-PREF |
| **總計** | **77** | - |

---

## 12. 變更記錄

| 版本 | 日期 | 變更內容 | 作者 |
|------|------|----------|------|
| 1.0 | 2025-12-19 | 初版建立 | System |
