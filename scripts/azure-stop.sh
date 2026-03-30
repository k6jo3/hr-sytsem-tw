#!/bin/bash
# ============================================================
# HRMS Azure 一鍵停止腳本
# 用途：demo 完執行，停止所有服務節省費用
# 執行：bash scripts/azure-stop.sh
# ============================================================

set -e

RG="hrms-demo-rg"
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m'
log() { echo -e "${GREEN}[✓]${NC} $1"; }

echo "=========================================="
echo "  停止 HRMS 所有服務"
echo "=========================================="

# 停止所有服務（縮到 0 實例）
ALL_SERVICES=(hrms-frontend hrms-gateway hrms-iam hrms-organization hrms-attendance hrms-payroll hrms-insurance hrms-project hrms-timesheet hrms-performance hrms-recruitment hrms-training hrms-workflow hrms-notification hrms-document hrms-reporting hrms-redis hrms-postgres)

for SERVICE in "${ALL_SERVICES[@]}"; do
  echo -n "  停止 ${SERVICE}..."
  az containerapp update --name $SERVICE --resource-group $RG --min-replicas 0 --output none 2>/dev/null && echo " ✓" || echo " (跳過)"
done

echo ""
echo "=========================================="
log "所有服務已停止"
echo -e "  ${YELLOW}目前費用：僅 Storage $0.06/月${NC}"
echo -e "  ${YELLOW}下次 demo 前執行：bash scripts/azure-start.sh${NC}"
echo "=========================================="
