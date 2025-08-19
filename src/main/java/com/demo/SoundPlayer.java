package com.demo;

import javazoom.jl.player.Player;

import javax.sound.sampled.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

public class SoundPlayer {
    // 播放声音文件
    public static void playSound(String soundFilePath, boolean isLowVolume) {
        new Thread(() -> {
            try {
                // 检查文件路径是否为空
                if (soundFilePath == null || soundFilePath.trim().isEmpty()) {
                    // 播放默认提示音
                    playDefaultSound(isLowVolume);
                    return;
                }
                
                // 尝试将文件路径解析为URL（支持资源文件）或File（支持用户自定义文件）
                URL resourceUrl = SoundPlayer.class.getClassLoader().getResource("sounds/" + soundFilePath);
                
                if (resourceUrl != null) {
                    // 播放内置资源文件
                    playSoundFromURL(resourceUrl, isLowVolume);
                } else {
                    // 尝试播放用户自定义文件
                    File soundFile = new File(soundFilePath);
                    if (soundFile.exists() && soundFile.isFile()) {
                        String extension = getFileExtension(soundFile);
                        if ("mp3".equalsIgnoreCase(extension)) {
                            playMP3File(soundFile);
                        } else if ("wav".equalsIgnoreCase(extension)) {
                            playWAVFile(soundFile, isLowVolume);
                        } else {
                            // 格式不支持，播放默认声音
                            playDefaultSound(isLowVolume);
                        }
                    } else {
                        // 文件不存在，播放默认声音
                        playDefaultSound(isLowVolume);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 播放失败时，尝试播放默认声音
                try {
                    playDefaultSound(isLowVolume);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
    
    // 播放默认提示音
    private static void playDefaultSound(boolean isLowVolume) {
        try {
            URL defaultSoundUrl = SoundPlayer.class.getClassLoader().getResource("sounds/default_sound.wav");
            if (defaultSoundUrl != null) {
                playSoundFromURL(defaultSoundUrl, isLowVolume);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 从URL播放声音文件（主要用于播放资源文件）
    private static void playSoundFromURL(URL soundUrl, boolean isLowVolume) throws Exception {
        String extension = getFileExtension(soundUrl.getFile());
        if ("mp3".equalsIgnoreCase(extension)) {
            try (InputStream inputStream = soundUrl.openStream()) {
                playMP3Stream(inputStream);
            }
        } else if ("wav".equalsIgnoreCase(extension)) {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundUrl);
            playWAVStream(audioInputStream, isLowVolume);
        }
    }
    
    // 播放MP3文件
    private static void playMP3File(File mp3File) throws Exception {
        try (FileInputStream fileInputStream = new FileInputStream(mp3File)) {
            playMP3Stream(fileInputStream);
        }
    }
    
    // 从输入流播放MP3文件
    private static void playMP3Stream(InputStream inputStream) throws Exception {
        Player player = new Player(inputStream);
        player.play();
    }
    
    // 播放WAV文件
    private static void playWAVFile(File wavFile, boolean isLowVolume) throws Exception {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(wavFile);
        playWAVStream(audioInputStream, isLowVolume);
    }
    
    // 从输入流播放WAV文件
    private static void playWAVStream(AudioInputStream audioInputStream, boolean isLowVolume) throws Exception {
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        
        // 调整音量
        if (isLowVolume) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            // 将音量降低（负值表示降低音量）
            float volumeReduction = -10.0f; // 降低约10dB
            gainControl.setValue(volumeReduction);
        }
        
        // 开始播放
        clip.start();
        
        // 等待播放完成
        Thread.sleep(clip.getMicrosecondLength() / 1000);
        
        // 释放资源
        clip.close();
        audioInputStream.close();
    }
    
    // 获取文件扩展名
    private static String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filePath.length() - 1) {
            return filePath.substring(lastDotIndex + 1);
        }
        return "";
    }
    
    // 获取文件扩展名
    private static String getFileExtension(File file) {
        String fileName = file.getName();
        return getFileExtension(fileName);
    }
}