# HR10 訓練管理服務業務合約

> **服務代碼:** HR10
> **服務名稱:** 訓練管理服務 (Training Management)
> **版本:** 1.0
> **更新日期:** 2026-02-23

---

## 概述

訓練管理服務負責課程管理、報名管理、證照管理、訓練統計等功能。支援完整的訓練流程：課程建立 → 發布報名 → 員工報名 → 主管審核 → 出席確認 → 完成訓練。

---

## API 端點概覽

### 課程管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/training/courses` | POST | TRN_CMD_C001 | 建立課程 | ✅ 已實作 |
| 2 | `GET /api/v1/training/courses` | GET | TRN_C001~C009 | 查詢課程列表 | ✅ 已實作 |
| 3 | `GET /api/v1/training/courses/{id}` | GET | - | 查詢課程詳情 | ✅ 已實作 |
| 4 | `PUT /api/v1/training/courses/{id}` | PUT | TRN_CMD_C002 | 更新課程 | ✅ 已實作 |
| 5 | `PUT /api/v1/training/courses/{id}/publish` | PUT | TRN_CMD_C003 | 發布課程 | ✅ 已實作 |
| 6 | `PUT /api/v1/training/courses/{id}/close` | PUT | TRN_CMD_C004 | 關閉報名 | ✅ 已實作 |
| 7 | `PUT /api/v1/training/courses/{id}/complete` | PUT | TRN_CMD_C005 | 完成課程 | ✅ 已實作 |

### 報名管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/training/enrollments` | POST | TRN_CMD_E001 | 報名課程 | ✅ 已實作 |
| 2 | `GET /api/v1/training/enrollments` | GET | TRN_E001~E007 | 查詢報名列表 | ✅ 已實作 |
| 3 | `PUT /api/v1/training/enrollments/{id}/approve` | PUT | TRN_CMD_E002 | 審核通過 | ✅ 已實作 |
| 4 | `PUT /api/v1/training/enrollments/{id}/reject` | PUT | TRN_CMD_E003 | 審核拒絕 | ✅ 已實作 |
| 5 | `PUT /api/v1/training/enrollments/{id}/cancel` | PUT | TRN_CMD_E004 | 取消報名 | ✅ 已實作 |
| 6 | `PUT /api/v1/training/enrollments/{id}/attendance` | PUT | TRN_CMD_E005 | 確認出席 | ✅ 已實作 |
| 7 | `PUT /api/v1/training/enrollments/{id}/complete` | PUT | TRN_CMD_E006 | 完成訓練 | ✅ 已實作 |
| 8 | `GET /api/v1/training/my` | GET | TRN_E005 | 我的訓練 | ✅ 已實作 |

### 證照管理 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `POST /api/v1/training/certificates` | POST | TRN_CMD_CT001 | 新增證照 | ✅ 已實作 |
| 2 | `GET /api/v1/training/certificates` | GET | TRN_CT001~CT006 | 查詢證照列表 | ✅ 已實作 |
| 3 | `GET /api/v1/training/certificates/{id}` | GET | - | 查詢證照詳情 | ✅ 已實作 |
| 4 | `PUT /api/v1/training/certificates/{id}` | PUT | TRN_CMD_CT002 | 更新證照 | ✅ 已實作 |
| 5 | `DELETE /api/v1/training/certificates/{id}` | DELETE | TRN_CMD_CT003 | 刪除證照 | ✅ 已實作 |
| 6 | `GET /api/v1/training/certificates/expiring` | GET | TRN_CT003 | 即將到期證照 | ✅ 已實作 |

### 訓練統計 API
| # | 端點 | 方法 | 場景 ID | 說明 | 實作狀態 |
|:---:|:---|:---:|:---|:---|:---:|
| 1 | `GET /api/v1/training/my/hours` | GET | TRN_R001~R002 | 我的訓練時數 | ✅ 已實作 |
| 2 | `GET /api/v1/training/statistics` | GET | TRN_R004 | 訓練統計報表 | ✅ 已實作 |
| 3 | `GET /api/v1/training/statistics/export` | GET | - | 匯出統計報表 | ✅ 已實作 |

