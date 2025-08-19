package com.demo;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * 用于生成默认提示音文件的工具类
 */
public class GenerateDefaultSound {

    public static void main(String[] args) {
        try {
            // 确保sounds目录存在
            File soundsDir = new File("src/main/resources/sounds");
            if (!soundsDir.exists()) {
                soundsDir.mkdirs();
            }
            
            // 生成一个简单的提示音WAV文件
            generateDefaultSound("src/main/resources/sounds/default_sound.wav");
            System.out.println("默认提示音文件已成功生成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成一个简单的WAV格式提示音
     * @param filePath 输出文件路径
     */
    public static void generateDefaultSound(String filePath) throws IOException, LineUnavailableException {
        // 音频格式设置
        AudioFormat format = new AudioFormat(8000, 8, 1, true, false);
        
        // 创建一个简单的正弦波声音
        byte[] buffer = new byte[10000];
        double frequency = 800; // 音调频率
        for (int i = 0; i < buffer.length; i++) {
            // 生成正弦波
            double angle = i / (format.getSampleRate() / frequency) * 2.0 * Math.PI;
            buffer[i] = (byte) (Math.sin(angle) * 80); // 音量适中
        }

        // 创建音频输入流
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, format, buffer.length);
        
        // 将生成的声音数据写入文件
        File outputFile = new File(filePath);
        AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, outputFile);
        
        // 关闭资源
        audioInputStream.close();
        byteArrayInputStream.close();
    }
}