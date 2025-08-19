package com.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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

            // 添加窗口关闭事件处理，确保点击关闭按钮时完全终止Java进程
            primaryStage.setOnCloseRequest(event -> {
                System.out.println("用户关闭窗口，正在停止所有服务...");
                
                // 停止提醒管理器
                try {
                    ReminderManager.getInstance().stopReminder();
                } catch (Exception e) {
                    System.err.println("停止提醒管理器时出错: " + e.getMessage());
                }
                
                // 强制终止所有Java线程，确保JVM完全退出
                System.out.println("所有服务已停止，强制终止JVM");
                // 设置为立即退出，不等待任何清理工作
                Runtime.getRuntime().halt(0);
            });
            
            // 确保在程序退出时无论如何都能完全终止
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("JVM正在关闭，执行最终清理...");
                // 额外的保障措施
                try {
                    ReminderManager.getInstance().stopReminder();
                } catch (Exception e) {
                    System.err.println("关闭钩子中停止提醒管理器时出错: " + e.getMessage());
                }
            }));
            
            // 设置应用程序图标 - 优先使用PNG格式，因为它在JavaFX中通常更稳定
            try {
                System.out.println("开始设置应用程序图标...");
                
                // 首先尝试加载用户放置的PNG图标
                URL pngIconUrl = getClass().getClassLoader().getResource("images/icon.png");
                if (pngIconUrl != null) {
                    System.out.println("找到PNG图标文件: " + pngIconUrl.getPath());
                    try {
                        // 方法1：使用默认构造函数不指定尺寸，让JavaFX自动识别
                        System.out.println("尝试方法1: 使用默认构造函数加载PNG图标");
                        Image pngIcon = new Image(pngIconUrl.toExternalForm());
                        primaryStage.getIcons().add(pngIcon);
                        
                        // 添加一个延迟检查，给图像加载一些时间
                        System.out.println("立即检查 - PNG图标宽度: " + pngIcon.getWidth() + ", 高度: " + pngIcon.getHeight());
                        
                        // 添加一个监听器来检查图像是否成功加载
                        pngIcon.progressProperty().addListener((observable, oldValue, newValue) -> {
                            System.out.println("PNG图标加载进度: " + (newValue.doubleValue() * 100) + "%");
                            if (newValue.doubleValue() == 1.0) {
                                System.out.println("PNG图标加载完成，实际尺寸: " + pngIcon.getWidth() + "x" + pngIcon.getHeight());
                            }
                        });
                        
                        pngIcon.errorProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue) {
                                System.err.println("PNG图标加载错误: 图像可能格式不正确或损坏");
                            }
                        });
                        
                        // 方法2：同时添加一个不同尺寸的版本作为备选
                        System.out.println("尝试方法2: 添加另一个尺寸的PNG图标");
                        primaryStage.getIcons().add(new Image(pngIconUrl.toExternalForm(), 48, 48, true, true));
                        
                        System.out.println("已添加PNG图标到应用程序");
                    } catch (Exception e) {
                        System.err.println("加载PNG图标时出错: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("警告: PNG图标文件未找到: images/icon.png");
                    
                    // 如果PNG图标未找到，再尝试加载SVG图标
                    // 先尝试加载简单的SVG图标
                    URL simpleIconUrl = getClass().getClassLoader().getResource("images/simple-icon.svg");
                    if (simpleIconUrl != null) {
                        System.out.println("找到简单SVG图标文件: " + simpleIconUrl.getPath());
                        try {
                            Image simpleIcon = new Image(simpleIconUrl.toExternalForm(), 48, 48, true, true, true);
                            primaryStage.getIcons().add(simpleIcon);
                            System.out.println("成功添加简单SVG图标，宽度: " + simpleIcon.getWidth() + ", 高度: " + simpleIcon.getHeight());
                        } catch (Exception e) {
                            System.err.println("加载简单SVG图标时出错: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("警告: 简单SVG图标文件未找到: images/simple-icon.svg");
                    }
                    
                    // 尝试加载原始的icon.svg
                    URL iconUrl = getClass().getClassLoader().getResource("images/icon.svg");
                    if (iconUrl != null) {
                        System.out.println("找到原始SVG图标文件: " + iconUrl.getPath());
                        try {
                            primaryStage.getIcons().add(new Image(iconUrl.toExternalForm(), 16, 16, true, true, true));
                            primaryStage.getIcons().add(new Image(iconUrl.toExternalForm(), 32, 32, true, true, true));
                            primaryStage.getIcons().add(new Image(iconUrl.toExternalForm(), 48, 48, true, true, true));
                            primaryStage.getIcons().add(new Image(iconUrl.toExternalForm(), 64, 64, true, true, true));
                            System.out.println("成功添加原始SVG图标，图标总数: " + primaryStage.getIcons().size());
                        } catch (Exception e) {
                            System.err.println("加载原始SVG图标时出错: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("警告: 原始SVG图标文件未找到: images/icon.svg");
                    }
                }
                
                // 添加更多诊断信息
                System.out.println("JavaFX版本: " + System.getProperty("javafx.version", "未知"));
                System.out.println("Java版本: " + System.getProperty("java.version"));
                System.out.println("操作系统: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
                System.out.println("应用图标总数: " + primaryStage.getIcons().size());
                
            } catch (Exception e) {
                System.err.println("设置应用图标失败: " + e.getMessage());
                e.printStackTrace();
            }
            
            primaryStage.show();
            
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