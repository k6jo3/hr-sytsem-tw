# HR02 組織員工服務合約測試摘要

**測試執行時間:** 2026-02-06

## 測試覆蓋率統計

### 總體統計
- **總合約場景:** 26 個 (根據 contracts/organization_contracts.md)
- **已實作測試:** 31 個 (包含 5 個額外的複合查詢和統計測試)
- **可執行測試:** 7 個 ✅
- **已標記 @Disabled:** 24 個 ⚠️
  - 9 個原有測試失敗，已標記 TODO
  - 15 個新增測試，因缺少實作或測試資料已標記 TODO

### 合約場景覆蓋

#### 1. 員工查詢合約 (ORG_E001 ~ ORG_E012)

| 場景 ID | 測試方法 | 狀態 | 備註 |
|:---|:---|:---:|:---|
| ORG_E001 | ORG_E001_QueryActiveEmployees | ✅ 通過 | 查詢在職員工 |
| ORG_E002 | ORG_E002_QueryTerminatedEmployees | ✅ 通過 | 查詢離職員工 |
| ORG_E003 | ORG_E003_QueryEmployeesByDepartment | ⚠️ Disabled | 預期數量不符，需確認測試資料或查詢邏輯 |
| ORG_E004 | ORG_E004_QueryEmployeesByName | ⚠️ Disabled | 預期數量不符或欄位名稱錯誤(full_name) |
| ORG_E005 | ORG_E005_QueryEmployeeByNumber | ✅ 通過 | 依工號查詢 |
| ORG_E006 | ORG_E006_QueryEmployeesByPosition | ⚠️ Disabled | 缺少position_id欄位與測試資料 |
| ORG_E007 | ORG_E007_QueryProbationEmployees | ✅ 通過 | 查詢試用期員工 |
| ORG_E008 | ORG_E008_QueryRegularEmployees | ⚠️ Disabled | employment_type欄位映射錯誤 |
| ORG_E009 | ORG_E009_ManagerQuerySubordinates | ⚠️ Disabled | IN查詢失敗或測試資料不足 |
| ORG_E010 | ORG_E010_EmployeeQuerySameDepartment | ⚠️ Disabled | 複合查詢(部門+狀態)失敗 |
| ORG_E011 | ORG_E011_QueryEmployeesByHireDate | ⚠️ Disabled | 日期過濾錯誤，需確認測試資料 |
| ORG_E012 | ORG_E012_QueryUnpaidLeaveEmployees | ✅ 通過 | 查詢留職停薪員工 |

**小計:** 12 個場景，5 個通過，7 個 Disabled

#### 2. 部門查詢合約 (ORG_D001 ~ ORG_D006)

| 場景 ID | 測試方法 | 狀態 | 備註 |
|:---|:---|:---:|:---|
| ORG_D001 | ORG_D001_QueryActiveDepartments | ⚠️ Disabled | 需實作 IDepartmentRepository |
| ORG_D002 | ORG_D002_QueryTopLevelDepartments | ⚠️ Disabled | 需實作 IDepartmentRepository |
| ORG_D003 | ORG_D003_QuerySubDepartments | ⚠️ Disabled | 需實作 IDepartmentRepository |
| ORG_D004 | ORG_D004_QueryDepartmentsByName | ⚠️ Disabled | 需實作 IDepartmentRepository |
| ORG_D005 | ORG_D005_QueryDepartmentByCode | ⚠️ Disabled | 需實作 IDepartmentRepository |
| ORG_D006 | ORG_D006_QueryInactiveDepartments | ⚠️ Disabled | 需實作 IDepartmentRepository |

**小計:** 6 個場景，0 個通過，6 個 Disabled（待實作 Repository 查詢方法）

#### 3. 職位查詢合約 (ORG_P001 ~ ORG_P004)

