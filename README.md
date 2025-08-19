# 久坐小管家

一款帮助用户提醒定时休息，预防久坐带来健康问题的桌面应用程序。

## 功能特点
- 自定义提醒间隔和休息时长
- 视觉和声音提醒
- 简洁美观的用户界面
- 支持多种提醒模式

## 环境要求
- **JDK 11 或更高版本**（必须包含JavaFX支持）
- **Windows操作系统**

## 如何运行应用程序

### 方法一：使用批处理脚本（推荐）
1. 确保已安装符合要求的JDK
2. 双击运行 `直接运行应用程序.bat` 批处理文件
3. 应用程序将启动并显示主界面

### 方法二：使用命令行
打开命令提示符，导航到项目目录，然后执行以下命令：
```cmd
java -jar target\sedentary-reminder-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 常见问题解决

### 1. 应用程序启动时出现 "Location is required" 错误
这通常是JavaFX资源加载问题。我们已经在代码中修复了这个问题，确保使用正确的ClassLoader方式加载FXML文件。

### 2. 出现 "Unsupported JavaFX configuration" 警告
这是Java模块系统与JavaFX的兼容性警告，通常不会影响程序功能。

### 3. 应用程序无法启动或崩溃
- 确认已安装正确版本的JDK（11或更高版本）
- 确认JDK包含JavaFX支持
- 尝试以管理员身份运行批处理脚本

## 开发说明
如果您想参与开发或修改此应用程序：
1. 使用Maven构建项目：`mvn clean package`
2. 项目使用JavaFX框架开发UI
3. 主要源代码位于 `src/main/java/com/demo/` 目录
4. 资源文件（FXML、CSS、图片、声音）位于 `src/main/resources/` 目录

## 注意事项
- 请勿修改JAR文件内容
- 如果您修改了源代码，请重新运行 `mvn clean package` 生成新的JAR文件
- 如需自定义提醒声音，请替换 `resources/sounds/default_sound.wav` 文件

## 联系我们
如有任何问题或建议，请随时联系开发团队。