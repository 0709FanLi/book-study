#!/bin/bash

echo "==================================="
echo "  自习室管理系统项目验证"
echo "==================================="

# 设置正确的JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_161.jdk/Contents/Home

echo "✅ 检查项目结构..."
if [ -f "pom.xml" ]; then
    echo "   ✓ Maven配置文件存在"
else
    echo "   ✗ Maven配置文件缺失"
fi

if [ -f "database-setup.sql" ]; then
    echo "   ✓ 数据库初始化脚本存在"
else
    echo "   ✗ 数据库初始化脚本缺失"
fi

echo "✅ 检查Java源代码..."
java_count=$(find . -name "*.java" | wc -l)
echo "   ✓ Java文件数量: $java_count"

echo "✅ 检查HTML模板..."
html_count=$(find . -name "*.html" | wc -l)
echo "   ✓ HTML文件数量: $html_count"

echo "✅ 检查编译环境..."
java -version
echo ""

echo "✅ 尝试编译项目..."
mvn clean compile

if [ $? -eq 0 ]; then
    echo ""
    echo "🎉 项目验证成功！"
    echo "📝 下一步："
    echo "   1. 启动MySQL服务"
    echo "   2. 执行数据库脚本: mysql -u root -p < database-setup.sql"
    echo "   3. 修改application.properties中的数据库密码"
    echo "   4. 运行项目: ./start-project.sh"
    echo "   5. 访问: http://localhost:8080"
else
    echo ""
    echo "❌ 编译失败，请检查环境配置"
fi 