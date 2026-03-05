# HR08 績效管理服務業務合約

> **服務代碼:** HR08
> **服務名稱:** 績效管理服務 (Performance Management)
> **版本:** 1.0
> **更新日期:** 2026-02-23

---

## 概述

績效管理服務負責考核週期管理、考核表單範本設計、員工自評/主管評核、最終評等確認及績效分布統計等功能。支援試用期考核、季度考核、年度考核三種類型。

---

## API 端點概覽

### 考核週期管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/performance/cycles` | POST | PFM_CMD_C001 | 建立考核週期 | ✅ 已實作 |
| 2 | `PUT /api/v1/performance/cycles/{id}` | PUT | PFM_CMD_C002 | 更新考核週期 | ✅ 已實作 |
| 3 | `DELETE /api/v1/performance/cycles/{id}` | DELETE | PFM_CMD_C003 | 刪除考核週期 | ✅ 已實作 |
| 4 | `PUT /api/v1/performance/cycles/{id}/start` | PUT | PFM_CMD_C004 | 啟動考核週期 | ✅ 已實作 |
| 5 | `PUT /api/v1/performance/cycles/{id}/complete` | PUT | PFM_CMD_C005 | 完成考核週期 | ✅ 已實作 |
| 6 | `GET /api/v1/performance/cycles` | GET | PFM_C001~C007 | 查詢考核週期列表 | ✅ 已實作 |
| 7 | `GET /api/v1/performance/cycles/{id}` | GET | - | 查詢考核週期詳情 | ✅ 已實作 |

### 考核範本管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/performance/cycles/{id}/template` | POST | PFM_CMD_T001 | 儲存考核範本 | ✅ 已實作 |
| 2 | `PUT /api/v1/performance/cycles/{id}/template/publish` | PUT | PFM_CMD_T002 | 發布考核範本 | ✅ 已實作 |

### 考核維護 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/performance/reviews/{id}/submit` | POST | PFM_CMD_R001 | 提交考核 | ✅ 已實作 |
| 2 | `PUT /api/v1/performance/reviews/{id}/finalize` | PUT | PFM_CMD_R002 | 確認最終評等 | ✅ 已實作 |
| 3 | `GET /api/v1/performance/reviews/my` | GET | PFM_R001~R004 | 查詢我的考核列表 | ✅ 已實作 |
| 4 | `GET /api/v1/performance/reviews/team` | GET | PFM_T001~T003 | 查詢團隊考核列表 | ✅ 已實作 |
| 5 | `GET /api/v1/performance/reviews/{id}` | GET | - | 查詢考核詳情 | ✅ 已實作 |
| 6 | `GET /api/v1/performance/cycles/{id}/distribution` | GET | - | 查詢績效分布 | ✅ 已實作 |

**總計：16 個 API 端點**

**場景分類：**
- **Command 操作：** 7 個（5 週期 + 2 範本）+ 2 個（考核維護）
- **Query 操作：** 14 個（7 週期 + 4 我的考核 + 3 團隊考核）

---

## 1. Command 操作業務合約

### 1.1 考核週期管理

#### PFM_CMD_C001: 建立考核週期

**API 端點：** `POST /api/v1/performance/cycles`

**業務場景描述：**

HR 建立新的考核週期，定義考核名稱、類型（試用期/季度/年度）、年份、起訖日期及自評/主管評核截止日。建立時狀態為 DRAFT。

**測試合約：**

```json
{
  "scenarioId": "PFM_CMD_C001",
  "apiEndpoint": "POST /api/v1/performance/cycles",
  "controller": "HR08CycleCmdController",
  "service": "CreateCycleServiceImpl",
  "permission": "performance:cycle:create",
  "request": {
    "cycleName": "2025年度考核",
    "cycleType": "ANNUAL",
    "year": 2025,
    "startDate": "2025-12-01",
    "endDate": "2026-01-31",
    "selfEvalDeadline": "2025-12-31",
    "managerEvalDeadline": "2026-01-15"
  },
  "businessRules": [
    "考核週期名稱不可為空",
    "結束日期必須在開始日期之後",
    "自評截止日必須在週期期間內",
    "主管評核截止日必須在自評截止日之後",
    "建立時狀態為 DRAFT"
  ],
  "expectedDataChanges": {
    "tables": ["performance_cycle"],
    "operations": [
      {
        "type": "INSERT",
        "table": "performance_cycle",
        "expectedFields": {
          "cycle_name": "2025年度考核",
          "cycle_type": "ANNUAL",
          "year": 2025,
          "status": "DRAFT",
          "is_deleted": 0
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "cycleId", "type": "string", "notNull": true}
    ]
  }
}
```

