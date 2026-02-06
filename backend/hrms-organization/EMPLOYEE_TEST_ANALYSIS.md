# EmployeeApiIntegrationTest 測試失敗分析報告

## 執行日期
2026-02-05 08:41

## 測試狀態
**失敗：13/15 測試未通過**

## 已完成的修復

### 1. ✅ 修復測試資料不匹配問題
**問題：** `organization_base_data.sql` 中的組織ID使用UUID格式，但測試和其他測試資料使用字串ID
**修復：** 將組織ID從UUID改為字串格式（'ORG001', 'ORG002'）以匹配測試期望

### 2. ✅ 確認 NationalId 格式正確
**驗證：** `A123456789` 和 `A223456789` 都是合法的台灣身份證字號格式
**規則：** `^[A-Z][12]\\d{8}$` - 首字母+性別碼(1或2)+8位數字

### 3. ✅ 確認 Service 層實作存在
**確認：** 以下 Service 都已實作
- `CreateEmployeeServiceImpl` ✓
- `GetEmployeeListServiceImpl` ✓  
- `GetEmployeeDetailServiceImpl` ✓
- ValidateEmployeeTask, CreateEmployeeTask 等 Tasks ✓

## 當前失敗測試分析

### 類別1：400 錯誤（請求驗證失敗）- 8個測試

1. `ORG_EMP_API_001_createEmployee_ShouldReturnEmployeeId` - 400 (預期200)
2. `ORG_EMP_API_002_createEmployee_DuplicateNumber_ShouldReturn409` - 400 (預期409)
3. `ORG_EMP_API_003_updateEmployee_ShouldReturnUpdatedEmployee` - 400 (預期200)
4. `ORG_EMP_API_004_updateEmployee_NotFound_ShouldReturn404` - 400 (預期404)
5. `ORG_EMP_API_008_terminateEmployee_ShouldReturnSuccess` - 400 (預期200)
6. `ORG_EMP_API_009_terminateEmployee_NotFound_ShouldReturn404` - 400 (預期404)

**可能原因：**
- Request DTO 驗證規則過於嚴格
- 測試案例缺少必填欄位
- Task 層拋出 `IllegalArgumentException`（會被當成400處理）

**需要檢查：**
1. `CreateEmployeeRequest` 中所有 `@NotBlank`, `@NotNull` 欄位
2. `UpdateEmployeeRequest` 驗證規則
3. `TerminateEmployeeRequest` 驗證規則
4. ValidateEmployeeTask 中的驗證邏輯（是否部門不存在導致異常）

### 類別2：500 錯誤（服務層異常）- 5個測試

1. `ORG_EMP_API_005_getEmployeeList_ShouldReturnEmployees` - 500 (預期200)
2. `ORG_EMP_API_006_getEmployeeDetail_ShouldReturnEmployeeDetail` - 500 (預期200)
3. `ORG_EMP_API_007_getEmployeeDetail_NotFound_ShouldReturn404` - 500 (預期404)
4. `ORG_EMP_API_010_searchByName_ShouldReturnMatchingEmployees` - 500 (預期200)
5. `ORG_EMP_API_011_filterByDepartment_ShouldReturnDepartmentEmployees` - 500 (預期200)
6. `ORG_EMP_API_012_filterByStatus_ShouldReturnFilteredEmployees` - 500 (預期200)
7. `ORG_EMP_API_013_pagination_ShouldReturnPagedResults` - 500 (預期200)

**可能原因：**
- Repository 層未完全實作（`findByQuery`, `countByQuery`）
- 資料庫Schema與代碼不匹配（欄位名稱錯誤）
- GetEmployeeListServiceImpl 中的排序欄位 `create_time` 可能不存在
- QueryBuilder 生成的SQL有誤

**需要檢查：**
1. `IEmployeeRepository.findByQuery()` 實作
2. `IEmployeeRepository.countByQuery()` 實作
3. Employee 資料表的實際Schema（特別是時間戳欄位名稱）
4. MyBatis Mapper XML 中的 SQL  語句

## 下一步修復建議

### 優先級1：修復400錯誤（創建/更新員工）

```java
// TODO: 檢查並記錄實際的驗證錯誤訊息
// 在測試中添加詳細日誌：
String responseBody = response.getResponse().getContentAsString();
log.info("Response body: {}", responseBody);
```

**行動項目：**
1. 在測試中打印完整的400錯誤響應體
2. 確認哪些必填欄位缺失或格式錯誤
3. 檢查 ValidateEmployeeTask 是否因為部門ID不匹配而拋出異常

### 優先級2：修復500錯誤（查詢功能）

**行動項目：**
1. 檢查 Employee entity/PO 的欄位映射
2. 確認 `created_at` vs `create_time` 欄位名稱
3. 實作或修復 `IEmployeeRepository.findByQuery()` 方法
4. 查看 EmployeeMapper.xml

### 優先級3：確認測試資料完整性

**行動項目：**
1. 確認 `organization_test_data.sql` 中的員工資料包含所有必要欄位
2. 確認部門資料（D001-D006）正確關聯到 ORG001
3. 執行 SQL 腳本，確認no SQL syntax errors

## 建議的調試流程

1. **單一測試深入分析**
   ```bash
   mvn -pl hrms-organization test -Dtest=EmployeeApiIntegrationTest#ORG_EMP_API_001_createEmployee_ShouldReturnEmployeeId -X
   ```
   
2. **查看詳細日誌**
   - 檢查 `target/surefire-reports/*.txt`
   - 查找 Stack trace 和異常訊息

3. **逐步修復**
   - 先修復一個400錯誤測試
   - 再修復一個500錯誤測試
   - 逐個突破

## 結論

測試失敗的根本原因可能是：
1. **資料庫Schema不匹配** - 最可能（500錯誤）
2. **驗證邏輯過嚴** - 需確認（400錯誤）
3. **Repository未完整實作** - 需檢查（500錯誤）

**建議保留 TODO 標記，待上述問題逐一排查後再移除。**
