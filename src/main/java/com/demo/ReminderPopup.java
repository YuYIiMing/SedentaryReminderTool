package com.demo;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ReminderPopup {
    // 显示短暂弹窗（全屏模式下使用）
    public static void showTemporaryPopup(String message, int durationSeconds) {
        Platform.runLater(() -> {
            // 创建一个无边框、无标题栏的窗口
            Stage popupStage = new Stage(StageStyle.TRANSPARENT);
            
            // 创建内容面板
            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-padding: 20px; -fx-background-radius: 10px;");
            
            // 创建标签显示消息
            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-wrap-text: true;");
            messageLabel.setMaxWidth(300);
            
            root.getChildren().add(messageLabel);
            
            // 创建场景
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT); // 场景透明
            
            popupStage.setScene(scene);
            popupStage.setAlwaysOnTop(true); // 窗口置顶
            
            // 计算屏幕中心位置
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            popupStage.setX((screenBounds.getWidth() - 300) / 2);
            popupStage.setY((screenBounds.getHeight() - 150) / 2);
            
            // 显示窗口
            popupStage.show();
            
            // 设置定时器，在指定时间后自动关闭窗口
            new Thread(() -> {
                try {
                    Thread.sleep(durationSeconds * 1000L);
                    Platform.runLater(() -> {
                        if (popupStage.isShowing()) {
                            popupStage.close();
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
    
    // 显示常规弹窗（非全屏模式下使用）
    public static void showNormalPopup(String message) {
        Platform.runLater(() -> {
            // 创建一个有边框的窗口
            Stage popupStage = new Stage();
            popupStage.setTitle("久坐提醒");
            
            // 创建内容面板
            StackPane root = new StackPane();
            root.setStyle("-fx-background-color: #ffffff; -fx-padding: 20px;");
            
            // 创建标签显示消息
            Label messageLabel = new Label(message);
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333; -fx-wrap-text: true;");
            messageLabel.setMaxWidth(300);
            messageLabel.setAlignment(javafx.geometry.Pos.CENTER);
            
            root.getChildren().add(messageLabel);
            
            // 创建场景
            Scene scene = new Scene(root, 400, 200);
            
            popupStage.setScene(scene);
            popupStage.setAlwaysOnTop(true); // 窗口置顶
            popupStage.setResizable(false);
            
            // 计算屏幕中心位置
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            popupStage.setX((screenBounds.getWidth() - 400) / 2);
            popupStage.setY((screenBounds.getHeight() - 200) / 2);
            
            // 显示窗口
            popupStage.show();
        });
    }
}