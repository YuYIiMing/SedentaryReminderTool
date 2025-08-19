package com.demo;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

public class ScreenFlasher {
    // 闪烁边缘的宽度（像素）
    private static final int FLASH_EDGE_WIDTH = 10;
    // 闪烁的速度（毫秒）
    private static final int FLASH_INTERVAL = 200;
    
    // 闪烁屏幕边缘
    public static void flashEdges(List<String> edges, int flashCount) {
        Platform.runLater(() -> {
            // 获取屏幕信息
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            
            // 为每个边缘创建一个闪烁的窗口
            for (String edge : edges) {
                flashEdge(screenBounds, edge, flashCount);
            }
        });
    }
    
    // 闪烁单个边缘
    private static void flashEdge(javafx.geometry.Rectangle2D screenBounds, String edge, int flashCount) {
        // 创建一个无边框、无标题栏的窗口
        Stage flashStage = new Stage(StageStyle.TRANSPARENT);
        
        // 创建内容面板
        StackPane root = new StackPane();
        root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8);");
        
        // 创建场景
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT); // 场景透明
        
        flashStage.setScene(scene);
        flashStage.setAlwaysOnTop(true); // 窗口置顶
        
        // 根据边缘类型设置窗口大小和位置
        switch (edge.toLowerCase()) {
            case "top":
                flashStage.setX(0);
                flashStage.setY(0);
                flashStage.setWidth(screenBounds.getWidth());
                flashStage.setHeight(FLASH_EDGE_WIDTH);
                break;
            case "bottom":
                flashStage.setX(0);
                flashStage.setY(screenBounds.getHeight() - FLASH_EDGE_WIDTH);
                flashStage.setWidth(screenBounds.getWidth());
                flashStage.setHeight(FLASH_EDGE_WIDTH);
                break;
            case "left":
                flashStage.setX(0);
                flashStage.setY(0);
                flashStage.setWidth(FLASH_EDGE_WIDTH);
                flashStage.setHeight(screenBounds.getHeight());
                break;
            case "right":
                flashStage.setX(screenBounds.getWidth() - FLASH_EDGE_WIDTH);
                flashStage.setY(0);
                flashStage.setWidth(FLASH_EDGE_WIDTH);
                flashStage.setHeight(screenBounds.getHeight());
                break;
            default:
                // 默认闪烁顶部边缘
                flashStage.setX(0);
                flashStage.setY(0);
                flashStage.setWidth(screenBounds.getWidth());
                flashStage.setHeight(FLASH_EDGE_WIDTH);
        }
        
        // 开始闪烁
        new Thread(() -> {
            try {
                for (int i = 0; i < flashCount; i++) {
                    // 显示窗口
                    Platform.runLater(() -> flashStage.show());
                    Thread.sleep(FLASH_INTERVAL);
                    
                    // 隐藏窗口
                    Platform.runLater(() -> flashStage.hide());
                    Thread.sleep(FLASH_INTERVAL);
                }
                
                // 最后关闭窗口
                Platform.runLater(() -> {
                    if (flashStage.isShowing()) {
                        flashStage.close();
                    }
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}