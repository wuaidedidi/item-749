# 洗衣店订单管理系统

洗衣店订单管理系统用于门店收衣、洗护服务计价、订单流转、客户资料维护和经营数据统计。后端采用 Java Spring MVC + MyBatis，前端采用原生 HTML/CSS/JavaScript。

## 功能特性

- 订单管理：创建订单、查看明细、更新处理状态、取消订单
- 客户管理：客户信息增删改查、手机号唯一校验
- 服务管理：维护水洗、干洗、熨烫、修补等服务项目
- 数据统计：控制台展示订单数量、收入、客户数和最近订单
- 系统管理：JWT 登录认证、管理员和员工角色

## 快速开始

项目交付环境使用项目同级目录的单个 Dockerfile 启动，源码目录内不再保留 compose 或子镜像配置。

```bash
# 在项目同级目录构建
docker build -t laundry-management-749 .

# 启动
docker run --rm -p 3000:3000 -p 8000:8080 laundry-management-749
```

访问地址：

| 服务 | 地址 |
|------|------|
| 前端界面 | http://localhost:3000 |
| 后端接口 | http://localhost:8000/api |
| 健康检查 | http://localhost:8000/api/health |

测试账号：

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | 管理员 |
| staff001 | 123456 | 员工 |
| staff002 | 123456 | 员工 |

## 本地开发

后端配置位于 `backend/src/main/resources/jdbc.properties`，默认连接 `127.0.0.1:3306/laundry_db`。数据库初始化脚本位于 `database/init.sql`。

后端构建：

```bash
cd backend
mvn -s settings.xml package -DskipTests
```

前端是静态页面，主要文件位于 `frontend/pages`、`frontend/js` 和 `frontend/css`。Nginx 配置位于 `frontend/nginx.conf`，默认监听 3000 并代理 `/api` 到同机 8080。

## 项目结构

```text
洗衣店订单管理系统/
├── backend/
│   ├── pom.xml
│   ├── settings.xml
│   └── src/
├── database/
│   └── init.sql
├── frontend/
│   ├── css/
│   ├── js/
│   ├── pages/
│   ├── index.html
│   └── nginx.conf
├── README.md
└── result.md
```

## 主要接口

- `POST /api/auth/login`：用户登录
- `POST /api/auth/register`：用户注册
- `GET /api/customers`：客户列表
- `GET /api/services`：服务项目列表
- `GET /api/orders`：订单列表
- `POST /api/orders`：创建订单
- `PUT /api/orders/{id}/status`：更新订单状态
- `GET /api/dashboard/stats`：控制台统计