#### PFM_CMD_C002: 更新考核週期

**API 端點：** `PUT /api/v1/performance/cycles/{id}`

**業務場景描述：**

HR 更新考核週期資訊。只有 DRAFT 狀態的週期可以修改。

**測試合約：**

```json
{
  "scenarioId": "PFM_CMD_C002",
  "apiEndpoint": "PUT /api/v1/performance/cycles/{id}",
  "controller": "HR08CycleCmdController",
  "service": "UpdateCycleServiceImpl",
  "permission": "performance:cycle:update",
  "request": {
    "cycleId": "CYC001",
    "cycleName": "2025年度考核（修正）",
    "cycleType": "ANNUAL",
    "startDate": "2025-12-01",
    "endDate": "2026-02-28"
  },
  "businessRules": [
    "只有 DRAFT 狀態的週期才能修改",
    "結束日期必須在開始日期之後",
    "自評截止日必須在週期期間內",
    "主管評核截止日必須在自評截止日之後"
  ],
  "expectedDataChanges": {
    "tables": ["performance_cycle"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "performance_cycle",
        "expectedFields": {
          "cycle_name": "2025年度考核（修正）",
          "end_date": "2026-02-28"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### PFM_CMD_C003: 刪除考核週期

**API 端點：** `DELETE /api/v1/performance/cycles/{id}`

**業務場景描述：**

HR 刪除考核週期。只有 DRAFT 狀態的週期才能刪除，已啟動或已完成的週期不可刪除。採用軟刪除機制。

**測試合約：**

```json
{
  "scenarioId": "PFM_CMD_C003",
  "apiEndpoint": "DELETE /api/v1/performance/cycles/{id}",
  "controller": "HR08CycleCmdController",
  "service": "DeleteCycleServiceImpl",
  "permission": "performance:cycle:delete",
  "request": {
    "cycleId": "CYC001"
  },
  "businessRules": [
    "只有 DRAFT 狀態的週期才能刪除",
    "IN_PROGRESS 或 COMPLETED 狀態不可刪除",
    "採用軟刪除 (is_deleted = 1)"
  ],
  "expectedDataChanges": {
    "tables": ["performance_cycle"],
    "operations": [
      {
        "type": "SOFT_DELETE",
        "table": "performance_cycle",
        "expectedFields": {
          "is_deleted": 1
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### PFM_CMD_C004: 啟動考核週期

**API 端點：** `PUT /api/v1/performance/cycles/{id}/start`

**業務場景描述：**

HR 啟動考核週期，將狀態從 DRAFT 變更為 IN_PROGRESS。啟動後，系統自動為所有符合條件的員工建立考核記錄。

**測試合約：**

```json
{
  "scenarioId": "PFM_CMD_C004",
  "apiEndpoint": "PUT /api/v1/performance/cycles/{id}/start",
  "controller": "HR08CycleCmdController",
  "service": "StartCycleServiceImpl",
  "permission": "performance:cycle:start",
  "request": {
    "cycleId": "CYC001"
  },
  "businessRules": [
    "只有 DRAFT 狀態的週期才能啟動",
    "週期必須已設定考核範本",
    "啟動後狀態變更為 IN_PROGRESS",
    "自動為符合條件的員工建立考核記錄"
  ],
  "expectedDataChanges": {
    "tables": ["performance_cycle", "performance_review"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "performance_cycle",
        "expectedFields": {
          "status": "IN_PROGRESS"
        }
      },
      {
        "type": "INSERT",
        "table": "performance_review",
        "expectedFields": {
          "status": "PENDING_SELF"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "CycleStartedEvent",
      "expectedFields": {
        "cycleId": "CYC001",
        "status": "IN_PROGRESS"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### PFM_CMD_C005: 完成考核週期

**API 端點：** `PUT /api/v1/performance/cycles/{id}/complete`

**業務場景描述：**

HR 完成考核週期，將狀態從 IN_PROGRESS 變更為 COMPLETED。所有考核記錄必須為 FINALIZED 才能完成週期。

**測試合約：**

```json
{
  "scenarioId": "PFM_CMD_C005",
  "apiEndpoint": "PUT /api/v1/performance/cycles/{id}/complete",
  "controller": "HR08CycleCmdController",
  "service": "CompleteCycleServiceImpl",
  "permission": "performance:cycle:complete",
  "request": {
    "cycleId": "CYC001"
  },
  "businessRules": [
    "只有 IN_PROGRESS 狀態的週期才能完成",
    "所有考核記錄必須為 FINALIZED 狀態",
    "完成後狀態變更為 COMPLETED"
  ],
  "expectedDataChanges": {
    "tables": ["performance_cycle"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "performance_cycle",
        "expectedFields": {
          "status": "COMPLETED"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "CycleCompletedEvent",
      "expectedFields": {
        "cycleId": "CYC001",
        "status": "COMPLETED"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

### 1.2 考核範本管理

#### PFM_CMD_T001: 儲存考核範本

**API 端點：** `POST /api/v1/performance/cycles/{id}/template`

**業務場景描述：**

HR 為考核週期設計考核表單範本，定義評分項目、計分方式及績效分布設定。

**測試合約：**

```json
{
  "scenarioId": "PFM_CMD_T001",
  "apiEndpoint": "POST /api/v1/performance/cycles/{id}/template",
  "controller": "HR08TemplateCmdController",
  "service": "SaveTemplateServiceImpl",
  "permission": "performance:template:save",
  "request": {
    "cycleId": "CYC001",
    "templateName": "2025年度考核範本",
    "scoringSystem": "PERCENTAGE",
    "enableDistribution": true,
    "items": [
      {"name": "工作績效", "weight": 40, "description": "核心工作目標達成率"},
      {"name": "專業能力", "weight": 30, "description": "專業知識與技能"},
      {"name": "工作態度", "weight": 30, "description": "團隊合作與溝通能力"}
    ]
  },
  "businessRules": [
    "只有 DRAFT 狀態的週期才能設定範本",
    "評分項目權重加總必須等於 100",
    "至少需要一個評分項目"
  ],
  "expectedDataChanges": {
    "tables": ["performance_template", "performance_template_item"],
    "operations": [
      {
        "type": "INSERT",
        "table": "performance_template",
        "expectedFields": {
          "cycle_id": "CYC001",
          "template_name": "2025年度考核範本",
          "scoring_system": "PERCENTAGE"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### PFM_CMD_T002: 發布考核範本

**API 端點：** `PUT /api/v1/performance/cycles/{id}/template/publish`

**業務場景描述：**

HR 發布考核範本，發布後範本不可再修改。

**測試合約：**

```json
{
  "scenarioId": "PFM_CMD_T002",
  "apiEndpoint": "PUT /api/v1/performance/cycles/{id}/template/publish",
  "controller": "HR08TemplateCmdController",
  "service": "PublishTemplateServiceImpl",
  "permission": "performance:template:publish",
  "request": {
    "cycleId": "CYC001"
  },
  "businessRules": [
    "範本必須存在且尚未發布",
    "發布後範本不可再修改"
  ],
  "expectedDataChanges": {
    "tables": ["performance_template"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "performance_template",
        "expectedFields": {
          "published": true
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

### 1.3 考核維護

#### PFM_CMD_R001: 提交考核

**API 端點：** `POST /api/v1/performance/reviews/{id}/submit`

**業務場景描述：**

員工或主管提交考核。員工提交自評後，狀態變更為 PENDING_MANAGER；主管提交評核後，狀態變更為 PENDING_FINALIZE。

**測試合約：**

```json
{
  "scenarioId": "PFM_CMD_R001",
  "apiEndpoint": "POST /api/v1/performance/reviews/{id}/submit",
  "controller": "HR08ReviewCmdController",
  "service": "SubmitReviewServiceImpl",
  "permission": "performance:review:submit",
  "request": {
    "reviewId": "REV001",
    "evaluationItems": [
      {"itemId": "ITEM001", "score": 85, "comments": "表現優良"},
      {"itemId": "ITEM002", "score": 90, "comments": "專業度高"}
    ],
    "comments": "整體表現優秀"
  },
  "businessRules": [
    "自評：PENDING_SELF → PENDING_MANAGER",
    "主管評：PENDING_MANAGER → PENDING_FINALIZE",
    "每個評分項目的分數必須在有效範圍內",
    "所有評分項目都必須填寫"
  ],
  "expectedDataChanges": {
    "tables": ["performance_review", "performance_evaluation"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "performance_review",
        "expectedFields": {
          "status": "PENDING_MANAGER"
        }
      },
      {
        "type": "INSERT",
        "table": "performance_evaluation",
        "expectedFields": {
          "review_id": "REV001"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "ReviewSubmittedEvent",
      "expectedFields": {
        "reviewId": "REV001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### PFM_CMD_R002: 確認最終評等

**API 端點：** `PUT /api/v1/performance/reviews/{id}/finalize`

**業務場景描述：**

HR 或高階主管確認員工最終評等分數與等級。確認後狀態變更為 FINALIZED，不可再修改。

**測試合約：**

```json
{
  "scenarioId": "PFM_CMD_R002",
  "apiEndpoint": "PUT /api/v1/performance/reviews/{id}/finalize",
  "controller": "HR08ReviewCmdController",
  "service": "FinalizeReviewServiceImpl",
  "permission": "performance:review:finalize",
  "request": {
    "reviewId": "REV001",
    "finalScore": 88,
    "finalRating": "A",
    "adjustmentReason": "表現一致優秀"
  },
  "businessRules": [
    "只有 PENDING_FINALIZE 狀態才能確認最終評等",
    "最終分數必須在有效範圍內 (0-100)",
    "最終評等必須為有效等級 (S/A/B/C/D)",
    "確認後狀態變更為 FINALIZED，不可再修改"
  ],
  "expectedDataChanges": {
    "tables": ["performance_review"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "performance_review",
        "expectedFields": {
          "status": "FINALIZED",
          "final_score": 88,
          "final_rating": "A"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "ReviewFinalizedEvent",
      "expectedFields": {
        "reviewId": "REV001",
        "finalRating": "A"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

---

## 2. Query 操作業務合約

### 查詢過濾條件驗證表

以下表格定義各查詢場景必須包含的過濾條件，供合約測試引擎自動驗證。

> **軟刪除策略說明：**
> - **所有查詢：** 統一使用 `is_deleted = 0` 軟刪除過濾

#### 2.1 考核週期查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PFM_C001 | 查詢進行中週期 | HR | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'`, `is_deleted = '0'` |
| PFM_C002 | 查詢特定考核類型週期 | HR | `{"cycleType":"ANNUAL"}` | `cycleType = 'ANNUAL'`, `is_deleted = '0'` |
| PFM_C003 | 查詢特定年份的週期 | HR | `{"year":2025}` | `year = '2025'`, `is_deleted = '0'` |
| PFM_C004 | 查詢草稿週期 | HR | `{"status":"DRAFT"}` | `status = 'DRAFT'`, `is_deleted = '0'` |
| PFM_C005 | 查詢已完成週期 | HR | `{"status":"COMPLETED"}` | `status = 'COMPLETED'`, `is_deleted = '0'` |
| PFM_C006 | 組合查詢: 年份+類型 | HR | `{"year":2025,"cycleType":"QUARTERLY"}` | `year = '2025'`, `cycleType = 'QUARTERLY'`, `is_deleted = '0'` |
| PFM_C007 | 組合查詢: 年份+狀態+類型 | HR | `{"year":2025,"status":"IN_PROGRESS","cycleType":"ANNUAL"}` | `year = '2025'`, `status = 'IN_PROGRESS'`, `cycleType = 'ANNUAL'`, `is_deleted = '0'` |

##### PFM_C001: 查詢進行中週期

```json
{
  "scenarioId": "PFM_C001",
  "apiEndpoint": "GET /api/v1/performance/cycles",
  "controller": "HR08CycleQryController",
  "service": "GetCyclesServiceImpl",
  "permission": "performance:cycle:read",
  "request": {
    "status": "IN_PROGRESS"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "IN_PROGRESS"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

##### PFM_C002: 查詢特定考核類型週期

```json
{
  "scenarioId": "PFM_C002",
  "apiEndpoint": "GET /api/v1/performance/cycles",
  "controller": "HR08CycleQryController",
  "service": "GetCyclesServiceImpl",
  "permission": "performance:cycle:read",
  "request": {
    "cycleType": "ANNUAL"
  },
  "expectedQueryFilters": [
    {"field": "cycleType", "operator": "=", "value": "ANNUAL"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

##### PFM_C003: 查詢特定年份的週期

```json
{
  "scenarioId": "PFM_C003",
  "apiEndpoint": "GET /api/v1/performance/cycles",
  "controller": "HR08CycleQryController",
  "service": "GetCyclesServiceImpl",
  "permission": "performance:cycle:read",
  "request": {
    "year": 2025
  },
  "expectedQueryFilters": [
    {"field": "year", "operator": "=", "value": 2025}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

#### 2.2 我的考核查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PFM_R001 | 查詢我的考核 (依員工ID) | EMPLOYEE | `{"employeeId":"E001"}` | `employee_id = 'E001'`, `is_deleted = '0'` |
| PFM_R002 | 查詢我的考核 (依週期) | EMPLOYEE | `{"cycleId":"CYC001","employeeId":"E001"}` | `cycle_id = 'CYC001'`, `employee_id = 'E001'`, `is_deleted = '0'` |
| PFM_R003 | 查詢我的考核 (依狀態) | EMPLOYEE | `{"employeeId":"E001","status":"PENDING_SELF"}` | `employee_id = 'E001'`, `status = 'PENDING_SELF'`, `is_deleted = '0'` |
| PFM_R004 | 查詢我的已完成考核 | EMPLOYEE | `{"employeeId":"E001","status":"FINALIZED"}` | `employee_id = 'E001'`, `status = 'FINALIZED'`, `is_deleted = '0'` |

##### PFM_R001: 查詢我的考核 (依員工ID)

```json
{
  "scenarioId": "PFM_R001",
  "apiEndpoint": "GET /api/v1/performance/reviews/my",
  "controller": "HR08ReviewQryController",
  "service": "GetMyReviewsServiceImpl",
  "permission": "performance:review:read",
  "request": {},
  "expectedQueryFilters": [
    {"field": "employeeId", "operator": "=", "value": "00000000-0000-0000-0000-000000000001"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

#### 2.3 團隊考核查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| PFM_T001 | 查詢團隊考核 (依評核者) | MANAGER | `{"reviewerId":"MGR001"}` | `reviewerId = 'MGR001'`, `is_deleted = '0'` |
| PFM_T002 | 查詢團隊考核 (依週期) | MANAGER | `{"cycleId":"CYC001","reviewerId":"MGR001"}` | `cycleId = 'CYC001'`, `reviewerId = 'MGR001'`, `is_deleted = '0'` |
| PFM_T003 | 查詢團隊考核 (僅週期) | MANAGER | `{"cycleId":"CYC001"}` | `cycleId = 'CYC001'`, `is_deleted = '0'` |

##### PFM_T001: 查詢團隊考核 (依評核者)

```json
{
  "scenarioId": "PFM_T001",
  "apiEndpoint": "GET /api/v1/performance/reviews/team",
  "controller": "HR08ReviewQryController",
  "service": "GetTeamReviewsServiceImpl",
  "permission": "performance:review:read",
  "request": {},
  "expectedQueryFilters": [
    {"field": "reviewerId", "operator": "=", "value": "00000000-0000-0000-0000-000000000001"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```
