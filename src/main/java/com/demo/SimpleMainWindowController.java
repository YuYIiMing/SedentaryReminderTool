package com.demo;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class SimpleMainWindowController implements Initializable {
    // UI组件 - 简化版只保留必要的组件
    @FXML private Slider intervalSlider;
    @FXML private Label intervalLabel;
    @FXML private RadioButton minuteRadioButton;
    @FXML private RadioButton hourRadioButton;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    
    // 简单的字段用于测试
    private ToggleGroup timeUnitGroup;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 初始化ToggleGroup
        timeUnitGroup = new ToggleGroup();
        minuteRadioButton.setToggleGroup(timeUnitGroup);
        hourRadioButton.setToggleGroup(timeUnitGroup);
        minuteRadioButton.setSelected(true);
        
        // 设置监听器
        setupListeners();
        
        // 初始化标签显示
        updateIntervalLabel();
    }
    
    // 设置监听器
    private void setupListeners() {
        // 滑块变化监听器
        intervalSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            updateIntervalLabel();
        });
        
        // 时间单位切换监听器
        timeUnitGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateIntervalLabel();
        });
        
        // 开始提醒按钮点击事件
        startButton.setOnAction(event -> {
            startReminder();
        });
        
        // 停止提醒按钮点击事件
        stopButton.setOnAction(event -> {
            stopReminder();
        });
    }
    
    // 更新间隔标签显示
    private void updateIntervalLabel() {
        int interval = (int) intervalSlider.getValue();
        if (minuteRadioButton.isSelected()) {
            intervalLabel.setText(interval + " 分钟");
        } else {
            intervalLabel.setText(interval + " 小时");
        }
    }
    
    // 开始提醒
    private void startReminder() {
        // 简单的实现，实际应用中可能需要调用提醒管理器
        startButton.setDisable(true);
        stopButton.setDisable(false);
        System.out.println("开始提醒");
    }
    
    // 停止提醒
    private void stopReminder() {
        // 简单的实现，实际应用中可能需要调用提醒管理器
        startButton.setDisable(false);
        stopButton.setDisable(true);
        System.out.println("停止提醒");
    }
}