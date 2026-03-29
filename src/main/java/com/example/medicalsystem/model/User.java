package com.example.medicalsystem.model;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 * 
 * 用于存储系统中的用户基本信息，包括账号、密码和角色等。
 * 是Patient（患者）、Doctor（医生）和管理员等角色的基础实体。
 * 存储在users表中，支持认证和授权处理。
 * 
 * @author Medical System Team
 * @version 1.0
 */
@Data
@Entity
@Table(name = "users")
public class User {
    /**
     * 用户ID，主键，自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户名，唯一标识，不可为空
     * 用于系统登录和身份识别
     */
    @Column(nullable = false, unique = true)
    private String username;

    /**
     * 用户密码，不可为空
     * 存储加密后的密码值
     */
    @Column(nullable = false)
    private String password;

    /**
     * 用户邮箱
     * 用于系统通知和密码重置等功能
     */
    @Column
    private String email;

    /**
     * 用户角色
     * 存储用户的系统角色，如"PATIENT"（患者）、"DOCTOR"（医生）或"ADMIN"（管理员）
     * 用于权限控制和业务处理
     */
    @Column(name = "role")
    private String role;  // Should store values like "PATIENT", "DOCTOR", "ADMIN"

    /**
     * 创建时间
     * 记录用户账号的创建时间
     */
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     * 记录用户信息的最后更新时间
     */
    @Column(name = "updated_at")
    private java.sql.Timestamp updatedAt;

    /**
     * 用户状态
     * 表示用户当前的状态，如"ACTIVE"（活跃）、"INACTIVE"（非活跃）或"LOCKED"（锁定）等
     */
    @Column(name = "status")
    private String status;

    /**
     * 最后登录时间
     * 记录用户最近一次登录系统的时间
     */
    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    /**
     * 创建前的处理方法
     * 在用户实体被持久化之前自动设置创建时间
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 