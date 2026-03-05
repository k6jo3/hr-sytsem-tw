#!/bin/bash
# Azure 基礎設施建置腳本
# 建立 Container Apps 環境 + PostgreSQL + ACR
#
# 前置需求：
#   1. 安裝 Azure CLI: https://docs.microsoft.com/cli/azure/install-azure-cli
#   2. 登入: az login
#   3. 設定 subscription: az account set --subscription <id>
#
# 用法：bash setup-azure.sh

set -e

# ===== 設定參數 =====
RESOURCE_GROUP="rg-hrms"
LOCATION="eastasia"               # 東亞（香港），離台灣最近
ACR_NAME="hrmsacr$(date +%s)"     # ACR 名稱需全域唯一
CAE_NAME="hrms-env"               # Container Apps Environment
PG_SERVER="hrms-pg-server"
PG_ADMIN_USER="hrms_admin"
PG_ADMIN_PASSWORD=""               # 執行時輸入

echo "=== HRMS Azure 基礎設施建置 ==="
echo ""

# 輸入 PostgreSQL 密碼
read -s -p "設定 PostgreSQL 管理員密碼 (至少 8 碼, 含大小寫+數字): " PG_ADMIN_PASSWORD
echo ""

if [ -z "$PG_ADMIN_PASSWORD" ]; then
    echo "錯誤：密碼不能為空"
    exit 1
fi

# ===== 1. Resource Group =====
echo ">>> 建立 Resource Group: ${RESOURCE_GROUP}..."
az group create \
    --name $RESOURCE_GROUP \
    --location $LOCATION

# ===== 2. Azure Container Registry (Basic SKU ~$5/月) =====
echo ">>> 建立 Container Registry: ${ACR_NAME}..."
az acr create \
    --resource-group $RESOURCE_GROUP \
    --name $ACR_NAME \
    --sku Basic \
    --admin-enabled true

# 取得 ACR 認證資訊
ACR_LOGIN_SERVER=$(az acr show --name $ACR_NAME --query loginServer -o tsv)
ACR_USERNAME=$(az acr credential show --name $ACR_NAME --query username -o tsv)
ACR_PASSWORD=$(az acr credential show --name $ACR_NAME --query "passwords[0].value" -o tsv)

echo "    ACR Server: ${ACR_LOGIN_SERVER}"

# ===== 3. Azure Database for PostgreSQL Flexible (Burstable B1ms ~$12/月) =====
echo ">>> 建立 PostgreSQL Flexible Server: ${PG_SERVER}..."
az postgres flexible-server create \
    --resource-group $RESOURCE_GROUP \
    --name $PG_SERVER \
    --location $LOCATION \
    --admin-user $PG_ADMIN_USER \
    --admin-password "$PG_ADMIN_PASSWORD" \
    --sku-name Standard_B1ms \
    --tier Burstable \
    --storage-size 32 \
    --version 15 \
    --yes

# 允許 Azure 服務存取
echo ">>> 設定防火牆規則..."
az postgres flexible-server firewall-rule create \
    --resource-group $RESOURCE_GROUP \
    --name $PG_SERVER \
    --rule-name AllowAzureServices \
    --start-ip-address 0.0.0.0 \
    --end-ip-address 0.0.0.0

# 建立 14 個資料庫
echo ">>> 建立資料庫..."
DATABASES=(hrms_iam hrms_organization hrms_attendance hrms_payroll hrms_insurance hrms_project hrms_timesheet hrms_performance hrms_recruitment hrms_training hrms_workflow hrms_notification hrms_document hrms_reporting)

for DB_NAME in "${DATABASES[@]}"; do
    echo "    建立 ${DB_NAME}..."
    az postgres flexible-server db create \
        --resource-group $RESOURCE_GROUP \
        --server-name $PG_SERVER \
        --database-name $DB_NAME
done

PG_HOST="${PG_SERVER}.postgres.database.azure.com"
echo "    PostgreSQL Host: ${PG_HOST}"

# ===== 4. Container Apps Environment =====
echo ">>> 建立 Container Apps Environment: ${CAE_NAME}..."
az containerapp env create \
    --resource-group $RESOURCE_GROUP \
    --name $CAE_NAME \
    --location $LOCATION

# ===== 5. Redis (以容器方式部署，免費) =====
echo ">>> 部署 Redis 容器..."
az containerapp create \
    --resource-group $RESOURCE_GROUP \
    --environment $CAE_NAME \
    --name hrms-redis \
    --image redis:7-alpine \
    --min-replicas 1 \
    --max-replicas 1 \
    --cpu 0.25 \
    --memory 0.5Gi \
    --ingress internal \
    --target-port 6379 \
    --transport tcp

REDIS_HOST="hrms-redis.internal.${CAE_NAME}.${LOCATION}.azurecontainerapps.io"

# ===== 輸出設定摘要 =====
echo ""
echo "=========================================="
echo "  Azure 基礎設施建置完成！"
echo "=========================================="
echo ""
echo "資源清單："
echo "  Resource Group : ${RESOURCE_GROUP}"
echo "  ACR Server     : ${ACR_LOGIN_SERVER}"
echo "  PostgreSQL Host: ${PG_HOST}"
echo "  PostgreSQL User: ${PG_ADMIN_USER}"
echo "  Redis          : hrms-redis (internal)"
echo "  Container Apps : ${CAE_NAME}"
echo ""
echo "後續步驟："
echo "  1. 將以下 Secrets 設定到 GitHub Repository:"
echo "     - AZURE_ACR_NAME=${ACR_NAME}"
echo "     - AZURE_ACR_LOGIN_SERVER=${ACR_LOGIN_SERVER}"
echo "     - AZURE_ACR_USERNAME=${ACR_USERNAME}"
echo "     - AZURE_ACR_PASSWORD=${ACR_PASSWORD}"
echo "     - AZURE_RESOURCE_GROUP=${RESOURCE_GROUP}"
echo "     - AZURE_CAE_NAME=${CAE_NAME}"
echo "     - DB_HOST=${PG_HOST}"
echo "     - DB_USERNAME=${PG_ADMIN_USER}"
echo "     - DB_PASSWORD=<你設定的密碼>"
echo ""
echo "  2. Push 到 main 觸發 CI/CD 自動部署"
echo ""
echo "每月預估成本："
echo "  ACR Basic        : ~\$5"
echo "  PostgreSQL B1ms  : ~\$12"
echo "  Container Apps   : ~\$0-5 (依流量)"
echo "  Redis Container  : ~\$0-3 (依流量)"
echo "  總計             : ~\$17-25/月"
