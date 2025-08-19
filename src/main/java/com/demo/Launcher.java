package com.demo;

import javafx.application.Application;

/**
 * 启动器类，用于解决JavaFX运行时组件缺失的问题
 * 从JDK 11开始，JavaFX不再包含在JDK中，需要单独添加
 */
public class Launcher {
    public static void main(String[] args) {
        Application.launch(Main.class, args);
    }
}