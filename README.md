# 会议室预约系统

基于 Java SpringBoot + Vue3 + Element Plus + MySQL 开发的公司会议室预约系统。

## 技术栈

- **后端**: Java 17, SpringBoot 3.2, MyBatis Plus, Spring Security, JWT
- **前端**: Vue3, Element Plus, Pinia, Axios, Dayjs
- **数据库**: MySQL 8.0, Redis 7
- **部署**: Docker, Docker Compose

## 核心功能

1. **会议室管理** - 管理员可添加、编辑、删除会议室，设置容量、设施等信息
2. **预约时间段选择** - 可视化时间槽选择，支持按日期查看会议室可用时段
3. **冲突检测** - 自动检测时间冲突，防止重复预约
4. **预约审批** - 管理员审批预约申请，支持通过/拒绝操作
5. **我的预约记录** - 查看个人预约历史，支持取消待审批或已通过的预约
6. **预约取消提醒** - 取消预约时确认提示

## 快速开始

### 环境要求

- Docker & Docker Compose
- Java 17+
- Maven 3.8+
- Node.js 18+

### 启动系统

```bash
# 使用启动脚本一键启动
./scripts/start.sh
```

启动脚本会自动完成以下操作：
1. 启动 Docker 服务 (MySQL, Redis)
2. 构建后端项目
3. 启动后端服务 (http://localhost:8080)
4. 安装前端依赖
5. 启动前端服务 (http://localhost:5173)

### 停止系统

```bash
./scripts/stop.sh
```

### 默认账号

- **管理员**: admin / admin
- **普通用户**: user / user

## 项目结构

```
meeting-room-system/
├── backend/              # SpringBoot 后端
│   ├── src/main/java/    # Java 源代码
│   └── pom.xml          # Maven 配置
├── frontend/             # Vue3 前端
│   ├── src/             # 源代码
│   └── package.json     # NPM 配置
├── docker/               # Docker 配置
│   ├── docker-compose.yml
│   └── init.sql         # 数据库初始化脚本
└── scripts/              # 启动脚本
    ├── start.sh
    └── stop.sh
```

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `GET /api/auth/info` - 获取当前用户信息

### 会议室接口
- `GET /api/rooms` - 获取会议室列表
- `POST /api/rooms` - 添加会议室 (管理员)
- `PUT /api/rooms/{id}` - 更新会议室 (管理员)
- `DELETE /api/rooms/{id}` - 删除会议室 (管理员)

### 预约接口
- `POST /api/reservations` - 创建预约
- `GET /api/reservations/my` - 获取我的预约
- `GET /api/reservations` - 获取所有预约 (管理员)
- `PUT /api/reservations/{id}/cancel` - 取消预约
- `POST /api/reservations/approve` - 审批预约 (管理员)
- `GET /api/reservations/room/{roomId}/timeslots` - 获取时间段
- `GET /api/reservations/check-conflict` - 检查时间冲突

## 数据库设计

### 用户表 (users)
- id, username, password, real_name, email, phone, role, status

### 会议室表 (meeting_rooms)
- id, name, location, capacity, facilities, description, status

### 预约表 (reservations)
- id, room_id, user_id, title, attendees, start_time, end_time, description, status, approver_id, approval_time, approval_remark

## 开发说明

### 后端开发

```bash
cd backend
mvn spring-boot:run
```

### 前端开发

```bash
cd frontend
npm install
npm run dev
```

## 许可证

MIT License
