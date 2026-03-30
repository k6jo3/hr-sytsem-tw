# 前端 Multi-stage Dockerfile

# ===== Stage 1: Build =====
FROM node:20-alpine AS builder

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci --ignore-scripts

COPY . .
RUN npm run build

# ===== Stage 2: Nginx =====
FROM nginx:alpine

# 複製自訂 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

# GATEWAY_PORT 預設 8080（Docker Compose），Azure Container Apps 設為 80
ENV GATEWAY_PORT=8080

# 用 sed 替換 nginx.conf 中的 GATEWAY_PORT 後啟動（比 envsubst 安全，不會動到 nginx 的 $host 等變數）
CMD ["/bin/sh", "-c", "sed -i \"s/\\${GATEWAY_PORT}/$GATEWAY_PORT/g\" /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
