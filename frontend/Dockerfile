## ---------- Build stage ----------
FROM node:20-alpine AS builder
WORKDIR /app

# 使用 corepack 管理 pnpm
ENV COREPACK_ENABLE_STRICT=0
RUN corepack enable && corepack prepare pnpm@9.12.3 --activate

# 复制依赖清单并安装
COPY pnpm-*.yaml ./
COPY package.json ./
COPY pnpm-workspace.yaml ./
COPY packages ./packages
RUN pnpm install --frozen-lockfile

# 复制源码
COPY . .

# 可通过构建参数覆盖后端地址，默认走 Nginx 反向代理 /api/
ARG VITE_SERVICE_BASE_URL=/api/
ENV VITE_SERVICE_BASE_URL=${VITE_SERVICE_BASE_URL}

# 构建
RUN pnpm -w build

## ---------- Runtime stage ----------
FROM nginx:1.27-alpine

# 安装必要的工具
RUN apk add --no-cache curl

# 复制静态资源与Nginx配置
COPY --from=builder /app/dist/ /usr/share/nginx/html/
COPY nginx/default.conf /etc/nginx/conf.d/default.conf

# 设置权限
RUN chown -R nginx:nginx /usr/share/nginx/html && \
  chown -R nginx:nginx /var/cache/nginx && \
  chown -R nginx:nginx /var/log/nginx && \
  chown -R nginx:nginx /etc/nginx/conf.d && \
  touch /var/run/nginx.pid && \
  chown -R nginx:nginx /var/run/nginx.pid

# 切换到非root用户
USER nginx

EXPOSE 80

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:80/ || exit 1

# 启动Nginx
CMD ["nginx", "-g", "daemon off;"]
