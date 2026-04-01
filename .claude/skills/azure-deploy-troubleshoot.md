---
description: Azure Container Apps 部署問題排查指南。當遇到 Azure 部署、nginx proxy、CORS、401/403/404/500 錯誤時使用。
---

# Azure Container Apps 部署問題排查指南

本指南適用於 HRMS 專案在 Azure Container Apps 上的部署排查。架構為：瀏覽器 → Nginx（前端 + reverse proxy） → Spring Cloud Gateway → 各微服務 → Azure DB for PostgreSQL。

---

## 常見問題快速檢查清單

在開始深入排查前，先逐項檢查以下常見問題：

### 啟動與健康檢查

- [ ] 容器是否持續重啟？檢查 health check 是否通過
- [ ] Actuator 的 health contributor 是否有非必要的外部依賴（如 MailHealthContributor）
- [ ] Spring Profile 是否正確設定為 `docker`？
- [ ] `LocalSecurityAutoConfig` 是否支援 `docker` profile？（必須是 `@Profile({"local", "docker"})`）

### Nginx 與 Proxy

- [ ] Nginx 是否在 Gateway 就緒後才啟動？（使用 wget/curl 等待 health endpoint）
- [ ] `proxy_pass` 是否使用變數搭配 `resolver`？（避免 DNS 快取）
- [ ] `proxy_set_header Host` 是否設為 Gateway 的 hostname（非前端域名）？
- [ ] `proxy_set_header Origin ""` 是否已設定？（清除 Origin 避免 CORS 誤判）

### Ingress 與網路

- [ ] 所有微服務的 `allowInsecure` 是否為 `true`？（避免 HTTP→HTTPS 302 將 POST 變 GET）
- [ ] 最近是否執行過 `az containerapp ingress update`？（可能覆蓋 allowInsecure 設定）
- [ ] Container Apps 的 internal ingress FQDN 是否正確？

### 資料庫

- [ ] 是否使用 Azure DB for PostgreSQL（而非容器內 PG）？
- [ ] 連線字串是否正確？環境變數是否已注入？
- [ ] Table schema 是否完整？是否有缺漏的欄位？

### 前後端整合

- [ ] 前端 API 路徑是否與後端端點一致？（注意路徑參數）
- [ ] Service bean name 是否與 Controller method name 匹配？（單複數、拼寫）
- [ ] Mock 開關是否已關閉？（檢查 `MockConfig.ts`）

---

## 逐層排查流程

遇到部署問題時，按以下順序由外到內排查：

### 第一層：Nginx（前端 + Reverse Proxy）

**目標：** 確認 Nginx 正常啟動、靜態資源可存取、proxy 設定正確。

1. 檢查 Nginx 容器狀態與日誌：
   ```bash
   az containerapp logs show --name nginx-app --resource-group <rg> --follow
   ```

2. 確認 Nginx 是否成功啟動（非持續等待 Gateway）：
   ```bash
   az containerapp exec --name nginx-app --resource-group <rg> --command "ps aux"
   ```

3. 檢查 nginx.conf 中的 upstream 設定：
   - `resolver` 是否指向 `168.63.129.16`（Azure 內部 DNS）
   - `proxy_pass` 是否使用變數（`set $gateway ...`）
   - `Host` 和 `Origin` header 是否正確設定

4. 在 Nginx 容器內測試對 Gateway 的連通性：
   ```bash
   az containerapp exec --name nginx-app --resource-group <rg> \
     --command "wget -q -O- http://<gateway-fqdn>:8080/actuator/health"
   ```

**常見問題：**
- 502 Bad Gateway → DNS 快取（IP 已變更）或 Gateway 尚未就緒
- CORS 403 → Origin header 未清除或 Host header 設定錯誤
- 靜態資源 404 → Nginx 的 root 路徑或 try_files 設定有誤

### 第二層：Spring Cloud Gateway

**目標：** 確認 Gateway 正常運作、路由規則正確。

1. 直接測試 Gateway 的 health endpoint：
   ```bash
   az containerapp exec --name gateway-app --resource-group <rg> \
     --command "curl http://localhost:8080/actuator/health"
   ```

2. 檢查 Gateway 路由是否正確載入：
   ```bash
   az containerapp exec --name gateway-app --resource-group <rg> \
     --command "curl http://localhost:8080/actuator/gateway/routes"
   ```

3. 查看 Gateway 日誌，關注 route 匹配與轉發：
   ```bash
   az containerapp logs show --name gateway-app --resource-group <rg> --follow
   ```

**常見問題：**
- 404 → 路由規則未匹配到目標微服務
- 503 → 目標微服務未註冊到 Eureka 或未就緒
- 401/403 → Security filter chain 攔截（檢查 Profile）

### 第三層：微服務

**目標：** 確認個別微服務正常運行、API 端點可用。

1. 檢查微服務 health：
   ```bash
   az containerapp exec --name <service-app> --resource-group <rg> \
     --command "curl http://localhost:<port>/actuator/health"
   ```

2. 直接在容器內測試 API：
   ```bash
   az containerapp exec --name <service-app> --resource-group <rg> \
     --command "curl -X POST http://localhost:<port>/api/v1/xxx -H 'Content-Type: application/json' -d '{}'"
   ```

