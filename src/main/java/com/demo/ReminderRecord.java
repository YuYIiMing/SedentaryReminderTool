package com.demo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 提醒记录类，用于存储每次计时的开始时间、结束时间和时长
 */
public class ReminderRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private long durationMinutes;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    public ReminderRecord(LocalDateTime startTime, LocalDateTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.durationMinutes = java.time.Duration.between(startTime, endTime).toMinutes();
    }
    
    public String getStartTimeString() {
        return startTime.format(FORMATTER);
    }
    
    public String getEndTimeString() {
        return endTime.format(FORMATTER);
    }
    
    public long getDurationMinutes() {
        return durationMinutes;
    }
    
    public String getDurationString() {
        return durationMinutes + " 分钟";
    }
    
    // 用于表格显示的属性
    public String getStartTimeDisplay() {
        return getStartTimeString();
    }
    
    public String getEndTimeDisplay() {
        return getEndTimeString();
    }
    
    public String getDurationDisplay() {
        return getDurationString();
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
}