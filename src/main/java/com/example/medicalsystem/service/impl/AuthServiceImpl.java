package com.example.medicalsystem.service.impl;

import com.example.medicalsystem.dto.LoginRequest;
import com.example.medicalsystem.dto.RegisterRequest;
import com.example.medicalsystem.dto.JwtAuthResponse;
import com.example.medicalsystem.model.User;
import com.example.medicalsystem.repository.UserRepository;
import com.example.medicalsystem.repository.DoctorRepository;
import com.example.medicalsystem.security.JwtTokenProvider;
import com.example.medicalsystem.service.AuthService;
import com.example.medicalsystem.model.Doctor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import com.example.medicalsystem.dto.UserDTO;
import com.example.medicalsystem.config.JwtConfig;
import org.springframework.transaction.annotation.Transactional;
import com.example.medicalsystem.model.Patient;
import com.example.medicalsystem.repository.PatientRepository;
import java.time.LocalDateTime;
import com.example.medicalsystem.security.UserPrincipal;
import java.util.regex.Pattern;

/**
 * 认证服务实现类
 * 
 * 负责用户的登录认证、注册和Token生成等功能。
 * 实现了AuthService接口，提供了对用户身份的验证和新用户的创建。
 * 使用JWT（JSON Web Token）方式进行身份验证和授权。
 * 支持医生和患者等不同角色的用户注册和身份验证。
 * 
 * @author Medical System Team
 * @version 1.0
 */
@Service
public class AuthServiceImpl implements AuthService {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    
    /**
     * 密码规则正则表达式
     * 至少8位，包含大小写字母、数字和特殊字符
     */
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$");

    /**
     * 认证管理器
     * 用于处理认证请求
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 用户仓库
     * 提供用户数据的访问
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * 医生仓库
     * 提供医生数据的访问
     */
    @Autowired
    private DoctorRepository doctorRepository;

    /**
     * 患者仓库
     * 提供患者数据的访问
     */
    @Autowired
    private PatientRepository patientRepository;

    /**
     * 密码编码器
     * 用于加密和验证密码
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * JWT令牌提供者
     * 用于生成和验证JWT令牌
     */
    @Autowired
    private JwtTokenProvider tokenProvider;

    /**
     * JWT配置
     * 包含JWT相关的参数设置
     */
    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 用户登录方法
     * 
     * 处理用户登录请求，验证用户凭据，生成JWT令牌。
     * 
     * @param loginRequest 包含用户名和密码的登录请求对象
     * @return 包含JWT令牌和用户信息的响应对象
     * @throws RuntimeException 如果认证失败或用户不存在
     */
    @Override
    public JwtAuthResponse login(LoginRequest loginRequest) {
        try {
            // 认证用户凭据
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
            // 获取用户信息
            User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
            // 构建用户DTO对象
            UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
            logger.debug("Generated token: {}", jwt);
            logger.debug("User details: {}", userDTO);
            
            // 构建并返回认证响应
            return JwtAuthResponse.builder()
                .token(jwt)
                .tokenType("Bearer")
                .user(userDTO)
                .build();
        } catch (AuthenticationException e) {
            logger.error("Authentication failed: ", e);
            throw new RuntimeException("Invalid username/password");
        }
    }

    /**
     * 用户注册方法
     * 
     * 处理用户注册请求，验证注册信息，创建用户账户和对应的角色档案。
     * 使用@Transactional注解确保操作的原子性。
     * 
     * @param request 包含用户注册信息的请求对象
     * @throws RuntimeException 如果注册信息无效或用户已存在
     */
    @Override
    @Transactional
    public void register(RegisterRequest request) {
        logger.info("Processing registration request for username: {}", request.getUsername());
        
        // 验证请求数据
        validateRegistrationRequest(request);
        
        // 创建用户账户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        
        // 使用相同的密码加密方法，确保与登录一致
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        // 设置角色，确保格式一致
        String role = normalizeRole(request.getRole());
        user.setRole(role);
        
        // 设置账户状态和创建时间
        user.setStatus("ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        
        logger.debug("Saving new user with role: {}", role);
        userRepository.save(user);
        logger.info("User saved successfully with ID: {}", user.getId());

        // 根据角色创建对应的用户档案
        if (role.equals("ROLE_DOCTOR")) {
            createDoctorProfile(user, request);
        } else if (role.equals("ROLE_PATIENT")) {
            createPatientProfile(user, request);
        } else {
            logger.warn("No profile created for role: {}", role);
        }
        
        logger.info("Registration completed successfully for user: {}", user.getUsername());
    }
    
    /**
     * 验证注册请求的有效性
     * 
     * 检查用户名和邮箱是否已存在，密码是否符合要求，角色是否有效，
     * 并根据不同角色验证必填字段。
     * 
     * @param request 注册请求对象
     * @throws RuntimeException 如果验证失败
     */
    private void validateRegistrationRequest(RegisterRequest request) {
        // 验证用户名唯一性
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Registration failed: Username {} already taken", request.getUsername());
            throw new RuntimeException("用户名已被使用");
        }

        // 验证邮箱唯一性
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email {} already in use", request.getEmail());
            throw new RuntimeException("邮箱已被使用");
        }
        
