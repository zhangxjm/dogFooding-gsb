#!/bin/bash

# 会议室预约系统停止脚本

set -e

echo "=========================================="
echo "   会议室预约系统 - 停止脚本"
echo "=========================================="

# 获取项目根目录
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

# 停止前端服务
if [ -f "$PROJECT_ROOT/frontend.pid" ]; then
    FRONTEND_PID=$(cat "$PROJECT_ROOT/frontend.pid")
    if ps -p "$FRONTEND_PID" > /dev/null 2>&1; then
        echo "停止前端服务 (PID: $FRONTEND_PID)..."
        kill "$FRONTEND_PID" 2>/dev/null || true
    fi
    rm -f "$PROJECT_ROOT/frontend.pid"
fi

# 停止后端服务
if [ -f "$PROJECT_ROOT/backend.pid" ]; then
    BACKEND_PID=$(cat "$PROJECT_ROOT/backend.pid")
    if ps -p "$BACKEND_PID" > /dev/null 2>&1; then
        echo "停止后端服务 (PID: $BACKEND_PID)..."
        kill "$BACKEND_PID" 2>/dev/null || true
    fi
    rm -f "$PROJECT_ROOT/backend.pid"
fi

# 停止 Docker 服务
echo "停止 Docker 服务..."
cd "$PROJECT_ROOT/docker"
docker-compose down

echo ""
echo "=========================================="
echo "   系统已停止"
echo "=========================================="
