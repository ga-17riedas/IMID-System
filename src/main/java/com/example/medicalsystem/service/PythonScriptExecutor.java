package com.example.medicalsystem.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

@Service
public class PythonScriptExecutor {
    private static final Logger logger = LoggerFactory.getLogger(PythonScriptExecutor.class);

    @Value("${python.executable}")
    private String pythonExecutable;

    @Value("${python.script.path}")
    private String scriptPath;

    public String executeScript(String scriptName, String... args) {
        try {
            // 构建命令
            List<String> command = new ArrayList<>();
            command.add(pythonExecutable);
            command.add(scriptName);
            command.addAll(Arrays.asList(args));

            // 创建进程构建器
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            processBuilder.redirectErrorStream(true);

            logger.info("Executing command: {}", String.join(" ", command));

            // 启动进程
            Process process = processBuilder.start();

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            // 等待进程完成
            int exitCode = process.waitFor();
            String outputStr = output.toString().trim();
            
            logger.info("Script output: {}", outputStr);

            if (exitCode != 0) {
                logger.error("Python script failed with exit code: {}. Error: {}", exitCode, outputStr);
                throw new RuntimeException("Python script execution failed: " + outputStr);
            }

            return outputStr;
        } catch (IOException | InterruptedException e) {
            logger.error("Failed to execute Python script", e);
            throw new RuntimeException("Failed to execute Python script: " + e.getMessage());
        }
    }
} 