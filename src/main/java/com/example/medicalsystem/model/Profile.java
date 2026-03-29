package com.example.medicalsystem.model;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "profiles")
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true)
    private Long userId;

    private String name;
    private String email;
    private String phone;
    
    // 可以添加更多字段
} 