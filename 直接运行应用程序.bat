@echo off

REM 检查Java是否可用
java -version >nul 2>&1
if %errorlevel% neq 0 (
echo 错误: 未找到Java运行环境。请先安装JDK 11或更高版本。
pause
exit /b 1
)

REM 检查JAR文件是否存在
if not exist "target\sedentary-reminder-1.0-SNAPSHOT-jar-with-dependencies.jar" (
echo 错误: JAR文件不存在。请先使用Maven打包项目。
pause
exit /b 1
)

echo 开始运行久坐小管家应用程序...
java -jar "target\sedentary-reminder-1.0-SNAPSHOT-jar-with-dependencies.jar"

REM 如果运行失败，显示错误信息
if %errorlevel% neq 0 (
echo 应用程序运行失败。
echo 请检查是否已正确安装JavaFX兼容的JDK版本。
pause
exit /b 1
)

REM 运行成功后退出
echo 应用程序已退出。
pause