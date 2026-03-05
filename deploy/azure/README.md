# Azure 部署指南

## 架構

```
使用者 (HTTPS)
    ↓
Azure Container Apps (external ingress)
    ├── hrms-frontend (Nginx + SPA)
    │   └── /api/* → 後端微服務 (internal ingress)
    ├── hrms-iam (8081)
    ├── hrms-organization (8082)
    ├── ... (14 個微服務, scale-to-zero)
    └── hrms-redis (internal)
              ↓
Azure Database for PostgreSQL Flexible Server
    └── 14 個資料庫
```

## 快速開始

### 1. 建立基礎設施（一次性）

```bash
az login
bash deploy/azure/setup-azure.sh
```

### 2. 設定 GitHub Secrets

| Secret | 說明 |
|:---|:---|
| `AZURE_CREDENTIALS` | `az ad sp create-for-rbac` 的 JSON 輸出 |
| `AZURE_ACR_LOGIN_SERVER` | ACR 登入伺服器（如 hrmsacr.azurecr.io） |
| `AZURE_ACR_USERNAME` | ACR 使用者名稱 |
| `AZURE_ACR_PASSWORD` | ACR 密碼 |
| `AZURE_RESOURCE_GROUP` | rg-hrms |
| `AZURE_CAE_NAME` | hrms-env |

建立 Service Principal：
```bash
az ad sp create-for-rbac --name "hrms-github-actions" \
  --role contributor \
  --scopes /subscriptions/<subscription-id>/resourceGroups/rg-hrms \
  --json-auth
```

### 3. 首次部署微服務

```bash
# 設定環境變數後執行
bash deploy/azure/deploy-services.sh
```

### 4. 後續更新

Push 到 main 即自動觸發 CI/CD。

## 每月成本估算

| 服務 | SKU | 月費 |
|:---|:---|---:|
| Container Registry | Basic | ~$5 |
| PostgreSQL Flexible | Burstable B1ms | ~$12 |
| Container Apps | Consumption (scale-to-zero) | ~$0-5 |
| Redis (容器) | - | ~$0-3 |
| **合計** | | **~$17-25** |

## 省錢技巧

- PostgreSQL 不用時可 `az postgres flexible-server stop`，停止不計費
- Container Apps scale-to-zero：無流量 = 不計費
- 面試展示前用 `az postgres flexible-server start` 喚醒
