#!/bin/bash

echo "==================================="
echo "  自习室管理系统启动脚本"
echo "==================================="

# 设置正确的JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_161.jdk/Contents/Home
echo "✅ JAVA_HOME 设置为: $JAVA_HOME"

# 检查Java版本
echo "✅ Java版本检查:"
java -version

# 检查Maven版本
echo "✅ Maven版本检查:"
mvn -version

echo ""
echo "==================================="
echo "  开始启动项目..."
echo "==================================="

# 启动项目
echo "🚀 正在启动Spring Boot应用..."
echo "📝 提示: 请确保MySQL已启动并执行了database-setup.sql脚本"
echo "🌐 启动成功后访问: http://localhost:8080"
echo ""

mvn spring-boot:run 