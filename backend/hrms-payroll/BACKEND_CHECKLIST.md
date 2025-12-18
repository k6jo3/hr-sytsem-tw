# HR04 薪資管理服務 - 後端開發工作項目清單

**服務代碼:** HR04 (PAY)
**Package:** `com.company.hrms.payroll`
**狀態:** 🔴 未開始
**最後更新:** 2025-12-18

---

## 📋 目錄

1. [整體進度概覽](#-整體進度概覽)
2. [Controller 開立](#1-controller-開立)
3. [Swagger 文件](#2-swagger-文件)
4. [Request/Response DTO](#3-requestresponse-dto)
5. [Application Service](#4-application-service)
6. [Domain Model 設計](#5-domain-model-設計)
7. [Domain Repository 介面](#6-domain-repository-介面)
8. [Domain Service](#7-domain-service)
9. [Domain 實作](#8-domain-實作)
10. [Infrastructure - PO/Entity](#9-infrastructure---poentity)
11. [Infrastructure - DAO/Mapper](#10-infrastructure---daomapper)
12. [Infrastructure - Repository 實作](#11-infrastructure---repository-實作)
13. [Infrastructure - 外部服務客戶端](#12-infrastructure---外部服務客戶端)
14. [JUnit 單元測試](#13-junit-單元測試)
15. [資料庫 Migration](#14-資料庫-migration)

---

## 📊 整體進度概覽

| 類別 | 總項目數 | 已完成 | 進度 |
|:---|:---:|:---:|:---:|
| Controller | 8 | 0 | 0% |
| Swagger | 19 | 0 | 0% |
| Request DTO | 12 | 0 | 0% |
| Response DTO | 12 | 0 | 0% |
| Application Service | 19 | 0 | 0% |
| Domain Aggregate | 3 | 0 | 0% |
| Domain Entity | 2 | 0 | 0% |
| Domain Value Object | 12 | 0 | 0% |
| Domain Repository Interface | 4 | 0 | 0% |
| Domain Service | 4 | 0 | 0% |
| Infrastructure PO | 7 | 0 | 0% |
| Infrastructure DAO | 7 | 0 | 0% |
| Infrastructure Mapper | 7 | 0 | 0% |
| Infrastructure Repository Impl | 4 | 0 | 0% |
| External Service Client | 3 | 0 | 0% |
| JUnit Test | 30+ | 0 | 0% |
| DB Migration | 6 | 0 | 0% |
| **總計** | **150+** | **0** | **0%** |

---

## 1. Controller 開立

**路徑:** `api/controller/`

### 1.1 薪資結構 Controller

| 項目 | 檔案名稱 | HTTP 方法 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪資結構命令控制器 | `HR04SalaryStructureCmdController.java` | POST, PUT, DELETE | 🔴 |
| ☐ 薪資結構查詢控制器 | `HR04SalaryStructureQryController.java` | GET | 🔴 |

### 1.2 薪資計算批次 Controller

| 項目 | 檔案名稱 | HTTP 方法 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪資批次命令控制器 | `HR04PayrollRunCmdController.java` | POST, PUT | 🔴 |
| ☐ 薪資批次查詢控制器 | `HR04PayrollRunQryController.java` | GET | 🔴 |

### 1.3 薪資單 Controller

| 項目 | 檔案名稱 | HTTP 方法 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪資單命令控制器 | `HR04PayslipCmdController.java` | POST | 🔴 |
| ☐ 薪資單查詢控制器 | `HR04PayslipQryController.java` | GET | 🔴 |

### 1.4 薪轉檔案 Controller

| 項目 | 檔案名稱 | HTTP 方法 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪轉檔命令控制器 | `HR04BankTransferCmdController.java` | POST | 🔴 |
| ☐ 薪轉檔查詢控制器 | `HR04BankTransferQryController.java` | GET | 🔴 |

---

## 2. Swagger 文件

**每個 API 端點需要完整的 Swagger 註解**

### 2.1 薪資結構 API (5個)

| 序號 | API | 端點 | operationId | 狀態 |
|:---:|:---|:---|:---|:---:|
| 1 | ☐ 建立薪資結構 | `POST /api/v1/salary-structures` | createSalaryStructure | 🔴 |
| 2 | ☐ 查詢薪資結構清單 | `GET /api/v1/salary-structures` | listSalaryStructures | 🔴 |
| 3 | ☐ 查詢薪資結構詳情 | `GET /api/v1/salary-structures/{id}` | getSalaryStructure | 🔴 |
| 4 | ☐ 更新薪資結構 | `PUT /api/v1/salary-structures/{id}` | updateSalaryStructure | 🔴 |
| 5 | ☐ 刪除薪資結構 | `DELETE /api/v1/salary-structures/{id}` | deleteSalaryStructure | 🔴 |

### 2.2 薪資計算批次 API (8個)

| 序號 | API | 端點 | operationId | 狀態 |
|:---:|:---|:---|:---|:---:|
| 1 | ☐ 建立薪資批次 | `POST /api/v1/payroll-runs` | createPayrollRun | 🔴 |
| 2 | ☐ 查詢薪資批次清單 | `GET /api/v1/payroll-runs` | listPayrollRuns | 🔴 |
| 3 | ☐ 查詢薪資批次詳情 | `GET /api/v1/payroll-runs/{runId}` | getPayrollRun | 🔴 |
| 4 | ☐ 執行薪資計算 | `POST /api/v1/payroll-runs/{runId}/execute` | executePayrollRun | 🔴 |
| 5 | ☐ 送審薪資批次 | `PUT /api/v1/payroll-runs/{runId}/submit` | submitPayrollRun | 🔴 |
| 6 | ☐ 核准薪資批次 | `PUT /api/v1/payroll-runs/{runId}/approve` | approvePayrollRun | 🔴 |
| 7 | ☐ 發送薪資單 | `POST /api/v1/payroll-runs/{runId}/send-payslips` | sendPayslips | 🔴 |
| 8 | ☐ 產生薪轉檔案 | `POST /api/v1/payroll-runs/{runId}/bank-transfer-file` | generateBankTransferFile | 🔴 |

### 2.3 薪資單 API (4個)

| 序號 | API | 端點 | operationId | 狀態 |
|:---:|:---|:---|:---|:---:|
| 1 | ☐ 查詢我的薪資單 (ESS) | `GET /api/v1/payslips/my` | getMyPayslips | 🔴 |
| 2 | ☐ 查詢薪資單清單 | `GET /api/v1/payslips` | listPayslips | 🔴 |
| 3 | ☐ 查詢薪資單詳情 | `GET /api/v1/payslips/{payslipId}` | getPayslip | 🔴 |
| 4 | ☐ 下載薪資單PDF | `GET /api/v1/payslips/{payslipId}/pdf` | downloadPayslipPdf | 🔴 |

### 2.4 薪轉檔案 API (2個)

| 序號 | API | 端點 | operationId | 狀態 |
|:---:|:---|:---|:---|:---:|
| 1 | ☐ 下載銀行媒體檔 | `GET /api/v1/payroll-runs/{runId}/bank-transfer-file` | getBankTransferFile | 🔴 |
| 2 | ☐ 產生薪轉檔案 | `POST /api/v1/payroll-runs/{runId}/bank-transfer-file/generate` | generateBankTransfer | 🔴 |

---

## 3. Request/Response DTO

**路徑:** `api/request/` 和 `api/response/`

### 3.1 Request DTO

| 項目 | 檔案名稱 | 路徑 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 建立薪資結構請求 | `CreateSalaryStructureRequest.java` | `request/salary_structure/` | 🔴 |
| ☐ 更新薪資結構請求 | `UpdateSalaryStructureRequest.java` | `request/salary_structure/` | 🔴 |
| ☐ 查詢薪資結構請求 | `ListSalaryStructuresRequest.java` | `request/salary_structure/` | 🔴 |
| ☐ 建立薪資批次請求 | `CreatePayrollRunRequest.java` | `request/payroll_run/` | 🔴 |
| ☐ 執行薪資計算請求 | `ExecutePayrollRunRequest.java` | `request/payroll_run/` | 🔴 |
| ☐ 送審薪資批次請求 | `SubmitPayrollRunRequest.java` | `request/payroll_run/` | 🔴 |
| ☐ 核准薪資批次請求 | `ApprovePayrollRunRequest.java` | `request/payroll_run/` | 🔴 |
| ☐ 發送薪資單請求 | `SendPayslipsRequest.java` | `request/payroll_run/` | 🔴 |
| ☐ 查詢我的薪資單請求 | `GetMyPayslipsRequest.java` | `request/payslip/` | 🔴 |
| ☐ 查詢薪資單清單請求 | `ListPayslipsRequest.java` | `request/payslip/` | 🔴 |
| ☐ 產生薪轉檔請求 | `GenerateBankTransferFileRequest.java` | `request/bank_transfer/` | 🔴 |
| ☐ 下載薪轉檔請求 | `GetBankTransferFileRequest.java` | `request/bank_transfer/` | 🔴 |

### 3.2 Response DTO

| 項目 | 檔案名稱 | 路徑 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 建立薪資結構回應 | `CreateSalaryStructureResponse.java` | `response/salary_structure/` | 🔴 |
| ☐ 薪資結構詳情回應 | `SalaryStructureDetailResponse.java` | `response/salary_structure/` | 🔴 |
| ☐ 薪資結構清單回應 | `SalaryStructureListResponse.java` | `response/salary_structure/` | 🔴 |
| ☐ 建立薪資批次回應 | `CreatePayrollRunResponse.java` | `response/payroll_run/` | 🔴 |
| ☐ 薪資批次詳情回應 | `PayrollRunDetailResponse.java` | `response/payroll_run/` | 🔴 |
| ☐ 薪資批次清單回應 | `PayrollRunListResponse.java` | `response/payroll_run/` | 🔴 |
| ☐ 執行薪資計算回應 | `ExecutePayrollRunResponse.java` | `response/payroll_run/` | 🔴 |
| ☐ 我的薪資單清單回應 | `MyPayslipsResponse.java` | `response/payslip/` | 🔴 |
| ☐ 薪資單詳情回應 | `PayslipDetailResponse.java` | `response/payslip/` | 🔴 |
| ☐ 薪資單清單回應 | `PayslipListResponse.java` | `response/payslip/` | 🔴 |
| ☐ 薪轉檔回應 | `BankTransferFileResponse.java` | `response/bank_transfer/` | 🔴 |
| ☐ 通用操作回應 | `PayrollOperationResponse.java` | `response/` | 🔴 |

### 3.3 共用 VO (View Object)

| 項目 | 檔案名稱 | 路徑 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪資結構摘要 VO | `SalaryStructureSummaryVO.java` | `response/vo/` | 🔴 |
| ☐ 薪資項目 VO | `SalaryItemVO.java` | `response/vo/` | 🔴 |
| ☐ 薪資批次摘要 VO | `PayrollRunSummaryVO.java` | `response/vo/` | 🔴 |
| ☐ 薪資統計 VO | `PayrollStatisticsVO.java` | `response/vo/` | 🔴 |
| ☐ 薪資單摘要 VO | `PayslipSummaryVO.java` | `response/vo/` | 🔴 |
| ☐ 薪資單項目 VO | `PayslipItemVO.java` | `response/vo/` | 🔴 |
| ☐ 加班費明細 VO | `OvertimePayDetailVO.java` | `response/vo/` | 🔴 |
| ☐ 保險扣除 VO | `InsuranceDeductionsVO.java` | `response/vo/` | 🔴 |

---

## 4. Application Service

**路徑:** `application/service/`

### 4.1 薪資結構服務 (5個)

| 項目 | 檔案名稱 | Bean Name | 狀態 |
|:---|:---|:---|:---:|
| ☐ 建立薪資結構服務 | `CreateSalaryStructureServiceImpl.java` | `createSalaryStructureServiceImpl` | 🔴 |
| ☐ 更新薪資結構服務 | `UpdateSalaryStructureServiceImpl.java` | `updateSalaryStructureServiceImpl` | 🔴 |
| ☐ 刪除薪資結構服務 | `DeleteSalaryStructureServiceImpl.java` | `deleteSalaryStructureServiceImpl` | 🔴 |
| ☐ 查詢薪資結構服務 | `GetSalaryStructureServiceImpl.java` | `getSalaryStructureServiceImpl` | 🔴 |
| ☐ 薪資結構清單服務 | `ListSalaryStructuresServiceImpl.java` | `listSalaryStructuresServiceImpl` | 🔴 |

### 4.2 薪資批次服務 (8個)

| 項目 | 檔案名稱 | Bean Name | 狀態 |
|:---|:---|:---|:---:|
| ☐ 建立薪資批次服務 | `CreatePayrollRunServiceImpl.java` | `createPayrollRunServiceImpl` | 🔴 |
| ☐ 查詢薪資批次服務 | `GetPayrollRunServiceImpl.java` | `getPayrollRunServiceImpl` | 🔴 |
| ☐ 薪資批次清單服務 | `ListPayrollRunsServiceImpl.java` | `listPayrollRunsServiceImpl` | 🔴 |
| ☐ 執行薪資計算服務 | `ExecutePayrollRunServiceImpl.java` | `executePayrollRunServiceImpl` | 🔴 |
| ☐ 送審薪資批次服務 | `SubmitPayrollRunServiceImpl.java` | `submitPayrollRunServiceImpl` | 🔴 |
| ☐ 核准薪資批次服務 | `ApprovePayrollRunServiceImpl.java` | `approvePayrollRunServiceImpl` | 🔴 |
| ☐ 發送薪資單服務 | `SendPayslipsServiceImpl.java` | `sendPayslipsServiceImpl` | 🔴 |
| ☐ 取消薪資批次服務 | `CancelPayrollRunServiceImpl.java` | `cancelPayrollRunServiceImpl` | 🔴 |

### 4.3 薪資單服務 (4個)

| 項目 | 檔案名稱 | Bean Name | 狀態 |
|:---|:---|:---|:---:|
| ☐ 查詢我的薪資單服務 | `GetMyPayslipsServiceImpl.java` | `getMyPayslipsServiceImpl` | 🔴 |
| ☐ 薪資單清單服務 | `ListPayslipsServiceImpl.java` | `listPayslipsServiceImpl` | 🔴 |
| ☐ 查詢薪資單詳情服務 | `GetPayslipDetailServiceImpl.java` | `getPayslipDetailServiceImpl` | 🔴 |
| ☐ 下載薪資單PDF服務 | `DownloadPayslipPdfServiceImpl.java` | `downloadPayslipPdfServiceImpl` | 🔴 |

### 4.4 薪轉檔案服務 (2個)

| 項目 | 檔案名稱 | Bean Name | 狀態 |
|:---|:---|:---|:---:|
| ☐ 產生薪轉檔服務 | `GenerateBankTransferFileServiceImpl.java` | `generateBankTransferFileServiceImpl` | 🔴 |
| ☐ 下載薪轉檔服務 | `GetBankTransferFileServiceImpl.java` | `getBankTransferFileServiceImpl` | 🔴 |

---

## 5. Domain Model 設計

**路徑:** `domain/model/`

### 5.1 聚合根 (Aggregate Root)

| 項目 | 檔案名稱 | 路徑 | 職責 | 狀態 |
|:---|:---|:---|:---|:---:|
| ☐ 薪資結構聚合根 | `SalaryStructure.java` | `aggregate/` | 定義員工薪資組成與計算規則 | 🔴 |
| ☐ 薪資批次聚合根 | `PayrollRun.java` | `aggregate/` | 管理薪資計算批次生命週期 | 🔴 |
| ☐ 薪資單聚合根 | `Payslip.java` | `aggregate/` | 個人薪資明細與計算結果 | 🔴 |

### 5.2 實體 (Entity)

| 項目 | 檔案名稱 | 路徑 | 職責 | 狀態 |
|:---|:---|:---|:---|:---:|
| ☐ 薪資項目實體 | `SalaryItem.java` | `entity/` | 薪資結構中的收入/扣除項目 | 🔴 |
| ☐ 薪資單項目實體 | `PayslipItem.java` | `entity/` | 薪資單中的明細項目 | 🔴 |

### 5.3 值對象 (Value Object)

| 項目 | 檔案名稱 | 路徑 | 說明 | 狀態 |
|:---|:---|:---|:---|:---:|
| ☐ 薪資結構ID | `StructureId.java` | `valueobject/` | 薪資結構唯一識別碼 | 🔴 |
| ☐ 批次ID | `RunId.java` | `valueobject/` | 薪資批次唯一識別碼 | 🔴 |
| ☐ 薪資單ID | `PayslipId.java` | `valueobject/` | 薪資單唯一識別碼 | 🔴 |
| ☐ 計薪期間 | `PayPeriod.java` | `valueobject/` | start, end (LocalDate) | 🔴 |
| ☐ 批次統計 | `PayrollStatistics.java` | `valueobject/` | 員工數、總薪資等統計 | 🔴 |
| ☐ 加班費明細 | `OvertimePayDetail.java` | `valueobject/` | 平日/休息日/假日加班費 | 🔴 |
| ☐ 保險扣除 | `InsuranceDeductions.java` | `valueobject/` | 勞保/健保/勞退自提 | 🔴 |
| ☐ 銀行帳戶 | `BankAccount.java` | `valueobject/` | 銀行代碼、帳號(遮罩) | 🔴 |
| ☐ 薪資制度列舉 | `PayrollSystem.java` | `valueobject/` | HOURLY, MONTHLY | 🔴 |
| ☐ 領薪週期列舉 | `PayrollCycle.java` | `valueobject/` | DAILY, WEEKLY, BI_WEEKLY, MONTHLY | 🔴 |
| ☐ 項目類型列舉 | `ItemType.java` | `valueobject/` | EARNING, DEDUCTION | 🔴 |
| ☐ 薪資單狀態列舉 | `PayslipStatus.java` | `valueobject/` | DRAFT, FINALIZED, SENT | 🔴 |
| ☐ 批次狀態列舉 | `PayrollRunStatus.java` | `valueobject/` | DRAFT → CALCULATING → COMPLETED → SUBMITTED → APPROVED → PAID / CANCELLED | 🔴 |

---

## 6. Domain Repository 介面

**路徑:** `domain/repository/`

| 項目 | 檔案名稱 | 主要方法 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪資結構儲存庫 | `ISalaryStructureRepository.java` | findById, findByEmployeeId, findByEmployeeAndEffectiveDate, findByOrganization, save | 🔴 |
| ☐ 薪資批次儲存庫 | `IPayrollRunRepository.java` | findById, findByOrganization, findByOrganizationAndPeriod, save | 🔴 |
| ☐ 薪資單儲存庫 | `IPayslipRepository.java` | findById, findByPayrollRun, findByEmployeeId, findByEmployeeAndYear, save, saveAll | 🔴 |
| ☐ 薪資項目定義儲存庫 | `IPayrollItemDefinitionRepository.java` | findByOrganization, findByCode, save | 🔴 |

---

## 7. Domain Service

**路徑:** `domain/service/`

| 項目 | 檔案名稱 | 職責 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪資計算引擎 (Saga) | `PayrollCalculationDomainService.java` | 協調薪資計算流程，整合多服務數據 | 🔴 |
| ☐ 加班費計算器 | `OvertimePayCalculator.java` | 依勞基法計算加班費 (平日1.34/1.67, 休息日2.67, 假日2.0) | 🔴 |
| ☐ 所得稅計算器 | `IncomeTaxCalculator.java` | 依 2025 年級距計算所得稅 | 🔴 |
| ☐ 請假扣薪計算器 | `LeaveDeductionCalculator.java` | 計算無薪假扣薪金額 | 🔴 |

---

## 8. Domain 實作

**路徑:** `domain/`

### 8.1 聚合根方法實作

#### SalaryStructure 聚合根

| 方法 | 說明 | 狀態 |
|:---|:---|:---:|
| ☐ `createMonthly(...)` | Factory - 建立月薪制薪資結構 | 🔴 |
| ☐ `createHourly(...)` | Factory - 建立時薪制薪資結構 | 🔴 |
| ☐ `addSalaryItem(SalaryItem)` | 新增薪資項目 | 🔴 |
| ☐ `removeSalaryItem(itemId)` | 移除薪資項目 | 🔴 |
| ☐ `adjustMonthlySalary(newSalary, effectiveDate)` | 調整月薪 | 🔴 |
| ☐ `calculateMonthlyGross()` | 計算月薪資總額 | 🔴 |
| ☐ `getOvertimeHourlyRate()` | 取得加班時薪 (月薪÷240) | 🔴 |
| ☐ `calculateInsurableSalary()` | 計算投保薪資 | 🔴 |

#### PayrollRun 聚合根

| 方法 | 說明 | 狀態 |
|:---|:---|:---:|
| ☐ `create(...)` | Factory - 建立薪資批次 | 🔴 |
| ☐ `startExecution(executorId, totalEmployees)` | 開始執行計算 | 🔴 |
| ☐ `updateProgress(processedCount, failedCount)` | 更新計算進度 | 🔴 |
| ☐ `complete(finalStats)` | 完成計算 | 🔴 |
| ☐ `submit(submitterId)` | 送審 | 🔴 |
| ☐ `approve(approverId)` | 核准 | 🔴 |
| ☐ `markAsPaid(bankFileUrl)` | 標記已付款 | 🔴 |
| ☐ `cancel()` | 取消批次 | 🔴 |

#### Payslip 聚合根

| 方法 | 說明 | 狀態 |
|:---|:---|:---:|
| ☐ `calculate(...)` | Factory - 核心計算邏輯 | 🔴 |
| ☐ `finalize()` | 定案薪資單 | 🔴 |
| ☐ `markAsSent()` | 標記已發送 | 🔴 |
| ☐ `setPdfUrl(url)` | 設定PDF路徑 | 🔴 |

---

## 9. Infrastructure - PO/Entity

**路徑:** `infrastructure/po/`

| 項目 | 檔案名稱 | 對應表 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪資結構 PO | `SalaryStructurePO.java` | `salary_structures` | 🔴 |
| ☐ 薪資結構項目 PO | `SalaryStructureItemPO.java` | `salary_structure_items` | 🔴 |
| ☐ 薪資批次 PO | `PayrollRunPO.java` | `payroll_runs` | 🔴 |
| ☐ 薪資單 PO | `PayslipPO.java` | `payslips` | 🔴 |
| ☐ 薪資項目定義 PO | `PayrollItemDefinitionPO.java` | `payroll_item_definitions` | 🔴 |
| ☐ 所得稅級距 PO | `IncomeTaxBracketPO.java` | `income_tax_brackets` | 🔴 |
| ☐ 薪資稽核日誌 PO | `PayrollAuditLogPO.java` | `payroll_audit_logs` | 🔴 |

---

## 10. Infrastructure - DAO/Mapper

**路徑:** `infrastructure/dao/` 和 `infrastructure/mapper/`

### 10.1 DAO 介面

| 項目 | 檔案名稱 | 狀態 |
|:---|:---|:---:|
| ☐ 薪資結構 DAO | `SalaryStructureDAO.java` | 🔴 |
| ☐ 薪資結構項目 DAO | `SalaryStructureItemDAO.java` | 🔴 |
| ☐ 薪資批次 DAO | `PayrollRunDAO.java` | 🔴 |
| ☐ 薪資單 DAO | `PayslipDAO.java` | 🔴 |
| ☐ 薪資項目定義 DAO | `PayrollItemDefinitionDAO.java` | 🔴 |
| ☐ 所得稅級距 DAO | `IncomeTaxBracketDAO.java` | 🔴 |
| ☐ 薪資稽核日誌 DAO | `PayrollAuditLogDAO.java` | 🔴 |

### 10.2 MyBatis Mapper (XML)

**路徑:** `resources/mapper/`

| 項目 | 檔案名稱 | 狀態 |
|:---|:---|:---:|
| ☐ 薪資結構 Mapper | `SalaryStructureMapper.xml` | 🔴 |
| ☐ 薪資結構項目 Mapper | `SalaryStructureItemMapper.xml` | 🔴 |
| ☐ 薪資批次 Mapper | `PayrollRunMapper.xml` | 🔴 |
| ☐ 薪資單 Mapper | `PayslipMapper.xml` | 🔴 |
| ☐ 薪資項目定義 Mapper | `PayrollItemDefinitionMapper.xml` | 🔴 |
| ☐ 所得稅級距 Mapper | `IncomeTaxBracketMapper.xml` | 🔴 |
| ☐ 薪資稽核日誌 Mapper | `PayrollAuditLogMapper.xml` | 🔴 |

---

## 11. Infrastructure - Repository 實作

**路徑:** `infrastructure/repository/`

| 項目 | 檔案名稱 | 實作介面 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪資結構儲存庫實作 | `SalaryStructureRepositoryImpl.java` | `ISalaryStructureRepository` | 🔴 |
| ☐ 薪資批次儲存庫實作 | `PayrollRunRepositoryImpl.java` | `IPayrollRunRepository` | 🔴 |
| ☐ 薪資單儲存庫實作 | `PayslipRepositoryImpl.java` | `IPayslipRepository` | 🔴 |
| ☐ 薪資項目定義儲存庫實作 | `PayrollItemDefinitionRepositoryImpl.java` | `IPayrollItemDefinitionRepository` | 🔴 |

---

## 12. Infrastructure - 外部服務客戶端

**路徑:** `infrastructure/client/`

| 項目 | 檔案名稱 | 調用服務 | 用途 | 狀態 |
|:---|:---|:---|:---|:---:|
| ☐ 組織服務客戶端 | `OrganizationServiceClient.java` | Organization (02) | 獲取在職員工清單 | 🔴 |
| ☐ 差勤服務客戶端 | `AttendanceServiceClient.java` | Attendance (03) | 獲取加班/請假月結數據 | 🔴 |
| ☐ 保險服務客戶端 | `InsuranceServiceClient.java` | Insurance (05) | 計算勞保/健保/勞退費用 | 🔴 |

### 12.1 輔助服務

**路徑:** `infrastructure/service/`

| 項目 | 檔案名稱 | 用途 | 狀態 |
|:---|:---|:---|:---:|
| ☐ PDF 生成服務 | `PayslipPdfGenerationService.java` | 薪資單 PDF 加密生成 | 🔴 |
| ☐ 銀行媒體檔服務 | `BankTransferFileGenerationService.java` | 產生銀行薪轉檔 | 🔴 |

---

## 13. JUnit 單元測試

**路徑:** `src/test/java/com/company/hrms/payroll/`

### 13.1 Domain 測試 (最高優先級 - 100% 覆蓋率)

| 項目 | 檔案名稱 | 測試內容 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪資結構聚合根測試 | `SalaryStructureTest.java` | 建立、新增項目、計算 | 🔴 |
| ☐ 薪資批次聚合根測試 | `PayrollRunTest.java` | 狀態流轉、執行、核准 | 🔴 |
| ☐ 薪資單聚合根測試 | `PayslipTest.java` | 計算邏輯、狀態變更 | 🔴 |
| ☐ 計薪期間值對象測試 | `PayPeriodTest.java` | 建立、天數計算 | 🔴 |
| ☐ 加班費明細測試 | `OvertimePayDetailTest.java` | 加班費計算 | 🔴 |
| ☐ 保險扣除測試 | `InsuranceDeductionsTest.java` | 保險費計算 | 🔴 |

### 13.2 Domain Service 測試

| 項目 | 檔案名稱 | 測試內容 | 狀態 |
|:---|:---|:---|:---:|
| ☐ 薪資計算引擎測試 | `PayrollCalculationDomainServiceTest.java` | Saga 流程 | 🔴 |
| ☐ 加班費計算器測試 | `OvertimePayCalculatorTest.java` | 平日/休息日/假日 | 🔴 |
| ☐ 所得稅計算器測試 | `IncomeTaxCalculatorTest.java` | 各級距計算 | 🔴 |
| ☐ 請假扣薪計算器測試 | `LeaveDeductionCalculatorTest.java` | 扣薪計算 | 🔴 |

### 13.3 Application Service 測試

| 項目 | 檔案名稱 | 狀態 |
|:---|:---|:---:|
| ☐ 建立薪資結構服務測試 | `CreateSalaryStructureServiceImplTest.java` | 🔴 |
| ☐ 建立薪資批次服務測試 | `CreatePayrollRunServiceImplTest.java` | 🔴 |
| ☐ 執行薪資計算服務測試 | `ExecutePayrollRunServiceImplTest.java` | 🔴 |
| ☐ 核准薪資批次服務測試 | `ApprovePayrollRunServiceImplTest.java` | 🔴 |
| ☐ 查詢我的薪資單服務測試 | `GetMyPayslipsServiceImplTest.java` | 🔴 |

### 13.4 Controller 整合測試

| 項目 | 檔案名稱 | 狀態 |
|:---|:---|:---:|
| ☐ 薪資結構 API 測試 | `HR04SalaryStructureControllerTest.java` | 🔴 |
| ☐ 薪資批次 API 測試 | `HR04PayrollRunControllerTest.java` | 🔴 |
| ☐ 薪資單 API 測試 | `HR04PayslipControllerTest.java` | 🔴 |
| ☐ 薪轉檔 API 測試 | `HR04BankTransferControllerTest.java` | 🔴 |

### 13.5 Repository 測試

| 項目 | 檔案名稱 | 狀態 |
|:---|:---|:---:|
| ☐ 薪資結構儲存庫測試 | `SalaryStructureRepositoryImplTest.java` | 🔴 |
| ☐ 薪資批次儲存庫測試 | `PayrollRunRepositoryImplTest.java` | 🔴 |
| ☐ 薪資單儲存庫測試 | `PayslipRepositoryImplTest.java` | 🔴 |

---

## 14. 資料庫 Migration

**路徑:** `resources/db/migration/`

| 序號 | 檔案名稱 | 內容 | 狀態 |
|:---|:---|:---|:---:|
| ☐ V1 | `V1__create_salary_structures.sql` | 建立 salary_structures 表 | 🔴 |
| ☐ V2 | `V2__create_salary_structure_items.sql` | 建立 salary_structure_items 表 | 🔴 |
| ☐ V3 | `V3__create_payroll_runs.sql` | 建立 payroll_runs 表 | 🔴 |
| ☐ V4 | `V4__create_payslips.sql` | 建立 payslips 表 | 🔴 |
| ☐ V5 | `V5__create_payroll_item_definitions.sql` | 建立 payroll_item_definitions 表 | 🔴 |
| ☐ V6 | `V6__create_income_tax_brackets.sql` | 建立 income_tax_brackets 表，含 2025 年資料 | 🔴 |
| ☐ V7 | `V7__create_payroll_audit_logs.sql` | 建立 payroll_audit_logs 表 | 🔴 |

---

## 15. Domain Event 定義

**路徑:** `domain/event/`

| 項目 | 檔案名稱 | 觸發時機 | 訂閱服務 | 狀態 |
|:---|:---|:---|:---|:---:|
| ☐ 薪資結構建立事件 | `SalaryStructureCreatedEvent.java` | 建立薪資結構 | Insurance | 🔴 |
| ☐ 薪資結構變更事件 | `SalaryStructureChangedEvent.java` | 薪資調整 | Insurance | 🔴 |
| ☐ 薪資批次開始事件 | `PayrollRunStartedEvent.java` | 開始薪資計算 | - | 🔴 |
| ☐ 薪資批次完成事件 | `PayrollRunCompletedEvent.java` | 薪資計算完成 | Notification, Report | 🔴 |
| ☐ 薪資單產生事件 | `PayslipGeneratedEvent.java` | 產生薪資單 | Notification, Document | 🔴 |
| ☐ 薪資核准事件 | `PayrollApprovedEvent.java` | 薪資核准 | - | 🔴 |
| ☐ 薪資發放事件 | `PayrollPaidEvent.java` | 薪資已發放 | Report | 🔴 |
| ☐ 薪資單發送事件 | `PayslipSentEvent.java` | 薪資單已寄送 | - | 🔴 |

---

## 📋 開發優先級建議

### 第一階段：核心 Domain (2-3 週)
1. ✅ 建立 Domain Value Objects (列舉、ID、值對象)
2. ✅ 建立 Domain Entities (SalaryItem, PayslipItem)
3. ✅ 建立 Domain Aggregates (SalaryStructure, PayrollRun, Payslip)
4. ✅ 建立 Repository Interfaces
5. ✅ 建立 Domain Services (計算引擎)
6. ✅ 撰寫 Domain 單元測試 (100% 覆蓋)

### 第二階段：Infrastructure (1-2 週)
1. ✅ 建立資料庫 Migration
2. ✅ 建立 PO (Persistence Objects)
3. ✅ 建立 DAO 與 MyBatis Mapper
4. ✅ 建立 Repository Implementations
5. ✅ 撰寫 Repository 測試

### 第三階段：Application & API (2-3 週)
1. ✅ 建立 Request/Response DTOs
2. ✅ 建立 Application Services
3. ✅ 建立 Controllers 與 Swagger
4. ✅ 撰寫 Application Service 測試
5. ✅ 撰寫 Controller 整合測試

### 第四階段：外部整合 (1 週)
1. ✅ 建立外部服務客戶端
2. ✅ 建立 PDF 生成服務
3. ✅ 建立銀行媒體檔服務
4. ✅ 撰寫整合測試

---

## 📚 參考文件

| 文件 | 路徑 | 說明 |
|:---|:---|:---|
| 系統設計書 | `spec/04_薪資管理服務系統設計書_part*.md` | HR04 完整設計規格 |
| 命名規範 | `spec/系統架構設計文件_命名規範.md` | 命名規範參考 |
| 後端開發規範 | `backend/架構說明與開發規範.md` | 後端開發指引 |
| 所得稅規格 | `spec/logic_spec/tax_insurance_tables_2025.md` | 2025 年稅率級距 |
| 加班費規格 | `spec/logic_spec/variable_hours_rules.md` | 勞基法加班費計算 |

---

**Legend:**
- 🔴 未開始
- 🟡 進行中
- 🟢 已完成
- ✅ 檢查點已通過

**最後更新:** 2025-12-18
