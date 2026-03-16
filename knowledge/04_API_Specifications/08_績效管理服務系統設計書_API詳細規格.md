# 績效管理服務 API 詳細規格

**版本:** 1.1
**建立日期:** 2025-12-30
**最後更新:** 2026-03-16
**Domain代號:** 08 (PFM)
**服務名稱:** hrms-performance
**API 總數:** 15 個端點

---

## 目錄

1. [Controller命名對照](#1-controller命名對照)
2. [API總覽](#2-api總覽)
3. [考核週期管理 API](#3-考核週期管理-api)
4. [考核表單管理 API](#4-考核表單管理-api)
5. [考核評估 API](#5-考核評估-api)
6. [考核查詢 API](#6-考核查詢-api)
7. [考核報表 API](#7-考核報表-api)

---

## 1. Controller命名對照

| Controller | 說明 |
|:---|:---|
| `HR08CycleCmdController` | 考核週期 Command 操作 |
| `HR08CycleQryController` | 考核週期 Query 操作 |
| `HR08TemplateCmdController` | 考核表單範本 Command 操作 |
| `HR08ReviewCmdController` | 考核評估 Command 操作 |
| `HR08ReviewQryController` | 考核評估 Query 操作 |
| `HR08ReportQryController` | 考核報表 Query 操作 |

---

## 2. API總覽

### 2.1 端點清單 (15 個端點)

| # | 端點 | 方法 | Controller | 說明 | 權限 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---|:---:|
| 1 | `/api/v1/performance/cycles` | POST | HR08CycleCmdController | 建立考核週期 | performance:cycle:create | 已實現 |
| 2 | `/api/v1/performance/cycles/{id}` | PUT | HR08CycleCmdController | 更新考核週期 | performance:cycle:update | 已實現 |
| 3 | `/api/v1/performance/cycles/{id}` | DELETE | HR08CycleCmdController | 刪除考核週期 | performance:cycle:delete | 已實現 |
| 4 | `/api/v1/performance/cycles/{id}/start` | PUT | HR08CycleCmdController | 啟動考核週期 | performance:cycle:start | 已實現 |
| 5 | `/api/v1/performance/cycles/{id}/complete` | PUT | HR08CycleCmdController | 完成考核週期 | performance:cycle:complete | 已實現 |
| 6 | `/api/v1/performance/cycles` | GET | HR08CycleQryController | 查詢考核週期列表 | performance:cycle:read | 已實現 |
| 7 | `/api/v1/performance/cycles/{id}` | GET | HR08CycleQryController | 查詢考核週期詳情 | performance:cycle:read | 已實現 |
| 8 | `/api/v1/performance/cycles/{id}/template` | POST | HR08TemplateCmdController | 儲存考核表單範本 | performance:template:update | 已實現 |
| 9 | `/api/v1/performance/cycles/{id}/template/publish` | PUT | HR08TemplateCmdController | 發布考核表單 | performance:template:publish | 已實現 |
| 10 | `/api/v1/performance/reviews/{id}/submit` | POST | HR08ReviewCmdController | 提交考核評估 | - | 已實現 |
| 11 | `/api/v1/performance/reviews/{id}/finalize` | PUT | HR08ReviewCmdController | 確認最終評等 | performance:review:finalize | 已實現 |
| 12 | `/api/v1/performance/reviews/my` | GET | HR08ReviewQryController | 查詢我的考核 | - | 已實現 |
| 13 | `/api/v1/performance/reviews/team` | GET | HR08ReviewQryController | 查詢團隊考核 | performance:team:read | 已實現 |
| 14 | `/api/v1/performance/reviews/{id}` | GET | HR08ReviewQryController | 查詢考核詳情 | - | 已實現 |
| 15 | `/api/v1/performance/reports/distribution/{cycleId}` | GET | HR08ReportQryController | 查詢評等分布 | performance:report:read | 已實現 |

---

## 3. 考核週期管理 API

### 3.1 建立考核週期

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/performance/cycles` |
| Controller | `HR08CycleCmdController` |
| Service | `CreateCycleServiceImpl` |
| 權限 | `performance:cycle:create` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | HR 建立年度/季度/試用期考核週期 |
| 使用者 | HR 管理員 |
| 頁面 | HR08-P01 考核週期管理頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證週期名稱不重複 |
| 2 | 驗證考核期間合理（結束日期 > 開始日期） |
| 3 | 驗證自評截止日與主管評截止日在合理範圍 |
| 4 | 建立考核週期，狀態為 DRAFT |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| cycleName | String | ✅ | 最長 100 字元，不可重複 | 週期名稱 | `"2025年度考核"` |
| cycleType | Enum | ✅ | PROBATION/QUARTERLY/ANNUAL | 考核類型 | `"ANNUAL"` |
| startDate | Date | ✅ | 必須早於 endDate | 考核期間開始日 | `"2025-01-01"` |
| endDate | Date | ✅ | 必須晚於 startDate | 考核期間結束日 | `"2025-12-31"` |
| selfEvalDeadline | Date | ⬚ | 須在 endDate 之後 | 自評截止日 | `"2026-01-15"` |
| managerEvalDeadline | Date | ⬚ | 須在 selfEvalDeadline 之後 | 主管評截止日 | `"2026-01-31"` |

**範例：**
```json
{
  "cycleName": "2025年度考核",
  "cycleType": "ANNUAL",
  "startDate": "2025-01-01",
  "endDate": "2025-12-31",
  "selfEvalDeadline": "2026-01-15",
  "managerEvalDeadline": "2026-01-31"
}
```

**Response**

**成功回應 (201 Created)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| cycleId | UUID | 週期 ID |
| cycleName | String | 週期名稱 |
| status | Enum | 狀態 (DRAFT) |
| createdAt | DateTime | 建立時間 |

```json
{
  "code": "SUCCESS",
  "message": "考核週期建立成功",
  "data": {
    "cycleId": "550e8400-e29b-41d4-a716-446655440000",
    "cycleName": "2025年度考核",
    "cycleType": "ANNUAL",
    "status": "DRAFT",
    "createdAt": "2025-12-30T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | `PFM_INVALID_DATE_RANGE` | 考核期間無效 | 確認結束日期晚於開始日期 |
| 400 | `PFM_INVALID_DEADLINE` | 截止日期設定無效 | 確認截止日期在考核期間之後 |
| 409 | `PFM_CYCLE_NAME_EXISTS` | 週期名稱已存在 | 使用其他名稱 |

---

### 3.2 更新考核週期

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/performance/cycles/{id}` |
| Controller | `HR08CycleCmdController` |
| Service | `UpdateCycleServiceImpl` |
| 權限 | `performance:cycle:update` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | HR 修改考核週期設定 |
| 使用者 | HR 管理員 |
| 頁面 | HR08-P01 考核週期管理頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證週期存在 |
| 2 | 只有 DRAFT 狀態的週期可以修改 |
| 3 | 驗證更新內容合理性 |
| 4 | 更新週期資料 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 週期 ID | `550e8400-e29b-41d4-a716-446655440000` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| cycleName | String | ⬚ | 最長 100 字元 | 週期名稱 | `"2025年度績效考核"` |
| startDate | Date | ⬚ | - | 考核期間開始日 | `"2025-01-01"` |
| endDate | Date | ⬚ | - | 考核期間結束日 | `"2025-12-31"` |
| selfEvalDeadline | Date | ⬚ | - | 自評截止日 | `"2026-01-15"` |
| managerEvalDeadline | Date | ⬚ | - | 主管評截止日 | `"2026-01-31"` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "考核週期已更新"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_CYCLE_NOT_FOUND` | 週期不存在 | 確認週期 ID |
| 400 | `PFM_CYCLE_NOT_EDITABLE` | 週期已啟動，無法修改 | 只有 DRAFT 狀態可修改 |

---

### 3.3 刪除考核週期

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `DELETE /api/v1/performance/cycles/{id}` |
| Controller | `HR08CycleCmdController` |
| Service | `DeleteCycleServiceImpl` |
| 權限 | `performance:cycle:delete` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | HR 刪除尚未啟動的考核週期 |
| 使用者 | HR 管理員 |
| 頁面 | HR08-P01 考核週期管理頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證週期存在 |
| 2 | 只有 DRAFT 狀態的週期可以刪除 |
| 3 | 刪除週期及相關表單範本 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 週期 ID | `550e8400-e29b-41d4-a716-446655440000` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "考核週期已刪除"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_CYCLE_NOT_FOUND` | 週期不存在 | 確認週期 ID |
| 400 | `PFM_CYCLE_NOT_DELETABLE` | 週期已啟動，無法刪除 | 只有 DRAFT 狀態可刪除 |

---

### 3.4 啟動考核週期

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/performance/cycles/{id}/start` |
| Controller | `HR08CycleCmdController` |
| Service | `StartCycleServiceImpl` |
| 權限 | `performance:cycle:start` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | HR 啟動考核週期，開始讓員工進行自評 |
| 使用者 | HR 管理員 |
| 頁面 | HR08-P01 考核週期管理頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證週期存在且狀態為 DRAFT |
| 2 | 驗證考核表單已發布 |
| 3 | 更新狀態為 IN_PROGRESS |
| 4 | 發布 PerformanceCycleStartedEvent |
| 5 | 通知所有員工開始自評 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 週期 ID | `550e8400-e29b-41d4-a716-446655440000` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "考核週期已啟動",
  "data": {
    "cycleId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "IN_PROGRESS",
    "affectedEmployeeCount": 156
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_CYCLE_NOT_FOUND` | 週期不存在 | 確認週期 ID |
| 400 | `PFM_CYCLE_ALREADY_STARTED` | 週期已啟動 | 該週期已在進行中 |
| 400 | `PFM_TEMPLATE_NOT_PUBLISHED` | 表單尚未發布 | 先發布考核表單 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| PerformanceCycleStartedEvent | `performance.cycle.started` | 週期啟動，通知所有員工 |

---

### 3.5 完成考核週期

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/performance/cycles/{id}/complete` |
| Controller | `HR08CycleCmdController` |
| Service | `CompleteCycleServiceImpl` |
| 權限 | `performance:cycle:complete` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | HR 完成整個考核週期，鎖定所有考核結果 |
| 使用者 | HR 管理員 |
| 頁面 | HR08-P01 考核週期管理頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證週期存在且狀態為 IN_PROGRESS |
| 2 | 驗證所有員工考核已完成確認 |
| 3 | 更新狀態為 COMPLETED |
| 4 | 鎖定所有考核記錄 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 週期 ID | `550e8400-e29b-41d4-a716-446655440000` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| force | Boolean | ⬚ | - | 強制完成（忽略未完成的考核） | `false` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "考核週期已完成",
  "data": {
    "cycleId": "550e8400-e29b-41d4-a716-446655440000",
    "status": "COMPLETED",
    "completedCount": 156,
    "pendingCount": 0
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_CYCLE_NOT_FOUND` | 週期不存在 | 確認週期 ID |
| 400 | `PFM_CYCLE_NOT_IN_PROGRESS` | 週期未在進行中 | 只有 IN_PROGRESS 可完成 |
| 400 | `PFM_PENDING_REVIEWS_EXIST` | 尚有未完成的考核 | 完成所有考核或使用 force=true |

---

### 3.6 查詢考核週期列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/performance/cycles` |
| Controller | `HR08CycleQryController` |
| Service | `GetCyclesServiceImpl` |
| 權限 | `performance:cycle:read` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 查詢所有考核週期 |
| 使用者 | HR 管理員 |
| 頁面 | HR08-P01 考核週期管理頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 查詢所有考核週期 |
| 2 | 支援狀態篩選 |
| 3 | 依建立時間倒序排列 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| status | Enum | ⬚ | - | 狀態篩選 | `IN_PROGRESS` |
| cycleType | Enum | ⬚ | - | 類型篩選 | `ANNUAL` |
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數 | `20` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "content": [
      {
        "cycleId": "cycle-uuid-001",
        "cycleName": "2025年度考核",
        "cycleType": "ANNUAL",
        "startDate": "2025-01-01",
        "endDate": "2025-12-31",
        "selfEvalDeadline": "2026-01-15",
        "managerEvalDeadline": "2026-01-31",
        "status": "IN_PROGRESS",
        "progress": {
          "total": 156,
          "selfEvalCompleted": 120,
          "managerEvalCompleted": 80,
          "finalized": 50
        },
        "createdAt": "2025-01-01T00:00:00Z"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 5,
    "totalPages": 1
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 403 | `AUTHZ_PERMISSION_DENIED` | 無權限 | 需有 performance:cycle:read 權限 |

---

### 3.7 查詢考核週期詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/performance/cycles/{id}` |
| Controller | `HR08CycleQryController` |
| Service | `GetCycleDetailServiceImpl` |
| 權限 | `performance:cycle:read` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 查詢單一考核週期的完整資訊含表單設定 |
| 使用者 | HR 管理員 |
| 頁面 | HR08-P01、HR08-P02 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 查詢週期基本資訊 |
| 2 | 查詢關聯的表單範本 |
| 3 | 計算進度統計 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 週期 ID | `550e8400-e29b-41d4-a716-446655440000` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "cycleId": "cycle-uuid-001",
    "cycleName": "2025年度考核",
    "cycleType": "ANNUAL",
    "startDate": "2025-01-01",
    "endDate": "2025-12-31",
    "selfEvalDeadline": "2026-01-15",
    "managerEvalDeadline": "2026-01-31",
    "status": "IN_PROGRESS",
    "template": {
      "formName": "2025年度績效考核表",
      "scoringSystem": "FIVE_POINT",
      "forcedDistribution": true,
      "distributionRules": {
        "A": 10,
        "B": 30,
        "C": 50,
        "D": 8,
        "E": 2
      },
      "evaluationItems": [
        {
          "itemId": "item-uuid-001",
          "itemName": "工作品質",
          "weight": 30,
          "description": "工作成果的品質與完整度"
        },
        {
          "itemId": "item-uuid-002",
          "itemName": "專業能力",
          "weight": 25,
          "description": "專業知識與技能的展現"
        }
      ],
      "isPublished": true
    },
    "progress": {
      "total": 156,
      "selfEvalCompleted": 120,
      "managerEvalCompleted": 80,
      "finalized": 50
    }
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_CYCLE_NOT_FOUND` | 週期不存在 | 確認週期 ID |

---

## 4. 考核表單管理 API

### 4.1 儲存考核表單範本

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/performance/cycles/{id}/template` |
| Controller | `HR08TemplateCmdController` |
| Service | `SaveTemplateServiceImpl` |
| 權限 | `performance:template:update` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | HR 設計考核表單，定義評估項目與權重 |
| 使用者 | HR 管理員 |
| 頁面 | HR08-P02 考核表單設計頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證週期存在且為 DRAFT 狀態 |
| 2 | 驗證評估項目權重總和為 100% |
| 3 | 驗證強制分配比例總和為 100%（若啟用） |
| 4 | 儲存表單範本 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 週期 ID | `550e8400-e29b-41d4-a716-446655440000` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| formName | String | ✅ | 最長 100 字元 | 表單名稱 | `"2025年度績效考核表"` |
| scoringSystem | Enum | ✅ | FIVE_GRADE/FIVE_POINT/HUNDRED | 評分制度 | `"FIVE_POINT"` |
| forcedDistribution | Boolean | ✅ | - | 是否啟用強制分配 | `true` |
| distributionRules | Object | ⬚ | 各等級比例總和 = 100 | 分配比例 | 見下方範例 |
| evaluationItems | Item[] | ✅ | 權重總和 = 100 | 評估項目 | 見下方範例 |

**範例：**
```json
{
  "formName": "2025年度績效考核表",
  "scoringSystem": "FIVE_POINT",
  "forcedDistribution": true,
  "distributionRules": {
    "A": 10,
    "B": 30,
    "C": 50,
    "D": 8,
    "E": 2
  },
  "evaluationItems": [
    {
      "itemName": "工作品質",
      "weight": 30,
      "description": "工作成果的品質與完整度",
      "scoringCriteria": "1分:不合格, 2分:待改進, 3分:達標, 4分:良好, 5分:優秀"
    },
    {
      "itemName": "專業能力",
      "weight": 25,
      "description": "專業知識與技能的展現"
    },
    {
      "itemName": "團隊合作",
      "weight": 20,
      "description": "與團隊成員的協作能力"
    },
    {
      "itemName": "溝通協調",
      "weight": 15,
      "description": "跨部門溝通與協調能力"
    },
    {
      "itemName": "創新改善",
      "weight": 10,
      "description": "主動發現問題並提出改善方案"
    }
  ]
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "表單設定已儲存"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_CYCLE_NOT_FOUND` | 週期不存在 | 確認週期 ID |
| 400 | `PFM_TEMPLATE_NOT_EDITABLE` | 表單已發布，無法修改 | 已發布的表單無法修改 |
| 400 | `PFM_WEIGHT_SUM_INVALID` | 權重總和必須為 100% | 調整各項目權重 |
| 400 | `PFM_DISTRIBUTION_SUM_INVALID` | 分配比例總和必須為 100% | 調整各等級比例 |

---

### 4.2 發布考核表單

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/performance/cycles/{id}/template/publish` |
| Controller | `HR08TemplateCmdController` |
| Service | `PublishTemplateServiceImpl` |
| 權限 | `performance:template:publish` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | HR 完成表單設計後發布，發布後無法修改 |
| 使用者 | HR 管理員 |
| 頁面 | HR08-P02 考核表單設計頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證週期存在 |
| 2 | 驗證表單已設定完整（至少一個評估項目） |
| 3 | 標記表單為已發布 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 週期 ID | `550e8400-e29b-41d4-a716-446655440000` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "考核表單已發布"
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_CYCLE_NOT_FOUND` | 週期不存在 | 確認週期 ID |
| 400 | `PFM_TEMPLATE_ALREADY_PUBLISHED` | 表單已發布 | 該表單已發布 |
| 400 | `PFM_TEMPLATE_INCOMPLETE` | 表單設定不完整 | 至少設定一個評估項目 |

---

## 5. 考核評估 API

### 5.1 提交考核評估

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/performance/reviews/{id}/submit` |
| Controller | `HR08ReviewCmdController` |
| Service | `SubmitReviewServiceImpl` |
| 權限 | - (登入即可) |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工提交自評、主管提交主管評 |
| 使用者 | 所有員工、主管 |
| 頁面 | HR08-P03 我的考核頁面、HR08-P04 團隊考核頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證考核週期在進行中 |
| 2 | 驗證評估類型正確（SELF 需為本人、MANAGER 需為主管） |
| 3 | 驗證所有評估項目已填寫 |
| 4 | 計算加權總分 |
| 5 | 依評分決定評等（A/B/C/D） |
| 6 | 儲存考核記錄 |
| 7 | 發布 PerformanceReviewSubmittedEvent |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 考核記錄 ID | `review-uuid-001` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| cycleId | UUID | ✅ | 必須存在且進行中 | 考核週期 ID | `"cycle-uuid"` |
| employeeId | UUID | ✅ | SELF 需為本人 | 被考核員工 ID | `"emp-uuid"` |
| reviewType | Enum | ✅ | SELF/MANAGER/PEER | 評估類型 | `"SELF"` |
| evaluationItems | Item[] | ✅ | 所有項目須評分 | 評估項目與分數 | 見下方範例 |
| comments | String | ⬚ | 最長 2000 字元 | 評語 | `"整體表現良好"` |

**範例（自評）：**
```json
{
  "cycleId": "cycle-uuid-001",
  "employeeId": "emp-uuid-001",
  "reviewType": "SELF",
  "evaluationItems": [
    {
      "itemId": "item-uuid-001",
      "score": 4,
      "selfComment": "工作品質持續維持高水準"
    },
    {
      "itemId": "item-uuid-002",
      "score": 4,
      "selfComment": "積極學習新技術"
    },
    {
      "itemId": "item-uuid-003",
      "score": 5,
      "selfComment": "與團隊合作順暢"
    },
    {
      "itemId": "item-uuid-004",
      "score": 4,
      "selfComment": "跨部門溝通良好"
    },
    {
      "itemId": "item-uuid-005",
      "score": 3,
      "selfComment": "持續尋找改善機會"
    }
  ],
  "comments": "本年度完成多項重要專案，持續精進專業能力"
}
```

**範例（主管評）：**
```json
{
  "cycleId": "cycle-uuid-001",
  "employeeId": "emp-uuid-001",
  "reviewType": "MANAGER",
  "evaluationItems": [
    {
      "itemId": "item-uuid-001",
      "score": 4,
      "managerComment": "工作成果品質優良"
    },
    {
      "itemId": "item-uuid-002",
      "score": 3,
      "managerComment": "專業能力符合預期"
    },
    {
      "itemId": "item-uuid-003",
      "score": 4,
      "managerComment": "團隊合作積極"
    },
    {
      "itemId": "item-uuid-004",
      "score": 4,
      "managerComment": "溝通能力良好"
    },
    {
      "itemId": "item-uuid-005",
      "score": 3,
      "managerComment": "有改善意識，可再加強"
    }
  ],
  "comments": "整體表現良好，建議加強專案管理能力"
}
```

**Response**

**成功回應 (201 Created)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| reviewId | UUID | 考核記錄 ID |
| overallScore | Decimal | 加權總分 |
| overallRating | String | 評等 (A/B/C/D) |
| nextStep | String | 下一步驟說明 |

```json
{
  "code": "SUCCESS",
  "message": "考核評估已提交",
  "data": {
    "reviewId": "review-uuid-001",
    "overallScore": 4.1,
    "overallRating": "B",
    "nextStep": "等待主管評核"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_CYCLE_NOT_FOUND` | 週期不存在 | 確認週期 ID |
| 400 | `PFM_CYCLE_NOT_IN_PROGRESS` | 週期未在進行中 | 確認週期狀態 |
| 400 | `PFM_SELF_EVAL_NOT_OWNER` | 自評必須為本人 | 只能為自己提交自評 |
| 400 | `PFM_MANAGER_EVAL_NOT_SUPERVISOR` | 非該員工的主管 | 只能評核直屬部屬 |
| 400 | `PFM_SELF_EVAL_NOT_COMPLETED` | 員工尚未完成自評 | 主管評須等自評完成 |
| 400 | `PFM_INCOMPLETE_ITEMS` | 有未評分的項目 | 完成所有項目評分 |
| 400 | `PFM_DEADLINE_PASSED` | 已超過截止日 | 聯繫 HR 處理 |
| 409 | `PFM_REVIEW_EXISTS` | 已提交過此類型評估 | 同類型評估只能提交一次 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| PerformanceReviewSubmittedEvent | `performance.review.submitted` | 評估提交，通知下一階段 |

---

### 5.2 確認最終評等

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/performance/reviews/{id}/finalize` |
| Controller | `HR08ReviewCmdController` |
| Service | `FinalizeReviewServiceImpl` |
| 權限 | `performance:review:finalize` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | HR 或二級主管確認員工最終評等 |
| 使用者 | HR、二級主管 |
| 頁面 | HR08-P04 團隊考核頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證考核記錄存在 |
| 2 | 驗證主管評已完成 |
| 3 | 設定最終評等（可調整） |
| 4 | 標記為已確認 |
| 5 | 發布 PerformanceReviewCompletedEvent |
| 6 | 通知薪資服務參考調薪 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 考核記錄 ID | `review-uuid-001` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| finalScore | Decimal | ⬚ | 0-5 或 0-100 依評分制度 | 最終分數（可調整） | `3.9` |
| finalRating | String | ✅ | A/B/C/D/E | 最終評等 | `"B"` |
| adjustmentReason | String | ⬚ | 最長 500 字元 | 調整原因（若與系統計算不同） | `"考量年資調整"` |

**範例：**
```json
{
  "finalScore": 3.9,
  "finalRating": "B",
  "adjustmentReason": null
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "考核已確認",
  "data": {
    "reviewId": "review-uuid-001",
    "employeeId": "emp-uuid-001",
    "employeeName": "張三",
    "finalScore": 3.9,
    "finalRating": "B",
    "status": "FINALIZED"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_REVIEW_NOT_FOUND` | 考核記錄不存在 | 確認記錄 ID |
| 400 | `PFM_MANAGER_EVAL_NOT_COMPLETED` | 主管評未完成 | 等待主管完成評核 |
| 400 | `PFM_ALREADY_FINALIZED` | 已確認過 | 此考核已完成確認 |
| 400 | `PFM_INVALID_RATING` | 評等無效 | 使用有效的評等值 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| PerformanceReviewCompletedEvent | `performance.review.completed` | 考核完成，通知薪資服務 |

---

## 6. 考核查詢 API

### 6.1 查詢我的考核

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/performance/reviews/my` |
| Controller | `HR08ReviewQryController` |
| Service | `GetMyReviewsServiceImpl` |
| 權限 | - (登入即可) |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 員工查看自己的考核歷史及當前考核狀態 |
| 使用者 | 所有員工 |
| 頁面 | HR08-P03 我的考核頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 取得當前登入者的員工 ID |
| 2 | 查詢所有相關考核週期與評估記錄 |
| 3 | 區分進行中與歷史考核 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| cycleId | UUID | ⬚ | - | 指定週期 | `cycle-uuid` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "currentCycle": {
      "cycleId": "cycle-uuid-001",
      "cycleName": "2025年度考核",
      "startDate": "2025-01-01",
      "endDate": "2025-12-31",
      "selfEvalDeadline": "2026-01-15",
      "status": "SELF_EVAL",
      "selfEvalCompleted": false,
      "managerEvalCompleted": false,
      "finalized": false,
      "template": {
        "evaluationItems": [
          {
            "itemId": "item-uuid-001",
            "itemName": "工作品質",
            "weight": 30,
            "description": "工作成果的品質與完整度"
          }
        ]
      }
    },
    "history": [
      {
        "cycleId": "cycle-uuid-000",
        "cycleName": "2024年度考核",
        "selfScore": 4.2,
        "managerScore": 4.0,
        "finalScore": 4.0,
        "finalRating": "B",
        "finalized": true,
        "completedAt": "2025-02-15T00:00:00Z"
      }
    ]
  }
}
```

---

### 6.2 查詢團隊考核

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/performance/reviews/team` |
| Controller | `HR08ReviewQryController` |
| Service | `GetTeamReviewsServiceImpl` |
| 權限 | `performance:team:read` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 主管查看團隊成員的考核狀態與結果 |
| 使用者 | 部門主管、專案經理 |
| 頁面 | HR08-P04 團隊考核頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 取得當前登入者管理的員工列表 |
| 2 | 查詢指定週期的考核記錄 |
| 3 | 計算評等分布統計 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| cycleId | UUID | ✅ | - | 考核週期 ID | `cycle-uuid` |
| status | Enum | ⬚ | - | 狀態篩選 | `PENDING_MANAGER` |
| search | String | ⬚ | - | 員工姓名搜尋 | `張` |
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數 | `20` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "cycleId": "cycle-uuid-001",
    "cycleName": "2025年度考核",
    "teamMembers": {
      "content": [
        {
          "employeeId": "emp-uuid-001",
          "employeeName": "張三",
          "employeeNumber": "E0001",
          "jobTitle": "工程師",
          "selfScore": 4.1,
          "selfRating": "B",
          "managerScore": null,
          "managerRating": null,
          "finalScore": null,
          "finalRating": null,
          "status": "PENDING_MANAGER",
          "selfEvalSubmittedAt": "2025-12-20T10:00:00Z"
        },
        {
          "employeeId": "emp-uuid-002",
          "employeeName": "李四",
          "employeeNumber": "E0002",
          "jobTitle": "資深工程師",
          "selfScore": 4.5,
          "selfRating": "A",
          "managerScore": 4.3,
          "managerRating": "B",
          "finalScore": null,
          "finalRating": null,
          "status": "PENDING_FINALIZE",
          "selfEvalSubmittedAt": "2025-12-19T14:00:00Z",
          "managerEvalSubmittedAt": "2025-12-25T11:00:00Z"
        }
      ],
      "page": 1,
      "size": 10,
      "totalElements": 10,
      "totalPages": 1
    },
    "distribution": {
      "A": {"count": 1, "percentage": 10},
      "B": {"count": 3, "percentage": 30},
      "C": {"count": 5, "percentage": 50},
      "D": {"count": 1, "percentage": 10}
    },
    "distributionCompliance": {
      "isCompliant": true,
      "violations": []
    }
  }
}
```

**考核狀態說明**

| 狀態 | 說明 |
|:---|:---|
| `PENDING_SELF` | 等待自評 |
| `PENDING_MANAGER` | 等待主管評 |
| `PENDING_FINALIZE` | 等待確認最終評等 |
| `FINALIZED` | 已完成 |

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_CYCLE_NOT_FOUND` | 週期不存在 | 確認週期 ID |
| 403 | `PFM_NOT_SUPERVISOR` | 非主管身份 | 需為部門主管 |

---

### 6.3 查詢考核詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/performance/reviews/{id}` |
| Controller | `HR08ReviewQryController` |
| Service | `GetReviewDetailServiceImpl` |
| 權限 | - (限本人或主管) |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | 查看單筆考核的完整詳情 |
| 使用者 | 員工本人、直屬主管、HR |
| 頁面 | HR08-P03、HR08-P04 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 驗證考核記錄存在 |
| 2 | 驗證查詢者權限（本人、主管、HR） |
| 3 | 回傳完整考核資料 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 考核記錄 ID | `review-uuid-001` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "reviewId": "review-uuid-001",
    "cycle": {
      "cycleId": "cycle-uuid-001",
      "cycleName": "2025年度考核",
      "cycleType": "ANNUAL"
    },
    "employee": {
      "employeeId": "emp-uuid-001",
      "employeeName": "張三",
      "employeeNumber": "E0001",
      "department": "研發部",
      "jobTitle": "工程師"
    },
    "selfEval": {
      "submittedAt": "2025-12-20T10:00:00Z",
      "overallScore": 4.1,
      "overallRating": "B",
      "items": [
        {
          "itemId": "item-uuid-001",
          "itemName": "工作品質",
          "weight": 30,
          "score": 4,
          "selfComment": "工作品質持續維持高水準"
        }
      ],
      "comments": "本年度完成多項重要專案"
    },
    "managerEval": {
      "reviewerId": "mgr-uuid-001",
      "reviewerName": "王主管",
      "submittedAt": "2025-12-25T11:00:00Z",
      "overallScore": 3.9,
      "overallRating": "B",
      "items": [
        {
          "itemId": "item-uuid-001",
          "itemName": "工作品質",
          "weight": 30,
          "score": 4,
          "managerComment": "工作成果品質優良"
        }
      ],
      "comments": "整體表現良好，建議加強專案管理能力"
    },
    "finalResult": {
      "finalScore": 3.9,
      "finalRating": "B",
      "confirmedBy": "hr-uuid-001",
      "confirmedByName": "HR Admin",
      "confirmedAt": "2025-12-28T15:00:00Z",
      "adjustmentReason": null
    },
    "status": "FINALIZED"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_REVIEW_NOT_FOUND` | 考核記錄不存在 | 確認記錄 ID |
| 403 | `PFM_ACCESS_DENIED` | 無權限查看 | 需為本人、主管或 HR |

---

## 7. 考核報表 API

### 7.1 查詢評等分布

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/performance/reports/distribution/{cycleId}` |
| Controller | `HR08ReportQryController` |
| Service | `GetDistributionServiceImpl` |
| 權限 | `performance:report:read` |
| 版本 | v1 |

**用途說明**

| 項目 | 說明 |
|:---|:---|
| 業務場景 | HR 查看整體考核結果分布與統計 |
| 使用者 | HR 管理員、高階主管 |
| 頁面 | HR08-P05 考核結果分析頁面 |

**業務邏輯**

| 步驟 | 處理邏輯 |
|:---|:---|
| 1 | 查詢指定週期的所有已確認考核 |
| 2 | 計算評等分布 |
| 3 | 計算部門平均分數 |
| 4 | 計算與強制分配的差異 |

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| cycleId | UUID | ✅ | 考核週期 ID | `cycle-uuid` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| departmentId | UUID | ⬚ | - | 部門篩選 | `dept-uuid` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "data": {
    "cycle": {
      "cycleId": "cycle-uuid-001",
      "cycleName": "2025年度考核"
    },
    "summary": {
      "totalEmployees": 156,
      "completedCount": 150,
      "completionRate": 96.2,
      "averageScore": 3.8
    },
    "distribution": {
      "A": {"count": 23, "percentage": 15.3, "target": 10},
      "B": {"count": 45, "percentage": 30.0, "target": 30},
      "C": {"count": 75, "percentage": 50.0, "target": 50},
      "D": {"count": 7, "percentage": 4.7, "target": 8},
      "E": {"count": 0, "percentage": 0.0, "target": 2}
    },
    "byDepartment": [
      {
        "departmentId": "dept-uuid-001",
        "departmentName": "研發部",
        "employeeCount": 50,
        "averageScore": 4.1,
        "distribution": {
          "A": 8,
          "B": 15,
          "C": 25,
          "D": 2
        }
      },
      {
        "departmentId": "dept-uuid-002",
        "departmentName": "業務部",
        "employeeCount": 30,
        "averageScore": 3.9,
        "distribution": {
          "A": 5,
          "B": 10,
          "C": 13,
          "D": 2
        }
      }
    ],
    "trends": [
      {"year": 2023, "averageScore": 3.6},
      {"year": 2024, "averageScore": 3.7},
      {"year": 2025, "averageScore": 3.8}
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | `PFM_CYCLE_NOT_FOUND` | 週期不存在 | 確認週期 ID |
| 403 | `AUTHZ_PERMISSION_DENIED` | 無報表權限 | 需有 performance:report:read 權限 |

---

> **注意：** 原文件中的「7.2 匯出考核報表 (`GET /api/v1/performance/reports/export`)」在目前程式碼中尚未實現，已從端點清單移除。如需此功能，需先實作對應的 Controller 端點。

---

## 附錄 A：考核狀態流轉

```
┌────────────────────────────────────────────────────────────────┐
│                        考核週期狀態                              │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌────────┐  啟動   ┌─────────────┐  完成   ┌───────────┐      │
│  │ DRAFT  │───────▶│ IN_PROGRESS │───────▶│ COMPLETED │       │
│  └────────┘        └─────────────┘        └───────────┘       │
│                                                                 │
└────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────┐
│                        考核記錄狀態                              │
├────────────────────────────────────────────────────────────────┤
│                                                                 │
│  ┌──────────────┐        ┌─────────────────┐                   │
│  │ PENDING_SELF │──────▶│ PENDING_MANAGER │                   │
│  └──────────────┘        └─────────────────┘                   │
│                                 │                               │
│                                 ▼                               │
│                    ┌───────────────────┐                       │
│                    │ PENDING_FINALIZE  │                       │
│                    └───────────────────┘                       │
│                                 │                               │
│                                 ▼                               │
│                         ┌───────────┐                          │
│                         │ FINALIZED │                          │
│                         └───────────┘                          │
│                                                                 │
└────────────────────────────────────────────────────────────────┘
```

---

## 附錄 B：錯誤碼總覽

| 錯誤碼 | HTTP | 說明 |
|:---|:---:|:---|
| `PFM_CYCLE_NOT_FOUND` | 404 | 考核週期不存在 |
| `PFM_REVIEW_NOT_FOUND` | 404 | 考核記錄不存在 |
| `PFM_CYCLE_NAME_EXISTS` | 409 | 週期名稱已存在 |
| `PFM_CYCLE_NOT_EDITABLE` | 400 | 週期已啟動，無法修改 |
| `PFM_CYCLE_NOT_DELETABLE` | 400 | 週期已啟動，無法刪除 |
| `PFM_CYCLE_ALREADY_STARTED` | 400 | 週期已啟動 |
| `PFM_CYCLE_NOT_IN_PROGRESS` | 400 | 週期未在進行中 |
| `PFM_TEMPLATE_NOT_PUBLISHED` | 400 | 表單尚未發布 |
| `PFM_TEMPLATE_NOT_EDITABLE` | 400 | 表單已發布，無法修改 |
| `PFM_TEMPLATE_ALREADY_PUBLISHED` | 400 | 表單已發布 |
| `PFM_TEMPLATE_INCOMPLETE` | 400 | 表單設定不完整 |
| `PFM_WEIGHT_SUM_INVALID` | 400 | 權重總和必須為 100% |
| `PFM_DISTRIBUTION_SUM_INVALID` | 400 | 分配比例總和必須為 100% |
| `PFM_INVALID_DATE_RANGE` | 400 | 日期範圍無效 |
| `PFM_INVALID_DEADLINE` | 400 | 截止日期設定無效 |
| `PFM_PENDING_REVIEWS_EXIST` | 400 | 尚有未完成的考核 |
| `PFM_SELF_EVAL_NOT_OWNER` | 400 | 自評必須為本人 |
| `PFM_MANAGER_EVAL_NOT_SUPERVISOR` | 400 | 非該員工的主管 |
| `PFM_SELF_EVAL_NOT_COMPLETED` | 400 | 員工尚未完成自評 |
| `PFM_MANAGER_EVAL_NOT_COMPLETED` | 400 | 主管評未完成 |
| `PFM_INCOMPLETE_ITEMS` | 400 | 有未評分的項目 |
| `PFM_DEADLINE_PASSED` | 400 | 已超過截止日 |
| `PFM_ALREADY_FINALIZED` | 400 | 已確認過 |
| `PFM_INVALID_RATING` | 400 | 評等無效 |
| `PFM_REVIEW_EXISTS` | 409 | 已提交過此類型評估 |
| `PFM_NOT_SUPERVISOR` | 403 | 非主管身份 |
| `PFM_ACCESS_DENIED` | 403 | 無權限查看 |

---

## 附錄 C：領域事件總覽

| 事件名稱 | Topic | 觸發時機 | 訂閱服務 |
|:---|:---|:---|:---|
| PerformanceCycleStartedEvent | `performance.cycle.started` | 啟動考核週期 | Notification |
| PerformanceReviewSubmittedEvent | `performance.review.submitted` | 提交考核評估 | Notification |
| PerformanceReviewCompletedEvent | `performance.review.completed` | 確認最終評等 | Payroll, Notification, Reporting |

---

## 附錄 D：評分制度說明

| 制度 | 說明 | 評等對應 |
|:---|:---|:---|
| FIVE_GRADE | 五等第制 | A/B/C/D/E 直接評選 |
| FIVE_POINT | 五分制 (1-5分) | ≥4.5=A, ≥3.5=B, ≥2.5=C, <2.5=D |
| HUNDRED | 百分制 (0-100) | ≥90=A, ≥70=B, ≥60=C, <60=D |

---

**文件建立日期:** 2025-12-30
**最後更新:** 2026-03-16
**版本:** 1.1
**API 總數:** 15 個端點
**變更說明:** v1.1 - 修正 4 個 API 路徑與實際程式碼一致 (reviews/my, reviews/team, reviews/{id}/submit, reports/distribution/{cycleId})，修正 saveTemplate HTTP 方法 (PUT -> POST)，移除未實現的 export 端點 (16 -> 15 個端點)
