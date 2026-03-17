# HR09 招募管理服務業務合約

> **服務代碼:** HR09
> **服務名稱:** 招募管理服務 (Recruitment Management)
> **版本:** 1.0
> **更新日期:** 2026-02-23

---

## 概述

招募管理服務負責職缺管理、應徵者管理、面試排程與評估、Offer 發放與追蹤、招募儀表板等功能。支援完整的招募流程：職缺開立 → 收集履歷 → 面試安排 → Offer 發送 → 錄用。

---

## API 端點概覽

### 職缺管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/recruitment/jobs` | POST | RCT_CMD_J001 | 建立職缺 | ✅ 已實作 |
| 2 | `PUT /api/v1/recruitment/jobs/{id}` | PUT | RCT_CMD_J002 | 更新職缺 | ✅ 已實作 |
| 3 | `POST /api/v1/recruitment/jobs/{id}/close` | POST | RCT_CMD_J003 | 關閉職缺 | ✅ 已實作 |
| 4 | `GET /api/v1/recruitment/jobs` | GET | RCT_J001~J007 | 查詢職缺列表 | ✅ 已實作 |
| 5 | `GET /api/v1/recruitment/jobs/{id}` | GET | - | 查詢職缺詳情 | ✅ 已實作 |

### 應徵者管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/recruitment/candidates` | POST | RCT_CMD_CD001 | 建立應徵者 | ✅ 已實作 |
| 2 | `PUT /api/v1/recruitment/candidates/{id}/status` | PUT | RCT_CMD_CD002 | 更新應徵者狀態 | ✅ 已實作 |
| 3 | `PUT /api/v1/recruitment/candidates/{id}/reject` | PUT | RCT_CMD_CD003 | 淘汰應徵者 | ✅ 已實作 |
| 4 | `PUT /api/v1/recruitment/candidates/{id}/hire` | PUT | RCT_CMD_CD004 | 錄用應徵者 | ✅ 已實作 |
| 5 | `GET /api/v1/recruitment/candidates` | GET | RCT_CD001~CD005 | 查詢應徵者列表 | ✅ 已實作 |
| 6 | `GET /api/v1/recruitment/candidates/{id}` | GET | - | 查詢應徵者詳情 | ✅ 已實作 |

### 面試管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/recruitment/interviews` | POST | RCT_CMD_I001 | 安排面試 | ✅ 已實作 |
| 2 | `PUT /api/v1/recruitment/interviews/{id}/reschedule` | PUT | RCT_CMD_I002 | 重新排程面試 | ✅ 已實作 |
| 3 | `POST /api/v1/recruitment/interviews/{id}/evaluations` | POST | RCT_CMD_I003 | 提交面試評估 | ✅ 已實作 |
| 4 | `POST /api/v1/recruitment/interviews/{id}/cancel` | POST | RCT_CMD_I004 | 取消面試 | ✅ 已實作 |
| 5 | `GET /api/v1/recruitment/interviews` | GET | RCT_I001~I006 | 查詢面試列表 | ✅ 已實作 |
| 6 | `GET /api/v1/recruitment/interviews/{id}` | GET | - | 查詢面試詳情 | ✅ 已實作 |

### Offer 管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/recruitment/offers` | POST | RCT_CMD_O001 | 建立 Offer | ✅ 已實作 |
| 2 | `POST /api/v1/recruitment/offers/{id}/accept` | POST | RCT_CMD_O002 | 接受 Offer | ✅ 已實作 |
| 3 | `POST /api/v1/recruitment/offers/{id}/reject` | POST | RCT_CMD_O003 | 拒絕 Offer | ✅ 已實作 |
| 4 | `POST /api/v1/recruitment/offers/{id}/withdraw` | POST | RCT_CMD_O004 | 撤回 Offer | ✅ 已實作 |
| 5 | `PUT /api/v1/recruitment/offers/{id}/extend` | PUT | RCT_CMD_O005 | 延長 Offer | ✅ 已實作 |
| 6 | `GET /api/v1/recruitment/offers` | GET | RCT_O001~O005 | 查詢 Offer 列表 | ✅ 已實作 |
| 7 | `GET /api/v1/recruitment/offers/{id}` | GET | - | 查詢 Offer 詳情 | ✅ 已實作 |

