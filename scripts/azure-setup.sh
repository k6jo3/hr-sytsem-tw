#!/bin/bash
# ============================================================
# HRMS Azure Container Apps 一鍵部署腳本
# 用途：建立所有 Azure 資源 + 設定 GitHub Secrets
# 執行：bash scripts/azure-setup.sh
# ============================================================

set -e

# ===== 配置區（可修改）=====
RESOURCE_GROUP="hrms-demo-rg"
LOCATION="eastasia"              # 東亞（香港），延遲較低
CAE_NAME="hrms-demo-env"         # Container Apps Environment 名稱
GITHUB_REPO="k6jo3/hr-sytsem-tw" # GitHub repo（新名稱）
SUBSCRIPTION_ID="9e00ad61-ec8a-4e7a-9b9e-1d61fabfae34"

# ghcr.io image 路徑
REGISTRY="ghcr.io"
IMAGE_PREFIX="${REGISTRY}/${GITHUB_REPO}"

# ===== 顏色輸出 =====
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m'

log() { echo -e "${GREEN}[✓]${NC} $1"; }
warn() { echo -e "${YELLOW}[!]${NC} $1"; }
err() { echo -e "${RED}[✗]${NC} $1"; exit 1; }

# ===== Step 1：建立 Resource Group =====
echo ""
echo "=========================================="
echo "  Step 1：建立 Resource Group"
echo "=========================================="
az group create \
  --name $RESOURCE_GROUP \
  --location $LOCATION \
  --output none
log "Resource Group: $RESOURCE_GROUP ($LOCATION)"

# ===== Step 2：建立 Container Apps Environment =====
echo ""
echo "=========================================="
echo "  Step 2：建立 Container Apps Environment"
echo "=========================================="
az containerapp env create \
  --name $CAE_NAME \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --output none
log "Container Apps Environment: $CAE_NAME"

# ===== Step 3：建立 Azure File Share（PostgreSQL 資料持久化）=====
echo ""
echo "=========================================="
echo "  Step 3：建立 Storage（資料持久化）"
echo "=========================================="
STORAGE_ACCOUNT="hrmsstorage$(date +%s | tail -c 6)"
az storage account create \
  --name $STORAGE_ACCOUNT \
  --resource-group $RESOURCE_GROUP \
  --location $LOCATION \
  --sku Standard_LRS \
  --output none
log "Storage Account: $STORAGE_ACCOUNT"

STORAGE_KEY=$(az storage account keys list \
  --account-name $STORAGE_ACCOUNT \
  --resource-group $RESOURCE_GROUP \
  --query "[0].value" -o tsv)

az storage share create \
  --name pgdata \
  --account-name $STORAGE_ACCOUNT \
  --quota 1 \
  --output none
log "File Share: pgdata (1 GB)"

# 掛載到 Container Apps Environment
az containerapp env storage set \
  --name $CAE_NAME \
  --resource-group $RESOURCE_GROUP \
  --storage-name pgdata \
  --azure-file-account-name $STORAGE_ACCOUNT \
  --azure-file-account-key "$STORAGE_KEY" \
  --azure-file-share-name pgdata \
  --access-mode ReadWrite \
  --output none
log "Storage 已掛載到 Environment"

# ===== Step 4：部署 PostgreSQL =====
echo ""
echo "=========================================="
echo "  Step 4：部署 PostgreSQL"
echo "=========================================="
az containerapp create \
  --name hrms-postgres \
  --resource-group $RESOURCE_GROUP \
  --environment $CAE_NAME \
  --image postgres:15-alpine \
  --cpu 0.5 \
  --memory 1Gi \
  --min-replicas 1 \
  --max-replicas 1 \
  --target-port 5432 \
  --ingress internal \
  --transport tcp \
  --env-vars \
    POSTGRES_USER=hrms \
    POSTGRES_PASSWORD=hrms_demo_2026 \
    PGDATA=/var/lib/postgresql/data/pgdata \
  --output none
log "PostgreSQL 已部署（internal ingress, port 5432）"

# ===== Step 5：部署 Redis =====
echo ""
echo "=========================================="
echo "  Step 5：部署 Redis"
echo "=========================================="
az containerapp create \
  --name hrms-redis \
  --resource-group $RESOURCE_GROUP \
  --environment $CAE_NAME \
  --image redis:7-alpine \
  --cpu 0.25 \
  --memory 0.5Gi \
  --min-replicas 1 \
  --max-replicas 1 \
  --target-port 6379 \
  --ingress internal \
  --transport tcp \
  --output none
log "Redis 已部署（internal ingress, port 6379）"

# ===== Step 6：部署 Gateway =====
echo ""
echo "=========================================="
echo "  Step 6：部署 Gateway"
echo "=========================================="
az containerapp create \
  --name hrms-gateway \
  --resource-group $RESOURCE_GROUP \
  --environment $CAE_NAME \
  --image "${IMAGE_PREFIX}/hrms-gateway:latest" \
  --registry-server $REGISTRY \
  --cpu 0.25 \
  --memory 0.5Gi \
  --min-replicas 0 \
  --max-replicas 2 \
  --target-port 8080 \
  --ingress internal \
  --env-vars \
    SPRING_PROFILES_ACTIVE=docker \
    SPRING_DATA_REDIS_HOST=hrms-redis \
    EUREKA_ENABLED=false \
  --output none
