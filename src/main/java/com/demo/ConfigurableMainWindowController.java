package com.demo;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class ConfigurableMainWindowController implements Initializable {
    // 基础设置UI组件
    @FXML private Slider intervalSlider;
    @FXML private Label intervalLabel;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Button testReminderButton;
    @FXML private Label countdownLabel; // 倒计时标签
    @FXML private Button aboutButton; // 关于软件按钮
    
    // 提醒记录UI组件
    @FXML private TableView<ReminderRecord> reminderRecordsTable;
    @FXML private TableColumn<ReminderRecord, String> startTimeColumn;
    @FXML private TableColumn<ReminderRecord, String> endTimeColumn;
    @FXML private TableColumn<ReminderRecord, String> durationColumn;
    @FXML private Button clearRecordsButton; // 清空记录按钮
    
    // 高级设置UI组件
    @FXML private TextArea reminderTextArea;
    @FXML private TextField soundFilePath;
    @FXML private ChoiceBox<String> popupColorChoiceBox;
    @FXML private Button browseSoundButton;
    @FXML private Button restoreDefaultSoundButton;
    @FXML private CheckBox popupCheckBox;
    @FXML private CheckBox soundCheckBox;
    @FXML private CheckBox flashCheckBox;
    @FXML private Slider popupDurationSlider;
    @FXML private Label popupDurationLabel;
    @FXML private Slider flashCountSlider;
    @FXML private Label flashCountLabel;
    @FXML private Button saveConfigButton;
    
    // 配置相关
    private Configuration config;
    private boolean isReminderRunning = false;
    private Timer timer; // 用于倒计时
    private long startTimeMillis; // 提醒开始时间
    private long nextReminderMillis; // 下次提醒时间
    private LocalDateTime currentReminderStartTime; // 当前提醒的开始时间
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化配置
        config = Configuration.getInstance();
        config.loadConfiguration(); // 确保加载配置
        
        // 从配置加载设置
        loadConfigToUI();
        
        // 设置监听器
        setupListeners();
        
        // 初始化标签显示
        updateIntervalLabel();
        updatePopupDurationLabel();
        updateFlashCountLabel();
        if (countdownLabel != null) {
            countdownLabel.setText("距离下次提醒还有：未开始");
            // 优化倒计时UI样式
            countdownLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        }
        
        // 初始化提醒记录表
        initializeReminderRecordsTable();
        
        // 初始状态：停止按钮禁用
        stopButton.setDisable(true);
    }
    
    // 初始化提醒记录表
    private void initializeReminderRecordsTable() {
        if (reminderRecordsTable == null) {
            return; // FXML中未定义该组件
        }
        
        // 设置表格列，先检查列是否为null
        if (startTimeColumn != null) {
            startTimeColumn.setCellValueFactory(new PropertyValueFactory<>("startTimeDisplay"));
        }
        if (endTimeColumn != null) {
            endTimeColumn.setCellValueFactory(new PropertyValueFactory<>("endTimeDisplay"));
        }
        if (durationColumn != null) {
            durationColumn.setCellValueFactory(new PropertyValueFactory<>("durationDisplay"));
        }
        
        // 设置表格数据
        refreshReminderRecordsTable();
    }
    
    // 刷新提醒记录表
    private void refreshReminderRecordsTable() {
        if (reminderRecordsTable == null) {
            return;
        }
        
        List<ReminderRecord> records = config.getReminderRecords();
        ObservableList<ReminderRecord> data = FXCollections.observableArrayList(records);
        reminderRecordsTable.setItems(data);
    }
    
    // 从配置加载设置到UI
    private void loadConfigToUI() {
        intervalSlider.setValue(config.getReminderInterval());
        reminderTextArea.setText(config.getReminderText() != null ? config.getReminderText() : "");
        soundFilePath.setText(config.getSoundFile() != null ? config.getSoundFile() : "");
        popupCheckBox.setSelected(config.isPopupEnabled());
        soundCheckBox.setSelected(config.isSoundEnabled());
        flashCheckBox.setSelected(config.isFlashEnabled());
        popupDurationSlider.setValue(config.getPopupDuration());
        flashCountSlider.setValue(config.getFlashCount());
        
        // 加载弹窗颜色配置
        if (popupColorChoiceBox != null) {
            String popupColor = config.getPopupColor();
            if (popupColor != null && !popupColor.isEmpty()) {
                popupColorChoiceBox.setValue(popupColor);
            } else {
                popupColorChoiceBox.setValue("蓝色"); // 默认选择蓝色
            }
        }
        
        // 设置滑块范围为1-300分钟
        intervalSlider.setMin(1);
        intervalSlider.setMax(300);
        intervalSlider.setValue(Math.min(Math.max(intervalSlider.getValue(), 1), 300));
    }
    
    // 设置监听器
    private void setupListeners() {
        // 滑块变化监听器
        intervalSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIntervalLabel();
        });
        
        // 弹窗持续时间滑块
        popupDurationSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updatePopupDurationLabel();
        });
        
        // 闪烁次数滑块
        flashCountSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateFlashCountLabel();
        });
        
        // 开始提醒按钮点击事件
        startButton.setOnAction(event -> {
            startReminder();
        });
        
        // 停止提醒按钮点击事件
        stopButton.setOnAction(event -> {
            stopReminder();
        });
        
        // 测试提醒按钮点击事件
        testReminderButton.setOnAction(event -> {
            testReminder();
        });
        
        // 浏览声音文件按钮点击事件
        browseSoundButton.setOnAction(event -> {
            browseSoundFile();
        });
        
        // 恢复默认提示音按钮点击事件
        if (restoreDefaultSoundButton != null) {
            restoreDefaultSoundButton.setOnAction(event -> {
                restoreDefaultSound();
            });
        }
        
        // 保存配置按钮点击事件
        saveConfigButton.setOnAction(event -> {
            saveConfiguration();
        });
        
        // 关于软件按钮点击事件
        if (aboutButton != null) {
            aboutButton.setOnAction(event -> {
                showAboutDialog();
            });
        }
        
        // 清空记录按钮点击事件
        if (clearRecordsButton != null) {
            clearRecordsButton.setOnAction(event -> {
                clearReminderRecords();
            });
        }
    }
    
    // 清空提醒记录
    private void clearReminderRecords() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("确认清空");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("确定要清空所有提醒记录吗？");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                config.clearReminderRecords();
                refreshReminderRecordsTable();
                
                Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                infoAlert.setTitle("操作成功");
                infoAlert.setHeaderText(null);
                infoAlert.setContentText("提醒记录已清空！");
                infoAlert.showAndWait();
            }
        });
    }
    
    // 更新间隔标签
    private void updateIntervalLabel() {
        int interval = (int) intervalSlider.getValue();
        intervalLabel.setText(String.valueOf(interval) + " 分钟");
    }
    
    // 更新弹窗持续时间标签
    private void updatePopupDurationLabel() {
        int duration = (int) popupDurationSlider.getValue();
        popupDurationLabel.setText(String.valueOf(duration));
    }
    
    // 更新闪烁次数标签
    private void updateFlashCountLabel() {
        int count = (int) flashCountSlider.getValue();
        flashCountLabel.setText(String.valueOf(count));
    }
    
    // 根据颜色名称获取背景色CSS值
    private String getBackgroundColorByColorName(String colorName) {
        // 添加null检查，如果colorName为null，则返回默认的蓝色
        if (colorName == null) {
            return "#3498db";
        }
        
        switch (colorName) {
            case "绿色":
                return "#2ecc71";
            case "红色":
                return "#e74c3c";
            case "黄色":
                return "#f1c40f";
            case "蓝色":
            default:
                return "#3498db";
        }
    }
    
    // 浏览声音文件
    private void browseSoundFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择提示音文件");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("音频文件", "*.wav", "*.mp3", "*.ogg"),
                new FileChooser.ExtensionFilter("所有文件", "*.*")
        );
        
        Stage stage = (Stage) browseSoundButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            soundFilePath.setText(selectedFile.getAbsolutePath());
        }
    }
    
    // 恢复默认提示音
    private void restoreDefaultSound() {
        soundFilePath.setText(Configuration.DEFAULT_SOUND_FILE);
        
        // 显示操作成功提示
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("操作成功");
        alert.setHeaderText(null);
        alert.setContentText("已恢复默认提示音！");
        alert.showAndWait();
    }
    
    // 保存配置
    private void saveConfiguration() {
        // 获取UI中的设置值
        int interval = (int) intervalSlider.getValue();
        String reminderText = reminderTextArea.getText();
        String soundFile = soundFilePath.getText();
        boolean popupEnabled = popupCheckBox.isSelected();
        boolean soundEnabled = soundCheckBox.isSelected();
        boolean flashEnabled = flashCheckBox.isSelected();
        int popupDuration = (int) popupDurationSlider.getValue();
        int flashCount = (int) flashCountSlider.getValue();
        
        // 更新配置
        config.setReminderInterval(interval);
        config.setReminderText(reminderText);
        config.setSoundFile(soundFile);
        config.setPopupEnabled(popupEnabled);
        config.setSoundEnabled(soundEnabled);
        config.setFlashEnabled(flashEnabled);
        config.setPopupDuration(popupDuration);
        config.setFlashCount(flashCount);
        
        // 保存弹窗颜色配置
        if (popupColorChoiceBox != null) {
            config.setPopupColor(popupColorChoiceBox.getValue());
        }
        
        // 保存提醒记录
        config.saveReminderRecords();
        
        // 保存到配置文件
        config.saveConfiguration();
        
        // 显示保存成功提示
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("配置保存成功");
        alert.setHeaderText(null);
        alert.setContentText("您的配置已成功保存！");
        alert.showAndWait();
        
        // 如果提醒正在运行，可以考虑重启提醒以应用新配置
        if (isReminderRunning) {
            restartReminder();
        }
    }
    
    // 开始提醒
    private void startReminder() {
        // 从UI获取当前设置并应用
        int interval = (int) intervalSlider.getValue();
        String reminderText = reminderTextArea.getText();
        boolean popupEnabled = popupCheckBox.isSelected();
        boolean soundEnabled = soundCheckBox.isSelected();
        boolean flashEnabled = flashCheckBox.isSelected();
        
        // 更新配置
        config.setReminderInterval(interval);
        config.setReminderText(reminderText);
        config.setPopupEnabled(popupEnabled);
        config.setSoundEnabled(soundEnabled);
        config.setFlashEnabled(flashEnabled);
        
        // 记录当前提醒的开始时间
        startTimeMillis = System.currentTimeMillis();
        nextReminderMillis = startTimeMillis + interval * 60 * 1000;
        currentReminderStartTime = LocalDateTime.now();
        
        // 启动倒计时
        startCountdown();
        
        isReminderRunning = true;
        startButton.setDisable(true);
        stopButton.setDisable(false);
        System.out.println("开始提醒，间隔：" + interval + "分钟");
    }
    
    // 启动倒计时
    private void startCountdown() {
        if (timer != null) {
            timer.cancel();
        }
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    updateCountdown();
                });
            }
        }, 0, 1000); // 每秒更新一次
    }
    
    // 更新倒计时显示
    private void updateCountdown() {
        if (countdownLabel == null || !isReminderRunning) {
            if (countdownLabel != null) {
                countdownLabel.setText("距离下次提醒还有：未开始");
            }
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        long remainingTime = nextReminderMillis - currentTime;
        
        if (remainingTime <= 0) {
            // 时间到，触发提醒
            triggerReminder();
            
            // 提醒后自动停止，不再自动开始下一轮
            stopReminder();
        } else {
            // 计算剩余时间
            long minutes = remainingTime / (60 * 1000);
            long seconds = (remainingTime % (60 * 1000)) / 1000;
            
            countdownLabel.setText(String.format("距离下次提醒还有：%d分%d秒", minutes, seconds));
        }
    }
    
    // 触发提醒
    private void triggerReminder() {
        String reminderText = config.getReminderText();
        boolean popupEnabled = config.isPopupEnabled();
        boolean soundEnabled = config.isSoundEnabled();
        boolean flashEnabled = config.isFlashEnabled();
        
        System.out.println("提醒触发：" + reminderText);
        
        // 显示弹窗
        if (popupEnabled) {
            int duration = config.getPopupDuration();
            showTestReminderPopup(reminderText, duration);
        }
        
        // 播放声音
        if (soundEnabled) {
            String soundFile = config.getSoundFile();
            SoundPlayer.playSound(soundFile, false);
        }
        
        // 屏幕闪烁（实际应用中需要实现）
        if (flashEnabled) {
            // 闪烁功能实现
            System.out.println("屏幕闪烁提醒已触发");
        }
    }
    
    // 停止提醒
    private void stopReminder() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        
        isReminderRunning = false;
        startButton.setDisable(false);
        stopButton.setDisable(true);
        if (countdownLabel != null) {
            countdownLabel.setText("距离下次提醒还有：未开始");
        }
        
        // 记录本次提醒结束时间
        if (currentReminderStartTime != null) {
            LocalDateTime endTime = LocalDateTime.now();
            ReminderRecord record = new ReminderRecord(currentReminderStartTime, endTime);
            config.addReminderRecord(record);
            currentReminderStartTime = null;
            
            // 添加记录后刷新表格显示
            refreshReminderRecordsTable();
        }
        
        System.out.println("停止提醒");
    }
    
    // 测试提醒功能
    private void testReminder() {
        // 获取UI中的设置值
        String reminderText = reminderTextArea.getText();
        boolean popupEnabled = popupCheckBox.isSelected();
        boolean soundEnabled = soundCheckBox.isSelected();
        boolean flashEnabled = flashCheckBox.isSelected();
        int popupDuration = (int) popupDurationSlider.getValue();
        String soundFile = soundFilePath.getText();
        
        // 在实际应用中，这里应该调用提醒管理器来显示测试提醒
        System.out.println("测试提醒功能");
        System.out.println("提醒文案：" + reminderText);
        System.out.println("启用弹窗：" + popupEnabled);
        System.out.println("启用声音：" + soundEnabled);
        System.out.println("启用闪烁：" + flashEnabled);
        
        // 如果启用了弹窗，显示一个测试弹窗
        if (popupEnabled) {
            showTestReminderPopup(reminderText, popupDuration);
        }
        
        // 如果启用了声音，播放测试声音
        if (soundEnabled) {
            System.out.println("尝试播放测试声音");
            SoundPlayer.playSound(soundFile, false);
        }
        
        // 显示测试成功提示
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("测试提醒");
        alert.setHeaderText(null);
        alert.setContentText("提醒功能测试完成！\n已根据当前设置进行了测试。");
        alert.showAndWait();
    }
    
    // 显示软件介绍对话框
    private void showAboutDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("关于久坐小管家");
        alert.setHeaderText("久坐小管家 v1.0");
        
        String content = "健康久坐，定期活动！\n\n" +
                "久坐健康知识：\n" +
                "- 长时间久坐会增加心血管疾病、糖尿病等风险\n" +
                "- 每30-60分钟起身活动5-10分钟为宜\n" +
                "- 简单的伸展运动可以有效缓解久坐带来的不适\n\n" +
                "推荐配置：\n" +
                "- 提醒间隔：30-60分钟\n" +
                "- 弹窗持续时间：4秒\n" +
                "- 同时启用声音和闪烁提示\n\n" +
                "- 本软件为免费软件，不包含任何广告\n" +
                "如有问题，请联系： yym888@zohomail.com";
        
        alert.setContentText(content);
        alert.getDialogPane().setPrefWidth(400);
        alert.getDialogPane().setPrefHeight(350);
        alert.showAndWait();
    }
    
    // 显示测试提醒弹窗
    private void showTestReminderPopup(String reminderText, int durationSeconds) {
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.UNDECORATED);
        popupStage.initOwner(((Stage) startButton.getScene().getWindow()));
        
        // 创建弹窗内容
        VBox content = new VBox(10);
        
        // 获取配置的弹窗颜色
        String popupColor = config.getPopupColor();
        String backgroundColor = getBackgroundColorByColorName(popupColor);
        
        content.setStyle("-fx-background-color: " + backgroundColor + "; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        content.setPrefWidth(300);
        
        // 添加图标
        ImageView icon = new ImageView();
        icon.setFitWidth(100);
        icon.setFitHeight(80);
        icon.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 0);");
        // 尝试加载图标，如果没有则使用默认样式
        try {
            InputStream iconStream = getClass().getResourceAsStream("/images/reminder_icon.png");
            if (iconStream != null) {
                icon.setImage(new Image(iconStream));
            }
        } catch (Exception e) {
            System.out.println("无法加载图标");
        }
        
        // 添加文本
        Label textLabel = new Label(reminderText);
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-wrap-text: true;");
        textLabel.setMaxWidth(Double.MAX_VALUE);
        
        // 添加到内容容器
        content.getChildren().addAll(icon, textLabel);
        
        // 创建场景并显示
        popupStage.setScene(new javafx.scene.Scene(content));
        popupStage.show();
        
        // 设置自动关闭
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(popupStage::close);
            }
        }, durationSeconds * 1000);
    }
    
    // 重启提醒（应用新配置）
    private void restartReminder() {
        stopReminder();
        startReminder();
    }
}