### 招募報表 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/recruitment/dashboard` | GET | - | 招募儀表板 | ✅ 已實作 |
| 2 | `GET /api/v1/recruitment/dashboard/export` | GET | - | 匯出招募報表 | ✅ 已實作 |

**總計：26 個 API 端點**

**場景分類：**
- **Command 操作：** 16 個（3 職缺 + 4 應徵者 + 4 面試 + 5 Offer）
- **Query 操作：** 23 個（7 職缺 + 5 應徵者 + 6 面試 + 5 Offer）

---

## 1. Command 操作業務合約

### 1.1 職缺管理

#### RCT_CMD_J001: 建立職缺

**API 端點：** `POST /api/v1/recruitment/jobs`

**業務場景描述：**

HR 建立新職缺，定義職位名稱、部門、需求人數、職位描述及需求條件。建立時狀態為 DRAFT。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_J001",
  "apiEndpoint": "POST /api/v1/recruitment/jobs",
  "controller": "HR09JobCmdController",
  "service": "CreateJobOpeningServiceImpl",
  "permission": "recruitment:job:create",
  "request": {
    "title": "資深軟體工程師",
    "departmentId": "D001",
    "headcount": 2,
    "description": "負責系統架構設計與開發",
    "requirements": "5年以上 Java 開發經驗"
  },
  "businessRules": [
    "職缺名稱不可為空",
    "需求人數必須大於 0",
    "建立時狀態為 DRAFT",
    "需指定所屬部門"
  ],
  "expectedDataChanges": {
    "tables": ["job_opening"],
    "operations": [
      {
        "type": "INSERT",
        "table": "job_opening",
        "expectedFields": {
          "title": "資深軟體工程師",
          "department_id": "D001",
          "headcount": 2,
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
      {"name": "jobOpeningId", "type": "string", "notNull": true}
    ]
  }
}
```

#### RCT_CMD_J002: 更新職缺

**API 端點：** `PUT /api/v1/recruitment/jobs/{id}`

**業務場景描述：**

HR 更新職缺資訊。DRAFT 和 OPEN 狀態均可修改。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_J002",
  "apiEndpoint": "PUT /api/v1/recruitment/jobs/{id}",
  "controller": "HR09JobCmdController",
  "service": "UpdateJobOpeningServiceImpl",
  "permission": "recruitment:job:update",
  "request": {
    "jobOpeningId": "JOB001",
    "title": "資深軟體工程師（修正）",
    "headcount": 3
  },
  "businessRules": [
    "DRAFT 和 OPEN 狀態可修改",
    "CLOSED 和 FILLED 狀態不可修改"
  ],
  "expectedDataChanges": {
    "tables": ["job_opening"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "job_opening",
        "expectedFields": {
          "title": "資深軟體工程師（修正）",
          "headcount": 3
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### RCT_CMD_J003: 關閉職缺

**API 端點：** `POST /api/v1/recruitment/jobs/{id}/close`

**業務場景描述：**

HR 關閉職缺，將狀態變更為 CLOSED。關閉後不再接受新的應徵者。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_J003",
  "apiEndpoint": "POST /api/v1/recruitment/jobs/{id}/close",
  "controller": "HR09JobCmdController",
  "service": "CloseJobOpeningServiceImpl",
  "permission": "recruitment:job:close",
  "request": {
    "jobOpeningId": "JOB001"
  },
  "businessRules": [
    "只有 OPEN 狀態的職缺才能關閉",
    "DRAFT → CLOSED 不允許（需先 OPEN）",
    "關閉後狀態變更為 CLOSED"
  ],
  "expectedDataChanges": {
    "tables": ["job_opening"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "job_opening",
        "expectedFields": {
          "status": "CLOSED"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

### 1.2 應徵者管理

#### RCT_CMD_CD001: 建立應徵者

**API 端點：** `POST /api/v1/recruitment/candidates`

**業務場景描述：**

HR 為職缺建立應徵者資料，包含姓名、聯絡資訊、履歷來源等。建立時狀態為 NEW。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_CD001",
  "apiEndpoint": "POST /api/v1/recruitment/candidates",
  "controller": "HR09CandidateCmdController",
  "service": "CreateCandidateServiceImpl",
  "permission": "recruitment:candidate:create",
  "request": {
    "openingId": "JOB001",
    "fullName": "王小明",
    "email": "wang@example.com",
    "phone": "0912345678",
    "source": "JOB_BANK"
  },
  "businessRules": [
    "應徵者必須關聯到有效職缺",
    "Email 格式必須正確",
    "建立時狀態為 NEW",
    "履歷來源為必填"
  ],
  "expectedDataChanges": {
    "tables": ["candidate"],
    "operations": [
      {
        "type": "INSERT",
        "table": "candidate",
        "expectedFields": {
          "full_name": "王小明",
          "status": "NEW",
          "source": "JOB_BANK"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "candidateId", "type": "string", "notNull": true}
    ]
  }
}
```

#### RCT_CMD_CD002: 更新應徵者狀態

**API 端點：** `PUT /api/v1/recruitment/candidates/{id}/status`

**業務場景描述：**

HR 更新應徵者狀態，狀態流程：NEW → SCREENING → INTERVIEWING → OFFERED → HIRED。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_CD002",
  "apiEndpoint": "PUT /api/v1/recruitment/candidates/{id}/status",
  "controller": "HR09CandidateCmdController",
  "service": "UpdateCandidateStatusServiceImpl",
  "permission": "recruitment:candidate:update",
  "request": {
    "candidateId": "CAND001",
    "status": "SCREENING"
  },
  "businessRules": [
    "狀態轉換必須合法（依據 CandidateStatus.canTransitionTo）",
    "不可跳過中間狀態"
  ],
  "expectedDataChanges": {
    "tables": ["candidate"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "candidate",
        "expectedFields": {
          "status": "SCREENING"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### RCT_CMD_CD003: 淘汰應徵者

**API 端點：** `PUT /api/v1/recruitment/candidates/{id}/reject`

**業務場景描述：**

HR 淘汰應徵者，狀態變更為 REJECTED。任何階段均可淘汰（除已錄用外）。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_CD003",
  "apiEndpoint": "PUT /api/v1/recruitment/candidates/{id}/reject",
  "controller": "HR09CandidateCmdController",
  "service": "RejectCandidateServiceImpl",
  "permission": "recruitment:candidate:reject",
  "request": {
    "candidateId": "CAND001"
  },
  "businessRules": [
    "已錄用 (HIRED) 的應徵者不可淘汰",
    "淘汰後狀態變更為 REJECTED"
  ],
  "expectedDataChanges": {
    "tables": ["candidate"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "candidate",
        "expectedFields": {
          "status": "REJECTED"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### RCT_CMD_CD004: 錄用應徵者

**API 端點：** `PUT /api/v1/recruitment/candidates/{id}/hire`

**業務場景描述：**

HR 錄用應徵者，狀態變更為 HIRED。錄用後自動觸發員工建立流程。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_CD004",
  "apiEndpoint": "PUT /api/v1/recruitment/candidates/{id}/hire",
  "controller": "HR09CandidateCmdController",
  "service": "HireCandidateServiceImpl",
  "permission": "recruitment:candidate:hire",
  "request": {
    "candidateId": "CAND001"
  },
  "businessRules": [
    "只有 OFFERED 狀態的應徵者才能錄用",
    "錄用後狀態變更為 HIRED",
    "觸發員工建立事件"
  ],
  "expectedDataChanges": {
    "tables": ["candidate"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "candidate",
        "expectedFields": {
          "status": "HIRED"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "CandidateHiredEvent",
      "expectedFields": {
        "candidateId": "CAND001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

### 1.3 面試管理

#### RCT_CMD_I001: 安排面試

**API 端點：** `POST /api/v1/recruitment/interviews`

**業務場景描述：**

HR 為應徵者安排面試，定義面試類型、日期、地點及面試官。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_I001",
  "apiEndpoint": "POST /api/v1/recruitment/interviews",
  "controller": "HR09InterviewCmdController",
  "service": "ScheduleInterviewServiceImpl",
  "permission": "recruitment:interview:schedule",
  "request": {
    "candidateId": "CAND001",
    "interviewRound": 1,
    "interviewType": "PHONE",
    "interviewDate": "2026-03-01T10:00:00",
    "location": "線上",
    "interviewerIds": ["INT001"]
  },
  "businessRules": [
    "應徵者必須存在且狀態為 SCREENING 或 INTERVIEWING",
    "面試日期不可為過去",
    "至少需指定一位面試官",
    "建立時狀態為 SCHEDULED"
  ],
  "expectedDataChanges": {
    "tables": ["interview"],
    "operations": [
      {
        "type": "INSERT",
        "table": "interview",
        "expectedFields": {
          "status": "SCHEDULED",
          "interview_type": "PHONE",
          "interview_round": 1
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "interviewId", "type": "string", "notNull": true}
    ]
  }
}
```

#### RCT_CMD_I002: 重新排程面試

**API 端點：** `PUT /api/v1/recruitment/interviews/{id}/reschedule`

**業務場景描述：**

HR 重新排程面試日期或地點。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_I002",
  "apiEndpoint": "PUT /api/v1/recruitment/interviews/{id}/reschedule",
  "controller": "HR09InterviewCmdController",
  "service": "RescheduleInterviewServiceImpl",
  "permission": "recruitment:interview:reschedule",
  "request": {
    "interviewId": "INTV001",
    "interviewDate": "2026-03-05T14:00:00",
    "location": "會議室 A"
  },
  "businessRules": [
    "只有 SCHEDULED 狀態的面試才能重新排程",
    "新面試日期不可為過去"
  ],
  "expectedDataChanges": {
    "tables": ["interview"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "interview",
        "expectedFields": {
          "interview_date": "2026-03-05T14:00:00",
          "location": "會議室 A"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### RCT_CMD_I003: 提交面試評估

**API 端點：** `POST /api/v1/recruitment/interviews/{id}/evaluations`

**業務場景描述：**

面試官提交面試評估結果，包含各面向評分及整體評價。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_I003",
  "apiEndpoint": "POST /api/v1/recruitment/interviews/{id}/evaluations",
  "controller": "HR09InterviewCmdController",
  "service": "SubmitEvaluationServiceImpl",
  "permission": "recruitment:interview:evaluate",
  "request": {
    "interviewId": "INTV001",
    "technicalScore": 85,
    "communicationScore": 90,
    "overallRating": "HIRE",
    "comments": "技術能力優秀，溝通良好"
  },
  "businessRules": [
    "只有 SCHEDULED 狀態的面試才能提交評估",
    "提交後面試狀態變更為 COMPLETED",
    "評分必須在有效範圍內 (0-100)"
  ],
  "expectedDataChanges": {
    "tables": ["interview", "interview_evaluation"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "interview",
        "expectedFields": {
          "status": "COMPLETED"
        }
      },
      {
        "type": "INSERT",
        "table": "interview_evaluation",
        "expectedFields": {
          "overall_rating": "HIRE"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### RCT_CMD_I004: 取消面試

**API 端點：** `POST /api/v1/recruitment/interviews/{id}/cancel`

**業務場景描述：**

HR 取消已排定的面試。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_I004",
  "apiEndpoint": "POST /api/v1/recruitment/interviews/{id}/cancel",
  "controller": "HR09InterviewCmdController",
  "service": "CancelInterviewServiceImpl",
  "permission": "recruitment:interview:cancel",
  "request": {
    "interviewId": "INTV001"
  },
  "businessRules": [
    "只有 SCHEDULED 狀態的面試才能取消",
    "COMPLETED 狀態的面試不可取消",
    "取消後狀態變更為 CANCELLED"
  ],
  "expectedDataChanges": {
    "tables": ["interview"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "interview",
        "expectedFields": {
          "status": "CANCELLED"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

### 1.4 Offer 管理

#### RCT_CMD_O001: 建立 Offer

**API 端點：** `POST /api/v1/recruitment/offers`

**業務場景描述：**

HR 為應徵者發送錄取通知 (Offer)，定義職位、薪資、到期日等。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_O001",
  "apiEndpoint": "POST /api/v1/recruitment/offers",
  "controller": "HR09OfferCmdController",
  "service": "CreateOfferServiceImpl",
  "permission": "recruitment:offer:create",
  "request": {
    "candidateId": "CAND001",
    "offeredPosition": "軟體工程師",
    "offeredSalary": 80000,
    "offerExpiryDate": "2026-03-15",
    "employmentType": "FULL_TIME",
    "startDate": "2026-04-01"
  },
  "businessRules": [
    "應徵者必須存在且至少完成一次面試",
    "到期日不可為過去",
    "建立時狀態為 PENDING",
    "應徵者狀態變更為 OFFERED"
  ],
  "expectedDataChanges": {
    "tables": ["offer", "candidate"],
    "operations": [
      {
        "type": "INSERT",
        "table": "offer",
        "expectedFields": {
          "status": "PENDING",
          "offered_position": "軟體工程師",
          "offered_salary": 80000
        }
      },
      {
        "type": "UPDATE",
        "table": "candidate",
        "expectedFields": {
          "status": "OFFERED"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "offerId", "type": "string", "notNull": true}
    ]
  }
}
```

#### RCT_CMD_O002: 接受 Offer

**API 端點：** `POST /api/v1/recruitment/offers/{id}/accept`

**業務場景描述：**

應徵者接受 Offer，狀態變更為 ACCEPTED。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_O002",
  "apiEndpoint": "POST /api/v1/recruitment/offers/{id}/accept",
  "controller": "HR09OfferCmdController",
  "service": "AcceptOfferServiceImpl",
  "permission": "recruitment:offer:accept",
  "request": {
    "offerId": "OFR001"
  },
  "businessRules": [
    "只有 PENDING 狀態的 Offer 才能接受",
    "未過期的 Offer 才能接受",
    "接受後狀態變更為 ACCEPTED"
  ],
  "expectedDataChanges": {
    "tables": ["offer"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "offer",
        "expectedFields": {
          "status": "ACCEPTED"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "OfferAcceptedEvent",
      "expectedFields": {
        "offerId": "OFR001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### RCT_CMD_O003: 拒絕 Offer

**API 端點：** `POST /api/v1/recruitment/offers/{id}/reject`

**業務場景描述：**

應徵者拒絕 Offer。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_O003",
  "apiEndpoint": "POST /api/v1/recruitment/offers/{id}/reject",
  "controller": "HR09OfferCmdController",
  "service": "RejectOfferServiceImpl",
  "permission": "recruitment:offer:reject",
  "request": {
    "offerId": "OFR001",
    "reason": "薪資考量"
  },
  "businessRules": [
    "只有 PENDING 狀態的 Offer 才能拒絕",
    "拒絕後狀態變更為 REJECTED"
  ],
  "expectedDataChanges": {
    "tables": ["offer"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "offer",
        "expectedFields": {
          "status": "REJECTED"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### RCT_CMD_O004: 撤回 Offer

**API 端點：** `POST /api/v1/recruitment/offers/{id}/withdraw`

**業務場景描述：**

HR 撤回已發送的 Offer。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_O004",
  "apiEndpoint": "POST /api/v1/recruitment/offers/{id}/withdraw",
  "controller": "HR09OfferCmdController",
  "service": "WithdrawOfferServiceImpl",
  "permission": "recruitment:offer:withdraw",
  "request": {
    "offerId": "OFR001"
  },
  "businessRules": [
    "只有 PENDING 狀態的 Offer 才能撤回",
    "ACCEPTED 後不可撤回",
    "撤回後狀態變更為 WITHDRAWN"
  ],
  "expectedDataChanges": {
    "tables": ["offer"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "offer",
        "expectedFields": {
          "status": "WITHDRAWN"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200
  }
}
```

#### RCT_CMD_O005: 延長 Offer

**API 端點：** `PUT /api/v1/recruitment/offers/{id}/extend`

**業務場景描述：**

HR 延長 Offer 到期日。

**測試合約：**

```json
{
  "scenarioId": "RCT_CMD_O005",
  "apiEndpoint": "PUT /api/v1/recruitment/offers/{id}/extend",
  "controller": "HR09OfferCmdController",
  "service": "ExtendOfferServiceImpl",
  "permission": "recruitment:offer:extend",
  "request": {
    "offerId": "OFR001",
    "newExpiryDate": "2026-04-01"
  },
  "businessRules": [
    "只有 PENDING 狀態的 Offer 才能延長",
    "新到期日必須在原到期日之後"
  ],
  "expectedDataChanges": {
    "tables": ["offer"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "offer",
        "expectedFields": {
          "offer_expiry_date": "2026-04-01"
        }
      }
    ]
  },
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
> - **職缺：** 使用 `is_deleted = 0` 軟刪除過濾（由 Assembler 自動加入）
> - **應徵者：** 無軟刪除欄位
> - **面試/Offer：** 無軟刪除欄位

#### 2.1 職缺查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| RCT_J001 | 查詢開放中職缺 | HR | `{"status":"OPEN"}` | `status = 'OPEN'`, `is_deleted = '0'` |
| RCT_J002 | 查詢特定部門職缺 | HR | `{"departmentId":"D001"}` | `department_id = 'D001'`, `is_deleted = '0'` |
| RCT_J003 | 查詢草稿職缺 | HR | `{"status":"DRAFT"}` | `status = 'DRAFT'`, `is_deleted = '0'` |
| RCT_J004 | 查詢已關閉職缺 | HR | `{"status":"CLOSED"}` | `status = 'CLOSED'`, `is_deleted = '0'` |
| RCT_J005 | 組合查詢: 狀態+部門 | HR | `{"status":"OPEN","departmentId":"D001"}` | `status = 'OPEN'`, `department_id = 'D001'`, `is_deleted = '0'` |
| RCT_J006 | 關鍵字搜尋職缺 | HR | `{"keyword":"工程師"}` | `is_deleted = '0'`, `title LIKE '%工程師%'`, `requirements LIKE '%工程師%'` |
| RCT_J007 | 組合查詢: 狀態+關鍵字 | HR | `{"status":"OPEN","keyword":"工程師"}` | `status = 'OPEN'`, `is_deleted = '0'`, `title LIKE '%工程師%'`, `requirements LIKE '%工程師%'` |

##### RCT_J001: 查詢開放中職缺

```json
{
  "scenarioId": "RCT_J001",
  "apiEndpoint": "GET /api/v1/recruitment/jobs",
  "controller": "HR09JobQryController",
  "service": "GetJobOpeningsServiceImpl",
  "permission": "recruitment:job:read",
  "request": {
    "status": "OPEN"
  },
  "expectedQueryFilters": [
    {"field": "status", "operator": "=", "value": "OPEN"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

##### RCT_J002: 查詢特定部門職缺

```json
{
  "scenarioId": "RCT_J002",
  "apiEndpoint": "GET /api/v1/recruitment/jobs",
  "controller": "HR09JobQryController",
  "service": "GetJobOpeningsServiceImpl",
  "permission": "recruitment:job:read",
  "request": {
    "departmentId": "D001"
  },
  "expectedQueryFilters": [
    {"field": "department_id", "operator": "=", "value": "D001"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

#### 2.2 應徵者查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| RCT_CD001 | 查詢特定職缺應徵者 | HR | `{"openingId":"550e8400-e29b-41d4-a716-446655440000"}` | `openingId = '550e8400-e29b-41d4-a716-446655440000'` |
| RCT_CD002 | 查詢篩選中應徵者 | HR | `{"status":"SCREENING"}` | `status = 'SCREENING'` |
| RCT_CD003 | 關鍵字搜尋應徵者 | HR | `{"keyword":"John"}` | `fullName LIKE '%John%'`, `email LIKE '%John%'` |
| RCT_CD004 | 組合查詢: 職缺+狀態 | HR | `{"openingId":"550e8400-e29b-41d4-a716-446655440000","status":"NEW"}` | `openingId = '550e8400-e29b-41d4-a716-446655440000'`, `status = 'NEW'` |
| RCT_CD005 | 查詢已錄用應徵者 | HR | `{"status":"HIRED"}` | `status = 'HIRED'` |

##### RCT_CD001: 查詢特定職缺應徵者

```json
{
  "scenarioId": "RCT_CD001",
  "apiEndpoint": "GET /api/v1/recruitment/candidates",
  "controller": "HR09CandidateQryController",
  "service": "GetCandidatesServiceImpl",
  "permission": "recruitment:candidate:read",
  "request": {
    "openingId": "550e8400-e29b-41d4-a716-446655440000"
  },
  "expectedQueryFilters": [
    {"field": "openingId", "operator": "=", "value": "550e8400-e29b-41d4-a716-446655440000"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

#### 2.3 面試查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| RCT_I001 | 查詢特定應徵者面試 | HR | `{"candidateId":"cand-001"}` | `candidateId = 'cand-001'` |
| RCT_I002 | 查詢已排程面試 | HR | `{"status":"SCHEDULED"}` | `status = 'SCHEDULED'` |
| RCT_I003 | 姓名模糊搜尋面試 | HR | `{"candidateName":"王"}` | `candidateName LIKE '王'` |
| RCT_I004 | 查詢電話面試 | HR | `{"interviewType":"PHONE"}` | `interviewType = 'PHONE'` |
| RCT_I005 | 查詢第一輪面試 | HR | `{"interviewRound":1}` | `interviewRound = '1'` |
| RCT_I006 | 組合查詢: 應徵者+狀態 | HR | `{"candidateId":"cand-001","status":"SCHEDULED"}` | `candidateId = 'cand-001'`, `status = 'SCHEDULED'` |

##### RCT_I001: 查詢特定應徵者面試

```json
{
  "scenarioId": "RCT_I001",
  "apiEndpoint": "GET /api/v1/recruitment/interviews",
  "controller": "HR09InterviewQryController",
  "service": "GetInterviewsServiceImpl",
  "permission": "recruitment:interview:read",
  "request": {
    "candidateId": "cand-001"
  },
  "expectedQueryFilters": [
    {"field": "candidateId", "operator": "=", "value": "cand-001"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

#### 2.4 Offer 查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| RCT_O001 | 查詢特定應徵者 Offer | HR | `{"candidateId":"cand-001"}` | `candidateId = 'cand-001'` |
| RCT_O002 | 查詢待處理 Offer | HR | `{"status":"PENDING"}` | `status = 'PENDING'` |
| RCT_O003 | 姓名模糊搜尋 Offer | HR | `{"candidateName":"王"}` | `candidateName LIKE '王'` |
| RCT_O004 | 職位模糊搜尋 Offer | HR | `{"offeredPosition":"工程師"}` | `offeredPosition LIKE '工程師'` |
| RCT_O005 | 組合查詢: 應徵者+狀態 | HR | `{"candidateId":"cand-001","status":"PENDING"}` | `candidateId = 'cand-001'`, `status = 'PENDING'` |

##### RCT_O001: 查詢特定應徵者 Offer

```json
{
  "scenarioId": "RCT_O001",
  "apiEndpoint": "GET /api/v1/recruitment/offers",
  "controller": "HR09OfferQryController",
  "service": "GetOffersServiceImpl",
  "permission": "recruitment:offer:read",
  "request": {
    "candidateId": "cand-001"
  },
  "expectedQueryFilters": [
    {"field": "candidateId", "operator": "=", "value": "cand-001"}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```
