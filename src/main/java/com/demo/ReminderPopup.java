package com.demo;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class ReminderPopup {
    // 显示短暂弹窗（全屏模式下使用）
    public static void showTemporaryPopup(String message, int durationSeconds) {
        Platform.runLater(() -> {
            createAndShowPopup(message, durationSeconds, true);
        });
    }
    
    // 显示常规弹窗（非全屏模式下使用）
    public static void showNormalPopup(String message) {
        Platform.runLater(() -> {
            createAndShowPopup(message, 0, false);
        });
    }
    
    // 统一的弹窗创建方法
    private static void createAndShowPopup(String message, int durationSeconds, boolean isTemporary) {
        // 创建窗口
        Stage popupStage = isTemporary ? new Stage(StageStyle.TRANSPARENT) : new Stage();
        if (!isTemporary) {
            popupStage.setTitle("久坐提醒");
        }
        
        // 主容器
        VBox container = new VBox(16);
        container.setPadding(new Insets(24));
        container.setAlignment(Pos.CENTER);
        container.setStyle("-fx-background-color: -background-white; " +
                          "-fx-background-radius: 16px; " +
                          "-fx-border-radius: 16px; " +
                          "-fx-border-color: -border-color; " +
                          "-fx-border-width: 1; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 20, 0, 0, 5);");
        
        // 设置动画
        container.setOpacity(0);
        container.setTranslateY(20);
        
        // 图标
        ImageView iconView = new ImageView();
        iconView.setFitWidth(48);
        iconView.setFitHeight(48);
        // 使用SVG图标
        iconView.setImage(new javafx.scene.image.Image("/images/reminder-icon.svg"));
        
        // 标题
        Label titleLabel = new Label("久坐提醒");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));
        titleLabel.setStyle("-fx-text-fill: -main-color;");
        
        // 消息内容
        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: -text-primary; -fx-wrap-text: true;");
        messageLabel.setMaxWidth(320);
        messageLabel.setAlignment(Pos.CENTER);
        messageLabel.setWrapText(true);
        
        // 关闭按钮
        Button closeButton = new Button("关闭");
        closeButton.setStyle("-fx-background-color: -main-color; " +
                            "-fx-text-fill: white; " +
                            "-fx-font-size: 14px; " +
                            "-fx-padding: 10 24; " +
                            "-fx-background-radius: 8px; " +
                            "-fx-cursor: hand; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 1);");
        
        // 按钮悬停效果
        closeButton.setOnMouseEntered(e -> closeButton.setStyle(
            closeButton.getStyle() + "-fx-background-color: derive(-main-color, 20%); " +
            "-fx-scale-x: 1.03; -fx-scale-y: 1.03;"));
        
        closeButton.setOnMouseExited(e -> closeButton.setStyle(
            closeButton.getStyle().replace("-fx-background-color: derive(-main-color, 20%); " +
            "-fx-scale-x: 1.03; -fx-scale-y: 1.03;", "")));
        
        closeButton.setOnAction(e -> popupStage.close());
        
        // 按钮容器
        HBox buttonBox = new HBox(12);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeButton);
        
        // 添加所有组件到容器
        container.getChildren().addAll(iconView, titleLabel, messageLabel, buttonBox);
        
        // 创建场景
        Scene scene = new Scene(container);
        if (isTemporary) {
            scene.setFill(Color.TRANSPARENT);
        }
        
        // 加载CSS样式
        scene.getStylesheets().add("/css/application.css");
        
        popupStage.setScene(scene);
        popupStage.setAlwaysOnTop(true);
        popupStage.setResizable(false);
        
        // 计算屏幕中心位置
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        double width = isTemporary ? 380 : 420;
        double height = isTemporary ? 280 : 300;
        popupStage.setWidth(width);
        popupStage.setHeight(height);
        popupStage.setX((screenBounds.getWidth() - width) / 2);
        popupStage.setY((screenBounds.getHeight() - height) / 2);
        
        // 显示窗口并添加淡入动画
        popupStage.show();
        
        // 添加淡入动画
        container.setOpacity(0);
        container.setTranslateY(20);
        
        // 动画效果
        container.setOpacity(0);
        new Thread(() -> {
            try {
                Thread.sleep(100); // 小延迟确保窗口已显示
                Platform.runLater(() -> {
                    // 添加平滑的淡入动画
                    for (int i = 0; i <= 10; i++) {
                        final int step = i;
                        Platform.runLater(() -> {
                            container.setOpacity(step * 0.1);
                            container.setTranslateY(20 - (step * 2));
                        });
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
        
        // 如果是临时弹窗，设置自动关闭
        if (durationSeconds > 0) {
            new Thread(() -> {
                try {
                    Thread.sleep(durationSeconds * 1000L);
                    Platform.runLater(() -> {
                        if (popupStage.isShowing()) {
                            // 添加淡出动画
                            for (int i = 10; i >= 0; i--) {
                                final int step = i;
                                Platform.runLater(() -> {
                                    container.setOpacity(step * 0.1);
                                    container.setTranslateY(20 - (step * 2));
                                });
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                            popupStage.close();
                        }
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}