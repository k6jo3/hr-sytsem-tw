# Azure CLI 常用指令速查表

## 1. 登入與帳號

```bash
# 登入 Azure
az login

# 查看目前帳號
az account show --query "{name:name, id:id}" -o table

# 查看所有訂閱
az account list -o table
```

## 2. 資源總覽

```bash
# 列出 Resource Group 下所有資源
az resource list --resource-group hrms-demo-rg --query "[].{name:name, type:type}" -o table

# 列出所有 Container Apps
az containerapp list --resource-group hrms-demo-rg --query "[].{name:name, replicas:properties.template.scale.minReplicas}" -o table

# Azure DB for PostgreSQL 狀態
az postgres flexible-server show --name hrms-pg-demo --resource-group hrms-demo-rg --query "{name:name, state:state, sku:sku.name}" -o table
```

## 3. Container Apps 狀態

```bash
# 所有 Container Apps 的 image 和 replica 設定
az containerapp list --resource-group hrms-demo-rg \
  --query "[].{name:name, minReplicas:properties.template.scale.minReplicas, image:properties.template.containers[0].image}" -o table

# 單一服務的實際 replica 數量（0 = 沒在跑，不計費）
az containerapp replica list --name hrms-iam --resource-group hrms-demo-rg \
  --query "[].{name:name, status:properties.runningState}" -o table

# 單一服務的 ingress 設定
az containerapp ingress show --name hrms-gateway --resource-group hrms-demo-rg -o json

# 單一服務的環境變數
az containerapp show --name hrms-iam --resource-group hrms-demo-rg \
  --query "properties.template.containers[0].env[].{name:name, value:value}" -o table
```

## 4. 日誌

```bash
# 查看某服務最近 20 行 log
az containerapp logs show --name hrms-iam --resource-group hrms-demo-rg --tail 20

# 過濾 error log
az containerapp logs show --name hrms-iam --resource-group hrms-demo-rg --tail 50 2>&1 | grep -i "error\|exception\|FATAL"

# 進入容器執行指令
az containerapp exec --name hrms-frontend --resource-group hrms-demo-rg --command "cat /etc/nginx/conf.d/default.conf"
```

## 5. 啟動與停止

```bash
# === 一鍵啟停（用腳本）===
bash scripts/azure-start.sh    # 啟動全部
bash scripts/azure-stop.sh     # 停止全部

# === 手動啟停單一服務 ===
# 啟動（設 minReplicas=1）
az containerapp update --name hrms-iam --resource-group hrms-demo-rg --min-replicas 1 --output none

# 停止（設 minReplicas=0）
az containerapp update --name hrms-iam --resource-group hrms-demo-rg --min-replicas 0 --output none

# 強制重啟（建新 revision）
az containerapp update --name hrms-iam --resource-group hrms-demo-rg --revision-suffix "restart1" --min-replicas 1 --output none

# === Azure DB ===
az postgres flexible-server start --name hrms-pg-demo --resource-group hrms-demo-rg
az postgres flexible-server stop --name hrms-pg-demo --resource-group hrms-demo-rg
```

## 6. 費用查詢

```bash
# 本月累計費用
az consumption usage list --start-date 2026-04-01 --end-date 2026-04-30 \
  --query "[?contains(instanceName, 'hrms')].{name:instanceName, cost:pretaxCost, currency:currency}" -o table

# Resource Group 的費用摘要（需到 Portal 看更詳細）
# Portal: https://portal.azure.com → Cost Management → Cost analysis
# 篩選 Resource Group = hrms-demo-rg

# 或用 CLI 查預估費用
az costmanagement query --type ActualCost --timeframe MonthToDate \
  --scope "/subscriptions/9e00ad61-ec8a-4e7a-9b9e-1d61fabfae34/resourceGroups/hrms-demo-rg" \
  --dataset-grouping name=ResourceType type=Dimension 2>/dev/null || echo "需到 Portal 查看"
```

## 7. 資料庫操作

```bash
# 連線到 Azure DB（需本地安裝 psql）
PGPASSWORD="Hrms_Demo_2026!" psql -h hrms-pg-demo.postgres.database.azure.com -U hrms -d hrms_iam

# 列出所有 database
PGPASSWORD="Hrms_Demo_2026!" psql -h hrms-pg-demo.postgres.database.azure.com -U hrms -l

# 列出某 database 的 table
PGPASSWORD="Hrms_Demo_2026!" psql -h hrms-pg-demo.postgres.database.azure.com -U hrms -d hrms_iam -c "\dt"

# 執行 SQL 檔案
PGPASSWORD="Hrms_Demo_2026!" psql -h hrms-pg-demo.postgres.database.azure.com -U hrms -d hrms_iam -f seed.sql
```

## 8. 部署與更新

```bash
# 更新某服務的 image
az containerapp update --name hrms-iam --resource-group hrms-demo-rg \
  --image ghcr.io/k6jo3/hr-sytsem-tw/hrms-iam:latest

# 設定環境變數
az containerapp update --name hrms-gateway --resource-group hrms-demo-rg \
  --set-env-vars "GATEWAY_CORS_ALLOWED_ORIGINS=http://localhost:3000,https://hrms-frontend.orangesmoke-bcd62134.eastasia.azurecontainerapps.io"

# 設定 ingress（允許 HTTP）
az containerapp ingress update --name hrms-gateway --resource-group hrms-demo-rg --allow-insecure

# 手動觸發 Azure CD
gh run list --repo k6jo3/hr-sytsem-tw --workflow="Azure CD" --limit 1
# 到 GitHub Actions 頁面手動 Run workflow
```

## 9. 清理（省錢）

```bash
# 停止所有服務（$0 計費，只剩 DB 儲存 ~$3.7/月）
bash scripts/azure-stop.sh

# 核彈級：刪除整個 Resource Group（所有資源一次清除，$0）
# ⚠️ 不可逆！
az group delete --name hrms-demo-rg --yes --no-wait

# 只刪 Azure DB（省 $3.7/月儲存費，但資料全丟）
az postgres flexible-server delete --name hrms-pg-demo --resource-group hrms-demo-rg --yes
```

## 10. 常用 URL

| 項目 | URL |
|:---|:---|
| 前端 | https://hrms-frontend.orangesmoke-bcd62134.eastasia.azurecontainerapps.io |
| GitHub Pages | https://k6jo3.github.io/hr-sytsem-tw |
| Azure Portal | https://portal.azure.com |
| Cost Analysis | Portal → hrms-demo-rg → Cost analysis |
| GitHub Actions | https://github.com/k6jo3/hr-sytsem-tw/actions |
| ghcr.io images | https://github.com/k6jo3?tab=packages |
