package com.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * JavaFX应用程序主类
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // 加载主界面FXML文件 - 使用ClassLoader确保跨平台兼容性
            ClassLoader classLoader = getClass().getClassLoader();
            URL fxmlUrl = classLoader.getResource("fxml/ConfigurableMainWindow.fxml");
            if (fxmlUrl == null) {
                throw new IOException("FXML文件未找到: fxml/ConfigurableMainWindow.fxml");
            }
            Parent root = FXMLLoader.load(fxmlUrl);
            Scene scene = new Scene(root, 900, 700);
            
            // 添加CSS样式表
            URL cssUrl = classLoader.getResource("css/application.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            else {
                System.out.println("警告: CSS样式表未找到");
            }
            
            primaryStage.setTitle("久坐小管家");
            primaryStage.setScene(scene);
            primaryStage.show();
            
            // 设置应用程序图标
            // primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/images/icon.png")));
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 为了兼容不同的运行环境，保留原有的main方法
     */
    public static void main(String[] args) {
        launch(args);
    }
}