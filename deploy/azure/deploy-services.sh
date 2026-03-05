#!/bin/bash
# 部署/更新所有微服務到 Azure Container Apps
# 用法：bash deploy-services.sh
#
# 需先設定環境變數或修改下方參數

set -e

# ===== 設定參數（請修改為實際值）=====
RESOURCE_GROUP="${AZURE_RESOURCE_GROUP:-rg-hrms}"
CAE_NAME="${AZURE_CAE_NAME:-hrms-env}"
ACR_LOGIN_SERVER="${AZURE_ACR_LOGIN_SERVER}"
ACR_USERNAME="${AZURE_ACR_USERNAME}"
ACR_PASSWORD="${AZURE_ACR_PASSWORD}"
IMAGE_TAG="${IMAGE_TAG:-latest}"

# 資料庫連線
DB_HOST="${DB_HOST}"
DB_USERNAME="${DB_USERNAME:-hrms_admin}"
DB_PASSWORD="${DB_PASSWORD}"

# JWT
JWT_SECRET="${JWT_SECRET:-your-256-bit-secret-key-for-jwt-token-generation-must-be-at-least-256-bits-long}"

# ===== 服務清單 =====
declare -A SERVICES
SERVICES=(
    ["hrms-iam"]="hrms_iam:8081"
    ["hrms-organization"]="hrms_organization:8082"
    ["hrms-attendance"]="hrms_attendance:8083"
    ["hrms-payroll"]="hrms_payroll:8084"
    ["hrms-insurance"]="hrms_insurance:8085"
    ["hrms-project"]="hrms_project:8086"
    ["hrms-timesheet"]="hrms_timesheet:8087"
    ["hrms-performance"]="hrms_performance:8088"
    ["hrms-recruitment"]="hrms_recruitment:8089"
    ["hrms-training"]="hrms_training:8090"
    ["hrms-workflow"]="hrms_workflow:8091"
    ["hrms-notification"]="hrms_notification:8092"
    ["hrms-document"]="hrms_document:8093"
    ["hrms-reporting"]="hrms_reporting:8094"
)

echo "=== 部署 HRMS 微服務到 Azure Container Apps ==="

for SERVICE_NAME in "${!SERVICES[@]}"; do
    IFS=':' read -r DB_NAME PORT <<< "${SERVICES[$SERVICE_NAME]}"

    echo ""
    echo ">>> 部署 ${SERVICE_NAME} (DB: ${DB_NAME}, Port: ${PORT})..."

    # 檢查服務是否已存在
    EXISTS=$(az containerapp show --name $SERVICE_NAME --resource-group $RESOURCE_GROUP 2>/dev/null && echo "yes" || echo "no")

    if [ "$EXISTS" = "yes" ]; then
        # 更新既有服務
        az containerapp update \
            --name $SERVICE_NAME \
            --resource-group $RESOURCE_GROUP \
            --image "${ACR_LOGIN_SERVER}/${SERVICE_NAME}:${IMAGE_TAG}"
    else
        # 建立新服務
        az containerapp create \
            --resource-group $RESOURCE_GROUP \
            --environment $CAE_NAME \
            --name $SERVICE_NAME \
            --image "${ACR_LOGIN_SERVER}/${SERVICE_NAME}:${IMAGE_TAG}" \
            --registry-server $ACR_LOGIN_SERVER \
            --registry-username $ACR_USERNAME \
            --registry-password $ACR_PASSWORD \
            --min-replicas 0 \
            --max-replicas 1 \
            --cpu 0.25 \
            --memory 0.5Gi \
            --ingress internal \
            --target-port $PORT \
            --env-vars \
                "DB_HOST=${DB_HOST}" \
                "DB_PORT=5432" \
                "DB_NAME=${DB_NAME}" \
                "DB_USERNAME=${DB_USERNAME}" \
                "DB_PASSWORD=${DB_PASSWORD}" \
                "REDIS_HOST=hrms-redis" \
                "REDIS_PORT=6379" \
                "KAFKA_SERVERS=" \
                "EUREKA_ENABLED=false" \
                "JWT_SECRET=${JWT_SECRET}" \
                "SPRING_PROFILES_ACTIVE=prod" \
            --scale-rule-name http-rule \
            --scale-rule-type http \
            --scale-rule-http-concurrency 10
    fi

    echo "    ✅ ${SERVICE_NAME} 部署完成"
done

# ===== 部署前端（Nginx）=====
echo ""
echo ">>> 部署前端 (Nginx)..."

FRONTEND_EXISTS=$(az containerapp show --name hrms-frontend --resource-group $RESOURCE_GROUP 2>/dev/null && echo "yes" || echo "no")

if [ "$FRONTEND_EXISTS" = "yes" ]; then
    az containerapp update \
        --name hrms-frontend \
        --resource-group $RESOURCE_GROUP \
        --image "${ACR_LOGIN_SERVER}/frontend:${IMAGE_TAG}"
else
    az containerapp create \
        --resource-group $RESOURCE_GROUP \
        --environment $CAE_NAME \
        --name hrms-frontend \
        --image "${ACR_LOGIN_SERVER}/frontend:${IMAGE_TAG}" \
        --registry-server $ACR_LOGIN_SERVER \
        --registry-username $ACR_USERNAME \
        --registry-password $ACR_PASSWORD \
        --min-replicas 0 \
        --max-replicas 2 \
        --cpu 0.25 \
        --memory 0.5Gi \
        --ingress external \
        --target-port 80 \
        --scale-rule-name http-rule \
        --scale-rule-type http \
        --scale-rule-http-concurrency 20
fi

# 取得前端 URL
FRONTEND_URL=$(az containerapp show --name hrms-frontend --resource-group $RESOURCE_GROUP --query "properties.configuration.ingress.fqdn" -o tsv)

echo ""
echo "=========================================="
echo "  部署完成！"
echo "=========================================="
echo ""
echo "  前端 URL: https://${FRONTEND_URL}"
echo ""
echo "  所有微服務已設定 scale-to-zero"
echo "  無流量時自動縮為 0，有請求時自動啟動"
