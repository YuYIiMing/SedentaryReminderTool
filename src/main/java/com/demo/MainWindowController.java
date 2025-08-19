package com.demo;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class MainWindowController implements Initializable {
    // UI组件
    @FXML private Slider reminderIntervalSlider;
    @FXML private Label intervalLabel;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Button testButton;
    @FXML private Label statusLabel;
    @FXML private ToggleGroup timeUnitGroup;
    @FXML private RadioButton minutesRadio;
    @FXML private RadioButton hoursRadio;
    
    // 配置和状态
    private Configuration config;
    private boolean isReminderRunning = false;
    private Timer timer;
    private long startTimeMillis;
    private long nextReminderMillis;
    private LocalDateTime currentReminderStartTime;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化配置
        config = Configuration.getInstance();
        config.loadConfiguration();
        
        // 加载配置到UI
        loadConfigToUI();
        
        // 设置监听器
        setupListeners();
        
        // 初始化UI状态
        updateIntervalLabel();
        statusLabel.setText("未开始");
        stopButton.setDisable(true);
        minutesRadio.setSelected(true);
    }
    
    // 从配置加载设置到UI
    private void loadConfigToUI() {
        reminderIntervalSlider.setValue(config.getReminderInterval());
        // 默认使用分钟
        minutesRadio.setSelected(true);
    }
    
    // 设置监听器
    private void setupListeners() {
        // 滑块变化监听器
        reminderIntervalSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIntervalLabel();
        });
        
        // 时间单位选择变化
        timeUnitGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateIntervalLabel();
        });
        
        // 开始按钮点击事件
        startButton.setOnAction(event -> {
            startReminder();
        });
        
        // 停止按钮点击事件
        stopButton.setOnAction(event -> {
            stopReminder();
        });
        
        // 测试按钮点击事件
        testButton.setOnAction(event -> {
            testReminder();
        });
    }
    
    // 更新间隔标签
    private void updateIntervalLabel() {
        int value = (int) reminderIntervalSlider.getValue();
        if (minutesRadio.isSelected()) {
            intervalLabel.setText(String.valueOf(value) + " 分钟");
        } else {
            intervalLabel.setText(String.valueOf(value) + " 小时");
        }
    }
    
    // 获取实际的提醒间隔（分钟）
    private int getActualReminderInterval() {
        int value = (int) reminderIntervalSlider.getValue();
        if (hoursRadio.isSelected()) {
            return value * 60; // 转换为分钟
        }
        return value;
    }
    
    // 开始提醒
    private void startReminder() {
        int interval = getActualReminderInterval();
        
        // 更新配置
        config.setReminderInterval(interval);
        
        // 记录开始时间
        startTimeMillis = System.currentTimeMillis();
        nextReminderMillis = startTimeMillis + interval * 60 * 1000;
        currentReminderStartTime = LocalDateTime.now();
        
        // 启动倒计时
        startCountdown();
        
        isReminderRunning = true;
        startButton.setDisable(true);
        stopButton.setDisable(false);
        statusLabel.setText("提醒中...");
        
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
        if (!isReminderRunning) {
            return;
        }
        
        long currentTime = System.currentTimeMillis();
        long remainingTime = nextReminderMillis - currentTime;
        
        if (remainingTime <= 0) {
            // 时间到，触发提醒
            triggerReminder();
        } else {
            // 计算剩余时间
            long minutes = remainingTime / (60 * 1000);
            long seconds = (remainingTime % (60 * 1000)) / 1000;
            
            statusLabel.setText(String.format("下次提醒：%d分%d秒", minutes, seconds));
        }
    }
    
    // 触发提醒
    private void triggerReminder() {
        // 使用配置的提醒设置
        String reminderText = config.getReminderText();
        boolean popupEnabled = config.isPopupEnabled();
        boolean soundEnabled = config.isSoundEnabled();
        boolean flashEnabled = config.isFlashEnabled();
        
        System.out.println("提醒触发：" + reminderText);
        
        // 显示弹窗
        if (popupEnabled) {
            int duration = config.getPopupDuration();
            showReminderPopup(reminderText, duration);
        }
        
        // 播放声音
        if (soundEnabled) {
            String soundFile = config.getSoundFile();
            SoundPlayer.playSound(soundFile, false);
        }
        
        // 屏幕闪烁
        if (flashEnabled) {
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
        statusLabel.setText("已停止");
        
        // 记录本次提醒结束时间
        if (currentReminderStartTime != null) {
            LocalDateTime endTime = LocalDateTime.now();
            ReminderRecord record = new ReminderRecord(currentReminderStartTime, endTime);
            config.addReminderRecord(record);
            currentReminderStartTime = null;
        }
        
        System.out.println("停止提醒");
    }
    
    // 测试提醒
    private void testReminder() {
        // 使用配置的提醒设置
        String reminderText = config.getReminderText();
        boolean popupEnabled = config.isPopupEnabled();
        boolean soundEnabled = config.isSoundEnabled();
        boolean flashEnabled = config.isFlashEnabled();
        int popupDuration = config.getPopupDuration();
        String soundFile = config.getSoundFile();
        
        System.out.println("测试提醒功能");
        System.out.println("提醒文案：" + reminderText);
        System.out.println("启用弹窗：" + popupEnabled);
        System.out.println("启用声音：" + soundEnabled);
        System.out.println("启用闪烁：" + flashEnabled);
        
        // 如果启用了弹窗，显示一个测试弹窗
        if (popupEnabled) {
            showReminderPopup(reminderText, popupDuration);
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
    
    // 显示提醒弹窗
    private void showReminderPopup(String reminderText, int durationSeconds) {
        Stage popupStage = new Stage();
        popupStage.initStyle(StageStyle.UNDECORATED);
        popupStage.initOwner(((Stage) startButton.getScene().getWindow()));
        
        // 创建弹窗内容
        VBox content = new VBox(10);
        content.setStyle("-fx-background-color: #3498db; -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        content.setPrefWidth(300);
        
        // 添加文本
        Label textLabel = new Label(reminderText);
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-wrap-text: true;");
        textLabel.setMaxWidth(Double.MAX_VALUE);
        
        // 添加到内容容器
        content.getChildren().add(textLabel);
        
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
}