3. 檢查日誌中的錯誤：
   ```bash
   az containerapp logs show --name <service-app> --resource-group <rg> \
     --type system  # 系統日誌
   az containerapp logs show --name <service-app> --resource-group <rg> \
     --type console  # 應用日誌
   ```

**常見問題：**
- 401 → `LocalSecurityAutoConfig` 未載入（Profile 問題）
- 500 + NoSuchBeanDefinitionException → Service bean name 不匹配
- 500 + SQL Error → H2 保留字或 schema 問題

### 第四層：資料庫

**目標：** 確認資料庫連線與 schema 完整性。

1. 從微服務容器測試 DB 連線：
   ```bash
   az containerapp exec --name <service-app> --resource-group <rg> \
     --command "curl -v telnet://<pg-host>:5432"
   ```

2. 使用 psql 連線檢查 schema（需要安裝 psql 的環境）：
   ```bash
   psql "host=<pg-host> dbname=<db> user=<user> password=<pass> sslmode=require" \
     -c "\dt"  # 列出所有 table
   ```

3. 驗證特定 table 結構：
   ```sql
   SELECT column_name, data_type FROM information_schema.columns
   WHERE table_name = '<table_name>' ORDER BY ordinal_position;
   ```

**常見問題：**
- Connection refused → 防火牆規則未開放、連線字串錯誤
- Column not found → 手動建 table 時遺漏欄位
- Permission denied → DB user 權限不足

---

## 常用 az CLI 診斷指令

### 容器狀態與日誌

```bash
# 查看容器應用清單
az containerapp list --resource-group <rg> -o table

# 查看特定應用的詳細資訊
az containerapp show --name <app-name> --resource-group <rg>

# 查看即時日誌（--follow 持續追蹤）
az containerapp logs show --name <app-name> --resource-group <rg> --follow

# 查看系統日誌（container runtime 層級）
az containerapp logs show --name <app-name> --resource-group <rg> --type system

# 查看 revision 清單
az containerapp revision list --name <app-name> --resource-group <rg> -o table

# 進入容器執行指令
az containerapp exec --name <app-name> --resource-group <rg> --command "/bin/sh"
```

### Ingress 設定

```bash
# 查看 ingress 設定
az containerapp ingress show --name <app-name> --resource-group <rg>

# 設定 allowInsecure（重要：每次 ingress update 後都要重設）
az containerapp ingress update --name <app-name> --resource-group <rg> --allow-insecure

# 查看 FQDN
az containerapp show --name <app-name> --resource-group <rg> \
  --query "properties.configuration.ingress.fqdn" -o tsv
```

### 環境變數與密鑰

```bash
# 查看環境變數
az containerapp show --name <app-name> --resource-group <rg> \
  --query "properties.template.containers[0].env" -o table

# 更新環境變數
az containerapp update --name <app-name> --resource-group <rg> \
  --set-env-vars "SPRING_PROFILES_ACTIVE=docker" "DB_HOST=<pg-host>"
```

### Log Analytics 查詢

```bash
# 查詢容器日誌（KQL）
az monitor log-analytics query \
  --workspace <workspace-id> \
  --analytics-query "ContainerAppConsoleLogs_CL | where ContainerAppName_s == '<app-name>' | where TimeGenerated > ago(1h) | order by TimeGenerated desc | take 50"
```

---

## Container Apps 重要注意事項

### DNS

- Container Apps 內部服務使用 FQDN：`<app-name>.internal.<env-unique-id>.<region>.azurecontainerapps.io`
- 短名稱（如 `gateway-app`）在 Container Apps 環境中**不支援**自動解析
- Azure 內部 DNS resolver 地址：`168.63.129.16`
- DNS 解析結果可能隨 scaling / revision 變動，不可快取

### Ingress

- Internal ingress 預設 `allowInsecure=false`，會 302 重導 HTTP→HTTPS
- 302 重導會將 POST 方法改為 GET（HTTP 規範歷史行為）
- `az containerapp ingress update --type` 會重置 `allowInsecure` 為預設值
- 建議：internal 服務間通訊一律設 `allowInsecure=true`

### CORS

- CORS 是瀏覽器行為，server-to-server 不需要 CORS
- Nginx → Gateway 是 server-to-server，應在 Nginx 清除 `Origin` header
- Gateway 的 CORS 設定只需對外層（瀏覽器直連時）生效
- 多層 proxy 架構中，`Host` header 的值會影響後端的路由判斷

### 儲存

- Container Apps 預設使用 ephemeral storage，容器重啟後資料清空
- Azure File Share（SMB）不支援 POSIX 權限控制（chmod），不適合 PostgreSQL
- 有狀態服務（DB、Cache）應使用 Azure 受管理服務

### 容器啟動順序

- Container Apps 不保證容器間的啟動順序
- 依賴其他服務的容器應實作 readiness 等待機制（如 wget 輪詢 health endpoint）
- health check 的 initialDelaySeconds 要設足夠長，避免服務還在啟動就被判定不健康

---

## 參考文件

- 詳細問題紀錄：`docs/AZURE_DEPLOYMENT_ISSUES.md`
- Nginx 設定：`nginx/nginx.conf`
- Gateway 路由設定：`backend/gateway/src/main/resources/application.yml`
- 各微服務 Docker profile：`backend/*/src/main/resources/application-docker.yml`
- 前端 Mock 開關：`frontend/src/config/MockConfig.ts`
- 前端 Proxy 設定：`frontend/vite.config.ts`
