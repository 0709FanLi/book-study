#!/bin/bash

echo "测试数据分析功能..."

# 测试基本连接
echo "1. 测试应用基本连接..."
curl -s http://localhost:8082/ | head -3

echo -e "\n\n2. 测试登录页面..."
curl -s http://localhost:8082/login | head -3

echo -e "\n\n3. 测试管理员登录..."
# 登录
curl -s -c cookies.txt -d "username=admin&password=123456" -X POST http://localhost:8082/login > /dev/null

echo -e "\n\n4. 测试访问管理后台..."
curl -s -b cookies.txt http://localhost:8082/admin/dashboard | head -5

echo -e "\n\n5. 测试数据分析页面..."
curl -s -b cookies.txt http://localhost:8082/admin/analytics | head -10

echo -e "\n\n6. 测试API端点..."
echo "预约趋势API:"
curl -s -b cookies.txt http://localhost:8082/admin/analytics/reservation-trend

echo -e "\n\n座位使用率API:"
curl -s -b cookies.txt http://localhost:8082/admin/analytics/seat-usage

echo -e "\n\n用户活跃度API:"
curl -s -b cookies.txt http://localhost:8082/admin/analytics/user-activity

echo -e "\n\n时间段使用API:"
curl -s -b cookies.txt http://localhost:8082/admin/analytics/hourly-usage

# 清理
rm -f cookies.txt

echo -e "\n\n测试完成！" 