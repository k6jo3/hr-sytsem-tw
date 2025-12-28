# HR02 組織員工服務 API 詳細規格

**版本:** 1.0
**日期:** 2025-12-29
**服務代碼:** HR02
**服務名稱:** Organization Service (組織員工服務)

---

## 目錄

1. [Controller 命名對照](#1-controller-命名對照)
2. [API 總覽](#2-api-總覽)
3. [組織管理 API](#3-組織管理-api)
4. [部門管理 API](#4-部門管理-api)
5. [員工管理 API](#5-員工管理-api)
6. [ESS 員工自助 API](#6-ess-員工自助-api)
7. [合約管理 API](#7-合約管理-api)

---

## 1. Controller 命名對照

| Controller 類別 | 說明 | API 數量 |
|:---|:---|:---:|
| `HR02OrganizationCmdController` | 組織管理命令 (CUD) | 2 |
| `HR02OrganizationQryController` | 組織管理查詢 (R) | 3 |
| `HR02DepartmentCmdController` | 部門管理命令 (CUD) | 4 |
| `HR02DepartmentQryController` | 部門管理查詢 (R) | 2 |
| `HR02EmployeeCmdController` | 員工管理命令 (CUD) | 5 |
| `HR02EmployeeQryController` | 員工管理查詢 (R) | 7 |
| `HR02EssCmdController` | ESS 自助命令 | 2 |
| `HR02EssQryController` | ESS 自助查詢 | 2 |
| `HR02ContractCmdController` | 合約管理命令 | 2 |
| `HR02ContractQryController` | 合約管理查詢 | 2 |

---

## 2. API 總覽

### 2.1 組織管理 API (5 個端點)

| 端點 | 方法 | Controller | 說明 | 權限 |
|:---|:---:|:---|:---|:---|
| `/api/v1/organizations` | POST | HR02OrganizationCmdController | 建立公司 | organization:create |
| `/api/v1/organizations` | GET | HR02OrganizationQryController | 查詢公司列表 | organization:read |
| `/api/v1/organizations/{id}` | GET | HR02OrganizationQryController | 查詢公司詳情 | organization:read |
| `/api/v1/organizations/{id}` | PUT | HR02OrganizationCmdController | 更新公司 | organization:update |
| `/api/v1/organizations/{id}/tree` | GET | HR02OrganizationQryController | 查詢組織樹 | organization:read |

### 2.2 部門管理 API (6 個端點)

| 端點 | 方法 | Controller | 說明 | 權限 |
|:---|:---:|:---|:---|:---|
| `/api/v1/departments` | POST | HR02DepartmentCmdController | 建立部門 | department:create |
| `/api/v1/departments/{id}` | GET | HR02DepartmentQryController | 查詢部門詳情 | department:read |
| `/api/v1/departments/{id}` | PUT | HR02DepartmentCmdController | 更新部門 | department:update |
| `/api/v1/departments/{id}` | DELETE | HR02DepartmentCmdController | 刪除部門 | department:delete |
| `/api/v1/departments/{id}/assign-manager` | PUT | HR02DepartmentCmdController | 指派主管 | department:update |
| `/api/v1/departments/{id}/deactivate` | PUT | HR02DepartmentCmdController | 停用部門 | department:update |

### 2.3 員工管理 API (12 個端點)

| 端點 | 方法 | Controller | 說明 | 權限 |
|:---|:---:|:---|:---|:---|
| `/api/v1/employees` | POST | HR02EmployeeCmdController | 建立員工 | employee:create |
| `/api/v1/employees` | GET | HR02EmployeeQryController | 查詢員工列表 | employee:read |
| `/api/v1/employees/{id}` | GET | HR02EmployeeQryController | 查詢員工詳情 | employee:read |
| `/api/v1/employees/{id}` | PUT | HR02EmployeeCmdController | 更新員工 | employee:update |
| `/api/v1/employees/{id}/terminate` | POST | HR02EmployeeCmdController | 員工離職 | employee:terminate |
| `/api/v1/employees/{id}/transfer` | POST | HR02EmployeeCmdController | 部門調動 | employee:transfer |
| `/api/v1/employees/{id}/promote` | POST | HR02EmployeeCmdController | 員工升遷 | employee:promote |
| `/api/v1/employees/{id}/regularize` | POST | HR02EmployeeCmdController | 試用期轉正 | employee:update |
| `/api/v1/employees/{id}/history` | GET | HR02EmployeeQryController | 查詢人事歷程 | employee:read |
| `/api/v1/employees/check-number` | GET | HR02EmployeeQryController | 檢查編號唯一性 | employee:read |
| `/api/v1/employees/check-email` | GET | HR02EmployeeQryController | 檢查Email唯一性 | employee:read |
| `/api/v1/employees/check-national-id` | GET | HR02EmployeeQryController | 檢查身分證號唯一性 | employee:read |

### 2.4 ESS 員工自助 API (4 個端點)

| 端點 | 方法 | Controller | 說明 | 權限 |
|:---|:---:|:---|:---|:---|
| `/api/v1/employees/me` | GET | HR02EssQryController | 查詢個人資料 | - (登入即可) |
| `/api/v1/employees/me` | PUT | HR02EssCmdController | 更新個人資料 | - (登入即可) |
| `/api/v1/employees/me/certificate-requests` | POST | HR02EssCmdController | 申請證明文件 | - (登入即可) |
| `/api/v1/employees/me/certificate-requests` | GET | HR02EssQryController | 查詢證明文件列表 | - (登入即可) |

### 2.5 合約管理 API (4 個端點)

| 端點 | 方法 | Controller | 說明 | 權限 |
|:---|:---:|:---|:---|:---|
| `/api/v1/employees/{employeeId}/contracts` | POST | HR02ContractCmdController | 建立合約 | contract:create |
| `/api/v1/employees/{employeeId}/contracts` | GET | HR02ContractQryController | 查詢合約列表 | contract:read |
| `/api/v1/contracts/{id}` | GET | HR02ContractQryController | 查詢合約詳情 | contract:read |
| `/api/v1/contracts/{id}` | PUT | HR02ContractCmdController | 更新合約 | contract:update |

---

## 3. 組織管理 API

### 3.1 建立公司

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/organizations` |
| Controller | `HR02OrganizationCmdController` |
| Service | `CreateOrganizationServiceImpl` |
| 權限 | `organization:create` |
| 版本 | v1 |

**用途說明**

建立新的公司實體，支援母公司與子公司結構。由系統管理員在企業擴張、成立子公司時使用。

**業務邏輯**

1. **驗證請求資料**
   - organizationCode 必須唯一，不可與現有公司代號重複
   - 若為子公司 (SUBSIDIARY)，parentOrganizationId 必須存在且為有效母公司
   - taxId（統一編號）格式驗證

2. **建立公司實體**
   - 產生 UUID 作為 organizationId
   - 設定 status = ACTIVE
   - 若為子公司，自動關聯至母公司

3. **記錄異動**
   - 記錄至審計日誌

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| organizationCode | String | ✅ | 1-50字元，唯一 | 公司代號 | `"SUB_A"` |
| organizationName | String | ✅ | 1-255字元 | 公司名稱 | `"子公司A"` |
| organizationType | Enum | ✅ | PARENT/SUBSIDIARY | 組織類型 | `"SUBSIDIARY"` |
| parentOrganizationId | UUID | ⬚ | 子公司必填，必須存在 | 母公司ID | `"550e8400-..."` |
| taxId | String | ⬚ | 8位數字 | 統一編號 | `"12345678"` |
| address | String | ⬚ | 最長500字元 | 公司地址 | `"台北市信義區..."` |
| phoneNumber | String | ⬚ | 電話格式 | 公司電話 | `"02-12345678"` |
| establishedDate | Date | ⬚ | 不可為未來日期 | 成立日期 | `"2020-01-01"` |

**範例：**
```json
{
  "organizationCode": "SUB_A",
  "organizationName": "子公司A",
  "organizationType": "SUBSIDIARY",
  "parentOrganizationId": "550e8400-e29b-41d4-a716-446655440000",
  "taxId": "12345678",
  "address": "台北市信義區信義路五段7號",
  "phoneNumber": "02-12345678",
  "establishedDate": "2020-01-01"
}
```

**Response**

**成功回應 (201 Created)**

| 欄位 | 類型 | 說明 |
|:---|:---|:---|
| organizationId | UUID | 新建公司ID |
| organizationCode | String | 公司代號 |
| organizationName | String | 公司名稱 |
| organizationType | Enum | 組織類型 |
| status | Enum | 公司狀態 |
| createdAt | DateTime | 建立時間 |

```json
{
  "code": "SUCCESS",
  "message": "公司建立成功",
  "data": {
    "organizationId": "550e8400-e29b-41d4-a716-446655440001",
    "organizationCode": "SUB_A",
    "organizationName": "子公司A",
    "organizationType": "SUBSIDIARY",
    "status": "ACTIVE",
    "createdAt": "2025-12-29T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_ORG_CODE_REQUIRED | 公司代號為必填 | 提供公司代號 |
| 400 | VALIDATION_ORG_CODE_FORMAT | 公司代號格式不正確 | 檢查格式（1-50字元） |
| 409 | RESOURCE_ORG_CODE_EXISTS | 公司代號已存在 | 使用其他公司代號 |
| 404 | RESOURCE_PARENT_ORG_NOT_FOUND | 母公司不存在 | 確認母公司ID正確性 |
| 400 | BUSINESS_SUBSIDIARY_REQUIRES_PARENT | 子公司必須指定母公司 | 提供 parentOrganizationId |

**領域事件**

無

---

### 3.2 查詢公司列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/organizations` |
| Controller | `HR02OrganizationQryController` |
| Service | `GetOrganizationListServiceImpl` |
| 權限 | `organization:read` |
| 版本 | v1 |

**用途說明**

查詢系統中所有公司列表，支援篩選與分頁。用於公司管理頁面顯示所有公司資訊。

**業務邏輯**

1. 根據查詢條件篩選公司
2. 支援按公司名稱、代號搜尋
3. 支援按狀態篩選
4. 返回分頁結果

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| search | String | ⬚ | - | 搜尋關鍵字（公司名稱/代號） | `"子公司"` |
| status | Enum | ⬚ | - | 狀態篩選 | `ACTIVE` |
| type | Enum | ⬚ | - | 組織類型篩選 | `SUBSIDIARY` |
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數 | `20` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "organizationId": "550e8400-e29b-41d4-a716-446655440000",
        "organizationCode": "PARENT",
        "organizationName": "母公司",
        "organizationType": "PARENT",
        "taxId": "12345678",
        "employeeCount": 200,
        "status": "ACTIVE",
        "createdAt": "2020-01-01T00:00:00Z"
      },
      {
        "organizationId": "550e8400-e29b-41d4-a716-446655440001",
        "organizationCode": "SUB_A",
        "organizationName": "子公司A",
        "organizationType": "SUBSIDIARY",
        "parentOrganizationId": "550e8400-e29b-41d4-a716-446655440000",
        "parentOrganizationName": "母公司",
        "taxId": "87654321",
        "employeeCount": 50,
        "status": "ACTIVE",
        "createdAt": "2022-06-01T00:00:00Z"
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 401 | AUTH_TOKEN_INVALID | Token 無效 | 重新登入 |
| 403 | AUTHZ_PERMISSION_DENIED | 無 organization:read 權限 | 聯繫管理員授權 |

---

### 3.3 查詢公司詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/organizations/{id}` |
| Controller | `HR02OrganizationQryController` |
| Service | `GetOrganizationDetailServiceImpl` |
| 權限 | `organization:read` |
| 版本 | v1 |

**用途說明**

查詢單一公司的詳細資訊，包含子公司統計、部門統計、員工統計等。

**業務邏輯**

1. 驗證公司ID存在
2. 查詢公司基本資訊
3. 統計子公司數量（若為母公司）
4. 統計部門數量
5. 統計員工數量

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 公司ID | `550e8400-e29b-41d4-a716-446655440000` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "organizationCode": "PARENT",
    "organizationName": "母公司",
    "organizationType": "PARENT",
    "taxId": "12345678",
    "address": "台北市信義區信義路五段7號",
    "phoneNumber": "02-12345678",
    "establishedDate": "2010-01-01",
    "status": "ACTIVE",
    "statistics": {
      "subsidiaryCount": 2,
      "departmentCount": 15,
      "activeEmployeeCount": 200,
      "totalEmployeeCount": 250
    },
    "createdAt": "2010-01-01T00:00:00Z",
    "updatedAt": "2025-06-15T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_ORG_NOT_FOUND | 公司不存在 | 確認公司ID正確性 |

---

### 3.4 更新公司

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/organizations/{id}` |
| Controller | `HR02OrganizationCmdController` |
| Service | `UpdateOrganizationServiceImpl` |
| 權限 | `organization:update` |
| 版本 | v1 |

**用途說明**

更新公司基本資訊。注意：公司代號 (organizationCode) 建立後不可更改。

**業務邏輯**

1. **驗證請求資料**
   - 公司必須存在
   - organizationCode 不可更改
   - organizationType 不可更改

2. **更新公司資訊**
   - 更新允許變更的欄位
   - 更新 updatedAt 時間戳

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 公司ID | `550e8400-e29b-41d4-a716-446655440000` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| organizationName | String | ⬚ | 1-255字元 | 公司名稱 | `"新母公司名稱"` |
| address | String | ⬚ | 最長500字元 | 公司地址 | `"台北市信義區..."` |
| phoneNumber | String | ⬚ | 電話格式 | 公司電話 | `"02-87654321"` |
| status | Enum | ⬚ | ACTIVE/INACTIVE | 公司狀態 | `"ACTIVE"` |

**範例：**
```json
{
  "organizationName": "新母公司名稱",
  "address": "台北市大安區復興南路一段1號",
  "phoneNumber": "02-87654321"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "公司更新成功",
  "data": {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "organizationCode": "PARENT",
    "organizationName": "新母公司名稱",
    "status": "ACTIVE",
    "updatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_ORG_NOT_FOUND | 公司不存在 | 確認公司ID正確性 |
| 400 | BUSINESS_CANNOT_CHANGE_ORG_CODE | 公司代號不可變更 | 移除 organizationCode 欄位 |
| 400 | BUSINESS_CANNOT_DEACTIVATE_WITH_EMPLOYEES | 有在職員工無法停用 | 先處理在職員工 |

---

### 3.5 查詢組織樹

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/organizations/{id}/tree` |
| Controller | `HR02OrganizationQryController` |
| Service | `GetOrganizationTreeServiceImpl` |
| 權限 | `organization:read` |
| 版本 | v1 |

**用途說明**

查詢指定公司的完整組織架構樹，包含所有部門層級與員工人數。用於組織架構圖頁面 (HR02-P01) 的樹狀結構顯示。

**業務邏輯**

1. 驗證公司ID存在
2. 遞迴查詢所有部門層級（最多5層）
3. 計算每個節點的員工人數
4. 包含部門主管資訊
5. 按 displayOrder 排序

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 公司ID | `550e8400-e29b-41d4-a716-446655440000` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| includeInactive | Boolean | ⬚ | false | 是否包含停用部門 | `false` |
| maxLevel | Integer | ⬚ | 5 | 最大展開層級 | `3` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "organizationName": "母公司",
    "organizationType": "PARENT",
    "employeeCount": 200,
    "departments": [
      {
        "departmentId": "dept-001",
        "departmentCode": "RD",
        "departmentName": "研發部",
        "level": 1,
        "managerId": "mgr-001",
        "managerName": "張經理",
        "employeeCount": 80,
        "status": "ACTIVE",
        "subDepartments": [
          {
            "departmentId": "dept-002",
            "departmentCode": "RD-FE",
            "departmentName": "前端組",
            "level": 2,
            "managerId": "mgr-002",
            "managerName": "李組長",
            "employeeCount": 25,
            "status": "ACTIVE",
            "subDepartments": []
          },
          {
            "departmentId": "dept-003",
            "departmentCode": "RD-BE",
            "departmentName": "後端組",
            "level": 2,
            "managerId": "mgr-003",
            "managerName": "王組長",
            "employeeCount": 30,
            "status": "ACTIVE",
            "subDepartments": []
          }
        ]
      },
      {
        "departmentId": "dept-010",
        "departmentCode": "HR",
        "departmentName": "人資部",
        "level": 1,
        "managerId": "mgr-010",
        "managerName": "陳經理",
        "employeeCount": 15,
        "status": "ACTIVE",
        "subDepartments": []
      }
    ]
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_ORG_NOT_FOUND | 公司不存在 | 確認公司ID正確性 |

---

## 4. 部門管理 API

### 4.1 建立部門

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/departments` |
| Controller | `HR02DepartmentCmdController` |
| Service | `CreateDepartmentServiceImpl` |
| 權限 | `department:create` |
| 版本 | v1 |

**用途說明**

建立新的部門，支援多層級部門結構（最多5層）。由 HR 管理員在組織調整時使用。

**業務邏輯**

1. **驗證請求資料**
   - organizationId 必須存在
   - 若有 parentDepartmentId，驗證父部門存在且屬於同一公司
   - 驗證層級不超過5層
   - departmentCode 在同一公司內唯一

2. **建立部門實體**
   - 產生 UUID 作為 departmentId
   - 設定 level = 父部門 level + 1（根部門為1）
   - 設定 status = ACTIVE
   - 設定 displayOrder

3. **發布領域事件**

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| organizationId | UUID | ✅ | 必須存在 | 所屬公司ID | `"org-uuid-001"` |
| parentDepartmentId | UUID | ⬚ | 若有則必須存在 | 父部門ID | `"dept-uuid-001"` |
| departmentCode | String | ✅ | 1-50字元，公司內唯一 | 部門代號 | `"RD-FE"` |
| departmentName | String | ✅ | 1-255字元 | 部門名稱 | `"前端組"` |
| managerId | UUID | ⬚ | 若有則必須存在 | 部門主管ID | `"emp-uuid-001"` |
| displayOrder | Integer | ⬚ | >= 0 | 顯示順序 | `1` |

**範例：**
```json
{
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "parentDepartmentId": "550e8400-e29b-41d4-a716-446655440100",
  "departmentCode": "RD-FE",
  "departmentName": "前端組",
  "managerId": "550e8400-e29b-41d4-a716-446655440200",
  "displayOrder": 1
}
```

**Response**

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "部門建立成功",
  "data": {
    "departmentId": "550e8400-e29b-41d4-a716-446655440101",
    "departmentCode": "RD-FE",
    "departmentName": "前端組",
    "level": 2,
    "parentDepartmentId": "550e8400-e29b-41d4-a716-446655440100",
    "managerId": "550e8400-e29b-41d4-a716-446655440200",
    "managerName": "李組長",
    "status": "ACTIVE",
    "createdAt": "2025-12-29T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_ORG_NOT_FOUND | 公司不存在 | 確認公司ID正確性 |
| 404 | RESOURCE_PARENT_DEPT_NOT_FOUND | 父部門不存在 | 確認父部門ID正確性 |
| 409 | RESOURCE_DEPT_CODE_EXISTS | 部門代號已存在 | 使用其他部門代號 |
| 400 | BUSINESS_MAX_DEPT_LEVEL_EXCEEDED | 部門層級超過5層 | 調整組織結構 |
| 404 | RESOURCE_MANAGER_NOT_FOUND | 主管不存在 | 確認主管ID正確性 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| DepartmentCreatedEvent | `organization.department.created` | 部門建立完成 |

---

### 4.2 查詢部門詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/departments/{id}` |
| Controller | `HR02DepartmentQryController` |
| Service | `GetDepartmentDetailServiceImpl` |
| 權限 | `department:read` |
| 版本 | v1 |

**用途說明**

查詢單一部門的詳細資訊，包含主管資訊、員工統計、子部門列表。

**業務邏輯**

1. 驗證部門存在
2. 查詢部門基本資訊
3. 查詢部門主管資訊
4. 統計部門員工人數
5. 查詢直接子部門列表

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 部門ID | `550e8400-e29b-41d4-a716-446655440100` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "departmentId": "550e8400-e29b-41d4-a716-446655440100",
    "departmentCode": "RD",
    "departmentName": "研發部",
    "level": 1,
    "organization": {
      "organizationId": "550e8400-e29b-41d4-a716-446655440000",
      "organizationName": "母公司"
    },
    "parentDepartment": null,
    "manager": {
      "employeeId": "550e8400-e29b-41d4-a716-446655440200",
      "employeeNumber": "E0050",
      "fullName": "張經理",
      "jobTitle": "研發經理"
    },
    "statistics": {
      "directEmployeeCount": 5,
      "totalEmployeeCount": 80,
      "subDepartmentCount": 4
    },
    "subDepartments": [
      {
        "departmentId": "550e8400-e29b-41d4-a716-446655440101",
        "departmentCode": "RD-FE",
        "departmentName": "前端組",
        "employeeCount": 25
      },
      {
        "departmentId": "550e8400-e29b-41d4-a716-446655440102",
        "departmentCode": "RD-BE",
        "departmentName": "後端組",
        "employeeCount": 30
      }
    ],
    "status": "ACTIVE",
    "createdAt": "2020-01-01T00:00:00Z",
    "updatedAt": "2025-06-15T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_DEPT_NOT_FOUND | 部門不存在 | 確認部門ID正確性 |

---

### 4.3 更新部門

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/departments/{id}` |
| Controller | `HR02DepartmentCmdController` |
| Service | `UpdateDepartmentServiceImpl` |
| 權限 | `department:update` |
| 版本 | v1 |

**用途說明**

更新部門基本資訊。注意：不可透過此 API 更改部門層級結構（需使用拖曳調整）。

**業務邏輯**

1. **驗證請求資料**
   - 部門必須存在
   - departmentCode 若更改需驗證唯一性
   - 不可更改 organizationId、parentDepartmentId、level

2. **更新部門資訊**

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 部門ID | `550e8400-e29b-41d4-a716-446655440100` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| departmentName | String | ⬚ | 1-255字元 | 部門名稱 | `"前端開發組"` |
| displayOrder | Integer | ⬚ | >= 0 | 顯示順序 | `2` |

**範例：**
```json
{
  "departmentName": "前端開發組",
  "displayOrder": 2
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "部門更新成功",
  "data": {
    "departmentId": "550e8400-e29b-41d4-a716-446655440101",
    "departmentCode": "RD-FE",
    "departmentName": "前端開發組",
    "status": "ACTIVE",
    "updatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_DEPT_NOT_FOUND | 部門不存在 | 確認部門ID正確性 |
| 409 | RESOURCE_DEPT_CODE_EXISTS | 部門代號已存在 | 使用其他部門代號 |

---

### 4.4 刪除部門

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `DELETE /api/v1/departments/{id}` |
| Controller | `HR02DepartmentCmdController` |
| Service | `DeleteDepartmentServiceImpl` |
| 權限 | `department:delete` |
| 版本 | v1 |

**用途說明**

永久刪除部門。僅限無員工、無子部門的空部門可以刪除。

**業務邏輯**

1. **驗證前置條件**
   - 部門必須存在
   - 部門下無在職員工
   - 部門下無子部門
   - 部門非已刪除狀態

2. **執行刪除**
   - 永久刪除部門記錄

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 部門ID | `550e8400-e29b-41d4-a716-446655440100` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "部門刪除成功",
  "data": {
    "departmentId": "550e8400-e29b-41d4-a716-446655440100",
    "deletedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_DEPT_NOT_FOUND | 部門不存在 | 確認部門ID正確性 |
| 400 | BUSINESS_DEPT_HAS_EMPLOYEES | 部門下有在職員工 | 先調動或離職員工 |
| 400 | BUSINESS_DEPT_HAS_SUBDEPTS | 部門下有子部門 | 先刪除或遷移子部門 |

---

### 4.5 指派主管

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/departments/{id}/assign-manager` |
| Controller | `HR02DepartmentCmdController` |
| Service | `AssignDepartmentManagerServiceImpl` |
| 權限 | `department:update` |
| 版本 | v1 |

**用途說明**

指派或變更部門主管。主管變更會記錄至人事歷程。

**業務邏輯**

1. **驗證請求資料**
   - 部門必須存在且為 ACTIVE 狀態
   - 新主管必須存在且為在職員工
   - 新主管建議屬於該部門或上層部門

2. **更新主管**
   - 記錄舊主管ID
   - 更新部門 managerId
   - 發布 DepartmentManagerChangedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 部門ID | `550e8400-e29b-41d4-a716-446655440100` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| managerId | UUID | ✅ | 必須存在且在職 | 新主管員工ID | `"emp-uuid-001"` |
| effectiveDate | Date | ⬚ | 不可為過去日期 | 生效日期（預設今天） | `"2026-01-01"` |
| reason | String | ⬚ | 最長500字元 | 變更原因 | `"組織調整"` |

**範例：**
```json
{
  "managerId": "550e8400-e29b-41d4-a716-446655440200",
  "effectiveDate": "2026-01-01",
  "reason": "組織調整"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "主管指派成功",
  "data": {
    "departmentId": "550e8400-e29b-41d4-a716-446655440100",
    "departmentName": "研發部",
    "oldManager": {
      "employeeId": "550e8400-e29b-41d4-a716-446655440199",
      "fullName": "前任經理"
    },
    "newManager": {
      "employeeId": "550e8400-e29b-41d4-a716-446655440200",
      "fullName": "張經理"
    },
    "effectiveDate": "2026-01-01",
    "updatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_DEPT_NOT_FOUND | 部門不存在 | 確認部門ID正確性 |
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認員工ID正確性 |
| 400 | BUSINESS_EMPLOYEE_NOT_ACTIVE | 員工非在職狀態 | 選擇在職員工 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| DepartmentManagerChangedEvent | `organization.department.manager-changed` | 主管變更，通知考勤服務 |

---

### 4.6 停用部門

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/departments/{id}/deactivate` |
| Controller | `HR02DepartmentCmdController` |
| Service | `DeactivateDepartmentServiceImpl` |
| 權限 | `department:update` |
| 版本 | v1 |

**用途說明**

停用部門（軟刪除）。停用後不會顯示在組織樹中，但資料保留供歷史查詢。

**業務邏輯**

1. **驗證前置條件**
   - 部門必須存在
   - 部門目前為 ACTIVE 狀態
   - 部門下無在職員工
   - 部門下無 ACTIVE 的子部門

2. **執行停用**
   - 更新 status = INACTIVE

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 部門ID | `550e8400-e29b-41d4-a716-446655440100` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| reason | String | ⬚ | 最長500字元 | 停用原因 | `"組織重整"` |

**範例：**
```json
{
  "reason": "組織重整"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "部門停用成功",
  "data": {
    "departmentId": "550e8400-e29b-41d4-a716-446655440100",
    "departmentName": "研發部",
    "status": "INACTIVE",
    "deactivatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_DEPT_NOT_FOUND | 部門不存在 | 確認部門ID正確性 |
| 400 | BUSINESS_DEPT_ALREADY_INACTIVE | 部門已停用 | 無需重複操作 |
| 400 | BUSINESS_DEPT_HAS_EMPLOYEES | 部門下有在職員工 | 先調動或離職員工 |
| 400 | BUSINESS_DEPT_HAS_ACTIVE_SUBDEPTS | 部門下有啟用中子部門 | 先停用子部門 |

---

## 5. 員工管理 API

### 5.1 建立員工（到職）

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/employees` |
| Controller | `HR02EmployeeCmdController` |
| Service | `CreateEmployeeServiceImpl` |
| 權限 | `employee:create` |
| 版本 | v1 |

**用途說明**

建立新員工記錄，執行完整的到職流程。這是人事系統的核心操作之一，會觸發多個下游服務的自動處理。

**業務邏輯**

1. **驗證請求資料**
   - employeeNumber 必須唯一（系統可自動產生或手動輸入）
   - nationalId（身分證號）必須唯一且格式正確
   - companyEmail 必須唯一
   - departmentId 和 managerId 必須存在

2. **建立員工實體**
   - 產生 UUID 作為 employeeId
   - 加密儲存敏感資料（nationalId、bankAccount）
   - 設定 employmentStatus = PROBATION
   - 計算 probationEndDate = hireDate + probationMonths

3. **記錄人事歷程**
   - 建立 employee_history 記錄（event_type = ONBOARDING）

4. **發布領域事件**
   - EmployeeCreatedEvent → IAM、Insurance、Payroll

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| employeeNumber | String | ⬚ | 格式 E+4位數，唯一（空則自動產生） | 員工編號 | `"E0001"` |
| firstName | String | ✅ | 1-100字元 | 名 | `"三"` |
| lastName | String | ✅ | 1-100字元 | 姓 | `"張"` |
| nationalId | String | ✅ | 台灣身分證格式，唯一 | 身分證號 | `"A123456789"` |
| dateOfBirth | Date | ✅ | 不可為未來日期 | 出生日期 | `"1990-01-01"` |
| gender | Enum | ✅ | MALE/FEMALE/OTHER | 性別 | `"MALE"` |
| maritalStatus | Enum | ⬚ | SINGLE/MARRIED/DIVORCED/WIDOWED | 婚姻狀況 | `"MARRIED"` |
| personalEmail | String | ⬚ | Email 格式 | 個人 Email | `"zhang@gmail.com"` |
| companyEmail | String | ✅ | Email 格式，唯一 | 公司 Email | `"zhang.san@company.com"` |
| mobilePhone | String | ✅ | 台灣手機格式 | 手機號碼 | `"0912345678"` |
| address | Object | ✅ | - | 地址 | 見下方 |
| emergencyContact | Object | ⬚ | - | 緊急聯絡人 | 見下方 |
| organizationId | UUID | ✅ | 必須存在 | 公司ID | `"org-uuid"` |
| departmentId | UUID | ✅ | 必須存在 | 部門ID | `"dept-uuid"` |
| managerId | UUID | ⬚ | 若有則必須存在 | 直屬主管ID | `"emp-uuid"` |
| jobTitle | String | ⬚ | 最長255字元 | 職稱 | `"前端工程師"` |
| jobLevel | String | ⬚ | 最長50字元 | 職等 | `"P3"` |
| employmentType | Enum | ✅ | FULL_TIME/CONTRACT/PART_TIME/INTERN | 雇用類型 | `"FULL_TIME"` |
| hireDate | Date | ✅ | - | 到職日期 | `"2025-01-01"` |
| probationMonths | Integer | ⬚ | 0-6，預設3 | 試用期月數 | `3` |
| bankAccount | Object | ⬚ | - | 銀行帳戶 | 見下方 |

**address 物件結構：**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| postalCode | String | ⬚ | 郵遞區號 |
| city | String | ✅ | 縣市 |
| district | String | ✅ | 區/鄉鎮 |
| street | String | ✅ | 街道地址 |

**emergencyContact 物件結構：**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| name | String | ✅ | 聯絡人姓名 |
| relationship | String | ⬚ | 關係 |
| phoneNumber | String | ✅ | 聯絡電話 |

**bankAccount 物件結構：**

| 欄位 | 類型 | 必填 | 說明 |
|:---|:---|:---:|:---|
| bankCode | String | ✅ | 銀行代碼 |
| bankName | String | ✅ | 銀行名稱 |
| accountNumber | String | ✅ | 帳號（會加密儲存） |
| accountName | String | ✅ | 戶名 |

**範例：**
```json
{
  "employeeNumber": "E0001",
  "firstName": "三",
  "lastName": "張",
  "nationalId": "A123456789",
  "dateOfBirth": "1990-01-01",
  "gender": "MALE",
  "maritalStatus": "MARRIED",
  "personalEmail": "zhang.san@gmail.com",
  "companyEmail": "zhang.san@company.com",
  "mobilePhone": "0912345678",
  "address": {
    "postalCode": "110",
    "city": "台北市",
    "district": "信義區",
    "street": "信義路五段7號"
  },
  "emergencyContact": {
    "name": "張太太",
    "relationship": "配偶",
    "phoneNumber": "0987654321"
  },
  "organizationId": "550e8400-e29b-41d4-a716-446655440000",
  "departmentId": "550e8400-e29b-41d4-a716-446655440001",
  "managerId": "550e8400-e29b-41d4-a716-446655440002",
  "jobTitle": "前端工程師",
  "jobLevel": "P3",
  "employmentType": "FULL_TIME",
  "hireDate": "2025-01-01",
  "probationMonths": 3,
  "bankAccount": {
    "bankCode": "012",
    "bankName": "台北富邦銀行",
    "accountNumber": "123456789012",
    "accountName": "張三"
  }
}
```

**Response**

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "員工建立成功",
  "data": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "employeeNumber": "E0001",
    "fullName": "張三",
    "companyEmail": "zhang.san@company.com",
    "employmentStatus": "PROBATION",
    "hireDate": "2025-01-01",
    "probationEndDate": "2025-04-01",
    "createdAt": "2025-12-29T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_REQUIRED_FIELD | 必填欄位未填 | 補填必填欄位 |
| 400 | VALIDATION_NATIONAL_ID_FORMAT | 身分證號格式錯誤 | 檢查身分證號格式 |
| 400 | VALIDATION_EMAIL_FORMAT | Email 格式錯誤 | 檢查 Email 格式 |
| 400 | VALIDATION_PHONE_FORMAT | 手機格式錯誤 | 檢查手機格式 |
| 409 | RESOURCE_EMPLOYEE_NUMBER_EXISTS | 員工編號已存在 | 使用其他編號或留空自動產生 |
| 409 | RESOURCE_NATIONAL_ID_EXISTS | 身分證號已存在 | 確認是否重複建檔 |
| 409 | RESOURCE_EMAIL_EXISTS | 公司 Email 已存在 | 使用其他 Email |
| 404 | RESOURCE_ORG_NOT_FOUND | 公司不存在 | 確認公司ID正確性 |
| 404 | RESOURCE_DEPT_NOT_FOUND | 部門不存在 | 確認部門ID正確性 |
| 404 | RESOURCE_MANAGER_NOT_FOUND | 主管不存在 | 確認主管ID正確性 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| EmployeeCreatedEvent | `organization.employee.created` | 員工建立完成 |

**事件 Payload：**
```json
{
  "eventId": "evt-uuid-001",
  "eventType": "EmployeeCreatedEvent",
  "occurredAt": "2025-12-29T10:00:00Z",
  "aggregateId": "550e8400-e29b-41d4-a716-446655440003",
  "aggregateType": "Employee",
  "payload": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "employeeNumber": "E0001",
    "companyEmail": "zhang.san@company.com",
    "organizationId": "550e8400-e29b-41d4-a716-446655440000",
    "departmentId": "550e8400-e29b-41d4-a716-446655440001",
    "hireDate": "2025-01-01",
    "roles": ["EMPLOYEE"]
  }
}
```

**後續處理：**
- ✅ IAM Service 自動建立 User 帳號
- ✅ Insurance Service 產生加保提醒
- ✅ Payroll Service 建立薪資主檔

---

### 5.2 查詢員工列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/employees` |
| Controller | `HR02EmployeeQryController` |
| Service | `GetEmployeeListServiceImpl` |
| 權限 | `employee:read` |
| 版本 | v1 |

**用途說明**

查詢員工列表，支援多條件篩選、關鍵字搜尋與分頁。用於員工列表頁面 (HR02-P03)。

**業務邏輯**

1. 根據查詢條件篩選員工
2. 支援員工編號、姓名、Email 模糊搜尋
3. 支援部門、狀態、到職日期範圍篩選
4. 返回分頁結果，包含部門路徑

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| search | String | ⬚ | - | 搜尋關鍵字（編號/姓名/Email） | `"張"` |
| status | Enum | ⬚ | - | 在職狀態 | `ACTIVE` |
| departmentId | UUID | ⬚ | - | 部門篩選（含子部門） | `"dept-uuid"` |
| organizationId | UUID | ⬚ | - | 公司篩選 | `"org-uuid"` |
| hireDateFrom | Date | ⬚ | - | 到職日期起 | `"2025-01-01"` |
| hireDateTo | Date | ⬚ | - | 到職日期迄 | `"2025-12-31"` |
| employmentType | Enum | ⬚ | - | 雇用類型 | `FULL_TIME` |
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 20 | 每頁筆數（最大100） | `20` |
| sort | String | ⬚ | hireDate,desc | 排序 | `employeeNumber,asc` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "employeeId": "550e8400-e29b-41d4-a716-446655440003",
        "employeeNumber": "E0001",
        "fullName": "張三",
        "departmentId": "550e8400-e29b-41d4-a716-446655440001",
        "departmentPath": "研發部 > 前端組",
        "jobTitle": "前端工程師",
        "employmentType": "FULL_TIME",
        "employmentStatus": "ACTIVE",
        "hireDate": "2025-01-01",
        "photoUrl": "/photos/e0001.jpg"
      },
      {
        "employeeId": "550e8400-e29b-41d4-a716-446655440004",
        "employeeNumber": "E0002",
        "fullName": "李四",
        "departmentId": "550e8400-e29b-41d4-a716-446655440002",
        "departmentPath": "研發部 > 後端組",
        "jobTitle": "後端工程師",
        "employmentType": "FULL_TIME",
        "employmentStatus": "PROBATION",
        "hireDate": "2025-11-15",
        "photoUrl": null
      }
    ],
    "page": 1,
    "size": 20,
    "totalElements": 156,
    "totalPages": 8
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | VALIDATION_INVALID_DATE_RANGE | 日期範圍不正確 | hireDateFrom 需小於等於 hireDateTo |

---

### 5.3 查詢員工詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/employees/{id}` |
| Controller | `HR02EmployeeQryController` |
| Service | `GetEmployeeDetailServiceImpl` |
| 權限 | `employee:read` |
| 版本 | v1 |

**用途說明**

查詢單一員工的完整詳細資訊，包含個人資料、職務資訊、銀行帳戶等。用於員工詳細資料頁面 (HR02-P04)。

**業務邏輯**

1. 驗證員工存在
2. 查詢員工所有資訊
3. 敏感資料遮罩處理（身分證號、銀行帳號）
4. 組合部門路徑、主管資訊

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 員工ID | `550e8400-e29b-41d4-a716-446655440003` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "employeeNumber": "E0001",
    "fullName": "張三",
    "firstName": "三",
    "lastName": "張",
    "nationalId": "A12***789",
    "dateOfBirth": "1990-01-01",
    "gender": "MALE",
    "maritalStatus": "MARRIED",
    "photoUrl": "/photos/e0001.jpg",
    "contact": {
      "companyEmail": "zhang.san@company.com",
      "personalEmail": "zhang.san@gmail.com",
      "mobilePhone": "0912345678"
    },
    "address": {
      "postalCode": "110",
      "city": "台北市",
      "district": "信義區",
      "street": "信義路五段7號"
    },
    "emergencyContact": {
      "name": "張太太",
      "relationship": "配偶",
      "phoneNumber": "0987654321"
    },
    "organization": {
      "organizationId": "550e8400-e29b-41d4-a716-446655440000",
      "organizationName": "母公司"
    },
    "department": {
      "departmentId": "550e8400-e29b-41d4-a716-446655440001",
      "departmentPath": "研發部 > 前端組"
    },
    "manager": {
      "employeeId": "550e8400-e29b-41d4-a716-446655440050",
      "employeeNumber": "E0050",
      "fullName": "李組長"
    },
    "jobTitle": "前端工程師",
    "jobLevel": "P3",
    "employmentType": "FULL_TIME",
    "employmentStatus": "ACTIVE",
    "hireDate": "2025-01-01",
    "probationEndDate": "2025-04-01",
    "terminationDate": null,
    "bankAccount": {
      "bankCode": "012",
      "bankName": "台北富邦銀行",
      "accountNumber": "***9012"
    },
    "createdAt": "2025-01-01T09:00:00Z",
    "updatedAt": "2025-06-15T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認員工ID正確性 |

---

### 5.4 更新員工

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/employees/{id}` |
| Controller | `HR02EmployeeCmdController` |
| Service | `UpdateEmployeeServiceImpl` |
| 權限 | `employee:update` |
| 版本 | v1 |

**用途說明**

更新員工基本資訊。部分欄位（如 employeeNumber、nationalId）建立後不可更改。調動、升遷、離職等操作需使用專屬 API。

**業務邏輯**

1. **驗證請求資料**
   - 員工必須存在
   - 不可更改：employeeNumber、nationalId、hireDate
   - 若更改 companyEmail 需驗證唯一性

2. **更新員工資訊**
   - 更新允許變更的欄位
   - 更新 updatedAt 時間戳

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 員工ID | `550e8400-e29b-41d4-a716-446655440003` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| firstName | String | ⬚ | 1-100字元 | 名 | `"三"` |
| lastName | String | ⬚ | 1-100字元 | 姓 | `"張"` |
| maritalStatus | Enum | ⬚ | - | 婚姻狀況 | `"MARRIED"` |
| personalEmail | String | ⬚ | Email 格式 | 個人 Email | `"new@gmail.com"` |
| companyEmail | String | ⬚ | Email 格式，唯一 | 公司 Email | `"new@company.com"` |
| mobilePhone | String | ⬚ | 電話格式 | 手機號碼 | `"0912345679"` |
| address | Object | ⬚ | - | 地址 | - |
| emergencyContact | Object | ⬚ | - | 緊急聯絡人 | - |
| jobTitle | String | ⬚ | 最長255字元 | 職稱 | `"資深前端工程師"` |
| jobLevel | String | ⬚ | 最長50字元 | 職等 | `"P4"` |
| bankAccount | Object | ⬚ | - | 銀行帳戶 | - |
| photoUrl | String | ⬚ | URL格式 | 照片網址 | `"/photos/new.jpg"` |

**範例：**
```json
{
  "mobilePhone": "0912345679",
  "address": {
    "postalCode": "106",
    "city": "台北市",
    "district": "大安區",
    "street": "復興南路一段1號"
  },
  "emergencyContact": {
    "name": "張太太",
    "relationship": "配偶",
    "phoneNumber": "0987654322"
  }
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "員工資料更新成功",
  "data": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "employeeNumber": "E0001",
    "fullName": "張三",
    "updatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認員工ID正確性 |
| 409 | RESOURCE_EMAIL_EXISTS | 公司 Email 已存在 | 使用其他 Email |
| 400 | BUSINESS_CANNOT_CHANGE_EMPLOYEE_NUMBER | 員工編號不可變更 | 移除該欄位 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| EmployeeEmailChangedEvent | `organization.employee.email-changed` | Email 變更時發布，通知 IAM 更新 |

---

### 5.5 員工離職

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/employees/{id}/terminate` |
| Controller | `HR02EmployeeCmdController` |
| Service | `TerminateEmployeeServiceImpl` |
| 權限 | `employee:terminate` |
| 版本 | v1 |

**用途說明**

執行員工離職流程，這是系統中最重要的事件之一，會觸發多個下游服務的連鎖處理。

**業務邏輯**

1. **驗證前置條件**
   - 員工必須存在
   - 員工非已離職狀態
   - 離職日期不可早於到職日期

2. **執行離職**
   - 更新 employmentStatus = TERMINATED
   - 設定 terminationDate 和 terminationReason
   - 記錄 employee_history（event_type = TERMINATION）

3. **發布領域事件（關鍵）**
   - EmployeeTerminatedEvent → 觸發多個服務

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 員工ID | `550e8400-e29b-41d4-a716-446655440003` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| terminationDate | Date | ✅ | 不可早於到職日期 | 離職日期 | `"2025-12-31"` |
| reason | String | ⬚ | 最長500字元 | 離職原因 | `"個人生涯規劃"` |

**範例：**
```json
{
  "terminationDate": "2025-12-31",
  "reason": "個人生涯規劃"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "員工離職處理成功",
  "data": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "employeeNumber": "E0001",
    "fullName": "張三",
    "terminationDate": "2025-12-31",
    "employmentStatus": "TERMINATED",
    "updatedAt": "2025-12-29T15:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認員工ID正確性 |
| 400 | BUSINESS_EMPLOYEE_ALREADY_TERMINATED | 員工已離職 | 無需重複操作 |
| 400 | BUSINESS_INVALID_TERMINATION_DATE | 離職日期早於到職日期 | 調整離職日期 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| EmployeeTerminatedEvent | `organization.employee.terminated` | **關鍵事件** |

**事件 Payload：**
```json
{
  "eventId": "evt-uuid-002",
  "eventType": "EmployeeTerminatedEvent",
  "occurredAt": "2025-12-29T15:30:00Z",
  "aggregateId": "550e8400-e29b-41d4-a716-446655440003",
  "aggregateType": "Employee",
  "payload": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "employeeNumber": "E0001",
    "terminationDate": "2025-12-31",
    "reason": "個人生涯規劃"
  }
}
```

**後續處理（關鍵）：**
- ✅ IAM Service 停用 User 帳號
- ✅ Attendance Service 計算未休假工資
- ✅ Insurance Service 產生退保提醒
- ✅ Payroll Service 執行離職結算
- ✅ Project Service 移除專案成員

---

### 5.6 部門調動

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/employees/{id}/transfer` |
| Controller | `HR02EmployeeCmdController` |
| Service | `TransferEmployeeServiceImpl` |
| 權限 | `employee:transfer` |
| 版本 | v1 |

**用途說明**

執行員工部門調動。調動可設定生效日期，會記錄至人事歷程。

**業務邏輯**

1. **驗證請求資料**
   - 員工必須存在且為在職狀態
   - 新部門必須存在且為 ACTIVE 狀態
   - 新主管若指定必須存在且為在職

2. **執行調動**
   - 記錄舊部門、舊主管資訊
   - 更新員工的 departmentId、managerId
   - 記錄 employee_history（event_type = DEPARTMENT_TRANSFER）

3. **發布領域事件**

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 員工ID | `550e8400-e29b-41d4-a716-446655440003` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| newDepartmentId | UUID | ✅ | 必須存在且 ACTIVE | 新部門ID | `"dept-uuid-002"` |
| newManagerId | UUID | ⬚ | 若有則必須存在且在職 | 新主管ID | `"emp-uuid-005"` |
| effectiveDate | Date | ✅ | 不可為過去日期 | 生效日期 | `"2026-01-01"` |
| reason | String | ⬚ | 最長500字元 | 調動原因 | `"組織調整"` |

**範例：**
```json
{
  "newDepartmentId": "550e8400-e29b-41d4-a716-446655440005",
  "newManagerId": "550e8400-e29b-41d4-a716-446655440006",
  "effectiveDate": "2026-01-01",
  "reason": "組織調整"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "部門調動成功",
  "data": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "employeeNumber": "E0001",
    "fullName": "張三",
    "oldDepartment": {
      "departmentId": "550e8400-e29b-41d4-a716-446655440001",
      "departmentName": "前端組"
    },
    "newDepartment": {
      "departmentId": "550e8400-e29b-41d4-a716-446655440005",
      "departmentName": "後端組"
    },
    "oldManager": {
      "employeeId": "550e8400-e29b-41d4-a716-446655440050",
      "fullName": "李組長"
    },
    "newManager": {
      "employeeId": "550e8400-e29b-41d4-a716-446655440006",
      "fullName": "王組長"
    },
    "effectiveDate": "2026-01-01"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認員工ID正確性 |
| 400 | BUSINESS_EMPLOYEE_NOT_ACTIVE | 員工非在職狀態 | 僅在職員工可調動 |
| 404 | RESOURCE_DEPT_NOT_FOUND | 部門不存在 | 確認部門ID正確性 |
| 400 | BUSINESS_DEPT_NOT_ACTIVE | 部門非啟用狀態 | 選擇啟用中的部門 |
| 404 | RESOURCE_MANAGER_NOT_FOUND | 主管不存在 | 確認主管ID正確性 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| EmployeeDepartmentChangedEvent | `organization.employee.department-changed` | 通知考勤、薪資服務 |

---

### 5.7 員工升遷

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/employees/{id}/promote` |
| Controller | `HR02EmployeeCmdController` |
| Service | `PromoteEmployeeServiceImpl` |
| 權限 | `employee:promote` |
| 版本 | v1 |

**用途說明**

執行員工升遷（職稱、職等變更）。升遷通常伴隨調薪，調薪部分需另行在 Payroll 服務處理。

**業務邏輯**

1. **驗證請求資料**
   - 員工必須存在且為在職狀態
   - 至少需變更 jobTitle 或 jobLevel 其一

2. **執行升遷**
   - 記錄舊職稱、舊職等
   - 更新 jobTitle、jobLevel
   - 記錄 employee_history（event_type = PROMOTION）

3. **發布領域事件**

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 員工ID | `550e8400-e29b-41d4-a716-446655440003` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| newJobTitle | String | ⬚ | 最長255字元 | 新職稱 | `"資深前端工程師"` |
| newJobLevel | String | ⬚ | 最長50字元 | 新職等 | `"P4"` |
| effectiveDate | Date | ✅ | 不可為過去日期 | 生效日期 | `"2026-01-01"` |
| reason | String | ⬚ | 最長500字元 | 升遷原因 | `"2025年度績效優異"` |

**範例：**
```json
{
  "newJobTitle": "資深前端工程師",
  "newJobLevel": "P4",
  "effectiveDate": "2026-01-01",
  "reason": "2025年度績效優異"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "員工升遷成功",
  "data": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "employeeNumber": "E0001",
    "fullName": "張三",
    "oldJobTitle": "前端工程師",
    "newJobTitle": "資深前端工程師",
    "oldJobLevel": "P3",
    "newJobLevel": "P4",
    "effectiveDate": "2026-01-01"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認員工ID正確性 |
| 400 | BUSINESS_EMPLOYEE_NOT_ACTIVE | 員工非在職狀態 | 僅在職員工可升遷 |
| 400 | VALIDATION_NO_CHANGE | 未指定任何變更 | 至少指定新職稱或新職等 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| EmployeePromotedEvent | `organization.employee.promoted` | 通知薪資、績效服務 |

---

### 5.8 試用期轉正

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/employees/{id}/regularize` |
| Controller | `HR02EmployeeCmdController` |
| Service | `RegularizeEmployeeServiceImpl` |
| 權限 | `employee:update` |
| 版本 | v1 |

**用途說明**

將試用期員工轉為正式員工。僅限目前為 PROBATION 狀態的員工可執行。

**業務邏輯**

1. **驗證前置條件**
   - 員工必須存在
   - 員工目前狀態為 PROBATION

2. **執行轉正**
   - 更新 employmentStatus = ACTIVE
   - 記錄 employee_history（event_type = PROBATION_PASSED）

3. **發布領域事件**

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 員工ID | `550e8400-e29b-41d4-a716-446655440003` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| effectiveDate | Date | ⬚ | 預設今天 | 轉正生效日期 | `"2025-04-01"` |
| remarks | String | ⬚ | 最長500字元 | 備註 | `"試用期考核通過"` |

**範例：**
```json
{
  "effectiveDate": "2025-04-01",
  "remarks": "試用期考核通過"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "試用期轉正成功",
  "data": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "employeeNumber": "E0001",
    "fullName": "張三",
    "employmentStatus": "ACTIVE",
    "regularizedAt": "2025-04-01"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認員工ID正確性 |
| 400 | BUSINESS_EMPLOYEE_NOT_PROBATION | 員工非試用期狀態 | 僅試用期員工可轉正 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| EmployeeProbationPassedEvent | `organization.employee.probation-passed` | 通知薪資服務可能調整 |

---

### 5.9 查詢人事歷程

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/employees/{id}/history` |
| Controller | `HR02EmployeeQryController` |
| Service | `GetEmployeeHistoryServiceImpl` |
| 權限 | `employee:read` |
| 版本 | v1 |

**用途說明**

查詢員工的完整人事異動歷程，包含到職、調動、升遷、調薪、離職等所有記錄。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 員工ID | `550e8400-e29b-41d4-a716-446655440003` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| eventType | Enum | ⬚ | - | 事件類型篩選 | `PROMOTION` |
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 20 | 每頁筆數 | `20` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "historyId": "hist-uuid-003",
        "eventType": "PROMOTION",
        "effectiveDate": "2026-01-01",
        "oldValue": {
          "jobTitle": "前端工程師",
          "jobLevel": "P3"
        },
        "newValue": {
          "jobTitle": "資深前端工程師",
          "jobLevel": "P4"
        },
        "reason": "2025年度績效優異",
        "createdAt": "2025-12-20T10:00:00Z"
      },
      {
        "historyId": "hist-uuid-002",
        "eventType": "PROBATION_PASSED",
        "effectiveDate": "2025-04-01",
        "oldValue": {
          "employmentStatus": "PROBATION"
        },
        "newValue": {
          "employmentStatus": "ACTIVE"
        },
        "reason": "試用期考核通過",
        "createdAt": "2025-04-01T09:00:00Z"
      },
      {
        "historyId": "hist-uuid-001",
        "eventType": "ONBOARDING",
        "effectiveDate": "2025-01-01",
        "oldValue": null,
        "newValue": {
          "departmentId": "550e8400-e29b-41d4-a716-446655440001",
          "jobTitle": "前端工程師",
          "jobLevel": "P3"
        },
        "reason": null,
        "createdAt": "2025-01-01T09:00:00Z"
      }
    ],
    "page": 1,
    "size": 20,
    "totalElements": 3,
    "totalPages": 1
  }
}
```

**事件類型列舉**

| 值 | 說明 |
|:---|:---|
| ONBOARDING | 到職 |
| PROBATION_PASSED | 試用期轉正 |
| DEPARTMENT_TRANSFER | 部門調動 |
| JOB_CHANGE | 職務異動 |
| PROMOTION | 升遷 |
| SALARY_ADJUSTMENT | 調薪 |
| TERMINATION | 離職 |
| REHIRE | 復職 |

---

### 5.10 檢查員工編號唯一性

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/employees/check-number` |
| Controller | `HR02EmployeeQryController` |
| Service | `CheckEmployeeNumberServiceImpl` |
| 權限 | `employee:read` |
| 版本 | v1 |

**用途說明**

在新增員工表單中即時檢查員工編號是否已存在。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| number | String | ✅ | 要檢查的員工編號 | `E0001` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "檢查完成",
  "data": {
    "number": "E0001",
    "available": false,
    "message": "員工編號已存在"
  }
}
```

---

### 5.11 檢查 Email 唯一性

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/employees/check-email` |
| Controller | `HR02EmployeeQryController` |
| Service | `CheckEmployeeEmailServiceImpl` |
| 權限 | `employee:read` |
| 版本 | v1 |

**用途說明**

在新增員工表單中即時檢查公司 Email 是否已存在。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| email | String | ✅ | 要檢查的 Email | `zhang.san@company.com` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "檢查完成",
  "data": {
    "email": "zhang.san@company.com",
    "available": true,
    "message": "Email 可使用"
  }
}
```

---

### 5.12 檢查身分證號唯一性

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/employees/check-national-id` |
| Controller | `HR02EmployeeQryController` |
| Service | `CheckNationalIdServiceImpl` |
| 權限 | `employee:read` |
| 版本 | v1 |

**用途說明**

在新增員工表單中即時檢查身分證號是否已存在。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| nationalId | String | ✅ | 要檢查的身分證號 | `A123456789` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "檢查完成",
  "data": {
    "nationalId": "A12***789",
    "available": false,
    "message": "身分證號已存在"
  }
}
```

---

## 6. ESS 員工自助 API

### 6.1 查詢個人資料

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/employees/me` |
| Controller | `HR02EssQryController` |
| Service | `GetMyProfileServiceImpl` |
| 權限 | - (登入即可) |
| 版本 | v1 |

**用途說明**

員工查詢自己的個人資料，用於 ESS 我的資料頁面 (HR02-P08)。

**業務邏輯**

1. 從 JWT Token 取得當前登入者的 employeeId
2. 查詢該員工的完整資料
3. 敏感資料遮罩處理

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "employeeNumber": "E0001",
    "fullName": "張三",
    "nationalId": "A12***789",
    "dateOfBirth": "1990-01-01",
    "gender": "MALE",
    "companyEmail": "zhang.san@company.com",
    "mobilePhone": "0912345678",
    "address": {
      "city": "台北市",
      "district": "信義區",
      "street": "信義路五段7號"
    },
    "department": {
      "departmentId": "dept-001",
      "departmentPath": "研發部 > 前端組"
    },
    "jobTitle": "前端工程師",
    "jobLevel": "P3",
    "hireDate": "2025-01-01",
    "bankAccount": {
      "bankName": "台北富邦銀行",
      "accountNumber": "***9012"
    }
  }
}
```

---

### 6.2 更新個人資料

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/employees/me` |
| Controller | `HR02EssCmdController` |
| Service | `UpdateMyProfileServiceImpl` |
| 權限 | - (登入即可) |
| 版本 | v1 |

**用途說明**

員工自行更新允許變更的個人資料，如聯絡方式、地址、緊急聯絡人等。

**業務邏輯**

1. 從 JWT Token 取得當前登入者的 employeeId
2. 驗證可變更欄位（不可變更職務相關資訊）
3. 更新資料

**可變更欄位限制：**
- ✅ 可變更：mobilePhone、personalEmail、address、emergencyContact
- ❌ 不可變更：employeeNumber、nationalId、companyEmail、jobTitle、department 等

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| mobilePhone | String | ⬚ | 電話格式 | 手機號碼 | `"0912345679"` |
| personalEmail | String | ⬚ | Email 格式 | 個人 Email | `"new@gmail.com"` |
| address | Object | ⬚ | - | 地址 | - |
| emergencyContact | Object | ⬚ | - | 緊急聯絡人 | - |

**範例：**
```json
{
  "mobilePhone": "0912345679",
  "address": {
    "postalCode": "106",
    "city": "台北市",
    "district": "大安區",
    "street": "復興南路一段1號"
  },
  "emergencyContact": {
    "name": "張太太",
    "relationship": "配偶",
    "phoneNumber": "0987654322"
  }
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "個人資料更新成功",
  "data": {
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "updatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | BUSINESS_CANNOT_CHANGE_RESTRICTED_FIELD | 嘗試變更受限欄位 | 僅更新允許變更的欄位 |

---

### 6.3 申請證明文件

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/employees/me/certificate-requests` |
| Controller | `HR02EssCmdController` |
| Service | `CreateCertificateRequestServiceImpl` |
| 權限 | - (登入即可) |
| 版本 | v1 |

**用途說明**

員工自行申請在職證明、薪資證明等證明文件。

**業務邏輯**

1. 從 JWT Token 取得當前登入者的 employeeId
2. 驗證員工為在職狀態
3. 建立申請記錄（status = PENDING）
4. 發布事件通知 HR 處理

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| certificateType | Enum | ✅ | 見下方列舉 | 證明文件類型 | `"EMPLOYMENT_CERTIFICATE"` |
| purpose | String | ⬚ | 最長500字元 | 申請用途 | `"申請房貸"` |
| quantity | Integer | ⬚ | 1-10，預設1 | 份數 | `2` |

**證明文件類型列舉：**

| 值 | 說明 |
|:---|:---|
| EMPLOYMENT_CERTIFICATE | 在職證明 |
| SALARY_CERTIFICATE | 薪資證明 |
| TAX_WITHHOLDING | 扣繳憑單 |

**範例：**
```json
{
  "certificateType": "EMPLOYMENT_CERTIFICATE",
  "purpose": "申請房貸",
  "quantity": 2
}
```

**Response**

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "證明文件申請成功",
  "data": {
    "requestId": "550e8400-e29b-41d4-a716-446655440010",
    "certificateType": "EMPLOYMENT_CERTIFICATE",
    "status": "PENDING",
    "requestDate": "2025-12-29T10:00:00Z",
    "estimatedCompletionDate": "2025-12-31T17:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 400 | BUSINESS_EMPLOYEE_NOT_ACTIVE | 非在職員工無法申請 | 僅在職員工可申請 |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| CertificateRequestedEvent | `organization.certificate.requested` | 通知 HR 處理申請 |

---

### 6.4 查詢證明文件列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/employees/me/certificate-requests` |
| Controller | `HR02EssQryController` |
| Service | `GetMyCertificateRequestsServiceImpl` |
| 權限 | - (登入即可) |
| 版本 | v1 |

**用途說明**

員工查詢自己的證明文件申請記錄。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Query Parameters**

| 參數名 | 類型 | 必填 | 預設值 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| status | Enum | ⬚ | - | 狀態篩選 | `PENDING` |
| page | Integer | ⬚ | 1 | 頁碼 | `1` |
| size | Integer | ⬚ | 10 | 每頁筆數 | `10` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "requestId": "550e8400-e29b-41d4-a716-446655440010",
        "certificateType": "EMPLOYMENT_CERTIFICATE",
        "certificateTypeName": "在職證明",
        "purpose": "申請房貸",
        "quantity": 2,
        "status": "COMPLETED",
        "requestDate": "2025-12-29T10:00:00Z",
        "processedAt": "2025-12-30T14:00:00Z",
        "documentUrl": "/documents/cert-001.pdf"
      },
      {
        "requestId": "550e8400-e29b-41d4-a716-446655440011",
        "certificateType": "SALARY_CERTIFICATE",
        "certificateTypeName": "薪資證明",
        "purpose": "信用卡申請",
        "quantity": 1,
        "status": "PENDING",
        "requestDate": "2025-12-28T09:00:00Z",
        "processedAt": null,
        "documentUrl": null
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 2,
    "totalPages": 1
  }
}
```

**狀態列舉：**

| 值 | 說明 |
|:---|:---|
| PENDING | 待處理 |
| APPROVED | 已核准（處理中） |
| REJECTED | 已駁回 |
| COMPLETED | 已完成 |

---

## 7. 合約管理 API

### 7.1 建立合約

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `POST /api/v1/employees/{employeeId}/contracts` |
| Controller | `HR02ContractCmdController` |
| Service | `CreateContractServiceImpl` |
| 權限 | `contract:create` |
| 版本 | v1 |

**用途說明**

為員工建立勞動合約記錄，支援不定期契約與定期契約。

**業務邏輯**

1. **驗證請求資料**
   - 員工必須存在
   - contractNumber 必須唯一
   - 若為定期契約 (FIXED_TERM)，endDate 必須大於 startDate

2. **建立合約**
   - 產生 UUID 作為 contractId
   - 設定 status = ACTIVE

3. **合約到期提醒**
   - 若為定期契約，發布 ContractCreatedEvent 供後續排程監控到期

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| employeeId | UUID | ✅ | 員工ID | `550e8400-e29b-41d4-a716-446655440003` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| contractType | Enum | ✅ | INDEFINITE/FIXED_TERM | 合約類型 | `"INDEFINITE"` |
| contractNumber | String | ✅ | 唯一 | 合約編號 | `"CON-2025-0001"` |
| startDate | Date | ✅ | - | 合約開始日期 | `"2025-01-01"` |
| endDate | Date | ⬚ | 定期契約必填，須大於 startDate | 合約結束日期 | `"2025-12-31"` |
| workingHours | Decimal | ⬚ | 正數，預設40 | 每週工時 | `40` |
| trialPeriodMonths | Integer | ⬚ | 0-6，預設0 | 試用期月數 | `3` |
| attachmentUrl | String | ⬚ | URL格式 | 合約附件網址 | `"/docs/contract.pdf"` |

**範例：**
```json
{
  "contractType": "INDEFINITE",
  "contractNumber": "CON-2025-0001",
  "startDate": "2025-01-01",
  "workingHours": 40,
  "trialPeriodMonths": 3
}
```

**Response**

**成功回應 (201 Created)**

```json
{
  "code": "SUCCESS",
  "message": "合約建立成功",
  "data": {
    "contractId": "550e8400-e29b-41d4-a716-446655440020",
    "employeeId": "550e8400-e29b-41d4-a716-446655440003",
    "contractType": "INDEFINITE",
    "contractNumber": "CON-2025-0001",
    "startDate": "2025-01-01",
    "endDate": null,
    "status": "ACTIVE",
    "createdAt": "2025-12-29T10:00:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_EMPLOYEE_NOT_FOUND | 員工不存在 | 確認員工ID正確性 |
| 409 | RESOURCE_CONTRACT_NUMBER_EXISTS | 合約編號已存在 | 使用其他編號 |
| 400 | BUSINESS_FIXED_TERM_REQUIRES_END_DATE | 定期契約需指定結束日期 | 提供 endDate |
| 400 | VALIDATION_END_DATE_BEFORE_START | 結束日期早於開始日期 | 調整日期 |

---

### 7.2 查詢員工合約列表

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/employees/{employeeId}/contracts` |
| Controller | `HR02ContractQryController` |
| Service | `GetEmployeeContractsServiceImpl` |
| 權限 | `contract:read` |
| 版本 | v1 |

**用途說明**

查詢指定員工的所有合約記錄。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| employeeId | UUID | ✅ | 員工ID | `550e8400-e29b-41d4-a716-446655440003` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "content": [
      {
        "contractId": "550e8400-e29b-41d4-a716-446655440020",
        "contractType": "INDEFINITE",
        "contractTypeName": "不定期契約",
        "contractNumber": "CON-2025-0001",
        "startDate": "2025-01-01",
        "endDate": null,
        "workingHours": 40,
        "status": "ACTIVE",
        "isExpiringSoon": false
      }
    ],
    "page": 1,
    "size": 10,
    "totalElements": 1,
    "totalPages": 1
  }
}
```

---

### 7.3 查詢合約詳情

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `GET /api/v1/contracts/{id}` |
| Controller | `HR02ContractQryController` |
| Service | `GetContractDetailServiceImpl` |
| 權限 | `contract:read` |
| 版本 | v1 |

**用途說明**

查詢單一合約的詳細資訊。

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 合約ID | `550e8400-e29b-41d4-a716-446655440020` |

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "查詢成功",
  "data": {
    "contractId": "550e8400-e29b-41d4-a716-446655440020",
    "employee": {
      "employeeId": "550e8400-e29b-41d4-a716-446655440003",
      "employeeNumber": "E0001",
      "fullName": "張三"
    },
    "contractType": "INDEFINITE",
    "contractTypeName": "不定期契約",
    "contractNumber": "CON-2025-0001",
    "startDate": "2025-01-01",
    "endDate": null,
    "workingHours": 40,
    "trialPeriodMonths": 3,
    "attachmentUrl": "/docs/contract.pdf",
    "status": "ACTIVE",
    "createdAt": "2025-01-01T09:00:00Z",
    "updatedAt": "2025-01-01T09:00:00Z"
  }
}
```

---

### 7.4 更新合約

**基本資訊**

| 項目 | 內容 |
|:---|:---|
| 端點 | `PUT /api/v1/contracts/{id}` |
| Controller | `HR02ContractCmdController` |
| Service | `UpdateContractServiceImpl` |
| 權限 | `contract:update` |
| 版本 | v1 |

**用途說明**

更新合約資訊，常用於合約續約（延長 endDate）或終止合約。

**業務邏輯**

1. **驗證請求資料**
   - 合約必須存在
   - 若為不定期契約，不可設定 endDate

2. **更新合約**
   - 若續約（延長 endDate），發布 ContractRenewedEvent

**Request**

**Headers**

| 名稱 | 必填 | 說明 |
|:---|:---:|:---|
| Authorization | ✅ | `Bearer {accessToken}` |
| Content-Type | ✅ | `application/json` |

**Path Parameters**

| 參數名 | 類型 | 必填 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|
| id | UUID | ✅ | 合約ID | `550e8400-e29b-41d4-a716-446655440020` |

**Request Body**

| 欄位 | 類型 | 必填 | 驗證規則 | 說明 | 範例 |
|:---|:---|:---:|:---|:---|:---|
| endDate | Date | ⬚ | 續約時須大於原 endDate | 新結束日期 | `"2026-12-31"` |
| workingHours | Decimal | ⬚ | 正數 | 每週工時 | `40` |
| attachmentUrl | String | ⬚ | URL格式 | 合約附件網址 | `"/docs/contract-v2.pdf"` |
| status | Enum | ⬚ | ACTIVE/TERMINATED | 合約狀態 | `"TERMINATED"` |

**範例（續約）：**
```json
{
  "endDate": "2026-12-31",
  "attachmentUrl": "/docs/contract-renewal.pdf"
}
```

**範例（終止）：**
```json
{
  "status": "TERMINATED"
}
```

**Response**

**成功回應 (200 OK)**

```json
{
  "code": "SUCCESS",
  "message": "合約更新成功",
  "data": {
    "contractId": "550e8400-e29b-41d4-a716-446655440020",
    "contractNumber": "CON-2025-0001",
    "endDate": "2026-12-31",
    "status": "ACTIVE",
    "updatedAt": "2025-12-29T10:30:00Z"
  }
}
```

**錯誤碼**

| HTTP | 錯誤碼 | 說明 | 處理建議 |
|:---:|:---|:---|:---|
| 404 | RESOURCE_CONTRACT_NOT_FOUND | 合約不存在 | 確認合約ID正確性 |
| 400 | BUSINESS_INDEFINITE_NO_END_DATE | 不定期契約不可設定結束日期 | 移除 endDate 欄位 |
| 400 | BUSINESS_RENEWAL_DATE_INVALID | 續約日期需大於原結束日期 | 調整 endDate |

**領域事件**

| 事件名稱 | Topic | 說明 |
|:---|:---|:---|
| ContractRenewedEvent | `organization.contract.renewed` | 合約續約 |

---

## 附錄：列舉值定義

### A.1 組織類型 (OrganizationType)

```java
public enum OrganizationType {
    PARENT("母公司"),
    SUBSIDIARY("子公司");
}
```

### A.2 組織狀態 (OrganizationStatus)

```java
public enum OrganizationStatus {
    ACTIVE("啟用"),
    INACTIVE("停用");
}
```

### A.3 部門狀態 (DepartmentStatus)

```java
public enum DepartmentStatus {
    ACTIVE("啟用"),
    INACTIVE("停用");
}
```

### A.4 性別 (Gender)

```java
public enum Gender {
    MALE("男"),
    FEMALE("女"),
    OTHER("其他");
}
```

### A.5 婚姻狀況 (MaritalStatus)

```java
public enum MaritalStatus {
    SINGLE("未婚"),
    MARRIED("已婚"),
    DIVORCED("離婚"),
    WIDOWED("喪偶");
}
```

### A.6 雇用類型 (EmploymentType)

```java
public enum EmploymentType {
    FULL_TIME("正職"),
    CONTRACT("約聘"),
    PART_TIME("兼職"),
    INTERN("實習");
}
```

### A.7 在職狀態 (EmploymentStatus)

```java
public enum EmploymentStatus {
    PROBATION("試用"),
    ACTIVE("在職"),
    PARENTAL_LEAVE("育嬰留停"),
    UNPAID_LEAVE("留職停薪"),
    TERMINATED("離職");
}
```

### A.8 合約類型 (ContractType)

```java
public enum ContractType {
    INDEFINITE("不定期契約"),
    FIXED_TERM("定期契約");
}
```

### A.9 合約狀態 (ContractStatus)

```java
public enum ContractStatus {
    ACTIVE("有效"),
    EXPIRED("已到期"),
    TERMINATED("已終止");
}
```

### A.10 人事歷程事件類型 (HistoryEventType)

```java
public enum HistoryEventType {
    ONBOARDING("到職"),
    PROBATION_PASSED("試用期轉正"),
    DEPARTMENT_TRANSFER("部門調動"),
    JOB_CHANGE("職務異動"),
    PROMOTION("升遷"),
    SALARY_ADJUSTMENT("調薪"),
    TERMINATION("離職"),
    REHIRE("復職");
}
```

### A.11 證明文件類型 (CertificateType)

```java
public enum CertificateType {
    EMPLOYMENT_CERTIFICATE("在職證明"),
    SALARY_CERTIFICATE("薪資證明"),
    TAX_WITHHOLDING("扣繳憑單");
}
```

### A.12 證明文件申請狀態 (CertificateRequestStatus)

```java
public enum CertificateRequestStatus {
    PENDING("待處理"),
    APPROVED("已核准"),
    REJECTED("已駁回"),
    COMPLETED("已完成");
}
```

---

**文件建立日期:** 2025-12-29
**版本:** 1.0
**API 總數:** 31 個端點
