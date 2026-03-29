package com.example.medicalsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 医疗系统应用程序入口类
 * 
 * 这是整个医疗系统的启动类，使用Spring Boot框架搭建。
 * 通过@SpringBootApplication注解集成了@Configuration, @EnableAutoConfiguration和@ComponentScan功能。
 * 启用了@EnableScheduling注解以支持定时任务的执行，用于系统指标收集、日志清理等定期工作。
 * 
 * @author Medical System Team
 * @version 1.0
 */
@SpringBootApplication
@EnableScheduling
public class MedicalSystemApplication {
    
    /**
     * 应用程序主入口方法
     * 
     * 启动Spring Boot应用，初始化整个医疗系统，包括:
     * - 组件扫描
     * - 自动配置
     * - Bean注册
     * - 内嵌服务器启动
     * - 调度器初始化
     * 
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(MedicalSystemApplication.class, args);
    }
} 