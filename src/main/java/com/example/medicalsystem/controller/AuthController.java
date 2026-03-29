package com.example.medicalsystem.controller;

import com.example.medicalsystem.dto.LoginRequest;
import com.example.medicalsystem.dto.RegisterRequest;
import com.example.medicalsystem.dto.ErrorResponse;
import com.example.medicalsystem.dto.UserResponse;
import com.example.medicalsystem.model.User;
import com.example.medicalsystem.model.Doctor;
import com.example.medicalsystem.repository.UserRepository;
import com.example.medicalsystem.repository.DoctorRepository;
import com.example.medicalsystem.security.JwtTokenProvider;
import com.example.medicalsystem.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证控制器
 * 
 * 提供用户认证相关的API接口，包括登录、注册和用户名/邮箱可用性检查等功能。
 * 处理来自前端的认证请求，调用认证服务实现具体的业务逻辑。
 * 使用JWT方式进行身份验证，登录成功后返回JWT令牌给客户端。
 * 
 * @author Medical System Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    /**
     * 认证管理器
     * 用于处理用户认证
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * JWT令牌提供者
     * 用于生成和验证JWT令牌
     */
    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * 用户仓库
     * 提供用户数据的访问
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 认证服务
     * 处理用户认证和注册的业务逻辑
     */
    @Autowired
    private AuthService authService;

    /**
     * 密码编码器
     * 用于密码的加密和验证
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 医生仓库
     * 提供医生数据的访问
     */
    @Autowired
    private DoctorRepository doctorRepository;

    /**
     * 测试密码加密方法
     * 
     * 用于测试密码加密功能，将原始密码进行加密并返回加密结果。
     * 仅用于开发和测试环境，生产环境应考虑移除。
     * 
     * @param rawPassword 原始密码
     * @return 加密后的密码
     */
    @GetMapping("/test-password")
    public String testPassword(@RequestParam String rawPassword) {
        String encodedPassword = passwordEncoder.encode(rawPassword);
        logger.debug("Raw password: {}", rawPassword);
        logger.debug("Encoded password: {}", encodedPassword);
        return String.format("Encoded password: %s", encodedPassword);
    }

    /**
     * 用户登录接口
     * 
     * 处理用户登录请求，验证用户凭据，生成JWT令牌并返回用户信息。
     * 如果是医生登录，会返回医生的全名信息。
     * 
     * @param loginRequest 包含用户名和密码的登录请求
     * @return 包含JWT令牌和用户信息的响应
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 验证用户凭据
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );

            // 设置安全上下文
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成JWT令牌
            String jwt = tokenProvider.generateToken(authentication);
            
            // 获取用户详细信息
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            
            // 构建响应数据
            Map<String, Object> response = new HashMap<>();
            if (user.getRole().equals("ROLE_DOCTOR")) {
                Doctor doctor = doctorRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Doctor not found"));
                response.put("fullName", doctor.getFullName());
            }
            
            response.put("token", jwt);
            response.put("user", new UserResponse(user));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Login failed for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.badRequest()
                .body(new ErrorResponse("登录失败：" + e.getMessage()));
        }
    }

    /**
     * 用户注册接口
     * 
     * 处理用户注册请求，验证注册信息的有效性，创建新用户账户。
     * 支持表单验证，当验证失败时返回详细错误信息。
     * 
     * @param request 包含注册信息的请求
     * @param bindingResult 表单绑定结果，包含验证错误信息
     * @return 注册结果响应
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request, BindingResult bindingResult) {
        try {
            // 处理验证错误
            if (bindingResult.hasErrors()) {
                List<String> errors = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.toList());
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("errors", errors);
                return ResponseEntity.badRequest().body(response);
            }
            
            // 调用注册服务
            authService.register(request);
            
            // 返回成功响应
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "注册成功");
            response.put("username", request.getUsername());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Registration failed", e);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    /**
     * 检查用户名是否可用
     * 
     * 用于前端表单验证，检查指定的用户名是否已被注册。
     * 
     * @param username 要检查的用户名
     * @return 包含可用性结果的响应
     */
    @GetMapping("/check-username")
    public ResponseEntity<?> checkUsername(@RequestParam String username) {
        boolean exists = userRepository.existsByUsername(username);
        Map<String, Object> response = new HashMap<>();
        response.put("available", !exists);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 检查邮箱是否可用
     * 
     * 用于前端表单验证，检查指定的邮箱是否已被注册。
     * 
     * @param email 要检查的邮箱
     * @return 包含可用性结果的响应
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        boolean exists = userRepository.existsByEmail(email);
        Map<String, Object> response = new HashMap<>();
        response.put("available", !exists);
        
        return ResponseEntity.ok(response);
    }
} 