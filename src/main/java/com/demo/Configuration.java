package com.demo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Configuration {
    // 默认配置
    private static final int DEFAULT_REMINDER_INTERVAL = 30; // 默认30分钟
    private static final String DEFAULT_REMINDER_TEXT = "该起身活动一下啦，伸个懒腰吧！";
    public static final String DEFAULT_SOUND_FILE = "default_sound.wav";
    private static final boolean DEFAULT_POPUP_ENABLED = true;
    private static final boolean DEFAULT_FLASH_ENABLED = true;
    private static final boolean DEFAULT_SOUND_ENABLED = true;
    private static final int DEFAULT_POPUP_DURATION = 4; // 默认4秒
    private static final int DEFAULT_FLASH_COUNT = 4; // 默认4次
    private static final String DEFAULT_POPUP_COLOR = "蓝色";
    
    // 配置文件路径 - 使用用户主目录下的配置文件
    private static final String CONFIG_DIR = System.getProperty("user.home") + File.separator + ".sedentary_reminder";
    private static final String CONFIG_FILE = CONFIG_DIR + File.separator + "config.properties";
    
    // 配置项
    private int reminderInterval; // 提醒间隔（分钟）
    private String reminderText;   // 提醒文案
    private String soundFile;      // 提示音文件路径
    private boolean popupEnabled;  // 是否启用弹窗提示
    private boolean flashEnabled;  // 是否启用屏幕闪烁
    private boolean soundEnabled;  // 是否启用声音提示
    private int popupDuration;     // 弹窗持续时间（秒）
    private int flashCount;        // 屏幕闪烁次数
    private List<String> flashEdges; // 闪烁边缘
    private List<ReminderRecord> reminderRecords; // 提醒记录
    private String popupColor; // 弹窗颜色
    
    private static Configuration instance;
    
    private Configuration() {
        reminderRecords = new ArrayList<>();
        loadConfiguration();
        // 注册关闭钩子，在程序退出时自动保存配置和提醒记录
        addShutdownHook();
    }
    
    /**
     * 添加JVM关闭钩子，在程序退出时自动保存配置和提醒记录
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                saveConfiguration();
                saveReminderRecords();
                System.out.println("程序退出时自动保存配置和提醒记录成功");
            } catch (Exception e) {
                System.err.println("程序退出时保存配置和提醒记录失败：" + e.getMessage());
                e.printStackTrace();
            }
        }));
    }
    
    public static synchronized Configuration getInstance() {
        if (instance == null) {
            instance = new Configuration();
        }
        return instance;
    }
    
    // 加载配置
    void loadConfiguration() {
        Properties properties = new Properties();
        try {
            // 确保配置目录存在
            Path configDirPath = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDirPath)) {
                Files.createDirectories(configDirPath);
            }
            
            File configFile = new File(CONFIG_FILE);
            if (configFile.exists()) {
                try (InputStream input = new FileInputStream(configFile)) {
                    properties.load(input);
                    reminderInterval = Integer.parseInt(properties.getProperty("reminder.interval", String.valueOf(DEFAULT_REMINDER_INTERVAL)));
                    reminderText = properties.getProperty("reminder.text", DEFAULT_REMINDER_TEXT);
                    soundFile = properties.getProperty("sound.file", DEFAULT_SOUND_FILE);
                    popupEnabled = Boolean.parseBoolean(properties.getProperty("popup.enabled", String.valueOf(DEFAULT_POPUP_ENABLED)));
                    flashEnabled = Boolean.parseBoolean(properties.getProperty("flash.enabled", String.valueOf(DEFAULT_FLASH_ENABLED)));
                    soundEnabled = Boolean.parseBoolean(properties.getProperty("sound.enabled", String.valueOf(DEFAULT_SOUND_ENABLED)));
                    popupDuration = Integer.parseInt(properties.getProperty("popup.duration", String.valueOf(DEFAULT_POPUP_DURATION)));
                    flashCount = Integer.parseInt(properties.getProperty("flash.count", String.valueOf(DEFAULT_FLASH_COUNT)));
                    popupColor = properties.getProperty("popup.color", DEFAULT_POPUP_COLOR);
                    
                    // 加载闪烁边缘配置
                    String edges = properties.getProperty("flash.edges", "top");
                    flashEdges = new ArrayList<>();
                    for (String edge : edges.split(",")) {
                        flashEdges.add(edge.trim());
                    }
                }
            } else {
                // 如果配置文件不存在，使用默认配置
                reminderInterval = DEFAULT_REMINDER_INTERVAL;
                reminderText = DEFAULT_REMINDER_TEXT;
                soundFile = DEFAULT_SOUND_FILE;
                popupEnabled = DEFAULT_POPUP_ENABLED;
                flashEnabled = DEFAULT_FLASH_ENABLED;
                soundEnabled = DEFAULT_SOUND_ENABLED;
                popupDuration = DEFAULT_POPUP_DURATION;
                flashCount = DEFAULT_FLASH_COUNT;
                flashEdges = new ArrayList<>();
                flashEdges.add("top");
            }
        } catch (IOException e) {
            e.printStackTrace();
            // 使用默认配置
            reminderInterval = DEFAULT_REMINDER_INTERVAL;
            reminderText = DEFAULT_REMINDER_TEXT;
            soundFile = DEFAULT_SOUND_FILE;
            popupEnabled = DEFAULT_POPUP_ENABLED;
            flashEnabled = DEFAULT_FLASH_ENABLED;
            soundEnabled = DEFAULT_SOUND_ENABLED;
            popupDuration = DEFAULT_POPUP_DURATION;
            flashCount = DEFAULT_FLASH_COUNT;
            flashEdges = new ArrayList<>();
            flashEdges.add("top");
            popupColor = DEFAULT_POPUP_COLOR;
        }
        
        // 加载提醒记录
        loadReminderRecords();
    }
    
    // 保存配置
    public void saveConfiguration() {
        try {
            // 确保配置目录存在
            Path configDirPath = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDirPath)) {
                Files.createDirectories(configDirPath);
            }
            
            Properties properties = new Properties();
            properties.setProperty("reminder.interval", String.valueOf(reminderInterval));
            properties.setProperty("reminder.text", reminderText);
            properties.setProperty("sound.file", soundFile);
            properties.setProperty("popup.enabled", String.valueOf(popupEnabled));
            properties.setProperty("flash.enabled", String.valueOf(flashEnabled));
            properties.setProperty("sound.enabled", String.valueOf(soundEnabled));
            properties.setProperty("popup.duration", String.valueOf(popupDuration));
            properties.setProperty("flash.count", String.valueOf(flashCount));
            properties.setProperty("popup.color", popupColor);
            
            // 保存闪烁边缘配置
            StringBuilder edgesBuilder = new StringBuilder();
            for (int i = 0; i < flashEdges.size(); i++) {
                edgesBuilder.append(flashEdges.get(i));
                if (i < flashEdges.size() - 1) {
                    edgesBuilder.append(",");
                }
            }
            properties.setProperty("flash.edges", edgesBuilder.toString());
            
            try (OutputStream output = new FileOutputStream(CONFIG_FILE)) {
                properties.store(output, "久坐小管家配置");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // 保存提醒记录
    public void saveReminderRecords() {
        try {
            // 确保配置目录存在
            Path configDirPath = Paths.get(CONFIG_DIR);
            if (!Files.exists(configDirPath)) {
                Files.createDirectories(configDirPath);
                System.out.println("配置目录已创建：" + CONFIG_DIR);
            }
            
            // 将提醒记录保存到文件（即使为空）
            String recordsFilePath = CONFIG_DIR + File.separator + "reminder_records.dat";
            
            // 检查记录列表是否为空
            if (reminderRecords == null || reminderRecords.isEmpty()) {
                // 如果记录为空，删除现有文件或创建一个空文件来表示清空状态
                File recordsFile = new File(recordsFilePath);
                if (recordsFile.exists()) {
                    recordsFile.delete();
                    System.out.println("提醒记录文件已删除（因为记录为空）");
                } else {
                    // 创建一个新的空文件
                    recordsFile.createNewFile();
                    System.out.println("已创建空的提醒记录文件");
                }
                return;
            }
            
            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream(recordsFilePath))) {
                oos.writeObject(reminderRecords);
                System.out.println("提醒记录保存成功，共" + reminderRecords.size() + "条记录，文件路径：" + recordsFilePath);
            }
        } catch (IOException e) {
            System.err.println("保存提醒记录失败：" + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // 加载提醒记录
    private void loadReminderRecords() {
        try {
            File recordsFile = new File(CONFIG_DIR + "/reminder_records.dat");
            if (recordsFile.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(
                        new FileInputStream(recordsFile))) {
                    reminderRecords = (List<ReminderRecord>) ois.readObject();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            // 如果加载失败，使用空列表
            reminderRecords = new ArrayList<>();
            e.printStackTrace();
        }
    }
    
    // 添加提醒记录
    public void addReminderRecord(ReminderRecord record) {
        reminderRecords.add(record);
        // 只保留最近100条记录
        if (reminderRecords.size() > 100) {
            reminderRecords.remove(0);
        }
    }
    
    // 获取所有提醒记录
    public List<ReminderRecord> getReminderRecords() {
        return new ArrayList<>(reminderRecords);
    }
    
    // 清空所有提醒记录
    public void clearReminderRecords() {
        reminderRecords.clear();
    }
    
    // Getters and Setters
    public int getReminderInterval() {
        return reminderInterval;
    }
    
    public void setReminderInterval(int reminderInterval) {
        // 验证时间范围：10分钟到2小时
        if (reminderInterval >= 10 && reminderInterval <= 120) {
            this.reminderInterval = reminderInterval;
        }
    }
    
    public String getReminderText() {
        return reminderText;
    }
    
    public void setReminderText(String reminderText) {
        // 验证文案长度：200字以内
        if (reminderText != null && reminderText.length() <= 200) {
            this.reminderText = reminderText;
        }
    }
    
    public String getSoundFile() {
        return soundFile;
    }
    
    public void setSoundFile(String soundFile) {
        this.soundFile = soundFile;
    }
    
    public boolean isPopupEnabled() {
        return popupEnabled;
    }
    
    public void setPopupEnabled(boolean popupEnabled) {
        this.popupEnabled = popupEnabled;
    }
    
    public boolean isFlashEnabled() {
        return flashEnabled;
    }
    
    public void setFlashEnabled(boolean flashEnabled) {
        this.flashEnabled = flashEnabled;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }
    
    public int getPopupDuration() {
        return popupDuration;
    }
    
    public void setPopupDuration(int popupDuration) {
        // 验证弹窗持续时间：3-5秒
        if (popupDuration >= 3 && popupDuration <= 5) {
            this.popupDuration = popupDuration;
        }
    }
    
    public int getFlashCount() {
        return flashCount;
    }
    
    public void setFlashCount(int flashCount) {
        // 验证闪烁次数：3-5次
        if (flashCount >= 3 && flashCount <= 5) {
            this.flashCount = flashCount;
        }
    }
    
    public String getPopupColor() {
        return popupColor;
    }
    
    public void setPopupColor(String popupColor) {
        this.popupColor = popupColor;
    }
    
    public List<String> getFlashEdges() {
        return flashEdges;
    }
    
    public void setFlashEdges(List<String> flashEdges) {
        this.flashEdges = flashEdges;
    }
}