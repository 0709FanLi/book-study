#!/bin/bash

echo "测试管理员登录和数据分析功能..."

# 清理之前的cookies
rm -f cookies.txt

echo "1. 获取登录页面..."
curl -s -c cookies.txt http://localhost:8082/login > login_page.html

echo "2. 尝试管理员登录..."
# 使用正确的密码登录
LOGIN_RESPONSE=$(curl -s -b cookies.txt -c cookies.txt -d "username=admin&password=123456" -X POST http://localhost:8082/login -w "%{http_code}")
echo "登录响应状态码: $LOGIN_RESPONSE"

echo "3. 测试访问管理后台首页..."
curl -s -b cookies.txt http://localhost:8082/admin/dashboard | head -5

echo -e "\n4. 测试访问数据分析页面..."
ANALYTICS_RESPONSE=$(curl -s -b cookies.txt http://localhost:8082/admin/analytics -w "%{http_code}")
echo "数据分析页面响应状态码: ${ANALYTICS_RESPONSE: -3}"

if [[ "${ANALYTICS_RESPONSE: -3}" == "200" ]]; then
    echo "✅ 数据分析页面访问成功！"
    echo "页面内容预览:"
    echo "$ANALYTICS_RESPONSE" | head -10
else
    echo "❌ 数据分析页面访问失败"
    echo "响应内容:"
    echo "$ANALYTICS_RESPONSE"
fi

echo -e "\n5. 测试API端点..."
echo "预约趋势API:"
curl -s -b cookies.txt http://localhost:8082/admin/analytics/reservation-trend | head -3

echo -e "\n座位使用率API:"
curl -s -b cookies.txt http://localhost:8082/admin/analytics/seat-usage | head -3

echo -e "\n用户活跃度API:"
curl -s -b cookies.txt http://localhost:8082/admin/analytics/user-activity | head -3

echo -e "\n时间段使用API:"
curl -s -b cookies.txt http://localhost:8082/admin/analytics/hourly-usage | head -3

# 清理
rm -f cookies.txt login_page.html

echo -e "\n测试完成！" 