# 久坐提醒工具 (Sedentary Reminder Tool)

## 项目功能介绍

久坐提醒工具是一个帮助用户避免长时间久坐的桌面应用程序。现代生活中，许多人因工作或学习需要长时间坐在电脑前，这对身体健康有诸多负面影响。本工具通过定时提醒用户起身活动，帮助用户养成健康的工作习惯。

主要功能包括：
- 自定义提醒间隔时间（默认为90分钟）
- 多种提醒方式（弹窗、声音、任务栏闪烁）
- 提醒内容自定义
- 休息活动建议
- 统计每日活动数据
- 开机自启动选项

## 技术方案

### 开发语言与框架
- **编程语言**：Java 11+
- **UI框架**：JavaFX
- **构建工具**：Maven

### 项目结构
```
SedentaryReminderTool/
├── .vscode/              # VS Code配置文件
├── installer/            # 安装程序相关文件
├── output/               # 输出文件
├── pom.xml               # Maven配置文件
├── src/
│   ├── main/
│   │   ├── java/         # 主源代码
│   │   └── resources/    # 资源文件
│   │       ├── css/      # 样式表
│   │       ├── fxml/     # UI布局文件
│   │       ├── images/   # 图片资源
│   │       └── sounds/   # 声音资源
│   └── test/             # 测试代码
├── start_sedentary_reminder.bat  # Windows启动脚本
└── target/               # Maven构建输出目录
```

### 核心技术点
1. **JavaFX UI设计**：使用FXML和CSS构建现代化用户界面
2. **定时任务调度**：使用Java的ScheduledExecutorService实现精确计时
3. **数据持久化**：使用JSON格式保存用户配置和统计数据
4. **多线程处理**：分离UI线程和后台任务，确保界面响应流畅
5. **系统集成**：实现任务栏通知、声音播放等系统级功能

## 使用说明

### 运行环境要求
- Java 11 或更高版本
- Maven 3.6 或更高版本（可选，用于构建）

### 运行方式

#### 方法1：使用批处理文件（Windows）
1. 双击 `start_sedentary_reminder.bat` 文件
2. 程序将在后台启动，终端会自动关闭

#### 方法2：使用Maven命令
1. 打开命令行终端
2. 导航到项目根目录
3. 执行以下命令：
   ```
   mvn exec:java@run-launcher
   ```

#### 方法3：直接运行JAR文件
1. 确保已构建项目（`mvn package`）
2. 导航到 `target` 目录
3. 执行以下命令：
   ```
   java -jar sedentary-reminder-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

### 配置说明
首次运行后，程序会在用户目录下创建配置文件 `sedentary_reminder_config.json`。您可以通过界面设置以下选项：
- 提醒间隔时间
- 提醒方式（弹窗、声音、闪烁）
- 提醒内容
- 开机自启动

### 注意事项
1. 确保您的系统已安装Java 11或更高版本
2. 如需修改默认配置，请通过应用程序界面进行更改，不要直接编辑配置文件
3. 如果程序无法启动，请检查Java和Maven是否正确安装并配置了环境变量

## 联系方式
如有任何问题或建议，请联系：
- Email: yym888@zohomail.com
- GitHub Issues: https://github.com/YuYIiMing/SedentaryReminderTool/issues
