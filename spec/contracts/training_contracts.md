# 教育訓練服務業務合約 (Training Service Contract)

> **服務代碼:** 10
> **版本:** 1.0
> **建立日期:** 2025-12-19
> **維護者:** SA Team

## 概述

本文件定義教育訓練服務的業務合約，涵蓋課程管理、報名、認證等查詢場景。

---

## 1. 課程查詢合約 (Course Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| TRN_C001 | 查詢開放報名課程 | EMPLOYEE | `{"status":"OPEN"}` | `status = 'OPEN'`, `is_deleted = 0` |
| TRN_C002 | 查詢進行中課程 | HR | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'`, `is_deleted = 0` |
| TRN_C003 | 查詢已結束課程 | HR | `{"status":"COMPLETED"}` | `status = 'COMPLETED'`, `is_deleted = 0` |
| TRN_C004 | 依類型查詢 | EMPLOYEE | `{"type":"MANDATORY"}` | `type = 'MANDATORY'`, `is_deleted = 0` |
| TRN_C005 | 依類別查詢 | EMPLOYEE | `{"category":"TECHNICAL"}` | `category = 'TECHNICAL'`, `is_deleted = 0` |
| TRN_C006 | 依名稱模糊查詢 | EMPLOYEE | `{"name":"領導"}` | `name LIKE '領導'`, `is_deleted = 0` |
| TRN_C007 | 查詢線上課程 | EMPLOYEE | `{"mode":"ONLINE"}` | `mode = 'ONLINE'`, `is_deleted = 0` |
| TRN_C008 | 查詢實體課程 | EMPLOYEE | `{"mode":"OFFLINE"}` | `mode = 'OFFLINE'`, `is_deleted = 0` |
| TRN_C009 | 依講師查詢 | HR | `{"instructorId":"E001"}` | `instructor_id = 'E001'`, `is_deleted = 0` |

---

## 2. 報名紀錄查詢合約 (Enrollment Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| TRN_E001 | 查詢課程報名 | HR | `{"courseId":"C001"}` | `course_id = 'C001'`, `is_deleted = 0` |
| TRN_E002 | 查詢員工報名 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| TRN_E003 | 查詢待審核報名 | HR | `{"status":"PENDING"}` | `status = 'PENDING'`, `is_deleted = 0` |
| TRN_E004 | 查詢已核准報名 | HR | `{"status":"APPROVED"}` | `status = 'APPROVED'`, `is_deleted = 0` |
| TRN_E005 | 員工查詢自己報名 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |
| TRN_E006 | 主管查詢下屬報名 | MANAGER | `{}` | `employee.department_id IN ('{managedDeptIds}')`, `is_deleted = 0` |
| TRN_E007 | 查詢已完成課程 | EMPLOYEE | `{"status":"COMPLETED"}` | `employee_id = '{currentUserId}'`, `status = 'COMPLETED'`, `is_deleted = 0` |

---

## 3. 認證查詢合約 (Certification Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| TRN_CT001 | 查詢員工認證 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = 0` |
| TRN_CT002 | 查詢有效認證 | HR | `{"status":"VALID"}` | `status = 'VALID'`, `is_deleted = 0` |
| TRN_CT003 | 查詢即將到期認證 | HR | `{"expiringWithin":30}` | `expiry_date <= '{today+30days}'`, `status = 'VALID'`, `is_deleted = 0` |
| TRN_CT004 | 查詢已過期認證 | HR | `{"status":"EXPIRED"}` | `status = 'EXPIRED'`, `is_deleted = 0` |
| TRN_CT005 | 員工查詢自己認證 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'`, `is_deleted = 0` |
| TRN_CT006 | 依認證類型查詢 | HR | `{"certType":"PROFESSIONAL"}` | `cert_type = 'PROFESSIONAL'`, `is_deleted = 0` |

---

## 4. 訓練紀錄查詢合約 (Training Record Query Contract)

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 (Required Filters) |
| :--- | :--- | :--- | :--- | :--- |
| TRN_R001 | 查詢員工訓練紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| TRN_R002 | 查詢年度訓練時數 | HR | `{"year":"2025"}` | `year = 2025` |
| TRN_R003 | 員工查詢自己紀錄 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'` |
| TRN_R004 | 查詢部門訓練紀錄 | HR | `{"deptId":"D001"}` | `department_id = 'D001'` |

---

## 補充說明

### 通用安全規則

1. **軟刪除過濾**: 主檔查詢須包含 `is_deleted = 0`
2. **個人資料**: 員工只能查詢自己的訓練紀錄
3. **主管權限**: 主管可查詢下屬訓練報名

### 課程類型代碼

| 代碼 | 說明 |
|:---|:---|
| MANDATORY | 必修 |
| OPTIONAL | 選修 |
| ONBOARDING | 新人訓練 |

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR | 全公司訓練 | 完整管理權限 |
| MANAGER | 下屬訓練紀錄 | 可審核下屬報名 |
| EMPLOYEE | 僅自己 | 可報名、查詢自己紀錄 |
