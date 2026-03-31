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

# azure 模式：nginx 的靜態 proxy_pass 用短名稱 hrms-gateway
# 但系統 DNS（resolv.conf）能解析，nginx 也能
# 只是需要等 Gateway DNS ready 才啟動 nginx
CMD ["/bin/sh", "-c", "\
  if [ \"$DEPLOY_ENV\" = \"azure\" ]; then \
    sed -i 's|http://hrms-gateway:8080|http://hrms-gateway|' /etc/nginx/conf.d/default.conf; \
    echo 'Waiting for Gateway DNS...'; \
    for i in $(seq 1 30); do \
      wget -q -O /dev/null http://hrms-gateway/actuator/health 2>/dev/null && break; \
      echo \"  Retry $i...\"; \
      sleep 3; \
    done; \
    echo 'Gateway ready, starting nginx'; \
  fi && \
  nginx -g 'daemon off;'"]