log "Gateway 已部署"

# ===== Step 7：部署 14 個後端微服務 =====
echo ""
echo "=========================================="
echo "  Step 7：部署後端微服務（14 個）"
echo "=========================================="

SERVICES=(
  "hrms-iam:8081"
  "hrms-organization:8082"
  "hrms-attendance:8083"
  "hrms-payroll:8084"
  "hrms-insurance:8085"
  "hrms-project:8086"
  "hrms-timesheet:8087"
  "hrms-performance:8088"
  "hrms-recruitment:8089"
  "hrms-training:8090"
  "hrms-workflow:8091"
  "hrms-notification:8092"
  "hrms-document:8093"
  "hrms-reporting:8094"
)

for ENTRY in "${SERVICES[@]}"; do
  SERVICE="${ENTRY%%:*}"
  PORT="${ENTRY##*:}"
  echo -n "  部署 ${SERVICE}..."
  az containerapp create \
    --name $SERVICE \
    --resource-group $RESOURCE_GROUP \
    --environment $CAE_NAME \
    --image "${IMAGE_PREFIX}/${SERVICE}:latest" \
    --registry-server $REGISTRY \
    --cpu 0.25 \
    --memory 0.5Gi \
    --min-replicas 0 \
    --max-replicas 2 \
    --target-port $PORT \
    --ingress internal \
    --env-vars \
      SPRING_PROFILES_ACTIVE=docker \
      SPRING_DATASOURCE_URL="jdbc:postgresql://hrms-postgres:5432/hrms_${SERVICE##hrms-}" \
      SPRING_DATASOURCE_USERNAME=hrms \
      SPRING_DATASOURCE_PASSWORD=hrms_demo_2026 \
      SPRING_DATA_REDIS_HOST=hrms-redis \
      SPRING_KAFKA_ENABLED=false \
      EUREKA_ENABLED=false \
      SERVER_PORT=$PORT \
    --output none 2>/dev/null && echo " ✓" || echo " (image 尚未推送，稍後更新)"
done
log "後端微服務部署完成"

# ===== Step 8：部署前端 =====
echo ""
echo "=========================================="
echo "  Step 8：部署前端（對外 ingress）"
echo "=========================================="
az containerapp create \
  --name hrms-frontend \
  --resource-group $RESOURCE_GROUP \
  --environment $CAE_NAME \
  --image "${IMAGE_PREFIX}/frontend:latest" \
  --registry-server $REGISTRY \
  --cpu 0.25 \
  --memory 0.5Gi \
  --min-replicas 0 \
  --max-replicas 2 \
  --target-port 80 \
  --ingress external \
  --output none 2>/dev/null || warn "Frontend image 尚未推送，稍後更新"
log "前端已部署（external ingress）"

# ===== Step 9：建立 Service Principal（給 GitHub Actions 用）=====
echo ""
echo "=========================================="
echo "  Step 9：建立 Service Principal"
echo "=========================================="
SP_JSON=$(az ad sp create-for-rbac \
  --name "hrms-github-deploy" \
  --role contributor \
  --scopes /subscriptions/$SUBSCRIPTION_ID/resourceGroups/$RESOURCE_GROUP \
  --sdk-auth 2>/dev/null || echo "EXISTING")

if [ "$SP_JSON" = "EXISTING" ]; then
  warn "Service Principal 可能已存在，請手動確認"
  warn "如需重建：az ad sp create-for-rbac --name hrms-github-deploy --role contributor --scopes /subscriptions/$SUBSCRIPTION_ID/resourceGroups/$RESOURCE_GROUP --sdk-auth"
else
  log "Service Principal 已建立"
fi

# ===== Step 10：輸出結果 =====
echo ""
echo "=========================================="
echo "  部署完成！"
echo "=========================================="
echo ""

FRONTEND_FQDN=$(az containerapp show \
  --name hrms-frontend \
  --resource-group $RESOURCE_GROUP \
  --query "properties.configuration.ingress.fqdn" -o tsv 2>/dev/null || echo "尚未就緒")

echo "前端 URL：https://${FRONTEND_FQDN}"
echo ""
echo "=========================================="
echo "  GitHub Secrets 設定（複製到 GitHub）"
echo "=========================================="
echo ""
echo "AZURE_RESOURCE_GROUP = ${RESOURCE_GROUP}"
echo "AZURE_CAE_NAME       = ${CAE_NAME}"
echo ""

if [ "$SP_JSON" != "EXISTING" ]; then
  echo "AZURE_CREDENTIALS（整段 JSON 複製）："
  echo "$SP_JSON"
fi

echo ""
echo "=========================================="
echo "  設定步驟"
echo "=========================================="
echo "1. 到 GitHub → Settings → Secrets → Actions"
echo "2. 新增以上 3 個 Secrets"
echo "3. 先手動觸發 CI/CD workflow 推送 images 到 ghcr.io"
echo "4. 再觸發 Azure CD workflow 更新容器"
echo "=========================================="