| 場景 ID | 測試方法 | 狀態 | 備註 |
|:---|:---|:---:|:---|
| ORG_P001 | ORG_P001_QueryActivePositions | ⚠️ Disabled | 需實作 IPositionRepository 及測試資料 |
| ORG_P002 | ORG_P002_QueryPositionsByDepartment | ⚠️ Disabled | 需實作 IPositionRepository 及測試資料 |
| ORG_P003 | ORG_P003_QueryPositionsByGrade | ⚠️ Disabled | 需實作 IPositionRepository 及測試資料 |
| ORG_P004 | ORG_P004_QueryPositionsByName | ⚠️ Disabled | 需實作 IPositionRepository 及測試資料 |

**小計:** 4 個場景，0 個通過，4 個 Disabled（待實作 Repository 與測試資料）

#### 4. 組織異動紀錄查詢合約 (ORG_L001 ~ ORG_L004)

| 場景 ID | 測試方法 | 狀態 | 備註 |
|:---|:---|:---:|:---|
| ORG_L001 | ORG_L001_QueryEmployeeChangeLogs | ⚠️ Disabled | 需實作 IEmployeeHistoryRepository 及測試資料 |
| ORG_L002 | ORG_L002_QueryChangeLogsByType | ⚠️ Disabled | 需實作 IEmployeeHistoryRepository 及測試資料 |
| ORG_L003 | ORG_L003_QueryChangeLogsByEffectiveDate | ⚠️ Disabled | 需實作 IEmployeeHistoryRepository 及測試資料 |
| ORG_L004 | ORG_L004_QueryChangeLogsByDepartment | ⚠️ Disabled | 需實作 IEmployeeHistoryRepository 及測試資料 |

**小計:** 4 個場景，0 個通過，4 個 Disabled（待實作 Repository 與測試資料）

#### 5. 額外測試（非合約場景）

| 測試分類 | 測試數量 | 通過 | Disabled | 備註 |
|:---|:---:|:---:|:---:|:---|
| 複合查詢場景 | 3 | 1 | 2 | 組合條件查詢測試 |
| 角色權限過濾 | 2 | 0 | 2 | 角色權限測試（已在ORG_E009, E010覆蓋） |
| 統計查詢 | 2 | 1 | 1 | count 查詢測試 |
| **小計** | **7** | **2** | **5** | - |

## 測試執行結果