**總計：24 個 API 端點**

**場景分類：**
- **Command 操作：** 14 個（5 課程 + 6 報名 + 3 證照）
- **Query 操作：** 26 個（9 課程 + 7 報名 + 6 證照 + 4 統計）

---

## 1. Command 操作業務合約

### 1.1 課程管理

#### TRN_CMD_C001: 建立課程

**API 端點：** `POST /api/v1/training/courses`

**業務場景描述：**

HR 或訓練管理員建立新訓練課程。課程初始狀態為 DRAFT，待所有資訊填寫完整後再發布開放報名。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_C001",
  "apiEndpoint": "POST /api/v1/training/courses",
  "controller": "HR10CourseCmdController",
  "service": "CreateCourseServiceImpl",
  "permission": "training:course:create",
  "request": {
    "courseName": "React 進階開發實戰",
    "courseType": "INTERNAL",
    "deliveryMode": "OFFLINE",
    "category": "TECHNICAL",
    "durationHours": 8,
    "startDate": "2026-12-15",
    "endDate": "2026-12-15"
  },
  "businessRules": [
    "課程名稱不可為空",
    "訓練時數必須大於 0",
    "結束日期需大於等於開始日期",
    "建立時狀態為 DRAFT"
  ],
  "expectedDataChanges": {
    "tables": ["training_course"],
    "operations": [
      {
        "type": "INSERT",
        "table": "training_course",
        "expectedFields": {
          "course_name": "React 進階開發實戰",
          "course_type": "INTERNAL",
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
      {"name": "courseId", "type": "string", "notNull": true},
      {"name": "courseName", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

#### TRN_CMD_C002: 更新課程

**API 端點：** `PUT /api/v1/training/courses/{id}`

**業務場景描述：**

HR 更新課程資訊。只有 DRAFT 或 OPEN 狀態的課程可以更新；若已有報名，部分欄位受限。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_C002",
  "apiEndpoint": "PUT /api/v1/training/courses/{id}",
  "controller": "HR10CourseCmdController",
  "service": "UpdateCourseServiceImpl",
  "permission": "training:course:update",
  "request": {
    "courseId": "course-uuid-001",
    "courseName": "React 進階開發實戰（更新版）",
    "maxParticipants": 35
  },
  "businessRules": [
    "只有 DRAFT 或 OPEN 狀態的課程可以更新",
    "COMPLETED 或 CANCELLED 狀態不可更新"
  ],
  "expectedDataChanges": {
    "tables": ["training_course"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "training_course",
        "expectedFields": {
          "course_name": "React 進階開發實戰（更新版）",
          "max_participants": 35
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "courseId", "type": "string", "notNull": true}
    ]
  }
}
```

#### TRN_CMD_C003: 發布課程

**API 端點：** `PUT /api/v1/training/courses/{id}/publish`

**業務場景描述：**

HR 將草稿課程發布為開放報名狀態，員工可開始報名。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_C003",
  "apiEndpoint": "PUT /api/v1/training/courses/{id}/publish",
  "controller": "HR10CourseCmdController",
  "service": "PublishCourseServiceImpl",
  "permission": "training:course:publish",
  "request": {
    "courseId": "course-uuid-001"
  },
  "businessRules": [
    "只有 DRAFT 狀態的課程可以發布",
    "發布後狀態變更為 OPEN",
    "必填欄位（日期、時數）需完整"
  ],
  "expectedDataChanges": {
    "tables": ["training_course"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "training_course",
        "expectedFields": {
          "status": "OPEN"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "CoursePublishedEvent",
      "expectedFields": {
        "courseId": "course-uuid-001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "courseId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

#### TRN_CMD_C004: 關閉報名

**API 端點：** `PUT /api/v1/training/courses/{id}/close`

**業務場景描述：**

HR 關閉課程報名，不再接受新報名。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_C004",
  "apiEndpoint": "PUT /api/v1/training/courses/{id}/close",
  "controller": "HR10CourseCmdController",
  "service": "CloseCourseEnrollmentServiceImpl",
  "permission": "training:course:close",
  "request": {
    "courseId": "course-uuid-001",
    "reason": "報名截止"
  },
  "businessRules": [
    "只有 OPEN 狀態的課程可以關閉報名",
    "關閉後狀態變更為 CLOSED",
    "待審核的報名自動拒絕"
  ],
  "expectedDataChanges": {
    "tables": ["training_course"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "training_course",
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

#### TRN_CMD_C005: 完成課程

**API 端點：** `PUT /api/v1/training/courses/{id}/complete`

**業務場景描述：**

HR 標記課程已結束，系統更新已出席學員的訓練記錄。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_C005",
  "apiEndpoint": "PUT /api/v1/training/courses/{id}/complete",
  "controller": "HR10CourseCmdController",
  "service": "CompleteCourseServiceImpl",
  "permission": "training:course:complete",
  "request": {
    "courseId": "course-uuid-001"
  },
  "businessRules": [
    "只有 CLOSED 狀態的課程可以完成",
    "完成後狀態變更為 COMPLETED",
    "已出席學員狀態更新為 COMPLETED",
    "未出席學員狀態更新為 NO_SHOW"
  ],
  "expectedDataChanges": {
    "tables": ["training_course", "training_enrollment"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "training_course",
        "expectedFields": {
          "status": "COMPLETED"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "CourseCompletedEvent",
      "expectedFields": {
        "courseId": "course-uuid-001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "courseId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

---

### 1.2 報名管理

#### TRN_CMD_E001: 報名課程

**API 端點：** `POST /api/v1/training/enrollments`

**業務場景描述：**

員工自行報名或 HR 代為報名課程，系統通知主管審核。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_E001",
  "apiEndpoint": "POST /api/v1/training/enrollments",
  "controller": "HR10EnrollmentCmdController",
  "service": "EnrollCourseServiceImpl",
  "permission": "training:enrollment:create",
  "request": {
    "courseId": "course-uuid-001",
    "reason": "希望提升前端技術能力"
  },
  "businessRules": [
    "課程必須存在且狀態為 OPEN",
    "未超過報名截止日",
    "課程名額未滿",
    "員工不可重複報名同一課程",
    "建立時狀態為 REGISTERED"
  ],
  "expectedDataChanges": {
    "tables": ["training_enrollment"],
    "operations": [
      {
        "type": "INSERT",
        "table": "training_enrollment",
        "expectedFields": {
          "status": "REGISTERED"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "EnrollmentCreatedEvent",
      "expectedFields": {
        "courseId": "course-uuid-001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "enrollmentId", "type": "string", "notNull": true},
      {"name": "courseId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

#### TRN_CMD_E002: 審核通過

**API 端點：** `PUT /api/v1/training/enrollments/{id}/approve`

**業務場景描述：**

主管或 HR 審核通過報名申請，通知員工報名成功。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_E002",
  "apiEndpoint": "PUT /api/v1/training/enrollments/{id}/approve",
  "controller": "HR10EnrollmentCmdController",
  "service": "ApproveEnrollmentServiceImpl",
  "permission": "training:enrollment:approve",
  "request": {
    "enrollmentId": "enroll-uuid-001",
    "remarks": "同意參訓"
  },
  "businessRules": [
    "報名必須存在且狀態為 REGISTERED",
    "審核者必須是員工主管或 HR",
    "課程名額未滿",
    "審核後狀態變更為 APPROVED"
  ],
  "expectedDataChanges": {
    "tables": ["training_enrollment"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "training_enrollment",
        "expectedFields": {
          "status": "APPROVED"
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "EnrollmentApprovedEvent",
      "expectedFields": {
        "enrollmentId": "enroll-uuid-001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "enrollmentId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

#### TRN_CMD_E003: 審核拒絕

**API 端點：** `PUT /api/v1/training/enrollments/{id}/reject`

**業務場景描述：**

主管或 HR 拒絕報名申請，通知員工未通過審核。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_E003",
  "apiEndpoint": "PUT /api/v1/training/enrollments/{id}/reject",
  "controller": "HR10EnrollmentCmdController",
  "service": "RejectEnrollmentServiceImpl",
  "permission": "training:enrollment:reject",
  "request": {
    "enrollmentId": "enroll-uuid-001",
    "reason": "目前專案進度緊迫，建議下期再報名"
  },
  "businessRules": [
    "報名必須存在且狀態為 REGISTERED",
    "拒絕原因為必填",
    "拒絕後狀態變更為 REJECTED"
  ],
  "expectedDataChanges": {
    "tables": ["training_enrollment"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "training_enrollment",
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

#### TRN_CMD_E004: 取消報名

**API 端點：** `PUT /api/v1/training/enrollments/{id}/cancel`

**業務場景描述：**

員工或 HR 取消報名申請。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_E004",
  "apiEndpoint": "PUT /api/v1/training/enrollments/{id}/cancel",
  "controller": "HR10EnrollmentCmdController",
  "service": "CancelEnrollmentServiceImpl",
  "permission": "training:enrollment:cancel",
  "request": {
    "enrollmentId": "enroll-uuid-001",
    "reason": "臨時有重要會議無法參加"
  },
  "businessRules": [
    "報名必須存在且狀態為 REGISTERED 或 APPROVED",
    "課程尚未開始",
    "取消者必須是本人或 HR",
    "取消後狀態變更為 CANCELLED"
  ],
  "expectedDataChanges": {
    "tables": ["training_enrollment"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "training_enrollment",
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

#### TRN_CMD_E005: 確認出席

**API 端點：** `PUT /api/v1/training/enrollments/{id}/attendance`

**業務場景描述：**

講師或 HR 確認學員出席狀態。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_E005",
  "apiEndpoint": "PUT /api/v1/training/enrollments/{id}/attendance",
  "controller": "HR10EnrollmentCmdController",
  "service": "ConfirmAttendanceServiceImpl",
  "permission": "training:enrollment:attendance",
  "request": {
    "enrollmentId": "enroll-uuid-001",
    "attended": true,
    "attendedHours": 8
  },
  "businessRules": [
    "報名必須存在且狀態為 APPROVED",
    "出席確認後狀態變更為 ATTENDED"
  ],
  "expectedDataChanges": {
    "tables": ["training_enrollment"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "training_enrollment",
        "expectedFields": {
          "status": "ATTENDED",
          "attendance": true
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "enrollmentId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true}
    ]
  }
}
```

#### TRN_CMD_E006: 完成訓練

**API 端點：** `PUT /api/v1/training/enrollments/{id}/complete`

**業務場景描述：**

講師或 HR 記錄學員完成訓練，登錄成績並更新訓練時數。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_E006",
  "apiEndpoint": "PUT /api/v1/training/enrollments/{id}/complete",
  "controller": "HR10EnrollmentCmdController",
  "service": "CompleteTrainingServiceImpl",
  "permission": "training:enrollment:complete",
  "request": {
    "enrollmentId": "enroll-uuid-001",
    "completedHours": 8,
    "score": 85,
    "passed": true,
    "feedback": "表現優異"
  },
  "businessRules": [
    "報名必須存在且狀態為 ATTENDED",
    "成績介於 0-100（若填寫）",
    "完成後狀態變更為 COMPLETED",
    "更新員工訓練時數統計"
  ],
  "expectedDataChanges": {
    "tables": ["training_enrollment"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "training_enrollment",
        "expectedFields": {
          "status": "COMPLETED",
          "passed": true
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "TrainingCompletedEvent",
      "expectedFields": {
        "enrollmentId": "enroll-uuid-001"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "enrollmentId", "type": "string", "notNull": true},
      {"name": "status", "type": "string", "notNull": true},
      {"name": "passed", "type": "boolean", "notNull": false}
    ]
  }
}
```

---

### 1.3 證照管理

#### TRN_CMD_CT001: 新增證照

**API 端點：** `POST /api/v1/training/certificates`

**業務場景描述：**

員工登錄取得的專業證照，系統設定到期提醒。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_CT001",
  "apiEndpoint": "POST /api/v1/training/certificates",
  "controller": "HR10CertificateCmdController",
  "service": "AddCertificateServiceImpl",
  "permission": "training:certificate:create",
  "request": {
    "certificateName": "AWS Solutions Architect - Associate",
    "issuingOrganization": "Amazon Web Services",
    "issueDate": "2023-12-15",
    "expiryDate": "2026-12-15",
    "category": "TECHNICAL",
    "isRequired": true
  },
  "businessRules": [
    "證照名稱為必填",
    "發證日期為必填",
    "到期日（若有）需晚於發證日",
    "建立後設定到期提醒排程（90/30/7 天）"
  ],
  "expectedDataChanges": {
    "tables": ["certificate"],
    "operations": [
      {
        "type": "INSERT",
        "table": "certificate",
        "expectedFields": {
          "certificate_name": "AWS Solutions Architect - Associate",
          "is_required": true,
          "is_deleted": 0
        }
      }
    ]
  },
  "expectedDomainEvents": [
    {
      "eventType": "CertificateAddedEvent",
      "expectedFields": {
        "certificateName": "AWS Solutions Architect - Associate"
      }
    }
  ],
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "certificateId", "type": "string", "notNull": true},
      {"name": "certificateName", "type": "string", "notNull": true}
    ]
  }
}
```

#### TRN_CMD_CT002: 更新證照

**API 端點：** `PUT /api/v1/training/certificates/{id}`

**業務場景描述：**

員工或 HR 更新證照資訊（如續證後更新到期日）。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_CT002",
  "apiEndpoint": "PUT /api/v1/training/certificates/{id}",
  "controller": "HR10CertificateCmdController",
  "service": "UpdateCertificateServiceImpl",
  "permission": "training:certificate:update",
  "request": {
    "certificateId": "cert-uuid-001",
    "expiryDate": "2028-12-15",
    "remarks": "已續證"
  },
  "businessRules": [
    "證照必須存在",
    "更新者必須是本人或 HR",
    "到期日需晚於發證日"
  ],
  "expectedDataChanges": {
    "tables": ["certificate"],
    "operations": [
      {
        "type": "UPDATE",
        "table": "certificate",
        "expectedFields": {
          "expiry_date": "2028-12-15"
        }
      }
    ]
  },
  "expectedResponse": {
    "statusCode": 200,
    "dataPath": "data",
    "requiredFields": [
      {"name": "certificateId", "type": "string", "notNull": true}
    ]
  }
}
```

#### TRN_CMD_CT003: 刪除證照

**API 端點：** `DELETE /api/v1/training/certificates/{id}`

**業務場景描述：**

員工或 HR 刪除證照記錄（軟刪除）。

**測試合約：**

```json
{
  "scenarioId": "TRN_CMD_CT003",
  "apiEndpoint": "DELETE /api/v1/training/certificates/{id}",
  "controller": "HR10CertificateCmdController",
  "service": "DeleteCertificateServiceImpl",
  "permission": "training:certificate:delete",
  "request": {
    "certificateId": "cert-uuid-001"
  },
  "businessRules": [
    "證照必須存在",
    "刪除者必須是本人或 HR",
    "必備證照需 HR 授權才能刪除",
    "執行軟刪除（is_deleted = 1）"
  ],
  "expectedDataChanges": {
    "tables": ["certificate"],
    "operations": [
      {
        "type": "SOFT_DELETE",
        "table": "certificate",
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

---

## 2. Query 操作業務合約

### 查詢過濾條件驗證表

> **軟刪除策略說明：**
> - **課程：** 使用 `is_deleted = 0` 軟刪除過濾
> - **報名：** 使用 `is_deleted = 0` 軟刪除過濾
> - **證照：** 使用 `is_deleted = 0` 軟刪除過濾

#### 2.1 課程查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| TRN_C001 | 查詢開放報名課程 | EMPLOYEE | `{"status":"OPEN"}` | `status = 'OPEN'` |
| TRN_C002 | 查詢進行中課程 | HR | `{"status":"IN_PROGRESS"}` | `status = 'IN_PROGRESS'` |
| TRN_C003 | 查詢已結束課程 | HR | `{"status":"COMPLETED"}` | `status = 'COMPLETED'` |
| TRN_C004 | 依類型查詢（必修） | EMPLOYEE | `{"type":"MANDATORY"}` | `type = 'MANDATORY'` |
| TRN_C005 | 依類別查詢（技術類） | EMPLOYEE | `{"category":"TECHNICAL"}` | `category = 'TECHNICAL'` |
| TRN_C006 | 依名稱模糊查詢 | EMPLOYEE | `{"name":"領導"}` | `name LIKE '領導'` |
| TRN_C007 | 查詢線上課程 | EMPLOYEE | `{"mode":"ONLINE"}` | `mode = 'ONLINE'` |
| TRN_C008 | 查詢實體課程 | EMPLOYEE | `{"mode":"OFFLINE"}` | `mode = 'OFFLINE'` |
| TRN_C009 | 依講師查詢 | HR | `{"instructorId":"E001"}` | `instructor_id = 'E001'` |

##### TRN_C001: 查詢開放報名課程

```json
{
  "scenarioId": "TRN_C001",
  "apiEndpoint": "GET /api/v1/training/courses",
  "controller": "HR10CourseQryController",
  "service": "GetCoursesServiceImpl",
  "permission": "training:course:read",
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

##### TRN_C005: 依類別查詢（技術類）

```json
{
  "scenarioId": "TRN_C005",
  "apiEndpoint": "GET /api/v1/training/courses",
  "controller": "HR10CourseQryController",
  "service": "GetCoursesServiceImpl",
  "permission": "training:course:read",
  "request": {
    "category": "TECHNICAL"
  },
  "expectedQueryFilters": [
    {"field": "category", "operator": "=", "value": "TECHNICAL"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

##### TRN_C006: 依名稱模糊查詢

```json
{
  "scenarioId": "TRN_C006",
  "apiEndpoint": "GET /api/v1/training/courses",
  "controller": "HR10CourseQryController",
  "service": "GetCoursesServiceImpl",
  "permission": "training:course:read",
  "request": {
    "name": "領導"
  },
  "expectedQueryFilters": [
    {"field": "name", "operator": "LIKE", "value": "領導"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

#### 2.2 報名查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| TRN_E001 | 查詢課程報名 | HR | `{"courseId":"C001"}` | `course_id = 'C001'` |
| TRN_E002 | 查詢員工報名 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| TRN_E003 | 查詢待審核報名 | HR | `{"status":"PENDING"}` | `status = 'PENDING'` |
| TRN_E004 | 查詢已核准報名 | HR | `{"status":"APPROVED"}` | `status = 'APPROVED'` |
| TRN_E005 | 員工查詢自己報名 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'` |
| TRN_E006 | 主管查詢下屬報名 | MANAGER | `{}` | `employee.department_id IN ('{managedDeptIds}')` |
| TRN_E007 | 查詢已完成課程 | EMPLOYEE | `{"status":"COMPLETED"}` | `employee_id = '{currentUserId}'`, `status = 'COMPLETED'` |

##### TRN_E001: 查詢課程報名

```json
{
  "scenarioId": "TRN_E001",
  "apiEndpoint": "GET /api/v1/training/enrollments",
  "controller": "HR10EnrollmentQryController",
  "service": "GetEnrollmentsServiceImpl",
  "permission": "training:enrollment:read",
  "request": {
    "courseId": "C001"
  },
  "expectedQueryFilters": [
    {"field": "course_id", "operator": "=", "value": "C001"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

##### TRN_E005: 員工查詢自己報名

```json
{
  "scenarioId": "TRN_E005",
  "apiEndpoint": "GET /api/v1/training/enrollments/me",
  "controller": "HR10EnrollmentQryController",
  "service": "GetMyTrainingsServiceImpl",
  "permission": "training:enrollment:read",
  "request": {},
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "00000000-0000-0000-0000-000000000001"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

#### 2.3 證照查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| TRN_CT001 | 查詢員工證照 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| TRN_CT002 | 查詢有效證照 | HR | `{"status":"VALID"}` | `status = 'VALID'` |
| TRN_CT003 | 查詢即將到期證照（30 天內） | HR | `{"expiringWithin":30}` | `expiry_date <= '{today+30days}'`, `status = 'VALID'` |
| TRN_CT004 | 查詢已過期證照 | HR | `{"status":"EXPIRED"}` | `status = 'EXPIRED'` |
| TRN_CT005 | 員工查詢自己證照 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'` |
| TRN_CT006 | 依認證類型查詢 | HR | `{"certType":"PROFESSIONAL"}` | `cert_type = 'PROFESSIONAL'` |

##### TRN_CT001: 查詢員工證照

```json
{
  "scenarioId": "TRN_CT001",
  "apiEndpoint": "GET /api/v1/training/certificates",
  "controller": "HR10CertificateQryController",
  "service": "GetCertificatesServiceImpl",
  "permission": "training:certificate:read",
  "request": {
    "employeeId": "E001"
  },
  "expectedQueryFilters": [
    {"field": "employee_id", "operator": "=", "value": "E001"},
    {"field": "is_deleted", "operator": "=", "value": 0}
  ],
  "expectedResponse": {
    "statusCode": 200,
    "minRecords": 0
  }
}
```

#### 2.4 訓練紀錄查詢

| 場景 ID | 測試描述 | 模擬角色 | 輸入 (Request) | 必須包含的過濾條件 |
| :--- | :--- | :--- | :--- | :--- |
| TRN_R001 | 查詢員工訓練紀錄 | HR | `{"employeeId":"E001"}` | `employee_id = 'E001'` |
| TRN_R002 | 查詢年度訓練時數 | HR | `{"year":"2025"}` | `year = 2025` |
| TRN_R003 | 員工查詢自己紀錄 | EMPLOYEE | `{}` | `employee_id = '{currentUserId}'` |
| TRN_R004 | 查詢部門訓練紀錄 | HR | `{"deptId":"D001"}` | `department_id = 'D001'` |

---

## 補充說明

### 通用安全規則

1. **個人資料保護：** 員工只能查詢自己的訓練紀錄和證照
2. **主管審核權：** 主管可審核下屬報名申請
3. **HR 管理權：** HR 可查詢全公司資料並代為操作

### 課程狀態流程

```
DRAFT（草稿）→ OPEN（開放報名）→ CLOSED（報名截止）→ COMPLETED（已結束）
                                        ↓
                                CANCELLED（已取消）
```

### 報名狀態流程

```
REGISTERED（已報名）
     ↓
  ┌──┴──┐
  ↓     ↓
APPROVED  REJECTED（已拒絕）
(已核准)
  ↓
ATTENDED（已出席）
  ↓
  ├── COMPLETED（已完成）
  └── NO_SHOW（未到）
```

### 角色權限說明

| 角色 | 可查詢範圍 | 特殊限制 |
|:---|:---|:---|
| HR | 全公司訓練 | 完整管理權限 |
| MANAGER | 下屬訓練紀錄 | 可審核下屬報名 |
| EMPLOYEE | 僅自己 | 可報名、查詢自己紀錄 |
