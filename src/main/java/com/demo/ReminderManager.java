package com.demo;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class ReminderManager {
    private static ReminderManager instance;
    private Timer timer;
    private AtomicBoolean isRunning = new AtomicBoolean(false);
    private Configuration config;
    
    private ReminderManager() {
        config = Configuration.getInstance();
    }
    
    public static synchronized ReminderManager getInstance() {
        if (instance == null) {
            instance = new ReminderManager();
        }
        return instance;
    }
    
    // 启动提醒服务
    public void startReminder() {
        if (isRunning.get()) {
            stopReminder();
        }
        
        isRunning.set(true);
        timer = new Timer(true); // 守护线程
        
        // 设置定时器，按照用户配置的时间间隔触发提醒
        long delay = 0; // 立即开始
        long period = config.getReminderInterval() * 60 * 1000; // 转换为毫秒
        
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                checkAndRemind();
            }
        }, delay, period);
    }
    
    // 停止提醒服务
    public void stopReminder() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        isRunning.set(false);
    }
    
    // 检查是否需要提醒并触发提醒
    private void checkAndRemind() {
        if (isFullScreenMode()) {
            // 全屏模式下的提醒
            notifyInFullScreenMode();
        } else {
            // 非全屏模式下的提醒
            notifyInNormalMode();
        }
    }
    
    // 检查是否处于全屏模式
    private boolean isFullScreenMode() {
        try {
            // 使用JNA调用Windows API获取当前活动窗口
            WinDef.HWND foregroundWindow = User32.INSTANCE.GetForegroundWindow();
            if (foregroundWindow == null) {
                return false;
            }
            
            // 获取屏幕尺寸
            int screenWidth = User32.INSTANCE.GetSystemMetrics(User32.SM_CXSCREEN);
            int screenHeight = User32.INSTANCE.GetSystemMetrics(User32.SM_CYSCREEN);
            
            // 获取窗口尺寸
            WinDef.RECT rect = new WinDef.RECT();
            User32.INSTANCE.GetWindowRect(foregroundWindow, rect);
            int windowWidth = rect.right - rect.left;
            int windowHeight = rect.bottom - rect.top;
            
            // 如果窗口尺寸与屏幕尺寸接近，则认为是全屏模式
            // 添加一些容差，处理窗口边框等情况
            int tolerance = 10;
            return Math.abs(windowWidth - screenWidth) <= tolerance && 
                   Math.abs(windowHeight - screenHeight) <= tolerance;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // 全屏模式下的提醒
    private void notifyInFullScreenMode() {
        // 在JavaFX应用线程中执行UI操作
        javafx.application.Platform.runLater(() -> {
            Configuration config = Configuration.getInstance();
            
            // 显示短暂弹窗
            if (config.isPopupEnabled()) {
                ReminderPopup.showTemporaryPopup(config.getReminderText(), config.getPopupDuration());
            }
            
            // 屏幕边缘闪烁
            if (config.isFlashEnabled()) {
                ScreenFlasher.flashEdges(config.getFlashEdges(), config.getFlashCount());
            }
            
            // 播放轻微提示音
            if (config.isSoundEnabled()) {
                SoundPlayer.playSound(config.getSoundFile(), true); // true表示降低音量
            }
        });
    }
    
    // 非全屏模式下的提醒
    private void notifyInNormalMode() {
        // 在JavaFX应用线程中执行UI操作
        javafx.application.Platform.runLater(() -> {
            Configuration config = Configuration.getInstance();
            
            // 显示常规弹窗（可以是置顶的）
            ReminderPopup.showNormalPopup(config.getReminderText());
            
            // 播放正常音量的提示音
            if (config.isSoundEnabled()) {
                SoundPlayer.playSound(config.getSoundFile(), false); // false表示正常音量
            }
        });
    }
    
    // 检查是否正在运行
    public boolean isRunning() {
        return isRunning.get();
    }
    
    // 重新启动提醒服务（当配置变更时调用）
    public void restartReminder() {
        if (isRunning.get()) {
            stopReminder();
            startReminder();
        }
    }
}