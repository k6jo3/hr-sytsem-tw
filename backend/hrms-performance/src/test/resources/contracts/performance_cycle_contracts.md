# 績效管理服務業務合約 (Performance Service Contract)

> **服務代碼:** 08
> **版本:** 1.0
> **建立日期:** 2026-01-07
> **維護者:** Dev Team

## 概述

本文件定義績效管理服務的業務合約，涵蓋考核週期、考核記錄等查詢場景。

---

## 1. 考核週期查詢合約 (Performance Cycle Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PFM_C001 | 查詢草稿狀態的週期 | Admin | `{"status":"DRAFT"}` | `status = 'DRAFT'` |
| PFM_C002 | 查詢特定年份的週期 | Admin | `{"year":2025}` | `year = 2025` |
