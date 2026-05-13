#!/bin/bash

# 会议室预约系统启动脚本

set -e

echo "=========================================="
echo "   会议室预约系统 - 启动脚本"
echo "=========================================="

# 获取项目根目录
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$PROJECT_ROOT"

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    echo "错误: Docker 未安装，请先安装 Docker"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "错误: Docker Compose 未安装，请先安装 Docker Compose"
    exit 1
fi

# 检查 Java 是否安装
if ! command -v java &> /dev/null; then
    echo "错误: Java 未安装，请先安装 Java 17+"
    exit 1
fi

# 检查 Maven 是否安装
if ! command -v mvn &> /dev/null; then
    echo "错误: Maven 未安装，请先安装 Maven"
    exit 1
fi

# 检查 Node.js 是否安装
if ! command -v node &> /dev/null; then
    echo "错误: Node.js 未安装，请先安装 Node.js 18+"
    exit 1
fi

echo ""
echo "[1/5] 启动 Docker 服务 (MySQL, Redis)..."
cd "$PROJECT_ROOT/docker"
docker-compose up -d

# 等待 MySQL 启动
echo "等待 MySQL 启动..."
sleep 10

# 检查 MySQL 是否就绪
for i in {1..30}; do
    if docker exec meeting-room-mysql mysql -umeeting_user -pmeeting_pass -e "SELECT 1" meeting_room &> /dev/null; then
        echo "MySQL 已就绪"
        break
    fi
    echo "等待 MySQL... ($i/30)"
    sleep 2
done

echo ""
echo "[2/5] 构建后端项目..."
cd "$PROJECT_ROOT/backend"
mvn clean package -DskipTests

echo ""
echo "[3/5] 启动后端服务..."
nohup java -jar target/meeting-room-system-1.0.0.jar > "$PROJECT_ROOT/backend.log" 2>&1 &
BACKEND_PID=$!
echo "后端服务已启动，PID: $BACKEND_PID"

# 等待后端启动
echo "等待后端服务启动..."
sleep 5
for i in {1..30}; do
    if curl -s http://localhost:8080/api/auth/login -X POST -H "Content-Type: application/json" -d '{"username":"test","password":"test"}' &> /dev/null || curl -s http://localhost:8080 &> /dev/null; then
        echo "后端服务已就绪"
        break
    fi
    echo "等待后端服务... ($i/30)"
    sleep 2
done

echo ""
echo "[4/5] 安装前端依赖..."
cd "$PROJECT_ROOT/frontend"
if [ ! -d "node_modules" ]; then
    npm install
fi

echo ""
echo "[5/5] 启动前端服务..."
nohup npm run dev > "$PROJECT_ROOT/frontend.log" 2>&1 &
FRONTEND_PID=$!
echo "前端服务已启动，PID: $FRONTEND_PID"

# 保存 PID
echo "$BACKEND_PID" > "$PROJECT_ROOT/backend.pid"
echo "$FRONTEND_PID" > "$PROJECT_ROOT/frontend.pid"

echo ""
echo "=========================================="
echo "   系统启动成功！"
echo "=========================================="
echo ""
echo "  前端访问: http://localhost:5173"
echo "  后端API: http://localhost:8080"
echo ""
echo "  默认账号:"
echo "    管理员: admin / admin"
echo "    普通用户: user / user"
echo ""
echo "  日志文件:"
echo "    后端: $PROJECT_ROOT/backend.log"
echo "    前端: $PROJECT_ROOT/frontend.log"
echo ""
echo "  停止服务: ./scripts/stop.sh"
echo "=========================================="
