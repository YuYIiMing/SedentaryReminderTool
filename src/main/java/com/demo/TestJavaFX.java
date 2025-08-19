package com.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * 简单的JavaFX测试类，用于验证JavaFX是否能正常运行
 */
public class TestJavaFX extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            StackPane root = new StackPane();
            Scene scene = new Scene(root, 300, 250);
            
            primaryStage.setTitle("JavaFX测试");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            System.out.println("JavaFX窗口已成功显示");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}