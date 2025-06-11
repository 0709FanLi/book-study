# 数据分析模块使用说明

## 概述

数据分析模块为自习室管理系统提供了丰富的统计和可视化功能，帮助管理员了解系统使用情况，做出数据驱动的决策。

## 功能特性

### 1. 统计概览卡片
- **总用户数**: 显示系统注册用户总数
- **座位总数**: 显示自习室可用座位总数
- **总预约数**: 显示历史预约记录总数
- **当前活跃预约**: 显示当前有效的预约数量

### 2. 图表分析

#### 2.1 预约趋势折线图 📈
- **功能**: 展示最近7天的每日预约数量变化趋势
- **图表类型**: 折线图
- **数据来源**: `reservation` 表按日期分组统计
- **用途**: 
  - 了解学生预约习惯的时间规律
  - 识别高峰期和低谷期
  - 为座位分配策略提供数据支持

#### 2.2 座位使用率统计图 📊
- **功能**: 展示各个座位的历史使用频次
- **图表类型**: 彩色柱状图
- **数据来源**: `reservation` 表按座位分组统计
- **用途**:
  - 识别最受欢迎和最少使用的座位
  - 优化座位布局和资源配置
  - 座位维护计划制定

#### 2.3 用户活跃度排行图 👥
- **功能**: 展示预约次数最多的前10名用户
- **图表类型**: 水平柱状图
- **数据来源**: `reservation` 表按用户分组统计
- **用途**:
  - 识别活跃用户
  - 了解用户使用模式
  - 制定用户激励政策

#### 2.4 24小时时间段使用分析图 ⏰
- **功能**: 展示一天中各个小时段的预约分布
- **图表类型**: 折线图
- **数据来源**: `reservation` 表按小时分组统计
- **用途**:
  - 了解学生学习时间偏好
  - 优化开放时间安排
  - 合理配置时段资源

## 访问方式

1. **管理员登录**: 使用管理员账号登录系统
2. **进入管理后台**: 点击导航栏的"管理后台"
3. **选择数据分析**: 在管理后台页面点击"数据分析"模块

## 技术实现

### 后端接口
- `GET /admin/analytics` - 数据分析主页面
- `GET /admin/analytics/reservation-trend` - 预约趋势数据API
- `GET /admin/analytics/seat-usage` - 座位使用率数据API
- `GET /admin/analytics/user-activity` - 用户活跃度数据API
- `GET /admin/analytics/hourly-usage` - 时间段使用数据API

### 前端技术
- **Chart.js**: 用于图表渲染和交互
- **Bootstrap 5**: 响应式布局和样式
- **Font Awesome**: 图标显示
- **JavaScript Fetch API**: 异步数据获取

### 数据库查询
```sql
-- 预约趋势查询
SELECT DATE(r.startTime), COUNT(r) 
FROM Reservation r 
WHERE r.startTime >= :startTime AND r.startTime <= :endTime 
GROUP BY DATE(r.startTime)

-- 座位使用率查询
SELECT s.seatNumber, COUNT(r) 
FROM Reservation r JOIN r.seat s 
GROUP BY s.seatNumber 
ORDER BY COUNT(r) DESC

-- 用户活跃度查询
SELECT u.fullName, COUNT(r) 
FROM Reservation r JOIN r.user u 
GROUP BY u.fullName 
ORDER BY COUNT(r) DESC

-- 时间段使用查询
SELECT HOUR(r.startTime), COUNT(r) 
FROM Reservation r 
GROUP BY HOUR(r.startTime)
```

## 数据准备

为了获得更好的可视化效果，建议执行 `database-sample-data.sql` 脚本来添加示例数据：

```bash
mysql -u root -p study_room_db < database-sample-data.sql
```

## 使用建议

1. **定期查看**: 建议管理员每周查看数据分析报告
2. **结合实际**: 将数据分析结果与实际管理经验相结合
3. **持续优化**: 根据数据趋势调整管理策略和资源配置
4. **数据归档**: 定期备份重要的统计数据

## 扩展功能

未来可以考虑添加以下功能：
- 月度和年度数据报表
- 数据导出功能（PDF/Excel）
- 预约取消率分析
- 用户满意度统计
- 实时数据监控仪表板

## 故障排除

### 图表不显示
1. 检查网络连接是否正常
2. 确认Chart.js CDN链接可访问
3. 查看浏览器控制台是否有JavaScript错误

### 数据不准确
1. 确认数据库中有足够的历史数据
2. 检查系统时间设置是否正确
3. 验证数据库查询语句的正确性 