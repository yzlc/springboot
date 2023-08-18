package com.yzlc.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@Slf4j
public class WindowsTimeUtil {
    public static void modify(String newDateTime) {
        // 构建命令
        String command = "cmd /c date " + newDateTime.substring(0, 10) + " && time " + newDateTime.substring(11);

        try {
            // 创建ProcessBuilder
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));

            // 将重定向错误流到标准输出，以便于调试
            processBuilder.redirectErrorStream(true);

            // 启动进程
            Process process = processBuilder.start();

            // 等待进程执行完成
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                log.info("系统时间已成功修改为：" + newDateTime);
            } else {
                log.info("无法修改系统时间。");
            }
        } catch (IOException | InterruptedException e) {
            log.error("", e);
        }
    }

    public static boolean trigger() {
        int exitCode = -1;
        try {
            // 构建命令
            String command = "w32tm /resync";

            // 执行命令
            Process process = Runtime.getRuntime().exec(command);

            // 获取命令输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }

            // 等待命令执行完成
            exitCode = process.waitFor();
            log.info("命令执行完成，退出码：" + exitCode);
        } catch (IOException | InterruptedException e) {
            log.error("命令执行失败", e);
        }
        return exitCode == 0;
    }
}