        // 验证密码与确认密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            logger.warn("Registration failed: Password and confirm password do not match");
            throw new RuntimeException("两次输入的密码不一致");
        }
        
        // 验证角色有效性
        String role = request.getRole();
        if (role == null || (!role.equals("DOCTOR") && !role.equals("PATIENT") && 
                             !role.equals("ROLE_DOCTOR") && !role.equals("ROLE_PATIENT"))) {
            logger.warn("Registration failed: Invalid role {}", role);
            throw new RuntimeException("无效的用户角色");
        }
        
        // 根据角色验证必填字段
        if (role.contains("DOCTOR")) {
            validateDoctorFields(request);
        } else if (role.contains("PATIENT")) {
            validatePatientFields(request);
        }
    }
    
    /**
     * 验证医生必填字段
     * 
     * 检查医生注册时必须提供的字段，如姓名、职称和科室等。
     * 
     * @param request 注册请求对象
     * @throws RuntimeException 如果必填字段缺失
     */
    private void validateDoctorFields(RegisterRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new RuntimeException("医生姓名不能为空");
        }
        if (request.getProfessionalTitle() == null || request.getProfessionalTitle().trim().isEmpty()) {
            throw new RuntimeException("专业职称不能为空");
        }
        if (request.getDepartment() == null || request.getDepartment().trim().isEmpty()) {
            throw new RuntimeException("所属科室不能为空");
        }
    }
    
    /**
     * 验证患者必填字段
     * 
     * 检查患者注册时必须提供的字段，如姓名和性别等。
     * 
     * @param request 注册请求对象
     * @throws RuntimeException 如果必填字段缺失
     */
    private void validatePatientFields(RegisterRequest request) {
        if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
            throw new RuntimeException("患者姓名不能为空");
        }
        if (request.getGender() == null || request.getGender().trim().isEmpty()) {
            throw new RuntimeException("性别不能为空");
        }
    }
    
    /**
     * 规范化角色名称
     * 
     * 确保角色名称以ROLE_前缀开始，符合Spring Security的要求。
     * 
     * @param role 原始角色名称
     * @return 规范化后的角色名称
     */
    private String normalizeRole(String role) {
        String normalizedRole = role.toUpperCase();
        if (!normalizedRole.startsWith("ROLE_")) {
            normalizedRole = "ROLE_" + normalizedRole;
        }
        return normalizedRole;
    }

    /**
     * 创建医生档案
     * 
     * 根据注册信息创建医生的详细档案。
     * 
     * @param user 用户对象
     * @param request 注册请求对象
     */
    private void createDoctorProfile(User user, RegisterRequest request) {
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setFullName(request.getFullName());
        doctor.setProfessionalTitle(request.getProfessionalTitle());
        doctor.setDepartment(request.getDepartment());
        doctor.setHospital(request.getHospital());
        doctor.setLicenseNumber(request.getLicenseNumber());
        doctor.setPhone(request.getPhone());
        
        logger.debug("Saving doctor profile for user ID: {}", user.getId());
        doctorRepository.save(doctor);
    }

    /**
     * 创建患者档案
     * 
     * 根据注册信息创建患者的详细档案。
     * 
     * @param user 用户对象
     * @param request 注册请求对象
     */
    private void createPatientProfile(User user, RegisterRequest request) {
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setFullName(request.getFullName());
        patient.setAge(request.getAge());
        patient.setGender(request.getGender());
        patient.setPhone(request.getPhone());
        patient.setEmergencyContact(request.getEmergencyContact());
        patient.setEmergencyPhone(request.getEmergencyPhone());
        
        logger.debug("Saving patient profile for user ID: {}", user.getId());
        patientRepository.save(patient);
    }
} 