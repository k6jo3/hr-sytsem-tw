# 專案管理服務業務合約 (Project Service Contract)

> **服務代碼:** 06
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義專案管理服務的業務合約，涵蓋專案、客戶、WBS、成本追蹤等查詢場景。

---

## 1. 專案查詢合約 (Project Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PRJ_P001 | 查詢進行中專案 | PM | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'` |
| PRJ_P002 | 查詢已完成專案 | PM | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| PRJ_P005 | 依名稱模糊查詢 | PM | `{"keyword":"系統"}` | `projectName LIKE '%系統%'` |

---

## 2. 客戶查詢合約 (Customer Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PRJ_C001 | 查詢有效客戶 | PM | `{"status":"ACTIVE"}` | `status = 'ACTIVE'` |
| PRJ_C002 | 依名稱模糊查詢 | PM | `{"keyword":"科技"}` | `customerName LIKE '%科技%'` |

---

## 3. WBS 查詢合約 (WBS Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| PRJ_W001 | 查詢專案 WBS | PM | `{"projectId":"P001"}` | `projectId = 'P001'` |
