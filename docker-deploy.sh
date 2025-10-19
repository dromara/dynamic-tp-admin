#!/bin/bash

# Docker部署脚本
set -e

echo "🚀 开始部署 Dynamic TP Admin..."

# 检查Docker是否安装
if ! command -v docker &> /dev/null; then
    echo "❌ Docker未安装，请先安装Docker"
    exit 1
fi

# 检查Docker Compose是否安装
if ! command -v docker-compose &> /dev/null && ! docker compose version &> /dev/null; then
    echo "❌ Docker Compose未安装，请先安装Docker Compose"
    exit 1
fi

# 检查环境变量文件
if [ ! -f ".env" ]; then
    echo "⚠️  未找到.env文件，将使用默认配置"
    echo "💡 可以复制env.example为.env并修改配置"
fi

# 停止并删除现有容器
echo "🛑 停止现有容器..."
docker-compose down --remove-orphans 2>/dev/null || docker compose down --remove-orphans 2>/dev/null

# 清理镜像（可选）
if [ "$1" = "--clean" ]; then
    echo "🧹 清理镜像..."
    docker-compose down --rmi all --volumes --remove-orphans 2>/dev/null || docker compose down --rmi all --volumes --remove-orphans 2>/dev/null
fi

# 构建并启动服务
echo "🔨 构建并启动服务..."
docker-compose up -d --build 2>/dev/null || docker compose up -d --build

# 等待服务启动
echo "⏳ 等待服务启动..."
sleep 30

# 检查服务状态
echo "📊 服务状态："
docker-compose ps 2>/dev/null || docker compose ps

# 检查健康状态
echo "🏥 健康检查："
for service in mysql redis admin frontend; do
    if docker-compose exec -T $service curl -f http://localhost:$(docker-compose port $service | cut -d: -f2) >/dev/null 2>&1 || \
       docker compose exec -T $service curl -f http://localhost:$(docker compose port $service | cut -d: -f2) >/dev/null 2>&1; then
        echo "✅ $service: 健康"
    else
        echo "❌ $service: 不健康"
    fi
done

echo ""
echo "🎉 部署完成！"
echo "🌐 前端地址: http://localhost"
echo "🔧 后端地址: http://localhost:9999"
echo "📚 API文档: http://localhost:9999/doc.html"
echo ""
echo "📋 常用命令："
echo "  查看日志: docker-compose logs -f [服务名]"
echo "  停止服务: docker-compose down"
echo "  重启服务: docker-compose restart [服务名]"
echo "  进入容器: docker-compose exec [服务名] sh"
