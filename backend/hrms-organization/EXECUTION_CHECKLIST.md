# HR02 組織員工服務 - 後端執行清單

**版本:** 1.0
**建立日期:** 2025-12-17
**設計文件:** `spec/02_組織員工服務系統設計書*.md`

---

## 目錄

1. [專案結構](#1-專案結構)
2. [Interface Layer 介面層](#2-interface-layer-介面層)
3. [Application Layer 應用層](#3-application-layer-應用層)
4. [Domain Layer 領域層](#4-domain-layer-領域層)
5. [Infrastructure Layer 基礎設施層](#5-infrastructure-layer-基礎設施層)
6. [資料庫](#6-資料庫)
7. [測試](#7-測試)
8. [進度追蹤](#8-進度追蹤)

---

## 1. 專案結構

```
backend/hrms-organization/src/main/java/com/company/hrms/organization/
├── api/
│   ├── controller/
│   │   ├── organization/
│   │   │   ├── HR02OrganizationCmdController.java
│   │   │   └── HR02OrganizationQryController.java
│   │   ├── department/
│   │   │   ├── HR02DepartmentCmdController.java
│   │   │   └── HR02DepartmentQryController.java
│   │   ├── employee/
│   │   │   ├── HR02EmployeeCmdController.java
│   │   │   └── HR02EmployeeQryController.java
│   │   ├── contract/
│   │   │   ├── HR02ContractCmdController.java
│   │   │   └── HR02ContractQryController.java
│   │   └── ess/
│   │       ├── HR02EssCmdController.java
│   │       └── HR02EssQryController.java
│   ├── request/
│   │   ├── organization/
│   │   ├── department/
│   │   ├── employee/
│   │   ├── contract/
│   │   └── ess/
│   └── response/
│       ├── organization/
│       ├── department/
│       ├── employee/
│       ├── contract/
│       └── ess/
├── application/
│   └── service/
│       ├── organization/
│       ├── department/
│       ├── employee/
│       ├── contract/
│       └── ess/
├── domain/
│   ├── model/
│   │   ├── aggregate/
│   │   ├── entity/
│   │   └── valueobject/
│   ├── event/
│   ├── repository/
│   └── service/
└── infrastructure/
    ├── dao/
    ├── mapper/
    ├── po/
    ├── repository/
    └── security/
```

---

## 2. Interface Layer 介面層

### 2.1 Controllers

#### 2.1.1 Organization Controllers

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR02OrganizationCmdController.java` | ⬜ | P1 |
| `HR02OrganizationQryController.java` | ⬜ | P1 |

**HR02OrganizationCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | 狀態 |
|:---|:---:|:---|:---:|
| `createOrganization` | POST | `/api/v1/organizations` | ⬜ |
| `updateOrganization` | PUT | `/api/v1/organizations/{organizationId}` | ⬜ |
| `deactivateOrganization` | PUT | `/api/v1/organizations/{organizationId}/deactivate` | ⬜ |

**HR02OrganizationQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | 狀態 |
|:---|:---:|:---|:---:|
| `getOrganizationList` | GET | `/api/v1/organizations` | ⬜ |
| `getOrganizationTree` | GET | `/api/v1/organizations/{organizationId}/tree` | ⬜ |

---

#### 2.1.2 Department Controllers

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR02DepartmentCmdController.java` | ⬜ | P1 |
| `HR02DepartmentQryController.java` | ⬜ | P1 |

**HR02DepartmentCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | 狀態 |
|:---|:---:|:---|:---:|
| `createDepartment` | POST | `/api/v1/departments` | ⬜ |
| `updateDepartment` | PUT | `/api/v1/departments/{departmentId}` | ⬜ |
| `assignManager` | PUT | `/api/v1/departments/{departmentId}/manager` | ⬜ |
| `reorderDepartment` | PUT | `/api/v1/departments/{departmentId}/reorder` | ⬜ |
| `deactivateDepartment` | PUT | `/api/v1/departments/{departmentId}/deactivate` | ⬜ |

**HR02DepartmentQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | 狀態 |
|:---|:---:|:---|:---:|
| `getDepartmentDetail` | GET | `/api/v1/departments/{departmentId}` | ⬜ |
| `getDepartmentManagers` | GET | `/api/v1/departments/{departmentId}/managers` | ⬜ |

---

#### 2.1.3 Employee Controllers

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR02EmployeeCmdController.java` | ⬜ | P0 |
| `HR02EmployeeQryController.java` | ⬜ | P0 |

**HR02EmployeeCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | 狀態 |
|:---|:---:|:---|:---:|
| `createEmployee` | POST | `/api/v1/employees` | ⬜ |
| `updateEmployee` | PUT | `/api/v1/employees/{employeeId}` | ⬜ |
| `transferEmployee` | POST | `/api/v1/employees/{employeeId}/transfer` | ⬜ |
| `promoteEmployee` | POST | `/api/v1/employees/{employeeId}/promote` | ⬜ |
| `adjustSalary` | POST | `/api/v1/employees/{employeeId}/adjust-salary` | ⬜ |
| `terminateEmployee` | POST | `/api/v1/employees/{employeeId}/terminate` | ⬜ |
| `completeProbation` | POST | `/api/v1/employees/{employeeId}/complete-probation` | ⬜ |
| `importEmployees` | POST | `/api/v1/employees/import` | ⬜ |

**HR02EmployeeQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | 狀態 |
|:---|:---:|:---|:---:|
| `getEmployeeList` | GET | `/api/v1/employees` | ⬜ |
| `getEmployeeDetail` | GET | `/api/v1/employees/{employeeId}` | ⬜ |
| `getEmployeeHistory` | GET | `/api/v1/employees/{employeeId}/history` | ⬜ |
| `getEmployeeEducations` | GET | `/api/v1/employees/{employeeId}/educations` | ⬜ |
| `getEmployeeExperiences` | GET | `/api/v1/employees/{employeeId}/experiences` | ⬜ |
| `checkEmployeeNumber` | GET | `/api/v1/employees/check-number` | ⬜ |
| `checkNationalId` | GET | `/api/v1/employees/check-national-id` | ⬜ |
| `checkEmail` | GET | `/api/v1/employees/check-email` | ⬜ |
| `exportEmployees` | GET | `/api/v1/employees/export` | ⬜ |

---

#### 2.1.4 Contract Controllers

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR02ContractCmdController.java` | ⬜ | P2 |
| `HR02ContractQryController.java` | ⬜ | P2 |

**HR02ContractCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | 狀態 |
|:---|:---:|:---|:---:|
| `createContract` | POST | `/api/v1/employees/{employeeId}/contracts` | ⬜ |
| `renewContract` | PUT | `/api/v1/contracts/{contractId}/renew` | ⬜ |
| `terminateContract` | PUT | `/api/v1/contracts/{contractId}/terminate` | ⬜ |

**HR02ContractQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | 狀態 |
|:---|:---:|:---|:---:|
| `getContractList` | GET | `/api/v1/employees/{employeeId}/contracts` | ⬜ |
| `getExpiringContracts` | GET | `/api/v1/contracts/expiring` | ⬜ |

---

#### 2.1.5 ESS Controllers (Employee Self Service)

| 檔案 | 狀態 | 優先級 |
|:---|:---:|:---:|
| `HR02EssCmdController.java` | ⬜ | P1 |
| `HR02EssQryController.java` | ⬜ | P1 |

**HR02EssCmdController 方法清單:**
| 方法名稱 | HTTP | 端點 | 狀態 |
|:---|:---:|:---|:---:|
| `updateMyProfile` | PUT | `/api/v1/employees/me` | ⬜ |
| `requestCertificate` | POST | `/api/v1/employees/me/certificate-requests` | ⬜ |

**HR02EssQryController 方法清單:**
| 方法名稱 | HTTP | 端點 | 狀態 |
|:---|:---:|:---|:---:|
| `getMyProfile` | GET | `/api/v1/employees/me` | ⬜ |
| `getMyCertificateRequests` | GET | `/api/v1/employees/me/certificate-requests` | ⬜ |

---

### 2.2 Request DTOs

#### 2.2.1 Organization Requests

| 檔案 | 狀態 |
|:---|:---:|
| `CreateOrganizationRequest.java` | ⬜ |
| `UpdateOrganizationRequest.java` | ⬜ |

#### 2.2.2 Department Requests

| 檔案 | 狀態 |
|:---|:---:|
| `CreateDepartmentRequest.java` | ⬜ |
| `UpdateDepartmentRequest.java` | ⬜ |
| `AssignManagerRequest.java` | ⬜ |
| `ReorderDepartmentRequest.java` | ⬜ |

#### 2.2.3 Employee Requests

| 檔案 | 狀態 |
|:---|:---:|
| `CreateEmployeeRequest.java` | ⬜ |
| `UpdateEmployeeRequest.java` | ⬜ |
| `TransferEmployeeRequest.java` | ⬜ |
| `PromoteEmployeeRequest.java` | ⬜ |
| `AdjustSalaryRequest.java` | ⬜ |
| `TerminateEmployeeRequest.java` | ⬜ |
| `EmployeeQueryRequest.java` | ⬜ |

#### 2.2.4 Contract Requests

| 檔案 | 狀態 |
|:---|:---:|
| `CreateContractRequest.java` | ⬜ |
| `RenewContractRequest.java` | ⬜ |

#### 2.2.5 ESS Requests

| 檔案 | 狀態 |
|:---|:---:|
| `UpdateMyProfileRequest.java` | ⬜ |
| `RequestCertificateRequest.java` | ⬜ |

---

### 2.3 Response DTOs

#### 2.3.1 Organization Responses

| 檔案 | 狀態 |
|:---|:---:|
| `CreateOrganizationResponse.java` | ⬜ |
| `OrganizationDetailResponse.java` | ⬜ |
| `OrganizationListResponse.java` | ⬜ |
| `OrganizationTreeResponse.java` | ⬜ |

#### 2.3.2 Department Responses

| 檔案 | 狀態 |
|:---|:---:|
| `CreateDepartmentResponse.java` | ⬜ |
| `DepartmentDetailResponse.java` | ⬜ |
| `DepartmentManagerListResponse.java` | ⬜ |

#### 2.3.3 Employee Responses

| 檔案 | 狀態 |
|:---|:---:|
| `CreateEmployeeResponse.java` | ⬜ |
| `EmployeeDetailResponse.java` | ⬜ |
| `EmployeeListResponse.java` | ⬜ |
| `EmployeeListItemResponse.java` | ⬜ |
| `TransferEmployeeResponse.java` | ⬜ |
| `PromoteEmployeeResponse.java` | ⬜ |
| `TerminateEmployeeResponse.java` | ⬜ |
| `EmployeeHistoryResponse.java` | ⬜ |
| `EmployeeEducationListResponse.java` | ⬜ |
| `EmployeeExperienceListResponse.java` | ⬜ |
| `CheckUniqueResponse.java` | ⬜ |

#### 2.3.4 Contract Responses

| 檔案 | 狀態 |
|:---|:---:|
| `CreateContractResponse.java` | ⬜ |
| `ContractDetailResponse.java` | ⬜ |
| `ContractListResponse.java` | ⬜ |
| `ExpiringContractListResponse.java` | ⬜ |

#### 2.3.5 ESS Responses

| 檔案 | 狀態 |
|:---|:---:|
| `MyProfileResponse.java` | ⬜ |
| `CertificateRequestResponse.java` | ⬜ |
| `CertificateRequestListResponse.java` | ⬜ |

---

## 3. Application Layer 應用層

### 3.1 Organization Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `CreateOrganizationServiceImpl.java` | createOrganization | ⬜ |
| `UpdateOrganizationServiceImpl.java` | updateOrganization | ⬜ |
| `DeactivateOrganizationServiceImpl.java` | deactivateOrganization | ⬜ |
| `GetOrganizationListServiceImpl.java` | getOrganizationList | ⬜ |
| `GetOrganizationTreeServiceImpl.java` | getOrganizationTree | ⬜ |

### 3.2 Department Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `CreateDepartmentServiceImpl.java` | createDepartment | ⬜ |
| `UpdateDepartmentServiceImpl.java` | updateDepartment | ⬜ |
| `AssignManagerServiceImpl.java` | assignManager | ⬜ |
| `ReorderDepartmentServiceImpl.java` | reorderDepartment | ⬜ |
| `DeactivateDepartmentServiceImpl.java` | deactivateDepartment | ⬜ |
| `GetDepartmentDetailServiceImpl.java` | getDepartmentDetail | ⬜ |
| `GetDepartmentManagersServiceImpl.java` | getDepartmentManagers | ⬜ |

### 3.3 Employee Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `CreateEmployeeServiceImpl.java` | createEmployee | ⬜ |
| `UpdateEmployeeServiceImpl.java` | updateEmployee | ⬜ |
| `TransferEmployeeServiceImpl.java` | transferEmployee | ⬜ |
| `PromoteEmployeeServiceImpl.java` | promoteEmployee | ⬜ |
| `AdjustSalaryServiceImpl.java` | adjustSalary | ⬜ |
| `TerminateEmployeeServiceImpl.java` | terminateEmployee | ⬜ |
| `CompleteProbationServiceImpl.java` | completeProbation | ⬜ |
| `ImportEmployeesServiceImpl.java` | importEmployees | ⬜ |
| `GetEmployeeListServiceImpl.java` | getEmployeeList | ⬜ |
| `GetEmployeeDetailServiceImpl.java` | getEmployeeDetail | ⬜ |
| `GetEmployeeHistoryServiceImpl.java` | getEmployeeHistory | ⬜ |
| `GetEmployeeEducationsServiceImpl.java` | getEmployeeEducations | ⬜ |
| `GetEmployeeExperiencesServiceImpl.java` | getEmployeeExperiences | ⬜ |
| `CheckEmployeeNumberServiceImpl.java` | checkEmployeeNumber | ⬜ |
| `CheckNationalIdServiceImpl.java` | checkNationalId | ⬜ |
| `CheckEmailServiceImpl.java` | checkEmail | ⬜ |
| `ExportEmployeesServiceImpl.java` | exportEmployees | ⬜ |

### 3.4 Contract Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `CreateContractServiceImpl.java` | createContract | ⬜ |
| `RenewContractServiceImpl.java` | renewContract | ⬜ |
| `TerminateContractServiceImpl.java` | terminateContract | ⬜ |
| `GetContractListServiceImpl.java` | getContractList | ⬜ |
| `GetExpiringContractsServiceImpl.java` | getExpiringContracts | ⬜ |

### 3.5 ESS Services

| 檔案 | 對應Controller方法 | 狀態 |
|:---|:---|:---:|
| `GetMyProfileServiceImpl.java` | getMyProfile | ⬜ |
| `UpdateMyProfileServiceImpl.java` | updateMyProfile | ⬜ |
| `RequestCertificateServiceImpl.java` | requestCertificate | ⬜ |
| `GetMyCertificateRequestsServiceImpl.java` | getMyCertificateRequests | ⬜ |

---

## 4. Domain Layer 領域層

### 4.1 Aggregate Roots (聚合根)

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `Organization.java` | 組織/公司聚合根 | ⬜ |
| `Department.java` | 部門聚合根 | ⬜ |
| `Employee.java` | 員工聚合根 (核心) | ⬜ |
| `EmployeeContract.java` | 員工合約聚合根 | ⬜ |

**Employee 聚合根核心方法:**
| 方法 | 說明 | 狀態 |
|:---|:---|:---:|
| `onboard()` | 到職 - 建立新員工 | ⬜ |
| `completeProbation()` | 試用期轉正 | ⬜ |
| `transferDepartment()` | 部門調動 | ⬜ |
| `promote()` | 升遷 | ⬜ |
| `terminate()` | 離職 | ⬜ |
| `updatePersonalInfo()` | 更新個人資料 | ⬜ |
| `isActive()` | 是否在職 | ⬜ |

---

### 4.2 Entities (實體)

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `Education.java` | 學歷實體 | ⬜ |
| `WorkExperience.java` | 工作經歷實體 | ⬜ |
| `EmployeeHistory.java` | 人事歷程實體 | ⬜ |
| `CertificateRequest.java` | 證明文件申請實體 | ⬜ |

---

### 4.3 Value Objects (值對象)

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `OrganizationId.java` | 組織ID | ⬜ |
| `DepartmentId.java` | 部門ID | ⬜ |
| `EmployeeId.java` | 員工ID | ⬜ |
| `ContractId.java` | 合約ID | ⬜ |
| `EducationId.java` | 學歷ID | ⬜ |
| `ExperienceId.java` | 經歷ID | ⬜ |
| `HistoryId.java` | 歷程ID | ⬜ |
| `NationalId.java` | 身分證號 (含加密/驗證) | ⬜ |
| `Email.java` | Email (含驗證) | ⬜ |
| `Address.java` | 地址 | ⬜ |
| `EmergencyContact.java` | 緊急聯絡人 | ⬜ |
| `BankAccount.java` | 銀行帳戶 (含加密) | ⬜ |

**Enum Value Objects:**
| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `OrganizationType.java` | PARENT, SUBSIDIARY | ⬜ |
| `OrganizationStatus.java` | ACTIVE, INACTIVE | ⬜ |
| `DepartmentStatus.java` | ACTIVE, INACTIVE | ⬜ |
| `Gender.java` | MALE, FEMALE, OTHER | ⬜ |
| `MaritalStatus.java` | SINGLE, MARRIED, DIVORCED, WIDOWED | ⬜ |
| `EmploymentType.java` | FULL_TIME, CONTRACT, PART_TIME, INTERN | ⬜ |
| `EmploymentStatus.java` | PROBATION, ACTIVE, PARENTAL_LEAVE, UNPAID_LEAVE, TERMINATED | ⬜ |
| `ContractType.java` | INDEFINITE, FIXED_TERM | ⬜ |
| `ContractStatus.java` | ACTIVE, EXPIRED, TERMINATED | ⬜ |
| `EmployeeHistoryEventType.java` | ONBOARDING, PROBATION_PASSED, DEPARTMENT_TRANSFER, JOB_CHANGE, PROMOTION, SALARY_ADJUSTMENT, TERMINATION, REHIRE | ⬜ |
| `CertificateType.java` | EMPLOYMENT_CERTIFICATE, SALARY_CERTIFICATE, TAX_WITHHOLDING | ⬜ |
| `CertificateRequestStatus.java` | PENDING, APPROVED, REJECTED, COMPLETED | ⬜ |
| `Degree.java` | 高中, 專科, 學士, 碩士, 博士 | ⬜ |

---

### 4.4 Domain Events (領域事件)

| 檔案 | 觸發時機 | 訂閱服務 | 狀態 |
|:---|:---|:---|:---:|
| `EmployeeCreatedEvent.java` | 新員工到職 | IAM, Insurance, Payroll | ⬜ |
| `EmployeeProbationPassedEvent.java` | 試用期轉正 | Payroll | ⬜ |
| `EmployeeTerminatedEvent.java` | 員工離職 | IAM, Attendance, Insurance, Payroll, Project | ⬜ |
| `EmployeeDepartmentChangedEvent.java` | 部門調動 | Attendance, Payroll | ⬜ |
| `EmployeeJobChangedEvent.java` | 職務異動 | Payroll | ⬜ |
| `EmployeePromotedEvent.java` | 員工升遷 | Payroll, Performance | ⬜ |
| `EmployeeSalaryChangedEvent.java` | 調薪 | Payroll, Insurance | ⬜ |
| `EmployeeEmailChangedEvent.java` | Email變更 | IAM | ⬜ |
| `DepartmentCreatedEvent.java` | 新增部門 | - | ⬜ |
| `DepartmentManagerChangedEvent.java` | 主管異動 | Attendance | ⬜ |
| `ContractExpiringEvent.java` | 合約即將到期 | Notification | ⬜ |
| `ContractRenewedEvent.java` | 合約續約 | - | ⬜ |
| `CertificateRequestedEvent.java` | 證明文件申請 | Notification | ⬜ |
| `CertificateCompletedEvent.java` | 證明文件完成 | Notification | ⬜ |

---

### 4.5 Repository Interfaces (Repository介面)

| 檔案 | 狀態 |
|:---|:---:|
| `IOrganizationRepository.java` | ⬜ |
| `IDepartmentRepository.java` | ⬜ |
| `IEmployeeRepository.java` | ⬜ |
| `IEmployeeContractRepository.java` | ⬜ |
| `IEducationRepository.java` | ⬜ |
| `IWorkExperienceRepository.java` | ⬜ |
| `IEmployeeHistoryRepository.java` | ⬜ |
| `ICertificateRequestRepository.java` | ⬜ |

**IEmployeeRepository 方法清單:**
| 方法 | 狀態 |
|:---|:---:|
| `findById(EmployeeId id)` | ⬜ |
| `findByEmployeeNumber(String employeeNumber)` | ⬜ |
| `findByCompanyEmail(String email)` | ⬜ |
| `findByDepartmentId(UUID departmentId)` | ⬜ |
| `findByManagerId(UUID managerId)` | ⬜ |
| `findByStatus(EmploymentStatus status)` | ⬜ |
| `findAll(EmployeeQueryCriteria criteria, Pageable pageable)` | ⬜ |
| `save(Employee employee)` | ⬜ |
| `existsByEmployeeNumber(String employeeNumber)` | ⬜ |
| `existsByNationalId(String nationalId)` | ⬜ |
| `existsByCompanyEmail(String email)` | ⬜ |
| `countByDepartmentIdAndStatus(UUID departmentId, EmploymentStatus status)` | ⬜ |

---

### 4.6 Domain Services (領域服務)

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `EmployeeNumberGeneratorDomainService.java` | 員工編號產生器 | ⬜ |
| `EmployeeValidationDomainService.java` | 員工資料驗證 | ⬜ |
| `DepartmentHierarchyDomainService.java` | 部門層級管理 | ⬜ |
| `ContractExpiryCheckDomainService.java` | 合約到期檢查 | ⬜ |

---

## 5. Infrastructure Layer 基礎設施層

### 5.1 Repository Implementations (Repository實作)

| 檔案 | 對應介面 | 狀態 |
|:---|:---|:---:|
| `OrganizationRepositoryImpl.java` | IOrganizationRepository | ⬜ |
| `DepartmentRepositoryImpl.java` | IDepartmentRepository | ⬜ |
| `EmployeeRepositoryImpl.java` | IEmployeeRepository | ⬜ |
| `EmployeeContractRepositoryImpl.java` | IEmployeeContractRepository | ⬜ |
| `EducationRepositoryImpl.java` | IEducationRepository | ⬜ |
| `WorkExperienceRepositoryImpl.java` | IWorkExperienceRepository | ⬜ |
| `EmployeeHistoryRepositoryImpl.java` | IEmployeeHistoryRepository | ⬜ |
| `CertificateRequestRepositoryImpl.java` | ICertificateRequestRepository | ⬜ |

---

### 5.2 DAOs (Data Access Objects)

| 檔案 | 狀態 |
|:---|:---:|
| `OrganizationDAO.java` | ⬜ |
| `DepartmentDAO.java` | ⬜ |
| `EmployeeDAO.java` | ⬜ |
| `EmployeeContractDAO.java` | ⬜ |
| `EducationDAO.java` | ⬜ |
| `WorkExperienceDAO.java` | ⬜ |
| `EmployeeHistoryDAO.java` | ⬜ |
| `CertificateRequestDAO.java` | ⬜ |

---

### 5.3 MyBatis Mappers

| 檔案 | 狀態 |
|:---|:---:|
| `OrganizationMapper.java` | ⬜ |
| `OrganizationMapper.xml` | ⬜ |
| `DepartmentMapper.java` | ⬜ |
| `DepartmentMapper.xml` | ⬜ |
| `EmployeeMapper.java` | ⬜ |
| `EmployeeMapper.xml` | ⬜ |
| `EmployeeContractMapper.java` | ⬜ |
| `EmployeeContractMapper.xml` | ⬜ |
| `EducationMapper.java` | ⬜ |
| `EducationMapper.xml` | ⬜ |
| `WorkExperienceMapper.java` | ⬜ |
| `WorkExperienceMapper.xml` | ⬜ |
| `EmployeeHistoryMapper.java` | ⬜ |
| `EmployeeHistoryMapper.xml` | ⬜ |
| `CertificateRequestMapper.java` | ⬜ |
| `CertificateRequestMapper.xml` | ⬜ |

---

### 5.4 Persistence Objects (PO)

| 檔案 | 對應資料表 | 狀態 |
|:---|:---|:---:|
| `OrganizationPO.java` | organizations | ⬜ |
| `DepartmentPO.java` | departments | ⬜ |
| `EmployeePO.java` | employees | ⬜ |
| `EmployeeContractPO.java` | employee_contracts | ⬜ |
| `EducationPO.java` | educations | ⬜ |
| `WorkExperiencePO.java` | work_experiences | ⬜ |
| `EmployeeHistoryPO.java` | employee_history | ⬜ |
| `CertificateRequestPO.java` | certificate_requests | ⬜ |

---

### 5.5 Security & Utils

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `EncryptionService.java` | AES-256 加解密服務 | ⬜ |
| `EncryptedStringConverter.java` | JPA 加密欄位轉換器 | ⬜ |
| `ExcelImportService.java` | Excel 匯入服務 | ⬜ |
| `ExcelExportService.java` | Excel 匯出服務 | ⬜ |

---

## 6. 資料庫

### 6.1 DDL Scripts

| 檔案 | 說明 | 狀態 |
|:---|:---|:---:|
| `V1__create_organizations_table.sql` | 組織表 | ⬜ |
| `V2__create_departments_table.sql` | 部門表 | ⬜ |
| `V3__create_employees_table.sql` | 員工表 | ⬜ |
| `V4__create_employee_contracts_table.sql` | 員工合約表 | ⬜ |
| `V5__create_employee_history_table.sql` | 員工歷程表 | ⬜ |
| `V6__create_educations_table.sql` | 學歷表 | ⬜ |
| `V7__create_work_experiences_table.sql` | 工作經歷表 | ⬜ |
| `V8__create_certificate_requests_table.sql` | 證明文件申請表 | ⬜ |

### 6.2 Index Scripts

| 檔案 | 狀態 |
|:---|:---:|
| `V9__create_indexes.sql` | ⬜ |

### 6.3 Initial Data

| 檔案 | 狀態 |
|:---|:---:|
| `V10__insert_initial_data.sql` | ⬜ |

---

## 7. 測試

### 7.1 Domain Tests (必須 100% 覆蓋)

| 檔案 | 狀態 |
|:---|:---:|
| `OrganizationTest.java` | ⬜ |
| `DepartmentTest.java` | ⬜ |
| `EmployeeTest.java` | ⬜ |
| `EmployeeContractTest.java` | ⬜ |
| `NationalIdTest.java` | ⬜ |
| `EmailTest.java` | ⬜ |
| `AddressTest.java` | ⬜ |
| `BankAccountTest.java` | ⬜ |

### 7.2 Application Service Tests

| 檔案 | 狀態 |
|:---|:---:|
| `CreateEmployeeServiceImplTest.java` | ⬜ |
| `TerminateEmployeeServiceImplTest.java` | ⬜ |
| `TransferEmployeeServiceImplTest.java` | ⬜ |

### 7.3 Integration Tests

| 檔案 | 狀態 |
|:---|:---:|
| `HR02EmployeeCmdControllerIT.java` | ⬜ |
| `HR02EmployeeQryControllerIT.java` | ⬜ |

---

## 8. 進度追蹤

### 總覽

| 分類 | 完成 | 總計 | 進度 |
|:---|:---:|:---:|:---:|
| Controllers | 0 | 10 | 0% |
| Request DTOs | 0 | 14 | 0% |
| Response DTOs | 0 | 21 | 0% |
| Application Services | 0 | 36 | 0% |
| Aggregates | 0 | 4 | 0% |
| Entities | 0 | 4 | 0% |
| Value Objects | 0 | 24 | 0% |
| Domain Events | 0 | 14 | 0% |
| Repository Interfaces | 0 | 8 | 0% |
| Domain Services | 0 | 4 | 0% |
| Repository Impls | 0 | 8 | 0% |
| DAOs | 0 | 8 | 0% |
| Mappers | 0 | 16 | 0% |
| POs | 0 | 8 | 0% |
| Database Scripts | 0 | 10 | 0% |
| Tests | 0 | 11 | 0% |
| **總計** | **0** | **190** | **0%** |

### 狀態說明

- ⬜ 未開始
- 🟡 進行中
- ✅ 已完成
- ❌ 已取消

---

## 附錄: 開發順序建議

### Phase 1: 核心基礎 (P0)

1. **Domain Layer** - 先建立領域模型
   - Value Objects (IDs, Enums)
   - Aggregates (Organization, Department, Employee)
   - Repository Interfaces

2. **Infrastructure Layer** - 資料存取
   - POs
   - Mappers
   - Repository Impls
   - DAOs

3. **Database** - DDL Scripts

### Phase 2: 員工 CRUD (P0)

4. **Employee API**
   - Request/Response DTOs
   - Controllers
   - Application Services

5. **Tests**
   - Domain Tests
   - Application Service Tests

### Phase 3: 組織部門 (P1)

6. **Organization & Department API**
   - 完成組織部門相關 API

### Phase 4: ESS 與合約 (P1-P2)

7. **ESS API**
8. **Contract API**

### Phase 5: 進階功能 (P2)

9. **Excel Import/Export**
10. **Domain Events Integration**

---

**最後更新:** 2025-12-17
