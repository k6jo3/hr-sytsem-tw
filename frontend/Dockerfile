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

# DEPLOY_ENV: "compose"（預設）或 "azure"
ENV DEPLOY_ENV=compose

# azure 模式：
# 1. 從 /etc/resolv.conf 讀取 search domain
# 2. 拼接完整 FQDN（hrms-gateway.<search-domain>）
# 3. 用 resolver + set 變數讓 nginx 延遲解析（不在啟動時解析）
# compose 模式：直接用 proxy_pass http://hrms-gateway:8080（docker network）
CMD ["/bin/sh", "-c", "\
  if [ \"$DEPLOY_ENV\" = \"azure\" ]; then \
    SEARCH=$(grep search /etc/resolv.conf | awk '{print $2}'); \
    FQDN=\"hrms-gateway.${SEARCH:-k8se-apps.svc.cluster.local}\"; \
    echo \"Gateway FQDN: ${FQDN}\"; \
    sed -i \"s|proxy_pass http://hrms-gateway:8080;|resolver 127.0.0.11 valid=5s ipv6=off;\\n        set \\$backend \\\"http://${FQDN}\\\";\\n        proxy_pass \\$backend;|\" /etc/nginx/conf.d/default.conf; \
    sed -i 's|proxy_set_header Host hrms-gateway;|proxy_set_header Host hrms-gateway;|' /etc/nginx/conf.d/default.conf; \
    echo '--- nginx config ---'; \
    grep -A3 proxy_pass /etc/nginx/conf.d/default.conf; \
    echo '---'; \
  fi && \
  nginx -g 'daemon off;'"]
