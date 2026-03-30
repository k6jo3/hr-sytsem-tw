#!/bin/bash
# ============================================================
# HRMS Azure 一鍵啟動腳本
# 用途：demo 前執行，啟動所有服務
# 執行：bash scripts/azure-start.sh
# ============================================================

set -e

RG="hrms-demo-rg"
PG_SERVER="hrms-pg-demo"
GREEN='\033[0;32m'
NC='\033[0m'
log() { echo -e "${GREEN}[✓]${NC} $1"; }

echo "=========================================="
echo "  啟動 HRMS 所有服務"
echo "=========================================="

# 啟動 Azure Database for PostgreSQL
echo "啟動 PostgreSQL（Azure Flexible Server）..."
az postgres flexible-server start --name $PG_SERVER --resource-group $RG --output none 2>/dev/null
log "PostgreSQL"

# 啟動 Redis
echo "啟動 Redis..."
az containerapp update --name hrms-redis --resource-group $RG --min-replicas 1 --output none 2>/dev/null
log "Redis"

echo "等待基礎設施就緒（30 秒）..."
sleep 30

# 啟動後端微服務 + Gateway
SERVICES=(hrms-gateway hrms-iam hrms-organization hrms-attendance hrms-payroll hrms-insurance hrms-project hrms-timesheet hrms-performance hrms-recruitment hrms-training hrms-workflow hrms-notification hrms-document hrms-reporting)

for SERVICE in "${SERVICES[@]}"; do
  echo -n "  啟動 ${SERVICE}..."
  az containerapp update --name $SERVICE --resource-group $RG --min-replicas 1 --output none 2>/dev/null && echo " ✓" || echo " (尚未建立)"
done
log "後端服務已啟動"

# 啟動前端
echo "啟動前端..."
az containerapp update --name hrms-frontend --resource-group $RG --min-replicas 1 --output none 2>/dev/null
log "前端"

# 取得前端 URL
echo ""
echo "=========================================="
FQDN=$(az containerapp show --name hrms-frontend --resource-group $RG --query "properties.configuration.ingress.fqdn" -o tsv 2>/dev/null || echo "尚未部署")
echo "  前端 URL：https://${FQDN}"
echo "  等待 Java 冷啟動約 90 秒後可使用"
echo "=========================================="
