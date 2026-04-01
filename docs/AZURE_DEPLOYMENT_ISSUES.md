# Azure Container Apps 部署問題紀錄

**專案：** HRMS 企業級人資管理系統
**部署環境：** Azure Container Apps
**紀錄日期：** 2026-04-01

---

## 目錄

1. [AZ-001 CI/CD toolchains.xml 缺失](#az-001-cicd-toolchainsxml-缺失)
2. [AZ-002 H2 保留字 year 衝突](#az-002-h2-保留字-year-衝突)
3. [AZ-003 Notification Mail Health Check 失敗](#az-003-notification-mail-health-check-失敗)
4. [AZ-004 Container Apps PostgreSQL 資料不持久](#az-004-container-apps-postgresql-資料不持久)
5. [AZ-005 Nginx DNS 解析快取導致路由失敗](#az-005-nginx-dns-解析快取導致路由失敗)
6. [AZ-006 HTTP→HTTPS Redirect 將 POST 變成 GET](#az-006-httphttps-redirect-將-post-變成-get)
7. [AZ-007 allowInsecure 設定被 ingress update 覆蓋](#az-007-allowinsecure-設定被-ingress-update-覆蓋)
8. [AZ-008 Nginx Host Header 導致 CORS 403](#az-008-nginx-host-header-導致-cors-403)
9. [AZ-009 Docker Profile 下 Security Config 未載入導致 401](#az-009-docker-profile-下-security-config-未載入導致-401)
10. [AZ-010 手動建 Table 缺欄位](#az-010-手動建-table-缺欄位)
11. [AZ-011 Dashboard API 路徑錯誤](#az-011-dashboard-api-路徑錯誤)
12. [AZ-012 Service Bean Name 不匹配](#az-012-service-bean-name-不匹配)

---

## AZ-001 CI/CD toolchains.xml 缺失

**現象：**
CI/CD pipeline 執行 Maven build 時失敗，錯誤訊息顯示找不到 toolchains.xml，Maven Toolchains Plugin 無法定位 JDK 21。

**發現方式：**
GitHub Actions workflow 執行失敗，build log 顯示 toolchains 相關錯誤。

**根因分析：**
專案使用 Maven Toolchains Plugin 指定 JDK 21 進行編譯，本地開發環境有 `~/.m2/toolchains.xml`，但 CI runner 環境未預先配置此檔案。

**解決方案：**
在 CI workflow 中新增 "Generate toolchains.xml" step，在 Maven build 之前動態產生 toolchains.xml，指向 CI runner 上的 JDK 21 路徑。

```yaml
- name: Generate toolchains.xml
  run: |
    mkdir -p ~/.m2
    cat > ~/.m2/toolchains.xml << 'EOF'
    <?xml version="1.0" encoding="UTF-8"?>
    <toolchains>
      <toolchain>
        <type>jdk</type>
        <provides><version>21</version></provides>
        <configuration><jdkHome>${{ env.JAVA_HOME }}</jdkHome></configuration>
      </toolchain>
    </toolchains>
    EOF
```

**涉及檔案：**
- `.github/workflows/*.yml`（CI workflow）
- `backend/pom.xml`（Toolchains Plugin 設定）

**經驗教訓：**
CI 環境與本地開發環境的差異容易被忽略。所有 build 依賴的設定檔都應在 CI workflow 中明確產生，不能假設環境已配置。

---

## AZ-002 H2 保留字 year 衝突

**現象：**
測試執行時，涉及 `LeaveBalancePO` 的 SQL 操作報語法錯誤，H2 資料庫拒絕接受 `year` 作為欄位名。

**發現方式：**
後端單元測試 / 整合測試執行失敗，錯誤訊息指向 SQL 語法錯誤。

**根因分析：**
`year` 是 H2 資料庫的保留字（SQL 標準保留字），直接使用會被解析為函式而非欄位名稱。PostgreSQL 雖然也有 `year` 保留字，但在大多數情境下較寬容，因此本地 PG 測試可能未發現此問題。

**解決方案：**
在 JPA Entity 的 `@Column` 註解中為欄位名加上雙引號跳脫：

```java
@Column(name = "\"year\"")
private Integer year;
```

**涉及檔案：**
- `backend/attendance-service/src/main/java/**/LeaveBalancePO.java`

**經驗教訓：**
H2 與 PostgreSQL 的 SQL 相容性存在差異，特別是保留字處理。命名欄位時應避免使用 SQL 保留字；若無法避免，必須在 Entity 層明確跳脫。建議在程式碼審查時加入保留字檢查。

---

## AZ-003 Notification Mail Health Check 失敗

**現象：**
Notification 微服務啟動後，Actuator health endpoint 回報 `DOWN`，導致 Container Apps 認為容器不健康並持續重啟。

**發現方式：**
Container Apps 部署後，容器反覆重啟，查看日誌發現 health check 失敗。

**根因分析：**
Spring Boot Actuator 自動配置的 `MailHealthContributor` 會嘗試連接 SMTP 伺服器進行健康檢查。在測試環境 / 部署初期未配置 `JavaMailSender` bean，導致 health check 拋出例外，整體 health 狀態變為 DOWN。

**解決方案：**
在測試及部署環境的 application profile 中停用 Mail health check：

```yaml
management:
  health:
    mail:
      enabled: false
```

**涉及檔案：**
- `backend/notification-service/src/main/resources/application-docker.yml`
- `backend/notification-service/src/main/resources/application.yml`

**經驗教訓：**
Spring Boot Actuator 的自動健康檢查可能因為外部依賴未就緒而導致容器被判定不健康。部署前應審查所有 health contributor，對非關鍵的外部依賴停用 health check，或設為 liveness / readiness 分離策略。

---

## AZ-004 Container Apps PostgreSQL 資料不持久

**現象：**
在 Container Apps 中以 sidecar 或獨立容器方式執行 PostgreSQL，重啟後所有資料消失。

**發現方式：**
部署後功能正常，但容器重啟（scaling event 或 revision 更新）後，資料庫回到初始狀態。

**根因分析：**
Container Apps 的 storage 預設為 ephemeral（臨時），容器重啟時所有資料清空。嘗試掛載 Azure File Share 作為持久儲存，但 PostgreSQL 啟動時需要對資料目錄執行 `chmod 700`，而 Azure File Share 使用 SMB 協定，不支援 POSIX 權限控制，導致 PG 啟動失敗。

**解決方案：**
放棄在 Container Apps 中自行管理 PostgreSQL，改用 Azure Database for PostgreSQL Flexible Server 作為受管理的資料庫服務。

**涉及檔案：**
- Azure 基礎設施設定（Container Apps 環境變數、連線字串）
- `backend/*/src/main/resources/application-docker.yml`（資料庫連線設定）

**經驗教訓：**
Container Apps 不適合執行需要持久儲存且對檔案系統權限有嚴格要求的有狀態服務（如 PostgreSQL）。有狀態服務應使用雲端受管理服務（Azure DB for PostgreSQL、Azure Cache for Redis 等）。在架構規劃階段就應將此納入考量。

---

## AZ-005 Nginx DNS 解析快取導致路由失敗

**現象：**
Nginx 作為前端 reverse proxy 啟動後，初始可正常存取 Gateway，但一段時間後 API 請求全部失敗，回傳 502 Bad Gateway。

**發現方式：**
部署後功能時好時壞，Container Apps 的 revision 更新或 scaling event 後開始出現 502。

**根因分析：**
Nginx 的 `proxy_pass` 若直接寫入主機名稱（非變數），會在啟動時解析 DNS 一次並永久快取該 IP。Container Apps 的內部 IP 會隨 revision 部署或 scaling 事件變動，導致 Nginx 持續向舊 IP 發送請求。

嘗試過的解法：
1. **resolver + 短名稱** — Container Apps 內部 DNS 不支援短名稱解析，失敗。
2. **resolver + FQDN** — 設定 `resolver 168.63.129.16`（Azure DNS）加上完整 FQDN，但因 Nginx 啟動時機早於 Gateway，解析仍可能失敗。
3. **最終方案** — 使用 `wget` 輪詢等待 Gateway 的 health endpoint 回應正常後，再啟動 Nginx，確保 DNS 解析時目標已就緒。

**解決方案：**
在 Nginx 容器的啟動腳本中加入等待邏輯：

```bash
# 等待 Gateway 就緒
until wget -q --spider http://gateway.internal.xxx.azurecontainerapps.io:8080/actuator/health; do
  echo "Waiting for Gateway..."
  sleep 5
done
# Gateway 就緒後啟動 nginx
nginx -g 'daemon off;'
```

同時在 nginx.conf 中使用變數方式設定 upstream，搭配 resolver：

```nginx
resolver 168.63.129.16 valid=30s;
set $gateway_host "gateway.internal.xxx.azurecontainerapps.io";
proxy_pass http://$gateway_host:8080;
```

**涉及檔案：**
- `nginx/nginx.conf`
- `nginx/start.sh`（啟動腳本）
- `nginx/Dockerfile`

**經驗教訓：**
在動態 IP 環境中，Nginx 的 DNS 快取行為是常見陷阱。必須使用變數搭配 resolver 來強制定期重新解析 DNS。此外，容器間的啟動順序無法保證，需要實作 readiness 等待機制。

---

## AZ-006 HTTP→HTTPS Redirect 將 POST 變成 GET

**現象：**
前端發送 POST 請求到後端 API（如登入），收到 302 Redirect 後瀏覽器自動以 GET 方法重新發送請求，導致後端回傳 405 Method Not Allowed 或其他非預期結果。

**發現方式：**
前端登入功能失敗，瀏覽器 Network tab 顯示 POST 請求被 302 重導後變成 GET。

**根因分析：**
Container Apps 的 internal ingress 預設 `allowInsecure=false`，會對 HTTP 請求回傳 302 重導到 HTTPS。根據 HTTP 規範，302 重導會將 POST 方法改為 GET（歷史行為），導致請求方法變更。

**解決方案：**
將所有微服務的 ingress 設定為 `allowInsecure=true`，允許 HTTP 流量，避免不必要的 302 重導：

```bash
az containerapp ingress update \
  --name <app-name> \
  --resource-group <rg> \
  --allow-insecure
```

**涉及檔案：**
- Azure Container Apps ingress 設定（所有微服務）
- 部署腳本

**經驗教訓：**
Container Apps 的 internal 通訊（服務對服務）不需要 HTTPS 重導，因為外部流量已由 ingress 層處理 TLS。internal ingress 的 `allowInsecure=false` 預設值在微服務架構中會造成非預期的重導行為，必須明確設為允許。

---

## AZ-007 allowInsecure 設定被 ingress update 覆蓋

**現象：**
已設定 `allowInsecure=true` 的微服務，在執行其他 ingress 更新操作後，`allowInsecure` 又被重置為 `false`，POST 請求再次出現 302 重導問題。

**發現方式：**
修改 ingress 設定（如調整 target port 或 ingress type）後，先前已修復的 POST→GET 問題復發。

**根因分析：**
`az containerapp ingress update` 的 `--type internal` 參數會重置其他 ingress 設定，包括 `allowInsecure`，回到預設值 `false`。Azure CLI 的 ingress update 並非增量更新，而是整體替換。

**解決方案：**
每次執行 ingress 相關的 update 操作後，必須重新設定 `allow-insecure`：

```bash
# 更新 ingress type 後，立即重設 allow-insecure
az containerapp ingress update --name <app> --resource-group <rg> --type internal
az containerapp ingress update --name <app> --resource-group <rg> --allow-insecure
```

建議將所有 ingress 設定整合到 IaC（如 Bicep / ARM template），避免手動操作遺漏。

**涉及檔案：**
- 部署腳本
- Azure CLI 操作步驟文件

**經驗教訓：**
Azure CLI 的部分命令不是增量更新而是全量替換，會靜默覆蓋先前的設定。手動操作容易遺漏步驟，應使用 Infrastructure as Code（Bicep、Terraform）宣告完整狀態，避免設定漂移。

---

## AZ-008 Nginx Host Header 導致 CORS 403

**現象：**
前端透過 Nginx proxy 呼叫後端 API 時，收到 403 Forbidden，錯誤訊息與 CORS 相關。

**發現方式：**
瀏覽器 console 顯示 CORS policy 錯誤，後端 Gateway 日誌顯示 Origin 不在允許清單內。

**根因分析：**
Nginx 設定了 `proxy_set_header Host $host`，將前端域名作為 Host header 傳給 Gateway。同時 Nginx 預設會保留原始請求的 `Origin` header 傳給後端。Gateway 的 CORS filter 收到的 Origin header（前端域名）與其允許的來源清單不匹配，因此拒絕請求回傳 403。

**解決方案：**
在 Nginx 的 proxy 設定中清除 Origin header，讓 Gateway 不進行 CORS 檢查（因為 Nginx → Gateway 是 server-to-server 通訊，不需要 CORS）：

```nginx
proxy_set_header Origin "";
proxy_set_header Host <gateway-hostname>;
```

**涉及檔案：**
- `nginx/nginx.conf`

**經驗教訓：**
Reverse proxy 架構中，HTTP header 的傳遞需要特別注意。CORS 是瀏覽器行為，server-to-server 的通訊不應受 CORS 限制。Nginx 作為中間層時，應明確控制傳給後端的 Host 和 Origin header，而非透傳瀏覽器送來的值。

---

## AZ-009 Docker Profile 下 Security Config 未載入導致 401

**現象：**
所有微服務在 Docker / Container Apps 環境下啟動後，任何 API 請求都回傳 401 Unauthorized。

**發現方式：**
部署後前端所有功能無法使用，後端日誌顯示 Spring Security 拒絕所有請求。

**根因分析：**
`LocalSecurityAutoConfig`（用於開發環境的寬鬆安全設定）僅標註 `@Profile("local")`。在 Docker 環境中使用的是 `docker` profile，此設定類不會被載入，導致 Spring Security 使用預設的嚴格設定，拒絕所有未認證請求。

**解決方案：**
將 Profile 條件擴展為支援 `local` 和 `docker` 兩個 profile：

```java
@Profile({"local", "docker"})
@Configuration
public class LocalSecurityAutoConfig {
    // ...
}
```

**涉及檔案：**
- `backend/common/src/main/java/**/LocalSecurityAutoConfig.java`

**經驗教訓：**
Profile 的命名與適用範圍需要統一規劃。`local` 和 `docker` 在安全需求上可能相同（均為非正式環境），但 Profile 名稱不同就不會自動繼承。建議用一個統一的 profile group 或 parent profile 來管理「非正式環境」的共用設定。

---

## AZ-010 手動建 Table 缺欄位

**現象：**
IAM 服務執行時，涉及 `permissions` table 的操作報 SQL 錯誤，提示 `created_at` 欄位不存在。

**發現方式：**
後端日誌顯示 SQL 執行失敗，錯誤訊息為 column "created_at" does not exist。

**根因分析：**
在 Azure DB for PostgreSQL 上手動建立 table 時，遺漏了 `created_at` 欄位。由於未使用 Flyway / Liquibase 等 schema migration 工具，手動操作容易遺漏欄位。

**解決方案：**
手動補上缺失的欄位：

```sql
ALTER TABLE permissions ADD COLUMN created_at TIMESTAMP DEFAULT NOW();
```

**涉及檔案：**
- Azure DB for PostgreSQL 的 `permissions` table DDL
- 建議新增：`backend/*/src/main/resources/db/migration/` schema migration 檔案

**經驗教訓：**
手動管理資料庫 schema 極易出錯，尤其在多微服務架構下每個服務可能有自己的 table。應使用 Flyway 或 Liquibase 進行 schema migration，確保 schema 版本化與可追蹤。所有 DDL 變更都應以 migration script 形式納入版本控制。

---

## AZ-011 Dashboard API 路徑錯誤

**現象：**
Dashboard 頁面多個區塊顯示錯誤或資料載入失敗，包括請假餘額、工作流程、使用者資訊等。

**發現方式：**
前端 console 顯示多個 API 呼叫回傳 404 Not Found 或參數錯誤。

**根因分析：**
前端 Dashboard 呼叫的 API 路徑與後端實際端點不匹配：
1. `leave/balances` 未帶 `employeeId` 路徑參數，後端需要 `leave/balances/{employeeId}`
2. `workflows/instances` 端點在後端不存在，實際端點為其他路徑
3. `auth/me` 端點在後端不存在，取得當前使用者資訊的端點路徑不同

**解決方案：**
修正前端 API 呼叫路徑，使其與後端 API 規格一致：

```typescript
// 修正前
api.get('/leave/balances');
// 修正後
api.get(`/leave/balances/${employeeId}`);
```

**涉及檔案：**
- `frontend/src/features/*/api/*.ts`（Dashboard 相關的 API 呼叫）
- `frontend/src/pages/HR*DashboardPage.tsx`

**經驗教訓：**
前後端 API 路徑的一致性應由合約測試保障。前端開發時若使用 Mock 資料，容易在切換到真實 API 時發現路徑不匹配。建議在合約文件中明確記錄完整 API 路徑，並在前端使用型別安全的 API client 自動生成。

---

## AZ-012 Service Bean Name 不匹配

**現象：**
呼叫請假餘額查詢 API 時，後端回傳 500 Internal Server Error，日誌顯示找不到對應的 Service bean。

**發現方式：**
後端日誌顯示 `NoSuchBeanDefinitionException`，找不到特定名稱的 bean。

**根因分析：**
專案使用 Service Factory 模式，Controller 方法名稱自動對應 Service bean name。Controller 方法名為 `getLeaveBalance`（單數），自動解析的 bean name 為 `getLeaveBalanceServiceImpl`，但實際 Service 類別的 bean name 註冊為 `getLeaveBalancesServiceImpl`（複數，多了一個 `s`）。

**解決方案：**
將 Service 類別的 `@Service` 註解中的 bean name 改為與 Controller 方法名一致：

```java
// 修正前
@Service("getLeaveBalancesServiceImpl")

// 修正後
@Service("getLeaveBalanceServiceImpl")
```

**涉及檔案：**
- `backend/attendance-service/src/main/java/**/GetLeaveBalanceServiceImpl.java`

**經驗教訓：**
Service Factory 模式的 "convention over configuration" 設計雖然減少了程式碼量，但方法名與 bean name 的隱式對應關係容易因拼寫差異而出錯。建議加入啟動時的 bean name 驗證機制，或在測試中加入 Controller 方法與 Service bean 的對應檢查。

---

## 經驗教訓總結

### 一、環境差異意識

| 面向 | 本地開發 | Docker Compose | Azure Container Apps |
|:---|:---|:---|:---|
| 資料庫 | H2（測試）/ PG（開發） | PG Container | Azure DB for PG |
| DNS | localhost | Docker network（服務名稱解析） | Internal FQDN（動態 IP） |
| 安全 | local profile（寬鬆） | docker profile（需明確設定） | 需處理 ingress / TLS |
| 儲存 | 本地磁碟 | Docker Volume | Ephemeral（需外部服務） |
| CORS | 同 origin 或 proxy | 容器間直連 | Nginx → Gateway（header 傳遞） |

**核心教訓：** 每一層環境都有不同的行為假設，不能假設本地通過的設定在雲端也能正常運作。應為每個環境建立專屬的驗證清單。

### 二、先本地驗證再推 CI/CD

- CI 環境缺少的設定檔（toolchains.xml）應在本地 CI 模擬後確認
- Docker Compose 環境應作為部署前的中間驗證層
- 建議流程：本地測試 → Docker Compose 驗證 → CI/CD → Azure 部署

### 三、日誌與可觀測性

- Azure Container Apps 的日誌有顯著延遲（數分鐘到十幾分鐘），排查問題時需要耐心等待
- 建議在容器啟動腳本中加入即時輸出的 health check log
- 善用 `az containerapp logs show --follow` 追蹤即時日誌
- 關鍵服務應加入 structured logging，方便在 Log Analytics 中查詢

### 四、Infrastructure as Code

- 手動操作 Azure CLI 容易遺漏步驟或被後續操作覆蓋（如 AZ-007 的 allowInsecure 問題）
- `az containerapp ingress update` 等命令是全量替換，不是增量更新
- 應使用 Bicep / Terraform 管理 Azure 資源，確保設定的可追蹤性與一致性
- 資料庫 schema 應使用 Flyway / Liquibase 版本化管理

### 五、CORS / Host / Origin 在 Proxy 架構的處理

- 瀏覽器 → Nginx → Gateway → 微服務 的多層架構中，HTTP header 會逐層傳遞
- `Host` header 決定後端路由，應設為目標服務的 hostname 而非前端域名
- `Origin` header 用於 CORS 檢查，server-to-server 通訊應清除此 header
- CORS 政策應只在最外層（面向瀏覽器的層）處理，內部服務間不需要 CORS

### 六、命名一致性與自動化驗證

- Service Factory 模式的方法名 ↔ bean name 對應需要自動化檢查
- 前後端 API 路徑應以合約文件為唯一真相來源
- SQL 保留字、Profile 名稱等隱式約定應有 linter 或啟動時驗證