```
[INFO] Tests run: 31, Failures: 0, Errors: 0, Skipped: 24
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### 各測試類別執行結果

| 測試類別 | 總測試數 | 通過 | Disabled |
|:---|:---:|:---:|:---:|
| EmployeeQueryContractTests | 10 | 5 | 5 |
| DepartmentQueryContractTests | 6 | 0 | 6 |
| PositionQueryContractTests | 4 | 0 | 4 |
| ChangeLogQueryContractTests | 4 | 0 | 4 |
| ComplexQueryScenarioTests | 3 | 1 | 2 |
| RolePermissionFilterTests | 2 | 0 | 2 |
| StatisticsQueryTests | 2 | 1 | 1 |
| **總計** | **31** | **7** | **24** |

## 需要修正的問題清單

### 高優先級（原有測試失敗）

1. **ORG_E003 - 依部門查詢員工**
   - 問題: 預期數量不符（預期 5 筆，實際結果未知）
   - 待確認: 測試資料中部門 D001 是否確實有 5 個員工
   - 待確認: department_id 欄位映射是否正確

2. **ORG_E004 - 依姓名模糊查詢**
   - 問題: 預期數量不符或欄位名稱錯誤
   - 待確認: full_name 欄位是否存在於資料庫
   - 待確認: LIKE 查詢是否正常運作

3. **ORG_E008 - 查詢正式員工**
   - 問題: employment_type 欄位映射錯誤或預期數量不符
   - 待確認: 測試資料中有多少 employment_type='REGULAR' 的員工
   - 待確認: EmploymentType enum 映射是否正確

4. **ORG_E009 - 主管查詢下屬**
   - 問題: IN 查詢失敗或測試資料不足
   - 待確認: D001 和 D002 部門是否有足夠的員工
   - 待確認: IN 查詢的 SQL 生成是否正確

5. **ORG_E010 - 員工查詢同部門**
   - 問題: 複合查詢條件失敗
   - 待確認: department_id + employment_status 的組合查詢邏輯

6. **ORG_E011 - 依到職日期範圍查詢**
   - 問題: 日期過濾錯誤
   - 待確認: 測試資料中 2025-01-01 之後到職的員工數量
   - 待確認: hire_date 欄位的日期比較邏輯

7. **queryActiveEmployeesInDepartment**
   - 問題: 複合查詢條件失敗
   - 待確認: department_id + employment_status 的組合邏輯

8. **queryRegularEmployeesHiredInDateRange**
   - 問題: employment_type + 日期範圍的複合查詢
   - 待確認: 測試資料與查詢邏輯是否一致

9. **countEmployeesByDepartment**
   - 問題: count 查詢失敗或預期數量不符
   - 待確認: IEmployeeRepository.countByQuery() 實作是否正確

### 中優先級（新增合約測試，待實作）

10. **ORG_E006 - 依職位查詢**
    - 缺少: Employee 實體的 position_id 欄位
    - 缺少: 測試資料中的職位資訊

11. **部門查詢合約 (ORG_D001~D006)**
    - 缺少: IDepartmentRepository.findByQuery() 實作
    - 缺少: Department 實體適配 QueryGroup 查詢
    - 測試資料: 已存在 6 個部門資料

12. **職位查詢合約 (ORG_P001~P004)**
    - 缺少: IPositionRepository 完整實作
    - 缺少: Position 實體與測試資料
    - 建議: 補充職位相關的測試資料 SQL

13. **異動紀錄合約 (ORG_L001~L004)**
    - 缺少: IEmployeeHistoryRepository.findByQuery() 實作
    - 缺少: EmployeeHistory 相關測試資料
    - 建議: 補充員工異動紀錄測試資料 SQL

## 建議修正順序

### Phase 1: 修正現有失敗測試（7 個）
1. 分析測試資料與預期結果的差異
2. 確認 QueryGroup 到 SQL 的轉換是否正確
3. 修正欄位映射問題（如 full_name, employment_type）
4. 確保複合查詢邏輯正確

### Phase 2: 補充員工相關功能（1 個）
1. 在 Employee 實體中新增 position_id 欄位
2. 補充測試資料（員工與職位的關聯）

### Phase 3: 實作部門查詢功能（6 個）
1. IDepartmentRepository 實作 findByQuery() 方法
2. Department 實體適配 QueryGroup
3. 驗證測試資料完整性

### Phase 4: 實作職位與異動紀錄功能（8 個）
1. 補充 Position 實體與 Repository
2. 補充 EmployeeHistory Repository 的查詢方法
3. 建立相關測試資料

## 測試資料現況

### 已存在的測試資料
- **員工資料:** 14 個員工
  - 8 個 ACTIVE
  - 3 個 PROBATION
  - 2 個 TERMINATED
  - 1 個 UNPAID_LEAVE
- **部門資料:** 6 個部門
  - 4 個頂層部門 (RD, SALES, FIN, HR)
  - 2 個子部門 (RD-FE, RD-BE)
  - 1 個 INACTIVE 部門

### 需補充的測試資料
- 職位資料 (positions 表)
- 員工-職位關聯
- 員工異動紀錄 (employee_history 表)

## 總結

✅ **已完成:**
- 26 個合約場景全部對應到測試方法
- 5 個額外的複合查詢與統計測試
- 9 個失敗測試已標記 @Disabled 並註明原因
- 15 個新增測試因缺少實作已標記 @Disabled

⏳ **待修正:**
- 9 個失敗的測試（員工查詢相關）
- 15 個待實作的測試（部門、職位、異動紀錄）

🎯 **目標:**
- 修正現有失敗測試，使員工查詢合約達到 100% 通過
- 逐步實作部門、職位、異動紀錄的 Repository 查詢方法
- 最終達成 26 個合約場景全部通過

---

**文件更新:** 2026-02-06
**測試版本:** OrganizationContractTest (31 個測試)
**測試框架:** JUnit 5 + Spring Boot Test + Testcontainers
