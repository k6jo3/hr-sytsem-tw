# Environment Setup & Troubleshooting Skill

## 使用時機
環境建置、服務啟動、問題排除、開發環境初始化時使用。
觸發關鍵字：`/env-setup`、「環境建置」、「啟動服務」、「環境問題」、「無法啟動」

> 最後更新：2026-03-14

---

## 1. 開發環境需求

| 工具 | 版本 | 用途 |
|:---|:---|:---|
| Java (JDK) | 21+ | 後端編譯與執行 |
| Maven | 3.9+ | 後端建構 |
| Node.js | 18+ | 前端建構 |
| npm | 9+ | 前端套件管理 |
| PostgreSQL | 15+ | 正式資料庫（Local 用 H2） |
| Redis | 7+ | 快取（Local 可略過） |
| Git | 2.40+ | 版本控制 |

---

## 2. 初次建置

### 2.1 後端建置

```bash
# 1. 確認 Java 版本（必須 21+）
java -version

# Windows 指定 JAVA_HOME（如果系統預設不是 21）
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.10.7-hotspot"

# 2. 安裝 common 模組（所有服務的基礎依賴）
cd backend
mvn install -pl hrms-common -DskipTests

# 3. 編譯全部模組
mvn compile

# 4. 執行全部測試
mvn test
```

### 2.2 前端建置

```bash
# 1. 安裝依賴
cd frontend
npm install

# 2. TypeScript 型別檢查
npx tsc --noEmit

# 3. 建構
npx vite build

# 4. 執行測試
npx vitest run

# 5. 啟動 Dev Server
npm run dev
# → http://localhost:5173
```

---

## 3. 服務啟動

### 3.1 啟動單一後端服務

```bash
cd backend
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.10.7-hotspot"

# 格式：mvn spring-boot:run -pl hrms-{service} -Plocal -Dspring-boot.run.profiles=local
mvn spring-boot:run -pl hrms-iam -Plocal -Dspring-boot.run.profiles=local
```

### 3.2 服務 Port 對照表

| 服務 | 模組 | Port | 啟動指令 |
|:---|:---|:---:|:---|
| IAM | hrms-iam | 8081 | `mvn spring-boot:run -pl hrms-iam -Plocal -Dspring-boot.run.profiles=local` |
| Organization | hrms-organization | 8082 | 同上替換模組名 |
| Attendance | hrms-attendance | 8083 | |
| Payroll | hrms-payroll | 8084 | |
| Insurance | hrms-insurance | 8085 | |
| Project | hrms-project | 8086 | |
| Timesheet | hrms-timesheet | 8087 | |
| Performance | hrms-performance | 8088 | |
| Recruitment | hrms-recruitment | 8089 | |
| Training | hrms-training | 8090 | |
| Workflow | hrms-workflow | 8091 | |
| Notification | hrms-notification | 8092 | |
| Document | hrms-document | 8093 | |
| Reporting | hrms-reporting | 8094 | |
| Eureka | api-gateway | 8761 | |

### 3.3 健康檢查

```bash
# 單一服務
curl -s -o /dev/null -w "%{http_code}" http://localhost:{port}/actuator/health

# 批次檢查所有服務
for port in 8081 8082 8083 8084 8085 8086 8087 8088 8089 8090 8091 8092 8093 8094; do
  result=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:$port/actuator/health 2>/dev/null || echo "000")
  echo "Port $port: $result"
done
```

### 3.4 關閉所有服務

```bash
# 批次關閉所有 HRMS 服務
for port in 8081 8082 8083 8084 8085 8086 8087 8088 8089 8090 8091 8092 8093 8094 8761; do
  pid=$(netstat -ano 2>/dev/null | grep ":$port " | grep "LISTENING" | awk '{print $5}' | head -1)
  if [ -n "$pid" ]; then
    taskkill //PID $pid //F 2>/dev/null && echo "Port $port (PID $pid): killed"
  fi
done
```

---

## 4. 常見問題與排除

### 4.1 Java 版本不對

**症狀**：`class file has wrong version 65.0, should be 61.0`

**原因**：Maven 使用 Java 17 但程式碼是 Java 21 編譯的

**修復**：
```bash
# 確認目前版本
java -version

# 指定正確的 JAVA_HOME
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.10.7-hotspot"

# 驗證
"$JAVA_HOME/bin/java" -version
```

### 4.2 Stale .class 檔案（刪除 source 但 target 殘留）

**症狀**：
- `ConflictingBeanDefinitionException`（兩個同名 Bean 衝突）
- `ClassCastException`（舊版 class 與新版不相容）
- 測試執行已刪除的 test class

**原因**：刪除 `.java` 後 `target/` 下的 `.class` 未清除

