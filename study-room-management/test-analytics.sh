#!/bin/bash

echo "等待应用启动..."
sleep 10

echo "测试首页访问..."
curl -s http://localhost:8081/ | head -3

echo -e "\n\n测试登录页面..."
curl -s http://localhost:8081/login | head -3

echo -e "\n\n测试管理员登录并访问数据分析页面..."
# 获取CSRF token
LOGIN_PAGE=$(curl -s -c cookies.txt http://localhost:8081/login)
CSRF_TOKEN=$(echo "$LOGIN_PAGE" | grep -o 'name="_csrf" value="[^"]*"' | cut -d'"' -f4)

echo "CSRF Token: $CSRF_TOKEN"

# 登录
curl -s -b cookies.txt -c cookies.txt \
  -d "username=admin&password=123456&_csrf=$CSRF_TOKEN" \
  -X POST http://localhost:8081/login

echo -e "\n\n访问数据分析页面..."
curl -s -b cookies.txt http://localhost:8081/admin/analytics | head -10

echo -e "\n\n测试API端点..."
curl -s -b cookies.txt http://localhost:8081/admin/analytics/reservation-trend

# 清理
rm -f cookies.txt

echo -e "\n\n测试完成！" 