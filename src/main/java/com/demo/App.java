package com.demo;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * 简单的测试应用程序，用于验证各个组件的功能
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 初始化配置
        Configuration config = Configuration.getInstance();
        
        // 测试提醒管理器
        testReminderManager();
        
        // 测试弹窗功能
        testReminderPopup();
        
        // 测试屏幕闪烁功能
        testScreenFlasher();
        
        // 测试声音播放功能
        testSoundPlayer();
        
        // 显示应用启动信息
        System.out.println("久坐小管家应用启动成功！");
    }
    
    /**
     * 测试提醒管理器
     */
    private void testReminderManager() {
        System.out.println("测试提醒管理器...");
        
        // 获取提醒管理器实例
        ReminderManager manager = ReminderManager.getInstance();
        
        // 检查初始状态
        System.out.println("初始状态: " + (manager.isRunning() ? "运行中" : "已停止"));
        
        // 配置测试参数（每1分钟提醒一次，方便测试）
        Configuration config = Configuration.getInstance();
        config.setReminderInterval(1);
        config.setReminderText("测试提醒：该起身活动一下啦！");
        
        System.out.println("提醒管理器测试完成。");
    }
    
    /**
     * 测试提醒弹窗功能
     */
    private void testReminderPopup() {
        System.out.println("测试提醒弹窗功能...");
        
        // 测试短暂弹窗（3秒后自动关闭）
        System.out.println("显示短暂弹窗（3秒后自动关闭）");
        ReminderPopup.showTemporaryPopup("这是一个短暂弹窗测试", 3);
        
        // 短暂延迟后测试常规弹窗
        try {
            Thread.sleep(5000); // 等待5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("显示常规弹窗");
        ReminderPopup.showNormalPopup("这是一个常规弹窗测试，需要手动关闭");
        
        System.out.println("提醒弹窗功能测试完成。");
    }
    
    /**
     * 测试屏幕闪烁功能
     */
    private void testScreenFlasher() {
        System.out.println("测试屏幕闪烁功能...");
        
        // 短暂延迟后测试屏幕闪烁
        try {
            Thread.sleep(5000); // 等待5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 测试顶部边缘闪烁3次
        System.out.println("测试顶部边缘闪烁3次");
        java.util.List<String> edges = new java.util.ArrayList<>();
        edges.add("top");
        ScreenFlasher.flashEdges(edges, 3);
        
        // 短暂延迟后测试多个边缘闪烁
        try {
            Thread.sleep(3000); // 等待3秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 测试多个边缘闪烁4次
        System.out.println("测试多个边缘闪烁4次");
        edges.clear();
        edges.add("top");
        edges.add("bottom");
        ScreenFlasher.flashEdges(edges, 4);
        
        System.out.println("屏幕闪烁功能测试完成。");
    }
    
    /**
     * 测试声音播放功能
     */
    private void testSoundPlayer() {
        System.out.println("测试声音播放功能...");
        
        // 注意：由于没有实际的声音文件，这里只是调用方法，但可能无法播放实际的声音
        // 实际应用中，需要确保在resources/sounds/目录下有对应的声音文件
        
        // 测试播放默认声音
        System.out.println("尝试播放默认声音（可能无法播放，因为缺少声音文件）");
        SoundPlayer.playSound("default_sound.wav", false);
        
        // 短暂延迟
        try {
            Thread.sleep(2000); // 等待2秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 测试播放低音量声音
        System.out.println("尝试播放低音量声音（可能无法播放，因为缺少声音文件）");
        SoundPlayer.playSound("default_sound.wav", true);
        
        System.out.println("声音播放功能测试完成。");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}