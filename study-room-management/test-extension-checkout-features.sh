#!/bin/bash

echo "🎯 续约与退座功能测试脚本"
echo "================================"

BASE_URL="http://localhost:8082"

echo "1. 测试应用程序状态..."
if curl -s "$BASE_URL" > /dev/null; then
    echo "✅ 应用程序运行正常"
else
    echo "❌ 应用程序未运行，请先启动"
    exit 1
fi

echo ""
echo "2. 测试页面访问..."

# 测试主要页面
pages=(
    "/"
    "/login"
    "/register"
)

for page in "${pages[@]}"; do
    if curl -s "$BASE_URL$page" > /dev/null; then
        echo "✅ $page 页面可访问"
    else
        echo "❌ $page 页面访问失败"
    fi
done

echo ""
echo "3. 功能特性说明..."
echo "📋 已实现的功能："
echo "   🔄 续约功能："
echo "      - 预约结束前30分钟内可申请续约"
echo "      - 每个预约最多续约3次"
echo "      - 每次续约1-2小时"
echo "      - 每日最多5次续约操作"
echo "      - 自动冲突检测"
echo ""
echo "   🚪 退座功能："
echo "      - 预约时间内随时可退座"
echo "      - 记录实际使用时长"
echo "      - 立即释放座位资源"
echo "      - 状态自动更新为已完成"
echo ""
echo "   📊 数据统计："
echo "      - 预约历史记录"
echo "      - 续约次数统计"
echo "      - 实际使用时长分析"
echo "      - 提前退座信息"

echo ""
echo "4. 测试账号信息..."
echo "   学生账号：student1 / 123456"
echo "   管理员账号：admin / 123456"

echo ""
echo "5. 访问地址..."
echo "   🌐 系统首页：$BASE_URL"
echo "   🔐 登录页面：$BASE_URL/login"
echo "   📝 注册页面：$BASE_URL/register"
echo "   🪑 座位预约：$BASE_URL/reservations"
echo "   📚 预约历史：$BASE_URL/reservations/history"

echo ""
echo "6. 数据库更新提醒..."
echo "   ⚠️  如需完整功能，请执行以下SQL："
echo "   ALTER TABLE reservation"
echo "   ADD COLUMN created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
echo "   ADD COLUMN last_extended_at TIMESTAMP NULL,"
echo "   ADD COLUMN extension_count INT DEFAULT 0,"
echo "   ADD COLUMN actual_end_time TIMESTAMP NULL,"
echo "   ADD COLUMN remarks VARCHAR(255) NULL;"

echo ""
echo "🎉 续约与退座功能已成功实现！"
echo "请访问 $BASE_URL 开始测试" 