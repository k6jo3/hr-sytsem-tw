# 員工查詢業務合約 (Employee Query Contract)

> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義員工查詢 API 的業務合約，確保所有查詢都包含必要的過濾條件。

## 合約定義

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| EMP_001 | 一般員工查詢在職人員 | EMPLOYEE | `{"status":"ACTIVE"}` | `status = 'ACTIVE'`, `is_deleted = 0` |
| EMP_002 | HR 查詢特定部門 | HR | `{"deptId":"D001"}` | `department.id = 'D001'`, `is_deleted = 0` |
| EMP_003 | 管理員模糊查詢姓名 | ADMIN | `{"name":"王"}` | `name LIKE '%王%'`, `is_deleted = 0` |
| EMP_004 | 訪客只能看公開資料 | GUEST | `{}` | `visibility = 'PUBLIC'`, `is_deleted = 0` |
| EMP_005 | 查詢離職員工 | HR | `{"status":"RESIGNED"}` | `status = 'RESIGNED'`, `is_deleted = 0` |
| EMP_006 | 跨部門查詢 | MANAGER | `{"deptIds":["D001","D002"]}` | `department.id IN ('D001','D002')`, `is_deleted = 0` |

## 補充說明

### 通用規則

1. **所有查詢都必須包含 `is_deleted = 0`**：確保不會查到已刪除的資料
2. **權限過濾**：不同角色有不同的資料範圍限制
3. **狀態過濾**：明確指定狀態條件

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| EMPLOYEE | 自己所屬部門 | 僅能查看基本資訊 |
| HR | 全公司 | 可查看完整資訊 |
| ADMIN | 全公司 | 無限制 |
| MANAGER | 自己管轄部門 | 可查看所屬員工 |
| GUEST | 公開資料 | 僅限 visibility = 'PUBLIC' |