**修復**：
```bash
# 方式一：clean 整個模組
cd backend/hrms-{service}
mvn clean test

# 方式二：只刪 stale test-classes（避免 jar lock 問題）
rm -rf target/test-classes/com/company/hrms/{service}/path/to/stale/

# 方式三：找出 stale class
find target -name "*.class" | while read f; do
  src=$(echo "$f" | sed 's|target/classes|src/main/java|;s|target/test-classes|src/test/java|;s|\.class$|.java|;s|\$.*\.java$|.java|')
  [ ! -f "$src" ] && echo "STALE: $f"
done
```

### 4.3 Windows 檔案鎖（mvn clean 失敗）

**症狀**：`Failed to clean project: Failed to delete xxx.jar`

**原因**：某個 Java process 正在使用該 jar

**修復**：
```bash
# 查看哪些 Java process 在跑
tasklist | grep java

# 找到佔用 port 的 PID
netstat -ano | grep ":{port} " | grep LISTENING

# 關閉特定 PID（確認不是重要服務後）
taskkill //PID {pid} //F

# 或者不做 clean，只做 test（繞過 jar lock）
mvn test
```

### 4.4 hrms-common 未安裝

**症狀**：其他模組編譯失敗，找不到 common 的類別

**修復**：
```bash
cd backend
mvn install -pl hrms-common -DskipTests
```

### 4.5 前端 Vite Proxy 設定

**症狀**：前端 API 呼叫回傳 404 或 CORS 錯誤

**修復**：檢查 `frontend/vite.config.ts` 的 `server.proxy` 設定：
```typescript
server: {
  proxy: {
    '/api/v1': {
      target: 'http://localhost:8081',  // 或對應服務的 port
      changeOrigin: true,
    },
  },
}
```

### 4.6 MockConfig 導致假陽性

**症狀**：前端頁面正常但資料是 Mock 的

**修復**：
```typescript
// frontend/src/config/MockConfig.ts
// 所有已完成串接的模組都應設為 false
modules: {
  AUTH: false,
  ORGANIZATION: false,
  // ...
}
```

### 4.7 H2 測試資料庫問題

**症狀**：`Column not found`、`Table not found`

**修復**：
```bash
# 檢查 test schema
cat src/test/resources/test-data/{service}_base_data.sql

# 確認 schema 與 PO 欄位同步
# 新增欄位時需同步更新 test-data/ 下的 SQL
```

### 4.8 記憶體不足

**症狀**：`OutOfMemoryError` 或 Maven build 卡住

**修復**：
```bash
# 增加 Maven 記憶體
export MAVEN_OPTS="-Xmx2g -XX:MaxMetaspaceSize=512m"

# 或在 .mvn/jvm.config 中設定
echo "-Xmx2g" > .mvn/jvm.config
```

### 4.9 Port 被佔用

**症狀**：`Address already in use: bind`

**修復**：
```bash
# 找到佔用的 PID
netstat -ano | grep ":{port} " | grep LISTENING

# 關閉或換 port
```

### 4.10 Lombok 序列化問題

**症狀**：JSON 欄位名稱不符預期（如 `boolean isLate` 序列化為 `late` 而非 `isLate`）

**原因**：Lombok 的 `boolean isXxx` getter 為 `isXxx()`，Jackson 移除 `is` prefix 序列化為 `xxx`

**修復**：
- 測試中使用 `$.late` 而非 `$.isLate`
- 或在欄位上加 `@JsonProperty("isLate")`

---

## 5. 環境驗證 Checklist

### 後端
- [ ] `java -version` → 21+
- [ ] `mvn -version` → 3.9+
- [ ] `mvn install -pl hrms-common -DskipTests` → 成功
- [ ] `mvn compile` → 全部模組成功
- [ ] `mvn test` → 0 failures, 0 errors
- [ ] 服務可正常啟動（`mvn spring-boot:run`）
- [ ] Health check 回應 200

### 前端
- [ ] `node -v` → 18+
- [ ] `npm install` → 成功
- [ ] `npx tsc --noEmit` → 0 errors
- [ ] `npx vite build` → 成功
- [ ] `npx vitest run` → 測試通過
- [ ] `npm run dev` → http://localhost:5173 可存取

### 串接
- [ ] Vite Proxy 設定正確
- [ ] MockConfig 目標模組設為 `false`
- [ ] 前端可呼叫後端 API（非 Mock）
- [ ] 登入流程正常

---

## 6. 開發環境重置

當環境出現無法解釋的問題時，依序嘗試：

```bash
# Level 1: 清除編譯結果
cd backend && mvn clean

# Level 2: 重建 common
mvn install -pl hrms-common -DskipTests
mvn clean compile

# Level 3: 重新安裝前端依賴
cd frontend && rm -rf node_modules && npm install

# Level 4: 清除 Maven 本地快取（最後手段）
rm -rf ~/.m2/repository/com/company/hrms
cd backend && mvn install -pl hrms-common -DskipTests
```
