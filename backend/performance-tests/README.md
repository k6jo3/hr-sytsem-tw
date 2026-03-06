# HRMS Performance Tests

Gatling Java DSL 效能測試模組。**不納入** parent pom 的 `<modules>`，避免常規建置觸發。

## 前置條件

- Java 21+
- Maven 3.8+
- 目標服務已啟動（`-Dspring-boot.run.profiles=local`）

## 測試場景

| Simulation | 說明 | 目標服務 |
|:---|:---|:---|
| `AuthSimulation` | 登入 + Token Refresh 壓力測試 | HR01 IAM (8081) |
| `EmployeeQuerySimulation` | 員工分頁查詢 + 部門篩選 | HR01 (8081) + HR02 (8082) |
| `MixedWorkloadSimulation` | 60% 查詢 + 30% 寫入 + 10% 重操作 | 多服務混合 |

## 執行方式

```bash
# 進入模組目錄
cd backend/performance-tests

# 執行單一場景
mvn gatling:test -Dgatling.simulationClass=com.company.hrms.perf.simulation.AuthSimulation

# 執行員工查詢場景
mvn gatling:test -Dgatling.simulationClass=com.company.hrms.perf.simulation.EmployeeQuerySimulation

# 執行混合負載場景
mvn gatling:test -Dgatling.simulationClass=com.company.hrms.perf.simulation.MixedWorkloadSimulation

# 執行全部場景
mvn gatling:test
```

## 報告

執行完畢後，HTML 報告位於 `target/gatling/` 目錄下。

## 效能基準

| 指標 | 目標值 |
|:---|:---|
| P95 回應時間 | < 3 秒（一般）/ < 5 秒（重操作） |
| 成功率 | > 95%（一般）/ > 90%（混合負載） |

## 測試資料

`src/test/resources/data/users.csv` 包含 5 個測試帳號，密碼皆為 `Admin@123`。
