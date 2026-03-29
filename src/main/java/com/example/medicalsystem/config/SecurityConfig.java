package com.example.medicalsystem.config;

import com.example.medicalsystem.security.CustomUserDetailsService;
import com.example.medicalsystem.security.JwtAuthenticationFilter;
import com.example.medicalsystem.security.CustomAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * 安全配置类
 * 
 * 负责配置系统的安全策略，包括认证、授权、会话管理、CORS等。
 * 使用Spring Security框架实现安全控制，支持基于JWT的无状态认证。
 * 配置了不同URL路径的访问权限，以及密码编码器、认证管理器等安全组件。
 * 
 * @author Medical System Team
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
    securedEnabled = true,
    jsr250Enabled = true,
    prePostEnabled = true
)
public class SecurityConfig {

    /**
     * 自定义用户详情服务
     * 用于从数据库加载用户信息
     */
    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /**
     * 自定义认证入口点
     * 处理未认证请求的响应
     */
    @Autowired
    private CustomAuthenticationEntryPoint unauthorizedHandler;

    /**
     * JWT认证过滤器
     * 处理JWT令牌的验证
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤链
     * 
     * 定义系统的安全规则，包括CORS、CSRF防护、会话管理、请求授权等。
     * 配置不同URL路径的访问权限，如公开API、需要特定角色的API等。
     * 将JWT过滤器添加到认证流程中。
     * 
     * @param http HttpSecurity对象，用于构建安全配置
     * @return 配置好的SecurityFilterChain对象
     * @throws Exception 如果配置过程中发生错误
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors()
                .and()
            .csrf()
                .disable()
            .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                // 允许静态资源访问
                .antMatchers(
                    "/",
                    "/favicon.ico",
                    "/**/*.png",
                    "/**/*.gif",
                    "/**/*.svg",
                    "/**/*.jpg",
                    "/**/*.html",
                    "/**/*.css",
                    "/**/*.js"
                ).permitAll()
                // 允许认证相关的API
                .antMatchers("/api/auth/**").permitAll()
                // 允许公开的API
                .antMatchers("/api/public/**").permitAll()
                // 医生相关的API需要DOCTOR角色
                .antMatchers("/api/doctor/**").hasRole("DOCTOR")
                // 患者相关的API需要PATIENT角色
                .antMatchers("/api/patient/**").hasRole("PATIENT")
                // 管理员相关的API需要ADMIN角色
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                // 医学影像相关的API需要认证
                .antMatchers("/api/medical/images/**").authenticated()
                // 其他所有请求需要认证
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * 认证管理器
     * 
     * 提供认证功能，用于验证用户凭据。
     * 从认证配置中获取认证管理器实例。
     * 
     * @param authConfig 认证配置对象
     * @return 认证管理器实例
     * @throws Exception 如果获取过程中发生错误
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 密码编码器
     * 
     * 提供密码加密和验证功能。
     * 使用BCrypt算法进行密码加密，提供较高的安全性。
     * 
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS配置源
     * 
     * 配置跨域资源共享策略，允许前端应用与后端API交互。
     * 设置允许的源、方法、头信息等CORS配置。
     * 
     * @return 配置好的CorsConfigurationSource对象
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    /**
     * HTTP防火墙配置
     * 
     * 配置URL请求的约束规则，允许特定字符出现在URL中。
     * 放宽了默认的严格限制，允许URL中包含双斜杠、分号等特殊字符。
     * 用于处理一些特殊的URL格式需求。
     * 
     * @return 配置好的HttpFirewall对象
     */
    @Bean
    public HttpFirewall allowUrlEncodedSlashHttpFirewall() {
        StrictHttpFirewall firewall = new StrictHttpFirewall();
        // 允许URL中包含双斜杠
        firewall.setAllowUrlEncodedDoubleSlash(true);
        // 允许分号
        firewall.setAllowSemicolon(true);
        // 允许URL编码的斜杠
        firewall.setAllowUrlEncodedSlash(true);
        
        return firewall;
    }
} 